package com.cannontech.common.validation.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.validation.dao.RphTagUiDao;
import com.cannontech.common.validation.model.ReviewPoint;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.core.dao.impl.YukonPaoRowMapper;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.LongRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RphTagUiDaoImpl implements RphTagUiDao {

	private YukonJdbcTemplate yukonJdbcTemplate;
    private PaoLoadingService paoLoadingService;
    
	@Override
    public List<ReviewPoint> getReviewPoints(int afterPaoId, int pageCount, List<RphTag> tags, boolean includeOk) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT");
    	sql.append("rt.*,");
    	sql.append("rph.*,");
    	sql.append("ypo.PAObjectID, ypo.PaoName, ypo.Type,");
    	sql.append("p.PointType");
    	sql.append("FROM RphTag rt");
    	sql.append("JOIN RawPointHistory rph ON (rph.ChangeId = rt.ChangeId)");
    	sql.append("JOIN Point p ON (rph.PointId = p.PointId)");
    	sql.append("JOIN YukonPAObject ypo ON (p.PAObjectID = ypo.PAObjectID)");
    	
    	sql.append("WHERE ypo.PAObjectID").gt(afterPaoId);
    	
    	if (!includeOk) {
    		sql.append("AND rt.ChangeId NOT IN (");
        	sql.append("	SELECT rt2.ChangeId FROM RphTag rt2 ");
        	sql.append("	WHERE rt2.TagName ").eq(RphTag.OK);
        	sql.append(")");
    	}
    	
    	sql.append("AND rt.TagName").neq(RphTag.OK);
    	
    	if (tags != null && tags.size() > 0) {
    		sql.append("AND rt.TagName IN (");
    		int i = 1;
    		for (RphTag tag : tags) {
    			sql.append("'" + tag + "'");
    			if (i != tags.size()) {
    				sql.append(", ");
    			}
    			i++;
    		}
    		sql.append(")");
    	}
    	
    	sql.append("ORDER BY ypo.PAObjectID, rph.ChangeId, rph.TimeStamp, rt.TagName");
    	
    	// get
    	PaoProvidingReviewPointsRse rse = new PaoProvidingReviewPointsRse(pageCount);
    	yukonJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), rse);
    	
		List<ReviewPoint> reviewPoints = rse.getResultList();
		List<PaoIdentifier> paos = rse.getPaos();
		
		Map<PaoIdentifier, DisplayablePao> displayableDeviceLookup = paoLoadingService.getDisplayableDeviceLookup(paos);
		for (int i = 0; i < reviewPoints.size(); i++) {
			
			PaoIdentifier paoIdentifier = paos.get(i);
			DisplayablePao displayablePao = displayableDeviceLookup.get(paoIdentifier);
			reviewPoints.get(i).setDisplayablePao(displayablePao);
		}
		
    	return reviewPoints;
    }
    
    private final class PaoProvidingReviewPointsRse implements ResultSetExtractor {
    	
		private List<PaoIdentifier> paos = new ArrayList<PaoIdentifier>();
		private List<ReviewPoint> resultList = new ArrayList<ReviewPoint>();
		private int pageCount;

		private PaoProvidingReviewPointsRse(int pageCount) {
			this.pageCount = pageCount;
		}

		@Override
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

			YukonPaoRowMapper yukonPaoRowMapper = new YukonPaoRowMapper();
			while(rs.next() && pageCount-- > 0){
		        
				// displayablePao does not get set on ReviewPoint
				// build list of PaoIdentifier so that paoLoadingService can be used to get displayablePaos later and set them on ReviewPoints
				PaoIdentifier paoIdentifier = yukonPaoRowMapper.mapRow(rs, 0);
				paos.add(paoIdentifier);
				
				ReviewPoint rp = new ReviewPoint();
				rp.setChangeId(rs.getLong("ChangeId"));
				rp.setRphTag(RphTag.valueOf(rs.getString("TagName")));
				
				PointValueBuilder builder = PointValueBuilder.create();
	            builder.withResultSet(rs);
	            builder.withType(rs.getString("pointtype"));
	            PointValueQualityHolder pvqh = builder.build();
	            
				rp.setPointValue(pvqh);
				
		        resultList.add(rp);
		    }
			
			return resultList;
		}
		
		public List<ReviewPoint> getResultList() {
			return resultList;
		}
		public List<PaoIdentifier> getPaos() {
			return paos;
		}
	}
    
    public Map<RphTag, Integer> getAllValidationTagCounts() {
    	
    	Map<RphTag, Integer> countMap = Maps.newHashMapWithExpectedSize(RphTag.getAllValidation().size());
    	
    	for (RphTag rphTag : RphTag.getAllValidation()) {
    		
    		SqlStatementBuilder sql = new SqlStatementBuilder();
    		sql.append("SELECT COUNT(*)");
    		sql.append("FROM RphTag rt");
    		sql.append("WHERE rt.TagName").eq(rphTag);
    		sql.append("AND rt.ChangeId NOT IN (");
        	sql.append("	SELECT rt3.ChangeId");
        	sql.append("	FROM RphTag rt3");
        	sql.append("	WHERE rt3.TagName").eq(RphTag.OK);
        	sql.append(")");
        	
        	int c = yukonJdbcTemplate.queryForInt(sql);
        	countMap.put(rphTag, c);
    	}
    	
    	return countMap;
    }
    
    @Override
    public List<Long> findMatchingChangeIds(Set<RphTag> set, Set<RphTag> mask) {
        Set<RphTag> mustNotHave = Sets.difference(mask, set);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct ChangeId");
        sql.append("from RphTag rt");
        sql.append("where");
        sql.append(  "rt.ChangeId NOT IN (");
        sql.append(    "select rt2.ChangeId from RphTag rt2 ");
        sql.append(    "where rt2.TagName ").in(mustNotHave);
        sql.append(  ")");
        sql.append("  AND rt.TagName").in(set);
        sql.append("group by ChangeId");
        sql.append("having count(*)").eq(set.size());
        
        List<Long> result = yukonJdbcTemplate.query(sql, new LongRowMapper());
        return result;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
		this.paoLoadingService = paoLoadingService;
	}
}
