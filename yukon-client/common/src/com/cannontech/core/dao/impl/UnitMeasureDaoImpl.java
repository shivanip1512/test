package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.data.lite.LiteUnitMeasure;

/**
 * Implementation of UnitMeasureDao
 * Creation date: (7/11/2006 9:40:33 AM)
 * @author: alauinger
 */
public final class UnitMeasureDaoImpl implements UnitMeasureDao {
    private YukonJdbcOperations yukonJdbcOperations;
    
    
    private static final ParameterizedRowMapper<LiteUnitMeasure> liteUnitMeasureRowMapper = 
        new ParameterizedRowMapper<LiteUnitMeasure>() {
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
            
            LiteUnitMeasure lum = yukonJdbcOperations.queryForObject(sql, liteUnitMeasureRowMapper);
            return lum;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public LiteUnitMeasure getLiteUnitMeasure(int uomid)  {        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UoMId, UoMName, CalcType, LongName");
        sql.append("FROM UnitMeasure");
        sql.append("WHERE UoMId").eq(uomid);
        
        LiteUnitMeasure lum = yukonJdbcOperations.queryForObject(sql, liteUnitMeasureRowMapper);
        
        return lum;
    }

    @Override
    public LiteUnitMeasure getLiteUnitMeasure(String uomName)  {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UoMId, UoMName, CalcType, LongName");
        sql.append("FROM UnitMeasure");
        sql.append("WHERE LOWER(LongName").eq(uomName.toLowerCase());
        
        LiteUnitMeasure lum = yukonJdbcOperations.queryForObject(sql, liteUnitMeasureRowMapper);
        return lum;
    }
    
    @Override
    public List<LiteUnitMeasure> getLiteUnitMeasures() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UoMId, UoMName, CalcType, LongName");
        sql.append("FROM UnitMeasure");

        List<LiteUnitMeasure> unitMeasures = yukonJdbcOperations.query(sql, liteUnitMeasureRowMapper);
        return unitMeasures;
    }
    
    // DI Setters
    @Autowired
    public void setYukonJdbcOperations(YukonJdbcOperations yukonJdbcOperations) {
        this.yukonJdbcOperations = yukonJdbcOperations;
    }
}
