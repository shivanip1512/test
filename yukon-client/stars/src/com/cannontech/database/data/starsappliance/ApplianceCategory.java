package com.cannontech.database.data.starsappliance;

import com.cannontech.database.db.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ApplianceCategory extends DBPersistent {

    private com.cannontech.database.db.starsappliance.ApplianceCategory applianceCategory = null;

    private java.util.Vector _LMProgramVector = null;

    public ApplianceCategory() {
        super();
    }

    public void setApplianceCategoryID(Integer newID) {
        getApplianceCategory().setApplianceCategoryID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getApplianceCategory().setDbConnection(conn);

        for (int i = 0; i < getLMProgramVector().size(); i++)
            ((com.cannontech.database.db.DBPersistent) getLMProgramVector().elementAt(i)).setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        delete( "LMProgramToApplianceCategoryMapping", "ApplianceCategoryID", getApplianceCategory().getApplianceCategoryID() );

        getApplianceCategory().delete();
    }

    public void add() throws java.sql.SQLException {
        getApplianceCategory().add();

        for (int i = 0; i < getLMProgramVector().size(); i++) {
            Object[] addValues = {
                getApplianceCategory().getApplianceCategoryID(),
                ((com.cannontech.database.db.loadcontrol.Program) getLMProgramVector().elementAt(i)).getProgramID()
            };
            add("LMProgramToApplianceCategoryMapping", addValues);
        }
    }

    public void update() throws java.sql.SQLException {
        getApplianceCategory().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getApplianceCategory().retrieve();

        String[] setterColumns = {
            "LMProgramID"
        };
        String[] constraintColumns = {
            "ApplianceCategoryID"
        };
        Object[] constraintValues = {
            getApplianceCategory().getApplianceCategoryID()
        };

        Object[] results = retrieve(setterColumns, "LMProgramToApplianceCategoryMapping", constraintColumns, constraintValues);
        for (int i = 0; i < results.length; i++) {
            com.cannontech.database.db.loadcontrol.Program program = new com.cannontech.database.db.loadcontrol.Program();

            program.setProgramID( (Integer) results[i] );
            program.setDbConnection( getDbConnection() );
            program.retrieve();

            getLMProgramVector().addElement( program );
        }
    }

    public com.cannontech.database.db.starsappliance.ApplianceCategory getApplianceCategory() {
        if (applianceCategory == null)
            applianceCategory = new com.cannontech.database.db.starsappliance.ApplianceCategory();
        return applianceCategory;
    }

    public void setApplianceCategory(com.cannontech.database.db.starsappliance.ApplianceCategory newApplianceCategory) {
        applianceCategory = newApplianceCategory;
    }

    public java.util.Vector getLMProgramVector() {
        if (_LMProgramVector == null)
            _LMProgramVector = new java.util.Vector(10);
        return _LMProgramVector;
    }

    public void setLMProgramVector(java.util.Vector newLMProgramVector) {
        _LMProgramVector = newLMProgramVector;
    }
}