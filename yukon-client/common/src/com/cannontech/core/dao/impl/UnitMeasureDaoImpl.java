package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteUnitMeasure;

/**
 * Implementation of UnitMeasureDao
 * Creation date: (7/11/2006 9:40:33 AM)
 * @author: alauinger
 */
public final class UnitMeasureDaoImpl implements UnitMeasureDao {
    private SimpleJdbcOperations jdbcOps;
  
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
    
    public LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID) {

        try {
            String sql =
                "select u.UoMID, u.UoMName, u.CalcType, u.LongName  from " +
                "unitmeasure u inner join pointunit pu " +
                "on u.uomid=pu.uomid " +
                "where pu.pointid=?";

            LiteUnitMeasure lum =
                jdbcOps.queryForObject(sql, liteUnitMeasureRowMapper, pointID);
            return lum;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public LiteUnitMeasure getLiteUnitMeasure(int uomid)  {        
        String sql = "select UoMID, UoMName, CalcType, LongName from UnitMeasure where uomid=?";
        
        LiteUnitMeasure lum = (LiteUnitMeasure)
            jdbcOps.queryForObject(sql, liteUnitMeasureRowMapper, uomid);
        
        return lum;
    }

    public LiteUnitMeasure getLiteUnitMeasure(String uomName)  {        
        String sql = "select UoMID, UoMName, CalcType, LongName from UnitMeasure where lower(LongName)=?";
        
        LiteUnitMeasure lum =
            jdbcOps.queryForObject(sql, liteUnitMeasureRowMapper, uomName.toLowerCase());
        return lum;
    }
    
    public List<LiteUnitMeasure> getLiteUnitMeasures() {
        List<LiteUnitMeasure> unitMeasures = 
            jdbcOps.query("select UoMID, UoMName, CalcType, LongName from UnitMeasure", liteUnitMeasureRowMapper);
        return unitMeasures;
    }
    
    public void setJdbcOps(SimpleJdbcOperations jdbcOperations) {
        this.jdbcOps = jdbcOperations;
    }
}
