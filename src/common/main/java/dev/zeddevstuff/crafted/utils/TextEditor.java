package dev.zeddevstuff.crafted.utils;

import dev.zeddevstuff.crafted.platform.IFont;
import dev.zeddevstuff.crafted.types.Float2;

import java.util.Objects;

import javax.annotation.Nonnull;

public class TextEditor
{
    private boolean dirty = false;
    public boolean isDirty() { return dirty; }
    public void clearDirty() { dirty = false; }

    private String text;
    public String getText() { return text; }
    public void setText(@Nonnull String text)
    {
        Objects.requireNonNull(text, "Text cannot be null");
        if(!Objects.equals(this.text, text))
            dirty = true;
        if(cursorPosition > text.length())
            cursorPosition = text.length();
        if(selectionStart > text.length())
            selectionStart = text.length();
        if(selectionEnd > text.length())
            selectionEnd = text.length();
        this.text = text;

    }
    private float cursorBlinkTimer;
    private boolean cursorVisible;
    public boolean isCursorVisible() { return cursorVisible; }
    private int cursorPosition;
    public int getCursorPosition() { return cursorPosition; }
    public void setCursorPosition(int position)
    {
        if (position < 0 || position > text.length())
            throw new IllegalArgumentException("Cursor position out of bounds: " + position);
        this.cursorPosition = position;
        cursorBlinkTimer = 0;
        cursorVisible = true;
    }

    private int selectionStart;
    public int getSelectionStart() { return selectionStart; }
    public void setSelectionStart(int position)
    {
        if (position < 0 || position > text.length())
            throw new IllegalArgumentException("Selection start position out of bounds: " + position);
        this.selectionStart = position;
    }
    private int selectionEnd;
    public int getSelectionEnd() { return selectionEnd; }
    public void setSelectionEnd(int position)
    {
        if (position < 0 || position > text.length())
            throw new IllegalArgumentException("Selection end position out of bounds: " + position);
        this.selectionEnd = position;
    }
    public void clearSelection()
    {
        selectionStart = selectionEnd = cursorPosition;
    }

    public boolean hasSelection()
    {
        return selectionStart != selectionEnd;
    }

    public String getSelectedText()
    {
        int start = Math.min(selectionStart, selectionEnd);
        int end = Math.max(selectionStart, selectionEnd);
        return text.substring(start, end);
    }

    public void selectAll()
    {
        selectionStart = 0;
        selectionEnd = text.length();
        setCursorPosition(text.length());
    }

    private boolean multiline;
    public boolean isMultiline() { return multiline; }

    public TextEditor(String initialText, boolean multiline)
    {
        this.text = initialText;
        this.cursorPosition = initialText.length();
        this.multiline = multiline;
    }

    public boolean updateBlink(float deltaTime)
    {
        cursorBlinkTimer += deltaTime;
        if (cursorBlinkTimer >= 0.5f)
        {
            cursorBlinkTimer = 0;
            cursorVisible = !cursorVisible;
        }
        return cursorVisible;
    }

    public String getTextBeforeCursor()
    {
        return text.substring(0, cursorPosition);
    }

    public void insert(String newText)
    {
        if (hasSelection())
        {
            replaceSelection(newText);
            return;
        }
        text = text.substring(0, cursorPosition) + newText + text.substring(cursorPosition);
        cursorPosition += newText.length();
        cursorBlinkTimer = 0;
        cursorVisible = true;
        dirty = true;
    }
    public void insert(char newChar)
    {
        insert(String.valueOf(newChar));
    }

    public void replaceSelection(String newText)
    {
        int start = Math.min(selectionStart, selectionEnd);
        int end = Math.max(selectionStart, selectionEnd);
        text = text.substring(0, start) + newText + text.substring(end);
        setCursorPosition(start + newText.length());
        selectionStart = selectionEnd = cursorPosition;
        dirty = true;
    }

    // TODO: Support control for word-wise movement.
    public void moveCursor(int offset, boolean ctrl, boolean select)
    {
        int newPosition = cursorPosition + offset;
        if (newPosition < 0) newPosition = 0;
        if (newPosition > text.length()) newPosition = text.length();
        if (select)
        {
            if (selectionStart == selectionEnd)
                selectionStart = cursorPosition;
            selectionEnd = newPosition;
        }
        else
        {
            selectionStart = selectionEnd = newPosition;
        }
        setCursorPosition(newPosition);
    }
    public void moveCursorToStart(boolean select)
    {
        if (select)
        {
            if (selectionStart == selectionEnd)
                selectionStart = cursorPosition;
            selectionEnd = 0;
        }
        else
        {
            selectionStart = selectionEnd = 0;
        }
        setCursorPosition(0);
    }
    public void moveCursorToEnd(boolean select)
    {
        if (select)
        {
            if (selectionStart == selectionEnd)
                selectionStart = cursorPosition;
            selectionEnd = text.length();
        }
        else
        {
            selectionStart = selectionEnd = text.length();
        }
        setCursorPosition(text.length());
    }

    public void moveCursorToPoint(IFont font, Float2 position, float scale)
    {
        int newPosition = getCursorPositionAtX(font, position.x(), scale);
        setCursorPosition(newPosition);
        clearSelection();
    }
    public void extendSelectionToPoint(IFont font, Float2 position, float scale)
    {
        int newPosition = getCursorPositionAtX(font, position.x(), scale);
        setCursorPosition(newPosition);
        selectionEnd = newPosition;
    }

    private int getCursorPositionAtX(IFont font, float x, float scale)
    {
        if (x <= 0 || text.isEmpty())
            return 0;

        float previousWidth = 0;
        for (int position = 1; position <= text.length(); position++)
        {
            float currentWidth = font.measureText(text.substring(0, position), scale, -1).width();
            if (x < (previousWidth + currentWidth) / 2)
                return position - 1;
            previousWidth = currentWidth;
        }
        return text.length();
    }

    public void delete(boolean backspace)
    {
        if (hasSelection())
        {
            replaceSelection("");
            return;
        }
        if (backspace && cursorPosition > 0)
        {
            text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
            setCursorPosition(cursorPosition - 1);
            dirty = true;
        }
        else if (!backspace && cursorPosition < text.length())
        {
            text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
            dirty = true;
        }
    }
}
