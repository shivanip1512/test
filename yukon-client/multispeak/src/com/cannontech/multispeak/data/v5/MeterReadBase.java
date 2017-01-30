package com.cannontech.multispeak.data.v5;

import java.util.Date;
import java.util.List;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.multispeak.MeterReading;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.spring.YukonSpringHook;

/**
 * The base class for all BillableDevices
 */
public abstract class MeterReadBase implements ReadableDevice{

    private MeterReading meterRead;
    private boolean populated = false;

    @Override
    public MeterReading getMeterRead(){
        if( meterRead == null) {
            meterRead = new MeterReading();
        }
        return meterRead;
    }
    
    @Override
    public void setMeterNumber(String meterNumber) {
        MeterID meterId = new MeterID();
        meterId.setMeterName(meterNumber);
        meterId.setUtility(MultispeakDefines.AMR_VENDOR);
        meterId.setRegisteredName(MultispeakDefines.REGISTERED_NAME);
        meterId.setSystemName(MultispeakDefines.APPNAME);
        getMeterRead().setMeterID(meterId );
        getMeterRead().setDeviceID(meterNumber);
    }
    
    @Override
    abstract public void populate(PointIdentifier pointIdentifier, Date dateTime, Double value);

    @Override
    public boolean isPopulated()
    {
        return populated;
    }

    @Override
    public void setPopulated(boolean value)
    {
        populated = value;        
    }
    
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
