package com.cannontech.common.gui.unchanging;

import javax.swing.text.*;
import java.awt.Toolkit;

public class TimeStringDocumentValidator extends PlainDocument 
{
	private boolean isValid = false;
public TimeStringDocumentValidator()
{
	super();
}          
/**
 * Insert the method's description here.
 * Creation date: (4/23/2001 4:33:44 PM)
 * @param value java.lang.String
 */
private void checkHourValue(String value) 
{
	int hour = Integer.parseInt( value );
	if( hour < 0 || hour > 23 )
		throw new NumberFormatException("Found the value hour < 0 or hour > 23 in " + this.getClass().getName() );
	
}
public boolean checkInput( String proposedValue ) throws NumberFormatException
{
   final StringBuffer buffer = new StringBuffer(proposedValue);

   try
   {
	   if( buffer.length() > 0 )
	   {
			int pos = -1;

			if( buffer.length() > 5 )
				return false;
			
			if( (pos = buffer.toString().indexOf(":")) != -1  )
				buffer.deleteCharAt( pos ); //remove the : before parsing

			try
			{
				//this ensures our : can not be in position 0
				Integer.parseInt( buffer.toString() );
			}
			catch( NumberFormatException ex )
			{
				//dont let this throw propogate!!
				return false;
			}
			
			if( pos == -1 ) //no : was found and we have at least 1 integer
			{					
				if( buffer.length() > 4 )
					return false;

				switch( buffer.length() )
				{
					case 1:
						checkHourValue( buffer.toString() );
						break;

					default:
						//try to do the best we can!!
						checkHourValue( buffer.substring( 0, (buffer.length()/2) ) );
						checkMinuteValue( buffer.substring( (buffer.length()/2), buffer.length()) );
							
						break;
				}

			}
			else if( pos >= 1 ) //have something like this->  9:30
			{
				if( pos == 1 && buffer.length() >= 4 )
					return false;
						
				checkHourValue( buffer.substring(0, pos) );

				if( pos < buffer.length() )
					checkMinuteValue( buffer.substring(pos, buffer.length()) );
			}
			

	   }

   }
   catch( NumberFormatException ex )
   {
		return false;
   }

   return true;
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/2001 4:33:44 PM)
 * @param value java.lang.String
 */
private void checkMinuteValue(String value) 
{
	int minute = Integer.parseInt( value );
	if( minute < 0 || minute > 59 )
		throw new NumberFormatException("Found the value minute < 0 or minute > 59 in " + this.getClass().getName() );
	
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
			if( checkInput( newValue ) )
			{
				if( newValue.length() == 1 
					 && newValue.toString().indexOf(":") == -1
					 && Integer.valueOf(newValue).intValue() > 2 )
				{
						string += ":";
				}
				else if( newValue.length() == 2 && newValue.toString().indexOf(":") == -1 )
				{
					try
					{
						checkHourValue(newValue);
						string += ":";
					}
					catch( NumberFormatException e )
					{}					
				}				
				 	
				super.insertString(offset, string, attributes);

				isValid = true;
			}

		}
		catch (Exception exception)
		{
			isValid = false;
			Toolkit.getDefaultToolkit().beep();
		}
   }
  
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2001 10:13:08 AM)
 * @return boolean
 */
public boolean isValid() {
	return isValid;
}
public void remove(int offset, int length) throws BadLocationException 
{
	int currentLength = getLength();

   String currentContent = getText(0, currentLength);
   String before = currentContent.substring(0, offset);
   String after = currentContent.substring(length + offset, currentLength);
   String newValue = before + after;

   try
   {
		checkInput( newValue );
		super.remove(offset, length);
   }
   catch (Exception exception)
   {
		Toolkit.getDefaultToolkit().beep();
   }

}
}
