package com.cannontech.database.data.lite;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.spring.YukonSpringHook;

public class LiteDeviceMeterNumber extends LiteBase {
    
    private String meterNumber;
    private PaoType paoType;
    
    public LiteDeviceMeterNumber() {
        super();
    }
    
    public LiteDeviceMeterNumber(int devID) {
        super();
        setDeviceID(devID);
        setLiteType(LiteTypes.DEVICE_METERNUMBER);
    }
    
    public LiteDeviceMeterNumber(int devID, String meterNum, PaoType paoType) {
        super();
        setDeviceID(devID);
        setMeterNumber(meterNum);
        setLiteType(LiteTypes.DEVICE_METERNUMBER);
        setPaoType(paoType);
    }
    
    public int getDeviceID() {
        return getLiteID();
    }
    
    public void setDeviceID(int deviceId) {
        setLiteID(deviceId);
    }
    
    public String getMeterNumber() {
        return meterNumber;
    }
    
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    
    public PaoType getPaoType() {
        
        if (paoType == null) {
            // Hope we never get here, but protection in case we do.
            // LiteDeviceMeterNumber has paoType, but (heavy) DeviceMeterGroup does not.
            PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
            paoType = paoDao.getYukonPao(getDeviceID()).getPaoIdentifier().getPaoType();
        }
        
        return paoType;
    }
    
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
    
    public String toString() {
        return meterNumber;
    }
    
    /**
     * I can be equal to other Lite PAO stuff
     * 
     * @return boolean
     */
    public boolean equals(Object o) {
        if (o instanceof LiteYukonPAObject) {
            return (((LiteBase) o).getLiteID() == this.getLiteID());
        } else {
            return super.equals(o);
        }
    }

}