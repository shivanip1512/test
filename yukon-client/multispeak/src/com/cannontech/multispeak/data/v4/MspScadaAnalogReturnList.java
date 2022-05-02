package com.cannontech.multispeak.data.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.multispeak.data.MspReturnList;

public class MspScadaAnalogReturnList extends MspReturnList {

    private List<ScadaAnalog> scadaAnalogs;

    public List<ScadaAnalog> getScadaAnalogs() {
        return scadaAnalogs;
    }

    public void setScadaAnalogs(List<ScadaAnalog> scadaAnalogs) {
        this.scadaAnalogs = scadaAnalogs;
    }

}
