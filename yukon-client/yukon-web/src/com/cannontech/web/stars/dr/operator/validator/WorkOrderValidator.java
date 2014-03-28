package com.cannontech.web.stars.dr.operator.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.dr.workOrder.model.WorkOrderDto;
import com.cannontech.user.YukonUserContext;

public class WorkOrderValidator extends SimpleValidator<WorkOrderDto> {
    
    private RolePropertyDao rolePropertyDao;
    private YukonUserContext userContext;
    
    public WorkOrderValidator(RolePropertyDao rolePropertyDao,
                              YukonUserContext userContext){
        super(WorkOrderDto.class);
        this.rolePropertyDao = rolePropertyDao;
        this.userContext = userContext;
    }
    
    @Override
    public void doValidation(WorkOrderDto workOrderDto, Errors errors) {

        if (!rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ORDER_NUMBER_AUTO_GEN, 
                                           userContext.getYukonUser())) { 
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
