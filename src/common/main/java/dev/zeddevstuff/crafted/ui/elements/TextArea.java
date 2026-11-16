package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.zeddevstuff.crafted.input.ImportantKeys.*;

@DefineElement("TextArea")
@Description("A multiline text input field that allows users to enter and edit text.")
public class TextArea extends Input
{
    public final Prop<Boolean> textWrap = registerProp(Boolean.class, "textWrap", false, dev.zeddevstuff.crafted.parsing.Parsers::parseBoolean);
    public final Prop<Integer> scrollX = registerProp("scrollX", 0, this::parseInteger);
    public final Prop<Integer> scrollY = registerProp("scrollY", 0, this::parseInteger);
    public final Prop<Boolean> horizontalScrollbar = registerProp(Boolean.class, "horizontalScrollbar", true, dev.zeddevstuff.crafted.parsing.Parsers::parseBoolean);
    public final Prop<Boolean> verticalScrollbar = registerProp(Boolean.class, "verticalScrollbar", true, dev.zeddevstuff.crafted.parsing.Parsers::parseBoolean);
    public final Prop<Boolean> autoHideScrollbars = registerProp(Boolean.class, "autoHideScrollbars", true, dev.zeddevstuff.crafted.parsing.Parsers::parseBoolean);
    public final Prop<Integer> scrollbarSize = registerProp("scrollbarSize", 4, this::parseInteger);
    private boolean canStartDrag;
    private boolean isDragging;
    private boolean draggingHorizontal;
    private boolean draggingVertical;
    private boolean horizontalScrollbarVisible;
    private boolean verticalScrollbarVisible;
    private float dragStartX;
    private float dragStartY;
    private int dragStartScrollX;
    private int dragStartScrollY;
    private int lastCursorPosition;

    private record VisualLine(int start, int end) {}

    public TextArea(CraftedContext ctx, Map<String, String> attributes)
    {
        super(ctx, attributes, true);
    }

    @Override
    public void onKeyPress(KeyEvent event)
    {
        if(event.modifiers.control() && (event.keyCode == A || event.keyCode == C || event.keyCode == X || event.keyCode == V || event.keyCode == Y || event.keyCode == Z))
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
            else if(event.keyCode == Z)
                text.undo();
            else if(event.keyCode == Y)
                text.redo();
            event.consume();
            return;
        }

        if(event.keyCode == TAB)
        {
            if(allowTabNavigation.get())
                return;
            if(event.modifiers.shift())
                text.removeIndentation(tabSize.get());
            else
                text.insert(" ".repeat(tabSize.get()));
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
            if(font.get() == null)
                return;
            moveCursorVertical(event.keyCode == DOWN, event.modifiers.shift());
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

    private int parseInteger(String value, Integer defaultValue)
    {
        try
        {
            return Math.max(0, Integer.parseInt(value));
        }
        catch(NumberFormatException e)
        {
            return defaultValue;
        }
    }

    private int textViewportWidth()
    {
        return Math.max(1, getLayout().width - 8 - (verticalScrollbarVisible ? scrollbarSize.get() : 0));
    }

    private int textViewportHeight()
    {
        return Math.max(1, getLayout().height - 8 - (horizontalScrollbarVisible ? scrollbarSize.get() : 0));
    }

    private List<VisualLine> visualLines(TextArea element)
    {
        String value = element.text.getText();
        var lines = new ArrayList<VisualLine>();
        int wrapWidth = element.textViewportWidth();
        int lineStart = 0;
        while(true)
        {
            int newline = value.indexOf('\n', lineStart);
            int lineEnd = newline < 0 ? value.length() : newline;
            if(lineStart == lineEnd)
                lines.add(new VisualLine(lineStart, lineEnd));
            else if(!textWrap.get())
                lines.add(new VisualLine(lineStart, lineEnd));
            else
            {
                int segmentStart = lineStart;
                while(segmentStart < lineEnd)
                {
                    int segmentEnd = segmentStart + 1;
                    while(segmentEnd <= lineEnd && element.font.get().measureText(
                        value.substring(segmentStart, segmentEnd), element.fontScale.get(), -1).width() <= wrapWidth)
                        segmentEnd++;
                    int end = Math.min(lineEnd, segmentEnd - 1);
                    if(end < lineEnd)
                    {
                        int space = value.lastIndexOf(' ', end - 1);
                        if(space >= segmentStart)
                            end = space;
                    }
                    if(end <= segmentStart)
                        end = Math.min(lineEnd, segmentStart + 1);
                    lines.add(new VisualLine(segmentStart, end));
                    segmentStart = end;
                    while(segmentStart < lineEnd && value.charAt(segmentStart) == ' ')
                        segmentStart++;
                }
            }
            if(newline < 0)
                break;
            lineStart = newline + 1;
        }
        return lines;
    }

    private int contentWidth(List<VisualLine> lines)
    {
        String value = text.getText();
        int width = 0;
        for(var line : lines)
            width = Math.max(width, (int)font.get().measureText(value.substring(line.start(), line.end()), fontScale.get(), -1).width());
        return width + 8;
    }

    private int contentHeight(List<VisualLine> lines)
    {
        return (int)(lines.size() * lineHeight()) + 8;
    }

    private int maxScrollX(List<VisualLine> lines)
    {
        return Math.max(0, contentWidth(lines) - textViewportWidth());
    }

    private int maxScrollY(List<VisualLine> lines)
    {
        return Math.max(0, contentHeight(lines) - textViewportHeight());
    }

    private void updateScrollbarState(List<VisualLine> lines)
    {
        boolean verticalVisible = autoHideScrollbars.get() ? false : verticalScrollbar.get();
        boolean horizontalVisible = autoHideScrollbars.get() ? false : horizontalScrollbar.get();
        for(int pass = 0; pass < 3; pass++)
        {
            this.verticalScrollbarVisible = verticalVisible;
            this.horizontalScrollbarVisible = horizontalVisible;
            lines = visualLines(this);
            boolean nextHorizontalVisible = horizontalScrollbar.get()
                && (!autoHideScrollbars.get() || contentWidth(lines) > getLayout().width - 8
                    - (verticalVisible ? scrollbarSize.get() : 0));
            boolean nextVerticalVisible = verticalScrollbar.get()
                && (!autoHideScrollbars.get() || contentHeight(lines) > getLayout().height - 8
                    - (nextHorizontalVisible ? scrollbarSize.get() : 0));
            if(verticalVisible == nextVerticalVisible && horizontalVisible == nextHorizontalVisible)
                break;
            verticalVisible = nextVerticalVisible;
            horizontalVisible = nextHorizontalVisible;
        }
        verticalScrollbarVisible = verticalVisible;
        horizontalScrollbarVisible = horizontalVisible;
        lines = visualLines(this);
        scrollX.set(Math.clamp(scrollX.get(), 0, maxScrollX(lines)));
        scrollY.set(Math.clamp(scrollY.get(), 0, maxScrollY(lines)));
    }

    private void ensureCursorVisible(List<VisualLine> lines)
    {
        String value = text.getText();
        int cursorLine = 0;
        VisualLine cursorVisualLine = lines.get(0);
        for(int index = 0; index < lines.size(); index++)
        {
            var candidate = lines.get(index);
            if(text.getCursorPosition() <= candidate.end())
            {
                cursorLine = index;
                cursorVisualLine = candidate;
                break;
            }
        }
        int cursorOffset = Math.clamp(text.getCursorPosition() - cursorVisualLine.start(), 0,
            cursorVisualLine.end() - cursorVisualLine.start());
        int cursorX = (int)font.get().measureText(
            value.substring(cursorVisualLine.start(), cursorVisualLine.start() + cursorOffset), fontScale.get(), -1).width();
        int cursorY = cursorLine * (int)lineHeight();
        if(cursorX < scrollX.get())
            scrollX.set(cursorX);
        else if(cursorX > scrollX.get() + textViewportWidth() - 2)
            scrollX.set(cursorX - textViewportWidth() + 2);
        if(cursorY < scrollY.get())
            scrollY.set(cursorY);
        else if(cursorY + lineHeight() > scrollY.get() + textViewportHeight())
            scrollY.set(cursorY + (int)lineHeight() - textViewportHeight());
        scrollX.set(Math.clamp(scrollX.get(), 0, maxScrollX(lines)));
        scrollY.set(Math.clamp(scrollY.get(), 0, maxScrollY(lines)));
    }

    private int positionAt(TextArea element, List<VisualLine> lines, float x, float y)
    {
        String value = element.text.getText();
        int line = Math.clamp((int)(y / Math.max(1, lineHeight())), 0, lines.size() - 1);
        var visualLine = lines.get(line);
        String lineText = value.substring(visualLine.start(), visualLine.end());
        if(x <= 0 || lineText.isEmpty())
            return visualLine.start();
        float previousWidth = 0;
        for(int index = 1; index <= lineText.length(); index++)
        {
            float width = element.font.get().measureText(lineText.substring(0, index), element.fontScale.get(), -1).width();
            if(x < (previousWidth + width) / 2)
                return visualLine.start() + index - 1;
            previousWidth = width;
        }
        return visualLine.end();
    }

    private void moveCursorVertical(boolean down, boolean select)
    {
        var lines = visualLines(this);
        int cursor = text.getCursorPosition();
        int currentLine = 0;
        for(int index = 0; index < lines.size(); index++)
        {
            if(cursor <= lines.get(index).end())
            {
                currentLine = index;
                break;
            }
        }
        int targetLine = Math.clamp(currentLine + (down ? 1 : -1), 0, lines.size() - 1);
        var current = lines.get(currentLine);
        var target = lines.get(targetLine);
        int column = Math.clamp(cursor - current.start(), 0, current.end() - current.start());
        int position = target.start() + Math.min(column, target.end() - target.start());
        if(select)
        {
            if(text.getSelectionStart() == text.getSelectionEnd())
                text.setSelectionStart(cursor);
            text.setSelectionEnd(position);
        }
        else
        {
            text.setSelectionStart(position);
            text.setSelectionEnd(position);
        }
        text.setCursorPosition(position);
    }

    private int horizontalTrackWidth()
    {
        return getLayout().width - (verticalScrollbarVisible ? scrollbarSize.get() : 0);
    }

    private int verticalTrackHeight()
    {
        return getLayout().height - (horizontalScrollbarVisible ? scrollbarSize.get() : 0);
    }

    private int horizontalThumbWidth(List<VisualLine> lines)
    {
        return Math.max(scrollbarSize.get(), Math.round(horizontalTrackWidth() * (float)textViewportWidth()
            / Math.max(textViewportWidth(), contentWidth(lines))));
    }

    private int verticalThumbHeight(List<VisualLine> lines)
    {
        return Math.max(scrollbarSize.get(), Math.round(verticalTrackHeight() * (float)textViewportHeight()
            / Math.max(textViewportHeight(), contentHeight(lines))));
    }

    private int horizontalThumbX(List<VisualLine> lines)
    {
        int available = horizontalTrackWidth() - horizontalThumbWidth(lines);
        int maxScroll = maxScrollX(lines);
        return getLayout().x + Math.round(available * (maxScroll == 0 ? 0 : (float)scrollX.get() / maxScroll));
    }

    private int verticalThumbY(List<VisualLine> lines)
    {
        int available = verticalTrackHeight() - verticalThumbHeight(lines);
        int maxScroll = maxScrollY(lines);
        return getLayout().y + Math.round(available * (maxScroll == 0 ? 0 : (float)scrollY.get() / maxScroll));
    }

    private boolean containsHorizontalThumb(float x, float y, List<VisualLine> lines)
    {
        var layout = getLayout();
        int screenX = layout.x - getAncestorRenderOffsetX();
        int screenY = layout.y - getAncestorRenderOffsetY();
        int thumbX = horizontalThumbX(lines) - getAncestorRenderOffsetX();
        return horizontalScrollbarVisible && x >= thumbX && x <= thumbX + horizontalThumbWidth(lines)
            && y >= screenY + layout.height - scrollbarSize.get() && y <= screenY + layout.height;
    }

    private boolean containsVerticalThumb(float x, float y, List<VisualLine> lines)
    {
        var layout = getLayout();
        int screenX = layout.x - getAncestorRenderOffsetX();
        int thumbY = verticalThumbY(lines) - getAncestorRenderOffsetY();
        return verticalScrollbarVisible && x >= screenX + layout.width - scrollbarSize.get()
            && x <= screenX + layout.width && y >= thumbY && y <= thumbY + verticalThumbHeight(lines);
    }

    private boolean containsHorizontalTrack(float x, float y)
    {
        var layout = getLayout();
        int screenX = layout.x - getAncestorRenderOffsetX();
        int screenY = layout.y - getAncestorRenderOffsetY();
        return horizontalScrollbarVisible
            && x >= screenX && x <= screenX + horizontalTrackWidth()
            && y >= screenY + layout.height - scrollbarSize.get() && y <= screenY + layout.height;
    }

    private boolean containsVerticalTrack(float x, float y)
    {
        var layout = getLayout();
        int screenX = layout.x - getAncestorRenderOffsetX();
        int screenY = layout.y - getAncestorRenderOffsetY();
        return verticalScrollbarVisible
            && x >= screenX + layout.width - scrollbarSize.get() && x <= screenX + layout.width
            && y >= screenY && y <= screenY + verticalTrackHeight();
    }

    @Override
    public void onMousePress(MouseEvent event)
    {
        if(event.button != MouseButton.LEFT || font.get() == null)
            return;
        var lines = visualLines(this);
        if(containsHorizontalThumb(event.x, event.y, lines))
        {
            draggingHorizontal = true;
            dragStartX = event.x;
            dragStartScrollX = scrollX.get();
            event.consume();
            event.stopPropagation();
            return;
        }
        if(containsHorizontalTrack(event.x, event.y))
        {
            int available = horizontalTrackWidth() - horizontalThumbWidth(lines);
            int position = Math.round((event.x - (getLayout().x - getAncestorRenderOffsetX())
                - horizontalThumbWidth(lines) / 2f) * maxScrollX(lines) / Math.max(1, available));
            scrollX.set(Math.clamp(position, 0, maxScrollX(lines)));
            event.consume();
            event.stopPropagation();
            return;
        }
        if(containsVerticalThumb(event.x, event.y, lines))
        {
            draggingVertical = true;
            dragStartY = event.y;
            dragStartScrollY = scrollY.get();
            event.consume();
            event.stopPropagation();
            return;
        }
        if(containsVerticalTrack(event.x, event.y))
        {
            int available = verticalTrackHeight() - verticalThumbHeight(lines);
            int position = Math.round((event.y - (getLayout().y - getAncestorRenderOffsetY())
                - verticalThumbHeight(lines) / 2f) * maxScrollY(lines) / Math.max(1, available));
            scrollY.set(Math.clamp(position, 0, maxScrollY(lines)));
            event.consume();
            event.stopPropagation();
            return;
        }
        canStartDrag = true;
        int position = positionAt(this, lines,
            event.x - getLayout().x + getAncestorRenderOffsetX() - 4 + scrollX.get(),
            event.y - getLayout().y + getAncestorRenderOffsetY() - 4 + scrollY.get());
        text.setCursorPosition(position);
        text.clearSelection();
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
            draggingHorizontal = false;
            draggingVertical = false;
            event.consume();
        }
    }

    @Override
    public void onMouseScroll(MouseEvent event)
    {
        var lines = visualLines(this);
        int oldX = scrollX.get();
        int oldY = scrollY.get();
        if(ctx.inputHandler.getModifiers().shift())
            scrollX.set(scrollX.get() - Math.round(event.scrollY * 10));
        else
        {
            scrollX.set(scrollX.get() - Math.round(event.scrollX * 10));
            scrollY.set(scrollY.get() - Math.round(event.scrollY * 10));
        }
        updateScrollbarState(lines);
        if(oldX != scrollX.get() || oldY != scrollY.get())
        {
            event.consume();
            event.stopPropagation();
        }
    }

    @Override
    public void onMouseMove(MouseEvent event)
    {
        var lines = visualLines(this);
        if(draggingHorizontal)
        {
            int available = horizontalTrackWidth() - horizontalThumbWidth(lines);
            if(available > 0)
                scrollX.set(dragStartScrollX + Math.round((event.x - dragStartX) * maxScrollX(lines) / available));
            updateScrollbarState(lines);
            event.consume();
            event.stopPropagation();
            return;
        }
        if(draggingVertical)
        {
            int available = verticalTrackHeight() - verticalThumbHeight(lines);
            if(available > 0)
                scrollY.set(dragStartScrollY + Math.round((event.y - dragStartY) * maxScrollY(lines) / available));
            updateScrollbarState(lines);
            event.consume();
            event.stopPropagation();
            return;
        }
        if(canStartDrag)
        {
            canStartDrag = false;
            isDragging = true;
        }
        if(isDragging && font.get() != null)
        {
            int position = positionAt(this, lines,
                event.x - getLayout().x + getAncestorRenderOffsetX() - 4 + scrollX.get(),
                event.y - getLayout().y + getAncestorRenderOffsetY() - 4 + scrollY.get());
            text.setCursorPosition(position);
            text.setSelectionEnd(position);
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
            String value = element.text.getText();
            List<VisualLine> lines = element.visualLines(element);
            element.updateScrollbarState(lines);
            lines = element.visualLines(element);
            if(element.text.getCursorPosition() != element.lastCursorPosition)
            {
                element.ensureCursorVisible(lines);
                element.lastCursorPosition = element.text.getCursorPosition();
            }
            int textLeft = layout.x + 4 - element.scrollX.get();
            int textTop = layout.y + 4 - element.scrollY.get();
            var settings = element.settings.get().withNoWrap();
            canvas.beginClip(layout.x - element.getAncestorRenderOffsetX() + 2,
                layout.y - element.getAncestorRenderOffsetY() + 2,
                layout.width - 4 - (element.verticalScrollbarVisible ? element.scrollbarSize.get() : 0),
                layout.height - 4 - (element.horizontalScrollbarVisible ? element.scrollbarSize.get() : 0));
            if(value.isEmpty() && !element.state.isFocused())
                canvas.drawText(element.placeholder.get(), textLeft, textTop, element.placeholderSettings, element.placeholderColor.get());
            else
            {
                int selectionStart = Math.min(element.text.getSelectionStart(), element.text.getSelectionEnd());
                int selectionEnd = Math.max(element.text.getSelectionStart(), element.text.getSelectionEnd());
                for(int lineIndex = 0; lineIndex < lines.size(); lineIndex++)
                {
                    VisualLine visualLine = lines.get(lineIndex);
                    String line = value.substring(visualLine.start(), visualLine.end());
                    int lineY = textTop + lineIndex * lineHeight;
                    int lineStart = visualLine.start();
                    int lineEnd = visualLine.end();
                    int selectedStart = Math.max(selectionStart, lineStart);
                    int selectedEnd = Math.min(selectionEnd, lineEnd);
                    if(selectedStart < selectedEnd)
                    {
                        int selectionX = textLeft + (int)element.font.get().measureText(line.substring(0, selectedStart - lineStart), settings.size(), -1).width();
                        int selectionWidth = (int)element.font.get().measureText(line.substring(selectedStart - lineStart, selectedEnd - lineStart), settings.size(), -1).width();
                        canvas.drawRect(selectionX, lineY, selectionWidth, lineHeight, element.selectionColor.get());
                    }
                    canvas.drawText(line, textLeft, lineY, settings, element.textColor.get());
                }

                if(element.text.updateBlink(canvas.deltaTime()) && element.state.isFocused())
                {
                    int cursorLine = 0;
                    VisualLine cursorVisualLine = lines.get(0);
                    for(int index = 0; index < lines.size(); index++)
                    {
                        var candidate = lines.get(index);
                        if(element.text.getCursorPosition() <= candidate.end())
                        {
                            cursorLine = index;
                            cursorVisualLine = candidate;
                            break;
                        }
                    }
                    int cursorOffset = Math.clamp(element.text.getCursorPosition() - cursorVisualLine.start(), 0, cursorVisualLine.end() - cursorVisualLine.start());
                    int cursorX = textLeft + (int)element.font.get().measureText(
                        value.substring(cursorVisualLine.start(), cursorVisualLine.start() + cursorOffset), settings.size(), -1).width();
                    int cursorY = textTop + cursorLine * lineHeight;
                    canvas.drawRect(cursorX, cursorY, 1, lineHeight, element.textColor.get());
                }
            }
            canvas.endClip();
            if(element.horizontalScrollbarVisible)
            {
                int trackWidth = element.horizontalTrackWidth();
                int thumbWidth = element.horizontalThumbWidth(lines);
                int thumbX = element.horizontalThumbX(lines);
                int scrollbarY = layout.y + layout.height - element.scrollbarSize.get();
                canvas.drawRect(layout.x, scrollbarY, trackWidth, element.scrollbarSize.get(), Color.Black);
                canvas.drawRect(thumbX, scrollbarY, thumbWidth, element.scrollbarSize.get(), Color.Gray);
            }
            if(element.verticalScrollbarVisible)
            {
                int trackHeight = element.verticalTrackHeight();
                int thumbHeight = element.verticalThumbHeight(lines);
                int thumbY = element.verticalThumbY(lines);
                int scrollbarX = layout.x + layout.width - element.scrollbarSize.get();
                canvas.drawRect(scrollbarX, layout.y, element.scrollbarSize.get(), trackHeight, Color.Black);
                canvas.drawRect(scrollbarX, thumbY, element.scrollbarSize.get(), thumbHeight, Color.Gray);
            }
        }
    }
}