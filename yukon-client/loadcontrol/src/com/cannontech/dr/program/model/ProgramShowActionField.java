package com.cannontech.dr.program.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ProgramShowActionField extends ProgramBackingFieldBase {

    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private final String baseKey = "yukon.web.modules.dr.programDetail.";
    private final ObjectWriter jsonWriter = new ObjectMapper().writer();

    @Override
    public String getFieldName() {
        return "SHOW_ACTION";
    }

    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor =
                messageSourceResolver.getMessageSourceAccessor(userContext);

        Map<String, Object> programStateMap = new HashMap<>();
        // Check null first - load management doesn't know about this group
        if (program == null) {
            programStateMap.put("unknown", true);
            programStateMap.put("unknownMsg", messageSourceAccessor.getMessage(baseKey + "unknown"));
            
            try {
                return jsonWriter.writeValueAsString(programStateMap);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        // Check manual active
        int programState = program.getProgramStatus();
        boolean running = programState == LMProgramBase.STATUS_MANUAL_ACTIVE
            || programState == LMProgramBase.STATUS_TIMED_ACTIVE;
        boolean scheduled = programState == LMProgramBase.STATUS_SCHEDULED;
        boolean disabled = program.getDisableFlag();

        messageSourceAccessor.getMessage(baseKey + "unknown");

        programStateMap.put("unknown", false);
        programStateMap.put("running", running);
        programStateMap.put("scheduled", scheduled);
        programStateMap.put("disabled", disabled);
        programStateMap.put("alreadyRunningMsg",
                            messageSourceAccessor.getMessage(baseKey + "alreadyRunning"));
        programStateMap.put("notRunningMsg",
                            messageSourceAccessor.getMessage(baseKey + "notRunning"));
        programStateMap.put("changeGearsDisabledMsg",
                            messageSourceAccessor.getMessage(baseKey + "actions.changeGears.disabled"));

        try {
            return jsonWriter.writeValueAsString(programStateMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected boolean handlesNull() {
        return true;
    }
}
