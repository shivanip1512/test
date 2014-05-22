package com.cannontech.database.data.device.lm;

import java.util.Vector;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;

public class MacroGroup extends LMGroup {

    private Vector<GenericMacro> macroGroupVector = null;

    public MacroGroup() {
        super(PaoType.MACRO_GROUP);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();

        for (int i = 0; i < getMacroGroupVector().size(); i++) {
            getMacroGroupVector().elementAt(i).setOwnerID(getPAObjectID());
            getMacroGroupVector().elementAt(i).add();
        }

    }

    @Override
    public void delete() throws java.sql.SQLException {
        GenericMacro.deleteAllGenericMacros(getDevice().getDeviceID(), MacroTypes.GROUP, getDbConnection());
        super.delete();
    }

    public Vector<GenericMacro> getMacroGroupVector() {

        if (macroGroupVector == null)
            macroGroupVector = new Vector<GenericMacro>();

        return macroGroupVector;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();

        macroGroupVector = new Vector<GenericMacro>();

        try {

            GenericMacro rArray[] = GenericMacro.getGenericMacros(getDevice().getDeviceID(), MacroTypes.GROUP, getDbConnection());

            for (int i = 0; i < rArray.length; i++) {
                // Since we are in the process of doing a retrieve
                // we need to make sure the new macro routes have a database
                // connection to use
                // otherwise we bomb below
                rArray[i].setDbConnection(getDbConnection());
                macroGroupVector.addElement(rArray[i]);
            }

        } catch (java.sql.SQLException e) {
            // not necessarily an error
        }

        // This necessary??
        for (int i = 0; i < getMacroGroupVector().size(); i++)
            getMacroGroupVector().elementAt(i).retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        for (int i = 0; i < getMacroGroupVector().size(); i++)
            getMacroGroupVector().elementAt(i).setDbConnection(conn);

    }

    @Override
    public void setDeviceID(Integer deviceID) {

        super.setDeviceID(deviceID);

        for (int i = 0; i < getMacroGroupVector().size(); i++)
            getMacroGroupVector().elementAt(i).setOwnerID(deviceID);
    }

    public void setMacroGroupVector(Vector<GenericMacro> newValue) {
        this.macroGroupVector = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();

        GenericMacro.deleteAllGenericMacros(getDevice().getDeviceID(), MacroTypes.GROUP, getDbConnection());

        for (int i = 0; i < getMacroGroupVector().size(); i++)
            getMacroGroupVector().elementAt(i).add();
    }
}
