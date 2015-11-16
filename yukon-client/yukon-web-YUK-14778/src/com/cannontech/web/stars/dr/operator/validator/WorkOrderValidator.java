package com.cannontech.web.stars.dr.operator.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.dr.workOrder.model.WorkOrderDto;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;

public class WorkOrderValidator extends SimpleValidator<WorkOrderDto> {
    
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    
    public WorkOrderValidator(){
        super(WorkOrderDto.class);
    }
    
    @Override
    public void doValidation(WorkOrderDto workOrderDto, Errors errors) {
        
        boolean isAutoGen = energyCompanySettingDao.getBoolean(EnergyCompanySettingType.WORK_ORDER_NUMBER_AUTO_GEN, 
                                                               workOrderDto.getWorkOrderBase().getEnergyCompanyId());
        
        if (!isAutoGen) { 
            YukonValidationUtils.rejectIfEmpty(errors, "workOrderBase.orderNumber", "empty");
            YukonValidationUtils.checkExceedsMaxLength(errors, "workOrderBase.orderNumber", workOrderDto.getWorkOrderBase().getOrderNumber(), 20);
        }
        
        YukonValidationUtils.checkExceedsMaxLength(errors, "workOrderBase.orderedBy", workOrderDto.getWorkOrderBase().getOrderedBy(), 30);
        YukonValidationUtils.checkExceedsMaxLength(errors, "workOrderBase.additionalOrderNumber", workOrderDto.getWorkOrderBase().getAdditionalOrderNumber(), 24);
        YukonValidationUtils.checkExceedsMaxLength(errors, "workOrderBase.description", workOrderDto.getWorkOrderBase().getDescription(), 500);
        
        if (!StringUtils.isBlank(workOrderDto.getWorkOrderBase().getActionTaken())) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "workOrderBase.actionTaken", workOrderDto.getWorkOrderBase().getActionTaken(), 200);
        }
    }
    
}
