package com.cannontech.common.validation.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.point.PointTypes;

public class RphTagUiDaoImpl implements RphTagUiDao {

	private YukonJdbcTemplate yukonJdbcTemplate;
    private PaoLoadingService paoLoadingService;
    
	@Override
    @SuppressWarnings("unchecked")
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
        	sql.append("	WHERE rt2.TagName ").eq(RphTag.OK.name());
        	sql.append(")");
    	}
    	
    	sql.append("AND rt.TagName").neq(RphTag.OK.name());
    	
    	if (tags != null && tags.size() > 0) {
    		sql.append("AND rt.TagName IN (");
    		int i = 1;
    		for (RphTag tag : tags) {
    			sql.append("'" + tag.name() + "'");
    			if (i != tags.size()) {
    				sql.append(", ");
    			}
    			i++;
    		}
    		sql.append(")");
    	}
    	
    	sql.append("ORDER BY ypo.PAObjectID, rph.ChangeId, rph.TimeStamp, rt.TagName");
    	
    	PaoProvidingReviewPointsRse rse = new PaoProvidingReviewPointsRse(pageCount);
		
		List<ReviewPoint> reviewPoints = (List<ReviewPoint>)yukonJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), rse);
		
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
		private int pageCount;

		private PaoProvidingReviewPointsRse(int pageCount) {
			this.pageCount = pageCount;
		}

		@Override
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

			List<ReviewPoint> resultList = new ArrayList<ReviewPoint>();
			
			YukonPaoRowMapper yukonPaoRowMapper = new YukonPaoRowMapper();
			while(rs.next() && pageCount-- > 0){
		        
				// displayablePao does not get set on ReviewPoint
				// build list of PaoIdentifier so that paoLoadingService can be used to get displayablePaos later and set them on ReviewPoints
				PaoIdentifier paoIdentifier = yukonPaoRowMapper.mapRow(rs, 0);
				paos.add(paoIdentifier);
				
				ReviewPoint rp = new ReviewPoint();
				rp.setChangeId(rs.getInt("ChangeId"));
				rp.setRphTag(RphTag.valueOf(rs.getString("TagName")));
				PointValueHolder pointValue = new SimplePointValue(rs.getInt("PointId"), rs.getTimestamp("TimeStamp"), PointTypes.getType(rs.getString("PointType")), rs.getDouble("Value"));
				rp.setPointValue(pointValue);
				
		        resultList.add(rp);
		    }
			
			return resultList;
		}
		
		public List<PaoIdentifier> getPaos() {
			return paos;
		}
	}

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
		this.paoLoadingService = paoLoadingService;
	}

    
    
//  public List<ReviewPoint> getReviewPoints(boolean includeOk) {
//  	
//  	SqlStatementBuilder sql = new SqlStatementBuilder();
//  	sql.append("SELECT");
//  	sql.append("ypo.PAOName,");
//  	sql.append("rph.PointId,");
//  	sql.append("p.PointType,");
//  	sql.append("rph.VALUE AS val, rph.TIMESTAMP AS t,");
//  	sql.append("prevRph.VALUE AS prevVal, prevRph.TIMESTAMP AS prevT,");
//  	sql.append("nextRph.VALUE AS nextVal, nextRph.TIMESTAMP AS nextT");
//  	sql.append("FROM (");
//  	sql.append("	SELECT ROW_NUMBER() OVER(PARTITION BY pointid ORDER BY Timestamp, changeid) orderid, pointId, value, timestamp, changeId");
//  	sql.append("	FROM RAWPOINTHISTORY");
//  	sql.append(") rph");
//  	sql.append("LEFT JOIN (");
//  	sql.append("	SELECT ROW_NUMBER() OVER(PARTITION BY pointid ORDER BY Timestamp, changeid)  orderid, pointId, value, timestamp");
//  	sql.append("	FROM RAWPOINTHISTORY");
//  	sql.append(") prevRph on rph.POINTID = prevRph.POINTID AND rph.orderid = prevRph.orderid + 1");
//  	sql.append("LEFT JOIN (");
//  	sql.append("	SELECT ROW_NUMBER() OVER(PARTITION BY pointid ORDER BY Timestamp, changeid)  orderid, pointId, value, timestamp");
//  	sql.append("	FROM RAWPOINTHISTORY");
//  	sql.append(") nextRph on rph.POINTID = nextRph.POINTID AND rph.orderid = nextRph.orderid - 1");
//  	sql.append("JOIN Point p ON (rph.POINTID = p.POINTID)");
//  	sql.append("JOIN RphTag rt ON (rph.CHANGEID = rt.ChangeId)");
//  	sql.append("JOIN YukonPAObject ypo ON (p.PAObjectID = ypo.PAObjectID)");
//  	
//  	if (!includeOk) {
//  		
//  		sql.append("WHERE rt.ChangeId NOT IN (");
//      	sql.append("	SELECT rt2.ChangeId FROM RphTag rt2 ");
//      	sql.append("	WHERE rt2.TagName ").eq(RphTag.OK.name());
//      	sql.append(")");
//      	sql.append("AND");
//      	
//  	} else {
//  		sql.append("WHERE");
//  	}
//  	
//  	sql.append("rt.TagName ").neq(RphTag.OK.name());
//  	
//  	return yukonJdbcTemplate.query(sql, new ReviewPointRowMapper());
//  }
}
