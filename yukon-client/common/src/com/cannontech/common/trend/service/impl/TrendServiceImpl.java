package com.cannontech.common.trend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.trend.model.ResetPeakModel;
import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.trend.service.TrendService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.yukon.IDatabaseCache;

public class TrendServiceImpl implements TrendService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private ToolsEventLogService toolsEventLogService;

    @Override
    public TrendModel create(TrendModel trend, LiteYukonUser liteYukonUser) {
        GraphDefinition graph = createTrend();
        trend.buildDBPersistent(graph);
        dbPersistentDao.performDBChange(graph, TransactionType.INSERT);
        trend.buildModel(graph);
        toolsEventLogService.trendCreated(trend.getName(), liteYukonUser);
        return trend;
    }

    private GraphDefinition createTrend() {
        GraphDefinition graph = new GraphDefinition();
        return graph;
    }


    @Override
    @Transactional
    public int delete(int trendId, LiteYukonUser liteYukonUser) {

        LiteGraphDefinition liteTrend = dbCache.getAllGraphDefinitions()
                                               .stream()
                                               .filter(group -> group.getLiteID() == trendId)
                                               .findFirst()
                                               .orElseThrow(() -> new NotFoundException("Trend Id not found"));
        GraphDefinition trend = (GraphDefinition) LiteFactory.createDBPersistent(liteTrend);
        dbPersistentDao.performDBChange(trend, TransactionType.DELETE);
        toolsEventLogService.trendDeleted(liteTrend.getName(), liteYukonUser);
        return trend.getGraphDefinition().getGraphDefinitionID();
    }

    @Override
    public TrendModel update(int id, TrendModel trendModel, LiteYukonUser liteYukonUser) {
        LiteGraphDefinition liteTrend = dbCache.getAllGraphDefinitions()
                                               .stream()
                                               .filter(group -> group.getLiteID() == id)
                                               .findFirst()
                                               .orElseThrow(() -> new NotFoundException("Trend Id not found"));
        GraphDefinition trend = (GraphDefinition) LiteFactory.createDBPersistent(liteTrend);
        trendModel.buildDBPersistent(trend);
        dbPersistentDao.performDBChange(trend, TransactionType.UPDATE);
        trendModel.buildModel(trend);
        toolsEventLogService.trendUpdated(liteTrend.getName(), liteYukonUser);
        return trendModel;
    }

    @Override
    public TrendModel retrieve(int id) {
        LiteGraphDefinition liteTrend = dbCache.getAllGraphDefinitions()
                                               .stream()
                                               .filter(group -> group.getLiteID() == id)
                                               .findFirst()
                                               .orElseThrow(() -> new NotFoundException("Trend Id not found"));
        GraphDefinition trend = (GraphDefinition) LiteFactory.createDBPersistent(liteTrend);
        GraphDefinition graphDefinition = (GraphDefinition) dbPersistentDao.retrieveDBPersistent(trend);
        TrendModel trendModel = new TrendModel();
        trendModel.buildModel(graphDefinition);
        return trendModel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int resetPeak(int id, ResetPeakModel resetPeakModel, LiteYukonUser liteYukonUser) {
        // only need graphDefinitionId for retrieve
        GraphDefinition graphDefinition = new GraphDefinition();
        graphDefinition.getGraphDefinition().setGraphDefinitionID(id);
        graphDefinition = (GraphDefinition) dbPersistentDao.retrieveDBPersistent(graphDefinition);

        graphDefinition.getGraphDataSeries()
                       .stream()
                       .filter(series -> GDSTypesFuncs.isPeakType(((GraphDataSeries) series).getType().intValue()))
                       .forEach(series -> {
                           GraphDataSeries dataSeries = (GraphDataSeries) series;
                           dataSeries.setMoreData(String.valueOf(resetPeakModel.getStartDate().getMillis()));
                           dbPersistentDao.performDBChange(dataSeries, TransactionType.UPDATE);
                       });
        toolsEventLogService.resetPeak(graphDefinition.getGraphDefinition().getName(), liteYukonUser,
                resetPeakModel.getStartDate());
        return id;
    }
}
