package com.cannontech.dr.scenario.model;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;

/**
 * Abstract Base Class for scenario backing fields 
 */
public abstract class ScenarioBackingFieldBase implements DemandResponseBackingField<Scenario> {

    private final static String baseKey = "yukon.web.modules.dr.scenario.value";
    protected final static MessageSourceResolvable blankFieldResolvable = 
        new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");
    
    private ScenarioService scenarioService;
    
    /**
     * Method to get the field value from the given scenario
     * @param scenario - scenario to get value for
     * @param userContext - Current userContext
     * @return Value of this field for the given scenario (Should be one of: String, 
     *                                                  MessageSourceResolvable, ResolvableTemplate)
     */
    public abstract Object getScenarioValue(Scenario scenario, 
                                              YukonUserContext userContext);

    @Override
    public Object getValue(Scenario scenario, YukonUserContext userContext) {
        if(scenario != null || handlesNull()) {
            return getScenarioValue((Scenario) scenario, userContext);
        } else {
            return blankFieldResolvable;
        }
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        // Default implementation to return NO sorter
        return null;
    }
    
    protected Scenario getScenarioFromYukonPao(YukonPao from) {
        return scenarioService.getScenario(from.getPaoIdentifier().getPaoId());
    }
    
    protected MessageSourceResolvable buildResolvable(String name, Object... args) {
        return new YukonMessageSourceResolvable(getKey(name), args);
    }
    
    protected String getKey(String suffix) {
        return baseKey + "." + suffix;
    }
    
    protected boolean handlesNull() {
        return false;
    }
    
    @Autowired
    public void setScenarioService(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }
}
