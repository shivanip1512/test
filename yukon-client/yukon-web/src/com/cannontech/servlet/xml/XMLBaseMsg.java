package com.cannontech.servlet.xml;

import org.w3c.dom.Document;

/**
 * @author ryan
 *
 */
public abstract class XMLBaseMsg implements IXMLMsg
{
	//maps to a unique PAO id
	private String id = null;


	/**
	 * Base implementation for all XML messages
	 */
	public void serializeXML( Document doc )
	{
		if( doc == null )
			throw new IllegalArgumentException("XML document should not be null");	
	}

	/**
	 * @return
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param string
	 */
	public void setId(String string)
	{
		id = string;
	}

}