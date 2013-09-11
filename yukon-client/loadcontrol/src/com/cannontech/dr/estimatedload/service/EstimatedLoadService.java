package com.cannontech.dr.estimatedload.service;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public interface EstimatedLoadService {

    /** Returns a message that displays the amount of Connected Load in kW that is attached to an LM object.
     * The object must be a LM program, control area, or scenario for a numeric value to be returned, or else
     * a generic 'N/A' message will be displayed.  Connected Load refers to the number of enrolled appliances in 
     * a given LM object multiplied by the average per-appliance load amount which is given in kW.  
     * @param drPaoIdentifier The pao identifier of the LM object for which connected load data is requested.
     * @return A message containing the numeric amount of connected load, in kW.
     */
    public YukonMessageSourceResolvable getConnectedLoad(PaoIdentifier drPaoIdentifier);

    /** Returns a message that displays the amount of Diversified Load in kW that is attached to an LM object.
     * The object must be a LM program, control area, or scenario for a numeric value to be returned, or else
     * a generic 'N/A' message will be displayed.  Diversified Load refers to the amount of load which is currently
     * drawing electricity, and is always a fraction of the Connected Load amount.  Diversified load is calculated
     * by first computing the connected load and then multiplying it by the output of the Appliance Category formula
     * assigned to the LM object identified by the PaoIdentifier parameter.  Formula output is always between 0.0 and
     * 1.0, so Diversified Load is always less than the Connected Load amount and always greater than or equal to 0.  
     *   
     * @param drPaoIdentifier The pao identifier of the LM object for which diversified load data is requested.
     * @return A message containing the numeric amount of diversified load, in kW.
     */
    public YukonMessageSourceResolvable getDiversifiedLoad(PaoIdentifier drPaoIdentifier);

    /** Returns a message that displays the Maximum kW Savings amount, given in kW, that is attached an LM object.
     * The object must be a LM program, control area, or scenario for a numeric value to be returned, or else
     * a generic 'N/A' message will be displayed.  Max kW Savings refers to the maximum amount of kW Savings that can
     * be obtained when program (or programs) control is initiated, based on the output of the formula assigned
     * to that program's default gear for the LM object identified by the PaoIdentifier parameter. Formula output is
     * always between 0.0 and 1.0, so Max kW Savings is always less than the Diversified Load amount and always greater
     * than or equal to 0.  
     *   
     * @param drPaoIdentifier The pao identifier of the LM object for which Max kW Savings data is requested.
     * @return Returns a message containing the numeric amount of Max kW Savings, in kW.
     */
    public YukonMessageSourceResolvable getKwSavings(PaoIdentifier drPaoIdentifier);

}
