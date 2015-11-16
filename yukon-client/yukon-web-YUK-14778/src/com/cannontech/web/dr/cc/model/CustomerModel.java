package com.cannontech.web.dr.cc.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.customer.CICustomerPointType;

public class CustomerModel {
    private List<CICustomerPointType> pointTypes;
    private Map<CICustomerPointType, BigDecimal> pointValues;
    private List<ProgramPaoModel> activePrograms;
    private List<ProgramPaoModel> availablePrograms;
    
    public List<CICustomerPointType> getPointTypes() {
        return pointTypes;
    }
    
    public void setPointTypes(List<CICustomerPointType> pointTypes) {
        this.pointTypes = pointTypes;
    }
    
    public Map<CICustomerPointType, BigDecimal> getPointValues() {
        return pointValues;
    }
    
    public void setPointValues(Map<CICustomerPointType, BigDecimal> pointValues) {
        this.pointValues = pointValues;
    }
    
    public List<ProgramPaoModel> getActivePrograms() {
        return activePrograms;
    }
    
    public void setActivePrograms(List<ProgramPaoModel> activePrograms) {
        this.activePrograms = activePrograms;
    }
    
    public List<ProgramPaoModel> getAvailablePrograms() {
        return availablePrograms;
    }
    
    public void setAvailablePrograms(List<ProgramPaoModel> availablePrograms) {
        this.availablePrograms = availablePrograms;
    }
    
    /**
     * Converts the specified list of LiteYukonPAObjects into ProgramPaoModels and populates availablePrograms with
     * them. (This will overwrite any existing available programs).
     */
    public void populateAvailablePrograms(List<LiteYukonPAObject> availablePrograms) {
        this.availablePrograms = new ArrayList<>();
        for(LiteYukonPAObject pao : availablePrograms) {
            ProgramPaoModel paoModel = new ProgramPaoModel();
            paoModel.setPaoId(pao.getPaoIdentifier().getPaoId());
            paoModel.setPaoName(pao.getPaoName());
            this.availablePrograms.add(paoModel);
        }
    }
    
    /**
     * Converts the specified list of LiteYukonPAObjects into ProgramPaoModels and populates activePrograms with
     * them. (This will overwrite any existing active programs).
     */
    public void populateActivePrograms(List<LiteYukonPAObject> activePrograms) {
        this.activePrograms = new ArrayList<>();
        for(LiteYukonPAObject pao : activePrograms) {
            ProgramPaoModel paoModel = new ProgramPaoModel();
            paoModel.setPaoId(pao.getPaoIdentifier().getPaoId());
            paoModel.setPaoName(pao.getPaoName());
            this.activePrograms.add(paoModel);
        }
    }
    
    /**
     * A simple, mutable representation of a pao with an int id and a name.
     */
    public static class ProgramPaoModel {
        private int paoId;
        private String paoName;
        public int getPaoId() {
            return paoId;
        }
        public void setPaoId(int paoId) {
            this.paoId = paoId;
        }
        public String getPaoName() {
            return paoName;
        }
        public void setPaoName(String paoName) {
            this.paoName = paoName;
        }
    }
}
