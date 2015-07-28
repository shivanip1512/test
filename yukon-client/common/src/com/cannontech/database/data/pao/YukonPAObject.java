package com.cannontech.database.data.pao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.LocationService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.database.db.NestedDBPersistentComparators;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.database.db.pao.PAOScheduleAssign;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

public abstract class YukonPAObject extends DBPersistent implements CTIDbChange {
    private com.cannontech.database.db.pao.YukonPAObject yukonPAObject = null;

    private Vector<PAOExclusion> paoExclusionVector = null;

    // may have zero or more schedules, instance of PAOScheduleAssign
    private List<PAOScheduleAssign> schedules = null;

    public YukonPAObject(PaoType paoType) {
        getYukonPAObject().setPaoType(paoType);
    }
    
    @Override
    public void add() throws SQLException {
        getYukonPAObject().add();

        for (int i = 0; i < getPAOExclusionVector().size(); i++) {
            ((DBPersistent) getPAOExclusionVector().get(i)).add();
        }

        for (int i = 0; i < getSchedules().size(); i++) {
            ((DBPersistent) getSchedules().get(i)).add();
        }
    }

    @Override
    public void addPartial() throws SQLException {
        add();
    }

    @Override
    public void delete() throws SQLException {
        deletePoints();

        // ADD TABLES THAT HAVE A REFERENCE TO THE YukonPAObject TABLE AND THAT
        // NEED TO BE DELETED WHEN A YukonPAObject ROW IS DELETED (CASCADE DELETE)
        delete("DynamicPAOStatistics", "PAObjectID", getPAObjectID());
        delete("LMControlHistory", "PAObjectID", getPAObjectID());
        delete("DynamicLMControlHistory", "PAObjectID", getPAObjectID());
        delete("PAOOwner", "ChildID", getPAObjectID());
        // Remove pao permissions
        PaoPermissionService paoPermissionService = YukonSpringHook.getBean(PaoPermissionService.class);
        paoPermissionService.removeAllPaoPermissions(getPAObjectID());

        delete("DynamicPAOInfo", "PAObjectID", getPAObjectID());

        PAOExclusion.deleteAllPAOExclusions(getPAObjectID().intValue(), getDbConnection());

        PAOScheduleAssign.deleteAllPAOScheduleAssignments(getPAObjectID().intValue(), getDbConnection());

        getYukonPAObject().delete();
    }

    @Override
    public void deletePartial() throws SQLException {
        // Nothing to do.
    }

    private void deletePoints() throws SQLException {
        List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(getPAObjectID());
        for (LitePoint point : points) {
            PointBase chubbyPt = PointFactory.createPoint(point.getPointType());
            chubbyPt.setPointID(point.getPointID());
            chubbyPt.setDbConnection(getDbConnection());

            chubbyPt.delete();
        }
    }

    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        ArrayList<DBChangeMsg> list = new ArrayList<DBChangeMsg>(10);

        // add the basic change method
        list.add(new DBChangeMsg(getPAObjectID().intValue(), DBChangeMsg.CHANGE_PAO_DB, getPaoType().getPaoCategory().getDbString(), getPaoType().getDbString(),
            dbChangeType));

        // if we are deleting this PAO, we need to take in account the Points that also get deleted
        if (dbChangeType == DbChangeType.DELETE) {
            // get all the point ids and their types that are owned by this PAObject
            int[][] idsAndTypes =
                YukonSpringHook.getBean(PointDao.class).getAllPointIDsAndTypesForPAObject(getPAObjectID());

            // add a new message for each point
            for (int i = 0; i < idsAndTypes.length; i++) {
                DBChangeMsg msg =
                    new DBChangeMsg(idsAndTypes[i][0], DBChangeMsg.CHANGE_POINT_DB, DBChangeMsg.CAT_POINT,
                        PointTypes.getType(idsAndTypes[i][1]), dbChangeType);

                list.add(msg);
            }

        }

        DBChangeMsg[] dbChange = new DBChangeMsg[list.size()];
        return list.toArray(dbChange);
    }

    public Integer getPAObjectID() {
        return getYukonPAObject().getPaObjectID();
    }

    public String getPAODescription() {
        return getYukonPAObject().getDescription();
    }

    public Character getPAODisableFlag() {
        return getYukonPAObject().getDisableFlag();
    }

    public String getPAOName() {
        return getYukonPAObject().getPaoName();
    }

    public String getPAOStatistics() {
        return getYukonPAObject().getPaoStatistics();
    }

    public PaoType getPaoType() {
        return getYukonPAObject().getPaoType();
    }
   
    protected com.cannontech.database.db.pao.YukonPAObject getYukonPAObject() {
        if (yukonPAObject == null) {
            yukonPAObject = new com.cannontech.database.db.pao.YukonPAObject();
        }

        return yukonPAObject;
    }

    @Override
    public void retrieve() throws SQLException {
        getYukonPAObject().retrieve();

        getPAOExclusionVector().removeAllElements();
        Vector<PAOExclusion> exc = PAOExclusion.getAllPAOExclusions(getPAObjectID(), getDbConnection());
        for (PAOExclusion paoExclusion : exc) {
            getPAOExclusionVector().add(paoExclusion);
        }

        getSchedules().clear();
        PAOScheduleAssign[] paoScheds =
            PAOScheduleAssign.getAllPAOSchedAssignments(getPAObjectID().intValue(), getDbConnection());
        for (int i = 0; i < paoScheds.length; i++) {
            getSchedules().add(paoScheds[i]);
        }
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);

        getYukonPAObject().setDbConnection(conn);

        for (int i = 0; i < getPAOExclusionVector().size(); i++) {
            ((DBPersistent) getPAOExclusionVector().get(i)).setDbConnection(conn);
        }

        for (int i = 0; i < getSchedules().size(); i++) {
            ((DBPersistent) getSchedules().get(i)).setDbConnection(conn);
        }
    }

    protected void setPAObjectID(Integer id) {
        getYukonPAObject().setPaObjectID(id);

        for (PAOExclusion paoExclusion : getPAOExclusionVector()) {
            // we must null out the PK since there is a new PAObjectID owner
            paoExclusion.setExclusionID(null);
            paoExclusion.setPaoID(id);
        }

        for (PAOScheduleAssign paoScheduleAssign : getSchedules()) {
            // we must null out the PK since there is a new PAObjectID owner
            paoScheduleAssign.setEventID(null);
            paoScheduleAssign.setPaoID(id);
        }

    }

    public void setPAOName(String name) {
        getYukonPAObject().setPaoName(name);
    }

    public void setPAODescription(String description) {
        getYukonPAObject().setDescription(description);
    }

    public void setPAOStatistics(String newStats) {
        getYukonPAObject().setPaoStatistics(newStats);
    }

    public boolean isDisabled() {
        return CtiUtilities.isTrue(getYukonPAObject().getDisableFlag());
    }

    public void setDisabled(boolean val) {
        getYukonPAObject().setDisableFlag(val ? CtiUtilities.trueChar : CtiUtilities.falseChar);
    }

    @Override
    public String toString() {
        return getYukonPAObject().getPaoName();
    }

    @Override
    public void update() throws SQLException {
        getYukonPAObject().update();
        if (getYukonPAObject().getDisableFlag().equals('Y')) {
            LocationService locationService = YukonSpringHook.getBean(LocationService.class);
            locationService.deleteLocation(getYukonPAObject().getPaObjectID(), YukonUserContext.system.getYukonUser());
        }
        // grab all the previous PAOExclusion entries for this object
        Vector<PAOExclusion> oldPAOExclusion = PAOExclusion.getAllPAOExclusions(getPAObjectID(), getDbConnection());

        // unleash the power of the NestedDBPersistent
        Vector<PAOExclusion> exclusionVector =
            NestedDBPersistentComparators.NestedDBPersistentCompare(oldPAOExclusion, getPAOExclusionVector(),
                NestedDBPersistentComparators.paoExclusionComparator);

        // throw the PAOExclusions into the Db
        for (int i = 0; i < exclusionVector.size(); i++) {
            ((NestedDBPersistent) exclusionVector.elementAt(i)).setDbConnection(getDbConnection());
            ((NestedDBPersistent) exclusionVector.elementAt(i)).executeNestedOp();

        }

        // completely remove and add the entire set of PAOScheduleAssignments
        PAOScheduleAssign.deleteAllPAOScheduleAssignments(getPAObjectID(), getDbConnection());
        for (PAOScheduleAssign paoScheduleAssign : getSchedules()) {
            paoScheduleAssign.add();
        }
    }

    public Vector<PAOExclusion> getPAOExclusionVector() {
        if (paoExclusionVector == null) {
            paoExclusionVector = new Vector<PAOExclusion>();
        }

        return paoExclusionVector;
    }

    public List<PAOScheduleAssign> getSchedules() {

        if (schedules == null) {
            schedules = new ArrayList<PAOScheduleAssign>(8);
        }

        return schedules;
    }

    public void setSchedules(List<PAOScheduleAssign> schedules) {
        this.schedules = schedules;
    }
}
