package com.cannontech.database.cache.functions;

import com.cannontech.spring.YukonSpringHook;

/**
 * Class which contains standard data access functionality
 */
public class DaoFuncs {

    /**
     * Method to get the next id for the given table
     * @param tableName - Table to get the next id for
     * @return The next id for the table
     */
    public static Integer getNextId(String tableName) {
        return new Integer(YukonSpringHook.getNextValueHelper().getNextValue(tableName));
    }
}
