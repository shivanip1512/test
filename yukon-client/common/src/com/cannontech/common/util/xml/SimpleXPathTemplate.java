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

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.transform.JDOMSource;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.xml.namespace.SimpleNamespaceContext;
import org.springframework.xml.transform.TransformerObjectSupport;
import org.springframework.xml.xpath.XPathException;
import org.springframework.xml.xpath.XPathOperations;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.exception.InvalidDateFormatException;
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
    private static Double emptyField = Double.NaN;

    /** Returns namespaces used in the XPath expression. */
    public Properties getNamespaces() {
        return namespaces;
    }

    /** Sets namespaces used in the XPath expression. Maps prefixes to namespaces. */
    public void setNamespaces(Properties namespaces) {
        this.namespaces = namespaces;
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
        return evaluateAsBoolean(expression, false);
    }
    
    public Boolean evaluateAsBoolean(String expression, Boolean defaultValue) {
        checkArgument(expression != null);

        Node result = evaluateAsNode(expression);
        if (result == null) {
            return defaultValue;
        }
        
        String textContent = StringUtils.trim(result.getTextContent());
        if (StringUtils.isBlank(textContent)) {
            return true;
        }
        
        if (textContent.equals("0")) {
            return false;
        }
        if (textContent.equals("1")) {
            return true;
        }
        
        if (textContent.equalsIgnoreCase("true") || textContent.equalsIgnoreCase("false")) {
            return Boolean.valueOf(textContent);
        }
            
        throw new IllegalArgumentException("A supplied boolean element does not have a valid boolean value.");
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
     * Return NaN if the expression exist but is empty.
     * @param expression
     * @return
     * @throws XPathException
     */
    public Double evaluateAsDouble(String expression, boolean isNaNForBlank) throws XPathException {
        checkArgument(expression != null);

        Double num = null;
        num = evaluateNumber(expression, isNaNForBlank);
        return num;
    }

    /**
     * Evaluate value at expression as a Float.
     * Returns null if the expression defines a node that does not exists.
     */
	public Float evaluateAsFloat(String expression) {
        checkArgument(expression != null);

    	Double num = evaluateNumber(expression, false);
        return num == null ? null : num.floatValue();
    }
	
	/**
     * Evaluate value at expression as a Long.
     * Returns null if the expression defines a node that does not exists.
     * @throws XPathException
     */
    public Long evaluateAsLong(String expression) {
        return evaluateAsLong(expression, null);
    }
    
    /**
     * Evaluate value at expression as a Long.
     * Returns the defaultValue if the expression defines a node that does not exists.
     * @throws XPathException
     */
    public Long evaluateAsLong(String expression, Long defaultValue) {
        checkArgument(expression != null);

        Double num = evaluateNumber(expression, false);
        if (num == null) {
            return defaultValue;
        }
        
        return num.longValue();
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
        checkArgument(expression != null);

        Double num = evaluateNumber(expression, false);
        return (num == null) ? defaultInt : (Integer) num.intValue();
    }
    
    /**
     * Helper for long, float, double, etc evaluators. 
	 * Evaluates number from expression. Returns null if expression defines node that does
	 * not exists, or if the number is NaN. Otherwise, return the number as a Double to be
	 * cast in the type required by calling method.
	 * 
	 * @throws NumberFormatException
	 */
    private Double evaluateNumber(String expression, boolean isNaNForBlank) throws NumberFormatException {

        // Check to see if it is empty. If it is return null.
        if (!isNaNForBlank && StringUtils.isBlank(evaluateAsString(expression))) {
            return null;
        } else {
            // checks if fields exist or not
            if (evaluateAsString(expression) == null) {
                return null;
            }
            // checks if fields are empty or whitespace characters
            if (StringUtils.isBlank(evaluateAsString(expression))) {
                return emptyField;
            }
        }

        Double num = (Double) evaluate(expression, XPathConstants.NUMBER);
        if (num.equals(Double.NaN)) {
            throw new NumberFormatException();
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
     * Parse an element for a Duration. If the the element doesn't exist or doesn't represent a
     * valid Duration, it returns null. 
     * @throws XPathException
     */
    public Duration evaluateAsDuration(String expression) throws XPathException {
        return evaluateAsDuration(expression, null);
    }

    /**
     * Parse an element for an Duration. If the the element doesn't exist or doesn't represent a
     * valid Duration, it returns the supplied defaultValue.
     * @throws XPathException
     */
    public Duration evaluateAsDuration(String expression, Duration defaultValue) throws XPathException {
        checkArgument(expression != null);

        Long durationInFloat = evaluateAsLong(expression);
        if (durationInFloat == null) {
            return defaultValue;
        }

        return new Duration(durationInFloat);
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
     * @throws XPathException
     */
    public Date evaluateAsDate(String expression) throws XPathException {
        return evaluateAsDate(expression, null);
    }
    
    /**
     * Parse an element for a date. If the element does not exists or is empty, return defaultDate.
     * Otherwise return a Date object for the date string in the element.
     * @throws InvalidDateFormatException
     */
    public Date evaluateAsDate(String expression, Date defaultDate) {
        checkArgument(expression != null);

        String dateStr = evaluateAsString(expression);
        
        if (StringUtils.isNotBlank(dateStr)) {
            Date date = null;
            try {
                date = Iso8601DateUtil.parseIso8601Date(dateStr.trim());
            } catch (IllegalArgumentException e) {
                throw new InvalidDateFormatException("The date "+dateStr+" is not a valid date");
            }
            return date;
        }
    
        return defaultDate;
    }
    
    public List<Integer> evaluateAsIntegerList(String expression) throws XPathException {
        checkArgument(expression != null);

        return evaluate(expression, new ObjectMapper<Node, Integer>() {
            @Override
            public Integer map(Node from) throws ObjectMappingException {
                String textContent = from.getTextContent();
                
                return Integer.valueOf(textContent);
            }
        });
    }
    
    public List<Long> evaluateAsLongList(String expression) throws XPathException {
        checkArgument(expression != null);

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

        return null;
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
            for (Map.Entry<Object, Object> entry : getNamespaces().entrySet()) {
                namespaceContext.bindNamespaceUri((String) entry.getKey(), (String) entry.getValue());
            }
            xpath.setNamespaceContext(namespaceContext);
        }
        try {
            if (context instanceof SAXSource) {
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
    }

    public void setContext(Source context) {
        if (context instanceof StreamSource) {
            try {
                // parse into JDOM to allow reusability (because InputStream can't be read twice)
                StreamSource stream = (StreamSource) context;
                SAXBuilder builder = new SAXBuilder();
                org.jdom2.Document document;
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
        org.jdom2.Document document;
        
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
