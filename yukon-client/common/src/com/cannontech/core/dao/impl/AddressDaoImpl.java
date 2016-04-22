package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.incrementer.NextValueHelper;

public class AddressDaoImpl implements AddressDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    public static final RowMapper<LiteAddress> rowMapper;

    static {
        insertSql =
            "INSERT INTO Address (AddressID,LocationAddress1,LocationAddress2,CityName,StateCode,ZipCode,County) "
                + "VALUES (?,?,?,?,?,?,?)";

        removeSql = "DELETE FROM Address WHERE AddressID = ?";

        updateSql =
            "UPDATE Address SET LocationAddress1 = ?, LocationAddress2 = ?, CityName = ?, StateCode = ?, ZipCode = ?, County = ? "
                + "WHERE AddressID = ?";

        selectAllSql =
            "SELECT AddressID,LocationAddress1,LocationAddress2,CityName,StateCode,ZipCode,County " + "FROM Address";

        selectByIdSql = selectAllSql + " WHERE AddressID = ?";

        rowMapper = createRowMapper();
    }

    @Override
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

        int rowsAffected =
            jdbcTemplate.update(insertSql, address.getAddressID(), locationAddress1, locationAddress2, cityName,
                stateCode, zipCode, county);
        boolean result = (rowsAffected == 1);
        return result;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void remove(int addressId) {
        LiteAddress address = getByAddressId(addressId);
        remove(address);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(LiteAddress address) {
        int rowsAffected = jdbcTemplate.update(removeSql, address.getAddressID());
        boolean result = (rowsAffected == 1);
        return result;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(LiteAddress address) {

        String locationAddress1 = SqlUtils.convertStringToDbValue(address.getLocationAddress1());
        String locationAddress2 = SqlUtils.convertStringToDbValue(address.getLocationAddress2());
        String cityName = SqlUtils.convertStringToDbValue(address.getCityName());
        String stateCode = SqlUtils.convertStringToDbValue(address.getStateCode());
        String zipCode = SqlUtils.convertStringToDbValue(address.getZipCode());
        String county = SqlUtils.convertStringToDbValue(address.getCounty());

        int rowsAffected =
            jdbcTemplate.update(updateSql, locationAddress1, locationAddress2, cityName, stateCode, zipCode, county,
                address.getAddressID());

        boolean result = (rowsAffected == 1);
        return result;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LiteAddress getByAddressId(final int addressId) throws DataAccessException {
        LiteAddress address = jdbcTemplate.queryForObject(selectByIdSql, rowMapper, addressId);
        return address;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Map<Integer, LiteAddress> getAddresses(final List<Integer> addressIdList) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

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

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LiteAddress> getAll() {
        List<LiteAddress> list = jdbcTemplate.query(selectAllSql, rowMapper, new Object[] {});
        return list;
    }

    private static final RowMapper<LiteAddress> createRowMapper() {
        RowMapper<LiteAddress> rowMapper = new RowMapper<LiteAddress>() {
            @Override
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
}
