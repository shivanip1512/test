package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (3/2/00 12:14:29 PM)
 * @author: 
 */
public class TextFieldDocument extends javax.swing.text.PlainDocument 
{
	private int maxCharCount = 0;
	private String currentValue = null;
	public static int MAX_DEVICE_NAME_LENGTH = 60;
	public static int MAX_IED_PASSWORD_LENGTH = 20;
	public static int MAX_INITIALIZATION_STRING_LENGTH = 50;
	public static int MAX_MCT_PASSWORD_LENGTH = 6;
	public static int MAX_METER_NUMBER_LENGTH = 15;
	public static int MAX_PAGER_NUMBER_LENGTH = 20;
	public static int MAX_PASSWORD_LENGTH = 20;
	public static int MAX_PHONE_NUMBER_LENGTH = 40;
	public static int MAX_POINT_LIMIT_NAME_LENGTH = 14;
	public static int MAX_POINT_NAME_LENGTH = 40;
	public static int MAX_PORT_DESCRIPTION_LENGTH = 20;
	public static int MAX_PREFIX_NUMBER_LENGTH = 10;
	public static int MAX_ROUTE_NAME_LENGTH = 40;
	public static int MAX_STATE_GROUP_NAME_LENGTH = 20;
	public static int MAX_STATE_NAME_LENGTH = 20;
	public static int MAX_SUFFIX_NUMBER_LENGTH = 10;
	public static int MAX_CAP_BANK_ADDRESS_LENGTH = 40;
	public static int MAX_CAP_SUBBUS_NAME_LENGTH = 30;
	public static int MAX_CAP_GEO_NAME_LENGTH = 30;

	// some simple int defined lengths
	public static int STRING_LENGTH_5 = 5;
	public static int STRING_LENGTH_10 = 10;
	public static int STRING_LENGTH_15 = 15;
	public static int STRING_LENGTH_20 = 20;
	public static int STRING_LENGTH_30 = 30;
	public static int STRING_LENGTH_40 = 40;
	public static int STRING_LENGTH_50 = 50;
	public static int STRING_LENGTH_60 = 60;
	public static int STRING_LENGTH_70 = 70;
	public static int STRING_LENGTH_80 = 80;
	public static int STRING_LENGTH_90 = 90;
	public static int STRING_LENGTH_100 = 100;
	
	private char[] invalidChars = null;


	public static final char[] INVALID_CHARS_PAO =
	{
		'\'',
		',',
		'|',
		'"'	
	};


	
/**
 * StringRangeDocument constructor comment.
 */
public TextFieldDocument() {
	super();
}
/**
 * StringRangeDocument constructor comment.
 */
public TextFieldDocument( int maxLength ) 
{
	super();

	maxCharCount = maxLength;
}

/**
 * StringRangeDocument constructor comment.
 */
public TextFieldDocument( int maxLength, char[] nonvalidChars ) 
{
	this( maxLength );
	
	invalidChars = nonvalidChars;
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/00 12:25:14 PM)
 * @param propesedValue java.lang.String
 */
public boolean checkInputValue(String proposedValue) 
{
	if( proposedValue.length() <= maxCharCount
		 && isValidString(proposedValue) )
	{
		return true;
	}
	else
		return false;
}


private boolean isValidString(String val)
{
	if( invalidChars != null )
	{
		for( int i = 0; i < invalidChars.length; i++ )
		{
			if( val.indexOf(invalidChars[i]) != -1 )
				return false;
		}
	}

	
	return true;
}

/**
 * Insert the method's description here.
 * Creation date: (3/2/00 12:18:12 PM)
 * @param offset int
 * @param string java.lang.String
 * @param attributes javax.swing.text.AttributeSet
 * @exception javax.swing.text.BadLocationException The exception description.
 */
public void insertString(int offset, String string, javax.swing.text.AttributeSet attributes) throws javax.swing.text.BadLocationException 
{
	if( string == null )
		return;
	else
	{
		String newValue;
		int length = getLength();

		if( length == 0 )
		{
			newValue = string;
		}
		else
		{
			String currentContent = getText( 0, length );
			StringBuffer currentBuffer = new StringBuffer( currentContent );

			currentBuffer.insert( offset, string );
			newValue = currentBuffer.toString();
		}

		if( checkInputValue( newValue ) )
			super.insertString( offset, string, attributes );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/00 12:19:34 PM)
 * @param offset int
 * @param length int
 * @exception javax.swing.text.BadLocationException The exception description.
 */
public void remove(int offset, int length) throws javax.swing.text.BadLocationException 
{
	int currentLength = getLength();
	String currentContent = getText( 0, currentLength );
	String before = currentContent.substring( 0, offset );
	String after = currentContent.substring( length + offset, currentLength );
	String newValue = before + after;

	super.remove( offset, length );
	
}
}
