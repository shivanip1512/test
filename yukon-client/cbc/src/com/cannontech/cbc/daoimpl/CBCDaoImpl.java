package com.cannontech.cbc.daoimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.cbc.dao.CBCDao;
import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.CBCPointTimestampParams;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.cbc.CBCUtils;

public class CBCDaoImpl  implements CBCDao{
    private PointDao pointDao;
    private PaoDao paoDao;
    private AuthDao authDao;
    public CBCDaoImpl() {
        super();
    }



    /* (non-Javadoc)
     * @see com.cannontech.cbc.daoimpl.CBCDao#getCBCPointTimeStamps(java.lang.Integer)
     */
    public List getCBCPointTimeStamps (Integer cbcID) {
        List pointList = new ArrayList (10);
        PointChangeCache pointCache = PointChangeCache.getPointChangeCache();
        
        List points = pointDao.getLitePointsByPaObjectId(cbcID.intValue());
        for (int i = 0; i < points.size(); i++) {       
            CBCPointTimestampParams pointTimestamp = new CBCPointTimestampParams();
            LitePoint point = (LitePoint) points.get(i);
            if (point != null) {
                pointTimestamp.setPointId(new Integer (point.getLiteID()));
                pointTimestamp.setPointName(point.getPointName());
            }
            //wait for the point data to initialize
            PointData pointData = new PointData();
            if (pointCache.getValue(point.getLiteID()) != null )
                pointData = ((PointData)pointCache.getValue(point.getLiteID()));
            
            if ( pointData.getType() == PointTypes.STATUS_POINT )
            {
                
                pointTimestamp.setValue (pointCache.getState(point.getLiteID(), pointData.getValue()));
            }
            else 
            {
                pointTimestamp.setValue(new Double ( pointData.getValue() ).toString() );
            }
            
            if (!pointData.getPointDataTimeStamp().equals( CBCUtils.getDefaultStartTime()) )
                pointTimestamp.setTimestamp(pointData.getPointDataTimeStamp());     
            pointList.add(pointTimestamp);
        }
        return pointList;
        }
    
    /* (non-Javadoc)
     * @see com.cannontech.cbc.daoimpl.CBCDao#getAllSubsForUser(com.cannontech.database.data.lite.LiteYukonUser)
     */
    public List getAllSubsForUser(LiteYukonUser user) {
        List subList = new ArrayList(10);
        
        List temp = paoDao.getAllCapControlSubBuses();
        for (Iterator iter = temp.iterator(); iter.hasNext();) {
            LiteYukonPAObject element = (LiteYukonPAObject) iter.next();
            
            if (authDao.userHasAccessPAO(user, element.getLiteID())) {
                subList.add(element);
            }
        }
        return subList;
    }



    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }



    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }



    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }


}
