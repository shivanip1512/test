package com.cannontech.common.util;

/**
 * Insert the type's description here.
 * Creation date: (3/28/2002 10:59:49 AM)
 * @author: 
 */
public final class StringUtils {
/**
 * StringUtils constructor comment.
 */
private StringUtils() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 11:00:46 AM)
 * @return java.lang.String
 * @param deletedChar char
 */
public static String addCharBetweenWords(char addedChar, String str )
{
	if( str == null )
		return null;

	StringBuffer b = new StringBuffer(str);

	for( int i = 0; i < b.length(); i++ )
		if( Character.isUpperCase(b.charAt(i)) )
			b.insert( i++, addedChar );

	return b.toString().trim();
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 11:00:46 AM)
 * @return java.lang.String
 * @param deletedChar char
 */
public static String removeChars(char deletedChar, String str )
{
	if( str == null )
		return null;

	StringBuffer b = new StringBuffer(str);

	for( int indx = b.toString().indexOf(deletedChar); indx >= 0; indx = b.toString().indexOf(deletedChar) )
		b.deleteCharAt( indx );

	return b.toString();
}
}
