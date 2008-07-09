package com.cannontech.multispeak.data;

import java.util.Date;
import java.util.List;

import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
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

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.data.ReadableDevice#getMeterRead()
     */
    public MeterRead getMeterRead(){
        if( meterRead == null)
            meterRead = new MeterRead();
        return meterRead;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.multispeak.data.ReadableDevice#setMeterNumber(java.lang.String)
     */
    public void setMeterNumber(String meterNumber) {
        getMeterRead().setMeterNo(meterNumber);
        getMeterRead().setObjectID(meterNumber);
        getMeterRead().setDeviceID(meterNumber);
        getMeterRead().setUtility(MultispeakDefines.AMR_VENDOR);
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.multispeak.data.ReadableDevice#populate(int, int, int, java.util.Date, java.lang.Double)
     */
    abstract public void populate(int pointType, int pointOffSet, int uomID, Date dateTime, Double value);

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.data.ReadableDevice#isPopulated()
     */
    public boolean isPopulated()
    {
        return populated;
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.data.ReadableDevice#setPopulated(boolean)
     */
    public void setPopulated(boolean value)
    {
        populated = value;        
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.multispeak.data.ReadableDevice#populateWithPointData(int)
     */
    public void populateWithPointData(int deviceID) {
        List<LitePoint> litePoints = (YukonSpringHook.getBean("pointDao", PointDao.class)).getLitePointsByPaObjectId(deviceID);
        DynamicDataSource dds = YukonSpringHook.getBean("dynamicDataSource", DynamicDataSource.class);
        
        for (LitePoint litePoint : litePoints) {
            PointValueHolder pointData = dds.getPointValue(litePoint.getPointID());
            if( pointData != null)
                populate(litePoint.getPointType(), litePoint.getPointOffset(), litePoint.getUofmID(), pointData.getPointDataTimeStamp(), pointData.getValue());
        }
    }
}
