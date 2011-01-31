package com.cannontech.web.stars.dr.operator.warehouse;

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

import com.cannontech.common.model.Address;
import com.cannontech.common.validator.AddressValidator;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.stars.dr.operator.warehouse.model.WarehouseDto;
import com.cannontech.web.stars.dr.operator.warehouse.service.impl.WarehouseServiceImpl;

@RequestMapping("/operator/energyCompany/warehouse/*")
@Controller
public class WarehouseController {
    
    private WarehouseServiceImpl warehouseService;
    private final String baseUrl = "/spring/stars/operator/energyCompany/warehouse";
    private RolePropertyDao rolePropertyDao;
    
    /* Main page */
    @RequestMapping
    public String home(YukonUserContext userContext, 
                       ModelMap modelMap, 
                       int ecId, 
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        modelMap.addAttribute("warehouses", warehouseService.getWarehousesForEnergyCompany(ecId));
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        return "operator/warehouse/home.jsp";
    }
    
    /* Individual View */
    @RequestMapping ("view")
    public String viewWarehouse(YukonUserContext userContext, 
                                ModelMap modelMap, 
                                int ecId, 
                                int warehouseId, 
                                EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        modelMap.addAttribute("warehouse", warehouseService.getWarehouse(warehouseId));
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        modelMap.addAttribute("mode", "VIEW");
        return "operator/warehouse/view.jsp";
    }
    
    /* New */
    @RequestMapping ("new")
    public String newWarehouse(YukonUserContext userContext, 
                               ModelMap modelMap, 
                               int ecId, 
                               EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        //check permissions
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_MULTI_WAREHOUSE, userContext.getYukonUser());
        
        WarehouseDto warehouseDto = new WarehouseDto(new Warehouse(), new LiteAddress());
        //Populate with the basics
        warehouseDto.getWarehouse().setEnergyCompanyID(ecId);
        modelMap.addAttribute("warehouseDto", warehouseDto);
        modelMap.addAttribute("mode", "CREATE");
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        return "operator/warehouse/edit.jsp";
    }
    
    /* Create */
    @RequestMapping ("create")
    public String createWarehouse(YukonUserContext userContext,
                                  ModelMap modelMap, 
                                  int ecId,
                                  final @ModelAttribute ("warehouseDto") WarehouseDto warehouseDto, 
                                  BindingResult bindingResult,
                                  FlashScope flashScope,
                                  EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        //check permissions
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_MULTI_WAREHOUSE, userContext.getYukonUser());
        
        WarehouseDtoValidator validator = new WarehouseDtoValidator();
        validator.validate(warehouseDto, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return editWarehouse(userContext, modelMap, ecId,  warehouseDto.getWarehouse().getWarehouseID(), energyCompanyInfoFragment);
        }
        
        if(warehouseService.createWarehouse(warehouseDto) > 0) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.warehouse.warehouseNotCreated"));
            return "redirect:home?ecId=" + ecId;
        } else {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.warehouse.warehouseCreated"));
            return editWarehouse(userContext, modelMap, ecId,  warehouseDto.getWarehouse().getWarehouseID(), energyCompanyInfoFragment);
        }
    }
    
    /* Edit */
    @RequestMapping ("edit")
    public String editWarehouse(YukonUserContext userContext, 
                                ModelMap modelMap, 
                                int ecId, 
                                int warehouseId, 
                                EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        //check permissions
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_MULTI_WAREHOUSE, userContext.getYukonUser());
        
        modelMap.addAttribute("warehouseDto", warehouseService.getWarehouse(warehouseId));
        modelMap.addAttribute("mode", "EDIT");
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        return "operator/warehouse/edit.jsp";
    }
    
    /* Update */
    @RequestMapping ("update")
    public String updateWarehouse(YukonUserContext userContext,
                                  ModelMap modelMap,
                                 int ecId,
                                 final @ModelAttribute ("warehouseDto") WarehouseDto warehouseDto, 
                                 BindingResult bindingResult,
                                 FlashScope flashScope,
                                 EnergyCompanyInfoFragment energyCompanyInfoFragment) {
      //check permissions
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_MULTI_WAREHOUSE, userContext.getYukonUser());
        
        WarehouseDtoValidator validator = new WarehouseDtoValidator();
        validator.validate(warehouseDto, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return editWarehouse(userContext, modelMap, ecId,  warehouseDto.getWarehouse().getWarehouseID(), energyCompanyInfoFragment);
        }
        
        if(warehouseService.updateWarehouse(warehouseDto)) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.warehouse.warehouseUpdated"));
            return "redirect:view?warehouseId=" + warehouseDto.getWarehouse().getWarehouseID() + "&ecId=" + ecId;
        } else {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.warehouse.warehouseNotUpdated"));
            return editWarehouse(userContext, modelMap, ecId,  warehouseDto.getWarehouse().getWarehouseID(), energyCompanyInfoFragment);
        }
    }
    
    /* Delete */
    @RequestMapping ("delete")
    public String deleteWarehouse(YukonUserContext userContext, 
                                  ModelMap modelMap, 
                                  int warehouseId,
                                  int ecId,
                                  FlashScope flashScope,
                                  EnergyCompanyInfoFragment energyCompanyInfoFragment) {
      //check permissions
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_MULTI_WAREHOUSE, userContext.getYukonUser());
        
        if(warehouseService.deleteWarehouse(warehouseId)) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.warehouse.warehouseDeleted"));
            return "redirect:home?ecId=" + ecId;
        } else {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.warehouse.warehouseNotDeleted"));
            return viewWarehouse(userContext, modelMap, ecId, warehouseId, energyCompanyInfoFragment);
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
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "warehouse.warehouseName", "yukon.web.modules.operator.warehouse.warehouseDto.warehouse.nameRequired");
            YukonValidationUtils.checkExceedsMaxLength(errors, "warehouse.warehouseName", warehouseDto.getWarehouse().getWarehouseName(), 60);
            
            //Notes
            YukonValidationUtils.checkExceedsMaxLength(errors, "warehouse.notes", warehouseDto.getWarehouse().getNotes(), 300);
        }
    };
    
    @Autowired
    public void setWarehouseService(WarehouseServiceImpl warehouseService) {
        this.warehouseService = warehouseService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @ModelAttribute("indexUrl")
    public String getIndexUrl() {
        return this.baseUrl + "/home";
    }
    
    @ModelAttribute("viewUrl")
    public String getViewUrl() {
        return this.baseUrl + "/view";
    }
    
    @ModelAttribute("newUrl")
    public String getNewUrl() {
        return this.baseUrl + "/new";
    }
    
    @ModelAttribute("createUrl")
    public String getCreateUrl() {
        return this.baseUrl + "/create";
    }
    
    @ModelAttribute("editUrl")
    public String getEditUrl() {
        return this.baseUrl + "/edit";
    }
    
    @ModelAttribute("updateUrl")
    public String getUpdateUrl() {
        return this.baseUrl + "/update";
    }
    
    @ModelAttribute("deleteUrl")
    public String getDeleteUrl() {
        return this.baseUrl + "/delete";
    }
}
