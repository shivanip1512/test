package com.cannontech.common.gui.unchanging;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class DoubleRangeDocument extends PlainDocument 
{
	int decimalPlaces = -1;
	double minimum, maximum;
	double currentValue = 0;

	//create and initialize our formatter
	public static final java.text.DecimalFormat DECIMAL_FORMATTER = new java.text.DecimalFormat();
	static
	{
		DECIMAL_FORMATTER.applyPattern("#########.########");
	}

  public DoubleRangeDocument(double minimum, double maximum) {
	this.minimum = minimum;
	this.maximum = maximum;
  }              
  public DoubleRangeDocument(double minimum, double maximum, int decPlaces ) {
	this.minimum = minimum;
	this.maximum = maximum;
	this.decimalPlaces = decPlaces;
  }                
public double checkInput(String proposedValue) throws NumberFormatException 
{
	double newValue = minimum;  //default to the smallest values
	
	if (proposedValue.length() > 0) 
	{
		if( minimum < 0.0 && proposedValue.charAt(0) == '-' ) //handle negative sign
		{
			if( proposedValue.length() == 1  //only have the negative sign here
				 || (proposedValue.length() == 2  //if the string starts with -.
				     && proposedValue.charAt(1) == '.') )
				newValue = 0.0;   //just act like the negative sign has a 0.0 value
			else
				newValue = Double.parseDouble( proposedValue.substring(1) );
		}
		else if( decimalPlaces > 0 && proposedValue.charAt(0) == '.') 
		{
			if( proposedValue.length() == 1 ) //allow the user to have the decimal point here
				newValue = 0.0;   //just act like the decimal point is a 0.0 value
			else
				newValue = Double.parseDouble(proposedValue);
		}
		else
	   	newValue = Double.parseDouble(proposedValue);
	}

	if( decimalPlaces >= 0 )
	{
		int location = -1;
		if( ( location = proposedValue.indexOf(".") ) != -1 )
		{
			if( proposedValue.substring( location, proposedValue.length() ).length() > (decimalPlaces + 1) )
				throw new NumberFormatException();
		}
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
  public double getValue() {
	return currentValue;
  }              
public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException 
{
	if (string == null) 
	{
		return;
	} 
	else 
	{
		String newValue;
		int length = getLength();
	  
		if (length == 0) 
		{
			newValue = string;
		} 
		else 
		{
			String currentContent = getText(0, length);
			StringBuffer currentBuffer = new StringBuffer(currentContent);
			currentBuffer.insert(offset, string);
			newValue = currentBuffer.toString();
		}

		try 
		{
			currentValue = checkInput(newValue);

			//if we have a double with the E notation
			if( string.indexOf("e") != -1 || string.indexOf("E") != -1 )
				super.insertString(offset, DECIMAL_FORMATTER.format(currentValue), attributes);
			else //just treat like a reguelar formatted double
				super.insertString(offset, string, attributes);
		} 
		catch (Exception exception) 
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
}      
public void remove(int offset, int length) throws BadLocationException 
{
	int currentLength = getLength();
	String currentContent = getText(0, currentLength);
	String before = currentContent.substring(0, offset);
	String after = currentContent.substring(length+offset, currentLength);
	String newValue = before + after;
	
	try 
	{
	  currentValue = checkInput(newValue);
	  super.remove(offset, length);
	} 
	catch (Exception exception) 
	{
	  Toolkit.getDefaultToolkit().beep();
	}
}      
}
