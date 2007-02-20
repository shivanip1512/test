package com.cannontech.core.authentication.dao.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserPasswordDaoImpl implements YukonUserPasswordDao {
    private SimpleJdbcOperations template;
    private SqlStatementBuilder checkSql;
    private SqlStatementBuilder setSql;
    private SqlStatementBuilder changeSql;
    private SqlStatementBuilder recoverSql;
    
    {
        checkSql = new SqlStatementBuilder();
        checkSql.append("select UserID from YukonUser");
        checkSql.append("where UserID = ? and Password = ?");

        recoverSql = new SqlStatementBuilder();
        recoverSql.append("select Password from YukonUser");
        recoverSql.append("where UserID = ?");
    
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
        int returnedId;
        try {
            returnedId = template.queryForInt(checkSql.toString(), user.getUserID(), password);
        } catch (DataAccessException e) {
            return false;
        }
        return returnedId == user.getUserID();
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
