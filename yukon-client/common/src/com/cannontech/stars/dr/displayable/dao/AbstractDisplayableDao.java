package com.cannontech.stars.dr.displayable.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.controlHistory.dao.ControlHistoryDao;
import com.cannontech.stars.dr.controlHistory.service.StarsControlHistoryService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;

public class AbstractDisplayableDao {
    protected ApplianceDao applianceDao;
    protected ApplianceCategoryDao applianceCategoryDao;
    protected ProgramDao programDao;
    protected ControlHistoryDao controlHistoryDao;
    protected StarsControlHistoryService controlHistoryService;
    protected InventoryDao inventoryDao;
    protected LMHardwareControlGroupDao lmHardwareControlGroupDao;

    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }

    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @Autowired
    public void setControlHistoryDao(ControlHistoryDao controlHistoryDao) {
        this.controlHistoryDao = controlHistoryDao;
    }

    @Autowired
    public void setControlHistoryService(StarsControlHistoryService controlHistoryService) {
        this.controlHistoryService = controlHistoryService;
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setLMHardwareControlGroupDao(LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }

}