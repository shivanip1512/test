package com.cannontech.core.dao.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public class UnitMeasureDaoImpl implements UnitMeasureDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private ChunkingSqlTemplate chunkingSqlTemplate;

    @PostConstruct
    public void init() {
        chunkingSqlTemplate = new ChunkingSqlTemplate(jdbcTemplate);
    }
    
    @Override
    public UnitOfMeasure getUnitMeasureByPointID(int pointId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT UomId");
            sql.append("FROM PointUnit");
            sql.append("WHERE PointId").eq(pointId);
            int uomId = jdbcTemplate.queryForInt(sql);
            return UnitOfMeasure.getForId(uomId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Table<Integer, PointIdentifier, UnitOfMeasure> getUnitMeasureByPaoIdAndPoint(List<? extends YukonPao> paos) {
        
        SqlFragmentGenerator<Integer> generator = (List<Integer> subList) -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT PAObjectID, PointType, PointOffset, UomId");
            sql.append("FROM Point p");
            sql.append("JOIN PointUnit pu ON p.pointid = pu.pointid");
            sql.append("WHERE PaObjectId").in(subList);
            return sql;
        };
        
        Iterable<Integer> paoIds = Lists.transform(paos, YukonPao.TO_PAO_ID);
        final HashBasedTable<Integer, PointIdentifier, UnitOfMeasure> table = HashBasedTable.create();
        
        chunkingSqlTemplate.query(generator, paoIds, (YukonResultSet rs) -> {
            UnitOfMeasure unitMeasure = UnitOfMeasure.getForId(rs.getInt("UomId"));
            int paoId = rs.getInt("PAObjectID");
            PointIdentifier pointIdentifier = rs.getPointIdentifier("PointType", "PointOffset");
            table.put(paoId, pointIdentifier, unitMeasure);
        });
        
        return table;
    }
}
