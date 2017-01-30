package com.cannontech.multispeak.data.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;


public class MspScadaAnalogReturnList extends MspReturnList {

    private List<SCADAAnalog> scadaAnalogs;

    public List<SCADAAnalog> getScadaAnalogs() {
        return scadaAnalogs;
    }
    public void setScadaAnalogs(List<SCADAAnalog> scadaAnalogs) {
        this.scadaAnalogs = scadaAnalogs;
    }
}
