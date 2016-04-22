package com.cannontech.tdc.utils;

import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.tdc.template.TemplateDisplay;

public class DataModelUtils {
    public static Integer getDisplayNum(String dispName) {
        String sqlStmt = "SELECT DisplayNum FROM Display WHERE ";
        sqlStmt += "Name like ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        Integer tempInt = yukonTemplate.queryForObject(sqlStmt, new String[] { dispName }, Integer.class);
        return tempInt;
    }

    public static List<Integer> getAllDisplaysForTemplate(Integer templateNum) {
        String sqlStmt = "SELECT DisplayNum FROM " + TemplateDisplay.TABLE_NAME + " WHERE ";
        sqlStmt += "TemplateNum = ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForList(sqlStmt,
                                          new Integer[] { templateNum },
                                          Integer.class);
    }

    public static void templatizeDisplay(Integer templateNum, Integer displayNum) {
        String sqlStmt = "insert into displaycolumns " + "select ?, title, typenum, ordering, width  from templatecolumns where templatenum = ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        try{
            yukonTemplate.update(sqlStmt, displayNum, templateNum);
        }
        catch (Exception e)
        {
            CTILogger.error(e);
        }
    }

    public static void deleteDisplayColumns(Integer dispNum) {
        String sqlStmt = "DELETE FROM DisplayColumns where DisplayNum = ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(sqlStmt, dispNum);
    }

    public static int getDisplayTemplate(long currentDisplayNumber) {
        int templateNum = -1;
        String sqlStmt = "SELECT TemplateNum FROM " + TemplateDisplay.TABLE_NAME + " WHERE ";
        sqlStmt += "DisplayNum = ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        try {
            templateNum =
                yukonTemplate.queryForObject(sqlStmt, Integer.class, (Integer.parseInt("" + currentDisplayNumber)));
        } catch (IncorrectResultSizeDataAccessException e) {
            templateNum = -1;
        }
        return templateNum;
    }
}
