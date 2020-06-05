package com.cannontech.common.trend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.trend.service.TrendService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.graph.GraphDefinition;

public class TrendServiceImpl implements TrendService {

    @Autowired private DBPersistentDao dbPersistentDao;

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


}
