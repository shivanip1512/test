package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

/**
 * Implementation of UnitMeasureDao
 * Creation date: (7/11/2006 9:40:33 AM)
 * @author: alauinger
 */
public class UnitMeasureDaoImpl implements UnitMeasureDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private ChunkingSqlTemplate chunkingSqlTemplate;

    @PostConstruct
    public void init() {
        chunkingSqlTemplate = new ChunkingSqlTemplate(jdbcTemplate);
    }
    
    private static final RowMapper<LiteUnitMeasure> liteUnitMeasureRowMapper = 
        new RowMapper<LiteUnitMeasure>() {
        @Override
        public LiteUnitMeasure mapRow(ResultSet rs, int rowNum) throws SQLException {
            int uomID = rs.getInt("UoMID");
            String unitMeasureName = rs.getString("UoMName").trim();
            int unitMeasureCalcType = rs.getInt("CalcType");
            String longName = rs.getString("LongName").trim();
            
            LiteUnitMeasure lum =
                new LiteUnitMeasure( uomID, unitMeasureName, unitMeasureCalcType, longName );
            
            return lum;
        }
    };
    
    @Override
    public LiteUnitMeasure getLiteUnitMeasureByPointID(int pointId) {

        try {
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT UM.UoMId, UM.UoMName, UM.CalcType, UM.LongName");
            sql.append("FROM UnitMeasure UM");
            sql.append("  JOIN PointUnit PU ON UM.UoMId = PU.UoMId");
            sql.append("WHERE PU.PointId").eq(pointId);
            
            LiteUnitMeasure lum = jdbcTemplate.queryForObject(sql, liteUnitMeasureRowMapper);
            return lum;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public Table<Integer, PointIdentifier, LiteUnitMeasure> getLiteUnitMeasureByPaoIdAndPoint(List<? extends YukonPao> paos) {
        final HashBasedTable<Integer, PointIdentifier, LiteUnitMeasure> table = HashBasedTable.create();
        SqlFragmentGenerator<Integer> generator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT PAObjectID, PointType, PointOffset, um.CalcType, um.Formula, um.LongName, um.UomId, um.UomName");
                sql.append("FROM Point p");
                sql.append("JOIN PointUnit pu ON p.pointid = pu.pointid");
                sql.append("JOIN UnitMeasure um ON um.uomid = pu.uomid");
                sql.append("WHERE PaObjectId").in(subList);
                return sql;
            }
        };
        Iterable<Integer> paoIds = Lists.transform(paos, YukonPao.TO_PAO_ID);
        chunkingSqlTemplate.query(generator, paoIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                LiteUnitMeasure unitMeasure = liteUnitMeasureRowMapper.mapRow(rs.getResultSet(), 0);
                int paoId = rs.getInt("PAObjectID");
                PointIdentifier PointIdentifier = rs.getPointIdentifier("PointType", "PointOffset");
                table.put(paoId, PointIdentifier, unitMeasure);
            }
        });
        return table;
    }

    @Override
    public LiteUnitMeasure getLiteUnitMeasure(int uomid)  {        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UoMId, UoMName, CalcType, LongName");
        sql.append("FROM UnitMeasure");
        sql.append("WHERE UoMId").eq(uomid);
        
        LiteUnitMeasure lum = jdbcTemplate.queryForObject(sql, liteUnitMeasureRowMapper);
        
        return lum;
    }

    @Override
    public LiteUnitMeasure getLiteUnitMeasure(String uomName)  {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UoMId, UoMName, CalcType, LongName");
        sql.append("FROM UnitMeasure");
        sql.append("WHERE LOWER(LongName").eq(uomName.toLowerCase());
        
        LiteUnitMeasure lum = jdbcTemplate.queryForObject(sql, liteUnitMeasureRowMapper);
        return lum;
    }
    
    @Override
    public List<LiteUnitMeasure> getLiteUnitMeasures() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UoMId, UoMName, CalcType, LongName");
        sql.append("FROM UnitMeasure");

        List<LiteUnitMeasure> unitMeasures = jdbcTemplate.query(sql, liteUnitMeasureRowMapper);
        return unitMeasures;
    }
}
