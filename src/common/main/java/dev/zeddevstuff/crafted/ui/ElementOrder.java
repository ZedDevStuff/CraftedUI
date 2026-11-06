package dev.zeddevstuff.crafted.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ElementOrder
{
    private ElementOrder() {}

    public record Entry(CraftedElement element, long absoluteZIndex, int traversalOrder) {}

    public static List<Entry> collect(CraftedElement root)
    {
        var entries = new ArrayList<Entry>();
        collect(root, root.zIndex.get(), entries, new int[] { 0 });
        entries.sort(Comparator
            .comparingLong(Entry::absoluteZIndex)
            .thenComparingInt(Entry::traversalOrder));
        return List.copyOf(entries);
    }

    private static void collect(
        CraftedElement element,
        long absoluteZIndex,
        List<Entry> entries,
        int[] traversalOrder
    )
    {
        entries.add(new Entry(element, absoluteZIndex, traversalOrder[0]++));
        for (var child : element.getChildren())
            collect(child, absoluteZIndex + child.zIndex.get(), entries, traversalOrder);
    }
}
