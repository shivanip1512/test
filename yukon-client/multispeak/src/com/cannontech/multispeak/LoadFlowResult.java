/**
 * LoadFlowResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class LoadFlowResult  extends com.cannontech.multispeak.MspResultsBase  implements java.io.Serializable {
    private java.lang.Float priKvA;
    private java.lang.Float priKvB;
    private java.lang.Float priKvC;
    private java.lang.Float priKvBal;
    private java.lang.Float sectionDropA;
    private java.lang.Float sectionDropB;
    private java.lang.Float sectionDropC;
    private java.lang.Float sectionDropBal;
    private java.lang.Float cumulativeDropA;
    private java.lang.Float cumulativeDropB;
    private java.lang.Float cumulativeDropC;
    private java.lang.Float cumulativeDropBal;
    private java.lang.Float thruCurrentA;
    private java.lang.Float thruCurrentB;
    private java.lang.Float thruCurrentC;
    private java.lang.Float thruCurrentBal;
    private java.lang.Float pctCapA;
    private java.lang.Float pctCapB;
    private java.lang.Float pctCapC;
    private java.lang.Float pctCapBal;
    private java.lang.Float thruKwA;
    private java.lang.Float thruKwB;
    private java.lang.Float thruKwC;
    private java.lang.Float thruKwBal;
    private java.lang.Float thruKvarA;
    private java.lang.Float thruKvarB;
    private java.lang.Float thruKvarC;
    private java.lang.Float thruKvarBal;
    private java.lang.Float pctPfA;
    private java.lang.Float pctPfB;
    private java.lang.Float pctPfC;
    private java.lang.Float pctPfBal;
    private java.lang.Float pctLoss;
    private java.lang.Long sectKwA;
    private java.lang.Long sectKwB;
    private java.lang.Long sectKwC;
    private java.lang.Long sectKwBal;
    private java.lang.Float secKvarA;
    private java.lang.Float secKvarB;
    private java.lang.Float secKvarC;
    private java.lang.Float secKvarBal;
    private java.lang.Long consOnA;
    private java.lang.Long consOnB;
    private java.lang.Long consOnC;
    private java.lang.Long consOnBal;
    private java.lang.Long consThruA;
    private java.lang.Long consThruB;
    private java.lang.Long consThruC;
    private java.lang.Long consThruBal;
    private java.lang.Float baseKvCalc;

    public LoadFlowResult() {
    }

    public LoadFlowResult(
           java.lang.Float priKvA,
           java.lang.Float priKvB,
           java.lang.Float priKvC,
           java.lang.Float priKvBal,
           java.lang.Float sectionDropA,
           java.lang.Float sectionDropB,
           java.lang.Float sectionDropC,
           java.lang.Float sectionDropBal,
           java.lang.Float cumulativeDropA,
           java.lang.Float cumulativeDropB,
           java.lang.Float cumulativeDropC,
           java.lang.Float cumulativeDropBal,
           java.lang.Float thruCurrentA,
           java.lang.Float thruCurrentB,
           java.lang.Float thruCurrentC,
           java.lang.Float thruCurrentBal,
           java.lang.Float pctCapA,
           java.lang.Float pctCapB,
           java.lang.Float pctCapC,
           java.lang.Float pctCapBal,
           java.lang.Float thruKwA,
           java.lang.Float thruKwB,
           java.lang.Float thruKwC,
           java.lang.Float thruKwBal,
           java.lang.Float thruKvarA,
           java.lang.Float thruKvarB,
           java.lang.Float thruKvarC,
           java.lang.Float thruKvarBal,
           java.lang.Float pctPfA,
           java.lang.Float pctPfB,
           java.lang.Float pctPfC,
           java.lang.Float pctPfBal,
           java.lang.Float pctLoss,
           java.lang.Long sectKwA,
           java.lang.Long sectKwB,
           java.lang.Long sectKwC,
           java.lang.Long sectKwBal,
           java.lang.Float secKvarA,
           java.lang.Float secKvarB,
           java.lang.Float secKvarC,
           java.lang.Float secKvarBal,
           java.lang.Long consOnA,
           java.lang.Long consOnB,
           java.lang.Long consOnC,
           java.lang.Long consOnBal,
           java.lang.Long consThruA,
           java.lang.Long consThruB,
           java.lang.Long consThruC,
           java.lang.Long consThruBal,
           java.lang.Float baseKvCalc) {
           this.priKvA = priKvA;
           this.priKvB = priKvB;
           this.priKvC = priKvC;
           this.priKvBal = priKvBal;
           this.sectionDropA = sectionDropA;
           this.sectionDropB = sectionDropB;
           this.sectionDropC = sectionDropC;
           this.sectionDropBal = sectionDropBal;
           this.cumulativeDropA = cumulativeDropA;
           this.cumulativeDropB = cumulativeDropB;
           this.cumulativeDropC = cumulativeDropC;
           this.cumulativeDropBal = cumulativeDropBal;
           this.thruCurrentA = thruCurrentA;
           this.thruCurrentB = thruCurrentB;
           this.thruCurrentC = thruCurrentC;
           this.thruCurrentBal = thruCurrentBal;
           this.pctCapA = pctCapA;
           this.pctCapB = pctCapB;
           this.pctCapC = pctCapC;
           this.pctCapBal = pctCapBal;
           this.thruKwA = thruKwA;
           this.thruKwB = thruKwB;
           this.thruKwC = thruKwC;
           this.thruKwBal = thruKwBal;
           this.thruKvarA = thruKvarA;
           this.thruKvarB = thruKvarB;
           this.thruKvarC = thruKvarC;
           this.thruKvarBal = thruKvarBal;
           this.pctPfA = pctPfA;
           this.pctPfB = pctPfB;
           this.pctPfC = pctPfC;
           this.pctPfBal = pctPfBal;
           this.pctLoss = pctLoss;
           this.sectKwA = sectKwA;
           this.sectKwB = sectKwB;
           this.sectKwC = sectKwC;
           this.sectKwBal = sectKwBal;
           this.secKvarA = secKvarA;
           this.secKvarB = secKvarB;
           this.secKvarC = secKvarC;
           this.secKvarBal = secKvarBal;
           this.consOnA = consOnA;
           this.consOnB = consOnB;
           this.consOnC = consOnC;
           this.consOnBal = consOnBal;
           this.consThruA = consThruA;
           this.consThruB = consThruB;
           this.consThruC = consThruC;
           this.consThruBal = consThruBal;
           this.baseKvCalc = baseKvCalc;
    }


    /**
     * Gets the priKvA value for this LoadFlowResult.
     * 
     * @return priKvA
     */
    public java.lang.Float getPriKvA() {
        return priKvA;
    }


    /**
     * Sets the priKvA value for this LoadFlowResult.
     * 
     * @param priKvA
     */
    public void setPriKvA(java.lang.Float priKvA) {
        this.priKvA = priKvA;
    }


    /**
     * Gets the priKvB value for this LoadFlowResult.
     * 
     * @return priKvB
     */
    public java.lang.Float getPriKvB() {
        return priKvB;
    }


    /**
     * Sets the priKvB value for this LoadFlowResult.
     * 
     * @param priKvB
     */
    public void setPriKvB(java.lang.Float priKvB) {
        this.priKvB = priKvB;
    }


    /**
     * Gets the priKvC value for this LoadFlowResult.
     * 
     * @return priKvC
     */
    public java.lang.Float getPriKvC() {
        return priKvC;
    }


    /**
     * Sets the priKvC value for this LoadFlowResult.
     * 
     * @param priKvC
     */
    public void setPriKvC(java.lang.Float priKvC) {
        this.priKvC = priKvC;
    }


    /**
     * Gets the priKvBal value for this LoadFlowResult.
     * 
     * @return priKvBal
     */
    public java.lang.Float getPriKvBal() {
        return priKvBal;
    }


    /**
     * Sets the priKvBal value for this LoadFlowResult.
     * 
     * @param priKvBal
     */
    public void setPriKvBal(java.lang.Float priKvBal) {
        this.priKvBal = priKvBal;
    }


    /**
     * Gets the sectionDropA value for this LoadFlowResult.
     * 
     * @return sectionDropA
     */
    public java.lang.Float getSectionDropA() {
        return sectionDropA;
    }


    /**
     * Sets the sectionDropA value for this LoadFlowResult.
     * 
     * @param sectionDropA
     */
    public void setSectionDropA(java.lang.Float sectionDropA) {
        this.sectionDropA = sectionDropA;
    }


    /**
     * Gets the sectionDropB value for this LoadFlowResult.
     * 
     * @return sectionDropB
     */
    public java.lang.Float getSectionDropB() {
        return sectionDropB;
    }


    /**
     * Sets the sectionDropB value for this LoadFlowResult.
     * 
     * @param sectionDropB
     */
    public void setSectionDropB(java.lang.Float sectionDropB) {
        this.sectionDropB = sectionDropB;
    }


    /**
     * Gets the sectionDropC value for this LoadFlowResult.
     * 
     * @return sectionDropC
     */
    public java.lang.Float getSectionDropC() {
        return sectionDropC;
    }


    /**
     * Sets the sectionDropC value for this LoadFlowResult.
     * 
     * @param sectionDropC
     */
    public void setSectionDropC(java.lang.Float sectionDropC) {
        this.sectionDropC = sectionDropC;
    }


    /**
     * Gets the sectionDropBal value for this LoadFlowResult.
     * 
     * @return sectionDropBal
     */
    public java.lang.Float getSectionDropBal() {
        return sectionDropBal;
    }


    /**
     * Sets the sectionDropBal value for this LoadFlowResult.
     * 
     * @param sectionDropBal
     */
    public void setSectionDropBal(java.lang.Float sectionDropBal) {
        this.sectionDropBal = sectionDropBal;
    }


    /**
     * Gets the cumulativeDropA value for this LoadFlowResult.
     * 
     * @return cumulativeDropA
     */
    public java.lang.Float getCumulativeDropA() {
        return cumulativeDropA;
    }


    /**
     * Sets the cumulativeDropA value for this LoadFlowResult.
     * 
     * @param cumulativeDropA
     */
    public void setCumulativeDropA(java.lang.Float cumulativeDropA) {
        this.cumulativeDropA = cumulativeDropA;
    }


    /**
     * Gets the cumulativeDropB value for this LoadFlowResult.
     * 
     * @return cumulativeDropB
     */
    public java.lang.Float getCumulativeDropB() {
        return cumulativeDropB;
    }


    /**
     * Sets the cumulativeDropB value for this LoadFlowResult.
     * 
     * @param cumulativeDropB
     */
    public void setCumulativeDropB(java.lang.Float cumulativeDropB) {
        this.cumulativeDropB = cumulativeDropB;
    }


    /**
     * Gets the cumulativeDropC value for this LoadFlowResult.
     * 
     * @return cumulativeDropC
     */
    public java.lang.Float getCumulativeDropC() {
        return cumulativeDropC;
    }


    /**
     * Sets the cumulativeDropC value for this LoadFlowResult.
     * 
     * @param cumulativeDropC
     */
    public void setCumulativeDropC(java.lang.Float cumulativeDropC) {
        this.cumulativeDropC = cumulativeDropC;
    }


    /**
     * Gets the cumulativeDropBal value for this LoadFlowResult.
     * 
     * @return cumulativeDropBal
     */
    public java.lang.Float getCumulativeDropBal() {
        return cumulativeDropBal;
    }


    /**
     * Sets the cumulativeDropBal value for this LoadFlowResult.
     * 
     * @param cumulativeDropBal
     */
    public void setCumulativeDropBal(java.lang.Float cumulativeDropBal) {
        this.cumulativeDropBal = cumulativeDropBal;
    }


    /**
     * Gets the thruCurrentA value for this LoadFlowResult.
     * 
     * @return thruCurrentA
     */
    public java.lang.Float getThruCurrentA() {
        return thruCurrentA;
    }


    /**
     * Sets the thruCurrentA value for this LoadFlowResult.
     * 
     * @param thruCurrentA
     */
    public void setThruCurrentA(java.lang.Float thruCurrentA) {
        this.thruCurrentA = thruCurrentA;
    }


    /**
     * Gets the thruCurrentB value for this LoadFlowResult.
     * 
     * @return thruCurrentB
     */
    public java.lang.Float getThruCurrentB() {
        return thruCurrentB;
    }


    /**
     * Sets the thruCurrentB value for this LoadFlowResult.
     * 
     * @param thruCurrentB
     */
    public void setThruCurrentB(java.lang.Float thruCurrentB) {
        this.thruCurrentB = thruCurrentB;
    }


    /**
     * Gets the thruCurrentC value for this LoadFlowResult.
     * 
     * @return thruCurrentC
     */
    public java.lang.Float getThruCurrentC() {
        return thruCurrentC;
    }


    /**
     * Sets the thruCurrentC value for this LoadFlowResult.
     * 
     * @param thruCurrentC
     */
    public void setThruCurrentC(java.lang.Float thruCurrentC) {
        this.thruCurrentC = thruCurrentC;
    }


    /**
     * Gets the thruCurrentBal value for this LoadFlowResult.
     * 
     * @return thruCurrentBal
     */
    public java.lang.Float getThruCurrentBal() {
        return thruCurrentBal;
    }


    /**
     * Sets the thruCurrentBal value for this LoadFlowResult.
     * 
     * @param thruCurrentBal
     */
    public void setThruCurrentBal(java.lang.Float thruCurrentBal) {
        this.thruCurrentBal = thruCurrentBal;
    }


    /**
     * Gets the pctCapA value for this LoadFlowResult.
     * 
     * @return pctCapA
     */
    public java.lang.Float getPctCapA() {
        return pctCapA;
    }


    /**
     * Sets the pctCapA value for this LoadFlowResult.
     * 
     * @param pctCapA
     */
    public void setPctCapA(java.lang.Float pctCapA) {
        this.pctCapA = pctCapA;
    }


    /**
     * Gets the pctCapB value for this LoadFlowResult.
     * 
     * @return pctCapB
     */
    public java.lang.Float getPctCapB() {
        return pctCapB;
    }


    /**
     * Sets the pctCapB value for this LoadFlowResult.
     * 
     * @param pctCapB
     */
    public void setPctCapB(java.lang.Float pctCapB) {
        this.pctCapB = pctCapB;
    }


    /**
     * Gets the pctCapC value for this LoadFlowResult.
     * 
     * @return pctCapC
     */
    public java.lang.Float getPctCapC() {
        return pctCapC;
    }


    /**
     * Sets the pctCapC value for this LoadFlowResult.
     * 
     * @param pctCapC
     */
    public void setPctCapC(java.lang.Float pctCapC) {
        this.pctCapC = pctCapC;
    }


    /**
     * Gets the pctCapBal value for this LoadFlowResult.
     * 
     * @return pctCapBal
     */
    public java.lang.Float getPctCapBal() {
        return pctCapBal;
    }


    /**
     * Sets the pctCapBal value for this LoadFlowResult.
     * 
     * @param pctCapBal
     */
    public void setPctCapBal(java.lang.Float pctCapBal) {
        this.pctCapBal = pctCapBal;
    }


    /**
     * Gets the thruKwA value for this LoadFlowResult.
     * 
     * @return thruKwA
     */
    public java.lang.Float getThruKwA() {
        return thruKwA;
    }


    /**
     * Sets the thruKwA value for this LoadFlowResult.
     * 
     * @param thruKwA
     */
    public void setThruKwA(java.lang.Float thruKwA) {
        this.thruKwA = thruKwA;
    }


    /**
     * Gets the thruKwB value for this LoadFlowResult.
     * 
     * @return thruKwB
     */
    public java.lang.Float getThruKwB() {
        return thruKwB;
    }


    /**
     * Sets the thruKwB value for this LoadFlowResult.
     * 
     * @param thruKwB
     */
    public void setThruKwB(java.lang.Float thruKwB) {
        this.thruKwB = thruKwB;
    }


    /**
     * Gets the thruKwC value for this LoadFlowResult.
     * 
     * @return thruKwC
     */
    public java.lang.Float getThruKwC() {
        return thruKwC;
    }


    /**
     * Sets the thruKwC value for this LoadFlowResult.
     * 
     * @param thruKwC
     */
    public void setThruKwC(java.lang.Float thruKwC) {
        this.thruKwC = thruKwC;
    }


    /**
     * Gets the thruKwBal value for this LoadFlowResult.
     * 
     * @return thruKwBal
     */
    public java.lang.Float getThruKwBal() {
        return thruKwBal;
    }


    /**
     * Sets the thruKwBal value for this LoadFlowResult.
     * 
     * @param thruKwBal
     */
    public void setThruKwBal(java.lang.Float thruKwBal) {
        this.thruKwBal = thruKwBal;
    }


    /**
     * Gets the thruKvarA value for this LoadFlowResult.
     * 
     * @return thruKvarA
     */
    public java.lang.Float getThruKvarA() {
        return thruKvarA;
    }


    /**
     * Sets the thruKvarA value for this LoadFlowResult.
     * 
     * @param thruKvarA
     */
    public void setThruKvarA(java.lang.Float thruKvarA) {
        this.thruKvarA = thruKvarA;
    }


    /**
     * Gets the thruKvarB value for this LoadFlowResult.
     * 
     * @return thruKvarB
     */
    public java.lang.Float getThruKvarB() {
        return thruKvarB;
    }


    /**
     * Sets the thruKvarB value for this LoadFlowResult.
     * 
     * @param thruKvarB
     */
    public void setThruKvarB(java.lang.Float thruKvarB) {
        this.thruKvarB = thruKvarB;
    }


    /**
     * Gets the thruKvarC value for this LoadFlowResult.
     * 
     * @return thruKvarC
     */
    public java.lang.Float getThruKvarC() {
        return thruKvarC;
    }


    /**
     * Sets the thruKvarC value for this LoadFlowResult.
     * 
     * @param thruKvarC
     */
    public void setThruKvarC(java.lang.Float thruKvarC) {
        this.thruKvarC = thruKvarC;
    }


    /**
     * Gets the thruKvarBal value for this LoadFlowResult.
     * 
     * @return thruKvarBal
     */
    public java.lang.Float getThruKvarBal() {
        return thruKvarBal;
    }


    /**
     * Sets the thruKvarBal value for this LoadFlowResult.
     * 
     * @param thruKvarBal
     */
    public void setThruKvarBal(java.lang.Float thruKvarBal) {
        this.thruKvarBal = thruKvarBal;
    }


    /**
     * Gets the pctPfA value for this LoadFlowResult.
     * 
     * @return pctPfA
     */
    public java.lang.Float getPctPfA() {
        return pctPfA;
    }


    /**
     * Sets the pctPfA value for this LoadFlowResult.
     * 
     * @param pctPfA
     */
    public void setPctPfA(java.lang.Float pctPfA) {
        this.pctPfA = pctPfA;
    }


    /**
     * Gets the pctPfB value for this LoadFlowResult.
     * 
     * @return pctPfB
     */
    public java.lang.Float getPctPfB() {
        return pctPfB;
    }


    /**
     * Sets the pctPfB value for this LoadFlowResult.
     * 
     * @param pctPfB
     */
    public void setPctPfB(java.lang.Float pctPfB) {
        this.pctPfB = pctPfB;
    }


    /**
     * Gets the pctPfC value for this LoadFlowResult.
     * 
     * @return pctPfC
     */
    public java.lang.Float getPctPfC() {
        return pctPfC;
    }


    /**
     * Sets the pctPfC value for this LoadFlowResult.
     * 
     * @param pctPfC
     */
    public void setPctPfC(java.lang.Float pctPfC) {
        this.pctPfC = pctPfC;
    }


    /**
     * Gets the pctPfBal value for this LoadFlowResult.
     * 
     * @return pctPfBal
     */
    public java.lang.Float getPctPfBal() {
        return pctPfBal;
    }


    /**
     * Sets the pctPfBal value for this LoadFlowResult.
     * 
     * @param pctPfBal
     */
    public void setPctPfBal(java.lang.Float pctPfBal) {
        this.pctPfBal = pctPfBal;
    }


    /**
     * Gets the pctLoss value for this LoadFlowResult.
     * 
     * @return pctLoss
     */
    public java.lang.Float getPctLoss() {
        return pctLoss;
    }


    /**
     * Sets the pctLoss value for this LoadFlowResult.
     * 
     * @param pctLoss
     */
    public void setPctLoss(java.lang.Float pctLoss) {
        this.pctLoss = pctLoss;
    }


    /**
     * Gets the sectKwA value for this LoadFlowResult.
     * 
     * @return sectKwA
     */
    public java.lang.Long getSectKwA() {
        return sectKwA;
    }


    /**
     * Sets the sectKwA value for this LoadFlowResult.
     * 
     * @param sectKwA
     */
    public void setSectKwA(java.lang.Long sectKwA) {
        this.sectKwA = sectKwA;
    }


    /**
     * Gets the sectKwB value for this LoadFlowResult.
     * 
     * @return sectKwB
     */
    public java.lang.Long getSectKwB() {
        return sectKwB;
    }


    /**
     * Sets the sectKwB value for this LoadFlowResult.
     * 
     * @param sectKwB
     */
    public void setSectKwB(java.lang.Long sectKwB) {
        this.sectKwB = sectKwB;
    }


    /**
     * Gets the sectKwC value for this LoadFlowResult.
     * 
     * @return sectKwC
     */
    public java.lang.Long getSectKwC() {
        return sectKwC;
    }


    /**
     * Sets the sectKwC value for this LoadFlowResult.
     * 
     * @param sectKwC
     */
    public void setSectKwC(java.lang.Long sectKwC) {
        this.sectKwC = sectKwC;
    }


    /**
     * Gets the sectKwBal value for this LoadFlowResult.
     * 
     * @return sectKwBal
     */
    public java.lang.Long getSectKwBal() {
        return sectKwBal;
    }


    /**
     * Sets the sectKwBal value for this LoadFlowResult.
     * 
     * @param sectKwBal
     */
    public void setSectKwBal(java.lang.Long sectKwBal) {
        this.sectKwBal = sectKwBal;
    }


    /**
     * Gets the secKvarA value for this LoadFlowResult.
     * 
     * @return secKvarA
     */
    public java.lang.Float getSecKvarA() {
        return secKvarA;
    }


    /**
     * Sets the secKvarA value for this LoadFlowResult.
     * 
     * @param secKvarA
     */
    public void setSecKvarA(java.lang.Float secKvarA) {
        this.secKvarA = secKvarA;
    }


    /**
     * Gets the secKvarB value for this LoadFlowResult.
     * 
     * @return secKvarB
     */
    public java.lang.Float getSecKvarB() {
        return secKvarB;
    }


    /**
     * Sets the secKvarB value for this LoadFlowResult.
     * 
     * @param secKvarB
     */
    public void setSecKvarB(java.lang.Float secKvarB) {
        this.secKvarB = secKvarB;
    }


    /**
     * Gets the secKvarC value for this LoadFlowResult.
     * 
     * @return secKvarC
     */
    public java.lang.Float getSecKvarC() {
        return secKvarC;
    }


    /**
     * Sets the secKvarC value for this LoadFlowResult.
     * 
     * @param secKvarC
     */
    public void setSecKvarC(java.lang.Float secKvarC) {
        this.secKvarC = secKvarC;
    }


    /**
     * Gets the secKvarBal value for this LoadFlowResult.
     * 
     * @return secKvarBal
     */
    public java.lang.Float getSecKvarBal() {
        return secKvarBal;
    }


    /**
     * Sets the secKvarBal value for this LoadFlowResult.
     * 
     * @param secKvarBal
     */
    public void setSecKvarBal(java.lang.Float secKvarBal) {
        this.secKvarBal = secKvarBal;
    }


    /**
     * Gets the consOnA value for this LoadFlowResult.
     * 
     * @return consOnA
     */
    public java.lang.Long getConsOnA() {
        return consOnA;
    }


    /**
     * Sets the consOnA value for this LoadFlowResult.
     * 
     * @param consOnA
     */
    public void setConsOnA(java.lang.Long consOnA) {
        this.consOnA = consOnA;
    }


    /**
     * Gets the consOnB value for this LoadFlowResult.
     * 
     * @return consOnB
     */
    public java.lang.Long getConsOnB() {
        return consOnB;
    }


    /**
     * Sets the consOnB value for this LoadFlowResult.
     * 
     * @param consOnB
     */
    public void setConsOnB(java.lang.Long consOnB) {
        this.consOnB = consOnB;
    }


    /**
     * Gets the consOnC value for this LoadFlowResult.
     * 
     * @return consOnC
     */
    public java.lang.Long getConsOnC() {
        return consOnC;
    }


    /**
     * Sets the consOnC value for this LoadFlowResult.
     * 
     * @param consOnC
     */
    public void setConsOnC(java.lang.Long consOnC) {
        this.consOnC = consOnC;
    }


    /**
     * Gets the consOnBal value for this LoadFlowResult.
     * 
     * @return consOnBal
     */
    public java.lang.Long getConsOnBal() {
        return consOnBal;
    }


    /**
     * Sets the consOnBal value for this LoadFlowResult.
     * 
     * @param consOnBal
     */
    public void setConsOnBal(java.lang.Long consOnBal) {
        this.consOnBal = consOnBal;
    }


    /**
     * Gets the consThruA value for this LoadFlowResult.
     * 
     * @return consThruA
     */
    public java.lang.Long getConsThruA() {
        return consThruA;
    }


    /**
     * Sets the consThruA value for this LoadFlowResult.
     * 
     * @param consThruA
     */
    public void setConsThruA(java.lang.Long consThruA) {
        this.consThruA = consThruA;
    }


    /**
     * Gets the consThruB value for this LoadFlowResult.
     * 
     * @return consThruB
     */
    public java.lang.Long getConsThruB() {
        return consThruB;
    }


    /**
     * Sets the consThruB value for this LoadFlowResult.
     * 
     * @param consThruB
     */
    public void setConsThruB(java.lang.Long consThruB) {
        this.consThruB = consThruB;
    }


    /**
     * Gets the consThruC value for this LoadFlowResult.
     * 
     * @return consThruC
     */
    public java.lang.Long getConsThruC() {
        return consThruC;
    }


    /**
     * Sets the consThruC value for this LoadFlowResult.
     * 
     * @param consThruC
     */
    public void setConsThruC(java.lang.Long consThruC) {
        this.consThruC = consThruC;
    }


    /**
     * Gets the consThruBal value for this LoadFlowResult.
     * 
     * @return consThruBal
     */
    public java.lang.Long getConsThruBal() {
        return consThruBal;
    }


    /**
     * Sets the consThruBal value for this LoadFlowResult.
     * 
     * @param consThruBal
     */
    public void setConsThruBal(java.lang.Long consThruBal) {
        this.consThruBal = consThruBal;
    }


    /**
     * Gets the baseKvCalc value for this LoadFlowResult.
     * 
     * @return baseKvCalc
     */
    public java.lang.Float getBaseKvCalc() {
        return baseKvCalc;
    }


    /**
     * Sets the baseKvCalc value for this LoadFlowResult.
     * 
     * @param baseKvCalc
     */
    public void setBaseKvCalc(java.lang.Float baseKvCalc) {
        this.baseKvCalc = baseKvCalc;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LoadFlowResult)) return false;
        LoadFlowResult other = (LoadFlowResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.priKvA==null && other.getPriKvA()==null) || 
             (this.priKvA!=null &&
              this.priKvA.equals(other.getPriKvA()))) &&
            ((this.priKvB==null && other.getPriKvB()==null) || 
             (this.priKvB!=null &&
              this.priKvB.equals(other.getPriKvB()))) &&
            ((this.priKvC==null && other.getPriKvC()==null) || 
             (this.priKvC!=null &&
              this.priKvC.equals(other.getPriKvC()))) &&
            ((this.priKvBal==null && other.getPriKvBal()==null) || 
             (this.priKvBal!=null &&
              this.priKvBal.equals(other.getPriKvBal()))) &&
            ((this.sectionDropA==null && other.getSectionDropA()==null) || 
             (this.sectionDropA!=null &&
              this.sectionDropA.equals(other.getSectionDropA()))) &&
            ((this.sectionDropB==null && other.getSectionDropB()==null) || 
             (this.sectionDropB!=null &&
              this.sectionDropB.equals(other.getSectionDropB()))) &&
            ((this.sectionDropC==null && other.getSectionDropC()==null) || 
             (this.sectionDropC!=null &&
              this.sectionDropC.equals(other.getSectionDropC()))) &&
            ((this.sectionDropBal==null && other.getSectionDropBal()==null) || 
             (this.sectionDropBal!=null &&
              this.sectionDropBal.equals(other.getSectionDropBal()))) &&
            ((this.cumulativeDropA==null && other.getCumulativeDropA()==null) || 
             (this.cumulativeDropA!=null &&
              this.cumulativeDropA.equals(other.getCumulativeDropA()))) &&
            ((this.cumulativeDropB==null && other.getCumulativeDropB()==null) || 
             (this.cumulativeDropB!=null &&
              this.cumulativeDropB.equals(other.getCumulativeDropB()))) &&
            ((this.cumulativeDropC==null && other.getCumulativeDropC()==null) || 
             (this.cumulativeDropC!=null &&
              this.cumulativeDropC.equals(other.getCumulativeDropC()))) &&
            ((this.cumulativeDropBal==null && other.getCumulativeDropBal()==null) || 
             (this.cumulativeDropBal!=null &&
              this.cumulativeDropBal.equals(other.getCumulativeDropBal()))) &&
            ((this.thruCurrentA==null && other.getThruCurrentA()==null) || 
             (this.thruCurrentA!=null &&
              this.thruCurrentA.equals(other.getThruCurrentA()))) &&
            ((this.thruCurrentB==null && other.getThruCurrentB()==null) || 
             (this.thruCurrentB!=null &&
              this.thruCurrentB.equals(other.getThruCurrentB()))) &&
            ((this.thruCurrentC==null && other.getThruCurrentC()==null) || 
             (this.thruCurrentC!=null &&
              this.thruCurrentC.equals(other.getThruCurrentC()))) &&
            ((this.thruCurrentBal==null && other.getThruCurrentBal()==null) || 
             (this.thruCurrentBal!=null &&
              this.thruCurrentBal.equals(other.getThruCurrentBal()))) &&
            ((this.pctCapA==null && other.getPctCapA()==null) || 
             (this.pctCapA!=null &&
              this.pctCapA.equals(other.getPctCapA()))) &&
            ((this.pctCapB==null && other.getPctCapB()==null) || 
             (this.pctCapB!=null &&
              this.pctCapB.equals(other.getPctCapB()))) &&
            ((this.pctCapC==null && other.getPctCapC()==null) || 
             (this.pctCapC!=null &&
              this.pctCapC.equals(other.getPctCapC()))) &&
            ((this.pctCapBal==null && other.getPctCapBal()==null) || 
             (this.pctCapBal!=null &&
              this.pctCapBal.equals(other.getPctCapBal()))) &&
            ((this.thruKwA==null && other.getThruKwA()==null) || 
             (this.thruKwA!=null &&
              this.thruKwA.equals(other.getThruKwA()))) &&
            ((this.thruKwB==null && other.getThruKwB()==null) || 
             (this.thruKwB!=null &&
              this.thruKwB.equals(other.getThruKwB()))) &&
            ((this.thruKwC==null && other.getThruKwC()==null) || 
             (this.thruKwC!=null &&
              this.thruKwC.equals(other.getThruKwC()))) &&
            ((this.thruKwBal==null && other.getThruKwBal()==null) || 
             (this.thruKwBal!=null &&
              this.thruKwBal.equals(other.getThruKwBal()))) &&
            ((this.thruKvarA==null && other.getThruKvarA()==null) || 
             (this.thruKvarA!=null &&
              this.thruKvarA.equals(other.getThruKvarA()))) &&
            ((this.thruKvarB==null && other.getThruKvarB()==null) || 
             (this.thruKvarB!=null &&
              this.thruKvarB.equals(other.getThruKvarB()))) &&
            ((this.thruKvarC==null && other.getThruKvarC()==null) || 
             (this.thruKvarC!=null &&
              this.thruKvarC.equals(other.getThruKvarC()))) &&
            ((this.thruKvarBal==null && other.getThruKvarBal()==null) || 
             (this.thruKvarBal!=null &&
              this.thruKvarBal.equals(other.getThruKvarBal()))) &&
            ((this.pctPfA==null && other.getPctPfA()==null) || 
             (this.pctPfA!=null &&
              this.pctPfA.equals(other.getPctPfA()))) &&
            ((this.pctPfB==null && other.getPctPfB()==null) || 
             (this.pctPfB!=null &&
              this.pctPfB.equals(other.getPctPfB()))) &&
            ((this.pctPfC==null && other.getPctPfC()==null) || 
             (this.pctPfC!=null &&
              this.pctPfC.equals(other.getPctPfC()))) &&
            ((this.pctPfBal==null && other.getPctPfBal()==null) || 
             (this.pctPfBal!=null &&
              this.pctPfBal.equals(other.getPctPfBal()))) &&
            ((this.pctLoss==null && other.getPctLoss()==null) || 
             (this.pctLoss!=null &&
              this.pctLoss.equals(other.getPctLoss()))) &&
            ((this.sectKwA==null && other.getSectKwA()==null) || 
             (this.sectKwA!=null &&
              this.sectKwA.equals(other.getSectKwA()))) &&
            ((this.sectKwB==null && other.getSectKwB()==null) || 
             (this.sectKwB!=null &&
              this.sectKwB.equals(other.getSectKwB()))) &&
            ((this.sectKwC==null && other.getSectKwC()==null) || 
             (this.sectKwC!=null &&
              this.sectKwC.equals(other.getSectKwC()))) &&
            ((this.sectKwBal==null && other.getSectKwBal()==null) || 
             (this.sectKwBal!=null &&
              this.sectKwBal.equals(other.getSectKwBal()))) &&
            ((this.secKvarA==null && other.getSecKvarA()==null) || 
             (this.secKvarA!=null &&
              this.secKvarA.equals(other.getSecKvarA()))) &&
            ((this.secKvarB==null && other.getSecKvarB()==null) || 
             (this.secKvarB!=null &&
              this.secKvarB.equals(other.getSecKvarB()))) &&
            ((this.secKvarC==null && other.getSecKvarC()==null) || 
             (this.secKvarC!=null &&
              this.secKvarC.equals(other.getSecKvarC()))) &&
            ((this.secKvarBal==null && other.getSecKvarBal()==null) || 
             (this.secKvarBal!=null &&
              this.secKvarBal.equals(other.getSecKvarBal()))) &&
            ((this.consOnA==null && other.getConsOnA()==null) || 
             (this.consOnA!=null &&
              this.consOnA.equals(other.getConsOnA()))) &&
            ((this.consOnB==null && other.getConsOnB()==null) || 
             (this.consOnB!=null &&
              this.consOnB.equals(other.getConsOnB()))) &&
            ((this.consOnC==null && other.getConsOnC()==null) || 
             (this.consOnC!=null &&
              this.consOnC.equals(other.getConsOnC()))) &&
            ((this.consOnBal==null && other.getConsOnBal()==null) || 
             (this.consOnBal!=null &&
              this.consOnBal.equals(other.getConsOnBal()))) &&
            ((this.consThruA==null && other.getConsThruA()==null) || 
             (this.consThruA!=null &&
              this.consThruA.equals(other.getConsThruA()))) &&
            ((this.consThruB==null && other.getConsThruB()==null) || 
             (this.consThruB!=null &&
              this.consThruB.equals(other.getConsThruB()))) &&
            ((this.consThruC==null && other.getConsThruC()==null) || 
             (this.consThruC!=null &&
              this.consThruC.equals(other.getConsThruC()))) &&
            ((this.consThruBal==null && other.getConsThruBal()==null) || 
             (this.consThruBal!=null &&
              this.consThruBal.equals(other.getConsThruBal()))) &&
            ((this.baseKvCalc==null && other.getBaseKvCalc()==null) || 
             (this.baseKvCalc!=null &&
              this.baseKvCalc.equals(other.getBaseKvCalc())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getPriKvA() != null) {
            _hashCode += getPriKvA().hashCode();
        }
        if (getPriKvB() != null) {
            _hashCode += getPriKvB().hashCode();
        }
        if (getPriKvC() != null) {
            _hashCode += getPriKvC().hashCode();
        }
        if (getPriKvBal() != null) {
            _hashCode += getPriKvBal().hashCode();
        }
        if (getSectionDropA() != null) {
            _hashCode += getSectionDropA().hashCode();
        }
        if (getSectionDropB() != null) {
            _hashCode += getSectionDropB().hashCode();
        }
        if (getSectionDropC() != null) {
            _hashCode += getSectionDropC().hashCode();
        }
        if (getSectionDropBal() != null) {
            _hashCode += getSectionDropBal().hashCode();
        }
        if (getCumulativeDropA() != null) {
            _hashCode += getCumulativeDropA().hashCode();
        }
        if (getCumulativeDropB() != null) {
            _hashCode += getCumulativeDropB().hashCode();
        }
        if (getCumulativeDropC() != null) {
            _hashCode += getCumulativeDropC().hashCode();
        }
        if (getCumulativeDropBal() != null) {
            _hashCode += getCumulativeDropBal().hashCode();
        }
        if (getThruCurrentA() != null) {
            _hashCode += getThruCurrentA().hashCode();
        }
        if (getThruCurrentB() != null) {
            _hashCode += getThruCurrentB().hashCode();
        }
        if (getThruCurrentC() != null) {
            _hashCode += getThruCurrentC().hashCode();
        }
        if (getThruCurrentBal() != null) {
            _hashCode += getThruCurrentBal().hashCode();
        }
        if (getPctCapA() != null) {
            _hashCode += getPctCapA().hashCode();
        }
        if (getPctCapB() != null) {
            _hashCode += getPctCapB().hashCode();
        }
        if (getPctCapC() != null) {
            _hashCode += getPctCapC().hashCode();
        }
        if (getPctCapBal() != null) {
            _hashCode += getPctCapBal().hashCode();
        }
        if (getThruKwA() != null) {
            _hashCode += getThruKwA().hashCode();
        }
        if (getThruKwB() != null) {
            _hashCode += getThruKwB().hashCode();
        }
        if (getThruKwC() != null) {
            _hashCode += getThruKwC().hashCode();
        }
        if (getThruKwBal() != null) {
            _hashCode += getThruKwBal().hashCode();
        }
        if (getThruKvarA() != null) {
            _hashCode += getThruKvarA().hashCode();
        }
        if (getThruKvarB() != null) {
            _hashCode += getThruKvarB().hashCode();
        }
        if (getThruKvarC() != null) {
            _hashCode += getThruKvarC().hashCode();
        }
        if (getThruKvarBal() != null) {
            _hashCode += getThruKvarBal().hashCode();
        }
        if (getPctPfA() != null) {
            _hashCode += getPctPfA().hashCode();
        }
        if (getPctPfB() != null) {
            _hashCode += getPctPfB().hashCode();
        }
        if (getPctPfC() != null) {
            _hashCode += getPctPfC().hashCode();
        }
        if (getPctPfBal() != null) {
            _hashCode += getPctPfBal().hashCode();
        }
        if (getPctLoss() != null) {
            _hashCode += getPctLoss().hashCode();
        }
        if (getSectKwA() != null) {
            _hashCode += getSectKwA().hashCode();
        }
        if (getSectKwB() != null) {
            _hashCode += getSectKwB().hashCode();
        }
        if (getSectKwC() != null) {
            _hashCode += getSectKwC().hashCode();
        }
        if (getSectKwBal() != null) {
            _hashCode += getSectKwBal().hashCode();
        }
        if (getSecKvarA() != null) {
            _hashCode += getSecKvarA().hashCode();
        }
        if (getSecKvarB() != null) {
            _hashCode += getSecKvarB().hashCode();
        }
        if (getSecKvarC() != null) {
            _hashCode += getSecKvarC().hashCode();
        }
        if (getSecKvarBal() != null) {
            _hashCode += getSecKvarBal().hashCode();
        }
        if (getConsOnA() != null) {
            _hashCode += getConsOnA().hashCode();
        }
        if (getConsOnB() != null) {
            _hashCode += getConsOnB().hashCode();
        }
        if (getConsOnC() != null) {
            _hashCode += getConsOnC().hashCode();
        }
        if (getConsOnBal() != null) {
            _hashCode += getConsOnBal().hashCode();
        }
        if (getConsThruA() != null) {
            _hashCode += getConsThruA().hashCode();
        }
        if (getConsThruB() != null) {
            _hashCode += getConsThruB().hashCode();
        }
        if (getConsThruC() != null) {
            _hashCode += getConsThruC().hashCode();
        }
        if (getConsThruBal() != null) {
            _hashCode += getConsThruBal().hashCode();
        }
        if (getBaseKvCalc() != null) {
            _hashCode += getBaseKvCalc().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LoadFlowResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadFlowResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priKvA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priKvA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priKvB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priKvB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priKvC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priKvC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priKvBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priKvBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectionDropA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionDropA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectionDropB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionDropB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectionDropC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionDropC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectionDropBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionDropBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cumulativeDropA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumulativeDropA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cumulativeDropB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumulativeDropB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cumulativeDropC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumulativeDropC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cumulativeDropBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumulativeDropBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruCurrentA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruCurrentA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruCurrentB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruCurrentB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruCurrentC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruCurrentC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruCurrentBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruCurrentBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pctCapA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pctCapA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pctCapB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pctCapB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pctCapC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pctCapC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pctCapBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pctCapBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruKwA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruKwA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruKwB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruKwB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruKwC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruKwC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruKwBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruKwBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruKvarA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruKvarA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruKvarB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruKvarB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruKvarC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruKvarC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thruKvarBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "thruKvarBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pctPfA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pctPfA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pctPfB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pctPfB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pctPfC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pctPfC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pctPfBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pctPfBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pctLoss");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pctLoss"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectKwA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectKwA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectKwB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectKwB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectKwC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectKwC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectKwBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectKwBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secKvarA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secKvarA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secKvarB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secKvarB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secKvarC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secKvarC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secKvarBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secKvarBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consOnA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consOnA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consOnB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consOnB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consOnC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consOnC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consOnBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consOnBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consThruA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consThruA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consThruB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consThruB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consThruC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consThruC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consThruBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consThruBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseKvCalc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseKvCalc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
