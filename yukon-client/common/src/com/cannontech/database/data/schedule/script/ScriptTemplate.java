/*
 * Created on Nov 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.schedule.script;

import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_DEMAND_DAYS_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_ENERGY_DAYS_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FILE_PATH_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FORMAT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_GROUP_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_GROUP_TYPE_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.EMAIL_SUBJECT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.FILE_PATH_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.GROUP_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.GROUP_TYPE_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.IED_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.IED_TYPE_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.MAX_RETRY_HOURS_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.MISSED_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.NOTIFICATION_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.NOTIFY_GROUP_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.PORTER_TIMEOUT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.QUEUE_OFF_COUNT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.READ_FROZEN_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.READ_WITH_RETRY_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.RESET_COUNT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.RETRY_COUNT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SCHEDULE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SCRIPT_DESC_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SCRIPT_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SUCCESS_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.TOU_RATE_PARAM;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.common.util.CtiUtilities;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ScriptTemplate {
    public static final String COMMENT = "#";
    public static final String ENDLINE = "\r\n";
    public static final String SET = "set ";

    //Strings for code block comments
    public static final String START = "START_";
    public static final String END = "END_";
    
    public static final String HEADER = "HEADER";
    public static final String PARAMETER_LIST = "PARAMETER_LIST";
    public static final String MAIN_CODE = "MAIN_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";
    public static final String BILLING = "BILLING";
    public static final String FOOTER = "FOOTER";
    
    //VERY CUSTOM, string command to Read Frozen 
    public static final String READ_FROZEN_ALPHA_COMMAND_STRING = "putconfig emetcon ied class 72 02";
    public static final String READ_FROZEN_S4_COMMAND_STRING = "putconfig emetcon ied class 0 1";
    
    // build safe filepath to yukon dir to use as default location for script/billing file
    // TODO Push the file path of the macs server to build this string. Hard coded until then.
    //String yukonPath = StringUtils.replace(CtiUtilities.getYukonBase(), "\\", "/");  
    private String exportBaseFilePath;

    //Map of Param<String> to VALUE<String>, param Strings are listed below
    private Map<ScriptParameters, String> paramToValueMap = null;
    //Map of Param<String> to DESCRIPTION<String>, param Strings are listed below
    private Map<ScriptParameters, String> paramToDescMap = null;
    
    public ScriptTemplate(){
        init();
    }
    
    private void init(){
        String yukonPath = "";
        String jwsYukonBase = System.getProperty("jnlp.yukon.server.base");
        if (jwsYukonBase != null) {
            yukonPath = jwsYukonBase;
        } else {
            String yukonBaseProperty = CtiUtilities.getYukonBase();
            if(yukonBaseProperty != null) {
                yukonPath = yukonBaseProperty;
            }else {
                yukonPath = "C:/Yukon";
            }
        }
        
        String path = StringUtils.replace(yukonPath, "\\", "/"); 
        String[] exportBaseFilePathParts = {path, "Server", "Export"};
        exportBaseFilePath = StringUtils.join(exportBaseFilePathParts, "/") + "/";
        
        paramToValueMap = new HashMap<ScriptParameters, String>(30);
        //give it some default values!
        paramToValueMap.put(SCHEDULE_NAME_PARAM, "");
        paramToValueMap.put(SCRIPT_FILE_NAME_PARAM, "");
        paramToValueMap.put(SCRIPT_DESC_PARAM, "");
        paramToValueMap.put(GROUP_NAME_PARAM, "");
        paramToValueMap.put(GROUP_TYPE_PARAM, "group"); // this isn't so much a group as a keyword now
        paramToValueMap.put(PORTER_TIMEOUT_PARAM, "1800");
        paramToValueMap.put(FILE_PATH_PARAM, exportBaseFilePath);
        paramToValueMap.put(MISSED_FILE_NAME_PARAM, "Missed.txt");
        paramToValueMap.put(SUCCESS_FILE_NAME_PARAM, "Success.txt");
        //Notification parameters
        paramToValueMap.put(NOTIFICATION_FLAG_PARAM, "false");
        paramToValueMap.put(NOTIFY_GROUP_PARAM, "");
        paramToValueMap.put(EMAIL_SUBJECT_PARAM, "");
        //Retry (within a read script by collectiongroup, not a retry read of a list)
        paramToValueMap.put(READ_WITH_RETRY_FLAG_PARAM, "false");
        paramToValueMap.put(RETRY_COUNT_PARAM, "3");
        paramToValueMap.put(QUEUE_OFF_COUNT_PARAM, "2");
        paramToValueMap.put(MAX_RETRY_HOURS_PARAM, "-1");
        //Billing parameters
        paramToValueMap.put(BILLING_FLAG_PARAM, "false");
        paramToValueMap.put(BILLING_FILE_NAME_PARAM, "Billing.txt");
        paramToValueMap.put(BILLING_FILE_PATH_PARAM, exportBaseFilePath);
        paramToValueMap.put(BILLING_FORMAT_PARAM, "none");
        paramToValueMap.put(BILLING_GROUP_NAME_PARAM, "");
        paramToValueMap.put(BILLING_GROUP_TYPE_PARAM, "group"); // this isn't so much a group as a keyword now
        paramToValueMap.put(BILLING_ENERGY_DAYS_PARAM, "7");
        paramToValueMap.put(BILLING_DEMAND_DAYS_PARAM, "30");
        //IED parameters
        paramToValueMap.put(IED_FLAG_PARAM, "false");
        paramToValueMap.put(TOU_RATE_PARAM, "rate A"); 
        paramToValueMap.put(RESET_COUNT_PARAM, "0");
        paramToValueMap.put(READ_FROZEN_PARAM, "");
        paramToValueMap.put(IED_TYPE_PARAM, "");
        
        paramToDescMap = new HashMap<ScriptParameters, String>(30);
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
        paramToDescMap.put(BILLING_GROUP_NAME_PARAM, "The name of the meter group to put in billing file.");
        paramToDescMap.put(BILLING_GROUP_TYPE_PARAM, "The type of group for billing generation.");
        paramToDescMap.put(BILLING_ENERGY_DAYS_PARAM, "The number of days previous to read energy.");
        paramToDescMap.put(BILLING_DEMAND_DAYS_PARAM, "The number of days previous to read demand.");
        //IED descriptions
        paramToDescMap.put(IED_FLAG_PARAM, "When true (and respective parameters exist), the meters read are IED type meters.");
        paramToDescMap.put(TOU_RATE_PARAM, "The Alpha/S4/KV TOU Rate to use."); 
        paramToDescMap.put(RESET_COUNT_PARAM, "The number of times to resend the demand reset command.");
        paramToDescMap.put(READ_FROZEN_PARAM, "MCTs with Alpha or S4 will move to read frozen register.");
        paramToDescMap.put(IED_TYPE_PARAM, "The IED type of meter (Alpha/S4/kV/kV2/Sentinel)");
    }
    
    /**
     * value is used...but MACS doesn't know how to react to an empty paramter:  Therefore, we must know its value...
     * @param template
     * @return
     */
	public static String getScriptCode(int template)
	{
        switch (template) {
        case ScriptTemplateTypes.METER_READ_SCRIPT:
            return buildScriptFromFiles("main_quit_by.tcl", 
                                        "main_read_group_1.tcl", 
                                        "main_send_log.tcl", 
                                        "main_read_group_2.tcl", 
                                        "main_end.tcl");
        case ScriptTemplateTypes.IED_360_370_METER_READ_SCRIPT:
            return buildScriptFromFiles("main_quit_by.tcl", 
                                        "main_set_demand_360_370.tcl", 
                                        "main_read_group_1_ied.tcl", 
                                        "main_send_log.tcl", 
                                        "main_read_group_2_ied.tcl", 
                                        "main_end.tcl");
        case ScriptTemplateTypes.IED_400_METER_READ_SCRIPT:
            return buildScriptFromFiles("main_quit_by.tcl", 
                                        "main_set_demand_400.tcl", 
                                        "main_read_group_1_ied.tcl", 
                                        "main_send_log.tcl", 
                                        "main_read_group_2_ied.tcl", 
                                        "main_end.tcl");
        case ScriptTemplateTypes.METER_READ_RETRY_SCRIPT:
            return buildScriptFromFiles("retry_start.tcl", 
                                        "retry_read.tcl", 
                                        "retry_end.tcl");
        case ScriptTemplateTypes.IED_360_370_METER_READ_RETRY_SCRIPT:
            return buildScriptFromFiles("retry_start.tcl", 
                                        "retry_read_ied.tcl", 
                                        "retry_end.tcl");
        case ScriptTemplateTypes.IED_400_METER_READ_RETRY_SCRIPT:
            return buildScriptFromFiles("retry_start.tcl", 
                                        "retry_read_ied.tcl", 
                                        "retry_end.tcl");
        case ScriptTemplateTypes.OUTAGE_METER_READ_SCRIPT:
            return buildScriptFromFiles("main_quit_by.tcl", 
                                        "main_read_outages_group_1.tcl", 
                                        "main_send_log.tcl", 
                                        "main_read_outages_group_2.tcl", 
                                        "main_end.tcl");
        case ScriptTemplateTypes.VOLTAGE_METER_READ_SCRIPT:
            return buildScriptFromFiles("main_quit_by.tcl", 
                                        "main_read_voltage_group_1.tcl", 
                                        "main_send_log.tcl", 
                                        "main_read_voltage_group_2.tcl", 
                                        "main_end.tcl");
        }
	    return "";
	}
    
    public static String buildScriptFromFiles(String... files) {
        try {
            StringBuilder result = new StringBuilder();
            for (String fileName : files) {
                InputStream resourceAsStream = ScriptTemplate.class.getResourceAsStream(fileName);
                Validate.notNull(resourceAsStream, "Couldn't find file: " + fileName);
                
                InputStreamReader reader = new InputStreamReader(resourceAsStream);
                try {
                    int byteCount = 0;
                    char[] buffer = new char[1024];
                    int bytesRead = -1;
                    while ((bytesRead = reader.read(buffer)) != -1) {
                        result.append(buffer, 0, bytesRead);
                        byteCount += bytesRead;
                    }
                }
                finally {
                    try {
                        reader.close();
                    }
                    catch (IOException ex) {
                        CTILogger.warn("Could not close Reader", ex);
                    }
                }
                
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to build script from files: " + Arrays.toString(files), e);
        }
    }
    
    public String getParameterValue(ScriptParameters parameter) {
        return paramToValueMap.get(parameter);
    }
    
    public void setParameterValue(ScriptParameters parameter, Object value) {
        String valueStr = value.toString();
        paramToValueMap.put(parameter, valueStr);
    }
	
    public String getParamaterDescription(ScriptParameters parameter) {
        return paramToDescMap.get(parameter);
    }
    
	
	public static String buildNotificationCode()
	{
	    return buildScriptFromFiles("notification.tcl");
	}
	
	public static String buildBillingCode()
	{
	    return buildScriptFromFiles("billing.tcl");
	}

	public static String buildScriptHeaderCode()
	{
	    return buildScriptFromFiles("header.tcl");
	}

	public static String buildScriptFooterCode()
	{
	    return buildScriptFromFiles("footer.tcl");
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
				try {
                    ScriptParameters parameter = ScriptParameters.lookup(tokenStr);
                    if (token.hasMoreTokens())
                    {
                        //Get the token to the end of the line!
                        String value = token.nextToken("\n\r\f").trim();
                        //Sorry folks...we're removing all quotes...what are you using them in tcl strings for anyway?
                        value = value.replaceAll("\"", "");
                    	paramToValueMap.put(parameter, value);
                    	CTILogger.debug(tokenStr + ", " + value);
                    }
                } catch (IllegalArgumentException e) {
                }
			}
            
            fixCollectionGroups(GROUP_NAME_PARAM, GROUP_TYPE_PARAM);
            fixCollectionGroups(BILLING_GROUP_NAME_PARAM, BILLING_GROUP_TYPE_PARAM);
	    }
	}

    private void fixCollectionGroups(ScriptParameters name, ScriptParameters type) {
        // we need to check the GroupName for the new vs old syntax
        String groupName = getParameterValue(name);
        if (!groupName.startsWith("/") && StringUtils.isNotBlank(groupName)) {
            // this is the old syntax, decode
            String groupTypeStr = getParameterValue(type);
            FixedDeviceGroups groupType = FixedDeviceGroups.COLLECTIONGROUP;
            if ("altgroup".equalsIgnoreCase(groupTypeStr)) {
                groupType = FixedDeviceGroups.TESTCOLLECTIONGROUP;
            } else if ("billgroup".equalsIgnoreCase(groupTypeStr)) {
                groupType = FixedDeviceGroups.BILLINGGROUP;
            }
            
            String newGroupName = groupType.getGroup(groupName);
            setParameterValue(name, newGroupName);
            setParameterValue(type, "group");
        }
    }
	
	private String buildSetParameter(ScriptParameters param)
	{
	    String code = ENDLINE + COMMENT + paramToDescMap.get(param) + ENDLINE;
	    code += SET + param + " \"" + paramToValueMap.get(param) + "\"" + ENDLINE;
	    
	    return code;
	}
    
	private String buildDisplayOnlyParameter(ScriptParameters param)
	{
	    String code = ENDLINE + COMMENT + param + " \"" + paramToValueMap.get(param) + "\"" + ENDLINE;
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
	
		if( Boolean.valueOf(paramToValueMap.get(READ_WITH_RETRY_FLAG_PARAM)))
		{
			paramList += buildDisplayOnlyParameter(READ_WITH_RETRY_FLAG_PARAM);
		    paramList += buildSetParameter(RETRY_COUNT_PARAM);
		    paramList += buildSetParameter(QUEUE_OFF_COUNT_PARAM);
		    paramList += buildSetParameter(MAX_RETRY_HOURS_PARAM);
		}
		
	    if( Boolean.valueOf(paramToValueMap.get(NOTIFICATION_FLAG_PARAM)))
	    {
	    	paramList += buildDisplayOnlyParameter(NOTIFICATION_FLAG_PARAM); 
		    paramList += buildSetParameter(NOTIFY_GROUP_PARAM);
		    paramList += buildSetParameter(EMAIL_SUBJECT_PARAM);
	    }
	    
	    if ( Boolean.valueOf(paramToValueMap.get(BILLING_FLAG_PARAM)))
	    {
	    	paramList += buildDisplayOnlyParameter(BILLING_FLAG_PARAM);
		    paramList += buildSetParameter(BILLING_FILE_NAME_PARAM);
		    paramList += buildSetParameter(BILLING_FILE_PATH_PARAM);
		    paramList += buildSetParameter(BILLING_GROUP_NAME_PARAM);
		    paramList += buildSetParameter(BILLING_GROUP_TYPE_PARAM);
		    paramList += buildSetParameter(BILLING_FORMAT_PARAM);
		    paramList += buildSetParameter(BILLING_ENERGY_DAYS_PARAM);
		    paramList += buildSetParameter(BILLING_DEMAND_DAYS_PARAM);
	    }
	
	    if( Boolean.valueOf(paramToValueMap.get(IED_FLAG_PARAM)))
	    {
	    	paramList += buildDisplayOnlyParameter(IED_FLAG_PARAM);
		    paramList += buildSetParameter(TOU_RATE_PARAM);
		    paramList += buildSetParameter(RESET_COUNT_PARAM);
            paramList += buildSetParameter(IED_TYPE_PARAM);
	    }
	    paramList += COMMENT + ScriptTemplate.END + ScriptTemplate.PARAMETER_LIST + ENDLINE;
	    return paramList;
	}	
}
