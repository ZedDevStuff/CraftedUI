package dev.zeddevstuff.crafted.parsing;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CraftedXmlParser
{
	static final DocumentBuilderFactory factory;
	static
	{
		factory = DocumentBuilderFactory.newInstance();

		try
		{
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
			factory.setExpandEntityReferences(false);
			factory.setXIncludeAware(false);
		}
		catch (ParserConfigurationException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @throws RuntimeException if the XML is invalid or cannot be parsed
	 */
	public static ParsedNode parse(String xml)
	{
		try
		{
            var builder = factory.newDocumentBuilder();
			var document = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
			var rootElement = document.getDocumentElement();
			return parsedNodeFromElement(rootElement);
		}
		catch (ParserConfigurationException | IOException | SAXException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static ParsedNode parsedNodeFromElement(org.w3c.dom.Element element)
	{
		var tag = element.getTagName();
		var attributes = new java.util.HashMap<String, String>();
		var attrMap = element.getAttributes();
		for (int i = 0; i < attrMap.getLength(); i++)
		{
			var attr = attrMap.item(i);
			if(attr.getNodeName().startsWith("xmlns") || attr.getNodeName().startsWith("xsi") || attr.getNodeName().contains(":"))
				continue;
			attributes.put(attr.getNodeName(), attr.getNodeValue());
		}
		var textContent = getTextContent(element);
		if (textContent != null && !textContent.isBlank())
		{
			attributes.put("text", textContent);
		}
		var childNodes = element.getChildNodes();
		var children = new java.util.ArrayList<ParsedNode>();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			var childNode = childNodes.item(i);
			if (childNode instanceof org.w3c.dom.Element)
			{
				children.add(parsedNodeFromElement((org.w3c.dom.Element) childNode));
			}
		}
		return new ParsedNode(tag, attributes, children.toArray(new ParsedNode[0]));
	}
	private static String getTextContent(org.w3c.dom.Element element)
	{
		var childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			var childNode = childNodes.item(i);
			if (childNode instanceof org.w3c.dom.Text)
			{
				return childNode.getTextContent();
			}
		}
		return null;
	}
}
