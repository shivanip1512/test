package com.cannontech.multispeak.data;

import java.util.List;

import com.cannontech.multispeak.deploy.service.ScadaAnalog;

public class MspScadaAnalogReturnList extends MspReturnList {

    private List<ScadaAnalog> scadaAnalogs;

    public List<ScadaAnalog> getScadaAnalogs() {
        return scadaAnalogs;
    }
    public void setScadaAnalogs(List<ScadaAnalog> scadaAnalogs) {
        this.scadaAnalogs = scadaAnalogs;
    }
}
