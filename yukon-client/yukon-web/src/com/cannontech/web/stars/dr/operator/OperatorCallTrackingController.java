package com.cannontech.web.stars.dr.operator;

import java.beans.PropertyEditor;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.web.bind.ServletRequestBindingException;
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
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
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
    private StarsDatabaseCache starsDatabaseCache;
    
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

    // CREATE CALL
    @RequestMapping
    public String create(ModelMap model, YukonUserContext context, AccountInfoFragment fragment) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        model.addAttribute("mode", PageEditMode.CREATE);
        
        CallReport callReport = new CallReport();
        callReport.setAccountId(fragment.getAccountId());
        callReport.setDateTaken(new Date());
        
        model.addAttribute("callReport", callReport);
        
        setupCallReportModel(fragment, model, context, null);
        
        return "operator/callTracking/viewCall.jsp";
    }
    
    // VIEW CALL
    @RequestMapping
    public String view(ModelMap model, int callId, YukonUserContext context, AccountInfoFragment fragment) {
        model.addAttribute("mode", PageEditMode.VIEW);

        setupViewEditModel(model, callId, context, fragment);
        
        return "operator/callTracking/viewCall.jsp";
    }
    
    // EDIT CALL
    @RequestMapping
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
    
    // UPDATE CALL
    @RequestMapping
    public String updateCall(@ModelAttribute("callReport") CallReport callReport,
                            BindingResult bindingResult,
                            ModelMap modelMap,
                            YukonUserContext userContext,
                            FlashScope flashScope,
                            AccountInfoFragment accountInfoFragment) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        setupCallReportModel(accountInfoFragment, modelMap, userContext, callReport.getCallId());
        
        // autoGenerateCallNumber for new call
        if (callReport.getCallId() == null && shouldAutoGenerateCallNumber(userContext.getYukonUser())) {
            
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
            String nextCallNumber = energyCompany.getNextCallNumber();
            callReport.setCallNumber(nextCallNumber);
        }

        // validate
        CallReportValidator callReportValidator = new CallReportValidator(callReportDao, accountInfoFragment.getEnergyCompanyId());
        callReportValidator.validate(callReport, bindingResult);
        
        if (bindingResult.hasErrors()) {
                
            // custom message for those that do not have a call number field to correct
            if (shouldAutoGenerateCallNumber(userContext.getYukonUser()) && callReportValidator.isHasDuplicateCallNumberError()) {
                bindingResult.reject("callNumberCollision");
            }
            
            modelMap.addAttribute("mode", callReport.getCallId() == null ? PageEditMode.CREATE : PageEditMode.EDIT);
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/callTracking/viewCall.jsp";
        }

        // update or insert
        if (callReport.getCallId()!= null) {
            callReportDao.update(callReport, accountInfoFragment.getEnergyCompanyId());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.viewCall.callUpdated"));
        } else {
            callReportDao.insert(callReport, accountInfoFragment.getEnergyCompanyId());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.viewCall.callCreated"));
        }
        
        return "redirect:callList";
    }
    
    // DELETE CALL
    @RequestMapping
    public String deleteCall(int deleteCallId,
                            ModelMap modelMap,
                            YukonUserContext userContext,
                            FlashScope flashScope,
                            AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        
        callReportDao.delete(deleteCallId);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.viewCall.callRemoved"));
        
        return "redirect:callList"; 
    }
    
    /**
     * Checks if OPERATOR_CALL_NUMBER_AUTO_GEN is set to "true" or some number. 
     * If it is, that signals to us that the call number should be auto generated.
     */
    private boolean shouldAutoGenerateCallNumber(LiteYukonUser user) {
        
        String callNumAutoGenstr = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_CALL_NUMBER_AUTO_GEN, user);
        Boolean callNumAutoGenBoolean = BooleanUtils.toBooleanObject(callNumAutoGenstr);
        
        return (callNumAutoGenBoolean != null && callNumAutoGenBoolean.booleanValue()) || NumberUtils.isDigits(callNumAutoGenstr);
    }
    
    private void setupCallReportModel(AccountInfoFragment accountInfoFragment, ModelMap modelMap, YukonUserContext userContext, Integer callId) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        boolean shouldAutoGenerateCallNumber = shouldAutoGenerateCallNumber(userContext.getYukonUser());
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
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

}