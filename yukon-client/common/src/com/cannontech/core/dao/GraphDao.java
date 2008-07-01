package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.graph.GraphDataSeries;

public interface GraphDao {

    public LiteGraphDefinition getLiteGraphDefinition(int id);

    public List<LiteYukonPAObject> getLiteYukonPaobjects(int gDefID);

    public List<GraphDataSeries> getAllGraphDataSeries();

    public List<GraphDataSeries> getAllGraphDataSeries(int type_);

    /**
     * Method to get a list of all graph definitions for a given user
     * @param yukonUserId - User to get graphs for
     * @return The list of graphs associated with the user
     */
    public List<LiteGraphDefinition> getGraphDefinitionsForUser(int yukonUserId);

}