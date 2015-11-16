package com.cannontech.web.util;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * Accepts a string in the form of HH:mm and translates it into seconds for a
 * day.
 * 
 * @author ryan
 */
public class TimeConverter implements Converter {

	public TimeConverter() {
		super();
	}
	
	
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

		int hour = 0;
		int minute = 0;


		if( value != null && value.length() > 0 ) {
			try
			{
				int pos = -1;
		
				if( (pos = value.indexOf(":")) != -1  ) //found the :
				{
					hour = Integer.parseInt( value.substring(0, pos) );

					if( (pos+1) < value.length() )
						minute = Integer.parseInt( value.substring(pos+1, value.length()) );
				}
				else  //did not find :
				{
					switch( value.length() )
					{
						case 1: //9
							hour = Integer.parseInt( value );
							break;
					
						case 2: //23
							hour = Integer.parseInt( value );
							if( hour >= 24 )
							{
								hour = Integer.parseInt( value.substring(0, 1) );
								minute = Integer.parseInt( value.substring(1, 2) );
							}
							break;

						case 3: //930
							hour = Integer.parseInt( value.substring(0, 1) );
							minute = Integer.parseInt( value.substring(1, 3) );
							break;

						case 4: //0930
							hour = Integer.parseInt( value.substring(0, 2) );
							minute = Integer.parseInt( value.substring(2, 4) );
							break;				
					}

					throw new IllegalArgumentException();
				}
			
			}
			catch( Exception e ) {

				FacesMessage facesMsg = new FacesMessage();
				facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );		
				facesMsg.setDetail( "Unable to parse given time string, expected format is HH:mm" );
				throw new ConverterException(facesMsg);
			}	
	
		}

		return new Integer( (hour * 3600) + (minute * 60) );
	}


	public String getAsString(FacesContext ctx, UIComponent component, Object object) {

		if( !(object instanceof Integer) ) {
		
			FacesMessage facesMsg = new FacesMessage();
			facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );		
			facesMsg.setDetail( "Invalid time entry" );
			throw new ConverterException("Invalid time entry");
		} 
		
		Integer seconds = (Integer)object;
		int hours = seconds.intValue() / 3600;
		int minutes = (seconds.intValue() % 3600) / 60;

		String hrStr = String.valueOf(hours);
		String minStr = String.valueOf(minutes);
	
		if( minutes <= 9 )
			minStr = "0" + minutes;

		if( hours <= 9 )
			hrStr = "0" + hours;

		return hrStr + ":" + minStr;
	}

}
