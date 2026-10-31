package dev.zeddevstuff.crafted.input;

import dev.zeddevstuff.crafted.ui.CraftedElement;

// I'm honestly not sure if this is the best way.
// Looked around a bit, also had Copilot look for options multiple times, and it came up with something like this most of the time,
// so I guess I'll use it with my own spin (hopefully not butchering anything)
// Other option was to use an Enum + EnumSet, but then I'd need to put it inside CraftedElement or still wrap that in a class, which seems messy to me
public class InteractionState
{
    public static final int NONE = 0;
    public static final int HOVERED = 1;
    public static final int PRESSED = 1 << 1;
    public static final int ARMED = 1 << 2;
    public static final int FOCUSED = 1 << 3;
    public static final int DISABLED = 1 << 4;

    private final Object key;
    private int flags;

    // I hate Java not having internal access modifiers, makes me need to do stuff like this >:(
    public InteractionState(Object key) { this.key = key; }

    public boolean has(int state) { return (flags & state) != 0; }
    public boolean isHovered()  { return has(HOVERED); }
    public boolean isPressed()  { return has(PRESSED); }
    public boolean isArmed()    { return has(ARMED); }
    public boolean isFocused()  { return has(FOCUSED); }
    public boolean isDisabled() { return has(DISABLED); }
    public boolean isActive()   { return has(PRESSED | ARMED); } // visual "pressed" feedback
    void set(int state, boolean on) { flags = on ? flags | state : flags & ~state; }
    // Just so the disabled prop can bind to this
    public void set(Object key, int state, boolean on) { if(this.key == key) set(state, on); }
    public boolean hasNone() { return has(NONE); }
}
