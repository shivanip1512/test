package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.core.dao.FdrTranslationDao;

public class FdrTranslationDaoImpl implements FdrTranslationDao {

    private static final String insertSql;
    private static final String selectAllSql;
    private static final String selectByPointIdAndTypeSql;
    private static final String selectByPaoIdAndTypeSql;    
    private static final String selectByTypeSql;
    private static final String selectByTypeAndTranslationSql;
    private static final String deleteSql;
    
    private static final ParameterizedRowMapper<FdrTranslation> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
                
        insertSql = "INSERT INTO fdrtranslation " +
        		"(pointid,directiontype,interfacetype,destination,translation) " +
        		"VALUES (?,?,?,?,?)";
        
        selectAllSql = "SELECT pointid,directiontype,interfacetype,destination,translation FROM fdrtranslation";
        
        selectByPointIdAndTypeSql = "SELECT pointid,directiontype,interfacetype,destination,translation FROM fdrtranslation" 
        	+ " WHERE pointid = ? AND interfacetype = ?";
        
        selectByPaoIdAndTypeSql = "SELECT FDRT.pointid, FDRT.directiontype, FDRT.interfacetype, FDRT.destination, FDRT.translation " 
                                + "FROM FDRTranslation FDRT, Point P " 
                                + "WHERE FDRT.pointId = P.pointId "
                                + "AND FDRT.interfaceType = ? "
                                + "AND P.PAOBjectID = ? ";
        
        selectByTypeSql = "SELECT pointid,directiontype,interfacetype,destination,translation FROM fdrtranslation" 
        	+ " WHERE interfacetype = ?";
        
        selectByTypeAndTranslationSql = "SELECT pointid,directiontype,interfacetype,destination,translation FROM fdrtranslation" 
        	+ " WHERE InterfaceType = ? and Translation = ?";
        
        deleteSql = "DELETE FROM fdrtranslation "
                  + "WHERE pointId = ? "
                  + "AND directionType = ? "
                  + "AND interfaceType = ? "
                  + "AND destination = ? "
                  + "AND translation = ? ";
        
        rowMapper = new ParameterizedRowMapper<FdrTranslation>() {
            public FdrTranslation mapRow(ResultSet rs, int rowNum) throws SQLException {
            	
            	FdrTranslation fdrTranslation = new FdrTranslation();
            	fdrTranslation.setPointId(rs.getInt("PointID"));
            	
            	String direction = rs.getString("directiontype");
            	fdrTranslation.setDirection(FdrDirection.getEnum(direction));
            	
            	String interfacetype = rs.getString("interfacetype");
            	fdrTranslation.setInterfaceType(FdrInterfaceType.valueOf(interfacetype));
            	
            	String destination = rs.getString("destination");
            	fdrTranslation.setDestination(FdrInterfaceType.valueOf(destination));
            	
            	String translation = rs.getString("translation");
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
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(FdrTranslation trans) {
        int rowsAffected = simpleJdbcTemplate.update(insertSql, trans.getPointId(),
                                                     trans.getDirection().toString(),
                                                     trans.getFdrInterfaceType().toString(),
                                                     trans.getDestination().toString(),
                                                     trans.getTranslation()
                                                     );
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public FdrTranslation getByPointIdAndType(int id, FdrInterfaceType type) {
    	return simpleJdbcTemplate.queryForObject(selectByPointIdAndTypeSql, rowMapper, id, type.toString());
	}
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<FdrTranslation> getByPaobjectIdAndType(int paoId, FdrInterfaceType type) {
        return simpleJdbcTemplate.query(selectByPaoIdAndTypeSql, rowMapper, type.toString(), paoId);        
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<FdrTranslation> getByInterfaceType(FdrInterfaceType type) {
    	return simpleJdbcTemplate.query(selectByTypeSql, rowMapper, type.toString() );
	}
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<FdrTranslation> getByInterfaceTypeAndTranslation(FdrInterfaceType type, String translation) {
    	return simpleJdbcTemplate.query(selectByTypeAndTranslationSql, rowMapper, type.toString(), translation );
	}
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<FdrTranslation> getAllTranslations() {
        return simpleJdbcTemplate.query(selectAllSql, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public boolean delete(FdrTranslation translation) {
        int rowsAffected = simpleJdbcTemplate.update(deleteSql,
                                                     translation.getPointId(),
                                                     translation.getDirection().toString(),
                                                     translation.getFdrInterfaceType().toString(),
                                                     translation.getDestination().toString(),
                                                     translation.getTranslation()
                                                     );
        boolean result = (rowsAffected == 1);
        return result;
    }
}
