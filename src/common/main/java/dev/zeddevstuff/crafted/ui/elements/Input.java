package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.ProxyProp;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.annotations.Ignore;
import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.input.events.*;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.IFont;
import dev.zeddevstuff.crafted.platform.ITexture;
import dev.zeddevstuff.crafted.platform.ITextureManager;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Float2;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.types.TextSettings;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.utils.TextEditor;

import java.util.Map;

import static dev.zeddevstuff.crafted.input.ImportantKeys.*;

@DefineElement("Input")
@Description("A text input field that allows users to enter and edit text.")
public class Input extends CraftedElement
{
    @Ignore
    private final TextEditor text = new TextEditor("", false);

    public final Prop<ISubmitEventHandler> onSubmit = registerProp(ISubmitEventHandler.class, "onSubmit", null, this::parseSubmitEventHandler);

    private ISubmitEventHandler parseSubmitEventHandler(String s, ISubmitEventHandler iSubmitEventHandler)
    {
        return ctx.getCallback(ISubmitEventHandler.class, s);
    }

    public final ProxyProp<String> value = registerProp(String.class, "value", text::getText, text::setText, Parsers::parseString);
    public final Prop<Boolean> allowTabNavigation = registerProp(Boolean.class, "allowTabNavigation", true, Parsers::parseBoolean);
    public final Prop<Boolean> allowArrowNavigation = registerProp(Boolean.class, "allowArrowNavigation", true, Parsers::parseBoolean);
    public final Prop<String> placeholder = registerProp(String.class, "placeholder", "", Parsers::parseString);

    public final Prop<Color> textColor = registerProp("textColor", Color.White, this::parseColor);
    public final Prop<Color> placeholderColor = registerProp("placeholderColor", Color.Gray, this::parseColor);
    public final Prop<Color> selectionColor = registerProp("selectionColor", Color.CornflowerBlue, this::parseColor);

    public final Prop<TextSettings> settings = new Prop<>(TextSettings.class, "settings", TextSettings.DEFAULT);
    private TextSettings placeholderSettings;
    public final ProxyProp<IFont> font;
    public final ProxyProp<Float> fontScale = registerProp(Float.class, "fontScale", settings.get()::size, (value) -> settings.set(settings.get().withSize(value)), Parsers::parseFloat);
    public final ProxyProp<Float> wrapWidth = registerProp(Float.class, "wrapWidth", settings.get()::wrapWidth, (value) -> settings.set(settings.get().withWrapWidth(value)), Parsers::parseFloat);
    public final ProxyProp<Boolean> shadow = registerProp(Boolean.class, "shadow", settings.get()::shadow, (value) -> settings.set(settings.get().withShadow(value)), Parsers::parseBoolean);
    public final ProxyProp<Color> shadowColor = registerProp(Color.class, "shadowColor", settings.get()::shadowColor, (value) -> settings.set(settings.get().withShadowColor(value)), this::parseColor);

    private float xOffset = 0;


    public Input(CraftedContext ctx, Map<String, String> attributes)
    {
        super(ctx, attributes);
        settings.addListener((prop, oldValue, newValue) -> {
            placeholderSettings = newValue.withShadow(false);
        });
        settings.set(ctx.crafted.defaultTextSettings.withShadow(true));
        font = registerProp(IFont.class, "font", settings.get()::font, (value) -> settings.set(settings.get().withFont(value)), ctx.crafted::getFontOrDefault);
    }

    @Override
    public void onCharTyped(KeyEvent event)
    {
        text.insert(event.character);
        event.consume();
    }

    @Override
    public void onKeyPress(KeyEvent event)
    {
        // TODO: Find a way to not be locked to QWERTY layouts
        if(event.modifiers.control())
        {
            if(event.keyCode == A)
                text.selectAll();
            else if(event.keyCode == C)
            {
                if(text.hasSelection())
                    ctx.crafted.clipboard.set(text.getSelectedText());
            }
            else if(event.keyCode == X)
            {
                if(text.hasSelection())
                {
                    ctx.crafted.clipboard.set(text.getSelectedText());
                    text.delete(false);
                }
            }
            else if(event.keyCode == V)
                text.insert(ctx.crafted.clipboard.get());
            else
                return;
            event.consume();
            return;
        }

        if(event.keyCode == UP || event.keyCode == DOWN)
        {
            if(!allowArrowNavigation.get())
                return;
            event.consume();
            return;
        }

        boolean shouldConsume = event.keyCode != TAB || !allowTabNavigation.get();
		if(event.keyCode == TAB && shouldConsume)
            text.insert("    ");
        if(event.keyCode == ENTER && !event.modifiers.shift())
            ctx.inputHandler.setFocus(null, event);
        else if(event.keyCode == BACKSPACE)
            text.delete(true);
        else if(event.keyCode == DELETE)
            text.delete(false);
        else if(event.keyCode == LEFT)
            text.moveCursor(-1, event.modifiers.control(), event.modifiers.shift());
        else if(event.keyCode == RIGHT)
            text.moveCursor(1, event.modifiers.control(), event.modifiers.shift());
        else if(event.keyCode == HOME)
            text.moveCursorToStart(event.modifiers.shift());
        else if(event.keyCode == END)
            text.moveCursorToEnd(event.modifiers.shift());
        if(shouldConsume)
            event.consume();
    }

    private boolean canStartDrag = false;
    private boolean isDragging = false;
    @Override
    public void onMousePress(MouseEvent event)
    {
        if(event.button == MouseButton.LEFT)
        {
            canStartDrag = true;
            text.moveCursorToPoint(font.get(), new Float2(event.x - getLayout().x - 4 + xOffset, event.y - getLayout().y), fontScale.get());
            text.setSelectionStart(text.getCursorPosition());
            event.consume();
        }
    }

    @Override
    public void onMouseRelease(MouseEvent event)
    {
        if(event.button == MouseButton.LEFT)
        {
            if(isDragging)
                isDragging = false;
            canStartDrag = false;
            event.consume();
        }
    }

    @Override
    public void onMouseMove(MouseEvent event)
    {
        if(canStartDrag)
        {
            isDragging = true;
            canStartDrag = false;
        }
        if(isDragging)
        {
            text.extendSelectionToPoint(font.get(), new Float2(event.x - getLayout().x - 4 + xOffset, event.y - getLayout().y), fontScale.get());
            event.consume();
        }
    }

    @Override
    public void onFocusLost(FocusEvent event)
    {
        onSubmit(event);
    }

    protected void onSubmit(UIEvent event)
    {
        text.clearSelection();
        if(!text.isDirty())
            return;
        var handler = onSubmit.get();
        if(handler != null)
            handler.onEvent(new SubmitEvent(this, event));
        if(!(event instanceof FocusEvent))
            ctx.inputHandler.setFocus(null, event);
        text.clearDirty();
    }

    @Override
    public boolean receivesInputEvents() { return true; }
    @Override
    public boolean isFocusable() { return true; }

    public static void register(Crafted crafted)
    {
        crafted.elements.registerElement(Input.class, "Input", Input::new);
        crafted.renderers.registerRenderer(Input.class, "default", new DefaultRenderer(crafted.textures));
    }

    public static class DefaultRenderer implements IRendersType<Input>
    {
        private static final Color borderColor = new Color(160, 160, 160);
        @Override
        public Class<?> getElementType() { return Input.class; }
        private final ITexture textfieldTexture, textfieldHighlightedTexture;
        public DefaultRenderer(ITextureManager textures)
        {
            textfieldTexture = textures.get("minecraft", "widget/text_field");
            textfieldHighlightedTexture = textures.get("minecraft", "widget/text_field_highlighted");
        }

        @Override
        public void render(ICanvas canvas, Input element)
        {
            if(element.font.get() == null)
                return;
            var layout = element.getLayout();
            if(element.state.isHoveredOrFocused() || element.state.isActive())
                canvas.drawTexture(textfieldHighlightedTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
            else
                canvas.drawTexture(textfieldTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
            canvas.beginClip(layout.x + 2, layout.y + 2, layout.width - 4, layout.height - 4);
            var textSettings = element.settings.get().withNoWrap();
            String text = element.text.getText();
            int textLeft = layout.x + 4;
            int textRight = layout.x + layout.width - 4;
            float textWidth = element.font.get().measureText(text, textSettings.size(), -1).width();
            float caretWidth = element.font.get().measureText(
                element.text.getTextBeforeCursor(), textSettings.size(), -1).width();
            float caretX = textLeft + caretWidth;
            int visibleWidth = Math.max(0, textRight - textLeft);
            if(caretX - element.xOffset > textRight)
                element.xOffset = caretX - textRight;
            else if(caretX - element.xOffset < textLeft)
                element.xOffset = caretX - textLeft;
            element.xOffset = Math.clamp(element.xOffset, 0, Math.max(0, textWidth - visibleWidth));

            float textHeight = element.font.get().measureText(text, textSettings.size(), -1).height();
            int textY = layout.y + (int)((layout.height - textHeight + 1) / 2);
            if(text.isEmpty() && !element.state.isFocused())
                canvas.drawText(element.placeholder.get(), layout.x + 4, textY, element.placeholderSettings, element.placeholderColor.get());
            else
            {
                canvas.beginTransform();
                canvas.translate(-element.xOffset, 0);

                int selectionStart = Math.min(element.text.getSelectionStart(), element.text.getSelectionEnd());
                int selectionEnd = Math.max(element.text.getSelectionStart(), element.text.getSelectionEnd());
                if(selectionStart != selectionEnd)
                {
                    float selectionX = textLeft + element.font.get()
                        .measureText(text.substring(0, selectionStart), textSettings.size(), -1).width();
                    float selectionEndX = textLeft + element.font.get()
                        .measureText(text.substring(0, selectionEnd), textSettings.size(), -1).width();
                    canvas.drawRect((int)selectionX, textY, (int)(selectionEndX - selectionX), (int)textHeight, element.selectionColor.get());
                }

                canvas.drawText(text, textLeft, textY, textSettings, element.textColor.get());
                if(element.text.updateBlink(canvas.deltaTime()) && element.state.isFocused())
                {
                    boolean insideText = element.text.getCursorPosition() < text.length();
                    int cursorX = (int)caretX;
                    int cursorY = insideText ? textY : textY + (int)textHeight - 1;
                    int cursorHeight = insideText ? (int)textHeight : 1;
                    int cursorWidth = insideText ? 1 : 5;
                    canvas.drawRect(cursorX, cursorY, cursorWidth, cursorHeight, element.textColor.get());
                }
                canvas.endTransform();
            }
            canvas.endClip();
        }
    }
}
