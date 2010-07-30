package com.cannontech.amr.crf.message;

import java.io.Serializable;

public class CrfMeterReadingArchiveRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private CrfMeterReadingData data;
    
    public void setData(CrfMeterReadingData data) {
        this.data = data;
    }
    
    public CrfMeterReadingData getData() {
        return data;
    }
}