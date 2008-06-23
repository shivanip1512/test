package com.cannontech.web.stars.dr.consumer.displayable.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryDao;
import com.cannontech.stars.dr.controlhistory.service.ControlHistoryService;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.optout.dao.ScheduledOptOutDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.dr.program.service.ProgramService;

public class AbstractDisplayableDao {
    protected ApplianceDao applianceDao;
    protected ApplianceCategoryDao applianceCategoryDao;
    protected ProgramDao programDao;
    protected ProgramService programService;
    protected ProgramEnrollmentService programEnrollmentService;
    protected ScheduledOptOutDao scheduledOptOutDao;
    protected ControlHistoryDao controlHistoryDao;
    protected ControlHistoryService controlHistoryService;
    protected InventoryBaseDao inventoryBaseDao;
    
    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }
    
    @Autowired
    public void setApplianceCategoryDao(
            ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
    @Autowired
    public void setProgramEnrollmentService(
            ProgramEnrollmentService programEnrollmentService) {
        this.programEnrollmentService = programEnrollmentService;
    }
    
    @Autowired
    public void setScheduledOptOutDao(ScheduledOptOutDao scheduledOptOutDao) {
        this.scheduledOptOutDao = scheduledOptOutDao;
    }
    
    @Autowired
    public void setControlHistoryDao(ControlHistoryDao controlHistoryDao) {
        this.controlHistoryDao = controlHistoryDao;
    }
    
    @Autowired
    public void setControlHistoryService(
            ControlHistoryService controlHistoryService) {
        this.controlHistoryService = controlHistoryService;
    }
    
    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }
    
}
