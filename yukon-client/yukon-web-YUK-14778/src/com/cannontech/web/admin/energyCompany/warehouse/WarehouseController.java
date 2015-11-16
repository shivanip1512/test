package com.cannontech.web.admin.energyCompany.warehouse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.model.Address;
import com.cannontech.common.validator.AddressValidator;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.admin.energyCompany.warehouse.model.WarehouseDto;
import com.cannontech.web.admin.energyCompany.warehouse.service.WarehouseService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTI_WAREHOUSE)
@Controller
@RequestMapping("/energyCompany/warehouse/*")
public class WarehouseController {
    
    public StarsDatabaseCache starsDatabaseCache;
    
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private StarsEventLogService starsEventLogService;
    @Autowired private WarehouseService warehouseService;
    
    private final String baseUrl = "/admin/energyCompany/warehouse";
    
    /* Main page */
    @RequestMapping("home")
    public String home(YukonUserContext userContext, 
                       ModelMap modelMap,
                       int ecId,
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        
        energyCompanyService.verifyViewPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        modelMap.addAttribute("warehouses", warehouseService.getWarehousesForEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId()));
        modelMap.addAttribute("energyCompanyName", energyCompanyInfoFragment.getCompanyName());
        return "energyCompany/warehouse/home.jsp";
    }
    
    /* Individual View */
    @RequestMapping("view")
    public String viewWarehouse(YukonUserContext userContext, 
                                ModelMap modelMap,
                                int warehouseId, 
                                int ecId,
                                EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        
        energyCompanyService.verifyViewPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        modelMap.addAttribute("warehouseDto", warehouseService.getWarehouse(warehouseId));
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        return "energyCompany/warehouse/view.jsp";
    }
    
    /* New */
    @RequestMapping("new")
    public String newWarehouse(YukonUserContext userContext, 
                               ModelMap modelMap, 
                               int ecId, 
                               EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        
        energyCompanyService.verifyViewPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        WarehouseDto warehouseDto = new WarehouseDto(new Warehouse(), new LiteAddress());
        //Populate with the basics
        warehouseDto.getWarehouse().setEnergyCompanyID(ecId);
        modelMap.addAttribute("warehouseDto", warehouseDto);
        modelMap.addAttribute("mode", PageEditMode.CREATE);
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        return "energyCompany/warehouse/edit.jsp";
    }
    
    /* Create */
    @RequestMapping(value="update", params="create")
    public String createWarehouse(YukonUserContext userContext,
                                  ModelMap modelMap, 
                                  int ecId,
                                  final @ModelAttribute ("warehouseDto") WarehouseDto warehouseDto, 
                                  BindingResult bindingResult,
                                  FlashScope flashScope,
                                  EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        
        starsEventLogService.addWarehouseAttempted(userContext.getYukonUser(),
                                                   warehouseDto.getWarehouse().getWarehouseName(),
                                                   EventSource.OPERATOR);
        
        modelMap.addAttribute("ecId", ecId);
        WarehouseDtoValidator validator = new WarehouseDtoValidator();
        validator.validate(warehouseDto, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            modelMap.addAttribute("mode", PageEditMode.CREATE);
            return "energyCompany/warehouse/edit.jsp";
        }
        
        warehouseService.createWarehouse(warehouseDto);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.warehouse.warehouseCreated", warehouseDto.getWarehouse().getWarehouseName()));
        
        return "redirect:home";
    }
    
    /* Edit */
    @RequestMapping("edit")
    public String editWarehouse(YukonUserContext userContext, 
                                ModelMap modelMap, 
                                int ecId, 
                                int warehouseId, 
                                EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        
        energyCompanyService.verifyViewPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        modelMap.addAttribute("warehouseDto", warehouseService.getWarehouse(warehouseId));
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        return "energyCompany/warehouse/edit.jsp";
    }
    
    /* Update */
    @RequestMapping(value="update", params="update")
    public String updateWarehouse(YukonUserContext userContext,
                                  ModelMap modelMap,
                                 int ecId,
                                 final @ModelAttribute ("warehouseDto") WarehouseDto warehouseDto, 
                                 BindingResult bindingResult,
                                 FlashScope flashScope,
                                 EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        

        starsEventLogService.updateWarehouseAttempted(userContext.getYukonUser(), 
                                                      warehouseDto.getWarehouse().getWarehouseName(),
                                                      EventSource.OPERATOR);
        
        WarehouseDtoValidator validator = new WarehouseDtoValidator();
        validator.validate(warehouseDto, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return editWarehouse(userContext, modelMap, ecId,  warehouseDto.getWarehouse().getWarehouseID(), energyCompanyInfoFragment);
        }
        
        warehouseService.updateWarehouse(warehouseDto);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.warehouse.warehouseUpdated", warehouseDto.getWarehouse().getWarehouseName()));
        modelMap.addAttribute("warehouseId", warehouseDto.getWarehouse().getWarehouseID());
        modelMap.addAttribute("ecId", ecId);
        return "redirect:view";
    }
    
    /* Delete */
    @RequestMapping(value="update", params="delete")
    public String deleteWarehouse(YukonUserContext userContext, 
                                  ModelMap modelMap,
                                  int ecId,
                                  final @ModelAttribute ("warehouseDto") WarehouseDto warehouseDto,
                                  FlashScope flashScope,
                                  EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        
        starsEventLogService.deleteWarehouseAttempted(userContext.getYukonUser(),
                                                                warehouseDto.getWarehouse().getWarehouseName(), EventSource.OPERATOR);
        modelMap.addAttribute("ecId", ecId);
        List<Integer> inventory = Warehouse.getAllInventoryInAWarehouse(warehouseDto.getWarehouse().getWarehouseID());
        //Only warehouse that contains no inventory may be deleted.
        if(inventory.isEmpty()){
            warehouseService.deleteWarehouse(warehouseDto);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.warehouse.warehouseDeleted", warehouseDto.getWarehouse().getWarehouseName()));
            return "redirect:home";
        }else{
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.warehouse.warehouse.warehouseNotEmpty", warehouseDto.getWarehouse().getWarehouseName()));
            modelMap.addAttribute("warehouseId", warehouseDto.getWarehouse().getWarehouseID());
            modelMap.addAttribute("ecId", ecId);
            return "redirect:view";
        }

    }
    
    private static class WarehouseDtoValidator extends SimpleValidator<WarehouseDto> {

        private WarehouseDtoValidator() {
            super(WarehouseDto.class);
        }

        @Override
        protected void doValidation(WarehouseDto warehouseDto, Errors errors) {
            //Check Address
            AddressValidator addressValidator = new AddressValidator();
            errors.pushNestedPath("address");
            addressValidator.validate(new Address(warehouseDto.getAddress()), errors);
            errors.popNestedPath();
            
            //Check Warehouse
            //Name
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "warehouse.warehouseName", "yukon.web.modules.adminSetup.warehouse.warehouseDto.warehouse.nameRequired");
            YukonValidationUtils.checkExceedsMaxLength(errors, "warehouse.warehouseName", warehouseDto.getWarehouse().getWarehouseName(), 60);
            
            //Notes
            YukonValidationUtils.checkExceedsMaxLength(errors, "warehouse.notes", warehouseDto.getWarehouse().getNotes(), 300);
        }
    };
    
    @ModelAttribute("baseUrl")
    public String getBaseUrl() {
        return this.baseUrl;
    }
}
