package com.cannontech.common.gui.unchanging;

/**
 * Insert the type's description here.
 * Creation date: (3/2/00 12:14:29 PM)
 * @author: 
 */
public class StringRangeDocument extends javax.swing.text.PlainDocument 
{
	private int maxCharCount = 0;
	private String currentValue = null;
/**
 * StringRangeDocument constructor comment.
 */
public StringRangeDocument() {
	super();
}
/**
 * StringRangeDocument constructor comment.
 */
public StringRangeDocument( int range ) 
{
	super();

	maxCharCount = range;
}
/**
 * StringRangeDocument constructor comment.
 * @param c javax.swing.text.AbstractDocument.Content
 */
protected StringRangeDocument(javax.swing.text.AbstractDocument.Content c) {
	super(c);
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/00 12:25:14 PM)
 * @param propesedValue java.lang.String
 */
public String checkInputValue(String propesedValue) 
{
	if( propesedValue.length() <= maxCharCount )
	{
		return propesedValue;
	}
	else
	{
		return propesedValue.substring( 0, maxCharCount );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/00 12:43:59 PM)
 * @return java.lang.String
 */
public String getValue() 
{
	return currentValue;
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

		currentValue = checkInputValue( newValue );
			
		if( offset < maxCharCount && newValue.length() < maxCharCount )
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

	currentValue = checkInputValue( newValue );
	super.remove( offset, length );
	
}
}
