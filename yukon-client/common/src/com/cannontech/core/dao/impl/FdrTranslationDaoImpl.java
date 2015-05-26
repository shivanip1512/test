package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class FdrTranslationDaoImpl implements FdrTranslationDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static final ParameterizedRowMapper<FdrTranslation> mapper;
    
    static {
        mapper = new ParameterizedRowMapper<FdrTranslation>() {
            
            public FdrTranslation mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                FdrTranslation fdrTranslation = new FdrTranslation();
                fdrTranslation.setPointId(rs.getInt("PointID"));
                
                String direction = rs.getString("DirectionType");
                fdrTranslation.setDirection(FdrDirection.getEnum(direction));
                
                String interfacetype = rs.getString("InterfaceType");
                fdrTranslation.setInterfaceType(FdrInterfaceType.valueOf(interfacetype));
                
                String translation = rs.getString("Translation");
                fdrTranslation.setTranslation(translation);
                Map<String,String> parameterMap = fdrTranslation.getParameterMap();
                
                parameterMap.clear();
                    
                String [] parameters = translation.split(";");
                
                for( String paramSet : parameters ) {
                    int splitSpot = paramSet.indexOf(":");
                    if (splitSpot != -1) {
                        parameterMap.put(paramSet.substring(0, splitSpot), paramSet.substring(splitSpot+1));
                    }
                }
                
                return fdrTranslation;
            }
        };
    }
    
    public boolean add(FdrTranslation translation) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("FdrTranslation");
        sink.addValue("PointId", translation.getPointId());
        sink.addValue("DirectionType", translation.getDirection().getValue());
        sink.addValue("InterfaceType", translation.getFdrInterfaceType().toString());
        sink.addValue("Destination", translation.getDestination());
        sink.addValue("Translation", translation.getTranslation());
        
        int rowsAffected = jdbcTemplate.update(sql);
        
        return rowsAffected == 1;
    }
    
    public FdrTranslation getByPointIdAndType(int pointId, FdrInterfaceType type) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PointId, DirectionType, InterfaceType, Translation");
        sql.append("FROM FdrTranslation");
        sql.append("WHERE PointId").eq(pointId);
        sql.append(  "AND InterfaceType").eq(type);
        
        return jdbcTemplate.queryForObject(sql, mapper);
    }
    
    public List<FdrTranslation> getByPaobjectIdAndType(int paoId, FdrInterfaceType type) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT FDRT.PointId, FDRT.DirectionType, FDRT.InterfaceType, FDRT.Translation");
        sql.append("FROM FdrTranslation FDRT, Point P");
        sql.append("WHERE FDRT.PointId = P.PointId");
        sql.append(  "AND FDRT.InterfaceType").eq(type.toString());
        sql.append(  "AND P.PAObjectId").eq(paoId);
        
        return jdbcTemplate.query(sql, mapper);
    }
    
    public List<FdrTranslation> getByInterfaceType(FdrInterfaceType type) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PointId, DirectionType, InterfaceType, Translation");
        sql.append("FROM FdrTranslation");
        sql.append("WHERE InterfaceType").eq(type.toString());
        
        return jdbcTemplate.query(sql, mapper);
    }
    
    public List<FdrTranslation> getByInterfaceTypeAndTranslation(FdrInterfaceType type, String translation) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PointId, DirectionType, InterfaceType, Translation");
        sql.append("FROM FdrTranslation");
        sql.append("WHERE InterfaceType").eq(type.toString());
        sql.append(  "AND Translation").eq(translation);
        
        return jdbcTemplate.query(sql, mapper);
    }
    
    public List<FdrTranslation> getAllTranslations() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PointId, DirectionType, InterfaceType, Translation");
        sql.append("FROM FdrTranslation");
        sql.append("ORDER BY InterfaceType");
        
        return jdbcTemplate.query(sql, mapper);
    }
    
    public boolean delete(FdrTranslation translation) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM FdrTranslation");
        sql.append("WHERE PointId").eq(translation.getPointId());
        sql.append(  "AND DirectionType").eq(translation.getDirection());
        sql.append(  "AND InterfaceType").eq(translation.getFdrInterfaceType().getDatabaseRepresentation());
        sql.append(  "AND Destination").eq(translation.getDestination());
        sql.append(  "AND Translation").eq(translation.getTranslation());
        
        int rowsAffected = jdbcTemplate.update(sql);
        
        return rowsAffected == 1;
    }
    
}