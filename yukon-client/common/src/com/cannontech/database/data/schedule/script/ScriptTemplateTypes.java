/*
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.database.data.schedule.script;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ScriptTemplateTypes
{
    public static final String METER_READ_SCRIPT_STRING = "Meter Read"; 
	public static final String METER_READ_RETRY_SCRIPT_STRING = "Meter Read Retry";
	public static final String IED_METER_READ_SCRIPT_STRING = "IED Meter Read";
	public static final String IED_METER_READ_RETRY_SCRIPT_STRING = "IED Meter Read Retry";
	public static final String NO_TEMPLATE_SCRIPT_STRING = "No Template";
	
	public static final int NO_TEMPLATE_SCRIPT = 0;
	public static final int METER_READ_SCRIPT = 1; 
	public static final int METER_READ_RETRY_SCRIPT = 2;
	public static final int IED_METER_READ_SCRIPT = 3;
	public static final int IED_METER_READ_RETRY_SCRIPT = 4;
		
	public static final String[] SCRIPT_TEMPLATES = new String[]{
        NO_TEMPLATE_SCRIPT_STRING,
		METER_READ_SCRIPT_STRING,
		METER_READ_RETRY_SCRIPT_STRING,
		IED_METER_READ_SCRIPT_STRING,
		IED_METER_READ_RETRY_SCRIPT_STRING
	};
	
	public static int getScriptTemplateFromString(String string)
	{
		if( string.equalsIgnoreCase(METER_READ_SCRIPT_STRING))
			return METER_READ_SCRIPT;
		else if (string.equalsIgnoreCase(METER_READ_RETRY_SCRIPT_STRING))
			return METER_READ_RETRY_SCRIPT;
		else if( string.equalsIgnoreCase(IED_METER_READ_SCRIPT_STRING))
			return IED_METER_READ_SCRIPT;
		else if( string.equalsIgnoreCase(IED_METER_READ_RETRY_SCRIPT_STRING))
			return IED_METER_READ_RETRY_SCRIPT;
		else if( string.equalsIgnoreCase(NO_TEMPLATE_SCRIPT_STRING))
		    return NO_TEMPLATE_SCRIPT;
		else
			return METER_READ_SCRIPT;	//some default is better than no default?
	}
	
	public static String[] getAllScriptTemplates()
	{
		return SCRIPT_TEMPLATES;
	}
	
	public static String getScriptTemplateFromID(int template)
	{
		return getAllScriptTemplates()[template];
	}

	public static boolean isNoTemplate(int template)
	{
		return (template == NO_TEMPLATE_SCRIPT);
	}
	public static boolean isMeteringTemplate(int template)
	{
	    return (template == METER_READ_SCRIPT
				|| template == METER_READ_RETRY_SCRIPT 
				|| template == IED_METER_READ_SCRIPT
				|| template == IED_METER_READ_RETRY_SCRIPT);	    
	}	
	public static boolean isRetryTemplate(int template)
	{
		return (template == METER_READ_RETRY_SCRIPT
				|| template == IED_METER_READ_RETRY_SCRIPT);
	}
	
	public static boolean isIEDTemplate(int template)
	{
		return (template == IED_METER_READ_RETRY_SCRIPT
				|| template == IED_METER_READ_SCRIPT ); 
	}
	
	public static boolean hasBilling(int template)
	{
		return (template == METER_READ_SCRIPT 
				|| template == METER_READ_RETRY_SCRIPT
				|| template == IED_METER_READ_SCRIPT
				|| template == IED_METER_READ_RETRY_SCRIPT);
	}
	public static boolean hasNotification(int template)
	{
		return (template == METER_READ_SCRIPT 
				|| template == METER_READ_RETRY_SCRIPT
				|| template == IED_METER_READ_SCRIPT
				|| template == IED_METER_READ_RETRY_SCRIPT);
	}
}
