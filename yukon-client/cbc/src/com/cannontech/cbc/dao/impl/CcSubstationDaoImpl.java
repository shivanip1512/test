package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import com.cannontech.cbc.dao.CcSubstationDao;

public class CcSubstationDaoImpl implements CcSubstationDao {
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public List<Integer> getAllUnassignedSubstationIds() {

        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("paobjectid") );
                return i;
            }
        };
        
        String query = "select paobjectid from yukonpaobject where type like 'CCSUBSTATION' ";
        query += "and ";
        query += "paobjectid not in (select substationbusid from ccsubareaassignment)";
        
        List<Integer> listmap = simpleJdbcTemplate.query(query, mapper);
        
        return listmap;
    }
    
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds( Integer areaId ) {

        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("paobjectid") );
                return i;
            }
        };
        
        String query = "select paobjectid from yukonpaobject where type like 'CCSUBSTATION' ";
        query += "and ";
        query += "paobjectid not in (select substationbusid from ccsubspecialareaassignment where areaid = "+areaId+ " )";
        
        List<Integer> listmap = simpleJdbcTemplate.query(query, mapper);
        
        return listmap;
    }

    public List<Integer> getAllSubstationIds() {
        //does not appear to be used
        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("substationid") );
                return i;
            }
        };
        
        String query = "select substationid from CapControlSubstation";
        
        List<Integer> listmap = simpleJdbcTemplate.query(query, mapper);
        
        return listmap;
    }
    
    public Integer getSubstationIdByName(String name) {

        String query = "select substationid from CapControlSubstation, yukonpaobject";
        query += "where substationid = paobjectid and paoname like "  + "'?'";

        Integer i = simpleJdbcTemplate.queryForInt(query, name);
        
        return i;
    }
}
