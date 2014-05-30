package com.cannontech.web.stars.dr.operator;

import java.beans.PropertyEditor;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.account.dao.CallReportDao;
import com.cannontech.stars.dr.account.model.CallReport;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
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

    @Autowired private CallReportDao callReportDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonListDao yukonListDao;

    private final String baseKey = "yukon.web.modules.operator.viewCall.";
    
    // CALL LIST
    @RequestMapping("callList")
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

    @RequestMapping("create")
    @CheckRoleProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING)
    public String create(ModelMap model, YukonUserContext context, AccountInfoFragment fragment) {
        model.addAttribute("mode", PageEditMode.CREATE);
        
        CallReport callReport = new CallReport();
        callReport.setAccountId(fragment.getAccountId());
        callReport.setDateTaken(new Date());
        
        model.addAttribute("callReport", callReport);
        
        setupCallReportModel(fragment, model, context, null);
        
        return "operator/callTracking/viewCall.jsp";
    }
    
    // VIEW CALL
    @RequestMapping("view")
    public String view(ModelMap model, int callId, YukonUserContext context, AccountInfoFragment fragment) {
        model.addAttribute("mode", PageEditMode.VIEW);

        setupViewEditModel(model, callId, context, fragment);
        
        return "operator/callTracking/viewCall.jsp";
    }
    
    // EDIT CALL
    @RequestMapping("edit")
    public String edit(ModelMap model, int callId, YukonUserContext context, AccountInfoFragment fragment) {
        model.addAttribute("mode", PageEditMode.EDIT);
        
        setupViewEditModel(model, callId, context, fragment);
        
        return "operator/callTracking/viewCall.jsp";
    }

    private void setupViewEditModel(ModelMap model, int callId, YukonUserContext context, AccountInfoFragment fragment) {
        setupCallReportModel(fragment, model, context, callId);
        CallReport callReport = callReportDao.getCallReportByCallId(callId);
        model.addAttribute("callReport", callReport);
    }

    private synchronized String getNextCallNumberStr(int energyCompanyId) {
        long maxCallNum = callReportDao.getLargestNumericCallNumber(energyCompanyId);

        long nextCallNum = maxCallNum + 1;
        if (maxCallNum == 0) {
            String value = energyCompanySettingDao.getString(EnergyCompanySettingType.CALL_TRACKING_NUMBER_AUTO_GEN, energyCompanyId);
            if (value != null && !value.equalsIgnoreCase(CtiUtilities.STRING_NONE)) {
                try {
                    long callNumberFromRoleProperty = Long.parseLong(value);
                    if (callNumberFromRoleProperty > nextCallNum) {
                        nextCallNum = callNumberFromRoleProperty;
                    }
                } catch (NumberFormatException e) {
                    // CALL_TRACKING_NUMBER_AUTO_GEN can also be 'true', or 'false'.
                }
            }
        }
        return String.valueOf(nextCallNum++);
    }

    @RequestMapping("updateCall")
    @CheckRoleProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING)
    public String updateCall(@ModelAttribute("callReport") CallReport callReport, BindingResult bindingResult,
            ModelMap modelMap, YukonUserContext userContext, FlashScope flashScope, 
            AccountInfoFragment accountInfoFragment) {

        setupCallReportModel(accountInfoFragment, modelMap, userContext, callReport.getCallId());
        boolean isAutoGen = shouldAutoGenerateCallNumber(accountInfoFragment.getEnergyCompanyId());
        
        if (callReport.getCallId() == null && isAutoGen) {
            String nextCallNum = getNextCallNumberStr(accountInfoFragment.getEnergyCompanyId());
            callReport.setCallNumber(nextCallNum);
        }

        // validate
        CallReportValidator callReportValidator = new CallReportValidator(callReportDao, accountInfoFragment.getEnergyCompanyId());
        callReportValidator.validate(callReport, bindingResult);
        
        if (bindingResult.hasErrors()) {
            // custom message for those that do not have a call number field to correct
            if (isAutoGen && callReportValidator.isHasDuplicateCallNumberError()) {
                bindingResult.reject("callNumberCollision");
            }
            
            modelMap.addAttribute("mode", callReport.getCallId() == null ? PageEditMode.CREATE : PageEditMode.EDIT);
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/callTracking/viewCall.jsp";
        }

        // update or insert
        if (callReport.getCallId() != null) {
            callReportDao.update(callReport, accountInfoFragment.getEnergyCompanyId());
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "callUpdated"));
        } else {
            callReportDao.insert(callReport, accountInfoFragment.getEnergyCompanyId());
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "callCreated"));
        }
        
        return "redirect:callList";
    }

    @RequestMapping("deleteCall")
    @CheckRoleProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING)
    public String deleteCall(int deleteCallId, ModelMap modelMap, FlashScope flashScope, 
            AccountInfoFragment accountInfoFragment) {
        callReportDao.delete(deleteCallId);

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "callRemoved"));

        return "redirect:callList";
    }
    
    /**
     * Checks if CALL_TRACKING_NUMBER_AUTO_GEN is set to "true" or some number. 
     * If it is, that signals to us that the call number should be auto generated.
     */
    private boolean shouldAutoGenerateCallNumber(int energyCompanyId) {
        String value = energyCompanySettingDao.getString(EnergyCompanySettingType.CALL_TRACKING_NUMBER_AUTO_GEN, energyCompanyId);
        
        return NumberUtils.isDigits(value) || BooleanUtils.toBoolean(value);
    }
    
    private void setupCallReportModel(AccountInfoFragment accountInfoFragment, ModelMap modelMap, YukonUserContext userContext, Integer callId) {

        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        boolean shouldAutoGenerateCallNumber = shouldAutoGenerateCallNumber(accountInfoFragment.getEnergyCompanyId());
        modelMap.addAttribute("shouldAutoGenerateCallNumber", shouldAutoGenerateCallNumber);
        
        if (callId != null) {
            CallReport currentCallReport = callReportDao.getCallReportByCallId(callId);
            modelMap.addAttribute("callNumber", currentCallReport.getCallNumber());
        }
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        
        if (binder.getTarget() != null) {
            DefaultMessageCodesResolver msgCodesResolver = new DefaultMessageCodesResolver();
            msgCodesResolver.setPrefix(baseKey);
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
}