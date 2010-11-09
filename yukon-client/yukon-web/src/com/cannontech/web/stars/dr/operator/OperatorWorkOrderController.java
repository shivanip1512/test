package com.cannontech.web.stars.dr.operator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jfree.report.JFreeReport;
import org.jfree.report.function.FunctionInitializeException;
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

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.WorkOrder;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.event.model.EventBase;
import com.cannontech.stars.dr.workOrder.model.WorkOrderCurrentStateEnum;
import com.cannontech.stars.dr.workOrder.model.WorkOrderDto;
import com.cannontech.stars.dr.workOrder.service.WorkOrderService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.validator.WorkOrderValidator;

@Controller
@RequestMapping(value = "/operator/workOrder/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WORK_ORDERS)
public class OperatorWorkOrderController {

    private AccountEventLogService accountEventLogService;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private RolePropertyDao rolePropertyDao;
    private ServiceCompanyDao serviceCompanyDao;
    private StarsDatabaseCache starsDatabaseCache;
    private WorkOrderService workOrderService;
    
    // CALL LIST
    @RequestMapping
    public String workOrderList(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        // pageEditMode
        boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.CREATE : PageEditMode.VIEW);
        
        // workOrders
        List<WorkOrderDto> workOrders = workOrderService.getWorkOrderList(accountInfoFragment.getAccountId());
        
        List<ServiceCompanyDto> allServiceCompanies = serviceCompanyDao.getAllServiceCompanies();
        modelMap.addAttribute("allServiceCompanies", allServiceCompanies);
        
        modelMap.addAttribute("workOrders", workOrders);
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
        return "operator/workOrder/workOrderList.jsp";
    }

    // VIEW CALL
    @RequestMapping
    public String viewWorkOrder(ModelMap modelMap,
                                Integer workOrderId,
                                YukonUserContext userContext,
                                AccountInfoFragment accountInfoFragment) {
        
        boolean allowAccountEditing = 
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());

        setupViewWorkOrderModelMap(accountInfoFragment, modelMap, userContext, workOrderId);
        
        // Getting the assigned entry id to help with the change service company java script
        LiteStarsEnergyCompany energyCompany = 
            starsDatabaseCache.getEnergyCompany(accountInfoFragment.getEnergyCompanyId());
        YukonListEntry yukonListEntry = 
            energyCompany.getYukonListEntry(WorkOrderCurrentStateEnum.ASSIGNED.getDefinitionId());
        modelMap.addAttribute("assignedEntryId", yukonListEntry.getEntryID());

        WorkOrderDto workOrderDto;
        if (workOrderId == null) {
            workOrderDto = new WorkOrderDto();
            workOrderDto.getWorkOrderBase().setAccountId(accountInfoFragment.getAccountId());
            modelMap.addAttribute("mode", PageEditMode.CREATE);
        } else {
            workOrderDto = workOrderService.getWorkOrder(workOrderId);
            
            if (!allowAccountEditing) {
                modelMap.addAttribute("mode", PageEditMode.VIEW);
            } else {
                modelMap.addAttribute("mode", PageEditMode.EDIT);
            }
        
        }
        
        

        modelMap.addAttribute("workOrderDto", workOrderDto);

        boolean showWorkOrderNumberField = 
            !rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_ORDER_NUMBER_AUTO_GEN,
                                                    userContext.getYukonUser());
        modelMap.addAttribute("showWorkOrderNumberField", showWorkOrderNumberField);
        
        return "operator/workOrder/viewWorkOrder.jsp";
    }
    
    // UPDATE CALL
    @RequestMapping
    public String updateWorkOrder(@ModelAttribute("workOrderDto") WorkOrderDto workOrderDto,
                                  BindingResult bindingResult,
                                  ModelMap modelMap,
                                  YukonUserContext userContext,
                                  FlashScope flashScope,
                                  AccountInfoFragment accountInfoFragment) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        setupViewWorkOrderModelMap(accountInfoFragment, modelMap, userContext, workOrderDto.getWorkOrderBase().getOrderId());

        // validate
        WorkOrderValidator workOrderValidator = new WorkOrderValidator(rolePropertyDao, userContext);
        workOrderValidator.validate(workOrderDto, bindingResult);
        
        if (bindingResult.hasErrors()) {
            modelMap.addAttribute("mode", workOrderDto.getWorkOrderBase().getOrderId() == 0 ? PageEditMode.CREATE : PageEditMode.EDIT);

            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/workOrder/viewWorkOrder.jsp";
        }

        // Create a work order
        if (workOrderDto.getWorkOrderBase().getOrderId() == 0) {
            accountEventLogService.workOrderCreationAttemptedByOperator(userContext.getYukonUser(),
                                                                        accountInfoFragment.getAccountNumber(),
                                                                        workOrderDto.getWorkOrderBase().getOrderNumber());

            workOrderService.createWorkOrder(workOrderDto, accountInfoFragment.getEnergyCompanyId(),
                                             accountInfoFragment.getAccountNumber(), userContext);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.workOrder.workOrderCreated"));
        
        // Update a work order
        } else {
            accountEventLogService.workOrderUpdateAttemptedByOperator(userContext.getYukonUser(),
                                                                      accountInfoFragment.getAccountNumber(),
                                                                      workOrderDto.getWorkOrderBase().getOrderNumber());

            workOrderService.updateWorkOrder(workOrderDto, accountInfoFragment.getAccountNumber(), userContext);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.workOrder.workOrderUpdated"));
        }
        
        return "redirect:workOrderList";
    }
    
    // DELETE WORK ORDER
    @RequestMapping
    public String deleteWorkOrder(int deleteWorkOrderId,
                                  ModelMap modelMap,
                                  YukonUserContext userContext,
                                  FlashScope flashScope,
                                  AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());

        WorkOrderDto workOrderDto = workOrderService.getWorkOrder(deleteWorkOrderId);
        accountEventLogService.workOrderDeletionAttemptedByOperator(userContext.getYukonUser(),
                                                                  accountInfoFragment.getAccountNumber(),
                                                                  workOrderDto.getWorkOrderBase().getOrderNumber());

        
        workOrderService.deleteWorkOrder(deleteWorkOrderId, accountInfoFragment.getAccountNumber(), userContext);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.workOrder.workOrderRemoved"));
        
        return "redirect:workOrderList"; 
    }
    
    // Generate Work Order PDF
    @RequestMapping
    public void generateWorkOrderReport(int workOrderId,
                                          HttpServletResponse response,
                                          AccountInfoFragment accountInfoFragment,
                                          YukonUserContext userContext) throws IOException, FunctionInitializeException {
        
        WorkOrderDto workOrder = workOrderService.getWorkOrder(workOrderId);
        
        WorkOrderModel workOrderModel = new WorkOrderModel();
        workOrderModel.setEnergyCompanyID(accountInfoFragment.getEnergyCompanyId());
        workOrderModel.setOrderID(workOrderId);
        workOrderModel.setUserID(userContext.getYukonUser().getUserID());

        // Generate Work Order Data
        WorkOrder workOrderJReport = new WorkOrder(workOrderModel);
        workOrderJReport.getModel().collectData();
        JFreeReport workOrderReport = workOrderJReport.createReport();
        workOrderReport.setData(workOrderJReport.getModel());
        
        // Write out work order report
        response.setContentType("application/pdf");
        OutputStream outputStream = response.getOutputStream();
        
        ReportFuncs.outputYukonReport(workOrderReport, "pdf", outputStream);
        
    }
    
    private void setupViewWorkOrderModelMap(AccountInfoFragment accountInfoFragment, ModelMap modelMap, 
                                            YukonUserContext userContext, Integer workOrderId) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        modelMap.addAttribute("workOrderId", workOrderId);
        
        // Get event history for the given work order id
        if (workOrderId != null) {
            List<EventBase> eventHistory = workOrderService.getWorkOrderEventHistory(workOrderId);
            modelMap.addAttribute("eventHistory", eventHistory);
        }

        List<ServiceCompanyDto> allServiceCompanies = serviceCompanyDao.getAllServiceCompanies();
        modelMap.addAttribute("allServiceCompanies", allServiceCompanies);
        
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        
        if (binder.getTarget() != null) {
            DefaultMessageCodesResolver msgCodesResolver = new DefaultMessageCodesResolver();
            msgCodesResolver.setPrefix("yukon.web.modules.operator.workOrder.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
        
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);
    }
    
    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
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
    public void setServiceCompanyDao(ServiceCompanyDao serviceCompanyDao) {
        this.serviceCompanyDao = serviceCompanyDao;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setWorkOrderService(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }
}
