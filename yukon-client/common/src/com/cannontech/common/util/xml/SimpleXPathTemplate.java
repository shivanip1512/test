/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cannontech.common.util.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;
import org.joda.time.Instant;
import org.springframework.xml.namespace.SimpleNamespaceContext;
import org.springframework.xml.transform.TransformerObjectSupport;
import org.springframework.xml.transform.TraxUtils;
import org.springframework.xml.xpath.NodeCallbackHandler;
import org.springframework.xml.xpath.NodeMapper;
import org.springframework.xml.xpath.XPathException;
import org.springframework.xml.xpath.XPathOperations;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.common.util.ObjectMapper;

/**
 * Implementation of {@link XPathOperations} that uses JAXP 1.3. JAXP 1.3 is part of Java SE since 1.5.
 * <p/>
 * Namespaces can be set using the <code>namespaces</code> property.
 *
 * @author Arjen Poutsma
 * @see #setNamespaces(java.util.Properties)
 * @since 1.0.0
 */
public class SimpleXPathTemplate extends TransformerObjectSupport {

    private Properties namespaces;

    /** Returns namespaces used in the XPath expression. */
    public Properties getNamespaces() {
        return namespaces;
    }

    /** Sets namespaces used in the XPath expression. Maps prefixes to namespaces. */
    public void setNamespaces(Properties namespaces) {
        this.namespaces = namespaces;
    }

    public void evaluate(String expression, NodeCallbackHandler callbackHandler)
    throws XPathException {
        evaluate(expression, new NodeCallbackHandlerNodeMapper(callbackHandler));
    }

    /** Static inner class that adapts a {@link NodeCallbackHandler} to the interface of {@link NodeMapper}. */
    private static class NodeCallbackHandlerNodeMapper implements NodeMapper {

        private final NodeCallbackHandler callbackHandler;

        public NodeCallbackHandlerNodeMapper(NodeCallbackHandler callbackHandler) {
            this.callbackHandler = callbackHandler;
        }

        public Object mapNode(Node node, int nodeNum) throws DOMException {
            callbackHandler.processNode(node);
            return null;
        }
    }


    /**
     * Returns the root element of the given source.
     *
     * @param source the source to get the root element from
     * @return the root element
     */
    protected Element getRootElement(Source source) throws TransformerException {
        DOMResult domResult = new DOMResult();
        transform(source, domResult);
        Document document = (Document) domResult.getNode();
        return document.getDocumentElement();
    }
    private XPathFactory xpathFactory;
    private Source context;

    public SimpleXPathTemplate() {
        this(XPathFactory.DEFAULT_OBJECT_MODEL_URI);
    }

    public SimpleXPathTemplate(String xpathFactoryUri) {
        try {
            xpathFactory = XPathFactory.newInstance(xpathFactoryUri);
        }
        catch (XPathFactoryConfigurationException ex) {
            throw new XPathException("Could not create XPathFactory", ex);
        }
    }

    /**
     * This method returns null if the tag isn't there, otherwise a Boolean object
     * of the tag contents.
     * @param expression
     * @return
     * @throws XPathException
     */
    public Boolean evaluateAsBoolean(String expression) throws XPathException {
        return evaluateAsBoolean(expression, null);
    }
    
    public Boolean evaluateAsBoolean(String expression, Boolean defaultValue) {
        Node result = evaluateAsNode(expression);
        return result == null ? defaultValue : Boolean.valueOf(result.getTextContent());
    }

    public Node evaluateAsNode(String expression) throws XPathException {
        return (Node) evaluate(expression, XPathConstants.NODE);
    }

    public List<Node> evaluateAsNodeList(String expression) throws XPathException {
        NodeList result = (NodeList) evaluate(expression, XPathConstants.NODESET);
        List<Node> nodes = new ArrayList<Node>(result.getLength());
        for (int i = 0; i < result.getLength(); i++) {
            nodes.add(result.item(i));
        }
        return nodes;
    }

    /**
     * Evaluate value at expression as a Double.
     * Returns null if the expression defines a node that does not exists.
     * @param expression
     * @return
     * @throws XPathException
     */
    public Double evaluateAsDouble(String expression) throws XPathException {
    	
    	Double num = evaluateNumber(expression);
        return num == null ? null : num;
    }

    /**
     * Evaluate value at expression as a Float.
     * Returns null if the expression defines a node that does not exists.
     * @param expression
     * @return
     * @throws XPathException
     */
	public Float evaluateAsFloat(String expression) throws XPathException {
		
    	Double num = evaluateNumber(expression);
        return num == null ? null : num.floatValue();
    }
	
	/**
     * Evaluate value at expression as a Long.
     * Returns null if the expression defines a node that does not exists.
     * @param expression
     * @return
     * @throws XPathException
     */
    public Long evaluateAsLong(String expression) throws XPathException {
        
    	Double num = evaluateNumber(expression);
        return num == null ? null : num.longValue();
    }
    
    /**
     * Evaluate value at expression as a Integer.
     * Returns null if the expression defines a node that does not exists.
     * @param expression
     * @return
     * @throws XPathException
     */
    public Integer evaluateAsInt(String expression) throws XPathException {
        return evaluateAsInt(expression, null);
    }
    
    public Integer evaluateAsInt(String expression, Integer defaultInt) throws XPathException {
        Double num = evaluateNumber(expression);
        return (num == null) ? defaultInt : (Integer) num.intValue();
    }
    
    /**
     * Helper for long, float, double, etc evaluators. 
	 * Evaluates number from expression. Returns null if expression defines node that does
	 * not exists, or if the number is NaN. Otherwise, return the number as a Double to be
	 * cast in the type required by calling method.
	 * @param expression
	 * @return
	 */
	private Double evaluateNumber(String expression) {
		
		Double num = (Double) evaluate(expression, XPathConstants.NUMBER);
    	if (num.equals(Double.NaN)) {
    		return null;
    	}
    	return num;
	}
	
    /**
     * Evaluate value at expression as a String.
     * Returns null if the expression defines a node that does not exists.
     * @param expression
     * @return
     * @throws XPathException
     */
    public String evaluateAsString(String expression) throws XPathException {
    	
    	if (evaluateAsNode(expression) == null) {
    		return null;
    	}
    	
        return (String) evaluate(expression, XPathConstants.STRING);
    }
    
    public List<String> evaluateAsStringList(String expression) throws XPathException {
        return evaluate(expression, new ObjectMapper<Node, String>() {
            @Override
            public String map(Node from) throws ObjectMappingException {
                String textContent = from.getTextContent();
                
                return textContent;
            }
        });
    }

    /**
     * Parse an element for an Instant. If the the element doesn't exist or doesn't represent a
     * valid date, it returns null. Otherwise it creates a Date object from the string and uses the
     * Date to generate an Instant.
     * @throws XPathException
     */
    public Instant evaluateAsInstant(String expression) throws XPathException {
        return evaluateAsInstant(expression, null);
    }

    /**
     * Parse an element for an Instant. If the the element doesn't exist or doesn't represent a
     * valid date, it returns the supplied defaultValue. Otherwise it creates a Date object from the
     * string and uses the Date to generate an Instant.
     * @throws XPathException
     */
    public Instant evaluateAsInstant(String expression, Instant defaultValue) throws XPathException {
        Date date = evaluateAsDate(expression);
        if (date == null) {
            return defaultValue;
        }

        return new Instant(date);
    }
    /**
     * Parse an element for a date. If the element does not exists or is empty, return null.
     * Otherwise return a Date object for the date string in the element.
     * @param expression
     * @return
     * @throws XPathException
     */
    public Date evaluateAsDate(String expression, Date defaultDate) throws XPathException {
        
        String dateStr = null;
        Date date = null;
        try {
            dateStr = evaluateAsString(expression);
        } catch (XPathException e) {}
        
        if (dateStr != null) {
            date = Iso8601DateUtil.parseIso8601Date(dateStr.trim());
        }
    
        return date==null ? defaultDate : date;
    }
    
    public Date evaluateAsDate(String expression) throws XPathException {
        return evaluateAsDate(expression, null);
    }

    public Object evaluateAsObject(String expression, NodeMapper nodeMapper) throws XPathException {
        Node node = evaluateAsNode(expression);
        if (node != null) {
            try {
                return nodeMapper.mapNode(node, 0);
            }
            catch (DOMException ex) {
                throw new XPathException("Mapping resulted in DOMException", ex);
            }
        }
        else {
            return null;
        }
    }

    public List evaluate(String expression, NodeMapper nodeMapper) throws XPathException {
        NodeList nodes = (NodeList) evaluate(expression, XPathConstants.NODESET);
        List results = new ArrayList(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            try {
                results.add(nodeMapper.mapNode(nodes.item(i), i));
            }
            catch (DOMException ex) {
                throw new XPathException("Mapping resulted in DOMException", ex);
            }
        }
        return results;
    }
    
    public List<Integer> evaluateAsIntegerList(String expression) throws XPathException {
        return evaluate(expression, new ObjectMapper<Node, Integer>() {
            @Override
            public Integer map(Node from) throws ObjectMappingException {
                String textContent = from.getTextContent();
                
                return Integer.valueOf(textContent);
            }
        });
    }
    
    public List<Long> evaluateAsLongList(String expression) throws XPathException {
        return evaluate(expression, new ObjectMapper<Node, Long>() {
            @Override
            public Long map(Node from) throws ObjectMappingException {
                String textContent = from.getTextContent();
                
                return Long.valueOf(textContent);
            }
        });
    }
    
    public <K> K evaluateAsObject(String expression, ObjectMapper<? super Node, K> nodeMapper) throws XPathException {
        Node node = evaluateAsNode(expression);
        if (node != null) {
            try {
                return nodeMapper.map(node);
            }
            catch (DOMException ex) {
                throw new XPathException("Mapping resulted in DOMException", ex);
            }
        }
        else {
            return null;
        }
    }

    public <K> List<K> evaluate(String expression, ObjectMapper<? super Node, K> nodeMapper) throws XPathException {
        NodeList nodes = (NodeList) evaluate(expression, XPathConstants.NODESET);
        List<K> results = new ArrayList<K>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            try {
                results.add(nodeMapper.map(nodes.item(i)));
            }
            catch (DOMException ex) {
                throw new XPathException("Mapping resulted in DOMException", ex);
            }
        }
        return results;
    }
    
    private Object evaluate(String expression, QName returnType) throws XPathException {
        XPath xpath = xpathFactory.newXPath();
        if (getNamespaces() != null && !getNamespaces().isEmpty()) {
            SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext();
            namespaceContext.setBindings(getNamespaces());
            xpath.setNamespaceContext(namespaceContext);
        }
        try {
            if (TraxUtils.isStaxSource(context)) {
                Element element = getRootElement(context);
                return xpath.evaluate(expression, element, returnType);
            }
            else if (context instanceof SAXSource) {
                SAXSource saxSource = (SAXSource) context;
                return xpath.evaluate(expression, saxSource.getInputSource(), returnType);
            }
            else if (context instanceof DOMSource) {
                DOMSource domSource = (DOMSource) context;
                return xpath.evaluate(expression, domSource.getNode(), returnType);
            }
            else {
                throw new IllegalArgumentException("context type unknown");
            }
        }
        catch (javax.xml.xpath.XPathException ex) {
            throw new XPathException("Could not evaluate XPath expression [" + expression + "]", ex);
        }
        catch (TransformerException ex) {
            throw new XPathException("Could not transform context to DOM Node", ex);
        }
    }

    public void setContext(Source context) {
        if (context instanceof StreamSource) {
            try {
                // parse into JDOM to allow reusability (because InputStream can't be read twice)
                StreamSource stream = (StreamSource) context;
                SAXBuilder builder = new SAXBuilder();
                org.jdom.Document document;
                if (stream.getInputStream() != null) {
                    document = builder.build(stream.getInputStream());
                }
                else if (stream.getReader() != null) {
                    document = builder.build(stream.getReader());
                }
                else {
                    throw new IllegalArgumentException("StreamSource contains neither InputStream nor Reader");
                }
                this.context = new JDOMSource(document);
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not parse context", e);
            }
        } else {
            this.context = context;
        }
    }

    public void setContext(String rawXml) {
        StringReader reader = new StringReader(rawXml);

        SAXBuilder builder = new SAXBuilder();
        org.jdom.Document document;
        
        try {
            document = builder.build(reader);
        } catch (JDOMException e) {
            throw new XPathException("JDOMException while building the Souce object");
        } catch (IOException e) {
            throw new XPathException("IOException while building the Souce object");
        }
        this.context = new JDOMSource(document);
    }
    
}
