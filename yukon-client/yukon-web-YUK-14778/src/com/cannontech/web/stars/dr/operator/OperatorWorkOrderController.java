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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.WorkOrder;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.event.model.EventBase;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.dr.workOrder.model.WorkOrderCurrentStateEnum;
import com.cannontech.stars.dr.workOrder.model.WorkOrderDto;
import com.cannontech.stars.dr.workOrder.service.WorkOrderService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.user.YukonUserContext;
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

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SelectionListService selectionListService;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private WorkOrderService workOrderService;
    @Autowired private WorkOrderValidator workOrderValidator;
    
    private final String baseKey = "yukon.web.modules.operator.workOrder.";
    
    @RequestMapping("workOrderList")
    public String workOrderList(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        // pageEditMode
        boolean allowAccountEditing = 
                rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.CREATE : PageEditMode.VIEW);
        
        // workOrders
        List<WorkOrderDto> workOrders = workOrderService.getWorkOrderList(accountInfoFragment.getAccountId());
        
        List<ServiceCompanyDto> allServiceCompanies = serviceCompanyDao.getAllServiceCompanies();
        modelMap.addAttribute("allServiceCompanies", allServiceCompanies);
        
        modelMap.addAttribute("workOrders", workOrders);
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
        return "operator/workOrder/workOrderList.jsp";
    }

    @RequestMapping("create")
    @CheckRoleProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING)
    public String create(ModelMap model, YukonUserContext context, AccountInfoFragment fragment) {
        model.addAttribute("mode", PageEditMode.CREATE);
        
        setupWorkOrderModel(fragment, model, context, null);
        
        WorkOrderDto workOrderDto = new WorkOrderDto();
        workOrderDto.getWorkOrderBase().setAccountId(fragment.getAccountId());
        workOrderDto.getWorkOrderBase().setEnergyCompanyId(fragment.getEnergyCompanyId());
        
        model.addAttribute("workOrderDto", workOrderDto);
        
        return "operator/workOrder/viewWorkOrder.jsp";
    }
    
    @RequestMapping("edit")
    @CheckRoleProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING)
    public String edit(ModelMap model, int workOrderId, YukonUserContext context, AccountInfoFragment fragment) {
        model.addAttribute("mode", PageEditMode.EDIT);
        
        setupViewEditModel(model, workOrderId, context, fragment);

        return "operator/workOrder/viewWorkOrder.jsp";
    }

    @RequestMapping("view")
    public String view(ModelMap model, int workOrderId, YukonUserContext context, AccountInfoFragment fragment) {
        model.addAttribute("mode", PageEditMode.VIEW);
        
        setupViewEditModel(model, workOrderId, context, fragment);
        
        return "operator/workOrder/viewWorkOrder.jsp";
    }
    
    private void setupViewEditModel(ModelMap model, int workOrderId, YukonUserContext context, AccountInfoFragment fragment) {
        setupWorkOrderModel(fragment, model, context, workOrderId);
        WorkOrderDto workOrderDto = workOrderService.getWorkOrder(workOrderId);
        model.addAttribute("workOrderDto", workOrderDto);
    }
    
    @RequestMapping("updateWorkOrder")
    @CheckRoleProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING)
    public String updateWorkOrder(@ModelAttribute("workOrderDto") WorkOrderDto workOrderDto, BindingResult bindingResult,
            ModelMap modelMap, YukonUserContext userContext, FlashScope flashScope, 
            AccountInfoFragment accountInfoFragment) {
        
        setupWorkOrderModel(accountInfoFragment, modelMap, userContext, workOrderDto.getWorkOrderBase().getOrderId());

        // validate
        workOrderValidator.validate(workOrderDto, bindingResult);
        
        if (bindingResult.hasErrors()) {
            modelMap.addAttribute("mode", workOrderDto.getWorkOrderBase().getOrderId() == 0 ? PageEditMode.CREATE : PageEditMode.EDIT);

            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/workOrder/viewWorkOrder.jsp";
        }

        // Create a work order
        if (workOrderDto.getWorkOrderBase().getOrderId() == 0) {
            accountEventLogService.workOrderCreationAttempted(userContext.getYukonUser(),
                                                              accountInfoFragment.getAccountNumber(),
                                                              workOrderDto.getWorkOrderBase().getOrderNumber(),
                                                              EventSource.OPERATOR);

            workOrderService.createWorkOrder(workOrderDto, accountInfoFragment.getEnergyCompanyId(),
                                             accountInfoFragment.getAccountNumber(), userContext);
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "workOrderCreated"));
        
        // Update a work order
        } else {
            accountEventLogService.workOrderUpdateAttempted(userContext.getYukonUser(),
                                                            accountInfoFragment.getAccountNumber(),
                                                            workOrderDto.getWorkOrderBase().getOrderNumber(),
                                                            EventSource.OPERATOR);

            workOrderService.updateWorkOrder(workOrderDto, accountInfoFragment.getAccountNumber(), userContext);
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "workOrderUpdated"));
        }
        
        return "redirect:workOrderList";
    }
    
    @RequestMapping("deleteWorkOrder")
    @CheckRoleProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING)
    public String deleteWorkOrder(int deleteWorkOrderId, ModelMap modelMap, YukonUserContext userContext, 
            FlashScope flashScope, AccountInfoFragment accountInfoFragment) {

        WorkOrderDto workOrderDto = workOrderService.getWorkOrder(deleteWorkOrderId);
        accountEventLogService.workOrderDeletionAttempted(userContext.getYukonUser(),
                                                          accountInfoFragment.getAccountNumber(),
                                                          workOrderDto.getWorkOrderBase().getOrderNumber(),
                                                          EventSource.OPERATOR);

        workOrderService.deleteWorkOrder(deleteWorkOrderId, accountInfoFragment.getAccountNumber(), userContext);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "workOrderRemoved"));
        
        return "redirect:workOrderList"; 
    }
    
    @RequestMapping("generateWorkOrderReport")
    public void generateWorkOrderReport(int workOrderId,
                                          HttpServletResponse response,
                                          AccountInfoFragment accountInfoFragment,
                                          YukonUserContext userContext) throws IOException, FunctionInitializeException {
        
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
    
    private void setupWorkOrderModel(AccountInfoFragment fragment, 
                                    ModelMap model, 
                                    YukonUserContext context, 
                                    Integer workOrderId) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        model.addAttribute("workOrderId", workOrderId);
        
        // Get event history for the given work order id
        if (workOrderId != null) {
            List<EventBase> eventHistory = workOrderService.getWorkOrderEventHistory(workOrderId);
            model.addAttribute("eventHistory", eventHistory);
        }

        List<ServiceCompanyDto> allServiceCompanies = serviceCompanyDao.getAllServiceCompanies();
        model.addAttribute("allServiceCompanies", allServiceCompanies);
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        
        // Getting the assigned entry id to help with the change service company java script
        LiteStarsEnergyCompany energyCompany = 
            starsDatabaseCache.getEnergyCompany(fragment.getEnergyCompanyId());
        YukonListEntry yukonListEntry = 
                selectionListService.getListEntry(energyCompany, WorkOrderCurrentStateEnum.ASSIGNED.getDefinitionId());
        model.addAttribute("assignedEntryId", yukonListEntry.getEntryID());
        
        boolean showWorkOrderNumberField = !energyCompanySettingDao.getBoolean(EnergyCompanySettingType.WORK_ORDER_NUMBER_AUTO_GEN, fragment.getEnergyCompanyId());
        model.addAttribute("showWorkOrderNumberField", showWorkOrderNumberField);

    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        
        if (binder.getTarget() != null) {
            DefaultMessageCodesResolver msgCodesResolver = new DefaultMessageCodesResolver();
            msgCodesResolver.setPrefix(baseKey);
            binder.setMessageCodesResolver(msgCodesResolver);
        }
        
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);
    }
}
