package com.cannontech.web.editor.model;

import java.util.HashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.myfaces.component.html.ext.HtmlInputText;


import com.cannontech.cbc.model.EditorDataModel;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.web.util.CapControlModelUtil;

public class CapControlStrategyModel implements EditorDataModel{

    private CapControlStrategy strategy = null;
    private HashMap<String, Boolean> enableTable = new HashMap<String, Boolean>(10);
    private HashMap<String, Double> valueTable = new HashMap<String, Double>(10);
    private boolean resetPkOffPkVals;
    
    public boolean isResetPkOffPkVals() {
        return resetPkOffPkVals;
    }

    public void setResetPkOffPkVals(boolean resetPkOffPkVals) {
        this.resetPkOffPkVals = resetPkOffPkVals;
    }

    public Boolean getIntegrateFlag() {
        return strategy.getIntegrateFlag().equalsIgnoreCase("Y");
    }

    public void setIntegrateFlag(Boolean integrateFlag) {
        strategy.setIntegrateFlag((integrateFlag) ? "Y" : "N");
    }

    public Integer getIntegratePeriod() {
        return strategy.getIntegratePeriod();
    }

    public void setIntegratePeriod(Integer integratePeriod) {
        strategy.setIntegratePeriod(integratePeriod);
    }

    public CapControlStrategyModel(CapControlStrategy s) {
        super();
        strategy = s;
        updateView();
    }

    private void resetEnableMap() {
        enableTable.put(CapControlModelUtil.PEAK_LOWER, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.PEAK_UPPER, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.PEAK_LAG, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.PEAK_LEAD, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.OFFP_LOWER, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.OFFP_UPPER, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.OFFP_LAG, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.OFFP_LEAD, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.PEAK_PF_POINT, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.OFFP_PF_POINT, Boolean.FALSE);
    }

    private void resetValueTable() {
        valueTable.put(CapControlModelUtil.PEAK_LOWER, new Double(0.0));
        valueTable.put(CapControlModelUtil.PEAK_UPPER, new Double(0.0));
        valueTable.put(CapControlModelUtil.PEAK_LAG, new Double(0.0));
        valueTable.put(CapControlModelUtil.PEAK_LEAD, new Double(0.0));
        valueTable.put(CapControlModelUtil.OFFP_LOWER, new Double(0.0));
        valueTable.put(CapControlModelUtil.OFFP_UPPER, new Double(0.0));
        valueTable.put(CapControlModelUtil.OFFP_LAG, new Double(0.0));
        valueTable.put(CapControlModelUtil.OFFP_LEAD, new Double(0.0));
        valueTable.put(CapControlModelUtil.PEAK_PF_POINT, new Double (0.0));
        valueTable.put(CapControlModelUtil.OFFP_PF_POINT, new Double (0.0));
    }

    public CapControlStrategy getStrategy() {
        return strategy;
    }

    private void updateView() {
        resetEnableMap();
        resetValueTable();
        String algo = strategy.getControlUnits();
        if (CapControlModelUtil.isVarStrat(algo)) {
            updateVoltStrat();
        } else if (CapControlModelUtil.isPFactor(algo)) {
            updatePFStrat();
            updateVoltStrat();
        } else if (CapControlModelUtil.isVoltStrat(algo)) {
            updateVoltStrat();
        } else if (CapControlModelUtil.isVoltVar(algo)) {
            updateVarStrat();
            updateVoltStrat();
        }
    }

    public HashMap<String, Boolean> getEnableTable() {
        return enableTable;
    }

    public HashMap<String, Double> getValueTable() {
        return valueTable;
    }

    private void updateVoltStrat() {
        enableTable.put(CapControlModelUtil.PEAK_LAG, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.PEAK_LEAD, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.OFFP_LAG, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.OFFP_LEAD, Boolean.TRUE);
        valueTable.put(CapControlModelUtil.PEAK_LAG, strategy.getPeakLag());
        valueTable.put(CapControlModelUtil.PEAK_LEAD, strategy.getPeakLead());
        valueTable.put(CapControlModelUtil.OFFP_LAG, strategy.getOffPkLag());
        valueTable.put(CapControlModelUtil.OFFP_LEAD, strategy.getOffPkLead());
    }

    private void updateVarStrat() {
        enableTable.put(CapControlModelUtil.PEAK_LOWER, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.PEAK_UPPER, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.OFFP_LOWER, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.OFFP_UPPER, Boolean.TRUE);
        valueTable.put(CapControlModelUtil.PEAK_LOWER, strategy.getPkVarLag());
        valueTable.put(CapControlModelUtil.PEAK_UPPER, strategy.getPkVarLead());
        valueTable.put(CapControlModelUtil.OFFP_LOWER, strategy.getOffpkVarLag());
        valueTable.put(CapControlModelUtil.OFFP_UPPER, strategy.getOffpkVarLead());

    }
    
    private void updatePFStrat () {
        enableTable.put(CapControlModelUtil.PEAK_PF_POINT, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.OFFP_PF_POINT, Boolean.TRUE);
        valueTable.put(CapControlModelUtil.PEAK_PF_POINT, strategy.getPkPFPoint());
        valueTable.put(CapControlModelUtil.OFFP_PF_POINT, strategy.getOffPkPFPoint());
    }

    public String getPEAK_UPPER() {
        return CapControlModelUtil.PEAK_UPPER;
    }

    public String getOFFP_LAG() {
        return CapControlModelUtil.OFFP_LAG;
    }

    public String getOFFP_LEAD() {
        return CapControlModelUtil.OFFP_LEAD;
    }

    public String getOFFP_LOWER() {
        return CapControlModelUtil.OFFP_LOWER;
    }

    public String getOFFP_UPPER() {
        return CapControlModelUtil.OFFP_UPPER;
    }

    public String getPEAK_LAG() {
        return CapControlModelUtil.PEAK_LAG;
    }

    public String getPEAK_LEAD() {
        return CapControlModelUtil.PEAK_LEAD;
    }

    public String getPEAK_LOWER() {
        return CapControlModelUtil.PEAK_LOWER;
    }
    
    public String getPEAK_PF_POINT() {
        return CapControlModelUtil.PEAK_PF_POINT;
    }
    
    public String getOFFP_PF_POINT () {
        return CapControlModelUtil.OFFP_PF_POINT;
    }
    
    public void updateModel() {
        updateData();
        updateView();
    }

    private void updateData() {
            strategy.setPeakLag(valueTable.get(CapControlModelUtil.PEAK_LAG));
            strategy.setPeakLead(valueTable.get(CapControlModelUtil.PEAK_LEAD));
            strategy.setOffPkLag(valueTable.get(CapControlModelUtil.OFFP_LAG));
            strategy.setOffPkLead(valueTable.get(CapControlModelUtil.OFFP_LEAD));
            strategy.setPkVarLag(valueTable.get(CapControlModelUtil.PEAK_LOWER));
            strategy.setPkVarLead(valueTable.get(CapControlModelUtil.PEAK_UPPER));
            strategy.setOffpkVarLag(valueTable.get(CapControlModelUtil.OFFP_LOWER));
            strategy.setOffpkVarLead(valueTable.get(CapControlModelUtil.OFFP_UPPER));
            strategy.setPkPFPoint(valueTable.get(CapControlModelUtil.PEAK_PF_POINT));
            strategy.setOffPkPFPoint(valueTable.get(CapControlModelUtil.OFFP_PF_POINT));
    }

    public void dataChanged(ValueChangeEvent vce) {
        UIComponent comp = vce.getComponent();
        if (comp instanceof HtmlInputText) {
            HtmlInputText textField = (HtmlInputText) comp;
            String clientID = textField.getClientId(FacesContext.getCurrentInstance());
            if (clientID.equalsIgnoreCase(CapControlModelUtil.PEAK_LAG)) {
                valueTable.put(CapControlModelUtil.PEAK_LAG, (Double)vce.getNewValue());
                strategy.setPeakLag((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.PEAK_LEAD)) {
                valueTable.put(CapControlModelUtil.PEAK_LEAD, (Double)vce.getNewValue());
                strategy.setPeakLead((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.OFFP_LAG)) {
                valueTable.put(CapControlModelUtil.OFFP_LAG, (Double)vce.getNewValue());
                strategy.setOffPkLag((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.OFFP_LEAD)) {
                valueTable.put(CapControlModelUtil.OFFP_LEAD, (Double)vce.getNewValue());
                strategy.setOffPkLead((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.PEAK_LOWER)) {
                valueTable.put(CapControlModelUtil.PEAK_LOWER, (Double)vce.getNewValue());
                strategy.setPkVarLag((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.PEAK_UPPER)) {
                valueTable.put(CapControlModelUtil.PEAK_UPPER, (Double)vce.getNewValue());
                strategy.setPkVarLead((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.OFFP_LOWER)) {
                valueTable.put(CapControlModelUtil.OFFP_LOWER, (Double)vce.getNewValue());
                strategy.setOffpkVarLag((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.OFFP_UPPER)) {
                valueTable.put(CapControlModelUtil.OFFP_UPPER, (Double)vce.getNewValue());
                strategy.setOffpkVarLead((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.PEAK_PF_POINT)) {
                valueTable.put(CapControlModelUtil.PEAK_PF_POINT, (Double)vce.getNewValue());
                strategy.setPkPFPoint((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.OFFP_PF_POINT)) {
                valueTable.put(CapControlModelUtil.OFFP_PF_POINT, (Double)vce.getNewValue());
                strategy.setOffPkPFPoint((Double) vce.getNewValue());
            }
        }
        updateModel();
    }

    public void resetValues(ValueChangeEvent vce) {
        resetValueTable();
        setResetPkOffPkVals(true);
    }
    
    public boolean isKVarAlgorithm () {
        String algo = strategy.getControlUnits();
        return CapControlModelUtil.isVarStrat(algo);
    }
    
    public boolean isPFAlgorithm () {
        String algo = strategy.getControlUnits();
        return CapControlModelUtil.isPFactor(algo);
    }
    
    public boolean isVoltVar () {
     String algo = strategy.getControlUnits();
     return CapControlModelUtil.isVoltVar(algo);
    }

    public void updateDataModel() {
        updateModel();
    }
    
}
