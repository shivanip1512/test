package com.cannontech.tdc.template;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.tdc.TDCMainPanel;
import com.cannontech.tdc.createdisplay.ColumnEditorDialog;
import com.cannontech.tdc.createdisplay.CreateDisplayDialog;
import com.cannontech.tdc.createdisplay.RemoveTemplateDialog;
import com.cannontech.tdc.createdisplay.TemplatePanel;
import com.cannontech.tdc.editdisplay.EditDisplayDialog;
import com.cannontech.tdc.model.TDCAbstractModel;
import com.cannontech.tdc.removedisplay.RemoveDisplayPanel;

public class TemplateDisplayModel extends TDCAbstractModel {

    // initialized in the super class
    TemplateDisplay dbPersistent;

    private Integer templateNum = new Integer(-1);
    private Integer displayNum = new Integer(-1);

    public TemplateDisplayModel() {
        super();
    }

       public TemplateDisplay getDbPersistent() {
        if (dbPersistent == null) {
            dbPersistent = new TemplateDisplay();
        }
        return dbPersistent;
    }

    @Override
    public String[] getAllRegisteredComponents() {
        String[] comps = { EditDisplayDialog.class.getName(),
                TemplatePanel.class.getName(), TDCMainPanel.class.getName(),
                ColumnEditorDialog.class.getName(),
                CreateDisplayDialog.class.getName(),
                RemoveDisplayPanel.class.getName(),
                RemoveTemplateDialog.class.getName()};
        return comps;
    }

    public void saveModel() {
        // try to see if we have any records
        try {
            
            dbPersistent.setDisplayNum(getDisplayNum());
            if (doInsert()) {
                setDbPersistent();
                dbPersistent.add();
            } else {
                setDbPersistent();
                dbPersistent.update();
            }

        } catch (SQLException e) {
            CTILogger.error(e);
        }

    }

    private void setDbPersistent() {
        dbPersistent.setTemplateNum(getTemplateNum());
        dbPersistent.setDisplayNum(getDisplayNum());
    }

    private boolean doInsert() throws SQLException {
        dbPersistent.retrieve();
        boolean insert = false;
        Integer dNum = dbPersistent.getDisplayNum();
        Integer tNum = dbPersistent.getTemplateNum();
        if (dNum.equals(getDbPersistent().INITVAL) && tNum.equals(getDbPersistent().INITVAL)) {
            insert = true;
        }
        return insert;
    }

    public Integer getDisplayNum() {
        return displayNum;
    }

    public void setDisplayNum(Integer displayNum) {
        this.displayNum = displayNum;
    }

    public Integer getTemplateNum() {
        return templateNum;
    }

    public void setTemplateNum(Integer templateNum) {
        this.templateNum = templateNum;
    }

    public void initModel() {
        dbPersistent.setDisplayNum(getDisplayNum());
        try {
            dbPersistent.retrieve();
            setThisModel();
        } catch (SQLException e) {
            CTILogger.error(e);
        }

    }

    private void setThisModel() {
        setTemplateNum(dbPersistent.getTemplateNum());
        setDisplayNum(dbPersistent.getDisplayNum());
    }

    public void removeModel() {
        dbPersistent.setDisplayNum(getDisplayNum());
        
        try {
            dbPersistent.delete();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            CTILogger.error(e);
        }
        
    }

    



}
