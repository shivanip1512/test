package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.graph.GraphDataSeries;

public interface GraphDao {

    List<LiteYukonPAObject> getLiteYukonPaobjects(int graphDefinitionId);

    List<GraphDataSeries> getAllGraphDataSeriesByType(int type);

    /**
     * Returns object loaded from database.
     * Use {@link #getLiteGraphDefinition(id)} unless necessary to load from database.
     */
    LiteGraphDefinition retrieveLiteGraphDefinition(int graphDefinitionId) throws NotFoundException;;
    
    /**
     * Returns serverDatabaseCache object
     */
    LiteGraphDefinition getLiteGraphDefinition(int graphDefinitionId);

    List<LiteGraphDefinition> getGraphDefinitions();

    List<LiteGraphDefinition> getGraphDefinitionsForUser(int userId);

    List<GraphDataSeries> getGraphDataSeries(int graphDefinitionId);

}