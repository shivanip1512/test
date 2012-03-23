/**
 * 
 */
package com.cannontech.selenium.core.input;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * This an API Level class that is defined to parse input data from XML file.
 * @author anuradha.uduwage
 *
 */
public class XMLDataFileDigester {

	private static final Logger log = Logger.getLogger(XMLDataFileDigester.class.getName());
	
	private Document document;
	
	/**
	 * Method will construct a Document Object from the given InputStream
	 * @param readIn The input stream to read the document.
	 */
	public XMLDataFileDigester(InputStream readIn) {
		SAXReader reader = new SAXReader();
		try {
			document = reader.read(readIn);
		}catch (DocumentException ex) {
			log.error("Error when attempting to parse input data file", ex);
		}
	}
	
	/**
	 * Parse parameters from the xml file and places them in a hashmap 
	 * to be used by the test.
	 * @param params HashMap
	 * @return HashMap with the parameters.
	 */
	public HashMap<String, Object> parseInput(HashMap<String, Object> params) {	
		Element rootElement = document.getRootElement();
		for(Iterator iter = rootElement.elementIterator("Parameter"); iter.hasNext();) {
			Element elem = (Element) iter.next();
			String paramName = elem.attributeValue("name");
			if(elem.elements().size() < 2) {
				String paramValue = elem.element("value").getText();
				log.info("Adding Input Param: " + paramName + " => " + paramValue);
				params.put(paramName, paramValue);
			}
			else {
				ArrayList<String> values = new ArrayList<String>();
				for(Iterator i = elem.elementIterator("value"); i.hasNext();) {
					values.add(((Element)i.next()).getText());
				}
				params.put(paramName, values.toArray(new String[0]));
			}
		}
		return params;
	}
}
