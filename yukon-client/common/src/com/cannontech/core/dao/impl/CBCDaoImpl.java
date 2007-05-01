package com.cannontech.core.dao.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.CBCDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.CBCPointTimestampParams;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.cbc.CBCUtils;

public class CBCDaoImpl  implements CBCDao{




    private PointDao pointDao;
    private PaoDao paoDao;
    private AuthDao authDao;
    private StateDao stateDao;
    private DynamicDataSource dynamicDataSource;
    private JdbcTemplate jdbcOps;
    private IDatabaseCache databaseCache;
    


    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }



    public CBCDaoImpl() {
        super();
    }



    /* (non-Javadoc)
     * @see com.cannontech.cbc.daoimpl.CBCDao#getCBCPointTimeStamps(java.lang.Integer)
     */
    public List getCBCPointTimeStamps (Integer cbcID) {
        List pointList = new ArrayList (10);
        
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
            try {
                pointData = dynamicDataSource.getPointData(point.getLiteID());
            }
            catch(DynamicDataAccessException ddae) {
                // Should this code really just use a default pointdata if one couldn't
                // be found for this point id??
            }
            
            if ( pointData.getType() == PointTypes.STATUS_POINT )
            {
                LiteState currentState = stateDao.getLiteState(point.getStateGroupID(), (int) pointData.getValue());
                String stateText = currentState.getStateText();
                pointTimestamp.setValue(stateText);
            }
            else 
            {
                Double analogVal = new Double ( pointData.getValue() );
                
                ScalarPoint persPoint = (ScalarPoint) LiteFactory.convertLiteToDBPers(point);
                Integer decimalPlaces = persPoint.getPointUnit().getDecimalPlaces();
                
                DecimalFormat formater = new DecimalFormat();
                formater.setMaximumFractionDigits(decimalPlaces);
                String format = formater.format(analogVal.doubleValue());
                pointTimestamp.setValue(format);
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
    
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    public void setJdbcOps(JdbcTemplate jdbcOps) {
        this.jdbcOps = jdbcOps;
    }



    public List<LitePoint> getPaoPoints(YukonPAObject pao) {
        return  pointDao.getLitePointsByPaObjectId(pao.getPAObjectID());
    }



    public Integer getParentForController(int id) {
        String sql = "select deviceid from capbank where controldeviceid = ?";
        Integer parentID = 0;
        try{
            parentID = jdbcOps.queryForInt(sql, new Integer[] {id});
        }
        catch (DataAccessException dae)
        {
            CTILogger.debug("Could not find parent for cbc:" + id);
        }
        return parentID;
    }



    public Integer getParentForPoint(int id) {
        String sql = "select paobjectid from point where pointid = ?";
        Integer parentID = 0;
        try{
            parentID = jdbcOps.queryForInt(sql, new Integer[] {id});
        }
        catch (DataAccessException dae)
        {
            CTILogger.debug("Could not find parent for cbc:" + id);
        }
        return parentID;
    }
    
    public Integer getRoleID (Integer rolePropID) {
        List<LiteYukonRoleProperty> roleProps = Collections.EMPTY_LIST;
        synchronized (databaseCache) {
            roleProps   = databaseCache.getAllYukonRoleProperties();
        }        
        for (LiteYukonRoleProperty property : roleProps) {
            if (property.getRolePropertyID() == rolePropID.intValue())
            {
                return property.getRoleID();
            }
        }
        throw new NotFoundException ("Role ID Could not be found");
    }
}

