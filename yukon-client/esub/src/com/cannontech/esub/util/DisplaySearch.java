package com.cannontech.esub.util;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.database.JdbcTemplateHelper;

/**
 * 
 * @author alauinger
 *
 */
public class DisplaySearch {
    
    private static String sql = "select DisplayUrl from EsubDisplayIndex where SearchKey=?";
    
    public String findDisplay(String searchKey) {
        String url = null;
        try {
            JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
            url = (String) jdbcOps.queryForObject(sql, new Object[] { searchKey }, String.class);
        }
        catch(IncorrectResultSizeDataAccessException e) {
            // We should only find one display for each search key
            // It is ok if we didn't find anything.
            if(e.getActualSize() > 1) {
                throw e;
            }
        }
        return url;
    }
}
