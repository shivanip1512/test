package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.service.ScenarioFieldService;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

public class ScenarioBackingService extends UpdateBackingServiceBase<Scenario> {
    private ScenarioService scenarioService;
    private ScenarioFieldService scenarioFieldService;

    @Override
    public DatedObject<Scenario> getDatedObject(int scenarioId) {
  
        Scenario scenario = scenarioService.getScenario(scenarioId);
        DatedObject<Scenario> datedScenario = new DatedObject<Scenario>(scenario);
            
        return datedScenario;
    }
    
    @Override
    public Object getValue(DatedObject<Scenario> datedObject, String[] idBits,
                           YukonUserContext userContext) {

        String fieldName = idBits[1];

        DemandResponseBackingField<Scenario> backingField = 
            scenarioFieldService.getBackingField(fieldName);
        
        Scenario scenario = null;
        if (datedObject != null) {
            scenario = datedObject.getObject();
        }
  
        return backingField.getValue(scenario, userContext);
    }
    
    @Autowired
    public void setScenarioService(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }
    
    @Autowired
    public void setScenarioFieldService(ScenarioFieldService scenarioFieldService) {
        this.scenarioFieldService = scenarioFieldService;
    }
}

