package dev.zeddevstuff.crafted.utils;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.IProp;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.annotations.Property;

import java.lang.reflect.Field;
import java.util.*;

public class SchemaGenerator
{
	public static String generateSchema(Crafted crafted)
	{
		XmlSchemaNode root = new XmlSchemaNode("xs:schema")
			.addAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
		XmlSchemaNode rootElement = elementNode("Crafted", DefineElement.ContentType.ANY, "The root element of a CraftedUI XML document.");
		root.addChild(rootElement);
		for(var element : crafted.elements.getClassToTagMap())
		{
			if(Objects.equals(element.getValue(), "Crafted"))
				continue;
			var elementClass = element.getKey();
			var elementAnnotation = elementClass.getAnnotation(DefineElement.class);
			if(elementAnnotation == null)
				continue;
			XmlSchemaNode elementNode = elementNode(elementAnnotation, element.getKey());
			root.addChild(elementNode);
		}
		return "<?xml version=\"1.0\" encoding=\"utf-16\"?>\n" + root.toXmlString();
	}

	private static XmlSchemaNode documentNode(Description doc)
	{
		XmlSchemaNode docNode = new XmlSchemaNode("xs:annotation");
		if(doc != null)
			docNode.addChild(new XmlSchemaNode("xs:documentation", null, doc.value()));
		return docNode;
	}
	private static XmlSchemaNode documentNode(String documentation)
	{
		XmlSchemaNode docNode = new XmlSchemaNode("xs:annotation");
		if(documentation != null && !documentation.isEmpty())
			docNode.addChild(new XmlSchemaNode("xs:documentation", null, documentation));
		return docNode;
	}

	private static XmlSchemaNode elementNode(DefineElement element, Class<?> elementClass)
	{
		XmlSchemaNode elementNode = new XmlSchemaNode("xs:element")
			.addAttribute("name", element.value());
		var elDoc = elementClass.getAnnotation(Description.class);
		if(elDoc != null)
			elementNode.addChild(documentNode(elDoc));
		var properties = Arrays.stream(elementClass.getFields()).filter(f -> IProp.class.isAssignableFrom(f.getType())).toList();
		boolean isComplexType = !properties.isEmpty() || element.contentType() != DefineElement.ContentType.NONE;
		if(isComplexType)
		{
			elementNode.addChild(complexTypeNode(element, properties));
		}
		else
		{
			elementNode.addAttribute("type", "xs:string");
			return elementNode;
		}
		return elementNode;
	}
	private static XmlSchemaNode elementNode(String name, DefineElement.ContentType contentType, String documentation)
	{
		XmlSchemaNode elementNode = new XmlSchemaNode("xs:element")
			.addAttribute("name", name);
		if(documentation != null && !documentation.isEmpty())
			elementNode.addChild(documentNode(documentation));
		if(contentType == DefineElement.ContentType.NONE)
			elementNode.addAttribute("type", "xs:string");
		else
			elementNode.addChild(complexTypeNode(name, contentType, List.of()));
		return elementNode;
	}

	private static XmlSchemaNode complexTypeNode(DefineElement element, List<Field> properties)
	{
		XmlSchemaNode complexTypeNode = new XmlSchemaNode("xs:complexType");
		XmlSchemaNode extensionNode = null;
		if(element.contentType() == DefineElement.ContentType.TEXT)
		{
			extensionNode = new XmlSchemaNode("xs:extension").addAttribute("base", "xs:string");
			complexTypeNode.addChild(new XmlSchemaNode("xs:simpleContent")
				.addChild(extensionNode));
		}
		else if(element.contentType() != DefineElement.ContentType.NONE)
		{
			extensionNode = complexTypeNode;
			var sequenceNode = new XmlSchemaNode("xs:sequence");
			HashMap<String, String> anyAttributes = new HashMap<>(){{
				put("minOccurs", "0");
				put("maxOccurs", "unbounded");
			}};
			sequenceNode.addChild(new XmlSchemaNode("xs:any", anyAttributes, new ArrayList<>()));
			complexTypeNode.addChild(sequenceNode);
		}
		else
			extensionNode = complexTypeNode;
		for(var property : properties)
		{
			XmlSchemaNode attributeNode = new XmlSchemaNode("xs:attribute")
				.addAttribute("name", property.getName());
			var propDoc = property.getAnnotation(Description.class);
			if(propDoc != null)
				attributeNode.addChild(documentNode(propDoc));
			extensionNode.addChild(attributeNode);
		}
		return complexTypeNode;
	}
	private static XmlSchemaNode complexTypeNode(String name, DefineElement.ContentType contentType, List<Field> properties)
	{
		XmlSchemaNode complexTypeNode = new XmlSchemaNode("xs:complexType");
		XmlSchemaNode extensionNode = null;
		if(contentType == DefineElement.ContentType.TEXT)
		{
			extensionNode = new XmlSchemaNode("xs:extension").addAttribute("base", "xs:string");
			complexTypeNode.addChild(new XmlSchemaNode("xs:simpleContent")
				.addChild(extensionNode));
		}
		else if(contentType != DefineElement.ContentType.NONE)
		{
			extensionNode = complexTypeNode;
			var sequenceNode = new XmlSchemaNode("xs:sequence");
			HashMap<String, String> anyAttributes = new HashMap<>(){{
				put("minOccurs", "0");
				put("maxOccurs", "unbounded");
			}};
			sequenceNode.addChild(new XmlSchemaNode("xs:any", anyAttributes, new ArrayList<>()));
			complexTypeNode.addChild(sequenceNode);
		}
		else
			extensionNode = complexTypeNode;
		for(var property : properties)
		{
			XmlSchemaNode attributeNode = new XmlSchemaNode("xs:attribute")
				.addAttribute("name", property.getName());
			var propDoc = property.getAnnotation(Description.class);
			if(propDoc != null)
				attributeNode.addChild(documentNode(propDoc));
			extensionNode.addChild(attributeNode);
		}
		return complexTypeNode;
	}

	private static XmlSchemaNode anySequenceNode()
	{
		HashMap<String, String> anyAttributes = new HashMap<>(){{
			put("minOccurs", "0");
			put("maxOccurs", "unbounded");
		}};
		return new XmlSchemaNode("xs:any", anyAttributes, new ArrayList<>());
	}
	static class XmlSchemaNode
	{
		public final String name;
		public final HashMap<String, String> attributes;
		public final String textContent;
		public final ArrayList<XmlSchemaNode> children;

		public XmlSchemaNode(String name)
		{
			this.name = name;
			this.attributes = new HashMap<>();
			this.children = new ArrayList<>();
			this.textContent = null;
		}

		public XmlSchemaNode(String name, HashMap<String, String> attributes, ArrayList<XmlSchemaNode> children)
		{
			this.name = name;
			if(attributes == null)
				attributes = new HashMap<>();
			this.attributes = attributes;
			if(children == null)
				children = new ArrayList<>();
			this.children = children;
			this.textContent = null;
		}
		public XmlSchemaNode(String name, HashMap<String, String> attributes, String textContent)
		{
			this.name = name;
			if(attributes == null)
				attributes = new HashMap<>();
			this.attributes = attributes;
			this.textContent = textContent;
			this.children = null;
		}

		public XmlSchemaNode addAttribute(String key, String value)
		{
			this.attributes.put(key, value);
			return this;
		}
		public XmlSchemaNode addChild(XmlSchemaNode child)
		{
			this.children.add(child);
			return this;
		}

		public String toXmlString()
		{
			return getXmlStringWithIndent(0);
		}
		private String getXmlStringWithIndent(int indentLevel)
		{
			StringBuilder sb = new StringBuilder();
			String indent = "  ".repeat(indentLevel);
			sb.append(indent).append("<").append(name);
			for (var entry : attributes.entrySet())
				sb.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
			if (children != null && !children.isEmpty())
			{
				sb.append(">\n");
				for (var child : children)
					sb.append(child.getXmlStringWithIndent(indentLevel + 1));
				sb.append(indent).append("</").append(name).append(">\n");
			}
			else
			{
				if (textContent != null)
					sb.append(">").append(textContent).append("</").append(name).append(">\n");
				else if (isSelfClosingTag(name))
					sb.append(" />\n");
				else
					sb.append("></").append(name).append(">\n");
			}
			return sb.toString();
		}
	}

	private static boolean isSelfClosingTag(String tagName)
	{
		return switch (tagName)
		{
			case "xs:any", "xs:attribute" -> true;
			default -> false;
		};
	}
}
