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
    
public static String trimSpaces( String s ) 
{
   int len = s.length();
   int st = 0;
   char[] val = s.toCharArray();    /* avoid getfield opcode */

   while ((st < len) && (val[st] == ' ')) {
       st++;
   }
   while ((st < len) && (val[len - 1] == ' ')) {
       len--;
   }
   
   return ((st > 0) || (len < s.length())) ? s.substring(st, len) : s;
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

/**
 * Takes an array of strings and try to convert and put each element
 * into an array of ints. If anything goes wrong, a zero length int
 * array is returned.
 * 
 */
public static int[] toIntArray(String[] str)
{
	int[] intArr = new int[0];
	if( str != null )
	{
		try
		{		
			intArr = new int[str.length];
			for( int i = 0; i < str.length; i++ )
				intArr[i] = Integer.parseInt(str[i]);
		}
		catch( NumberFormatException nfe )
		{
			intArr = new int[0];
		}
	}

	return intArr;		
}
}