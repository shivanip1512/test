package com.cannontech.capcontrol.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.CompleteRegulator;

public class Regulator implements YukonPao {
    
    private Integer id;
    private String name;
    private PaoType type;
    private String description;
    private boolean disabled;
    private int configId;
    private Map<RegulatorPointMapping, Integer> mappings = new LinkedHashMap<>();

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public PaoType getType() {
        return type;
    }
    public void setType(PaoType type) {
        this.type = type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isDisabled() {
        return disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    public int getConfigId() {
        return configId;
    }
    public void setConfigId(int configId) {
        this.configId = configId;
    }
    public Map<RegulatorPointMapping, Integer> getMappings() {
        return mappings;
    }
    public void setMappings(Map<RegulatorPointMapping, Integer> mappings) {
        this.mappings = mappings;
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(id, type);
    }
    
    public CompleteRegulator asCompletePao() {
        
        CompleteRegulator complete = new CompleteRegulator();
        
        complete.setDescription(description);
        complete.setDisabled(disabled);
        if (id != null) {
            complete.setPaoIdentifier(PaoIdentifier.of(id, type));
        }
        complete.setPaoName(name);
        complete.setStatistics("");
        
        return complete;
    }
    
    public static Regulator fromCompletePao(CompleteRegulator complete) {
        
        Regulator regulator = new Regulator();
        
        regulator.setType(complete.getPaoType());
        regulator.setId(complete.getPaObjectId());
        regulator.setDisabled(complete.isDisabled());
        regulator.setName(complete.getPaoName());
        regulator.setDescription(complete.getDescription());
        
        return regulator;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Regulator [id=%s, name=%s, type=%s, description=%s, disabled=%s, mappings=%s]",
            id, name, type, description, disabled, mappings);
    }
    
}