package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.input.events.FocusEvent;
import dev.zeddevstuff.crafted.input.events.KeyEvent;
import dev.zeddevstuff.crafted.input.events.MouseEvent;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.ITexture;
import dev.zeddevstuff.crafted.platform.ITextureManager;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Float2;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.utils.TextEditor;

import java.util.Map;

import static dev.zeddevstuff.crafted.input.ImportantKeys.*;

@DefineElement("TextArea")
@Description("A multiline text input field that allows users to enter and edit text.")
public class TextArea extends Input
{
    private boolean canStartDrag;
    private boolean isDragging;

    public TextArea(CraftedContext ctx, Map<String, String> attributes)
    {
        super(ctx, attributes, true);
    }

    @Override
    public void onKeyPress(KeyEvent event)
    {
        if(event.modifiers.control() && (event.keyCode == A || event.keyCode == C || event.keyCode == X || event.keyCode == V))
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
            event.consume();
            return;
        }

        if(event.keyCode == TAB && allowTabNavigation.get())
        {
            text.insert("    ");
            event.consume();
            return;
        }
        if(event.keyCode == ENTER)
            text.insert('\n');
        else if(event.keyCode == BACKSPACE)
            text.delete(true);
        else if(event.keyCode == DELETE)
            text.delete(false);
        else if(event.keyCode == UP || event.keyCode == DOWN)
        {
            if(!allowArrowNavigation.get())
                return;
            text.moveCursorVertical(event.keyCode == UP ? -1 : 1, event.modifiers.shift());
        }
        else if(event.keyCode == LEFT)
            text.moveCursor(-1, event.modifiers.control(), event.modifiers.shift());
        else if(event.keyCode == RIGHT)
            text.moveCursor(1, event.modifiers.control(), event.modifiers.shift());
        else if(event.keyCode == HOME)
            text.moveCursorToLineStart(event.modifiers.shift());
        else if(event.keyCode == END)
            text.moveCursorToLineEnd(event.modifiers.shift());
        else
            return;
        event.consume();
    }

    private float lineHeight()
    {
        return font.get().measureText("Ag", fontScale.get(), -1).height();
    }

    @Override
    public void onMousePress(MouseEvent event)
    {
        if(event.button != MouseButton.LEFT || font.get() == null)
            return;
        canStartDrag = true;
        text.moveCursorToPoint(font.get(), new Float2(event.x - getLayout().x - 4, event.y - getLayout().y - 4), fontScale.get(), lineHeight());
        text.setSelectionStart(text.getCursorPosition());
        event.consume();
    }

    @Override
    public void onMouseRelease(MouseEvent event)
    {
        if(event.button == MouseButton.LEFT)
        {
            canStartDrag = false;
            isDragging = false;
            event.consume();
        }
    }

    @Override
    public void onMouseMove(MouseEvent event)
    {
        if(canStartDrag)
        {
            canStartDrag = false;
            isDragging = true;
        }
        if(isDragging && font.get() != null)
        {
            text.extendSelectionToPoint(font.get(), new Float2(event.x - getLayout().x - 4, event.y - getLayout().y - 4), fontScale.get(), lineHeight());
            event.consume();
        }
    }

    public static void register(Crafted crafted)
    {
        crafted.elements.registerElement(TextArea.class, "TextArea", TextArea::new);
        crafted.renderers.registerRenderer(TextArea.class, "default", new DefaultRenderer(crafted.textures));
    }

    public static class DefaultRenderer implements IRendersType<TextArea>
    {
        private final ITexture textfieldTexture;
        private final ITexture textfieldHighlightedTexture;

        public DefaultRenderer(ITextureManager textures)
        {
            textfieldTexture = textures.get("minecraft", "widget/text_field");
            textfieldHighlightedTexture = textures.get("minecraft", "widget/text_field_highlighted");
        }

        @Override
        public Class<?> getElementType() { return TextArea.class; }

        @Override
        public void render(ICanvas canvas, TextArea element)
        {
            if(element.font.get() == null)
                return;
            var layout = element.getLayout();
            var texture = element.state.isHoveredOrFocused() || element.state.isActive()
                ? textfieldHighlightedTexture : textfieldTexture;
            canvas.drawTexture(texture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));

            int lineHeight = Math.max(1, (int)element.lineHeight());
            int textLeft = layout.x + 4;
            int textTop = layout.y + 4;
            String value = element.text.getText();
            String[] lines = value.split("\\n", -1);
            var settings = element.settings.get().withNoWrap();
            canvas.beginClip(layout.x - element.getAncestorRenderOffsetX() + 2,
                layout.y - element.getAncestorRenderOffsetY() + 2, layout.width - 4, layout.height - 4);
            if(value.isEmpty() && !element.state.isFocused())
                canvas.drawText(element.placeholder.get(), textLeft, textTop, element.placeholderSettings, element.placeholderColor.get());
            else
            {
                int selectionStart = Math.min(element.text.getSelectionStart(), element.text.getSelectionEnd());
                int selectionEnd = Math.max(element.text.getSelectionStart(), element.text.getSelectionEnd());
                int offset = 0;
                for(int lineIndex = 0; lineIndex < lines.length; lineIndex++)
                {
                    String line = lines[lineIndex];
                    int lineY = textTop + lineIndex * lineHeight;
                    int lineStart = offset;
                    int lineEnd = lineStart + line.length();
                    int selectedStart = Math.max(selectionStart, lineStart);
                    int selectedEnd = Math.min(selectionEnd, lineEnd);
                    if(selectedStart < selectedEnd)
                    {
                        int selectionX = textLeft + (int)element.font.get().measureText(line.substring(0, selectedStart - lineStart), settings.size(), -1).width();
                        int selectionWidth = (int)element.font.get().measureText(line.substring(selectedStart - lineStart, selectedEnd - lineStart), settings.size(), -1).width();
                        canvas.drawRect(selectionX, lineY, selectionWidth, lineHeight, element.selectionColor.get());
                    }
                    canvas.drawText(line, textLeft, lineY, settings, element.textColor.get());
                    offset = lineEnd + 1;
                }

                if(element.text.updateBlink(canvas.deltaTime()) && element.state.isFocused())
                {
                    String beforeCursor = value.substring(0, element.text.getCursorPosition());
                    int cursorLine = beforeCursor.lastIndexOf('\n') + 1;
                    int cursorX = textLeft + (int)element.font.get().measureText(beforeCursor.substring(cursorLine), settings.size(), -1).width();
                    int cursorY = textTop + (int)(beforeCursor.substring(0, cursorLine).chars().filter(character -> character == '\n').count()) * lineHeight;
                    canvas.drawRect(cursorX, cursorY, 1, lineHeight, element.textColor.get());
                }
            }
            canvas.endClip();
        }
    }
}