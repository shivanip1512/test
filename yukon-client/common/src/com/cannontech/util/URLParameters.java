package com.cannontech.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author rneuharth
 *
 * A class to hold URL parameters (the quer) and then to
 * print them out when needed.
 * The output generated is NOT encoded.
 */
public class URLParameters extends HashMap
{

	/**
	 * 
	 */
	public URLParameters()
	{
		super(16);
	}


	/**
	 * Give us a URL query string
	 */
	public String toString()
	{
		StringBuffer b = new StringBuffer();
		Iterator it = entrySet().iterator();
		int indx = 0;
		
		while( it.hasNext() )
		{
			Map.Entry me = (Map.Entry)it.next();
			b.append( (indx++ == 0 ? "?" : "&") );
			b.append( me.getKey().toString() + "=" + me.getValue().toString() );
		}
			
		return b.toString();
	}

}