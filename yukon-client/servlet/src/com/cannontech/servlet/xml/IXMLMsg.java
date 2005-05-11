package com.cannontech.servlet.xml;

import org.w3c.dom.Document;

/**
 * @author ryan
 *
 */
public interface IXMLMsg
{
	/**
	 * Serializes an xml document.
	 * 
	 */
	public void serializeXML( Document doc );

}
