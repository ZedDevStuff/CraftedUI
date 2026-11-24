package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.UIDocumentBuilder;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.ui.*;
import dev.zeddevstuff.crafted.utils.TypedList;

import java.util.Map;

@DefineElement("List")
@Description("Displays a list of items using a template for each item.")
public class List extends ScrollView implements MetaElement
{
	public final Prop<TypedList<?>> items = registerProp(TypedList.class, "items", null, this::parseItems);
	public final Prop<BindingExtractor> extractor = registerProp(BindingExtractor.class, "extractor", null, this::parseExtractor);
	public final Prop<String> bindingPrefix = registerProp("bindingPrefix", "", this::parseString);

	private ParsedNode templateNode;
	private TypedList<?> observedItems;
	private final TypedList.IValuesChangeListener<Object> itemsChangeListener = this::itemsChanged;

	public List(UIContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		items.addListener((prop, oldValue, newValue) -> {
			stopObserving(oldValue);
			startObserving(newValue);
			refresh();
		});
	}

	private TypedList<?> parseItems(String name, TypedList<?> ignored)
	{
		var value = ctx.getVar(TypedList.class, name);
		return value == null ? null : value.value;
	}

	private BindingExtractor parseExtractor(String name, BindingExtractor ignored)
	{
		var value = ctx.getVar(BindingExtractor.class, name);
		return value == null ? null : value.value;
	}

	private String parseString(String value, String ignored)
	{
		return value;
	}

	@Override
	public void setTemplateNodes(ParsedNode[] templateNodes)
	{
		if(templateNodes.length != 1)
			throw new IllegalArgumentException("List requires exactly one child template.");
		templateNode = templateNodes[0];
		startObserving(items.get());
		refresh();
	}

	private void startObserving(TypedList<?> list)
	{
		if(list == null || list == observedItems)
			return;
		observedItems = list;
		@SuppressWarnings("unchecked")
		var typedList = (TypedList<Object>) list;
		typedList.addChangeListener(itemsChangeListener);
	}

	private void stopObserving(TypedList<?> list)
	{
		if(list == null || list != observedItems)
			return;
		@SuppressWarnings("unchecked")
		var typedList = (TypedList<Object>) list;
		typedList.removeChangeListener(itemsChangeListener);
		observedItems = null;
	}

	private void itemsChanged()
	{
		refresh();
	}

	private void refresh()
	{
		if(templateNode == null)
			return;
		removeChildren(getChildren().toArray(new CraftedElement[0]));
		var list = items.get();
		if(list == null)
			return;
		for(var item : list)
		{
			var extractorValue = extractor.get();
			var scope = extractorValue == null
				? new BindingScope(name -> null, bindingPrefix.get())
				: extractorValue.extract(item);
			if(scope == null)
				scope = new BindingScope(name -> null, bindingPrefix.get());
			ctx.pushBindingScope(scope);
			try
			{
				addChild(UIDocumentBuilder.buildTree(ctx, templateNode));
			}
			finally
			{
				ctx.popBindingScope();
			}
		}
	}

	@Override
	protected void copyExtraStateTo(CraftedElement clone)
	{
		if(clone instanceof List list && templateNode != null)
			list.setTemplateNodes(new ParsedNode[]{templateNode});
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(List.class, "List", List::new);
	}
}