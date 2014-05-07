package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.database.db.NestedDBPersistentComparators;
import com.cannontech.database.db.device.DeviceVerification;

public class RTM extends IEDBase {
    private Vector<DeviceVerification> deviceVerificationVector;

    @Override
    public void add() throws SQLException {
        super.add();

        // add the RTCs to be verified
        for (int i = 0; i < getDeviceVerificationVector().size(); i++) {
            getDeviceVerificationVector().elementAt(i).add();
        }
    }

    @Override
    public void delete() throws SQLException {
        // get all those pesky RTCs assigned to this RTM
        DeviceVerification.deleteAllVerifications(getPAObjectID(), getDbConnection());

        // nuke the dynamic entries
        deleteFromDynamicTables();

        super.delete();
    }

    private void deleteFromDynamicTables() throws SQLException {
        delete("DynamicVerification", "receiverID", getPAObjectID());
    }

    public Vector<DeviceVerification> getDeviceVerificationVector() {
        if (deviceVerificationVector == null) {
            deviceVerificationVector = new Vector<>();
        }

        return deviceVerificationVector;
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();

        // retrieve all the RTCs for this RTM
        Vector<DeviceVerification> veries = DeviceVerification.getAllVerifications(getPAObjectID(), getDbConnection());
        for (int index = 0; index < veries.size(); index++) {
            getDeviceVerificationVector().add(veries.elementAt(index));
        }
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);

        for (int index = 0; index < getDeviceVerificationVector().size(); index++) {
            getDeviceVerificationVector().elementAt(index).setDbConnection(conn);
        }
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);

        for (int i = 0; i < getDeviceVerificationVector().size(); i++) {
            getDeviceVerificationVector().elementAt(i).setReceiverID(deviceID);
        }
    }

    public void setDeviceVerificationVector(Vector<DeviceVerification> deviceVerificationVector) {
        this.deviceVerificationVector = deviceVerificationVector;
    }

    @Override
    public void update() throws SQLException {
        super.update();

        Vector<DeviceVerification> veriesVector = new Vector<>();

        // grab all the previous gear entries for this program
        Vector<DeviceVerification> oldVeries =
            DeviceVerification.getAllVerifications(getPAObjectID(), getDbConnection());

        // unleash the power of the NestedDBPersistent
        veriesVector = NestedDBPersistentComparators.NestedDBPersistentCompare(oldVeries, getDeviceVerificationVector(),
                NestedDBPersistentComparators.deviceVerificationComparator);

        // throw the gears into the Db
        for (int i = 0; i < veriesVector.size(); i++) {
            veriesVector.elementAt(i).setReceiverID(getPAObjectID());
            ((NestedDBPersistent) veriesVector.elementAt(i)).executeNestedOp();
        }
    }
}
