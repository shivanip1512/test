package com.cannontech.core.dao;

import java.util.List;

import junit.framework.TestCase;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.spring.YukonSpringHook;

public class PointDaoIntTest extends TestCase {
    private PointDao pointDao = (PointDao) YukonSpringHook.getBean("pointDao");
      
    public void testGetLitePoint() {
        LitePoint p = pointDao.getLitePoint(0);
        assertNotNull("Failed to load the system point", p);
    }

    public void testGetLitePoints() {
        List<LitePoint> p = pointDao.getLitePoints(new Integer[] { 0, -100, 100 });
        assertTrue("Failed to load system points", p.size()>=2);
    }

    public void testGetNextPointId() {
        int id = pointDao.getNextPointId();
        assertTrue("Failed to load next point id", id >=0);
    }
    
    public void testGetNextPointIds() {
        int num = 100;
        int[] ids = pointDao.getNextPointIds(num);
        assertTrue("Failed to load the next " + num + " point ids", ids.length==100);
    }

    public void testGetLitePointsBy() {
        List<LitePoint> p = pointDao.getLitePointsBy(new Integer[] {PointTypes.SYSTEM_POINT}, null, new Integer[] {DeviceTypes.SYSTEM}, null, null);
        assertTrue("Failed to load systems points", p.size() > 0);
    }

    public void testGetLitePointsByNumStates() {
        List<LitePoint> p = pointDao.getLitePointsByNumStates(2);
    }
    
    public void testGetLitePointsByPaObjectId() {
        List<LitePoint> p = pointDao.getLitePointsByPaObjectId(0);
        assertTrue("Failed to load system points off system device", p.size()>0);
    }
    
    public void testGetAllPointIDSAndTypesForPAObject() {
        int[][] idsAndTypes = pointDao.getAllPointIDsAndTypesForPAObject(0);
        assertTrue("Failed to load ids and types of points off system device", idsAndTypes != null);
    }

    public void testGetPointName() {
        String name = pointDao.getPointName(0);
        assertTrue("Failed to load the name of the system point", name.equals("System Point"));
    }
    
    public void testGetLitePointLimit() {
        
    }

    public void testLitePointUnit() {
        //LitePointUnit getPointUnit(int pointID);
    }

    public void testGetLiteStateGroup() {
        LiteStateGroup lsg = pointDao.getStateGroup(0);
        assertNotNull("Failed to load system state group", lsg);
    }

    public void testGetPointIDByDeviceID_Offset_PointType() {
        int id = pointDao.getPointIDByDeviceID_Offset_PointType(0, 0, PointTypes.SYSTEM_POINT);
    }
 
    public void testGetPointData() {
        List<LiteRawPointHistory> lrphList = pointDao.getPointData(0,null,null);
    }

    public void testGetCapBankMonitorPoints() { 
        //hmm
    }

    public void testGetPointOffset() {       
    }
       
    public void testGetPointMultiplier() {
        //double d = getPointMultiplier(0
    }
}
