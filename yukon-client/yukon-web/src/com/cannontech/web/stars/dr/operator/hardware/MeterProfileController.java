package com.cannontech.web.stars.dr.operator.hardware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.SwitchAssignment;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.HardwareModelHelper;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.util.SessionUtil;
import com.google.common.collect.Sets;

@RequestMapping("/operator/hardware/mp/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class MeterProfileController {
    
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private HardwareModelHelper helper;
    @Autowired private OperatorAccountService operatorAccountService;
    @Autowired private SelectionListService selectionListService;

    /**
     * View page GET
     */
    @RequestMapping(value="view", method=RequestMethod.GET)
    public String view(ModelMap model, AccountInfoFragment fragment, int inventoryId) {
        model.addAttribute("mode", PageEditMode.VIEW);
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        return setupModel(model, fragment, hardware);
    }
    
    /**
     * Editing page GET 
     */
    @RequestMapping(value="edit", method=RequestMethod.GET)
    public String edit(ModelMap model, YukonUserContext context, AccountInfoFragment fragment, int inventoryId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        return setupModel(model, fragment, hardware);
    }
    
    /**
     * Try to update the meter profile POST 
     */
    @RequestMapping(value="edit", method=RequestMethod.POST)
    public String edit(@ModelAttribute Hardware hardware, BindingResult result,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 FlashScope flash,
                                 AccountInfoFragment fragment,
                                 int inventoryId) throws ServletRequestBindingException {
        
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Creation */
            model.clear();
            AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
            model.addAttribute("inventoryId", inventoryId);
            return "redirect:view";
        }
        
        LiteYukonUser user = context.getYukonUser();
        helper.updateAttempted(hardware, user, YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, result);
        
        if (result.hasErrors()) {
            model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModel(model, fragment, hardware);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/hardware/meterProfile.jsp";
        }
        
         boolean statusChange = hardwareUiService.updateHardware(user, hardware);
        
        /* If the device status was changed, spawn an event for it */
        if (statusChange) {
            int userId = SessionUtil.getParentLoginUserId(request.getSession(), user.getUserID());
            EventUtils.logSTARSEvent(userId, EventUtils.EVENT_CATEGORY_INVENTORY, hardware.getDeviceStatusEntryId(), inventoryId);
        }
        
        /* Flash hardware updated */
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareUpdated"));
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        model.addAttribute("inventoryId", inventoryId);
        
        return "redirect:view";
    }
    
    /**
     * Creation page GET 
     */
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String create(ModelMap model, YukonUserContext context, AccountInfoFragment fragment) {
        model.addAttribute("mode", PageEditMode.CREATE);
        YukonEnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(context.getYukonUser());
        YukonListEntry typeEntry = selectionListService.getListEntry(energyCompany, HardwareType.NON_YUKON_METER.getDefinitionId());
        
        Hardware hardware = new Hardware();
        hardware.setHardwareType(HardwareType.NON_YUKON_METER);
        hardware.setHardwareTypeEntryId(typeEntry.getEntryID());
        hardware.setFieldInstallDate(new Date());
        hardware.setAccountId(fragment.getAccountId());
        
        for (SwitchAssignment assignement : hardwareUiService.getSwitchAssignments(new ArrayList<Integer>(), fragment.getAccountId())) {
            hardware.getSwitchAssignments().add(assignement);
        }
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, context.getYukonUser());
        
        if (typeEntry.getEntryID() == 0) {
            /* This happens when using stars to handle meters (role property z_meter_mct_base_desig='stars') but this energy company does
             * not have the 'MCT' device type (which is the non yukon meter device type) added to it's device type list in the 
             * config energy company section yet.  This is a setup issue and we will blow up here for now.  */
            throw new NoNonYukonMeterDeviceTypeException("There is no meter type in the device types list for this energy company.");
        }

        model.addAttribute("hardware", hardware);
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        return "operator/hardware/meterProfile.jsp";
    }
    
    /**
     * Try to create a meter profile POST 
     */
    @RequestMapping(value="create", method=RequestMethod.POST)
    public String create(@ModelAttribute("hardware") Hardware hardware, BindingResult result,
                                 ModelMap model, 
                                 YukonUserContext context,
                                 HttpServletRequest request,
                                 FlashScope flash) throws ServletRequestBindingException {
        
        AccountInfoFragment fragment = operatorAccountService.getAccountInfoFragment(hardware.getAccountId());
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        int accountId = fragment.getAccountId();
        
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Creation */
            model.clear();
            model.addAttribute("accountId", accountId);
            return "redirect:/stars/operator/hardware/list";
        }
        
        LiteYukonUser user = context.getYukonUser();
        Set<YukonRoleProperty> verifyProperties = Sets.newHashSet(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                                                  YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE);
        helper.creationAttempted(user, fragment.getAccountNumber(), hardware, verifyProperties, result);

        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            return setupErrorView(model, fragment);
        }
        try {
            int inventoryId = helper.create(user, hardware, result, request.getSession());
            AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareCreated"));
            model.addAttribute("inventoryId", inventoryId);
        } catch (RuntimeException e) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.error.createDeviceFailed"));
            return setupErrorView(model, fragment);
        }

        return "redirect:view";
    }
    
    private String setupErrorView(ModelMap model, AccountInfoFragment fragment) {
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        model.addAttribute("mode", PageEditMode.CREATE);
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        return "operator/hardware/meterProfile.jsp";
    }
    
    private String setupModel(ModelMap model, AccountInfoFragment fragment, Hardware hardware) {
        int inventoryId = hardware.getInventoryId();
        model.addAttribute("inventoryId", inventoryId);
        model.addAttribute("editingRoleProperty", YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING);
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), fragment.getAccountId());
        model.addAttribute("hardware", hardware);
        model.addAttribute("displayName", hardware.getDisplayName());
        model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        
        return "operator/hardware/meterProfile.jsp";
    }
    
}