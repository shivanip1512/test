package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteGraphDefinition;

public interface GraphDao {

    public LiteGraphDefinition getLiteGraphDefinition(int id);

    public List getLiteYukonPaobjects(int gDefID);

    public List getAllGraphDataSeries();

    public List getAllGraphDataSeries(int type_);

    /**
     * Method to get a list of all graph definitions for a given user
     * @param yukonUserId - User to get graphs for
     * @return The list of graphs associated with the user
     */
    public List<LiteGraphDefinition> getGraphDefinitionsForUser(int yukonUserId);

}