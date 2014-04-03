package com.cannontech.dr.controlarea.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ControlAreaShowActionField extends ControlAreaBackingFieldBase {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private final String baseKey = "yukon.web.modules.dr.controlAreaDetail.";
    private final ObjectWriter jsonWriter = new ObjectMapper().writer();

    @Override
    public String getFieldName() {
        return "SHOW_ACTION";
    }
    
    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor =
                messageSourceResolver.getMessageSourceAccessor(userContext);

        Map<String, Object> controlAreaStateMap = new HashMap<>();
        // Check null first - load management doesn't know about this group
        if (controlArea == null) {
            controlAreaStateMap.put("unknown", true);
            controlAreaStateMap.put("unknownMsg", messageSourceAccessor.getMessage(baseKey + "unknown"));

            try {
                return jsonWriter.writeValueAsString(controlAreaStateMap);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        // Check manual active
        boolean noAssignedPrograms = controlArea.getLmProgramVector().isEmpty();
        boolean inactive = controlArea.getControlAreaState() == LMControlArea.STATE_INACTIVE;
        boolean fullyActive = controlArea.getControlAreaState() == LMControlArea.STATE_FULLY_ACTIVE;
        boolean disabled = controlArea.getDisableFlag();

        controlAreaStateMap.put("unknown", false);
        controlAreaStateMap.put("noAssignedPrograms", noAssignedPrograms);
        controlAreaStateMap.put("inactive", inactive);
        controlAreaStateMap.put("fullyActive", fullyActive);
        controlAreaStateMap.put("disabled", disabled);
        controlAreaStateMap.put("noAssignedProgramsMsg",
                            messageSourceAccessor.getMessage(baseKey + "noAssignedPrograms"));
        controlAreaStateMap.put("inactiveMsg",
                            messageSourceAccessor.getMessage(baseKey + "inactive"));
        controlAreaStateMap.put("fullyActiveMsg",
                            messageSourceAccessor.getMessage(baseKey + "fullyActive"));
        try {
            return jsonWriter.writeValueAsString(controlAreaStateMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected boolean handlesNull() {
        return true;
    }
}
