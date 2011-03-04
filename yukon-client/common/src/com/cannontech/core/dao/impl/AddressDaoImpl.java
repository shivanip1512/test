package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.database.SqlUtils;
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
    public static final ParameterizedRowMapper<LiteAddress> rowMapper;
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
        
        String locationAddress1 = SqlUtils.convertStringToDbValue(address.getLocationAddress1());
		String locationAddress2 = SqlUtils.convertStringToDbValue(address.getLocationAddress2());
		String cityName = SqlUtils.convertStringToDbValue(address.getCityName());
		String stateCode = SqlUtils.convertStringToDbValue(address.getStateCode());
		String zipCode = SqlUtils.convertStringToDbValue(address.getZipCode());
		String county = SqlUtils.convertStringToDbValue(address.getCounty());
		
		int rowsAffected = simpleJdbcTemplate.update(insertSql, address.getAddressID(),
                                                                locationAddress1,
                                                                locationAddress2,
                                                                cityName,
                                                                stateCode,
                                                                zipCode,
                                                                county);
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void remove(int addressId) {
        LiteAddress address = getByAddressId(addressId);
        remove(address);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(LiteAddress address) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql, address.getAddressID());
        boolean result = (rowsAffected == 1);
        return result;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(LiteAddress address) {
    	
    	String locationAddress1 = SqlUtils.convertStringToDbValue(address.getLocationAddress1());
		String locationAddress2 = SqlUtils.convertStringToDbValue(address.getLocationAddress2());
		String cityName = SqlUtils.convertStringToDbValue(address.getCityName());
		String stateCode = SqlUtils.convertStringToDbValue(address.getStateCode());
		String zipCode = SqlUtils.convertStringToDbValue(address.getZipCode());
		String county = SqlUtils.convertStringToDbValue(address.getCounty());
		
		int rowsAffected = simpleJdbcTemplate.update(updateSql, locationAddress1,
                                                                locationAddress2,
                                                                cityName,
                                                                stateCode,
                                                                zipCode,
                                                                county,
                                                                address.getAddressID());
    	
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
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(simpleJdbcTemplate);
        
        final List<LiteAddress> addressList = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append(selectAllSql);
                sqlBuilder.append(" WHERE AddressID IN (");
                sqlBuilder.append(subList);
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
        List<LiteAddress> list = simpleJdbcTemplate.query(selectAllSql, rowMapper, new Object[]{});
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteAddress> getByCityName(final String cityName) {
        List<LiteAddress> list = simpleJdbcTemplate.query(selectByCityName, rowMapper, cityName);
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteAddress> getByCounty(final String county) {
        List<LiteAddress> list = simpleJdbcTemplate.query(selectByCounty, rowMapper, county);
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteAddress> getByStateCode(final String stateCode) {
        List<LiteAddress> list = simpleJdbcTemplate.query(selectByStateCode, rowMapper, stateCode);
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteAddress> getByZipCode(final String zipCode) {
        List<LiteAddress> list = simpleJdbcTemplate.query(selectByZipCode, rowMapper, zipCode);
        return list;
    }

    private static final ParameterizedRowMapper<LiteAddress> createRowMapper() {
        ParameterizedRowMapper<LiteAddress> rowMapper = new ParameterizedRowMapper<LiteAddress>() {
            public LiteAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
                LiteAddress address = new LiteAddress();
                address.setAddressID(rs.getInt("AddressID"));
                address.setCityName(SqlUtils.convertDbValueToString(rs, "CityName"));
                address.setCounty(SqlUtils.convertDbValueToString(rs, "County"));
                address.setLocationAddress1(SqlUtils.convertDbValueToString(rs, "LocationAddress1"));
                address.setLocationAddress2(SqlUtils.convertDbValueToString(rs, "LocationAddress2"));
                address.setStateCode(SqlUtils.convertDbValueToString(rs, "StateCode"));
                address.setZipCode(SqlUtils.convertDbValueToString(rs, "ZipCode"));
                return address;
            }
        };
        return rowMapper;
    }
    
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}
