package com.cannontech.common.validation.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.VeeReviewEventLogService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.validation.dao.RphTagDao;
import com.cannontech.common.validation.dao.RphTagUiDao;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.common.validation.service.ValidationHelperService;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;

public class ValidationHelperServiceImpl implements ValidationHelperService {
    private static final Logger log = YukonLogManager.getLogger(ValidationHelperServiceImpl.class);
    
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private RphTagDao rphTagDao;
    @Autowired private RphTagUiDao rphTagUiDao;
    @Autowired private VeeReviewEventLogService veeReviewEventLogService;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PointDao pointDao;
    @Autowired private PaoLoadingService paoLoadingService;

    @Override
    public void acceptAllMatchingRows(Set<RphTag> tags, LiteYukonUser liteYukonUser) {
        List<Long> changeIds = rphTagUiDao.findMatchingChangeIds(tags);
        for (long changeId : changeIds) {
            acceptRawPointHistoryRow(changeId, liteYukonUser);
        }
    }

    @Override
    public void deleteAllMatchingRows(Set<RphTag> tags, LiteYukonUser liteYukonUser) {
        List<Long> changeIds = rphTagUiDao.findMatchingChangeIds(tags);
        for (long changeId : changeIds) {
            deleteRawPointHistoryRow(changeId, liteYukonUser);
        }
    }

    @Override
    public void resetValidationEngine(Date since) {
        long changeIdToResetTo = 0;
        if (since != null) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("select min(changeId)");
            sql.append("from RawPointHistory");
            sql.append("where TimeStamp").gte(since);
            
            changeIdToResetTo = jdbcTemplate.queryForLong(sql) - 1; // one less because we start after
        }
        persistedSystemValueDao.setValue(PersistedSystemValueKey.VALIDATION_ENGINE_LAST_CHANGE_ID, changeIdToResetTo);
    }
    
    @Override
    public void deleteRawPointHistoryRow(long changeId, LiteYukonUser user) {
        
    	PointValueQualityHolder pointValueQualityHolder = rawPointHistoryDao.getPointValueQualityForChangeId(changeId);
		int pointId = pointValueQualityHolder.getId();
		PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(pointId);
		DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(paoPointIdentifier.getPaoIdentifier());
        
        rawPointHistoryDao.deleteValue(changeId);
        
        veeReviewEventLogService.deletePointValue(changeId, 
												  pointValueQualityHolder.getValue(), 
												  pointValueQualityHolder.getPointDataTimeStamp(), 
												  displayablePao.getName(), 
												  paoPointIdentifier.getPaoIdentifier().getPaoType(),
												  pointId,
												  paoPointIdentifier.getPointIdentifier().getPointType(),
												  paoPointIdentifier.getPointIdentifier().getOffset(),
												  user);
    }
    
    @Override
    public void acceptRawPointHistoryRow(long changeId, LiteYukonUser user) {
        
        // because the RawPointHistory table and the Point table can become unattached,
        // we must be careful that this changeId is for a point/pao that still exists
        
        rphTagDao.acceptChangeId(changeId);
        
    	PointValueQualityHolder pointValueQualityHolder;
        try {
            pointValueQualityHolder = rawPointHistoryDao.getPointValueQualityForChangeId(changeId);
        } catch (EmptyResultDataAccessException e) {
            log.info("acceptRawPointHistoryRow processed for a non-existing point, changeId=" + changeId);
            return;
        }
		int pointId = pointValueQualityHolder.getId();
		PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(pointId);
		DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(paoPointIdentifier.getPaoIdentifier());
        
        veeReviewEventLogService.acceptPointValue(changeId, 
        										  pointValueQualityHolder.getValue(), 
        									      pointValueQualityHolder.getPointDataTimeStamp(), 
												  displayablePao.getName(), 
												  paoPointIdentifier.getPaoIdentifier().getPaoType(),
												  pointId,
												  paoPointIdentifier.getPointIdentifier().getPointType(),
												  paoPointIdentifier.getPointIdentifier().getOffset(),
												  user);
        
        if (pointValueQualityHolder.getPointQuality().equals(PointQuality.Questionable)) {
            
        	rawPointHistoryDao.changeQuality(changeId, PointQuality.Normal);
            
        	veeReviewEventLogService.updateQuestionableQuality(changeId, 
															   pointValueQualityHolder.getValue(), 
															   pointValueQualityHolder.getPointDataTimeStamp(), 
															   displayablePao.getName(), 
															   paoPointIdentifier.getPaoIdentifier().getPaoType(),
															   pointId,
															   paoPointIdentifier.getPointIdentifier().getPointType(),
															   paoPointIdentifier.getPointIdentifier().getOffset(),
															   user);
        }
    }
}
