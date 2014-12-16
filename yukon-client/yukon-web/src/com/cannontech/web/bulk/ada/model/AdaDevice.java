package com.cannontech.web.bulk.ada.model;

import java.util.Comparator;

import org.apache.commons.lang.ObjectUtils;

import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;

public class AdaDevice implements YukonPao {
    public static final Comparator<AdaDevice> ON_NAME = new Comparator<AdaDevice>() {
        @Override
        public int compare(AdaDevice o1, AdaDevice o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };
    
    public static final Comparator<AdaDevice> ON_PAO_TYPE = new Comparator<AdaDevice>() {
        @Override
        public int compare(AdaDevice o1, AdaDevice o2) {
            return o1.getPaoIdentifier().getPaoType().name().compareTo(o2.getPaoIdentifier().getPaoType().name());
        }
    };
    
    public static final Comparator<AdaDevice> ON_METER_NUMBER = new Comparator<AdaDevice>() {
        @Override
        public int compare(AdaDevice o1, AdaDevice o2) {
            return ObjectUtils.compare(o1.getMeterNumber(), o2.getMeterNumber());
        }
    };
    
    public static final Comparator<AdaDevice> ON_MISSING_INTERVALS = new Comparator<AdaDevice>() {
        @Override
        public int compare(AdaDevice o1, AdaDevice o2) {
            return Integer.compare(o1.getMissingIntervals(), o2.getMissingIntervals());
        }
    };
    
    private DeviceArchiveData data;
    private String name;
    private String meterNumber;
    private int missingIntervals;
    
    public DeviceArchiveData getData() {
        return data;
    }
    
    public void setData(DeviceArchiveData data) {
        this.data = data;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMeterNumber() {
        return meterNumber;
    }
    
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    
    public int getMissingIntervals() {
        return missingIntervals;
    }
    
    public void setMissingIntervals(int missingIntervals) {
        this.missingIntervals = missingIntervals;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return data.getPaoIdentifier();
    }
    
}