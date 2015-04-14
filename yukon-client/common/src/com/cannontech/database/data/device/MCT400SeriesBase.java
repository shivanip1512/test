package com.cannontech.database.data.device;

import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.db.device.DeviceMCT400Series;

public abstract class MCT400SeriesBase extends MCTBase {
    
    private DeviceMCT400Series deviceMCT400Series = null;

    public MCT400SeriesBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        if (hasDisconnectOrTOU()) {
            getDeviceMCT400Series().add();
        }
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        if (hasDisconnectOrTOU()) {
            getDeviceMCT400Series().add();
        }
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceMCT400Series().delete();
        super.delete();
    }
    
    @Override
    public void deletePartial() throws java.sql.SQLException {
        getDeviceMCT400Series().delete();
        super.deletePartial();
    }


    public DeviceMCT400Series getDeviceMCT400Series() {
        if (deviceMCT400Series == null) {
            deviceMCT400Series = new DeviceMCT400Series();
        }

        return deviceMCT400Series;
    }

    public void setDeviceMCT400Series(DeviceMCT400Series mct400Series) {
        deviceMCT400Series = mct400Series;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceMCT400Series().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceMCT400Series().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceMCT400Series().setDeviceID(deviceID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        if (hasDisconnectOrTOU()) {
            getDeviceMCT400Series().delete();
            getDeviceMCT400Series().add();
        }
    }

    /**
     * This method deletes the DeviceMCT400Series data for getDeviceID(). The
     * disconnectAddress is also set to null to "clear out" the object.
     */
    public void deleteAnAddress() {
        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceMCT400Series ");
        sql.append("WHERE DeviceID = ").appendArgument(getDeviceMCT400Series().getDeviceID());
        template.update(sql.getSql(), sql.getArguments());

        getDeviceMCT400Series().setDisconnectAddress(null); // clear out any
                                                            // remembrance of an
                                                            // address
    }

    /**
     * Returns true when object has disconnect or TOU to write(add or update) to
     * the database
     * @return
     */
    private boolean hasDisconnectOrTOU() {
        // TODO - todate (20091206) nothing seems to set/get TOUScheduleId per
        // device.
        // At some point, changes may need to be made to set TOUScheduleId
        return getDeviceMCT400Series().getDisconnectAddress() != null;
    }

    /**
     * Helper method to check if disconnectAddress is unique.
     * @param address
     * @param excludedPAOId - if null, check is excluded.
     * @return list of devices having address already assigned
     */
    public static List<String> isDiscAddressUnique(Integer address, Integer thisPaoId) {

        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT y.paoname FROM YukonPaobject y JOIN DeviceMCT400Series mct ON y.paobjectId = mct.deviceId");
        sql.append("WHERE DisconnectAddress").eq(address);
        if (thisPaoId != null) {
            sql.append("AND y.paobjectid").neq(thisPaoId);
        }

        List<String> devices = template.queryForList(sql.getSql(),
                                                     sql.getArguments(),
                                                     String.class);

        return devices;
    }
}