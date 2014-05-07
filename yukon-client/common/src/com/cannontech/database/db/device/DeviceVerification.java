package com.cannontech.database.db.device;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.NestedDBPersistent;


public class DeviceVerification extends NestedDBPersistent {
    private Integer receiverID;
    private Integer transmitterID;
    private String resendOnFail;
    private String disable;

    public static final String SETTER_COLUMNS[] = { "RECEIVERID", "TRANSMITTERID", "RESENDONFAIL", "DISABLE" };
    public static final String CONSTRAINT_COLUMNS[] = { "RECEIVERID" };
    public static final String TABLE_NAME = "DeviceVerification";

    public DeviceVerification() {
    }

    public DeviceVerification(Integer recID, Integer transID, String rsendFail, String flagDisable) {
        receiverID = recID;
        transmitterID = transID;
        resendOnFail = rsendFail;
        disable = flagDisable;
    }

    @Override
    public void add() throws SQLException {
        Object addValues[] = { getReceiverID(), getTransmitterID(), getResendOnFail(), getDisable() };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getReceiverID());
    }

    public static boolean deleteAllVerifications(Integer devID, Connection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("Database connection should not be (null)");
        }

        try {
            Statement stat = conn.createStatement();
            stat.execute("DELETE FROM " + TABLE_NAME + " WHERE " + CONSTRAINT_COLUMNS[0] + "= " + devID);

            if (stat != null) {
                stat.close();
            }
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    public static final Vector<DeviceVerification> getAllVerifications(Integer deviceID, Connection conn)
            throws SQLException {
        Vector<DeviceVerification> tmpList = new Vector<>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String sql =
            "SELECT " + SETTER_COLUMNS[0] + ", " + SETTER_COLUMNS[1] + ", " + SETTER_COLUMNS[2] + ", "
                + SETTER_COLUMNS[3] + " FROM " + TABLE_NAME + " WHERE " + CONSTRAINT_COLUMNS[0] + " = " + deviceID;

        try {
            if (conn == null) {
                throw new IllegalStateException("Database connection should not be null.");
            }
            pstmt = conn.prepareStatement(sql.toString());
            // pstmt.setInt( 1, deviceID.intValue() );

            rset = pstmt.executeQuery();

            while (rset.next()) {
                DeviceVerification rtc = new DeviceVerification();

                rtc.setDbConnection(conn);
                rtc.setReceiverID(new Integer(rset.getInt("RECEIVERID")));
                rtc.setTransmitterID(new Integer(rset.getInt("TRANSMITTERID")));
                rtc.setResendOnFail(rset.getString("RESENDONFAIL"));
                rtc.setDisable(rset.getString("DISABLE"));

                tmpList.add(rtc);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e2) {
                CTILogger.error(e2.getMessage(), e2);// something is up
            }
        }
        return tmpList;
    }

    public Integer getReceiverID() {
        return receiverID;
    }

    public Integer getTransmitterID() {
        return transmitterID;
    }

    public String getResendOnFail() {
        return resendOnFail;
    }

    public String getDisable() {
        return disable;
    }

    @Override
    public void retrieve() {
        Integer constraintValues[] = { getReceiverID() };

        try {
            Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

            if (results.length == SETTER_COLUMNS.length) {
                setTransmitterID((Integer) results[1]);
                setResendOnFail((String) results[2]);
                setDisable((String) results[3]);
            } else {
                throw new Error(getClass() + " - Incorrect Number of results retrieved");
            }
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    public void setReceiverID(Integer recID) {
        receiverID = recID;
    }

    public void setTransmitterID(Integer transID) {
        transmitterID = transID;
    }

    public void setResendOnFail(java.lang.String resFail) {
        resendOnFail = resFail;
    }

    public void setDisable(String flagDisable) {
        disable = flagDisable;
    }

    @Override
    public void update() {
        Object setValues[] = { getReceiverID(), getTransmitterID(), getResendOnFail(), getDisable() };

        Object constraintValues[] = { getReceiverID() };

        try {
            update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        }
    }
}
