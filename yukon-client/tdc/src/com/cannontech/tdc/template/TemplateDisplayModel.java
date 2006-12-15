package com.cannontech.tdc.template;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;

public class TemplateDisplayModel {

    // initialized in the super class
    TemplateDisplay dbPersistent;
    private Integer templateNum = new Integer(-1);
    private Integer displayNum = new Integer(-1);

    public TemplateDisplayModel() 
    {
        getDbPersistent();

    }

    public TemplateDisplay getDbPersistent() {
        if (dbPersistent == null) {
            dbPersistent = new TemplateDisplay();
        }
        return dbPersistent;
    }

    public void saveModel(Integer dispNum, Integer tempNum) {
        setDisplayNum(dispNum);
        setTemplateNum(tempNum);
        if (getDisplayNum() != null && getTemplateNum() != null) {
            dbPersistent.setDisplayNum(getDisplayNum());
            try {
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
        if (dNum.equals(TemplateDisplay.INITVAL) && tNum.equals(TemplateDisplay.INITVAL)) {
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

    public void initModel(Integer dispNum) {
        dbPersistent.setDisplayNum(dispNum);
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


    public void removeModel(Integer dispNum) {
        dbPersistent.setDisplayNum(dispNum);

        try {
            dbPersistent.delete();
        } catch (SQLException e) {
            CTILogger.error(e);
        }

    }
}
