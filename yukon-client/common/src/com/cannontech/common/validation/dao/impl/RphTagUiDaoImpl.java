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
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.validation.dao.RphTagUiDao;
import com.cannontech.common.validation.model.ReviewPoint;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.TypeRowMapper;
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
        sql.append("SELECT rt.PeakUp, rt.PeakDown, rt.UnreasonableUp, rt.UnreasonableDown, rt.ChangeOut,");
        sql.append("rph.*, ypo.PaobjectId, ypo.PaoName, ypo.Type, p.PointType,");
        sql.append("ROW_NUMBER() OVER (ORDER BY ypo.PAObjectID, rph.ChangeId, rph.TimeStamp, rt.PeakUp, rt.PeakDown, rt.UnreasonableUp, rt.UnreasonableDown, rt.ChangeOut) AS rn");
        sql.append("FROM RphTag rt");
        sql.append("JOIN RawPointHistory rph ON (rph.ChangeId = rt.ChangeId)");
        sql.append("JOIN Point p ON (rph.PointId = p.PointId)");
        sql.append("JOIN YukonPaobject ypo ON (p.PaobjectID = ypo.PaobjectId)");
        sql.append("WHERE rt.Accepted").eq_k(0);

        SqlFragmentCollection orCollection = SqlFragmentCollection.newOrCollection();
        for (RphTag rphTag : tags) {
            // this assumes that RphTag enums match the column names, otherwise suggest adding columnname field to enum
            SqlStatementBuilder clause = new SqlStatementBuilder();
            clause.append(rphTag).eq_k(1);
            orCollection.add(clause);
        }
        if (!orCollection.isEmpty()) {
            sql.append("AND").appendFragment(orCollection);
        }
        
        sql.append(") results");
        sql.append("WHERE rn BETWEEN " + pagingParameters.getOneBasedStartIndex() + " AND "
            + pagingParameters.getOneBasedEndIndex());

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

        SearchResults<ReviewPoint> results =
            SearchResults.pageBasedForSublist(reviewPoints, pagingParameters.getPageNumber(),
                pagingParameters.getItemsPerPage(), reviewPoints.size());
        return results;
    }

    private final static class ReviewPointRowMapper implements YukonRowMapper<ReviewPoint> {
        // The displayablePao property does not get set on ReviewPoint. Keep a map of PaoIdentifier by
        // changeId
        // we can call paoLoadingService later to get displayablePaos.
        Multimap<YukonPao, ReviewPoint> reviewPointsByPao = ArrayListMultimap.create();

        @Override
        public ReviewPoint mapRow(YukonResultSet rs) throws SQLException {
            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PaobjectId", "Type");

            ReviewPoint reviewPoint = new ReviewPoint();
            long changeId = rs.getLong("ChangeId");
            reviewPoint.setChangeId(changeId);
            
            if (rs.getBoolean("PeakUp")) reviewPoint.addRphTag(RphTag.PEAKUP);
            if (rs.getBoolean("PeakDown")) reviewPoint.addRphTag(RphTag.PEAKDOWN);
            if (rs.getBoolean("UnreasonableUp")) reviewPoint.addRphTag(RphTag.UNREASONABLEUP);
            if (rs.getBoolean("UnreasonableDown")) reviewPoint.addRphTag(RphTag.UNREASONABLEDOWN);
            if (rs.getBoolean("ChangeOut")) reviewPoint.addRphTag(RphTag.CHANGEOUT);

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

        SqlStatementBuilder sql = new SqlStatementBuilder();
        // This assumes the aliased names equal the RphTag enums
        sql.append("SELECT"); 
        sql.append("SUM (CASE WHEN PeakUp = 1 THEN 1 ELSE 0 END) AS PeakUp,"); 
        sql.append("SUM (CASE WHEN PeakDown = 1 THEN 1 ELSE 0 END) AS PeakDown,"); 
        sql.append("SUM (CASE WHEN UnreasonableUp = 1 THEN 1 ELSE 0 END) AS UnreasonableUp,"); 
        sql.append("SUM (CASE WHEN UnreasonableDown = 1 THEN 1 ELSE 0 END) AS UnreasonableDown,"); 
        sql.append("SUM (CASE WHEN ChangeOut = 1 THEN 1 ELSE 0 END) AS ChangeOut");
        sql.append("FROM RphTag");
        sql.append("WHERE Accepted").eq_k(0);

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                countMap.put(RphTag.PEAKUP, rs.getInt("PeakUp"));
                countMap.put(RphTag.PEAKDOWN, rs.getInt("PeakDown"));
                countMap.put(RphTag.UNREASONABLEUP, rs.getInt("UnreasonableUp"));
                countMap.put(RphTag.UNREASONABLEDOWN, rs.getInt("UnreasonableDown"));
                countMap.put(RphTag.CHANGEOUT, rs.getInt("ChangeOut"));
            }
        });

        return countMap;
    }

    @Override
    public int getTotalValidationTagCounts() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT"); 
        sql.append("SUM (CASE WHEN PeakUp = 1 THEN 1 ELSE 0 END) +"); 
        sql.append("SUM (CASE WHEN PeakDown = 1 THEN 1 ELSE 0 END)  +"); 
        sql.append("SUM (CASE WHEN UnreasonableUp = 1 THEN 1 ELSE 0 END)  +"); 
        sql.append("SUM (CASE WHEN UnreasonableDown = 1 THEN 1 ELSE 0 END)  +"); 
        sql.append("SUM (CASE WHEN ChangeOut = 1 THEN 1 ELSE 0 END)");
        sql.append("FROM RphTag");
        sql.append("WHERE Accepted").eq_k(0);

        return yukonJdbcTemplate.queryForInt(sql);
    }

    @Override
    public List<Long> findMatchingChangeIds(Set<RphTag> set) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ChangeId");
        sql.append("FROM RphTag rt");
        sql.append("WHERE PeakUp").eq_k(set.contains(RphTag.PEAKUP) ? 1 : 0);
        sql.append("AND PeakDown").eq_k(set.contains(RphTag.PEAKDOWN) ? 1 : 0);
        sql.append("AND UnreasonableUp").eq_k(set.contains(RphTag.UNREASONABLEUP) ? 1 : 0);
        sql.append("AND UnreasonableDown").eq_k(set.contains(RphTag.UNREASONABLEDOWN) ? 1 : 0);
        sql.append("AND ChangeOut").eq_k(set.contains(RphTag.CHANGEOUT) ? 1 : 0);

        List<Long> result = yukonJdbcTemplate.query(sql, TypeRowMapper.LONG);
        return result;
    }
}
