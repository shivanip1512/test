package com.cannontech.web.stars.dr.operator.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.dr.account.dao.CallReportDao;
import com.cannontech.stars.dr.account.model.CallReport;

public class CallReportValidator extends SimpleValidator<CallReport> {
	
	private CallReportDao callReportDao;
	private int energyCompanyId;
	private boolean hasDuplicateCallNumberError = false;

	public CallReportValidator(){
    	super(CallReport.class);
    }
	
	public CallReportValidator(CallReportDao callReportDao, int energyCompanyId){
    	super(CallReport.class);
    	this.callReportDao = callReportDao;
    	this.energyCompanyId = energyCompanyId;
    }
	
	@Override
    public void doValidation(CallReport callReport, Errors errors) {

		YukonValidationUtils.rejectIfEmpty(errors, "callNumber", "emptyCallNumber");
		
        YukonValidationUtils.checkExceedsMaxLength(errors, "callNumber", callReport.getCallNumber(), 20);
        YukonValidationUtils.checkExceedsMaxLength(errors, "takenBy", callReport.getTakenBy(), 30);
        YukonValidationUtils.checkExceedsMaxLength(errors, "description", callReport.getDescription(), 300);
        
        // don't allow duplicate callNumbers for same energyCompany
        Integer foundCallId = callReportDao.findCallIdByCallNumber(callReport.getCallNumber(), energyCompanyId);
        if (foundCallId != null && (callReport.getCallId() == null || foundCallId.intValue() != callReport.getCallId())) {
        	errors.rejectValue("callNumber", "callNumberExists");
        	hasDuplicateCallNumberError = true;
        } else {
        	hasDuplicateCallNumberError = false;
        }
    }
	
	public boolean isHasDuplicateCallNumberError() {
		return hasDuplicateCallNumberError;
	}
}
