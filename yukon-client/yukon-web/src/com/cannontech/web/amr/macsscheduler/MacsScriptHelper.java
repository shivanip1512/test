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
import static com.cannontech.database.data.schedule.script.ScriptParameters.SCHEDULE_NAME_PARAM;

import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;

import com.cannontech.amr.macsscheduler.model.MacsSchedule;
import com.cannontech.amr.macsscheduler.model.MacsScriptOptions;
import com.cannontech.amr.macsscheduler.model.MacsScriptTemplate;
import com.cannontech.database.data.schedule.script.ScriptTemplate;
import com.cannontech.yukon.IDatabaseCache;

public class MacsScriptHelper {

    /**
     * Creates script file from schedule.
     * schedule.scriptOptions.scriptText contains script file created.
     */
    public static void generateScript(MacsSchedule schedule, IDatabaseCache databaseCache) {
        MacsScriptOptions options = schedule.getScriptOptions();
        MacsScriptTemplate template = schedule.getTemplate();
        if(template.isNoTemplateSelected()){
            return;
        }

        ScriptTemplate scriptTemplate = loadScriptTemplateFromOptions(schedule, databaseCache);
        
        StringBuilder script = new StringBuilder();
        script.append(ScriptTemplate.buildScriptHeaderCode());
        script.append(scriptTemplate.buildParameterScript());
   
        String tempText = ScriptTemplate.getScriptSection(options.getScriptText(), ScriptTemplate.MAIN_CODE, false);
        if (tempText == null) {
            script.append(ScriptTemplate.getScriptCode(template.getId()));
        } else {
            script.append(tempText);
        }
        if (options.isNotificationSelected()) {
            script.append(ScriptTemplate.getScriptSection(options.getScriptText(), ScriptTemplate.NOTIFICATION, true));
        }
        if (options.isBillingSelected()) {
            script.append(ScriptTemplate.getScriptSection(options.getScriptText(), ScriptTemplate.BILLING, true));
        }
        script.append(ScriptTemplate.getScriptSection(options.getScriptText(), ScriptTemplate.FOOTER, true));
        options.setScriptText(script.toString());
    }
    
    /**
     * Loads script option from script file.
     * schedule.scriptOptions.scriptText contains script file
     */
    public static void loadOptionsFromScriptFile(String script, MacsSchedule schedule, IDatabaseCache databaseCache) {
        MacsScriptOptions options = schedule.getScriptOptions();
        options.setScriptText(script);
        MacsScriptTemplate template = schedule.getTemplate();
        
        if(template.isNoTemplateSelected()){
            return;
        }
        
        ScriptTemplate scriptTemplate = new ScriptTemplate();
        scriptTemplate.loadParamsFromScript(ScriptTemplate.getScriptSection(options.getScriptText(), ScriptTemplate.PARAMETER_LIST, false));   
        loadOptionsFromScriptTemplate(schedule, scriptTemplate, databaseCache);
    }
    
    /**
     * Loads schedule with default script option.
     */
    public static void loadDefaultScriptOptions(MacsSchedule schedule) {
        MacsScriptOptions options = schedule.getScriptOptions();
        loadOptionsFromScriptTemplate(schedule, new ScriptTemplate(), null);
        options.setFileName(schedule.getScheduleName() + ".ctl");
    }
        
    /**
     * Populates script option with template values.
     */
    private static void loadOptionsFromScriptTemplate(MacsSchedule schedule, ScriptTemplate scriptTemplate, IDatabaseCache databaseCache) {

        MacsScriptOptions options = schedule.getScriptOptions();
        options.setFileName(scriptTemplate.getParameterValue(SCRIPT_FILE_NAME_PARAM));
        options.setDescription(scriptTemplate.getParameterValue(SCRIPT_DESC_PARAM));
        options.setFilePath(scriptTemplate.getParameterValue(FILE_PATH_PARAM));
        options.setMissedFileName(scriptTemplate.getParameterValue(MISSED_FILE_NAME_PARAM));
        options.setSuccessFileName(scriptTemplate.getParameterValue(SUCCESS_FILE_NAME_PARAM));
        options.setPorterTimeout(NumberUtils.toInt(scriptTemplate.getParameterValue(PORTER_TIMEOUT_PARAM), 0));
        options.setGroupName(scriptTemplate.getParameterValue(GROUP_NAME_PARAM));     
        options.setRetryCount(NumberUtils.toInt(scriptTemplate.getParameterValue(RETRY_COUNT_PARAM), 0));
        options.setMaxRetryHours(NumberUtils.toInt(scriptTemplate.getParameterValue(MAX_RETRY_HOURS_PARAM), 0));
        options.setQueueOffCount(NumberUtils.toInt(scriptTemplate.getParameterValue(QUEUE_OFF_COUNT_PARAM), 0));
            
        options.setBillingSelected(Boolean.valueOf(scriptTemplate.getParameterValue(BILLING_FLAG_PARAM)));
        options.setBillingFileName(scriptTemplate.getParameterValue(BILLING_FILE_NAME_PARAM));
        options.setBillingFilePath(scriptTemplate.getParameterValue(BILLING_FILE_PATH_PARAM));
        options.setBillingFormat(scriptTemplate.getParameterValue(BILLING_FORMAT_PARAM));
        options.setBillingEnergyDays(NumberUtils.toInt(scriptTemplate.getParameterValue(BILLING_ENERGY_DAYS_PARAM), 0));
        options.setBillingDemandDays(NumberUtils.toInt(scriptTemplate.getParameterValue(BILLING_DEMAND_DAYS_PARAM), 0));
        options.setBillingGroupName(scriptTemplate.getParameterValue(BILLING_GROUP_NAME_PARAM));

        options.setNotificationSelected(Boolean.valueOf(scriptTemplate.getParameterValue(NOTIFICATION_FLAG_PARAM)));
        options.setNotificationSubject(scriptTemplate.getParameterValue(EMAIL_SUBJECT_PARAM));    
        options.setScriptText(scriptTemplate.buildParameterScript());
        
        options.setDemandResetSelected(Boolean.valueOf(scriptTemplate.getParameterValue(NOTIFICATION_FLAG_PARAM)));

        options.setDemandResetRetryCount(Integer.valueOf(scriptTemplate.getParameterValue(RESET_COUNT_PARAM)));
        if(options.getDemandResetRetryCount() > 0) {
            options.setDemandResetSelected(true);
        }

        options.setTouRate(scriptTemplate.getParameterValue(TOU_RATE_PARAM));
        options.setIedType(scriptTemplate.getParameterValue(IED_TYPE_PARAM));
        
        if (options.isNotificationSelected()) {
            String groupName = scriptTemplate.getParameterValue(NOTIFY_GROUP_PARAM);
            options.setNotificationGroupId(databaseCache.getAllContactNotificationGroups().stream().filter(
                g -> g.getNotificationGroupName().equals(groupName)).findFirst().get().getLiteID());
        }
    }
        
    /**
     * Adds parameters from schedule to script template.
     */
    private static ScriptTemplate loadScriptTemplateFromOptions(MacsSchedule schedule, IDatabaseCache databaseCache) {
        ScriptTemplate scriptTemplate = new ScriptTemplate();
        MacsScriptTemplate template = schedule.getTemplate();
        MacsScriptOptions options = schedule.getScriptOptions();
        scriptTemplate.setParameterValue(SCHEDULE_NAME_PARAM, schedule.getScheduleName());
        scriptTemplate.setParameterValue(SCRIPT_FILE_NAME_PARAM, Objects.toString(options.getFileName(), ""));
        scriptTemplate.setParameterValue(SCRIPT_DESC_PARAM, Objects.toString(options.getDescription(), ""));
        scriptTemplate.setParameterValue(GROUP_NAME_PARAM, Objects.toString(options.getGroupName(), ""));
        scriptTemplate.setParameterValue(PORTER_TIMEOUT_PARAM, options.getPorterTimeout());
        scriptTemplate.setParameterValue(FILE_PATH_PARAM, Objects.toString(options.getFilePath(), ""));
        scriptTemplate.setParameterValue(MISSED_FILE_NAME_PARAM, Objects.toString(options.getMissedFileName(), ""));
        scriptTemplate.setParameterValue(SUCCESS_FILE_NAME_PARAM, Objects.toString(options.getSuccessFileName(), ""));

        if (options.isBillingSelected()) {
            scriptTemplate.setParameterValue(BILLING_FLAG_PARAM, true);
            scriptTemplate.setParameterValue(BILLING_FILE_NAME_PARAM, Objects.toString(options.getBillingFileName(), ""));
            scriptTemplate.setParameterValue(BILLING_GROUP_NAME_PARAM, Objects.toString(options.getBillingGroupName(), ""));
            scriptTemplate.setParameterValue(BILLING_FILE_PATH_PARAM, Objects.toString(options.getBillingFilePath(), ""));
            scriptTemplate.setParameterValue(BILLING_FORMAT_PARAM, options.getBillingFormat());
            scriptTemplate.setParameterValue(BILLING_ENERGY_DAYS_PARAM, options.getBillingEnergyDays());
            scriptTemplate.setParameterValue(BILLING_DEMAND_DAYS_PARAM, options.getBillingDemandDays());
        }

        if (options.isNotificationSelected()) {
            scriptTemplate.setParameterValue(NOTIFICATION_FLAG_PARAM, true);
            scriptTemplate.setParameterValue(NOTIFY_GROUP_PARAM,
                databaseCache.getAllContactNotificationGroups().stream().filter(
                    g -> g.getLiteID() == options.getNotificationGroupId()).findFirst().get().getNotificationGroupName());
            scriptTemplate.setParameterValue(EMAIL_SUBJECT_PARAM,
                Objects.toString(options.getNotificationSubject(), ""));
        }

        if (!template.isRetry()) {
            scriptTemplate.setParameterValue(READ_WITH_RETRY_FLAG_PARAM, true);
            scriptTemplate.setParameterValue(RETRY_COUNT_PARAM, options.getRetryCount());
            scriptTemplate.setParameterValue(QUEUE_OFF_COUNT_PARAM, options.getQueueOffCount());
            scriptTemplate.setParameterValue(MAX_RETRY_HOURS_PARAM, options.getMaxRetryHours());
        }

        if (template.isIed()) {
            scriptTemplate.setParameterValue(IED_FLAG_PARAM, true);
            scriptTemplate.setParameterValue(TOU_RATE_PARAM, options.getTouRate());
            scriptTemplate.setParameterValue(RESET_COUNT_PARAM, options.getDemandResetRetryCount());
            scriptTemplate.setParameterValue(IED_TYPE_PARAM, Objects.toString(options.getIedType(), ""));
        }
        
        return scriptTemplate;
    }
}
