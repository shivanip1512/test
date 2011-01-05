package com.cannontech.web.stars.dr.operator.service.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Address;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.stars.dr.operator.energyCompany.EnergyCompanyInfoFragment;
import com.cannontech.web.stars.dr.operator.service.EnergyCompanyInfoService;

public class EnergyCompanyInfoServiceImpl implements EnergyCompanyInfoService {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private AddressDao addressDao;
    
    private class InfoPart {
        public String name;
        public int addressId;
    }
    
    @Override
    public EnergyCompanyInfoFragment getEnergyCompanyInfoFragment(int ecId) {
        EnergyCompanyInfoFragment fragment = new EnergyCompanyInfoFragment(ecId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ec.Name, c.AddressId");
        sql.append("FROM EnergyCompany ec");
        sql.append(  "JOIN Contact c on c.ContactId = ec.PrimaryContactId");
        sql.append("WHERE ec.EnergyCompanyId").eq(ecId);
        
        InfoPart part = yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<InfoPart>() {
            @Override
            public InfoPart mapRow(YukonResultSet rs) throws SQLException {
                InfoPart infoPart = new InfoPart();
                infoPart.name = rs.getString("Name");
                infoPart.addressId = rs.getInt("AddressId");
                return infoPart;
            }
        });
        
        fragment.setCompanyName(part.name);
        fragment.setAddress(new Address(addressDao.getByAddressId(part.addressId)));
        
        return fragment;
    }
    
    @Autowired
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
}