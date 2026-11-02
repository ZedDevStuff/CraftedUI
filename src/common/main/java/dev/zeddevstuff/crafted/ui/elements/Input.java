package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.ProxyProp;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.annotations.Ignore;
import dev.zeddevstuff.crafted.input.events.*;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.IFont;
import dev.zeddevstuff.crafted.platform.ITexture;
import dev.zeddevstuff.crafted.platform.ITextureManager;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.types.Color;
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

    public final Prop<Color> textColor = registerProp("textColor", Color.White, Parsers::parseColor);
    public final Prop<Color> placeholderColor = registerProp("placeholderColor", Color.Gray, Parsers::parseColor);

    public final Prop<TextSettings> settings = new Prop<>(TextSettings.class, "settings", TextSettings.DEFAULT);
    private TextSettings placeholderSettings;
    public final ProxyProp<IFont> font;
    public final ProxyProp<Float> fontScale = registerProp(Float.class, "fontScale", settings.get()::size, (value) -> settings.set(settings.get().withSize(value)), Parsers::parseFloat);
    public final ProxyProp<Float> wrapWidth = registerProp(Float.class, "wrapWidth", settings.get()::wrapWidth, (value) -> settings.set(settings.get().withWrapWidth(value)), Parsers::parseFloat);
    public final ProxyProp<Boolean> shadow = registerProp(Boolean.class, "shadow", settings.get()::shadow, (value) -> settings.set(settings.get().withShadow(value)), Parsers::parseBoolean);
    public final ProxyProp<Color> shadowColor = registerProp(Color.class, "shadowColor", settings.get()::shadowColor, (value) -> settings.set(settings.get().withShadowColor(value)), Parsers::parseColor);

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
        event.consume();
        text.insert(event.character);
    }

    @Override
    public void onKeyPress(KeyEvent event)
    {
        boolean shouldConsume = event.keyCode != TAB || !allowTabNavigation.get();
		if(event.keyCode == TAB && shouldConsume)
            text.insert("    ");
        else if(event.keyCode == ENTER && !event.modifiers.shift())
            ctx.inputHandler.setFocus(null);
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

    @Override
    public void onFocusLost(FocusEvent event)
    {
        onSubmit(event);
    }

    protected void onSubmit(UIEvent event)
    {
        if(!text.isDirty())
            return;
        var handler = onSubmit.get();
        if(handler != null)
            handler.onEvent(new SubmitEvent(this, event));
        if(!(event instanceof FocusEvent))
            ctx.inputHandler.setFocus(null);
        text.clearDirty();
    }

    @Override
    public boolean receivesInputEvents() { return true; }
    @Override
    public boolean isFocusable() { return true; }

    public static void register(Crafted crafted)
    {
        crafted.elements.registerElement(Input.class, "Input", Input::new);
        crafted.renderers.registerRenderer(Input.class, "default", new DefaultInputRenderer(crafted.textures));
    }

    public static class DefaultInputRenderer implements IRenders<Input>
    {
        private static final Color borderColor = new Color(160, 160, 160);
        private final ITexture textfieldTexture, textfieldHighlightedTexture;
        public DefaultInputRenderer(ITextureManager textures)
        {
            textfieldTexture = textures.get("minecraft", "widget/text_field");
            textfieldHighlightedTexture = textures.get("minecraft", "widget/text_field_highlighted");
        }

        @Override
        public void render(ICanvas canvas, Input element)
        {
            if(element.font.get() == null)
                return;
            // TODO: Make this support text scaling properly.
            var layout = element.getLayout();
            if(element.state.isHovered() || element.state.isActive() || element.state.isFocused())
                canvas.drawTexture(textfieldHighlightedTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
            else
                canvas.drawTexture(textfieldTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
            canvas.beginClip(layout.x + 2, layout.y + 2, layout.width - 4, layout.height - 4);
            float textHeight = element.font.get().measureText(element.text.getText(), element.settings.get().size(), element.settings.get().wrapWidth()).height();
            int textY = layout.y + (int)((layout.height - textHeight + 1) / 2);
            if(element.text.getText().isEmpty() && !element.state.isFocused())
                canvas.drawText(element.placeholder.get(), layout.x + 4, textY, element.placeholderSettings, element.placeholderColor.get());
            else
            {
                canvas.beginTransform();
                if(element.xOffset != 0)
                    canvas.translate(-element.xOffset, 0);
                canvas.drawText(element.text.getText(), layout.x + 4, textY, element.settings.get(), element.textColor.get());
            }
            if(element.text.updateBlink(canvas.deltaTime()) && element.state.isFocused())
            {
                String textBeforeCursor = element.text.getTextBeforeCursor();
                boolean insideText = element.text.getCursorPosition() < element.text.getText().length();
                int cursorX = textBeforeCursor.isEmpty() ? layout.x + 4 : layout.x + 4 + (insideText ? 1 : 2) + (int)element.font.get()
                    .measureText(textBeforeCursor, element.settings.get().size(), element.settings.get().wrapWidth()).width();
                int cursorY = insideText ? textY : textY + (int)textHeight - 1;
                int cursorHeight = insideText ? (int)textHeight : 1;
                int cursorWidth = insideText ? 1 : 5;
                canvas.drawRect(cursorX, cursorY, cursorWidth, cursorHeight, element.textColor.get());
            }
            if(element.xOffset != 0)
                canvas.endTransform();
            canvas.endClip();
        }
    }
}
