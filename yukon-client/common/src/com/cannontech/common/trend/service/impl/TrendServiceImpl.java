package com.cannontech.common.trend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.trend.service.TrendService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.yukon.IDatabaseCache;

public class TrendServiceImpl implements TrendService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;

    @Override
    public TrendModel create(TrendModel trend) {
        GraphDefinition graph = createTrend();
        trend.buildDBPersistent(graph);
        dbPersistentDao.performDBChange(graph, TransactionType.INSERT);
        trend.buildModel(graph);
        return trend;
    }

    private GraphDefinition createTrend() {
        GraphDefinition graph = new GraphDefinition();
        return graph;
    }


    @Override
    @Transactional
    public int delete(int trendId) {

        LiteGraphDefinition liteTrend = dbCache.getAllGraphDefinitions()
                                               .stream()
                                               .filter(group -> group.getLiteID() == trendId)
                                               .findFirst()
                                               .orElseThrow(() -> new NotFoundException("Trend Id not found"));
        GraphDefinition trend = (GraphDefinition) LiteFactory.createDBPersistent(liteTrend);
        dbPersistentDao.performDBChange(trend, TransactionType.DELETE);
        return trend.getGraphDefinition().getGraphDefinitionID();
    }

    @Override
    public TrendModel update(int id, TrendModel trendModel) {
        LiteGraphDefinition liteTrend = dbCache.getAllGraphDefinitions()
                                               .stream()
                                               .filter(group -> group.getLiteID() == id)
                                               .findFirst()
                                               .orElseThrow(() -> new NotFoundException("Trend Id not found"));
        GraphDefinition trend = (GraphDefinition) LiteFactory.createDBPersistent(liteTrend);
        trendModel.buildDBPersistent(trend);
        dbPersistentDao.performDBChange(trend, TransactionType.UPDATE);
        trendModel.buildModel(trend);
        return trendModel;
    }

}
