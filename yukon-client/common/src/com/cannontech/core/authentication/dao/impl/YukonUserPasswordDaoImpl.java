package com.cannontech.core.authentication.dao.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserPasswordDaoImpl implements YukonUserPasswordDao {
    private SimpleJdbcOperations template;
    private SqlStatementBuilder setSql;
    private SqlStatementBuilder changeSql;
    private SqlStatementBuilder recoverSql;
    
    {
        recoverSql = new SqlStatementBuilder();
        recoverSql.append("SELECT Password");
        recoverSql.append("FROM YukonUser");
        recoverSql.append("WHERE UserId = ?");
    
        setSql = new SqlStatementBuilder();
        setSql.append("update YukonUser");
        setSql.append("set Password = ?");
        setSql.append("where UserID = ?");
    
        changeSql = new SqlStatementBuilder();
        changeSql.append("update YukonUser");
        changeSql.append("set Password = ?");
        changeSql.append("where UserID = ? and Password = ?");
    }

    public boolean checkPassword(LiteYukonUser user, String password) {
        try {
            String pwd = (String) template.queryForObject(recoverSql.toString(),
                                                          String.class,
                                                          user.getUserID());
            
            if (pwd.equals(password))
            	return true;
        } catch (DataAccessException e) {
            return false;
        }
        return false;
    }
    
    public boolean changePassword(LiteYukonUser user, String newPassword) {
        int rows;
        try {
            rows = template.update(setSql.toString(), newPassword, user.getUserID());
        } catch (DataAccessException e) {
            return false;
        }
        return rows == 1;
    }
    
    public boolean changePassword(LiteYukonUser user, String oldPassword, String newPassword) {
        int rows;
        try {
            rows = template.update(changeSql.toString(), newPassword, user.getUserID(), oldPassword);
        } catch (DataAccessException e) {
            return false;
        }
        return rows == 1;
    }

    public String recoverPassword(LiteYukonUser user) {
        String password = template.queryForObject(recoverSql.toString(), String.class, user.getUserID());
        return password;
    }
    
    @Required
    public void setTemplate(SimpleJdbcOperations template) {
        this.template = template;
    }

}
