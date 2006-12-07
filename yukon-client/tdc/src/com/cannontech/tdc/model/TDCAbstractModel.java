package com.cannontech.tdc.model;

import java.awt.Frame;
import java.lang.reflect.Field;
import java.sql.Connection;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.tdc.utils.DataModelUtils;

public abstract class TDCAbstractModel implements TDCDataModel {

    private Frame mainFrame;

    public TDCAbstractModel() {
        super();
        DBPersistent dbPersistent = getDbPersistent();
        Connection dbConnection = dbPersistent.getDbConnection();
        dbPersistent.setDbConnection(dbConnection);
    }

    public abstract DBPersistent getDbPersistent();

    public Frame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(Frame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public abstract String[] getAllRegisteredComponents();

    public void updateModel(Object sender, String fieldName, Object value,
            String cxt) {
        Frame parent = getMainFrame();
        if (sender != null) {
            TDCDataModel dataModel = DataModelUtils.getComponentDataModel(parent,
                                                                          sender,
                                                                          cxt);
            // check if the message applies to us
            if (dataModel != null) {
                if (dataModel.getClass().equals(this.getClass())) {
                    updateDataModel(fieldName, value);
                }
            }
        } else {
            updateDataModel(fieldName, value);
        }
    }

    // just in case we know which component to update
    public void updateDataModel(String fieldName, Object value) {
        try {
            Field declaredField = this.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(this, value);
        } catch (Exception e) {
            CTILogger.error(e);
        }
    }

}
