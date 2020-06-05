package com.cannontech.common.trend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.trend.setup.service.TrendService;
import com.cannontech.yukon.IDatabaseCache;

public class TrendServiceImpl implements TrendService {
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;

    @Override
    @Transactional
    public int delete(int trendId) {

        LiteGraphDefinition liteTrend = dbCache.getAllGraphDefinitions()
                                               .stream()
                                               .filter(group -> group.getLiteID() == trendId)
                                               .findFirst()
                                               .orElseThrow(() -> new NotFoundException("Trend Id  not found"));
        GraphDefinition trend = (GraphDefinition) LiteFactory.createDBPersistent(liteTrend);
        dbPersistentDao.performDBChange(trend, TransactionType.DELETE);
        return trend.getGraphDefinition().getGraphDefinitionID();
    }
}
