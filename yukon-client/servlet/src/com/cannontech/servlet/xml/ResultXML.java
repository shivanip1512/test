package com.cannontech.servlet.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A data structure representing a generic XML structure. Here is
 * an example of what it could look like:
 *
	<result>
	  <id>40</id> 
	  <state>DISABLED</state>
	  .
	  
	  .
	</result>
 *
 */
public class ResultXML extends XMLBaseMsg
{
	private String state = null;

	//some generic optional elements
	// returns items with an element name in the following format:
	//    param0, param1, param2, param3 ...
	private String[] optionalElems = new String[0];


	public ResultXML( String id_, String state_ )
	{
		this(id_, state_, new String[0] );
	}

	public ResultXML( String id_, String state_, String[] optionalElems_ )
	{
		super();
		
		setId( id_ );
		setState( state_ );

		setOptionalElems( optionalElems_ );		
	}

	public void serializeXML( Document doc )
	{
		//always call the supers implementation
		super.serializeXML( doc );
		

		Element root = doc.getDocumentElement();
		Element resultElem = doc.createElement("result");

		//all elements inside of the <result> tag
		Element idElem = doc.createElement("id");
		Element stateElem = doc.createElement("state");
	
		stateElem.appendChild( doc.createTextNode(getState()) );				
		idElem.appendChild( doc.createTextNode(getId()) );
		
	

		//add all elements here
		resultElem.appendChild( idElem );
		resultElem.appendChild( stateElem );


		//add all attributes here
		//example:
		//	stateElem.setAttribute( "name", getName() );


		//handle the addition of any optional elements that are found
		_handleOptionalElems( doc, resultElem );

		root.appendChild( resultElem );
	}

	private void _handleOptionalElems( Document doc, Element parent )
	{
		if( getOptionalElems() == null || doc == null || parent == null )
			return;

		for( int i = 0; i < getOptionalElems().length; i++ )
		{
			//format of name:  param0, param1, param2 ...
			Element optElem = doc.createElement("param" + i);
			optElem.appendChild( doc.createTextNode(getOptionalElems()[i]) );
			parent.appendChild( optElem );
		}
	}

	/**
	 * @return
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @param string
	 */
	public void setState(String string)
	{
		state = string;
	}

	/**
	 * @return
	 */
	public String[] getOptionalElems()
	{
		return optionalElems;
	}

	/**
	 * @param strings
	 */
	public void setOptionalElems(String[] strings)
	{
		optionalElems = strings;
	}

}
