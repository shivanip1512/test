package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.graph.GraphDataSeries;

public interface GraphDao {

    List<LiteYukonPAObject> getLiteYukonPaobjects(int graphDefinitionId);

    List<GraphDataSeries> getAllGraphDataSeries();

    List<GraphDataSeries> getAllGraphDataSeriesByType(int type);

    LiteGraphDefinition getLiteGraphDefinition(int graphDefinitionId);

    List<LiteGraphDefinition> getGraphDefinitions();

    List<LiteGraphDefinition> getGraphDefinitionsForUser(int userId);
    
    List<LiteGraphDefinition> getGraphDefinitionsForEnergyCompany(int ecId);

    List<GraphDataSeries> getGraphDataSeries(int graphDefinitionId);

}