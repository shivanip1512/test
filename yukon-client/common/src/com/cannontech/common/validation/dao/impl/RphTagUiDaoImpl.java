package com.cannontech.common.validation.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.validation.dao.RphTagUiDao;
import com.cannontech.common.validation.model.ReviewPoint;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class RphTagUiDaoImpl implements RphTagUiDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoLoadingService paoLoadingService;

    @Override
    public SearchResults<ReviewPoint> getReviewPoints(PagingParameters pagingParameters, List<RphTag> tags) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM (");
        sql.append("SELECT rt.TagName, rph.*, ypo.PaobjectId, ypo.PaoName, ypo.Type, p.PointType,");
        sql.append("ROW_NUMBER() OVER (ORDER BY ypo.PAObjectID, rph.ChangeId, rph.TimeStamp, rt.TagName) AS rn");
        sql.append("FROM RphTag rt");
        sql.append(    "JOIN RawPointHistory rph ON (rph.ChangeId = rt.ChangeId)");
        sql.append(    "JOIN Point p ON (rph.PointId = p.PointId)");
        sql.append(    "JOIN YukonPaobject ypo ON (p.PaobjectID = ypo.PaobjectId)");

        //The following four lines are here because the tables are not sane.
        //When a point is accepted another row is added to RphTag with a changeId of 'OK'. 
        //This means the same changeId has 'OK' and some other value that is not 'OK'.
        //This SQL removes any Ids that have any rows with OK attached to them.
        sql.append("AND rt.ChangeId NOT IN (");
        sql.append("    SELECT rt2.ChangeId FROM RphTag rt2 ");
        sql.append("    WHERE rt2.TagName ").eq_k(RphTag.OK);
        sql.append(")");
        
        sql.append("WHERE rt.TagName").neq_k(RphTag.OK);
        sql.append(    "AND rt.TagName").in_k(tags);

        sql.append(") results");
        sql.append("WHERE rn BETWEEN " + pagingParameters.getOneBasedStartIndex() + " AND " + 
                pagingParameters.getOneBasedEndIndex());

        // get
        ReviewPointRowMapper rowMapper = new ReviewPointRowMapper();
        List<ReviewPoint> reviewPoints = yukonJdbcTemplate.query(sql, rowMapper);

        Collection<YukonPao> paos = rowMapper.reviewPointsByPao.keySet();

        Map<PaoIdentifier, DisplayablePao> displayablePaos = paoLoadingService.getDisplayableDeviceLookup(paos);
        for (Map.Entry<PaoIdentifier, DisplayablePao> entry : displayablePaos.entrySet()) {
            PaoIdentifier pao = entry.getKey();
            DisplayablePao displayablePao = entry.getValue();
            for (ReviewPoint reviewPoint : rowMapper.reviewPointsByPao.get(pao)) {
                reviewPoint.setDisplayablePao(displayablePao);
            }
        }

        SearchResults<ReviewPoint> results = SearchResults.pageBasedForSublist(reviewPoints, 
                pagingParameters.getPage(), pagingParameters.getItemsPerPage(), reviewPoints.size());
        return results;
    }

    private final static class ReviewPointRowMapper implements YukonRowMapper<ReviewPoint> {
        // The displayablePao property does not get set on ReviewPoint.  Keep a map of PaoIdentifier by changeId
        // we can call paoLoadingService later to get displayablePaos.
        Multimap<YukonPao, ReviewPoint> reviewPointsByPao = ArrayListMultimap.create();

        @Override
        public ReviewPoint mapRow(YukonResultSet rs) throws SQLException {
            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PaobjectId", "Type");

            ReviewPoint reviewPoint = new ReviewPoint();
            long changeId = rs.getLong("ChangeId");
            reviewPoint.setChangeId(changeId);
            reviewPoint.setRphTag(rs.getEnum("TagName", RphTag.class));

            PointValueBuilder builder = PointValueBuilder.create();
            builder.withResultSet(rs);
            builder.withType(rs.getString("pointtype"));
            PointValueQualityHolder pvqh = builder.build();

            reviewPoint.setPointValue(pvqh);
            reviewPointsByPao.put(paoIdentifier, reviewPoint);

            return reviewPoint;
        }
    }

    @Override
    public Map<RphTag, Integer> getAllValidationTagCounts() {
        final Map<RphTag, Integer> countMap = Maps.newHashMapWithExpectedSize(RphTag.getAllValidation().size());
        
        //the countmap is initially initialized to zero such that
        //even if the database does have count for some attributes for example 'UDC'
        //it doesn't return a null value for the count 
        for(RphTag rphtag:RphTag.getAllValidation()){
            countMap.put(rphtag,0);
        }
        
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT rt.Tagname, COUNT(*) as counter");
            sql.append("FROM RphTag rt");
            sql.append("Where rt.ChangeId NOT IN (");
            sql.append("	SELECT rt3.ChangeId");
            sql.append("	FROM RphTag rt3");
            sql.append("	WHERE rt3.TagName").eq_k(RphTag.OK);
            sql.append(")");
            sql.append("group by TagName");
            
            yukonJdbcTemplate.query(sql,new YukonRowCallbackHandler(){
                @Override
                public void processRow(YukonResultSet rs) throws SQLException {
                 
                    countMap.put(rs.getEnum("Tagname", RphTag.class), rs.getInt("counter"));
                    
                }
            });
            
        return countMap;
    }
    
    @Override
    public int getTotalValidationTagCounts() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM RphTag rt");
        sql.append("WHERE rt.TagName").neq_k(RphTag.OK);
        sql.append("AND rt.ChangeId NOT IN (");
        sql.append("    SELECT rt3.ChangeId");
        sql.append("    FROM RphTag rt3");
        sql.append("    WHERE rt3.TagName").eq_k(RphTag.OK);
        sql.append(")");
        int c = yukonJdbcTemplate.queryForInt(sql);
        
        return c;
    }

    @Override
    public List<Long> findMatchingChangeIds(Set<RphTag> set) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ChangeId");
        sql.append("from RphTag rt");
        sql.append("where rt.TagName ").in(set);
        sql.append("group by ChangeId");
        sql.append("having count(*) = 1");

        List<Long> result = yukonJdbcTemplate.query(sql, RowMapper.LONG);
        return result;
    }
}
