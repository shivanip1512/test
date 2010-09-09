package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class ServiceCompanyDaoImpl implements ServiceCompanyDao {
    private YukonJdbcTemplate yukonJdbcTemplate;

    private SqlStatementBuilder selectBase = new SqlStatementBuilder();
    {    
        selectBase.append("SELECT CompanyId, CompanyName, AddressId, MainPhoneNumber, ");
        selectBase.append(       "MainFaxNumber, PrimaryContactId, HIType");
        selectBase.append("FROM ServiceCompany SC");
    }

    @Override
    public ServiceCompanyDto getCompanyById(int serviceCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectBase);
        sql.append("WHERE CompanyId").eq(serviceCompanyId);
        
        return yukonJdbcTemplate.queryForObject(sql, new ServiceCompanyDtoRowMapper());
    }
    
    public List<ServiceCompanyDto> getAllServiceCompanies() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectBase);
        
        return yukonJdbcTemplate.query(sql, new ServiceCompanyDtoRowMapper());
        
    }
    
    
    // Row Mapper
    private static class ServiceCompanyDtoRowMapper implements YukonRowMapper<ServiceCompanyDto> {

        @Override
        public ServiceCompanyDto mapRow(YukonResultSet rs) throws SQLException {
            ServiceCompanyDto serviceCompanyDto = new ServiceCompanyDto();

            serviceCompanyDto.setCompanyId(rs.getInt("companyId"));
            serviceCompanyDto.setCompanyName(SqlUtils.convertDbValueToString(rs.getString("companyName")));
            serviceCompanyDto.setAddressId(rs.getInt("addressId"));
            serviceCompanyDto.setMainPhoneNumber(SqlUtils.convertDbValueToString(rs.getString("mainPhoneNumber")));
            serviceCompanyDto.setMainFaxNumber(SqlUtils.convertDbValueToString(rs.getString("mainFaxNumber")));
            serviceCompanyDto.setPrimaryContactId(rs.getInt("primaryContactId"));
            serviceCompanyDto.setHiType(SqlUtils.convertDbValueToString(rs.getString("hiType")));

            return serviceCompanyDto;

        }
    }
    
    // DI
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
