package com.afoix.metadatavalidator.entities;

import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import com.google.common.collect.Streams;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;
import java.util.stream.Stream;

/**
 * An entity that is an XML DOM hierarchy.
 */
public class XMLEntity implements Entity {
    private final Node root;
    private String identifier;

    public XMLEntity(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean hasAttribute(String attributeNameOrPath) throws InvalidAttributeNameOrPathException {
        XPath xPath = XPathFactory.newDefaultInstance().newXPath();
        XPathNodes nodes;
        try {
            nodes = xPath.evaluateExpression(attributeNameOrPath, root, XPathNodes.class);
        } catch (XPathExpressionException e) {
            throw new InvalidAttributeNameOrPathException("Attribute name/path was not a valid XPath expression", attributeNameOrPath, e);
        }
        return nodes.size() > 0;
    }

    @Override
    public Stream<Object> getAttributeValues(String attributeNameOrPath) throws InvalidAttributeNameOrPathException {
        XPath xPath = XPathFactory.newDefaultInstance().newXPath();
        XPathNodes nodes;
        try {
            nodes = xPath.evaluateExpression(attributeNameOrPath, root, XPathNodes.class);
        } catch (XPathExpressionException e) {
            throw new InvalidAttributeNameOrPathException("Attribute name/path was not a valid XPath expression", attributeNameOrPath, e);
        }
        return Streams.stream(nodes).map(node ->
                node.hasChildNodes() ? node.getFirstChild().getNodeValue() : null);
    }

    @Override
    public void setCaseSensitive(boolean isCaseSensitive) {
        if (!isCaseSensitive) {
            throw new RuntimeException("XML does not support case-insensitive");
        }
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }
}
