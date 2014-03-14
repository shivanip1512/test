package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.graph.GraphDataSeries;

public interface GraphDao {

    /** Get all paos for a graph */
    public List<LiteYukonPAObject> getLiteYukonPaobjects(int graphDefinitionId);

    /** Get all data series */
    public List<GraphDataSeries> getAllGraphDataSeries();

    /** Get data series by type */
    public List<GraphDataSeries> getAllGraphDataSeriesByType(int type);

    /** Get a graph */
    public LiteGraphDefinition getLiteGraphDefinition(int graphDefinitionId);

    /** Get all graphs */
    public List<LiteGraphDefinition> getGraphDefinitions();

    /** Get all graphs for a user*/
    public List<LiteGraphDefinition> getGraphDefinitionsForUser(int userId);
    
    /** Get all graphs for an energy company */
    public List<LiteGraphDefinition> getGraphDefinitionsForEnergyCompany(int ecId);

    /** Get all the data series for a graph */
    public List<GraphDataSeries> getGraphDataSeries(int graphDefinitionId);

}