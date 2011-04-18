package com.cannontech.web.admin.energyCompany.serviceCompany;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyService;
import com.cannontech.web.admin.energyCompany.serviceCompany.model.ServiceCompanyDtoValidator;
import com.cannontech.web.admin.energyCompany.serviceCompany.service.ServiceCompanyService;

@RequestMapping("/energyCompany/serviceCompany/*")
@Controller
public class ServiceCompanyController {
    
    private static final String baseUrl = "/spring/adminSetup/energyCompany/serviceCompany";
    private EnergyCompanyService energyCompanyService;
    private RolePropertyDao rolePropertyDao;
    private ServiceCompanyService serviceCompanyService;
    private ServiceCompanyDao serviceCompanyDao;
    private ServiceCompanyDtoValidator serviceCompanyDtoValidator;
    private YukonUserDao yukonUserDao;
    
    private void checkPermissionsAndSetupModel(EnergyCompanyInfoFragment energyCompanyInfoFragment,
                      ModelMap modelMap,
                      YukonUserContext userContext) {
        energyCompanyService.verifyViewPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        rolePropertyDao.verifyAnyProperties(userContext.getYukonUser(), YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES, YukonRoleProperty.OPERATOR_CONSUMER_INFO_WORK_ORDERS);
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        
    }
    
    private List<LiteYukonUser> availableLogins(int ignoreID) {
        List<LiteYukonUser> availableLogins = new ArrayList<LiteYukonUser>();
        int loginIds[] = Contact.findAvailableUserIDs(ignoreID);    //seems a bit broad
        for(int loginId : loginIds) {
            availableLogins.add(yukonUserDao.getLiteYukonUser(loginId));
        }
        return availableLogins;
    }

    /* Index */
    @RequestMapping ("list")
    public String index(YukonUserContext userContext, 
                        ModelMap modelMap, 
                        int ecId, 
                        EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
        //get all for this energy company
        modelMap.addAttribute("serviceCompanies", serviceCompanyDao.getAllServiceCompaniesForEnergyCompany(ecId));
        return "energyCompany/serviceCompany/list.jsp";
    }
    
    @RequestMapping("view")
    public String viewServiceCompany(YukonUserContext userContext, 
                                     ModelMap modelMap, 
                                     int ecId, 
                                     int serviceCompanyId,
                                     EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
        ServiceCompanyDto serviceCompany = serviceCompanyService.getServiceCompany(serviceCompanyId);
        modelMap.addAttribute("availableLogins", availableLogins(serviceCompany.getPrimaryContact().getLoginID()));
        modelMap.addAttribute("serviceCompany", serviceCompany);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        return "energyCompany/serviceCompany/edit.jsp";
    }
    
    @RequestMapping("new")
    public String newServiceCompany(YukonUserContext userContext, 
                                    ModelMap modelMap, 
                                    int ecId, 
                                    EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
        modelMap.addAttribute("availableLogins", availableLogins(UserUtils.USER_DEFAULT_ID));
        modelMap.addAttribute("serviceCompany", new ServiceCompanyDto());
        modelMap.addAttribute("mode", PageEditMode.CREATE);
        
        return "energyCompany/serviceCompany/edit.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST, value="update", params="create")
    public String createServiceCompany(YukonUserContext userContext, 
                                       ModelMap modelMap, 
                                       int ecId,
                                       final @ModelAttribute ("serviceCompany") ServiceCompanyDto serviceCompany, 
                                       BindingResult bindingResult,
                                       FlashScope flashScope,
                                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
        //verify the object
        serviceCompanyDtoValidator.validate(serviceCompany, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            modelMap.addAttribute("availableLogins", availableLogins(UserUtils.USER_DEFAULT_ID));
            modelMap.addAttribute("mode", PageEditMode.CREATE);
            return "energyCompany/serviceCompany/edit.jsp";
        }
        
        //do not allow unauthorized modification of designation codes
        if(!getEditDesignationCodes(userContext, ecId)) {
            serviceCompany.setDesignationCodes(null);
        }
        
        serviceCompanyService.createServiceCompany(serviceCompany, ecId);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.serviceCompany.serviceCompanyCreated", serviceCompany.getCompanyName()));
        
        return "redirect:list";
    }
    
    @RequestMapping("edit")
    public String editServiceCompany(YukonUserContext userContext, 
                                     ModelMap modelMap, 
                                     int ecId,
                                     int serviceCompanyId,
                                     EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
        ServiceCompanyDto serviceCompany = serviceCompanyService.getServiceCompany(serviceCompanyId);
        
        modelMap.addAttribute("serviceCompany", serviceCompany);
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        modelMap.addAttribute("availableLogins", availableLogins(serviceCompany.getPrimaryContact().getLoginID()));
        
        return "energyCompany/serviceCompany/edit.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST, value="update", params="update")
    public String updateServiceCompany(YukonUserContext userContext, 
                                       ModelMap modelMap, 
                                       int ecId,
                                       final @ModelAttribute ("serviceCompany") ServiceCompanyDto serviceCompany, 
                                       BindingResult bindingResult,
                                       FlashScope flashScope,
                                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
      //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
      //verify the object
        serviceCompanyDtoValidator.validate(serviceCompany, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            modelMap.addAttribute("mode", PageEditMode.EDIT);
            modelMap.addAttribute("availableLogins", availableLogins(serviceCompany.getPrimaryContact().getContactID()));
            return "energyCompany/serviceCompany/edit.jsp";
        }
        
        //do not allow unauthorized modification of designation codes
        if(!getEditDesignationCodes(userContext, ecId)) {
            serviceCompany.setDesignationCodes(null);
        }
        serviceCompanyService.updateServiceCompany(serviceCompany);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.serviceCompany.serviceCompanyUpdated", serviceCompany.getCompanyName()));
        modelMap.addAttribute("serviceCompanyId", serviceCompany.getCompanyId());
        return "redirect:view";
    }
    
    @RequestMapping(method=RequestMethod.POST, value="update", params="delete")
    public String deleteServiceCompany(YukonUserContext userContext, 
                                       ModelMap modelMap, 
                                       final @ModelAttribute ("serviceCompany") ServiceCompanyDto serviceCompany,
                                       FlashScope flashScope,
                                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
      //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        serviceCompanyService.deleteServiceCompany(serviceCompany);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.serviceCompany.serviceCompanyDeleted", serviceCompany.getCompanyName()));
        
        return "redirect:list";
    }
    
    @ModelAttribute ("canEditDesignationCodes")
    public boolean getEditDesignationCodes(YukonUserContext userContext, int ecId) {
        return rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_ALLOW_DESIGNATION_CODES, userContext.getYukonUser());
    }
    
    @ModelAttribute ("canViewDesignationCodes")
    public boolean getViewDesignationCodes(YukonUserContext userContext, int ecId) {
        return rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY, userContext.getYukonUser());
    }
    
    @ModelAttribute("baseUrl")
    public String getBaseUrl() {
        return baseUrl;
    }
    
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
    @Autowired
    public void setWarehouseService(ServiceCompanyService serviceCompanyService) {
        this.serviceCompanyService = serviceCompanyService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setServiceCompanyDtoValidator(ServiceCompanyDtoValidator serviceCompanyDtoValidator) {
        this.serviceCompanyDtoValidator = serviceCompanyDtoValidator;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    @Autowired
    public void setServiceCompanyDao(ServiceCompanyDao serviceCompanyDao) {
        this.serviceCompanyDao = serviceCompanyDao;
    }
}
