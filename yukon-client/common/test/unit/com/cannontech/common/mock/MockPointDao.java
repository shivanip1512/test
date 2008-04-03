package com.cannontech.common.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointTypes;

/**
 * Mock PointDao class for testing purposes. The methods in this class return
 * static value expected for unit testing
 */
public class MockPointDao implements PointDao {

    /**
     * This method will return a point based on a fake id (ids 1 thru 4 are
     * valid)
     */
    public LitePoint getLitePoint(int pointID) {

        switch (pointID) {

        case 1:
            return new LitePoint(1, "analog1", 1, 1, 1, 0, 0, 1);
        case 2:
            return new LitePoint(1, "pulse1", 2, 1, 2, 0, 0, 1);
        case 3:
            return new LitePoint(1, "demand1", 3, 1, 1, 0, 0, 0);
        case 4:
            return new LitePoint(1, "pulse2", 2, 1, 4, 0, 0, 0);
        }
        throw new NotFoundException("point not found");
    }

    public List<LitePoint> getLitePoints(Integer[] pointIds) {
        return null;
    }

    public int getMaxPointID() {
        return 0;
    }

    public int getNextPointId() {
        return 0;
    }

    public int[] getNextPointIds(int count) {
        return null;
    }
    
    public LitePoint getLitePointIdByDeviceId_Offset_PointType(int deviceId, int pointOffset, int pointType) throws NotFoundException {
        
        int pointId = getPointIDByDeviceID_Offset_PointType(deviceId, pointOffset, pointType);
        if (pointId == PointTypes.SYS_PID_SYSTEM) {
            throw new NotFoundException("nope");
        } else {
            return getLitePoint(pointId);
        }
    }

    public List<LitePoint> getLitePointsBy(Integer[] pointTypes, Integer[] uomIDs,
            Integer[] paoTypes, Integer[] paoCategories, Integer[] paoClasses) {
        return null;
    }

    public List<LitePoint> getLitePointsByNumStates(int numStates) {
        return null;
    }

    public List<LitePoint> getLitePointsByPaObjectId(int paObjectId) {
        List<LitePoint> pointList = new ArrayList<LitePoint>();
        for (int x = 1; x < 5; x++) {
            pointList.add(getLitePoint(x));
        }
        return pointList;
    }

    public int[][] getAllPointIDsAndTypesForPAObject(int deviceid) {
        return null;
    }

    public String getPointName(int id) {
        return null;
    }

    public LitePointLimit getPointLimit(int pointID) {
        return null;
    }

    public LitePointUnit getPointUnit(int pointID) {
        return null;
    }

    public LiteStateGroup getStateGroup(int stateGroupID) {
        return null;
    }

    /**
     * This method will return a fake point id that is just the point type or
     * the point offset if type is 2
     */
    public int getPointIDByDeviceID_Offset_PointType(int deviceID, int pointOffset, int pointType) {

        switch (pointType) {

        case 1:
            return 1;
        case 2:
            return pointOffset;
        case 3:
            return 3;
        }

        return PointTypes.SYS_PID_SYSTEM;
    }

    public List<LiteRawPointHistory> getPointData(int pointID, Date startDate, Date stopDate) {
        return null;
    }

    public List getCapBankMonitorPoints(CapBank capBank) {
        return null;
    }

    public int getPointDataOffset(int pointId) {
        return 0;
    }

    public double getPointMultiplier(int pointId) {
        return 0;
    }

	public List<LitePoint> searchByName(String name, String paoClass) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public List<LitePoint> getLitePointIdByDeviceId_PointType(int deviceId,
            int pointType) throws NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

}