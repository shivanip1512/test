package com.cannontech.database.db.capcontrol;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class CapBankAdditional extends DBPersistent implements CTIDbChange {
    public static final String STR_Y = "Y";
    public static final String TABLE_NAME = "CapBankAdditional";

    private static final String STR_N = new String("N");
    private static final Integer INT_ZERO = new Integer(0);
    private static final Double DBL_ZERO = new Double(0.0);

    public static final String STR_NONE = "(none)";

    private Integer deviceID;
    private Integer maintAreaID = INT_ZERO;
    private Integer poleNumber = INT_ZERO;
    private String driveDir = STR_NONE;
    private Double latit = DBL_ZERO;
    private Double longtit = DBL_ZERO;
    private String capBankConfig = STR_NONE;
    private String commMedium = STR_NONE;
    private Integer commStrengh = INT_ZERO;
    private String extAntenna = STR_N;
    private String antennaType = STR_NONE;
    private Date lastMaintVisit = getBeginningTime();
    private Date lastInspVisit = getBeginningTime();
    private Date opCountResetDate = getBeginningTime();
    private String potentTransformer = STR_NONE;
    private String maintReqPending = STR_N;
    private String otherComments = STR_NONE;
    private String opTeamComments = STR_NONE;
    private Date cbcBattInstallDate = getBeginningTime();

    private Boolean extAnt = Boolean.TRUE;
    private Boolean reqPend = Boolean.TRUE;
    private Boolean customCommMedium = Boolean.TRUE;
    private String commMediumCustom = STR_NONE;

    public String[] CONSTRAINT_COLUMNS = { "DeviceID" };
    public String[] SETTER_COLUMNS = { "MaintenanceAreaID", "PoleNumber",
            "DriveDirections", "Latitude", "Longitude", "CapBankConfig",
            "CommMedium", "CommStrength", "ExtAntenna", "AntennaType",
            "LastMaintVisit", "LastInspVisit", "OpCountResetDate",
            "PotentialTransformer", "MaintenanceReqPend", "OtherComments",
            "OpTeamComments", "CbcBattInstallDate" };

    @Override
    public void add() throws SQLException {
        Object[] values = new Object[] { getDeviceID(), getMaintAreaID(),
                getPoleNumber(), getDriveDir(), getLatit(), getLongtit(),
                getCapBankConfig(), getCommMedium(), getCommStrengh(),
                getExtAntenna(), getAntennaType(), getLastMaintVisit(),
                getLastInspVisit(), getOpCountResetDate(),
                getPotentTransformer(), getMaintReqPending(),
                getOtherComments(), getOpTeamComments(),
                getCbcBattInstallDate() };
        add(TABLE_NAME, values);
    }

    public static Date getBeginningTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    public void delete() throws SQLException {
        delete(TABLE_NAME, "DeviceID", getDeviceID());
    }

    @Override
    public void retrieve() throws SQLException {
        Object[] values = new Object[] { getDeviceID() };
        Object[] results = retrieve(SETTER_COLUMNS,
                                    TABLE_NAME,
                                    CONSTRAINT_COLUMNS,
                                    values);
        if (results.length == SETTER_COLUMNS.length) {

            setMaintAreaID((Integer) results[0]);
            setPoleNumber((Integer) results[1]);
            setDriveDir((String) results[2]);
            setLatit((Double) results[3]);
            setLongtit((Double) results[4]);
            setCapBankConfig((String) results[5]);
            setCommMedium((String) results[6]);
            setCommStrengh((Integer) results[7]);
            setExtAntenna((String) results[8]);
            setAntennaType((String) results[9]);
            setLastMaintVisit((Date) results[10]);
            setLastInspVisit((Date) results[11]);
            setOpCountResetDate((Date) results[12]);
            setPotentTransformer((String) results[13]);
            setMaintReqPending((String) results[14]);
            setOtherComments((String) results[15]);
            setOpTeamComments((String) results[16]);
            setCbcBattInstallDate((Date) results[17]);

        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved. Bank ID is " + getDeviceID());
        }

    }

    @Override
    public void update() throws SQLException {
        update(TABLE_NAME,
               SETTER_COLUMNS,
               new Object[] { getMaintAreaID(), getPoleNumber(), getDriveDir(),
                       getLatit(), getLongtit(), getCapBankConfig(),
                       getCommMedium(), getCommStrengh(), getExtAntenna(),
                       getAntennaType(), getLastMaintVisit(),
                       getLastInspVisit(), getOpCountResetDate(),
                       getPotentTransformer(), getMaintReqPending(),
                       getOtherComments(), getOpTeamComments(),
                       getCbcBattInstallDate() },
               CONSTRAINT_COLUMNS,
               new Object[] { getDeviceID() });
    }

    public Integer getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(Integer objectID) {
        deviceID = objectID;
    }

    public String getAntennaType() {
        return antennaType;
    }

    public void setAntennaType(String antennaType) {
        this.antennaType = antennaType;
    }

    public String getCapBankConfig() {
        return capBankConfig;
    }

    public void setCapBankConfig(String capBankConfig) {
        this.capBankConfig = capBankConfig;
    }

    public Date getCbcBattInstallDate() {
        return cbcBattInstallDate;
    }

    public void setCbcBattInstallDate(Date cbcBattInstallDate) {
        this.cbcBattInstallDate = cbcBattInstallDate;
    }

    public String getCommMedium() {
        return commMedium;
    }

    public void setCommMedium(String commMedium) {
        this.commMedium = commMedium;
    }

    public Integer getCommStrengh() {
        return commStrengh;
    }

    public void setCommStrengh(Integer commStrengh) {
        this.commStrengh = commStrengh;
    }

    public String getDriveDir() {
        return driveDir;
    }

    public void setDriveDir(String driveDir) {
        this.driveDir = driveDir;
    }

    public String getExtAntenna() {
        return extAntenna;
    }

    public void setExtAntenna(String extAntenna) {
        this.extAntenna = extAntenna;
    }

    public Date getLastInspVisit() {
        return lastInspVisit;
    }

    public void setLastInspVisit(Date lastInspVisit) {
        this.lastInspVisit = lastInspVisit;
    }

    public Date getLastMaintVisit() {
        return lastMaintVisit;
    }

    public void setLastMaintVisit(Date lastMaintVisit) {
        this.lastMaintVisit = lastMaintVisit;
    }

    public Double getLatit() {
        return latit;
    }

    public void setLatit(Double latit) {
        this.latit = latit;
    }

    public Double getLongtit() {
        return longtit;
    }

    public void setLongtit(Double longtit) {
        this.longtit = longtit;
    }

    public Integer getMaintAreaID() {
        return maintAreaID;
    }

    public void setMaintAreaID(Integer maintAreaID) {
        this.maintAreaID = maintAreaID;
    }

    public String getMaintReqPending() {
        return maintReqPending;
    }

    public void setMaintReqPending(String maintReqPending) {
        this.maintReqPending = maintReqPending;
    }

    public Date getOpCountResetDate() {
        return opCountResetDate;
    }

    public void setOpCountResetDate(Date opCountResetDate) {
        this.opCountResetDate = opCountResetDate;
    }

    public String getOpTeamComments() {
        return opTeamComments;
    }

    public void setOpTeamComments(String opTeamComments) {
        this.opTeamComments = opTeamComments;
    }

    public String getOtherComments() {
        return otherComments;
    }

    public void setOtherComments(String otherComments) {
        this.otherComments = otherComments;
    }

    public Integer getPoleNumber() {
        return poleNumber;
    }

    public void setPoleNumber(Integer poleNumber) {
        this.poleNumber = poleNumber;
    }

    public String getPotentTransformer() {
        return potentTransformer;
    }

    public void setPotentTransformer(String potentTransformer) {
        this.potentTransformer = potentTransformer;
    }

    public Boolean getExtAnt() {
        if (getExtAntenna().equalsIgnoreCase(STR_N)) {
            extAnt = Boolean.FALSE;
        }
        return extAnt;
    }

    public void setExtAnt(Boolean extAnt) {
        if (extAnt.equals(Boolean.TRUE)) {
            setExtAntenna(STR_Y);
        } else {
            setExtAntenna(STR_N);
            setAntennaType(STR_NONE);
        }
        this.extAnt = extAnt;
    }

    public Boolean getReqPend() {
        if (getMaintReqPending().equalsIgnoreCase(STR_N)) {
            reqPend = Boolean.FALSE;
        }
        return reqPend;
    }

    public void setReqPend(Boolean reqPend) {
        if (reqPend.equals(Boolean.TRUE)) {
            setMaintReqPending(STR_Y);
        } else {
            setMaintReqPending(STR_N);
        }
        this.reqPend = reqPend;
    }

    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        DBChangeMsg[] dbChange = new DBChangeMsg[1];

        dbChange[0] = new DBChangeMsg(
                        getDeviceID().intValue(),
                        DBChangeMsg.CHANGE_CBC_ADDINFO_DB,
                        DBChangeMsg.CAT_CBC_ADDINFO,
                        dbChangeType);

        return dbChange;
    }

    public Boolean getCustomCommMedium() {
        return customCommMedium;
    }

    public void setCustomCommMedium(Boolean customCommMedium) {
        this.customCommMedium = customCommMedium;
    }

    public String getCommMediumCustom() {
        return commMediumCustom;
    }

    public void setCommMediumCustom(String commMediumCustom) {
        this.commMediumCustom = commMediumCustom;
    }
}
