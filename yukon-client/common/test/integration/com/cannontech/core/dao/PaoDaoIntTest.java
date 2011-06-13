package com.cannontech.core.dao;

import java.util.List;

import junit.framework.TestCase;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class PaoDaoIntTest extends TestCase {
    PaoDao paoDao = (PaoDao) YukonSpringHook.getBean("paoDao");
    private IDatabaseCache databaseCache = (IDatabaseCache) YukonSpringHook.getBean("databaseCache");
    
    public void testGetLiteYukonPao() {
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(0);
        assertNotNull("Failed to load the system device", pao);
    }
    
    public void testGetLiteYukonPAObjectByType() {
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPAObjectByType(DeviceTypes.SYSTEM);
        assertTrue("Failed to load a system device by type", paos.size() > 0);
    }
    
    public void testGetLiteYukonPAOObjectBy() {
        List<LiteYukonPAObject> paos = 
            paoDao.getLiteYukonPAObjectBy(null, null,null,null,null); 
        assertTrue("Failed to load any paos", paos.size() > 0);
    }
    
    public void testGetAllCapControlSubBuses() {
        List ccSubBuses =  paoDao.getAllCapControlSubBuses();
    }

    public void testNextPaoId() {
        int id = paoDao.getNextPaoId();
        assertTrue("Failed to load next pao id", id >= 0);
    }
    
    public void testGetNextPaoIds() {
        int numIds = 100;
        int[] ids = paoDao.getNextPaoIds(numIds);
        assertTrue("Failed to load next " + numIds + " pao ids", ids.length == numIds);
    }

    public void testGetYukonPAOName() {
        String name = paoDao.getYukonPAOName(0);
        assertEquals("Failed to load system device name", "System Device", name);
    }

    public void testGetAllLiteRoutes() {
        LiteYukonPAObject[] routes = paoDao.getAllLiteRoutes();
    }

    public void testGetRoutesByType() {
        LiteYukonPAObject[] routes = paoDao.getRoutesByType(new int[] {RouteTypes.ROUTE_CCU});
    }


    public void testGetAllUnusedCCPAOs() {
        LiteYukonPAObject[] ccPaos = paoDao.getAllUnusedCCPAOs(-100);
    }

    public void testCountLiteYukonPaoByName() {
        int num = paoDao.countLiteYukonPaoByName("System Device", false);
        assertEquals("Failed to load the system device by exact name match", num, 1);
        num = paoDao.countLiteYukonPaoByName("Sys", true);
        assertTrue("Failed to load the system device by fuzzy name match", num > 0);
        num = paoDao.countLiteYukonPaoByName("NoWayADeviceHasThisName", false);
        assertTrue("Matched a device that can't possibly be in the database", num == 0);
    }
    
    public void testGetLiteYukonPaoByName() {
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName("System Device", false);
        assertTrue("Failed to load the system device by exact name match", paos.get(0).getPaoName().equals("System Device"));
        paos = paoDao.getLiteYukonPaoByName("Sys", true);
        assertTrue("Failed to load the system device by fuzzy name match", paos.get(0).getPaoName().contains("Sys"));
        paos = paoDao.getLiteYukonPaoByName("NoWayADeviceHasThisName", false);
        assertTrue("Matched a device that can't possibly be in the database", paos.size() == 0);
    }


}
