/*
 * Created on Nov 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.schedule.script;

import java.util.HashMap;
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogger;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ScriptTemplate implements ScriptParameters
{
    //Map of Param<String> to VALUE<String>, param Strings are listed below
    private HashMap paramToValueMap = null;
    //Map of Param<String> to DESCRIPTION<String>, param Strings are listed below
    private HashMap paramToDescMap = null;
    

    /**
     * value is used...but MACS doesn't know how to react to an empty paramter:  Therefore, we must know its value...
     * @param template
     * @return
     */
	public static String getScriptCode(int template)
	{
	    if( template == ScriptTemplateTypes.METER_READ_SCRIPT
	            || template == ScriptTemplateTypes.IED_METER_READ_SCRIPT)
	        return buildMeterReadCode(template);
	    else if(template == ScriptTemplateTypes.METER_READ_RETRY_SCRIPT
	            || template == ScriptTemplateTypes.IED_METER_READ_RETRY_SCRIPT)
	        return buildIEDMeterReadRetryCode();
	    return "";
	}
	
    /**
     * Map of param <String (from ScriptParameters class)> to value<String>
     * @return Returns the paramToValueMap.
     */
    public HashMap getParamToValueMap()
    {
        if( paramToValueMap == null)
        {
            paramToValueMap = new HashMap(30);
            //give it some default values!
            paramToValueMap.put(SCHEDULE_NAME_PARAM, "");
            paramToValueMap.put(SCRIPT_FILE_NAME_PARAM, "");
            paramToValueMap.put(SCRIPT_DESC_PARAM, "");
            paramToValueMap.put(GROUP_NAME_PARAM, "");
            paramToValueMap.put(GROUP_TYPE_PARAM, "group");
            paramToValueMap.put(PORTER_TIMEOUT_PARAM, "1800");
            paramToValueMap.put(FILE_PATH_PARAM, "C:/yukon/server/export/");
            paramToValueMap.put(MISSED_FILE_NAME_PARAM, "Missed.txt");
            paramToValueMap.put(SUCCESS_FILE_NAME_PARAM, "Success.txt");
            //Notification parameters
            paramToValueMap.put(NOTIFICATION_FLAG_PARAM, "false");
            paramToValueMap.put(NOTIFY_GROUP_PARAM, "");
            paramToValueMap.put(EMAIL_SUBJECT_PARAM, "");
            //Retry (within a read script by collectiongroup, not a retry read of a list)
            paramToValueMap.put(READ_WITH_RETRY_FLAG_PARAM, "false");
            paramToValueMap.put(RETRY_COUNT_PARAM, "6");
            paramToValueMap.put(QUEUE_OFF_COUNT_PARAM, "3");
            paramToValueMap.put(MAX_RETRY_HOURS_PARAM, "-1");
            //Billing parameters
            paramToValueMap.put(BILLING_FLAG_PARAM, "false");
            paramToValueMap.put(BILLING_FILE_NAME_PARAM, "Billing.txt");
            paramToValueMap.put(BILLING_FILE_PATH_PARAM, "C:/yukon/server/export/");
            paramToValueMap.put(BILLING_FORMAT_PARAM, "none");
            paramToValueMap.put(BILLING_GROUP_TYPE_PARAM, "collect");
            paramToValueMap.put(BILLING_ENERGY_DAYS_PARAM, "7");
            paramToValueMap.put(BILLING_DEMAND_DAYS_PARAM, "30");
            //IED parameters
            paramToValueMap.put(IED_FLAG_PARAM, "false");
            paramToValueMap.put(TOU_RATE_PARAM, "rate C"); 
            paramToValueMap.put(RESET_COUNT_PARAM, "2");
            paramToValueMap.put(READ_FROZEN_PARAM, "");
        }
        return paramToValueMap;
    }
    
    /**
     * Map of param <String (from ScriptParameters class)> to description<String>
     * @return Returns the paramToValueMap.
     */
    public HashMap getParamToDescMap()
    {
        if( paramToDescMap == null)
        {
            paramToDescMap = new HashMap(30);
            //Set the values, a map is used cause they are easier to find this way!
			paramToDescMap.put(SCHEDULE_NAME_PARAM, "The name of the schedule.");
			paramToDescMap.put(SCRIPT_FILE_NAME_PARAM, "The name of the script file, extension .ctl.");
			paramToDescMap.put(SCRIPT_DESC_PARAM, "A description of the script.");
			paramToDescMap.put(GROUP_NAME_PARAM, "The name of the meter group to read.");
			paramToDescMap.put(GROUP_TYPE_PARAM, "The type of group.");
			paramToDescMap.put(PORTER_TIMEOUT_PARAM, "Number of seconds to wait for porter to try.");
			paramToDescMap.put(FILE_PATH_PARAM, "The directory path to write the result files to.");
			paramToDescMap.put(MISSED_FILE_NAME_PARAM, "Name of the file to write a missed meter read list.  If retry, this file must contain the list of meters to retry read.");
			paramToDescMap.put(SUCCESS_FILE_NAME_PARAM, "Name of the file to write a success meter read list.");
			//Notification descriptions
			paramToDescMap.put(NOTIFICATION_FLAG_PARAM, "When true (and respective parameters exist), a notification message will be sent.");
			paramToDescMap.put(NOTIFY_GROUP_PARAM, "The group to notfiy when script completes.");
			paramToDescMap.put(EMAIL_SUBJECT_PARAM, "The subject of the notification message.");
			//Retry (within a read script by collectiongroup, not a retry read of a list) Descriptions
			paramToDescMap.put(READ_WITH_RETRY_FLAG_PARAM, "When true (and respective parameters exist), the script will perform retry reads on a group. (Not valid on a list of meters)");
			paramToDescMap.put(RETRY_COUNT_PARAM, "Number of times to retry missed meter reads.");
			paramToDescMap.put(QUEUE_OFF_COUNT_PARAM, "Number of times to retry read before turning off queuing, must be less than Retry Count.");
			paramToDescMap.put(MAX_RETRY_HOURS_PARAM, "Max time (in hours) for retries to run (-1 = no limit).");
			//Billing descriptions
			paramToDescMap.put(BILLING_FLAG_PARAM, "When true (and respective parameters exist), a billing file will be generated.");
			paramToDescMap.put(BILLING_FILE_NAME_PARAM, "The name of the billing file.");
			paramToDescMap.put(BILLING_FILE_PATH_PARAM, "Directory to write the Billing File to.");
			paramToDescMap.put(BILLING_FORMAT_PARAM, "The format of the billing file.  If \"none\", no file will be generated.");
			paramToDescMap.put(BILLING_GROUP_TYPE_PARAM, "The type of group for billing generation.");
			paramToDescMap.put(BILLING_ENERGY_DAYS_PARAM, "The number of days previous to read energy.");
			paramToDescMap.put(BILLING_DEMAND_DAYS_PARAM, "The number of days previous to read demand.");
			//IED descriptions
			paramToDescMap.put(IED_FLAG_PARAM, "When true (and respective parameters exist), the meters read are IED type meters.");
			paramToDescMap.put(TOU_RATE_PARAM, "The Alpha/S4/KV TOU Rate to use."); 
			paramToDescMap.put(RESET_COUNT_PARAM, "The number of times to resend the demand reset command.");
			paramToDescMap.put(READ_FROZEN_PARAM, "MCTs with Alpha will move to read frozen register.");
        }
        return paramToDescMap;
    }

    
	private static String buildMeterReadCode(int template)
	{
	    String code = ENDLINE + COMMENT + START + MAIN_CODE + ENDLINE;
	    code += COMMENT + "Establish a time of day to quit by, default to one day max" + ENDLINE;
	    code += "if {$" + MAX_RETRY_HOURS_PARAM + " > 0 } {" + ENDLINE;
	    code += "    set RetriesDoneAt [calcTargetTimeInSecs [ expr $" + MAX_RETRY_HOURS_PARAM + " * 3600 ] ]" + ENDLINE;
	    code += "} else {" + ENDLINE;
	    code += "    set RetriesDoneAt [calcTargetTimeInSecs 86400 ]" + ENDLINE;
	    code += "}" + ENDLINE + ENDLINE;
	    
	    if( ScriptTemplateTypes.isIEDTemplate(template))
	    {
	        code += COMMENT + "Send Demand Resets (for IED meters)" + ENDLINE;
	        code += "while {$" + RESET_COUNT_PARAM + " > 0} {" + ENDLINE + ENDLINE;
	        code += "    set " + RESET_COUNT_PARAM + " [expr $"+ RESET_COUNT_PARAM + " - 1]" + ENDLINE + ENDLINE;
	        code += "    " + COMMENT + "Reset the Demand" + ENDLINE;
	        code += "    putvalue reset ied" + ENDLINE + ENDLINE;
	        code += "    " + COMMENT + "Move MCT with Alpha to read the frozen register" + ENDLINE;
	        code += "    eval $" + READ_FROZEN_PARAM + ENDLINE + ENDLINE;
	        code += "    " + COMMENT + "Wait 2 minutes and send the Reset again" + ENDLINE;
	        code += "    dout \"Wait 120 after Demand Reset\"" + ENDLINE;
	        code += "    wait 120" + ENDLINE;
	        code += "}" + ENDLINE + ENDLINE;
	    }
	    
	    code += COMMENT + "Read the Group (this is the First attempt)" + ENDLINE;
	    code += "select $" + GROUP_TYPE_PARAM + " $" + GROUP_NAME_PARAM + ENDLINE;
	    if( ScriptTemplateTypes.isIEDTemplate(template))
	        code += "getvalue ied kwh $" + TOU_RATE_PARAM + " update timeout $" + PORTER_TIMEOUT_PARAM + ENDLINE + ENDLINE;	        
	    else
	        code += "getvalue kwh update timeout $" + PORTER_TIMEOUT_PARAM + ENDLINE + ENDLINE;
	    
	    code += COMMENT + "Send out a log message" + ENDLINE;
	    code += "set GCount [llength $SuccessList]"+ ENDLINE;
	    code += "set Message [getMeterCountLogText $GCount [llength $MissedList]]"+ ENDLINE;
	    code += "dout $Message" + ENDLINE + ENDLINE;
	    
	    code += COMMENT + "Make some text for additional info, log info events with counts" + ENDLINE;
	    code += "set ExInfoText [getTryCountLogText -1 $" + SCHEDULE_NAME_PARAM + "]" + ENDLINE;
	    code += "LogEvent \"\" $Message $ExInfoText" + ENDLINE + ENDLINE;
	    
	    code += COMMENT + "Write the un/successfully read meters to respective files" + ENDLINE;
	    code += "saveListToNewFile $SuccessList $" + SUCCESS_FILE_NAME_PARAM + " $" + FILE_PATH_PARAM + ENDLINE;
	    code += "saveListToNewFile $MissedList $" + MISSED_FILE_NAME_PARAM + " $" + FILE_PATH_PARAM + ENDLINE + ENDLINE;
	    
	    code += COMMENT + "Main loop - break out when there are no more missed meters" + ENDLINE;
	    code += "while { $" + RETRY_COUNT_PARAM + " > 0 && [llength $MissedList] > 0} {" +ENDLINE + ENDLINE;
	    code += "    " + COMMENT + "Only check if there is a retry finish time" + ENDLINE;
	    code += "    if { [ expr [ clock seconds ] ] > $RetriesDoneAt } {" + ENDLINE;
	    code += "        dout \"Retry Expire time has exceeded, this will be the Last attempt\"" + ENDLINE;
	    code += "        set "+ RETRY_COUNT_PARAM + " 1 " + ENDLINE;
	    code += "    }" + ENDLINE + ENDLINE;
	    
	    code += "    dout \"Retry all the Missed Meters\"" + ENDLINE;
	    code += "    select list $MissedList" + ENDLINE + ENDLINE;
	    
	    code += "    if { $" + RETRY_COUNT_PARAM + " <= $" + QUEUE_OFF_COUNT_PARAM + " } {" + ENDLINE;
	    code += "        " + COMMENT + "Turn off queueing on the last few attempts" + ENDLINE;
	    code += "        dout \"Attempting read WITHOUT queueing turned on\"" + ENDLINE;
	    if( ScriptTemplateTypes.isIEDTemplate(template))
		    code += "        getvalue ied kwh $" + TOU_RATE_PARAM + " update timeout $" + PORTER_TIMEOUT_PARAM + " noqueue" + ENDLINE;
	    else
	        code += "        getvalue kwh update timeout $" + PORTER_TIMEOUT_PARAM + " noqueue" + ENDLINE;
	    code += "    } else {" + ENDLINE;
	    code += "        " + COMMENT + "Queueing is turned on" + ENDLINE;
	    code += "        dout \"Attempting read WITH queueing turned on\"" + ENDLINE;
	    if( ScriptTemplateTypes.isIEDTemplate(template))
		    code += "        getvalue ied kwh $" + TOU_RATE_PARAM + " update timeout $" + PORTER_TIMEOUT_PARAM + ENDLINE;
	    else
	        code += "        getvalue kwh update timeout $" + PORTER_TIMEOUT_PARAM + ENDLINE;
	    code += "    }" + ENDLINE + ENDLINE;
	    
	    code += "    " + COMMENT + "Append successfully read meters to list/file" + ENDLINE;
	    code += "    appendListToFile $SuccessList $" + SUCCESS_FILE_NAME_PARAM + " $" + FILE_PATH_PARAM + ENDLINE + ENDLINE;
	    code += "    " + COMMENT + "Write unsuccessfully read meters to new list/file" + ENDLINE;
	    code += "    saveListToNewFile $MissedList $" + MISSED_FILE_NAME_PARAM + " $" + FILE_PATH_PARAM + ENDLINE + ENDLINE;
	    
	    code += "    " + COMMENT + "Total the successfully read meters count" + ENDLINE;
	    code += "    set GCount [expr $GCount + [llength $SuccessList]]" + ENDLINE + ENDLINE;
	    
	    code += "    " + COMMENT + "Send out a log message" + ENDLINE;
	    code += "    set Message [getMeterCountLogText $GCount [llength $MissedList]]"+ ENDLINE;
	    code += "    dout $Message" + ENDLINE + ENDLINE;
	    
	    code += "    " + COMMENT + "Make some text for additional info, log info events with counts" + ENDLINE;
	    code += "    set ExInfoText [getTryCountLogText -1 $" + SCHEDULE_NAME_PARAM + "]" + ENDLINE;
	    code += "    LogEvent \"\" $Message $ExInfoText" + ENDLINE + ENDLINE;
	    
	    code += "    " + COMMENT + "Decrement Retry Count" + ENDLINE;
	    code += "    set " + RETRY_COUNT_PARAM + " [expr $" + RETRY_COUNT_PARAM + " - 1]" + ENDLINE;
	    code += "}" + ENDLINE;
	    code += COMMENT + "End Main Loop" + ENDLINE;
	    code += COMMENT + END + MAIN_CODE + ENDLINE;

	    return code;
	}
	
	public static String buildNotificationCode()
	{
	    String code = ENDLINE + COMMENT + START + NOTIFICATION + ENDLINE;
	    code += COMMENT + "Send email notification" + ENDLINE;
	    code += "set EMessage \"\\n\\n\"" + ENDLINE;
	    code += "append EMessage $Message" + ENDLINE;
	    code += "sendnotification $" + NOTIFY_GROUP_PARAM + " $" + EMAIL_SUBJECT_PARAM + " $" + " $EMessage" + ENDLINE;
	    code += COMMENT + END + NOTIFICATION + ENDLINE;
	    return code;
	}
	
	public static String buildBillingCode()
	{
	    String code = ENDLINE + COMMENT + START + BILLING + ENDLINE;
	    code += COMMENT + "Generate billing file" + ENDLINE;
	    code += "if { $" + BILLING_FORMAT_PARAM + " != \"none\" } {" + ENDLINE;
	    code += "    " + COMMENT + "Wait a bit to allow dispatch to complete" + ENDLINE;
	    code += "    wait 300" + ENDLINE + ENDLINE;
	    code += "    exportBillingFile $" + GROUP_NAME_PARAM + " $" + BILLING_GROUP_TYPE_PARAM + " $" + BILLING_FORMAT_PARAM;
	    code += " $" + BILLING_FILE_NAME_PARAM + " $" + BILLING_FILE_PATH_PARAM + " $" + BILLING_ENERGY_DAYS_PARAM + " $" + BILLING_DEMAND_DAYS_PARAM + ENDLINE;
	    code += "}" + ENDLINE;
	    code += COMMENT + END + BILLING + ENDLINE;
	    return code;
	}
	private static String buildMeterReadRetryCode(int template)
	{
	    String code = ENDLINE + COMMENT + START + MAIN_CODE + ENDLINE;
	    code += COMMENT + "Load the meter list from the file" + ENDLINE;
	    code += COMMENT + "Load the Missed (unsuccessful) meters to read" + ENDLINE;
	    code += "set Missed [loadListFromFile $" + MISSED_FILE_NAME_PARAM + " $" + FILE_PATH_PARAM + "]" + ENDLINE + ENDLINE;
	    
	    code += "set Message \"Meters to Retry: \"" + ENDLINE;
	    code += "append Message [llength $Missed]" + ENDLINE;
	    code += "dout $Message" + ENDLINE + ENDLINE;
	    
	    code += COMMENT + "Log an Info Event containing the Missed Meter count" + ENDLINE;
	    code += "LogEvent \"\" $Message $" + SCHEDULE_NAME_PARAM + ENDLINE + ENDLINE;
	    
	    code += COMMENT + "Read the List (only one attempt)" + ENDLINE;
	    code += "select list $Missed" + ENDLINE;
	    if( ScriptTemplateTypes.isIEDTemplate(template))
	        code += "getvalue ied kwh $" + TOU_RATE_PARAM + " update timeout $" + PORTER_TIMEOUT_PARAM + " noqueue" + ENDLINE + ENDLINE;
	    else
	        code += "getvalue kwh update timeout $" + PORTER_TIMEOUT_PARAM + " noqueue" + ENDLINE + ENDLINE;
	    
	    code += COMMENT + "Append the successfully read meters to the Success file" + ENDLINE;
	    code += "appendListToFile $SuccessList $" + SUCCESS_FILE_NAME_PARAM + " $" + FILE_PATH_PARAM + ENDLINE + ENDLINE;
	    
	    code += COMMENT + "Write the unsuccessfullly read meters to the Missed file" + ENDLINE;
	    code += "saveListToNewFile $MissedList $" + MISSED_FILE_NAME_PARAM + " $" + FILE_PATH_PARAM + ENDLINE + ENDLINE;
	    
	    code += COMMENT + "Send out a log message" + ENDLINE;
	    code += "set GCount [llength $SuccessList]"+ ENDLINE;
	    code += "set Message [getMeterCountLogText $GCount [llength $MissedList]]"+ ENDLINE;
	    code += "dout $Message" + ENDLINE + ENDLINE;
	    
	    code += COMMENT + "Log info events with counts" + ENDLINE;
	    code += "LogEvent \"\" $Message $" + SCHEDULE_NAME_PARAM + ENDLINE + ENDLINE;
	    return code;
	}
	private static String buildIEDMeterReadRetryCode()
	{
	    return "TODO";
	}

	public static String buildScriptHeaderCode()
	{
	    String code = COMMENT + START + HEADER + ENDLINE;
	    code += COMMENT + " ***********************************************************" + ENDLINE;
	    code += COMMENT + " *********     THIS IS AN AUTO-GENERATED SCRIPT    *********" + ENDLINE;
	    code += COMMENT + " ***  IT MAY BE EDITTED WITH A TEXT EDITOR *IF* CHANGES  ***" + ENDLINE;
	    code += COMMENT + " ***  ARE ONLY MADE BETWEEN THE START_xxx and END_xxx    ***" + ENDLINE;
	    code += COMMENT + " ***     TAGS, ALL OTHER CHANGES WILL BE IGNORED         ***" + ENDLINE;
	    code += COMMENT + " ***********************************************************" + ENDLINE;
	    code += COMMENT + " ***   EXCEPTION:  PARAMETER_LIST CODE BLOCK IS ALWAYS   ***" + ENDLINE;
	    code += COMMENT + " ***   REGENERATED, TO ADD NEW PARAMETERS, SET THEM IN   ***" + ENDLINE;
	    code += COMMENT + " ***   THE MAIN CODE BLOCK AND THEY WILL BE PRESERVED    ***" + ENDLINE;
	    code += COMMENT + " ***********************************************************" + ENDLINE;
	    code += COMMENT + " ***   IF EDITTING THIS SCRIPT, YOU ARE NOW PERSONALLY   ***" + ENDLINE;
	    code += COMMENT + " ***   RESPONSIBLE FOR IT'S CORRECTNESS AND RUNNABILITY  ***" + ENDLINE;
	    code += COMMENT + " ***********************************************************" + ENDLINE;	    
	    code += COMMENT + END + HEADER + ENDLINE;
	    return code;
	}

	public static String buildScriptFooterCode()
	{
	    String code = ENDLINE + COMMENT + START + FOOTER + ENDLINE;
	    code += "set Done \" Done\"" + ENDLINE;
	    code += "set Finished [concat $" + SCHEDULE_NAME_PARAM + " $Done]" + ENDLINE;
	    code += "dout $Finished" + ENDLINE;
	    code += COMMENT + END + FOOTER + ENDLINE;
	    return code;
	}
	public static String getScriptSection(String text, String sectionTag)
	{
	    String startTag = START + sectionTag;
	    String endTag = END + sectionTag;
	    if( text != null)
	    {
	        int startIndex = text.indexOf(startTag)-1;//get that COMMENT char too ( -1)!
	        int endIndex = text.lastIndexOf(endTag) + endTag.length();
	        if (startIndex >= 0 && endIndex > startIndex)
	            return ENDLINE + text.substring(startIndex, endIndex) + ENDLINE;
	    }
	    return null;
	}
	

	public void loadParamsFromScript(String script)
	{
	    if( script != null)
	    {
			StringTokenizer token = new StringTokenizer(script);
			String tokenStr = "";
			while(token.hasMoreTokens())
			{
				tokenStr = token.nextToken(" \t\n\r\f").trim();
				tokenStr = tokenStr.replaceFirst("#", "");	//remove any comment chars from the string!
				if (getParamToValueMap().get(tokenStr) != null && token.hasMoreTokens())
				{
				    //Get the token to the end of the line!
				    String value = token.nextToken("\n\r\f").trim();
				    //Sorry folks...we're removing all quotes...what are you using them in tcl strings for anyway?
				    value = value.replaceAll("\"", "");
					getParamToValueMap().put(tokenStr, value);
					CTILogger.info(tokenStr + ", " + value);
				}
			}
	    }
	}
	
	private String buildSetParameter(String param)
	{
	    String code = ENDLINE + COMMENT + getParamToDescMap().get(param) + ENDLINE;
	    code += SET + param + " \"" + getParamToValueMap().get(param) + "\"" + ENDLINE;
	    
	    return code;
	}
	private String buildDisplayOnlyParameter(String param)
	{
	    String code = ENDLINE + COMMENT + param + " \"" + getParamToValueMap().get(param) + "\"" + ENDLINE;
	    return code;
	}
	/**
	 * @return
	 */
	public String buildParameterScript()
	{
	    String paramList = ENDLINE + COMMENT + ScriptTemplate.START + ScriptTemplate.PARAMETER_LIST + ENDLINE;
	    paramList += buildDisplayOnlyParameter(SCRIPT_FILE_NAME_PARAM);
	    paramList += buildDisplayOnlyParameter(SCRIPT_DESC_PARAM);
	    paramList += buildSetParameter(SCHEDULE_NAME_PARAM);
	    paramList += buildSetParameter(GROUP_NAME_PARAM);
	    paramList += buildSetParameter(GROUP_TYPE_PARAM);
	    paramList += buildSetParameter(PORTER_TIMEOUT_PARAM);
	    
	    paramList += buildSetParameter(FILE_PATH_PARAM);
	    paramList += buildSetParameter(SUCCESS_FILE_NAME_PARAM);
	    paramList += buildSetParameter(MISSED_FILE_NAME_PARAM);
	
		if( Boolean.valueOf((String)getParamToValueMap().get(READ_WITH_RETRY_FLAG_PARAM)).booleanValue())
		{
			paramList += buildDisplayOnlyParameter(READ_WITH_RETRY_FLAG_PARAM);
		    paramList += buildSetParameter(RETRY_COUNT_PARAM);
		    paramList += buildSetParameter(QUEUE_OFF_COUNT_PARAM);
		    paramList += buildSetParameter(MAX_RETRY_HOURS_PARAM);
		}
		
	    if( Boolean.valueOf((String)getParamToValueMap().get(NOTIFICATION_FLAG_PARAM)).booleanValue())
	    {
	    	paramList += buildDisplayOnlyParameter(NOTIFICATION_FLAG_PARAM); 
		    paramList += buildSetParameter(NOTIFY_GROUP_PARAM);
		    paramList += buildSetParameter(EMAIL_SUBJECT_PARAM);
	    }
	    
	    if ( Boolean.valueOf((String)getParamToValueMap().get(BILLING_FLAG_PARAM)).booleanValue())
	    {
	    	paramList += buildDisplayOnlyParameter(BILLING_FLAG_PARAM);
		    paramList += buildSetParameter(BILLING_FILE_NAME_PARAM);
		    paramList += buildSetParameter(BILLING_FILE_PATH_PARAM);
		    paramList += buildSetParameter(BILLING_GROUP_TYPE_PARAM);
		    paramList += buildSetParameter(BILLING_FORMAT_PARAM);
		    paramList += buildSetParameter(BILLING_ENERGY_DAYS_PARAM);
		    paramList += buildSetParameter(BILLING_DEMAND_DAYS_PARAM);
	    }
	
	    if( Boolean.valueOf((String)getParamToValueMap().get(IED_FLAG_PARAM)).booleanValue())
	    {
	    	paramList += buildDisplayOnlyParameter(IED_FLAG_PARAM);
		    paramList += buildSetParameter(TOU_RATE_PARAM);
		    paramList += buildSetParameter(RESET_COUNT_PARAM);
		    paramList += buildSetParameter(READ_FROZEN_PARAM);
	    }
	    paramList += COMMENT + ScriptTemplate.END + ScriptTemplate.PARAMETER_LIST + ENDLINE;
	    return paramList;
	}	
}