package com.cannontech.common.util.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joda.time.LocalTime;
import org.springframework.core.io.Resource;

import com.cannontech.common.util.Iso8601DateUtil;

public class XmlUtils {
    private final static XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());

    public static List<Integer> convertToIntegerList(List<?> selectNodes) {
        List<Integer> result = new ArrayList<Integer>(selectNodes.size());
        for (Object object : selectNodes) {
            String strValue;
            if (object instanceof Element) {
                Element element = (Element) object;
                strValue = element.getTextTrim();
            } else if (object instanceof Text) {
                Text text = (Text) object;
                strValue = text.getTextTrim();
            } else {
                throw new IllegalArgumentException("Node type must be Element or Text, found: " + object.getClass());
            }
            Integer longValue = Integer.valueOf(strValue);
            result.add(longValue);
        }
        return result;
    }
    
    public static Element createStringElement(String name, Namespace namespace, String value) {
        Element element = new Element(name, namespace);
        
        element.setText(value);
        
        return element;
    }
    
    public static Element createIntegerElement(String name, Namespace namespace, int value) {
        Element element = new Element(name, namespace);
        
        element.setText(Integer.toString(value));
        
        return element;
    }

    public static Element createLongElement(String name, Namespace namespace, long value) {
        Element element = new Element(name, namespace);
        
        element.setText(Long.toString(value));
        
        return element;
    }
    
    /**
     * Creates an element containing a date in ISO format with a UTC zone.
     * Ex: 2008-10-13T12:30:00Z
     */
    public static Element createDateElement(String name, Namespace namespace, Date value) {
        Element element = new Element(name, namespace);
        
        String dateStr = Iso8601DateUtil.formatIso8601Date(value);
        
        element.setText(dateStr);
        
        return element;
    }

    /**
     * Creates an element containing a local time.
     * Ex: 14:25
     */
    public static Element createLocalTimeElement(String name, Namespace namespace, LocalTime value) {
        Element element = new Element(name, namespace);
        
        String localTimeStr = YukonXPathTemplate.PERIOD_START_TIME_FORMATTER.print(value);
        element.setText(localTimeStr);
        
        return element;
    }
    
    public static Element createDoubleElement(String name, Namespace namespace, double value) {
        Element element = new Element(name, namespace);
        
        String string = Double.toString(value);
        string = string.replace("Infinity", "INF");
        
        element.setText(string);
        
        return element;
    }
    
    public static Element createBooleanElement(String name, Namespace namespace, boolean value) {
        Element element = new Element(name, namespace);
        
        if (value) {
        	element.setText("true");
        } else {
        	element.setText("false");
        }
        
        return element;
    }
    
    public static Element createElementFromResource(Resource resource) throws JDOMException, IOException {
        
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(resource.getInputStream());
        Element inputElement = document.getRootElement();
        return inputElement;
    }
    
    public static void printElement(Element element, String title) throws IOException {
        
        if (!StringUtils.isBlank(title)) {
            System.out.println(title + ":");
        }
        xmlOutputter.output(element, System.out);
        System.out.println();
        System.out.println();
    }
    
    private static TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public static void transform(Source source, Result result) throws TransformerException {
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);
    }

    /**
     * Translate the supplied enumString into the enum supplied
     * 
     * @throws IllegalArgumentException the enumString is not a valid representation of the enum class.
     */
    public static <E extends Enum<E>> E  toEnum(String enumString, Class<E> enumClass) throws IllegalArgumentException {
        // Try getting the value from the enum
        try {
            String stringValue = convertReadableToEnum(enumString);
            return Enum.valueOf(enumClass, stringValue);
        } catch (IllegalArgumentException e) {
            // See if there is an xmlRepresentation of this value.
            E enumValue = findEnumFromXmlRepresentation(enumString, enumClass);
            if (enumValue != null) {
                return enumValue;
            }
        }

        throw new IllegalArgumentException(enumString + " is not a legal representation of " + enumClass);
    }

    /**
     * Translate the supplied enumString into the enum supplied
     */
    public static <E extends Enum<E>> String toXmlRepresentation(E enumValue) {
        
        // See if there is an xmlRepresentation of this value.
        String enumStr = findXmlRepresentation(enumValue);
        if (enumStr != null) {
            return enumStr;
        }
        
        // Try getting the value from the enum
        return convertEnumToReadable(enumValue);
    }
    
    /**
     * Search for the xmlRepresentation in the supplied enumClass.  If it finds the xmlRepresentation it will
     * return the enum associated with it.  If not it will return null.
     */
    private static <E extends Enum<E>> E findEnumFromXmlRepresentation(String xmlRepresentation, Class<E> enumClass) {
        Validate.notNull(enumClass);
        Validate.notEmpty(xmlRepresentation);
        
        Field[] enumValues = enumClass.getFields();
        for (Field enumValue : enumValues) {
            XmlRepresentation annotation = enumValue.getAnnotation(XmlRepresentation.class);
            if (annotation != null && annotation.value().equals(xmlRepresentation)) {
                try {
                    Object type = enumValue.get(enumClass);
                    return enumClass.cast(type);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // Because of the way were getting these variables there is absolutely no way for these exceptions to be thrown.
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Return the XmlRepresentation of the enum value supplied.
     */
    private static <E extends Enum<E>> String findXmlRepresentation(E enumObj) {
        Validate.notNull(enumObj);
        
        XmlRepresentation xmlRepresentation = null;
        try {
            xmlRepresentation = enumObj.getClass().getField(enumObj.name()).getAnnotation(XmlRepresentation.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException("Because of the way were getting these variables there is absolutely no way for these exceptions to be thrown", e);
        }

        if (xmlRepresentation == null) {
            return null;
        }
        return xmlRepresentation.value();
    }
    
    /**
     * Converts the supplied enum from our standard enum format to a more user friendly format. <br><br>
     * ABC_DEF_GHI > Abc Def Ghi <br>
     * WEEKDAY_WEEKEND > Weekday Weekend
     */
    private static <E extends Enum<E>> String convertEnumToReadable(E enumObject) {
        Validate.notNull(enumObject);
        
        String enumString = enumObject.name();
        return WordUtils.capitalizeFully(enumString.replace('_', ' '));
    }
    
    /**
     * Converts the supplied enum string into our standard enum format <br><br>
     * Abc Def Ghi > ABC_DEF_GHI <br>
     * Weekday Weekend > WEEKDAY_WEEKEND
     */
    private static String convertReadableToEnum(String enumString) {
        Validate.notNull(enumString);
        
        return enumString.toUpperCase().replaceAll(" ", "_");
    }
    
    /**
     * Returns formats xml string to display nicely in a log file.
     */
    public static String getPrettyXml(String xmlData, int indent) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);

            Source xmlInput = new StreamSource(new StringReader(xmlData));
            transformer.transform(xmlInput, xmlOutput);

            return System.getProperty("line.separator") + xmlOutput.getWriter().toString();
        } catch (Exception e) {
            //ignore
            return "";
        }
    }
    
    /**
     * Converts jaxb class to xml string, returns formated xml string to display nicely in a log file.
     */
    public static String getPrettyXml(Object response) {
        try {
            JAXBContext jc = JAXBContext.newInstance(response.getClass());
            Marshaller marshaller = jc.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(response, stringWriter);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            return getPrettyXml(stringWriter.toString(), 2);
        } catch (Exception e) {
            // ignore
            return "";
        }
    }
}
