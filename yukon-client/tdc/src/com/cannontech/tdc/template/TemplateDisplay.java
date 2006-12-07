package com.cannontech.tdc.template;

import java.sql.Connection;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.DBPersistent;

public class TemplateDisplay extends DBPersistent {

    public static final Integer INITVAL = new Integer(-1);
    
    private Integer templateNum = INITVAL;
    private Integer displayNum = INITVAL;

    public static final String CONSTRAINT_COLUMNS[] = {"DisplayNum" };

    public static final String SETTER_COLUMNS[] = { "DisplayNum","TemplateNum" };

    public static final String TABLE_NAME = "TemplateDisplay";

    /**
     */
    public TemplateDisplay(Integer tempNum, Integer dispNum) {
        super();
        setTemplateNum(tempNum);
        setDisplayNum(dispNum);

    }

    public TemplateDisplay() {

    }

    /**
     * add method comment.
     */
    public void add() throws java.sql.SQLException {
        Object[] addValues = { getDisplayNum(), getTemplateNum()  };

        add(TABLE_NAME, addValues);
    }

    /**
     * delete method comment.
     */
    public void delete() throws java.sql.SQLException {
        Object[] values = { getDisplayNum()};
        delete(TABLE_NAME, CONSTRAINT_COLUMNS, values);

    }

    /**
     * retrieve method comment.
     */
    public void retrieve() throws java.sql.SQLException {

        Object setterValues[] = { getDisplayNum()};

        Object results[] = retrieve(SETTER_COLUMNS,
                                    TABLE_NAME,
                                    CONSTRAINT_COLUMNS,  setterValues);

        if (results.length == SETTER_COLUMNS.length) {
            setDisplayNum((Integer) results[0]);
            setTemplateNum((Integer) results[1]);

        }
        else {
            setDisplayNum(INITVAL);
            setTemplateNum(INITVAL);
        }

    }

    /**
     * update method comment.
     */
    public void update() throws java.sql.SQLException {

        String[] setColumns = { "TemplateNum"};
        Object setValues[] = { getTemplateNum()};
        String constraintColumns[] = { "DisplayNum" };
        Object constraintValues[] = { getDisplayNum() };
        update(TABLE_NAME,
               setColumns,
               setValues,
               constraintColumns,
               constraintValues);

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

    @Override
    public Connection getDbConnection() {
        Connection connection = PoolManager.getInstance()
                                           .getConnection(CtiUtilities.getDatabaseAlias());
        return connection;
    }

    @Override
    public void deletePartial() {

    }
}
