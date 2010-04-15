package com.cannontech.web.stars.dr.operator;

import java.beans.PropertyEditor;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CallReportDao;
import com.cannontech.stars.dr.account.model.CallReport;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.validator.CallReportValidator;
import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/operator/callTracking/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_CALL_TRACKING)
public class OperatorCallTrackingController {

	private CallReportDao callReportDao;
	private YukonListDao yukonListDao;
	private DatePropertyEditorFactory datePropertyEditorFactory;
	private RolePropertyDao rolePropertyDao;
	
	// CALL LIST
	@RequestMapping
    public String callList(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		// pageEditMode
		boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.CREATE : PageEditMode.VIEW);
		
		// callReports
		List<CallReport> callReports = callReportDao.getAllCallReportByAccountId(accountInfoFragment.getAccountId());
		
		// callReportsWrappers
		List<CallReportWrapper> callReportsWrappers = Lists.newArrayListWithCapacity(callReports.size());
		for (CallReport callReport : callReports) {
			callReportsWrappers.add(new CallReportWrapper(callReport));
		}
		modelMap.addAttribute("callReportsWrappers", callReportsWrappers);
		
		return "operator/callTracking/callList.jsp";
	}

	// VIEW CALL
	@RequestMapping
	public String viewCall(ModelMap modelMap,
							Integer callId,
							YukonUserContext userContext,
							AccountInfoFragment accountInfoFragment) {
		
		rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		CallReport callReport;
		if (callId == null) {
			callReport = new CallReport();
			callReport.setAccountId(accountInfoFragment.getAccountId());
			callReport.setDateTaken(new Date());
			modelMap.addAttribute("mode", PageEditMode.CREATE);
		} else {
			callReport = callReportDao.getCallReportByCallId(callId);
			modelMap.addAttribute("mode", PageEditMode.EDIT);
		}
		modelMap.addAttribute("callReport", callReport);
		
		return "operator/callTracking/viewCall.jsp";
	}
	
	// UPDATE CALL
	@RequestMapping
	public String updateCall(@ModelAttribute("callReport") CallReport callReport,
							BindingResult bindingResult,
							ModelMap modelMap,
							YukonUserContext userContext,
							FlashScope flashScope,
							AccountInfoFragment accountInfoFragment) {
		
		rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		
		// pageEditMode
		boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		CallReportValidator callReportValidator = new CallReportValidator(callReportDao, accountInfoFragment.getEnergyCompanyId());
		
		callReportValidator.validate(callReport, bindingResult);
		if (bindingResult.hasErrors()) {
				
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			return "operator/callTracking/viewCall.jsp";
		}

		if (callReport.getCallId()!= null) {
			callReportDao.update(callReport, accountInfoFragment.getEnergyCompanyId());
			flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.viewCall.callUpdated"));
		} else {
			callReportDao.insert(callReport, accountInfoFragment.getEnergyCompanyId());
			flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.viewCall.callCreated"));
		}
		
		modelMap.addAttribute("callId", callReport.getCallId());
		return "redirect:viewCall";
	}
	
	// DELETE CALL
	@RequestMapping
	public String deleteCall(int callId,
							ModelMap modelMap,
							YukonUserContext userContext,
							FlashScope flashScope,
							AccountInfoFragment accountInfoFragment) {
		
		rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		
		callReportDao.delete(callId);
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.viewCall.callRemoved"));
		
		return "redirect:callList"; 
	}
	
	@InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
		
		if (binder.getTarget() != null) {
			DefaultMessageCodesResolver msgCodesResolver = new DefaultMessageCodesResolver();
	        msgCodesResolver.setPrefix("yukon.web.modules.operator.viewCall.");
	        binder.setMessageCodesResolver(msgCodesResolver);
		}
        
        PropertyEditor fullDateTimeEditor = datePropertyEditorFactory.getPropertyEditor(DateFormatEnum.DATEHM, userContext);
        binder.registerCustomEditor(Date.class, fullDateTimeEditor);
    }
	
	public class CallReportWrapper {
		
		private CallReport callReport;
		
		public CallReportWrapper(CallReport callReport) {
			this.callReport = callReport;
		}
		
		public CallReport getCallReport() {
			return callReport;
		}
		
		public String getType() {
			
			YukonListEntry yukonListEntry = yukonListDao.getYukonListEntry(callReport.getCallTypeId());
			return yukonListEntry.getEntryText();
		}
	}
	
	@Autowired
	public void setCallReportDao(CallReportDao callReportDao) {
		this.callReportDao = callReportDao;
	}
	
	@Autowired
	public void setYukonListDao(YukonListDao yukonListDao) {
		this.yukonListDao = yukonListDao;
	}
	
	@Autowired
	public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
		this.datePropertyEditorFactory = datePropertyEditorFactory;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
}
