package com.cannontech.database.db.point;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.model.UserPageType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class Point extends DBPersistent {
    public static final Character PSEUDOFLAG_PSEUDO = 'P';
    public static final Character PSEUDOFLAG_REAL = 'R';
    public static final Character PSEUDOFLAG_SYSTEM = 'S';

    private Integer pointID = null;
    private String pointType = null;
    private String pointName = null;
    private Integer paoID = null;
    private String logicalGroup = PointLogicalGroups.DEFAULT.getDbValue();
    private Integer stateGroupID = null;
    private Character serviceFlag = null;
    private Character alarmInhibit = null;
    // private Character pseudoFlag = null; This is a derived attribute
    private Integer pointOffset = null;
    private PointArchiveType archiveType = null;
    private Integer archiveInterval = null;
    private boolean physicalOffset = true;
    
    /* TODO Can be removed with PointForm.java */
    private boolean archiveStatusData = false;

    public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };

    public static final String SETTER_COLUMNS[] = {
        "PointType",
        "PointName",
        "PAObjectID",
        "LogicalGroup",
        "StateGroupID",
        "ServiceFlag",
        "AlarmInhibit",
        "PseudoFlag",
        "PointOffset",
        "ArchiveType",
        "ArchiveInterval"
    };

    public final static String TABLE_NAME = "Point";

    public Point() {}

    public Point(Integer pointID, String pointType, String pointName, Integer newPaoID, String logicalGroup,
            Integer stateGroupID, Character serviceFlag, Character alarmInhibit, Integer pointOffset, 
            PointArchiveType archiveType, Integer archiveInterval) {

        initialize(pointID, pointType, pointName, newPaoID, logicalGroup, stateGroupID, serviceFlag, alarmInhibit,
            pointOffset, archiveType, archiveInterval);
    }

    @Override
    public void add() throws SQLException {
        Object addValues[] =
            { getPointID(), getPointType(), getPointName(), getPaoID(), getLogicalGroup(), getStateGroupID(),
                getServiceFlag(), getAlarmInhibit(), getPseudoFlag(), getPointOffset(), getArchiveType().getDatabaseRepresentation(),
                getArchiveInterval() };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID());

        UserPageDao userPageDao = YukonSpringHook.getBean(UserPageDao.class);
        userPageDao.deleteUserPages(getPointID(), UserPageType.POINT);
    }

    public Character getAlarmInhibit() {
        return alarmInhibit;
    }

    public Integer getArchiveInterval() {
        return archiveInterval;
    }

    public PointArchiveType getArchiveType() {
        return archiveType;
    }

    public String getLogicalGroup() {
        return logicalGroup;
    }

    public Integer getPaoID() {
        return paoID;
    }

    public Integer getPointID() {
        return pointID;
    }

    public String getPointName() {
        return pointName;
    }

    public Integer getPointOffset() {
        return pointOffset;
    }

    public String getPointType() {
        return pointType;
    }
    
    public PointType getPointTypeEnum() {
        return PointType.getForString(pointType);
    }
    
    public void setPointTypeEnum(PointType type) {
        pointType = type.getPointTypeString();
    }

    public Character getPseudoFlag() {
        if (getPointID() != null && getPointOffset() != null) {
            if (getPointID().intValue() > 0) {
                return (getPointOffset().intValue() < 1 || getPointTypeEnum().isCalcPoint()) ? PSEUDOFLAG_PSEUDO : PSEUDOFLAG_REAL;
            } else
                return PSEUDOFLAG_SYSTEM;

        } else {
            CTILogger.info("***** Unrecognized PSEUDO_FLAG found in : " + getClass().getName());
            return '?';
        }

        // return pseudoFlag;
    }

    public Character getServiceFlag() {
        return serviceFlag;
    }

    public Integer getStateGroupID() {
        return stateGroupID;
    }

    public void initialize(Integer pointID, String pointType, String pointName, Integer newPaoID, String logicalGroup,
            Integer stateGroupID, Character serviceFlag, Character alarmInhibit, Integer pointOffset, 
            PointArchiveType archiveType, Integer archiveInterval) {
        
        setPointID(pointID);
        setPointType(pointType);
        setPointName(pointName);
        setPaoID(newPaoID);
        setLogicalGroup(logicalGroup);
        setStateGroupID(stateGroupID);
        setServiceFlag(serviceFlag);
        setAlarmInhibit(alarmInhibit);
        setPointOffset(pointOffset);
        setArchiveType(archiveType);
        setArchiveInterval(archiveInterval);
    }

    @Override
    public void retrieve() throws SQLException {
        Object constraintValues[] = { getPointID() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setPointType((String) results[0]);
            setPointName((String) results[1]);
            setPaoID((Integer) results[2]);
            setLogicalGroup((String) results[3]);
            setStateGroupID((Integer) results[4]);

            String temp;

            temp = (String) results[5];
            if (temp != null)
                setServiceFlag(temp.charAt(0));

            temp = (String) results[6];
            if (temp != null)
                setAlarmInhibit(temp.charAt(0));

            temp = (String) results[7];
            // if( temp != null )
            // setPseudoFlag(  temp.charAt(0) ) ;

            setPointOffset((Integer) results[8]);
            setArchiveType(PointArchiveType.getByDbString((String) results[9]));
            setArchiveInterval((Integer) results[10]);
        } else
            throw new Error(getClass() + " - Incorrect Number of results retrieved");

    }

    public void setAlarmInhibit(Character newValue) {
        this.alarmInhibit = newValue;
    }

    public void setArchiveInterval(Integer newValue) {
        this.archiveInterval = newValue;
    }

    public void setArchiveType(PointArchiveType archiveType) {
        if (archiveType == PointArchiveType.NONE) {
            this.setArchiveStatusData(false);
        } else {
            this.setArchiveStatusData(true);
        }

        this.archiveType = archiveType;
    }

    public void setLogicalGroup(String newValue) {
        this.logicalGroup = newValue;
    }

    public void setPaoID(Integer newPaoID) {
        paoID = newPaoID;
    }

    public void setPointID(Integer newValue) {
        this.pointID = newValue;
    }

    public void setPointName(String newValue) {
        this.pointName = newValue;
    }

    public void setPointOffset(Integer newValue) {
        this.pointOffset = newValue;
    }

    public void setPointType(String newValue) {
        this.pointType = newValue;
    }

    public void setServiceFlag(Character newValue) {
        this.serviceFlag = newValue;
    }

    public void setStateGroupID(Integer stateGroupID) {
        this.stateGroupID = stateGroupID;
    }

    @Override
    public void update() throws SQLException {
        Object setValues[] = {
            getPointType(),
            getPointName(),
            getPaoID(),
            getLogicalGroup(),
            getStateGroupID(),
            getServiceFlag(),
            getAlarmInhibit(),
            getPseudoFlag(),
            getPointOffset(),
            getArchiveType().getDatabaseRepresentation(),
            getArchiveInterval()
        };

        Object constraintValues[] = { getPointID() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public boolean isOutOfService() {
        return CtiUtilities.isTrue(getServiceFlag());
    }

    public void setOutOfService(boolean val) {
        setServiceFlag(val ? CtiUtilities.trueChar : CtiUtilities.falseChar);
    }

    public boolean isAlarmsDisabled() {
        return CtiUtilities.isTrue(getAlarmInhibit());
    }

    public void setAlarmsDisabled(boolean val) {
        setAlarmInhibit(val ? CtiUtilities.trueChar : CtiUtilities.falseChar);
    }

    public boolean isArchiveStatusData() {
        return archiveStatusData;
    }

    private void setArchiveStatusData(boolean archiveStatusData) {
        this.archiveStatusData = archiveStatusData;
    }

    public boolean isPhysicalOffset() {
        return physicalOffset;
    }

    public void setPhysicalOffset(boolean physicalOffset) {
        this.physicalOffset = physicalOffset;
    }

    
}
