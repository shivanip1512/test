package com.cannontech.stars.dr.hardware.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;

public class LMHardwareConfigurationDaoImpl implements LMHardwareConfigurationDao {
    
    private SimpleJdbcTemplate simpleJdbcTemplate; 
    
    @Override
    public void add(LMHardwareConfiguration lmConfiguration) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(LMHardwareConfiguration lmConfiguration) {
        delete(lmConfiguration.getInventoryId());
    }
    
    @Override
    public void delete(int invetoryId) {
        String sql = "DELETE FROM LMHardwareConfiguration WHERE InventoryID = ?";
        simpleJdbcTemplate.update(sql, invetoryId);
    }
    
    @Override
    public void deleteForAppliance(int applianceId) {
        String sql = "DELETE FROM LMHardwareConfiguration WHERE ApplianceId = ?";
        simpleJdbcTemplate.update(sql, applianceId);
    }

    @Override
    @Transactional
    public void delete(List<Integer> inventoryIds) {
        for(Integer id : inventoryIds) {
            delete(id);
        }
    }

    @Override
    public void update(LMHardwareConfiguration lmConfiguration) {
        // TODO Auto-generated method stub

    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
