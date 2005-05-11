package com.cannontech.servlet.xml;

/**
 * @author ryan
 * 
 * Returns an XML message with the following form:
 * 
	<response>
	  <method>callBack</method>
	  .
	  {may contain many ResultXML elements here}
	  .
	</response>

 *
 */
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cannontech.clientutils.CTILogger;

public class DynamicUpdate
{
	/**
	 * Outputs XML results in a XML DOM as a string
	 * 
	 */
	public static final String createXML( String callBackMethod, ResultXML[] xmlMsgs )
	{
		try 
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();
			Document doc = impl.createDocument(null, "response", null);
			Element root = doc.getDocumentElement();
	
			Element methodElem = doc.createElement("method");
			methodElem.appendChild( doc.createTextNode(callBackMethod) );
			root.appendChild( methodElem );

			for( int i = 0; i < xmlMsgs.length; i++ )
			{
				xmlMsgs[i].serializeXML( doc );
			}
	
	
			//write the XML doc out to the respnse stream
			StringWriter writer = new StringWriter();
			OutputFormat format = new OutputFormat(doc);
			XMLSerializer s = new XMLSerializer( writer, format );
			s.serialize(doc);
			writer.flush();

			return writer.getBuffer().toString();
		}
		catch( Exception e )
		{
			CTILogger.error("Unable to create XML block", e );
			return "";
		}

	}

}