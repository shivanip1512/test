package com.cannontech.dbtools.updater.dao;

import java.util.List;

public interface DBUpdatesDao {

    /**
     * Method returns all available updateIds
     */
    List<String> getUpdateIds();
}
