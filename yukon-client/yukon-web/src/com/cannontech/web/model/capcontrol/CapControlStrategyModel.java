package com.cannontech.web.model.capcontrol;

import java.util.HashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.myfaces.component.html.ext.HtmlInputText;

import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.web.util.CapControlModelUtil;

public class CapControlStrategyModel {

    private CapControlStrategy strategy = null;
    private HashMap enableTable = new HashMap(10);
    private HashMap valueTable = new HashMap(10);

    public CapControlStrategyModel(CapControlStrategy s) {
        super();
        strategy = s;
        updateView();
    }

    private void resetEnableMap() {
        enableTable.put(CapControlModelUtil.PEAK_LOWER, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.PEAK_UPPER, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.PEAK_LAG, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.PEAK_LEAD, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.OFFP_LOWER, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.OFFP_UPPER, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.OFFP_LAG, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.OFFP_LEAD, Boolean.TRUE);
        
        enableTable.put(CapControlModelUtil.PEAK_PF_POINT, Boolean.TRUE);
        enableTable.put(CapControlModelUtil.OFFP_PF_POINT, Boolean.TRUE);

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
            updateVarStrat();

        } 
        else if (CapControlModelUtil.isPFactor(algo)) {
            updatePFStrat();
            updateVoltStrat();
        } 
        else if (CapControlModelUtil.isVoltStrat(algo)) {
            updateVoltStrat();
        }
        else if (CapControlModelUtil.isVoltVar(algo)) {
            updateVarStrat();
            updateVoltStrat();
        }
        
    }



    public HashMap getEnableTable() {
        return enableTable;
    }

    public HashMap getValueTable() {
        return valueTable;
    }

    private void updateVoltStrat() {
        enableTable.put(CapControlModelUtil.PEAK_LAG, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.PEAK_LEAD, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.OFFP_LAG, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.OFFP_LEAD, Boolean.FALSE);

        valueTable.put(CapControlModelUtil.PEAK_LAG, strategy.getPeakLag());
        valueTable.put(CapControlModelUtil.PEAK_LEAD, strategy.getPeakLead());
        valueTable.put(CapControlModelUtil.OFFP_LAG, strategy.getOffPkLag());
        valueTable.put(CapControlModelUtil.OFFP_LEAD, strategy.getOffPkLead());

    }

    private void updateVarStrat() {
        enableTable.put(CapControlModelUtil.PEAK_LOWER, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.PEAK_UPPER, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.OFFP_LOWER, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.OFFP_UPPER, Boolean.FALSE);

        valueTable.put(CapControlModelUtil.PEAK_LOWER, strategy.getPkVarLag());
        valueTable.put(CapControlModelUtil.PEAK_UPPER, strategy.getPkVarLead());
        valueTable.put(CapControlModelUtil.OFFP_LOWER,
                       strategy.getOffpkVarLag());
        valueTable.put(CapControlModelUtil.OFFP_UPPER,
                       strategy.getOffpkVarLead());

    }
    
    private void updatePFStrat () {
        enableTable.put(CapControlModelUtil.PEAK_PF_POINT, Boolean.FALSE);
        enableTable.put(CapControlModelUtil.OFFP_PF_POINT, Boolean.FALSE);
        
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
       // if (strategy.getPeakLag().doubleValue() == 0)
            strategy.setPeakLag((Double) valueTable.get(CapControlModelUtil.PEAK_LAG));
       // if (strategy.getPeakLead().doubleValue() == 0)
            strategy.setPeakLead((Double) valueTable.get(CapControlModelUtil.PEAK_LEAD));
       // if (strategy.getOffPkLag().doubleValue() == 0)
            strategy.setOffPkLag((Double) valueTable.get(CapControlModelUtil.OFFP_LAG));
       // if (strategy.getOffPkLead().doubleValue() == 0)
            strategy.setOffPkLead((Double) valueTable.get(CapControlModelUtil.OFFP_LEAD));

//        if (strategy.getPkVarLag().doubleValue() == 0)
            strategy.setPkVarLag((Double) valueTable.get(CapControlModelUtil.PEAK_LOWER));
//        if (strategy.getPkVarLead().doubleValue() == 0)
            strategy.setPkVarLead((Double) valueTable.get(CapControlModelUtil.PEAK_UPPER));
//        if (strategy.getOffpkVarLag().doubleValue() == 0)
            strategy.setOffpkVarLag((Double) valueTable.get(CapControlModelUtil.OFFP_LOWER));
//        if (strategy.getOffpkVarLead().doubleValue() == 0)
            strategy.setOffpkVarLead((Double) valueTable.get(CapControlModelUtil.OFFP_UPPER));

            strategy.setPkPFPoint((Double) valueTable.get(CapControlModelUtil.PEAK_PF_POINT));
            strategy.setOffPkPFPoint((Double)valueTable.get(CapControlModelUtil.OFFP_PF_POINT));

    }

    public void dataChanged(ValueChangeEvent vce) {
        UIComponent comp = vce.getComponent();
        if (comp instanceof HtmlInputText) {
            HtmlInputText textField = (HtmlInputText) comp;
            String clientID = textField.getClientId(FacesContext.getCurrentInstance());
            if (clientID.equalsIgnoreCase(CapControlModelUtil.PEAK_LAG)) {
                valueTable.put(vce.getNewValue(), CapControlModelUtil.PEAK_LAG);
                strategy.setPeakLag((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.PEAK_LEAD)) {
                valueTable.put(vce.getNewValue(), CapControlModelUtil.PEAK_LEAD);
                strategy.setPeakLead((Double) vce.getNewValue());

            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.OFFP_LAG)) {
                valueTable.put(vce.getNewValue(), CapControlModelUtil.OFFP_LAG);
                strategy.setOffPkLag((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.OFFP_LEAD)) {
                valueTable.put(vce.getNewValue(), CapControlModelUtil.OFFP_LEAD);
                strategy.setOffPkLead((Double) vce.getNewValue());
            }

            if (clientID.equalsIgnoreCase(CapControlModelUtil.PEAK_LOWER)) {
                valueTable.put(vce.getNewValue(), CapControlModelUtil.PEAK_LOWER);
                strategy.setPkVarLag((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.PEAK_UPPER)) {
                valueTable.put(vce.getNewValue(), CapControlModelUtil.PEAK_UPPER);
                strategy.setPkVarLead((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.OFFP_LOWER)) {
                valueTable.put(vce.getNewValue(), CapControlModelUtil.OFFP_LOWER);
                strategy.setOffpkVarLag((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.OFFP_UPPER)) {
                valueTable.put(vce.getNewValue(), CapControlModelUtil.OFFP_UPPER);
                strategy.setOffpkVarLead((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.PEAK_PF_POINT)) {
                valueTable.put(vce.getNewValue(), CapControlModelUtil.PEAK_PF_POINT);
                strategy.setPkPFPoint((Double) vce.getNewValue());
            }
            if (clientID.equalsIgnoreCase(CapControlModelUtil.OFFP_PF_POINT)) {
                valueTable.put(vce.getNewValue(), CapControlModelUtil.OFFP_PF_POINT);
                strategy.setOffPkPFPoint((Double) vce.getNewValue());
            }

        
        }

        updateModel();
    }

}
