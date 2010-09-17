package com.cannontech.web.amr.statusPointProcessing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointProcessing.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitor;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitorMessageProcessor;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitorStateType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/statusPointProcessing/process/*")
@CheckRoleProperty(YukonRoleProperty.STATUS_POINT_PROCESSING)
public class StatusPointProcessingController {

	private StatusPointMonitorDao statusPointMonitorDao;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	@RequestMapping
    public String process(int statusPointMonitorId, 
                          ModelMap model, 
                          YukonUserContext userContext) throws ServletRequestBindingException {
                          
	    setupProcessAttributes(statusPointMonitorId, model, userContext);
		return "statusPointProcessing/process/process.jsp";
	}
	
	private void setupProcessAttributes(int statusPointMonitorId, ModelMap model, YukonUserContext userContext) {
	    
	    StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
        model.addAttribute("statusPointMonitor", statusPointMonitor);
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        if (statusPointMonitor.getEvaluatorStatus() == MonitorEvaluatorStatus.ENABLED) {
            model.addAttribute("statusPointMonitorStatus", messageSourceAccessor.getMessage("yukon.web.modules.amr.statusPointEditor.enabled"));
        } else {
            model.addAttribute("statusPointMonitorStatus", messageSourceAccessor.getMessage("yukon.web.modules.amr.statusPointEditor.disabled"));
        }
        
        List<String> prevStateStrings = Lists.newArrayList();
        List<String> nextStateStrings = Lists.newArrayList();
        for (StatusPointMonitorMessageProcessor processor : statusPointMonitor.getStatusPointMonitorMessageProcessors()) {
            //previous state handling
            if (processor.getPrevStateType() != StatusPointMonitorStateType.EXACT) {
                if (processor.getPrevStateType() == StatusPointMonitorStateType.DIFFERENCE) {
                    prevStateStrings.add(messageSourceAccessor.getMessage("yukon.web.modules.amr.statusPointEditor.state.difference"));
                } else if (processor.getPrevStateType() == StatusPointMonitorStateType.DONT_CARE) {
                    prevStateStrings.add(messageSourceAccessor.getMessage("yukon.web.modules.amr.statusPointEditor.state.dontCare"));
                }
            } else {
                prevStateStrings.add(statusPointMonitor.getStateGroup().getStatesList().get(processor.getPrevStateInt()).toString());
            }
            
            //next state handling
            if (processor.getNextStateType() != StatusPointMonitorStateType.EXACT) {
                if (processor.getNextStateType() == StatusPointMonitorStateType.DIFFERENCE) {
                    nextStateStrings.add(messageSourceAccessor.getMessage("yukon.web.modules.amr.statusPointEditor.state.difference"));
                } else if (processor.getNextStateType() == StatusPointMonitorStateType.DONT_CARE) {
                    nextStateStrings.add(messageSourceAccessor.getMessage("yukon.web.modules.amr.statusPointEditor.state.dontCare"));
                }
            } else {
                nextStateStrings.add(statusPointMonitor.getStateGroup().getStatesList().get(processor.getNextStateInt()).toString());
            }
        }
        
        model.addAttribute("prevStateStrings", prevStateStrings);
        model.addAttribute("nextStateStrings", nextStateStrings);
	}
	
	@Autowired
	public void setStatusPointMonitorDao(StatusPointMonitorDao statusPointMonitorDao) {
		this.statusPointMonitorDao = statusPointMonitorDao;
	}
	
	@Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}