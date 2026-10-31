package dev.zeddevstuff.crafted.utils;

public class TextEditor
{
    private String text;
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    private int cursorPosition;
    public int getCursorPosition() { return cursorPosition; }
    public void setCursorPosition(int position)
    {
        if (position < 0 || position > text.length())
            throw new IllegalArgumentException("Cursor position out of bounds: " + position);
        this.cursorPosition = position;
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

    private boolean multiline;
    public boolean isMultiline() { return multiline; }

    public TextEditor(String initialText, boolean multiline)
    {
        this.text = initialText;
        this.cursorPosition = initialText.length();
        this.multiline = multiline;
    }

    public void insert(String newText)
    {
        text = text.substring(0, cursorPosition) + newText + text.substring(cursorPosition);
        cursorPosition += newText.length();
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
        cursorPosition = start + newText.length();
        selectionStart = selectionEnd = cursorPosition;
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
        cursorPosition = newPosition;
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
        cursorPosition = 0;
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
        cursorPosition = text.length();
    }

    public void delete(boolean backspace)
    {
        if (backspace && cursorPosition > 0)
        {
            text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
            cursorPosition--;
        }
        else if (!backspace && cursorPosition < text.length())
        {
            text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
        }
    }
}
