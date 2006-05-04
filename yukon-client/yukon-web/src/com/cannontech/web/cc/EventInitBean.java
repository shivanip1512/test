package com.cannontech.web.cc;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyBase;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.web.cc.methods.EventCreationBase;

public class EventInitBean {

    private Map<String, EventCreationBase> methodBeanLookup;
    private StrategyFactory strategyFactory;
    private ProgramService programService;
    
    public Map<String, EventCreationBase> getMethodBeanLookup() {
        return methodBeanLookup;
    }

    public void setMethodBeanLookup(Map<String, EventCreationBase> methodBeanLookup) {
        this.methodBeanLookup = methodBeanLookup;
    }

    public void setStrategyFactory(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }
    
    public String initEvent() {
        // determine selected program
        try {
            ExternalContext externalContext = 
                FacesContext.getCurrentInstance().getExternalContext();
            String programIdStr = 
                (String) externalContext.getRequestParameterMap().get("programId");
            int programId = Integer.parseInt(programIdStr);
            Program selectedProgram = programService.getProgram(programId);
            StrategyBase strategy = strategyFactory.getStrategy(selectedProgram);
            String methodKey = strategy.getMethodKey();
            EventCreationBase methodBean = methodBeanLookup.get(methodKey);
            if (methodBean == null) {
                throw new Exception("No Bean is configured for method: " + methodKey);
            }
            methodBean.setProgram(selectedProgram);
            methodBean.setStrategy(strategy);
            methodBean.initialize();
            String startPageKey = methodBean.getStartPage();
            return startPageKey;
        } catch (Exception e) {
            CTILogger.warn("Could not start the selected program.", e);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                                "Could not start the selected program", 
                                                e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
    }

    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
}
