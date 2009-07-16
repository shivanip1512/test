package com.cannontech.multispeak.data;

import java.util.Date;
import java.util.List;

import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.spring.YukonSpringHook;

/**
 * The base class for all BillableDevices
 */
public abstract class MeterReadBase implements ReadableDevice{

    private MeterRead meterRead;
    private boolean populated = false;

    @Override
    public MeterRead getMeterRead(){
        if( meterRead == null)
            meterRead = new MeterRead();
        return meterRead;
    }
    
    @Override
    public void setMeterNumber(String meterNumber) {
        getMeterRead().setMeterNo(meterNumber);
        getMeterRead().setObjectID(meterNumber);
        getMeterRead().setDeviceID(meterNumber);
        getMeterRead().setUtility(MultispeakDefines.AMR_VENDOR);
    }
    
    @Override
    abstract public void populate(int pointType, int pointOffSet, int uomID, Date dateTime, Double value);

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
        DynamicDataSource dds = YukonSpringHook.getBean("dynamicDataSource", DynamicDataSource.class);
        
        for (LitePoint litePoint : litePoints) {
            PointValueQualityHolder pointData = dds.getPointValue(litePoint.getPointID());
            if( pointData != null && pointData.getPointQuality().getQuality() != PointQuality.Uninitialized.getQuality())
                populate(litePoint.getPointType(), litePoint.getPointOffset(), litePoint.getUofmID(), pointData.getPointDataTimeStamp(), pointData.getValue());
        }
    }
}
