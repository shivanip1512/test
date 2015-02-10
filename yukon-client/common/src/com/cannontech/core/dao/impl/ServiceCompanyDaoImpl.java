package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.DesignationCodeDao;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.energyCompany.EcMappingCategory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ServiceCompanyDaoImpl implements ServiceCompanyDao {
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private DesignationCodeDao designationCodeDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private LoadingCache< Set<Integer>, List<ServiceCompanyDto>> serviceCompanyListCache= CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<Set<Integer>, List<ServiceCompanyDto>>(){

        @Override
        public List<ServiceCompanyDto> load(Set<Integer> arg0) throws Exception {
            return null;
        }
        
    });
    
    private LoadingCache<Integer, ServiceCompanyDto> serviceCompanyCache= CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<Integer, ServiceCompanyDto>(){

        @Override
        public ServiceCompanyDto load(Integer arg0) throws Exception {
            return null;
        }
        
    });

    private SimpleTableAccessTemplate<ServiceCompanyDto> serviceCompanyTemplate;

    private SqlFragmentSource selectBase;
    {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SC.CompanyID, SC.CompanyName, SC.AddressID, SC.MainPhoneNumber,");
        sql.append("       SC.MainFaxNumber, SC.PrimaryContactID, SC.HIType, AD.LocationAddress1,");
        sql.append("       AD.LocationAddress2, AD.CityName, AD.StateCode, AD.ZipCode, AD.County,");
        sql.append("       CT.ContFirstName, CT.ContLastName, CT.AddressID, CT.LogInID,");
        sql.append("       LG.UserName");
        sql.append("FROM ServiceCompany SC");
        sql.append("JOIN ECToGenericMapping EC ON EC.ItemID = SC.CompanyID AND EC.MappingCategory");
        sql.eq_k(EcMappingCategory.SERVICE_COMPANY);
        sql.append("LEFT JOIN Address AD ON SC.AddressID = AD.AddressID");
        sql.append("LEFT JOIN Contact CT ON SC.PrimaryContactID = CT.ContactID");
        sql.append("LEFT JOIN YukonUser LG ON CT.LogInID = LG.UserID");
        selectBase = sql;
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

    private static class ServiceCompanyDtoRowMapper implements YukonRowMapper<ServiceCompanyDto> {
        @Override
        public ServiceCompanyDto mapRow(YukonResultSet rs) throws SQLException {
            ServiceCompanyDto serviceCompanyDto = new ServiceCompanyDto();

            serviceCompanyDto.setCompanyId(rs.getNullableInt("CompanyID"));
            serviceCompanyDto.setCompanyName(rs.getStringSafe("CompanyName"));
            serviceCompanyDto.setMainPhoneNumber(rs.getStringSafe("MainPhoneNumber"));
            serviceCompanyDto.setMainFaxNumber(rs.getStringSafe("MainFaxNumber"));
            serviceCompanyDto.setHiType(rs.getStringSafe("HIType"));

            // Set address object.
            ResultSet resultSet = rs.getResultSet();
            AddressDaoImpl.rowMapper.mapRow(resultSet, -1);
            serviceCompanyDto.setAddress(AddressDaoImpl.rowMapper.mapRow(resultSet, -1));

            // Set primary contact object.
            serviceCompanyDto.setPrimaryContact(new LiteContact(rs.getInt("PrimaryContactID"),
                rs.getStringSafe("ContFirstName"), rs.getStringSafe("ContLastName"), rs.getInt("LogInID"),
                rs.getInt("AddressID")));

            return serviceCompanyDto;
        }
    }

    private void populateServiceCompany(ServiceCompanyDto serviceCompany) {
        serviceCompany.setDesignationCodes(designationCodeDao.getDesignationCodesByServiceCompanyId(serviceCompany.getCompanyId()));
        LiteContactNotification email =
            contactNotificationDao.getFirstNotificationForContactByType(serviceCompany.getPrimaryContact(),
                ContactNotificationType.EMAIL);
        if (email != null) {
            serviceCompany.setEmailContactNotification(email.getNotification());
        }
    }

    @PostConstruct
    public void init() throws Exception {
        serviceCompanyTemplate = new SimpleTableAccessTemplate<ServiceCompanyDto>(jdbcTemplate, nextValueHelper);
        serviceCompanyTemplate.setTableName("ServiceCompany");
        serviceCompanyTemplate.setPrimaryKeyField("CompanyID");
        serviceCompanyTemplate.setFieldMapper(serviceCompanyDtoFieldMapper);
        serviceCompanyTemplate.setPrimaryKeyValidOver(0);
    }

    // CRUD
    @Override
    public ServiceCompanyDto getCompanyById(int serviceCompanyId) {
        ServiceCompanyDto cachedServiceCompany = serviceCompanyCache.getIfPresent(serviceCompanyId);
        if (cachedServiceCompany != null) {
            return cachedServiceCompany;
        } else {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.appendFragment(selectBase);
            sql.append("WHERE CompanyId").eq(serviceCompanyId);

            ServiceCompanyDto serviceCompany = jdbcTemplate.queryForObject(sql, new ServiceCompanyDtoRowMapper());
            populateServiceCompany(serviceCompany);

            serviceCompanyCache.put(serviceCompanyId, serviceCompany);
            return serviceCompany;
        }
    }

    @Override
    public List<ServiceCompanyDto> getAllServiceCompanies() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectBase);

        List<ServiceCompanyDto> serviceCompanies = jdbcTemplate.query(sql, new ServiceCompanyDtoRowMapper());

        for (ServiceCompanyDto serviceCompany : serviceCompanies) {
            populateServiceCompany(serviceCompany);
        }

        return serviceCompanies;
    }

    @Override
    public List<ServiceCompanyDto> getAllServiceCompaniesForEnergyCompanies(Set<Integer> energyCompanyIds) {
        List<ServiceCompanyDto> cachedserviceCompanyDto = (List<ServiceCompanyDto>) serviceCompanyListCache.getAllPresent(energyCompanyIds);
        if (!cachedserviceCompanyDto.isEmpty()) {
            return cachedserviceCompanyDto;
        } else {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.appendFragment(selectBase);
            sql.append("WHERE ec.energycompanyid").in(energyCompanyIds);

            List<ServiceCompanyDto> serviceCompanies = jdbcTemplate.query(sql, new ServiceCompanyDtoRowMapper());

            for (ServiceCompanyDto serviceCompany : serviceCompanies) {
                populateServiceCompany(serviceCompany);
            }

            serviceCompanyListCache.put(energyCompanyIds, serviceCompanies);
            return serviceCompanies;
        }

    }

    @Override
    public void create(ServiceCompanyDto serviceCompany, int energyCompanyId) {
        serviceCompanyTemplate.save(serviceCompany);

        // add to mapping table
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ectogenericmapping");
        sql.values(energyCompanyId, serviceCompany.getCompanyId(), EcMappingCategory.SERVICE_COMPANY);

        jdbcTemplate.update(sql);
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

        jdbcTemplate.update(sql);

        // delete from mapping table
        SqlStatementBuilder sql2 = new SqlStatementBuilder();
        sql2.append("DELETE FROM ectogenericmapping");
        sql2.append("WHERE ItemID").eq(serviceCompanyId);
        sql2.append("AND MappingCategory").eq_k(EcMappingCategory.SERVICE_COMPANY);

        jdbcTemplate.update(sql2);
    }

    @Override
    public void moveInventory(int fromServiceCompanyId, int toServiceCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE InventoryBase");
        sql.append("SET InstallationCompanyId").eq(toServiceCompanyId);
        sql.append("WHERE InstallationCompanyId").eq(fromServiceCompanyId);

        jdbcTemplate.update(sql);
    }

    @Override
    public int getInventoryCountForServiceCompany(int serviceCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Count(*)");
        sql.append("FROM InventoryBase IB");
        sql.append("WHERE IB.InstallationCompanyId").eq(serviceCompanyId);

        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public List<DisplayableServiceCompany> getAllServiceCompanies(Iterable<Integer> energyCompanyIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select sc.CompanyID companyId, sc.CompanyName companyName from ServiceCompany sc");
        sql.append("join ECToGenericMapping ecgm on ecgm.ItemID = sc.CompanyID");
        sql.append("and ecgm.MappingCategory").eq_k(EcMappingCategory.SERVICE_COMPANY);
        sql.append("where ecgm.EnergyCompanyID").in(energyCompanyIds);
        List<DisplayableServiceCompany> allServiceCompanies =
            jdbcTemplate.query(sql, new YukonRowMapper<DisplayableServiceCompany>() {
                @Override
                public DisplayableServiceCompany mapRow(YukonResultSet rs) throws SQLException {
                    int id = rs.getInt("companyId");
                    String name = rs.getStringSafe("companyName");
                    DisplayableServiceCompany sc = new DisplayableServiceCompany(id, name);
                    return sc;
                }
            });
        return allServiceCompanies;
    }
}
