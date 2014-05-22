package com.cannontech.database.data.port;

import java.util.Vector;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.pao.PAOowner;

public class PooledPort extends DirectPort {

    private Vector<PAOowner> portVector = null;

    public PooledPort() {
        super(PaoType.DIALOUT_POOL);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();

        for (int i = 0; i < getPortVector().size(); i++)
            getPortVector().get(i).add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        PAOowner.deleteAllPAOowners(getPAObjectID(), getDbConnection());
        super.delete();
    }

    public Vector<PAOowner> getPortVector() {
        if (portVector == null)
            portVector = new Vector<PAOowner>();

        return portVector;
    }

    public void setPortVector(Vector<PAOowner> portVect_) {
        portVector = portVect_;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();

        try {
            PAOowner pArray[] = PAOowner.getAllPAOownerChildren(getPAObjectID(), getDbConnection());

            for (int i = 0; i < pArray.length; i++) {
                getPortVector().addElement(pArray[i]);
            }

        } catch (java.sql.SQLException e) {
            // not necessarily an error
        }
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        for (int i = 0; i < getPortVector().size(); i++)
            getPortVector().get(i).setDbConnection(conn);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();

        PAOowner.deleteAllPAOowners(getPAObjectID(), getDbConnection());

        for (int i = 0; i < getPortVector().size(); i++)
            getPortVector().get(i).add();
    }
}
