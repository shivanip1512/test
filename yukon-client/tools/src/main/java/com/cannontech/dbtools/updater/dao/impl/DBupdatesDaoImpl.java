package com.cannontech.dbtools.updater.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.dbtools.updater.dao.DBUpdatesDao;

public class DBupdatesDaoImpl implements DBUpdatesDao {

    @Override
    public List<String> getUpdateIds() {
        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
        List<String> updateIds = new ArrayList<>();
        String sql = "SELECT UpdateId FROM DBUpdates";
        updateIds = template.queryForList(sql, String.class);
        System.out.println("---------" + updateIds);
        return updateIds;
    }
}
