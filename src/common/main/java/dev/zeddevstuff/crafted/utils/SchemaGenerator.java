package dev.zeddevstuff.crafted.utils;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.IProp;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.annotations.Ignore;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.ui.elements.CraftedRoot;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class SchemaGenerator
{
	private SchemaGenerator() {}

	public static String generateSchema(Crafted crafted)
	{
		var types = new LinkedHashMap<Class<?>, XmlSchemaNode>();
		addType(CraftedElement.class, types, new HashSet<>());
		addType(CraftedRoot.class, types, new HashSet<>());

		var elements = new ArrayList<XmlSchemaNode>();
		for(var entry : crafted.elements.getClassToTagMapEntrySet())
		{
			var elementClass = entry.getKey();
			var annotation = elementClass.getAnnotation(DefineElement.class);
			if(annotation == null || !CraftedElement.class.isAssignableFrom(elementClass))
				continue;

			addType(elementClass, types, new HashSet<>());
			elements.add(elementNode(entry.getValue(), elementClass));
		}

		var root = new XmlSchemaNode("xs:schema")
			.addAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
		for(var type : types.values())
			root.addChild(type);
		for(var element : elements)
			root.addChild(element);

		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + root.toXmlString();
	}

	private static void addType(Class<?> elementClass, Map<Class<?>, XmlSchemaNode> types, Set<Class<?>> visiting)
	{
		if(types.containsKey(elementClass) || !visiting.add(elementClass))
			return;

		var parent = elementClass.getSuperclass();
		if (parent != null && CraftedElement.class.isAssignableFrom(parent))
			addType(parent, types, visiting);

		var type = new XmlSchemaNode("xs:complexType").addAttribute("name", typeName(elementClass));
		if (parent != null && CraftedElement.class.isAssignableFrom(parent))
		{
			var extension = new XmlSchemaNode("xs:extension")
				.addAttribute("base", typeName(parent));
			type.addChild(new XmlSchemaNode("xs:complexContent").addChild(extension));
			addAttributes(extension, elementClass);
		}
		else
		{
			type.addAttribute("mixed", "true");
			type.addChild(new XmlSchemaNode("xs:sequence").addChild(anySequenceNode()));
			addAttributes(type, elementClass);
		}

		visiting.remove(elementClass);
		types.put(elementClass, type);
	}

	private static void addAttributes(XmlSchemaNode parent, Class<?> elementClass)
	{
		for(var field : elementClass.getDeclaredFields())
		{
			if (!isSchemaProperty(field))
				continue;

			var attribute = new XmlSchemaNode("xs:attribute").addAttribute("name", field.getName());
			var description = field.getAnnotation(Description.class);
			if (description != null)
				attribute.addChild(documentNode(description.value()));
			parent.addChild(attribute);
		}
	}

	private static boolean isSchemaProperty(Field field)
	{
		return IProp.class.isAssignableFrom(field.getType())
			&& !field.isAnnotationPresent(Ignore.class)
			&& !Modifier.isStatic(field.getModifiers());
	}

	private static XmlSchemaNode elementNode(String tag, Class<?> elementClass)
	{
		var element = new XmlSchemaNode("xs:element")
			.addAttribute("name", tag)
			.addAttribute("type", typeName(elementClass));
		var description = elementClass.getAnnotation(Description.class);
		if (description != null)
			element.addChild(documentNode(description.value()));
		return element;
	}

	private static String typeName(Class<?> elementClass)
	{
		return elementClass.getSimpleName();
	}

	private static XmlSchemaNode documentNode(String documentation)
	{
		return new XmlSchemaNode("xs:annotation")
			.addChild(new XmlSchemaNode("xs:documentation", null, documentation));
	}

	private static XmlSchemaNode anySequenceNode()
	{
		return new XmlSchemaNode("xs:any")
			.addAttribute("minOccurs", "0")
			.addAttribute("maxOccurs", "unbounded")
			.addAttribute("processContents", "lax");
	}

	private static final class XmlSchemaNode
	{
		private final String name;
		private final Map<String, String> attributes = new LinkedHashMap<>();
		private final List<XmlSchemaNode> children = new ArrayList<>();
		private final String textContent;

		private XmlSchemaNode(String name)
		{
			this(name, null, null);
		}

		private XmlSchemaNode(String name, Map<String, String> attributes, String textContent)
		{
			this.name = name;
			if (attributes != null)
				this.attributes.putAll(attributes);
			this.textContent = textContent;
		}

		private XmlSchemaNode addAttribute(String key, String value)
		{
			attributes.put(key, value);
			return this;
		}

		private XmlSchemaNode addChild(XmlSchemaNode child)
		{
			children.add(child);
			return this;
		}

		private String toXmlString()
		{
			return toXmlString(0);
		}

		private String toXmlString(int indentLevel)
		{
			var indent = "  ".repeat(indentLevel);
			var xml = new StringBuilder(indent).append('<').append(name);
			for (var attribute : attributes.entrySet())
				xml.append(' ').append(attribute.getKey()).append("=\"").append(escape(attribute.getValue())).append("\"");

			if (!children.isEmpty())
			{
				xml.append(">\n");
				for (var child : children)
					xml.append(child.toXmlString(indentLevel + 1));
				xml.append(indent).append("</").append(name).append(">\n");
			}
			else if (textContent != null)
				xml.append('>').append(escape(textContent)).append("</").append(name).append(">\n");
			else
				xml.append(" />\n");
			return xml.toString();
		}

		private static String escape(String value)
		{
			return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&apos;");
		}
	}
}