package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.device.MCTBroadcastMapping;

public class MCT_Broadcast extends CarrierBase {

    private Vector<MCTBroadcastMapping> MCTVector = null;
    private int mctOrderNum;

    public MCT_Broadcast() {
        super(PaoType.MCTBROADCAST);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();

        if (MCTVector != null) {

            MCTBroadcastMapping.deleteAllBroadCastMappings(getDevice().getDeviceID(), getDbConnection());

            for (int j = 0; j < MCTVector.size(); j++) {
                if (MCTVector.elementAt(j).getMctBroadcastID() == null)
                    MCTVector.elementAt(j).setMctBroadcastID(this.getPAObjectID());

                MCTVector.elementAt(j).setOrdering(new Integer(j));
                MCTVector.elementAt(j).add();
            }
        }
    }

    @Override
    public void delete() throws java.sql.SQLException {

        // nail all the MCTs mapped to this broadcast
        MCTBroadcastMapping.deleteAllBroadCastMappings(getDevice().getDeviceID(), getDbConnection());
        super.delete();
        setDbConnection(null);
    }

    public int getMctOrderNum() {
        return mctOrderNum;
    }

    public Vector<MCTBroadcastMapping> getMCTVector() {
        if (MCTVector == null) {
            MCTVector = new Vector<MCTBroadcastMapping>();
        }
        return MCTVector;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {

        super.retrieve();

        try {
            MCTBroadcastMapping[] mcts = MCTBroadcastMapping.getAllMCTsList(getDevice().getDeviceID(), getDbConnection());
            for (int i = 0; i < mcts.length; i++) {
                mcts[i].setDbConnection(getDbConnection());
                getMCTVector().addElement(mcts[i]);
            }
        } catch (java.sql.SQLException e) {
            // not necessarily an error
        }
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        if (MCTVector != null) {
            for (int j = 0; j < MCTVector.size(); j++)
                MCTVector.elementAt(j).setDbConnection(conn);
        }
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);

        if (MCTVector != null) {
            for (int i = 0; i < MCTVector.size(); i++) {
                MCTVector.elementAt(i).setMctBroadcastID(deviceID);
            }
        }
    }

    public void setMctOrderNum(int newMctOrderNum) {
        mctOrderNum = newMctOrderNum;
    }

    public void setMCTVector(Vector<MCTBroadcastMapping> newMCTVector) {
        MCTVector = newMCTVector;
    }

    @Override
    public void update() throws java.sql.SQLException {

        super.update();

        if (MCTVector != null) {

            MCTBroadcastMapping.deleteAllBroadCastMappings(getDevice().getDeviceID(), getDbConnection());

            for (int j = 0; j < MCTVector.size(); j++) {
                if (MCTVector.elementAt(j).getMctBroadcastID() == null)
                    MCTVector.elementAt(j).setMctBroadcastID(this.getPAObjectID());

                MCTVector.elementAt(j).setOrdering(new Integer(j));
                MCTVector.elementAt(j).add();
            }
        }
    }

    public final NativeIntVector getAllMCTsIDList( Integer MCTBroadcastID) {
        NativeIntVector mctIntList = new NativeIntVector(30);
        Connection conn = null;

        try {
            conn = PoolManager.getInstance().getConnection("yukon");
            setDbConnection(conn);

            mctIntList = MCTBroadcastMapping.getAllMCTsIDList(MCTBroadcastID, conn);

            // Lose the reference to the connection
            setDbConnection(null);
            return mctIntList;
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
            return null;
        }

        finally { // make sure to close the connection
            try {
                if (conn != null)
                    conn.close();
            } catch (java.sql.SQLException e2) {
                CTILogger.error(e2.getMessage(), e2);
            }
        }
    }
}