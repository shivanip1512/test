package com.cannontech.common.gui.unchanging;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LongRangeDocument extends PlainDocument {

  long minimum, maximum;
  long currentValue = 0;

public LongRangeDocument()
{
	this.minimum = Long.MIN_VALUE;
	this.maximum = Long.MAX_VALUE;
}          
public LongRangeDocument(long minimum, long maximum) 
{
	this.minimum = minimum;
	this.maximum = maximum;
}          
public long checkInput(String proposedValue) throws NumberFormatException 
{
   long newValue = minimum;  //default to the smallest values

   if (proposedValue.length() > 0)
   {
		if( minimum < 0 && proposedValue.charAt(0) == '-' ) //handle negative sign
		{
			if( proposedValue.length() == 1 ) //only have the negative sign here
				newValue = 0;   //just act like the negative sign has a 0 value
			else
				newValue = Long.parseLong( proposedValue.substring(1) );
		}
		else
	   	newValue = Long.parseLong(proposedValue);
   }
   
   if ((minimum <= newValue) && (newValue <= maximum))
   {
	  return newValue;
   }
   else
   {
	  throw new NumberFormatException();
   }
}
  public long getValue() {
	return currentValue;
  }          
  public void insertString(int offset, String string, AttributeSet attributes)
	  throws BadLocationException {

	if (string == null) {
	  return;
	} else {
	  String newValue;
	  int length = getLength();
	  if (length == 0) {
		newValue = string;
	  } else {
		String currentContent = getText(0, length);
		StringBuffer currentBuffer = new StringBuffer(currentContent);
		currentBuffer.insert(offset, string);
		newValue = currentBuffer.toString();
	  }
	  try {
		currentValue = checkInput(newValue);
		super.insertString(offset, string, attributes);
	  } catch (Exception exception) {
		Toolkit.getDefaultToolkit().beep();
	  }
	}
  }        
  public void remove(int offset, int length) throws BadLocationException {
	int currentLength = getLength();
	String currentContent = getText(0, currentLength);
	String before = currentContent.substring(0, offset);
	String after = currentContent.substring(length+offset, currentLength);
	String newValue = before + after;
	try {
	  currentValue = checkInput(newValue);
	  super.remove(offset, length);
	} catch (Exception exception) {
	  Toolkit.getDefaultToolkit().beep();
	}
  }        
}
