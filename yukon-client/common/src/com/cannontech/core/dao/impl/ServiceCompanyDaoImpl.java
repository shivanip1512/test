package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.database.YukonJdbcTemplate;

public class ServiceCompanyDaoImpl implements ServiceCompanyDao {
    private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public ServiceCompanyDto getCompanyById(int serviceCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select CompanyId, CompanyName, AddressId, MainPhoneNumber, MainFaxNumber, PrimaryContactId, HIType");
        sql.append("from ServiceCompany");
        sql.append("where CompanyId =").appendArgument(serviceCompanyId);
        
        return yukonJdbcTemplate.queryForObject(sql, new ParameterizedRowMapper<ServiceCompanyDto>() {
            @Override
            public ServiceCompanyDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                ServiceCompanyDto serviceCompanyDto = new ServiceCompanyDto();
                serviceCompanyDto.setCompanyId(rs.getInt("companyId"));
                serviceCompanyDto.setCompanyName(rs.getString("companyName"));
                serviceCompanyDto.setAddressId(rs.getInt("addressId"));
                serviceCompanyDto.setMainPhoneNumber(rs.getString("mainPhoneNumber"));
                serviceCompanyDto.setMainFaxNumber(rs.getString("mainFaxNumber"));
                serviceCompanyDto.setPrimaryContactId(rs.getInt("primaryContactId"));
                serviceCompanyDto.setHiType(rs.getString("hiType"));
                return serviceCompanyDto;
            }
        });
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
