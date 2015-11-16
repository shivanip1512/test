package com.cannontech.web.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.google.common.collect.Multimap;

/**
 * Mock PointDao class for testing purposes. The methods in this class return
 * static value expected for unit testing
 */
public class MockPointDao implements PointDao {

    /**
     * This method will return a point based on a fake id (ids 1 thru 4 are
     * valid)
     */
    @Override
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

    @Override
    public LitePoint getLitePointIdByDeviceId_Offset_PointType(int deviceId, int pointOffset, int pointType)
            throws NotFoundException {

        int pointId = getPointIDByDeviceID_Offset_PointType(deviceId, pointOffset, pointType);
        if (pointId == PointTypes.SYS_PID_SYSTEM) {
            throw new NotFoundException("nope");
        }
        return getLitePoint(pointId);
    }

    @Override
    public List<LitePoint> getLitePointsByPaObjectId(int paObjectId) {
        List<LitePoint> pointList = new ArrayList<LitePoint>();
        for (int x = 1; x < 5; x++) {
            pointList.add(getLitePoint(x));
        }
        return pointList;
    }

    /**
     * This method will return a fake point id that is just the point type or
     * the point offset if type is 2
     */
    @Override
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

    @Override
    public List<LitePoint> getLitePoints(Iterable<Integer> pointIds) {
        return null;
    }

    public int getMaxPointID() {
        return 0;
    }

    @Override
    public int getPointId(PaoPointIdentifier paoPointIdentifier) {
        return 0;
    }

    @Override
    public java.util.List<PointBase> getPointsForPao(int paoId) {
        return null;
    }

    @Override
    public int getNextPointId() {
        return 0;
    }

    @Override
    public int[] getNextPointIds(int count) {
        return null;
    }

    @Override
    public List<LitePoint> getLitePointsBy(List<PointType> pointTypes) {
        return null;
    }

    @Override
    public int[][] getAllPointIDsAndTypesForPAObject(int deviceid) {
        return null;
    }

    @Override
    public String getPointName(int id) {
        return null;
    }

    @Override
    public LitePointLimit getPointLimit(int pointID) {
        return null;
    }

    @Override
    public LitePointUnit getPointUnit(int pointID) {
        return null;
    }

    @Override
    public LiteStateGroup getStateGroup(int stateGroupID) {
        return null;
    }

    @Override
    public List<CapBankMonitorPointParams> getCapBankMonitorPoints(CapBank capBank) {
        return null;
    }

    @Override
    public List<LitePoint> getLitePointIdByDeviceId_PointType(int deviceId, int pointType) throws NotFoundException {
        return null;
    }

    @Override
    public boolean deviceHasPoint(int deviceId, int pointOffset, int pointType) {
        return false;
    }

    @Override
    public PaoPointIdentifier getPaoPointIdentifier(int pointId) {
        return null;
    }

    @Override
    public LitePoint getLitePoint(PaoPointIdentifier paoPointIdentifier) {
        return null;
    }

    @Override
    public Map<PaoPointIdentifier, PointInfo> getPointInfoById(Iterable<PaoPointIdentifier> paoPointIdentifiers) {
        return null;
    }

    @Override
    public Map<PaoIdentifier, PointInfo> getPointInfoByPointName(Iterable<PaoIdentifier> paoIdentifiers,
            String pointName) {
        return null;
    }

    @Override
    public Map<PaoIdentifier, PointInfo> getPointInfoByDefaultName(Iterable<PaoIdentifier> paoIdentifiers,
            String defaultName) {
        return null;
    }

    @Override
    public Map<PaoIdentifier, PointInfo> getPointInfoByPointIdentifier(Iterable<PaoIdentifier> paoIdentifiers,
            PointIdentifier pointIdentifier) {
        return null;
    }

    @Override
    public LitePoint findPointByName(YukonPao pao, String pointName) {
        return null;
    }

    @Override
    public Map<Integer, PointInfo> getPointInfoByPointIds(Iterable<Integer> pointIds) {
        return null;
    }

    @Override
    public Map<LitePoint, PaoPointIdentifier> getLitePointsForPaoPointIdentifiers(
            Iterable<PaoPointIdentifier> paoPointIdentifiers) {
        return null;
    }

    @Override
    public SqlFragmentSource getAttributeLookupSql(Attribute attribute) {
        return null;
    }

    @Override
    public SqlFragmentSource getAttributeLookupSqlLimit(Attribute attribute, int limitToRowCount) {
        return null;
    }

    @Override
    public Multimap<Integer, Integer> getPaoPointMultimap(Iterable<Integer> paoIds) {
        return null;
    }

    @Override
    public List<LitePointLimit> getAllPointLimits() {
        return null;
    }

    @Override
    public List<LitePoint> findAllPointsWithName(String pointName) {
        return null;
    }

    @Override
    public PointBase get(int id) {
        return null;
    }

    @Override
    public Map<PointType, List<PointInfo>> getAllPointNamesAndTypesForPAObject(int paobjectId) {
        return null;
    }
}