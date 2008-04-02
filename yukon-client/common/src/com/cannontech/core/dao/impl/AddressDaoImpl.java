package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.incrementer.NextValueHelper;

public class AddressDaoImpl implements AddressDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    private static final String selectByCityName;
    private static final String selectByStateCode;
    private static final String selectByZipCode;
    private static final String selectByCounty;
    private static final ParameterizedRowMapper<LiteAddress> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
        insertSql = "INSERT INTO Address (AddressID,LocationAddress1,LocationAddress2,CityName,StateCode,ZipCode,County) " +
                    "VALUES (?,?,?,?,?,?,?)";
        
        removeSql = "DELETE FROM Address WHERE AddressID = ?";
        
        updateSql = "UPDATE Address SET LocationAddress1 = ?, LocationAddress2 = ?, CityName = ?, StateCode = ?, ZipCode = ?, County = ? " +
                    "WHERE AddressID = ?";
        
        selectAllSql = "SELECT AddressID,LocationAddress1,LocationAddress2,CityName,StateCode,ZipCode,County " +
                       "FROM Address";
        
        selectByIdSql = selectAllSql + " WHERE AddressID = ?";
        
        selectByCityName = selectAllSql + " WHERE CityName = ?";
        
        selectByStateCode = selectAllSql + " WHERE StateCode = ?";
        
        selectByZipCode = selectAllSql + " WHERE ZipCode = ?";
        
        selectByCounty = selectAllSql + " WHERE County = ?";
        
        rowMapper = createRowMapper();
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(final LiteAddress address) {
        int nextAddressId = nextValueHelper.getNextValue("Address");
        address.setAddressID(nextAddressId);
        
        int rowsAffected = simpleJdbcTemplate.update(insertSql, address.getAddressID(),
                                                                address.getLocationAddress1(),
                                                                address.getLocationAddress2(),
                                                                address.getCityName(),
                                                                address.getStateCode(),
                                                                address.getZipCode(),
                                                                address.getCounty());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(LiteAddress address) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql, address.getAddressID());
        boolean result = (rowsAffected == 1);
        return result;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(LiteAddress address) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql, address.getLocationAddress1(),
                                                                address.getLocationAddress2(),
                                                                address.getCityName(),
                                                                address.getStateCode(),
                                                                address.getZipCode(),
                                                                address.getCounty());
        boolean result = (rowsAffected == 1);
        return result;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LiteAddress getByAddressId(final int addressId) throws DataAccessException {
        LiteAddress address = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, addressId);
        return address;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Map<Integer,LiteAddress> getAddresses(final List<Integer> addressIdList) {
        ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
        
        final List<LiteAddress> addressList = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append(selectAllSql);
                sqlBuilder.append(" WHERE AddressID IN (");
                sqlBuilder.append(SqlStatementBuilder.convertToSqlLikeList(subList));
                sqlBuilder.append(")");
                String sql = sqlBuilder.toString();
                return sql;
            }
        }, addressIdList, rowMapper);
        
        final Map<Integer, LiteAddress> resultMap = new HashMap<Integer, LiteAddress>(addressIdList.size());
        for (final LiteAddress address : addressList) {
            Integer key = address.getAddressID();
            resultMap.put(key, address);
        }
        
        return resultMap;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteAddress> getAll() {
        try {
            List<LiteAddress> list = simpleJdbcTemplate.query(selectAllSql, rowMapper, new Object[]{});
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteAddress> getByCityName(final String cityName) {
        try {
            List<LiteAddress> list = simpleJdbcTemplate.query(selectByCityName, rowMapper, cityName);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteAddress> getByCounty(final String county) {
        try {
            List<LiteAddress> list = simpleJdbcTemplate.query(selectByCounty, rowMapper, county);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteAddress> getByStateCode(final String stateCode) {
        try {
            List<LiteAddress> list = simpleJdbcTemplate.query(selectByStateCode, rowMapper, stateCode);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteAddress> getByZipCode(final String zipCode) {
        try {
            List<LiteAddress> list = simpleJdbcTemplate.query(selectByZipCode, rowMapper, zipCode);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    private static final ParameterizedRowMapper<LiteAddress> createRowMapper() {
        ParameterizedRowMapper<LiteAddress> rowMapper = new ParameterizedRowMapper<LiteAddress>() {
            public LiteAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
                LiteAddress address = new LiteAddress();
                address.setAddressID(rs.getInt("AddressID"));
                address.setCityName(rs.getString("CityName"));
                address.setCounty(rs.getString("County"));
                address.setLocationAddress1(rs.getString("LocationAddress1"));
                address.setLocationAddress2(rs.getString("LocationAddress2"));
                address.setStateCode(rs.getString("StateCode"));
                address.setZipCode(rs.getString("ZipCode"));
                return address;
            }
        };
        return rowMapper;
    }
    
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}
