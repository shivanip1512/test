package com.cannontech.database.data.device;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.customer.DeviceCustomerList;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.device.DynamicDeviceScanData;
import com.cannontech.database.db.device.DynamicVerification;
import com.cannontech.spring.YukonSpringHook;

public abstract class DeviceBase extends YukonPAObject implements EditorPanel {
    private Device device = null;
    protected boolean isPartialDelete;

    public DeviceBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        if (getDevice().getDeviceID() == null) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
            setDeviceID(paoDao.getNextPaoId());
            if (this instanceof GridAdvisorBase) {
                ((GridAdvisorBase) this).setDefaultPort();
            }
        }

        super.add();
        getDevice().add();
    }

    @SuppressWarnings("unused")
    @Override
    public void addPartial() throws java.sql.SQLException {
        setDeviceID(getDevice().getDeviceID());
    }

    public boolean allowRebroadcast() {

        return false;
    }

    @Override
    public void delete() throws java.sql.SQLException {
        DynamicDeviceScanData.deleteDynamicDeviceScanData(getDevice().getDeviceID(), getDbConnection());

        delete(DynamicVerification.TABLE_NAME, "TransmitterID", getDevice().getDeviceID());

        delete(DynamicVerification.TABLE_NAME, "ReceiverID", getDevice().getDeviceID());

        delete(DeviceCustomerList.TABLE_NAME, DeviceCustomerList.CONSTRAINT_COLUMNS[0], getDevice().getDeviceID());

        delete("ImportPendingComm", "DeviceId", getPAObjectID());

        if (!isPartialDelete) {
            getDevice().delete();
            super.delete();
        }
    }

    @Override
    public void deletePartial() throws java.sql.SQLException {
        isPartialDelete = true;
        this.delete();
        isPartialDelete = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DeviceBase)
            return getDevice().getDeviceID().equals(((DeviceBase) obj).getDevice().getDeviceID());
        else
            return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getDevice().getDeviceID().hashCode();
    }

    public Device getDevice() {
        if (device == null)
            device = new Device();

        return device;
    }

    public final static String hasRoute(Integer deviceID) {
        SqlStatement stmt = new SqlStatement("SELECT PAOName FROM YukonPaobject y, Route r" + 
                                            " WHERE r.DeviceID=" + deviceID + " AND r.RouteID=y.PAObjectID", 
                                            CtiUtilities.getDatabaseAlias());

        try {
            stmt.execute();
            if (stmt.getRowCount() > 0)
                return stmt.getRow(0)[0].toString();
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDevice().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDevice().setDbConnection(conn);
    }

    public void setDevice(Device newValue) {
        this.device = newValue;
    }

    public void setDeviceID(Integer deviceID) {
        super.setPAObjectID(deviceID);
        getDevice().setDeviceID(deviceID);
    }

//    public void setDeviceType(String devType) {
//        getYukonPAObject().setType(devType);
//    }

    public void setDisableFlag(Character ch) {
        getYukonPAObject().setDisableFlag(ch);
    }

    @Override
    public void setPAOName(String name) {
        super.setPAOName(name);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDevice().update();
    }
}
