package com.cannontech.multispeak.data.v4;

import java.util.Date;
import java.util.List;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.spring.YukonSpringHook;

/**
 * The base class for all BillableDevices
 */
public abstract class MeterReadBase implements ReadableDevice{

    private MeterReading meterReading;
    private boolean populated = false;
    
    @Override
    public MeterReading getMeterReading(){
        if( meterReading == null) {
            meterReading = new MeterReading();
        }
        return meterReading;
    }
    
    @Override
    public void setMeterNumber(String meterNumber) {
        MeterID meterId = new MeterID();
        meterId.setMeterNo(meterNumber);
        getMeterReading().setMeterID(meterId);
        getMeterReading().setObjectID(meterNumber);
        getMeterReading().setDeviceID(meterNumber);
        getMeterReading().setUtility(MultispeakDefines.AMR_VENDOR);
    }
    
    @Override
    public boolean isPopulated()
    {
        return populated;
    }
    
    //TODO create classes for its implementation
    @Override
    abstract public void populate(PointIdentifier pointIdentifier, Date dateTime, Double value);
    
    @Override
    public void populateWithPointData(int deviceID) {
        List<LitePoint> litePoints = (YukonSpringHook.getBean("pointDao", PointDao.class)).getLitePointsByPaObjectId(deviceID);
        AsyncDynamicDataSource dds = YukonSpringHook.getBean("asyncDynamicDataSource", AsyncDynamicDataSource.class);
        
        for (LitePoint litePoint : litePoints) {
            PointValueQualityHolder pointData = dds.getPointValue(litePoint.getPointID());
            if( pointData != null && pointData.getPointQuality() != PointQuality.Uninitialized) {
                populate(PointIdentifier.createPointIdentifier(litePoint), pointData.getPointDataTimeStamp(), pointData.getValue());
            }
        }
    }

}
