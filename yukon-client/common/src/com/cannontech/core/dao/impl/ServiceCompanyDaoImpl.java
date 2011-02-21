package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.incrementer.NextValueHelper;

public class ServiceCompanyDaoImpl implements ServiceCompanyDao, InitializingBean {
    
    private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<ServiceCompanyDto> serviceCompanyTemplate;

    private SqlStatementBuilder selectBase = new SqlStatementBuilder();
    {    
        selectBase.append("SELECT CompanyId, CompanyName, AddressId, MainPhoneNumber, ");
        selectBase.append(       "MainFaxNumber, PrimaryContactId, HIType");
        selectBase.append("FROM ServiceCompany SC");
    }
    
    private SqlStatementBuilder selectECBase = new SqlStatementBuilder();
    {
        selectECBase.append("SELECT sc.*,");
        selectECBase.append("ad.locationaddress1, ad.locationaddress2, ad.cityname, ad.statecode, ad.zipcode, ad.county,");
        selectECBase.append("ct.contfirstname, ct.contlastname, ct.addressid, ct.loginid,");
        selectECBase.append("lg.username");
        selectECBase.append("FROM");
        selectECBase.append("servicecompany AS sc");
        selectECBase.append("JOIN ectogenericmapping AS ec ON ec.itemid = sc.companyid AND ec.mappingcategory = 'ServiceCompany'");
        selectECBase.append("LEFT JOIN address AS ad ON sc.addressid = ad.addressid");
        selectECBase.append("LEFT JOIN contact AS ct ON sc.primarycontactid = ct.contactid");
        selectECBase.append("LEFT JOIN yukonuser AS lg ON ct.loginid = lg.userid");
    }
    
    // ServiceCompanyDto -> to -> sql
    private FieldMapper<ServiceCompanyDto> serviceCompanyDtoFieldMapper = new FieldMapper<ServiceCompanyDto>() {

        @Override
        public void extractValues(MapSqlParameterSource p, ServiceCompanyDto serviceCompanyDto) {
            p.addValue("CompanyName", serviceCompanyDto.getCompanyName());
            p.addValue("MainPhoneNumber", serviceCompanyDto.getMainPhoneNumber());        
            p.addValue("MainFaxNumber", serviceCompanyDto.getMainFaxNumber());
            p.addValue("HIType", serviceCompanyDto.getHiType());
            p.addValue("AddressID", serviceCompanyDto.getAddress().getAddressID());
            p.addValue("PrimaryContactID", serviceCompanyDto.getPrimaryContact().getContactID());
        }

        @Override
        public Number getPrimaryKey(ServiceCompanyDto object) {
            return object.getCompanyId();
        }

        @Override
        public void setPrimaryKey(ServiceCompanyDto object, int value) {
            object.setCompanyId(value);
        }
    };
    
    // Row Mappers  sql -> to -> ServiceCompanyDto
    private static class ServiceCompanyDtoRowMapper implements YukonRowMapper<ServiceCompanyDto> {

        @Override
        public ServiceCompanyDto mapRow(YukonResultSet rs) throws SQLException {
            ServiceCompanyDto serviceCompanyDto = new ServiceCompanyDto();

            serviceCompanyDto.setCompanyId(rs.getInt("CompanyID"));
            serviceCompanyDto.setCompanyName(rs.getString("CompanyName"));
            serviceCompanyDto.setMainPhoneNumber(rs.getString("MainPhoneNumber"));
            serviceCompanyDto.setMainFaxNumber(rs.getString("MainFaxNumber"));
            serviceCompanyDto.setHiType(rs.getString("HIType"));
            
            //set address obj
            serviceCompanyDto.setAddress(new LiteAddress(rs.getInt("AddressID"),
                                                         rs.getString("LocationAddress1"),
                                                         rs.getString("LocationAddress2"),
                                                         rs.getString("CityName"),
                                                         rs.getString("StateCode"),
                                                         rs.getString("ZipCode")));
            serviceCompanyDto.getAddress().setZipCode(rs.getString("ZipCode"));
            
            //set primary contact obj
            serviceCompanyDto.setPrimaryContact(new LiteContact(rs.getInt("PrimaryContactID"),
                                                                rs.getString("ContFirstName"),
                                                                rs.getString("ContLastName"),
                                                                rs.getInt("LogInID"),
                                                                rs.getInt("AddressID")));
            
            return serviceCompanyDto;
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        serviceCompanyTemplate = new SimpleTableAccessTemplate<ServiceCompanyDto>(yukonJdbcTemplate, nextValueHelper);
        serviceCompanyTemplate.setTableName("ServiceCompany");
        serviceCompanyTemplate.setPrimaryKeyField("CompanyID");
        serviceCompanyTemplate.setFieldMapper(serviceCompanyDtoFieldMapper);
        serviceCompanyTemplate.setPrimaryKeyValidOver(0);
    }

    // CRUD
    @Override
    public ServiceCompanyDto getCompanyById(int serviceCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectECBase);
        sql.append("WHERE CompanyId").eq(serviceCompanyId);
        
        return yukonJdbcTemplate.queryForObject(sql, new ServiceCompanyDtoRowMapper());
    }
    
    public List<ServiceCompanyDto> getAllServiceCompanies() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectBase);
        
        return yukonJdbcTemplate.query(sql, new ServiceCompanyDtoRowMapper());
        
    }
    
    @Override
    public List<ServiceCompanyDto> getAllServiceCompaniesForEnergyCompany(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectECBase);
        sql.append("WHERE ec.energycompanyid").eq(energyCompanyId);
        
        return yukonJdbcTemplate.query(sql, new ServiceCompanyDtoRowMapper());
    }
    
    @Override
    public void create(ServiceCompanyDto serviceCompany, int energyCompanyId) {
        serviceCompanyTemplate.save(serviceCompany);
        
        //add to mapping table
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ectogenericmapping");
        sql.values(energyCompanyId, serviceCompany.getCompanyId(), "ServiceCompany");
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void update(ServiceCompanyDto serviceCompany) {
        serviceCompanyTemplate.save(serviceCompany);
    }
    
    @Override
    public void delete(int serviceCompanyId) {
        // Delete service company
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ServiceCompany");
        sql.append("WHERE CompanyID").eq(serviceCompanyId);
        
        yukonJdbcTemplate.update(sql);
        
        //delete from mapping table
        SqlStatementBuilder sql2 = new SqlStatementBuilder();
        sql2.append("DELETE FROM ectogenericmapping");
        sql2.append("WHERE ItemID").eq(serviceCompanyId);
        sql2.append("AND MappingCategory").eq("'ServiceCompany'");
        
        yukonJdbcTemplate.update(sql2);
    }
    
    // DI
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}
