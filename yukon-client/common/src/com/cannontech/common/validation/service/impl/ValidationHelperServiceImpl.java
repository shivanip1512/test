package com.cannontech.common.validation.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.VeeReviewEventLogService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.validation.dao.RphTagDao;
import com.cannontech.common.validation.dao.RphTagUiDao;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.common.validation.service.ValidationHelperService;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class ValidationHelperServiceImpl implements ValidationHelperService {
    private RawPointHistoryDao rawPointHistoryDao;
    private RphTagDao rphTagDao;
    private RphTagUiDao rphTagUiDao;
    private VeeReviewEventLogService veeReviewEventLogService;
    private PersistedSystemValueDao persistedSystemValueDao;
    private YukonJdbcOperations yukonJdbcOperations;
    private Set<RphTag> validationPlusOkTags;
    {
        Builder<RphTag> builder = ImmutableSet.builder();
        builder.addAll(RphTag.getAllValidation());
        builder.add(RphTag.OK);
        validationPlusOkTags = builder.build();
    }

    @Override
    public void acceptAllMatchingRows(Set<RphTag> tags, LiteYukonUser liteYukonUser) {
        List<Integer> changeIds = rphTagUiDao.findMatchingChangeIds(tags, validationPlusOkTags);
        for (int changeId : changeIds) {
            acceptRawPointHistoryRow(changeId, liteYukonUser);
        }
    }

    @Override
    public void deleteAllMatchingRows(Set<RphTag> tags, LiteYukonUser liteYukonUser) {
        List<Integer> changeIds = rphTagUiDao.findMatchingChangeIds(tags, validationPlusOkTags);
        for (int changeId : changeIds) {
            deleteRawPointHistoryRow(changeId, liteYukonUser);
        }
    }

    @Override
    public void resetValidationEngine(Date since) {
        int changeIdToResetTo = 0;
        if (since != null) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("select min(changeId)");
            sql.append("from RawPointHistory");
            sql.append("where TimeStamp").gte(since);
            
            changeIdToResetTo = yukonJdbcOperations.queryForInt(sql) - 1; // one less because we start after
        }
        persistedSystemValueDao.setValue(PersistedSystemValueKey.VALIDATION_ENGINE_LAST_CHANGE_ID, changeIdToResetTo);
    }
    
    @Override
    public void deleteRawPointHistoryRow(int changeId, LiteYukonUser user) {
        
        PointValueQualityHolder p = rawPointHistoryDao.getPointValueQualityForChangeId(changeId);
        
        rawPointHistoryDao.deleteValue(changeId);
        veeReviewEventLogService.deletePointValue(changeId, p.getValue(), p.getPointDataTimeStamp(), p.getType(), user);
    }
    
    @Override
    public void acceptRawPointHistoryRow(int changeId, LiteYukonUser user) {
        
        PointValueQualityHolder p = rawPointHistoryDao.getPointValueQualityForChangeId(changeId);
        
        rphTagDao.insertTag(changeId, RphTag.OK);
        veeReviewEventLogService.acceptPointValue(changeId, p.getValue(), p.getPointDataTimeStamp(), p.getType(), user);
        
        PointValueQualityHolder pointValueQualityHolder = rawPointHistoryDao.getPointValueQualityForChangeId(changeId);
        if (pointValueQualityHolder.getPointQuality().equals(PointQuality.Questionable)) {
            rawPointHistoryDao.changeQuality(changeId, PointQuality.Normal);
            veeReviewEventLogService.updateQuestionableQuality(changeId, p.getValue(), p.getPointDataTimeStamp(), p.getType(), user);
        }
    }
    
    @Autowired
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }
    
    @Autowired
    public void setRphTagDao(RphTagDao rphTagDao) {
        this.rphTagDao = rphTagDao;
    }
    
    @Autowired
    public void setRphTagUiDao(RphTagUiDao rphTagUiDao) {
        this.rphTagUiDao = rphTagUiDao;
    }
    
    @Autowired
    public void setVeeReviewEventLogService(VeeReviewEventLogService veeReviewEventLogService) {
        this.veeReviewEventLogService = veeReviewEventLogService;
    }
    
    @Autowired
    public void setPersistedSystemValueDao(PersistedSystemValueDao persistedSystemValueDao) {
        this.persistedSystemValueDao = persistedSystemValueDao;
    }
    
    @Autowired
    public void setYukonJdbcOperations(YukonJdbcOperations yukonJdbcOperations) {
        this.yukonJdbcOperations = yukonJdbcOperations;
    }

}
