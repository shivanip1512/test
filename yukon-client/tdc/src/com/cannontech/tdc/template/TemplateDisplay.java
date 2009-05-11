package com.cannontech.tdc.template;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.database.JdbcTemplateHelper;

public class TemplateDisplay {

    public static final Integer INITVAL = new Integer(-1);
    
    private Integer templateNum = INITVAL;
    private Integer displayNum = INITVAL;

    public static final String CONSTRAINT_COLUMNS[] = {"DisplayNum" };

    public static final String SETTER_COLUMNS[] = { "DisplayNum","TemplateNum" };

    public static final String TABLE_NAME = "TemplateDisplay";



    public TemplateDisplay() 
    {
        super();
    }

    /**
     * add method comment.
     */
    public void add() throws java.sql.SQLException {
        String sqlStmt = "INSERT INTO "  + TABLE_NAME + " VALUES (?,?)" ;
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(sqlStmt,new Integer[] { getDisplayNum(), getTemplateNum() });
    }

    /**
     * delete method comment.
     */
    public void delete() throws java.sql.SQLException {
        String sqlStmt = "delete from " + TABLE_NAME + " where DisplayNum = ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(sqlStmt,new Integer[] { getDisplayNum()});

    }

    /**
     * retrieve method comment.
     */
	public void retrieve() throws java.sql.SQLException {
        String sqlStmt = "SELECT TemplateNum FROM " + TABLE_NAME + " WHERE DisplayNum = ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        try {
            Integer templateNum = yukonTemplate.queryForInt(sqlStmt, new Integer[] {getDisplayNum()});
            setDisplayNum(  getDisplayNum() );
            setTemplateNum(templateNum);
        } catch (EmptyResultDataAccessException e){
            setDisplayNum(INITVAL);
            setTemplateNum(INITVAL);
        }
    }

    /**
     * update method comment.
     */
    public void update() throws java.sql.SQLException {
        String sqlStmt = "update " + TABLE_NAME + " set templatenum = ? where displaynum = ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(sqlStmt, new Integer[] {getTemplateNum(), getDisplayNum()});

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


}
