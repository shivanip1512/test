package com.cannontech.web.amr.macsscheduler;

import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_DEMAND_DAYS_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_ENERGY_DAYS_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FILE_PATH_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FORMAT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_GROUP_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.EMAIL_SUBJECT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.FILE_PATH_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.GROUP_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.IED_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.IED_TYPE_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.MAX_RETRY_HOURS_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.MISSED_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.NOTIFICATION_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.NOTIFY_GROUP_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.PORTER_TIMEOUT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.QUEUE_OFF_COUNT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.READ_WITH_RETRY_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.RESET_COUNT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.RETRY_COUNT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SCRIPT_DESC_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SCRIPT_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SUCCESS_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.TOU_RATE_PARAM;

import com.cannontech.amr.macsscheduler.model.MacsScriptOptions;
import com.cannontech.amr.macsscheduler.model.MacsScriptTemplate;
import com.cannontech.database.data.schedule.script.ScriptTemplate;

public class MacsScriptHelper {

    public static String build(MacsScriptOptions options, MacsScriptTemplate template) {
        if(template.isNoTemplateSelected()){
            return "";
        }
                        
        ScriptTemplate scriptTemplate = new ScriptTemplate();
        scriptTemplate.setParameterValue(SCRIPT_FILE_NAME_PARAM, options.getFileName());
        scriptTemplate.setParameterValue(SCRIPT_DESC_PARAM, options.getDescription());
    
        scriptTemplate.setParameterValue(GROUP_NAME_PARAM, options.getGroup().getFullName());
        scriptTemplate.setParameterValue(PORTER_TIMEOUT_PARAM, options.getPorterTimeout());
        scriptTemplate.setParameterValue(FILE_PATH_PARAM, options.getFilePath());
        scriptTemplate.setParameterValue(MISSED_FILE_NAME_PARAM, options.getMissedFileName());
        scriptTemplate.setParameterValue(SUCCESS_FILE_NAME_PARAM, options.getSuccessFileName());
    
        if (options.hasBillingInfo()) {
            scriptTemplate.setParameterValue(BILLING_FLAG_PARAM, true);
            scriptTemplate.setParameterValue(BILLING_FILE_NAME_PARAM, options.getBillingFileName());
            scriptTemplate.setParameterValue(BILLING_FILE_PATH_PARAM, options.getBillingFilePath());
            scriptTemplate.setParameterValue(BILLING_FORMAT_PARAM, options.getBillingFromat());
            scriptTemplate.setParameterValue(BILLING_ENERGY_DAYS_PARAM, options.getBillingEnergyDays());
            scriptTemplate.setParameterValue(BILLING_DEMAND_DAYS_PARAM, options.getBillingDemandDays());
            scriptTemplate.setParameterValue(BILLING_GROUP_NAME_PARAM, options.getBillingGroup().getFullName());
        }

        if (options.hasNotificationInfo()) {
            scriptTemplate.setParameterValue(NOTIFICATION_FLAG_PARAM, true);
            scriptTemplate.setParameterValue(NOTIFY_GROUP_PARAM, options.getNotificationGroupName());
            scriptTemplate.setParameterValue(EMAIL_SUBJECT_PARAM, options.getNotificationSubject());
        }
        
        if(!template.isRetry()){
            scriptTemplate.setParameterValue(READ_WITH_RETRY_FLAG_PARAM, true);
            scriptTemplate.setParameterValue(RETRY_COUNT_PARAM, options.getRetryCount());
            scriptTemplate.setParameterValue(QUEUE_OFF_COUNT_PARAM, options.getQueueOffCount());
            scriptTemplate.setParameterValue(MAX_RETRY_HOURS_PARAM, options.getMaxRetryHours());
        }
       
        if (template.isIed()) {
            scriptTemplate.setParameterValue(IED_FLAG_PARAM, true);
            scriptTemplate.setParameterValue(TOU_RATE_PARAM, options.getTouRate());
            scriptTemplate.setParameterValue(RESET_COUNT_PARAM, options.getDemandResetRetryCount());
            if (template.isIed300()) {
                scriptTemplate.setParameterValue(IED_TYPE_PARAM, options.getFrozenDemandRegister());
            } else if (template.isIed400()) {
                scriptTemplate.setParameterValue(IED_TYPE_PARAM, options.getIedType());
            }
        }
        
        StringBuilder script = new StringBuilder();
        script.append(ScriptTemplate.buildScriptHeaderCode());
        script.append(scriptTemplate.buildParameterScript());
        
        return null;
    }
        

    public static void write(String fileName, String script) {
    }

    public static String read(String fileName) {
        return null;
    }

    public static void delete(String fileName) {
    }
}
