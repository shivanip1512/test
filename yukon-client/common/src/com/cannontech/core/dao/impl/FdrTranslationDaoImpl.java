package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.opc.model.FdrDirection;
import com.cannontech.common.opc.model.FdrInterfaceType;
import com.cannontech.common.opc.model.FdrTranslation;
import com.cannontech.core.dao.FdrTranslationDao;

public class FdrTranslationDaoImpl implements FdrTranslationDao {

    private static final String insertSql;
    private static final String selectByIdAndTypeSql;
    private static final String selectByTypeSql;
    
    private static final ParameterizedRowMapper<FdrTranslation> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
                
        insertSql = "INSERT INTO fdrtranslation " +
        		"(pointid,directiontype,interfacetype,destination,translation) " +
        		"VALUES (?,?,?,?,?)";
        
        selectByIdAndTypeSql = "SELECT pointid,directiontype,interfacetype,destination,translation FROM fdrtranslation" 
        	+ " WHERE pointid = ? AND interfacetype = ?";
        
        selectByTypeSql = "SELECT pointid,directiontype,interfacetype,destination,translation FROM fdrtranslation" 
        	+ " WHERE interfacetype = ?";     
        
        rowMapper = new ParameterizedRowMapper<FdrTranslation>() {
            public FdrTranslation mapRow(ResultSet rs, int rowNum) throws SQLException {
            	
            	FdrTranslation fdrTranslation = new FdrTranslation();
            	fdrTranslation.setFdrPointId(rs.getInt("PointID"));
            	
            	String direction = rs.getString("directiontype");
            	fdrTranslation.setDirection(FdrDirection.getEnum(direction));
            	
            	String interfacetype = rs.getString("interfacetype");
            	fdrTranslation.setInterfaceType(FdrInterfaceType.valueOf(interfacetype));
            	
            	String destination = rs.getString("destination");
            	fdrTranslation.setDestination(FdrInterfaceType.valueOf(destination));
            	
            	fdrTranslation.setTranslation(rs.getString("translation"));
            	
                return fdrTranslation;
            }
        };
    }
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    public boolean add( FdrTranslation trans) {
        int rowsAffected = simpleJdbcTemplate.update(insertSql, trans.getFdrPointId(),
                                                     trans.getDirection().toString(),
                                                     trans.getInterfaceType().toString(),
                                                     trans.getDestination().toString(),
                                                     trans.getTranslation()
                                                     );
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public FdrTranslation getByPointIdAndType(int id, FdrInterfaceType type) {
    	return simpleJdbcTemplate.queryForObject(selectByIdAndTypeSql, rowMapper, id, type.toString());
	}
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<FdrTranslation> getByInterfaceType(FdrInterfaceType type) {
    	return simpleJdbcTemplate.query(selectByTypeSql, rowMapper, type.toString() );
	}
}
