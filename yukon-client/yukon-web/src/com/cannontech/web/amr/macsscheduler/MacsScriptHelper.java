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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.cannontech.amr.macsscheduler.model.MacsSchedule;
import com.cannontech.amr.macsscheduler.model.MacsScriptOptions;
import com.cannontech.amr.macsscheduler.model.MacsScriptTemplate;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.database.data.schedule.script.ScriptParameters;
import com.cannontech.database.data.schedule.script.ScriptTemplate;
import com.google.common.base.Strings;

public class MacsScriptHelper {

    public static void loadFromInput(MacsSchedule schedule) {
        MacsScriptOptions options = schedule.getScriptOptions();
        MacsScriptTemplate template = schedule.getTemplate();
        if(template.isNoTemplateSelected()){
            return;
        }
        StringBuilder script = new StringBuilder();
        script.append(ScriptTemplate.buildScriptHeaderCode());
        script.append(getInputParameters(template, options));
        String tempText = ScriptTemplate.getScriptSection(options.getScriptText(), ScriptTemplate.MAIN_CODE, false);
        if (tempText == null) {
            tempText = ScriptTemplate.getScriptCode(template.getId());
        }
        script.append(ScriptTemplate.getScriptSection(options.getScriptText(), ScriptTemplate.NOTIFICATION, true));
        script.append(ScriptTemplate.getScriptSection(options.getScriptText(), ScriptTemplate.BILLING, true));
        script.append(ScriptTemplate.getScriptSection(options.getScriptText(), ScriptTemplate.FOOTER, true));
        options.setScriptText(script.toString());
    }
    
    private static DeviceGroup getGroup(ScriptTemplate scriptTemplate, ScriptParameters groupParam,
            DeviceGroupService deviceGroupService) {
        String groupName = scriptTemplate.getParameterValue(groupParam);
        if (StringUtils.isNotEmpty(groupName)) {
            return deviceGroupService.resolveGroupName(groupName);
        }
        return null;
    }
    
    public static void loadFromFile(MacsSchedule schedule, DeviceGroupService deviceGroupService) {

        MacsScriptOptions options = schedule.getScriptOptions();
        MacsScriptTemplate template = schedule.getTemplate();
        
        if(template.isNoTemplateSelected()){
            return;
        }
        
        ScriptTemplate scriptTemplate = new ScriptTemplate();
  

        scriptTemplate.loadParamsFromScript(
            ScriptTemplate.getScriptSection(options.getScriptText(), ScriptTemplate.PARAMETER_LIST, false));

        loadValuesFromTemplate(schedule, scriptTemplate, deviceGroupService);
    }

    public static void loadDefaultValues(MacsSchedule schedule, DeviceGroupService deviceGroupService) {
        loadValuesFromTemplate(schedule, new ScriptTemplate(), deviceGroupService);
    }
    
    public static void loadValuesFromTemplate(MacsSchedule schedule, ScriptTemplate scriptTemplate,
            DeviceGroupService deviceGroupService) {
        scriptTemplate.setParameterValue(SCHEDULE_NAME_PARAM, schedule.getScheduleName());
        MacsScriptOptions options = schedule.getScriptOptions();
        String fileName = Strings.isNullOrEmpty(options.getFileName()) ? schedule.getScheduleName()+".ctl" : options.getFileName();
        scriptTemplate.setParameterValue(SCRIPT_FILE_NAME_PARAM, fileName);
        options.setDescription(scriptTemplate.getParameterValue(SCRIPT_DESC_PARAM));
        options.setFilePath(scriptTemplate.getParameterValue(FILE_PATH_PARAM));
        options.setMissedFileName(scriptTemplate.getParameterValue(MISSED_FILE_NAME_PARAM));
        options.setSuccessFileName(scriptTemplate.getParameterValue(SUCCESS_FILE_NAME_PARAM));
        options.setPorterTimeout(NumberUtils.toInt(scriptTemplate.getParameterValue(PORTER_TIMEOUT_PARAM), 0));
        
        scriptTemplate.setParameterValue(READ_WITH_RETRY_FLAG_PARAM, !schedule.getTemplate().isRetry());
        
        options.setRetryCount(NumberUtils.toInt(scriptTemplate.getParameterValue(RETRY_COUNT_PARAM), 0));
        options.setMaxRetryHours(NumberUtils.toInt(scriptTemplate.getParameterValue(MAX_RETRY_HOURS_PARAM), 0));
        options.setQueueOffCount(NumberUtils.toInt(scriptTemplate.getParameterValue(QUEUE_OFF_COUNT_PARAM), 0));
       
        options.setBillingSelected(Boolean.valueOf(scriptTemplate.getParameterValue(BILLING_FLAG_PARAM)));
        options.setBillingFileName(scriptTemplate.getParameterValue(BILLING_FILE_NAME_PARAM));
        options.setBillingFilePath(scriptTemplate.getParameterValue(BILLING_FILE_PATH_PARAM));
        options.setBillingFormat(scriptTemplate.getParameterValue(BILLING_FORMAT_PARAM));
        options.setBillingEnergyDays(NumberUtils.toInt(scriptTemplate.getParameterValue(BILLING_ENERGY_DAYS_PARAM), 0));
        options.setBillingDemandDays(NumberUtils.toInt(scriptTemplate.getParameterValue(BILLING_DEMAND_DAYS_PARAM), 0));
        options.setBillingGroup(getGroup(scriptTemplate, BILLING_GROUP_NAME_PARAM, deviceGroupService));

        options.setNotificationSelected(Boolean.valueOf(scriptTemplate.getParameterValue(NOTIFICATION_FLAG_PARAM)));
        options.setNotificationGroupName(scriptTemplate.getParameterValue(NOTIFY_GROUP_PARAM));
        options.setNotificationSubject(scriptTemplate.getParameterValue(EMAIL_SUBJECT_PARAM));    
        options.setScriptText(scriptTemplate.buildParameterScript());
    }
        
    private static String getInputParameters(MacsScriptTemplate template, MacsScriptOptions options) {
        ScriptTemplate scriptTemplate = new ScriptTemplate();
        scriptTemplate.setParameterValue(SCRIPT_FILE_NAME_PARAM, options.getFileName());
        scriptTemplate.setParameterValue(SCRIPT_DESC_PARAM, options.getDescription());

        scriptTemplate.setParameterValue(GROUP_NAME_PARAM, options.getGroup().getFullName());
        scriptTemplate.setParameterValue(PORTER_TIMEOUT_PARAM, options.getPorterTimeout());
        scriptTemplate.setParameterValue(FILE_PATH_PARAM, options.getFilePath());
        scriptTemplate.setParameterValue(MISSED_FILE_NAME_PARAM, options.getMissedFileName());
        scriptTemplate.setParameterValue(SUCCESS_FILE_NAME_PARAM, options.getSuccessFileName());

        if (options.isBillingSelected()) {
            scriptTemplate.setParameterValue(BILLING_FLAG_PARAM, true);
            scriptTemplate.setParameterValue(BILLING_FILE_NAME_PARAM, options.getBillingFileName());
            scriptTemplate.setParameterValue(BILLING_FILE_PATH_PARAM, options.getBillingFilePath());
            scriptTemplate.setParameterValue(BILLING_FORMAT_PARAM, options.getBillingFormat());
            scriptTemplate.setParameterValue(BILLING_ENERGY_DAYS_PARAM, options.getBillingEnergyDays());
            scriptTemplate.setParameterValue(BILLING_DEMAND_DAYS_PARAM, options.getBillingDemandDays());
            scriptTemplate.setParameterValue(BILLING_GROUP_NAME_PARAM, options.getBillingGroup().getFullName());
        }

        if (options.isNotificationSelected()) {
            scriptTemplate.setParameterValue(NOTIFICATION_FLAG_PARAM, true);
            scriptTemplate.setParameterValue(NOTIFY_GROUP_PARAM, options.getNotificationGroupName());
            scriptTemplate.setParameterValue(EMAIL_SUBJECT_PARAM, options.getNotificationSubject());
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
            if (template.isIed300()) {
                scriptTemplate.setParameterValue(IED_TYPE_PARAM, options.getFrozenDemandRegister());
            } else if (template.isIed400()) {
                scriptTemplate.setParameterValue(IED_TYPE_PARAM, options.getIedType());
            }
        }
        return scriptTemplate.buildParameterScript();
    }
}
