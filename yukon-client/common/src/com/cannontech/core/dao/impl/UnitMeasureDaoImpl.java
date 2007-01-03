package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteUnitMeasure;

/**
 * Implementation of UnitMeasureDao
 * Creation date: (7/11/2006 9:40:33 AM)
 * @author: alauinger
 */
public final class UnitMeasureDaoImpl implements UnitMeasureDao {
    private JdbcOperations jdbcOps;
  
    String liteUoMSql = 
        "select UoMID, UoMName, CalcType, LongName from UnitMeasure ";
    
    private static final RowMapper liteUnitMeasureRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createLiteUnitMeasure(rs);
        }
    };
    
/* (non-Javadoc)
 * @see com.cannontech.core.dao.UnitMeasureDao#getLiteUnitMeasureByPointID(int)
 */
public LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID) {
    
    String sql =
    "select u.UoMID, u.UoMName, u.CalcType, u.LongName  from " +
    "unitmeasure u inner join pointunit pu " +
    "on u.uomid=pu.uomid " +
    "where pu.pointid=?";
    
    LiteUnitMeasure lum = (LiteUnitMeasure)
        jdbcOps.queryForObject(sql, new Object[] {pointID}, liteUnitMeasureRowMapper);
    return lum;
}

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.UnitMeasureDao#getLiteUnitMeasure(int)
     */
    public LiteUnitMeasure getLiteUnitMeasure(int uomid)  {        
        String sql = liteUoMSql + " where uomid=?";
        
        LiteUnitMeasure lum = (LiteUnitMeasure)
            jdbcOps.queryForObject(sql, new Object[] { uomid }, liteUnitMeasureRowMapper);
        
        return lum;
    }

    public LiteUnitMeasure getLiteUnitMeasure(String uomName)  {        
        String sql = liteUoMSql + " where LongName=?";
        
        LiteUnitMeasure lum = (LiteUnitMeasure)
        jdbcOps.queryForObject(sql, new Object[] { uomName }, liteUnitMeasureRowMapper);
        
        return lum;
    }
    
    public List<LiteUnitMeasure> getLiteUnitMeasures() {
        List<LiteUnitMeasure> unitMeasures = 
            jdbcOps.query(liteUoMSql, liteUnitMeasureRowMapper);
        return unitMeasures;
    }
    
    public void setJdbcOps(JdbcOperations jdbcOperations) {
        this.jdbcOps = jdbcOperations;
    }
    
    private static LiteUnitMeasure createLiteUnitMeasure(ResultSet rset) throws SQLException{
        int uomID = rset.getInt(1);
        String unitMeasureName = rset.getString(2).trim();
        int unitMeasureCalcType = rset.getInt(3);
        String longName = rset.getString(4).trim();

        LiteUnitMeasure lum =
            new LiteUnitMeasure( uomID, unitMeasureName, unitMeasureCalcType, longName );
        
        return lum;
    }
}
