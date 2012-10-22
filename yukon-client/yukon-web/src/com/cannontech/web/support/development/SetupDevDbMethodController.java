package com.cannontech.web.support.development;

import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.Map;

import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.cannontech.web.support.development.database.objects.DevAMR;
import com.cannontech.web.support.development.database.objects.DevCCU;
import com.cannontech.web.support.development.database.objects.DevCapControl;
import com.cannontech.web.support.development.database.objects.DevCommChannel;
import com.cannontech.web.support.development.database.objects.DevPaoType;
import com.cannontech.web.support.development.database.objects.DevRoleProperties;
import com.cannontech.web.support.development.database.objects.DevStars;
import com.cannontech.web.support.development.database.service.impl.DevAMRCreationService;
import com.cannontech.web.support.development.database.service.impl.DevCapControlCreationService;
import com.cannontech.web.support.development.database.service.impl.DevRolePropUpdaterService;
import com.cannontech.web.support.development.database.service.impl.DevStarsCreationService;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/development/setupDatabase/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class SetupDevDbMethodController {
    private static final Logger log = YukonLogManager.getLogger(SetupDevDbMethodController.class);
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private DevRolePropUpdaterService devRolePropUpdaterService;
    @Autowired private DevAMRCreationService devAMRCreationService;
    @Autowired private DevCapControlCreationService devCapControlCreationService;
    @Autowired private DevStarsCreationService devStarsCreationService;
    @Autowired private RoleDao roleDao;
    @Autowired private EnergyCompanyDao ecDao;

    @RequestMapping
    public void main(ModelMap model) {
        DevAMR devAMR = new DevAMR();
        DevCapControl devCapControl = new DevCapControl();
        DevStars devStars = new DevStars();
        DevRoleProperties devRoleProperties = new DevRoleProperties();

        model.addAttribute("devRoleProperties",devRoleProperties);
        model.addAttribute("devAMR", devAMR);
        model.addAttribute("devCapControl", devCapControl);
        model.addAttribute("devStars", devStars);

        List<LiteYukonGroup> allGroups = yukonGroupDao.getAllGroups();
        List<LiteYukonGroup> resGroups = Lists.newArrayList();
        for (LiteYukonGroup grp : allGroups) {
            if (roleDao.getRolesForGroup(grp.getGroupID()).contains(YukonRole.RESIDENTIAL_CUSTOMER)) {
                resGroups.add(grp);
            }
        }

        model.addAttribute("resGroups", resGroups);
        model.addAttribute("allGroups", allGroups);

        LiteYukonPAObject[] allRoutes = paoDao.getAllLiteRoutes();
        model.addAttribute("allRoutes", allRoutes);

        List<LiteStarsEnergyCompany> allEnergyCompanies = starsDatabaseCache.getAllEnergyCompanies();
        model.addAttribute("allEnergyCompanies", allEnergyCompanies);
    }

    @RequestMapping
    @ResponseBody
    public String checkAvailability() {
        JSONObject json = new JSONObject();
        
        json.put("roleProperties", !devRolePropUpdaterService.isRunning());
        json.put("amr", !devAMRCreationService.isRunning());
        json.put("capControl", !devCapControlCreationService.isRunning());
        json.put("capControlProgress", devCapControlCreationService.getPercentComplete());
        json.put("stars", !devStarsCreationService.isRunning());
        json.put("starsProgress", devStarsCreationService.getPercentComplete());
        
        return json.toString();
    }
    
    @RequestMapping
    public String setupRoleProperties(DevRoleProperties devRoleProperties, LiteYukonUser user, FlashScope flashScope, ModelMap model) {
        
        if (!devRolePropUpdaterService.isRunning()) {
            try {
                Map<YukonRole,Boolean> results = devRolePropUpdaterService.executeSetup(devRoleProperties);
                model.addAttribute("results",results);
            } catch (Exception e) {
                log.warn("caught exception in Setup Role Properties", e);
                flashScope.setError(YukonMessageSourceResolvable
                                    .createDefaultWithoutCode("Unable to setup Role Properties: " + e.getMessage()));
            }
        }
        
        List<LiteYukonGroup> allGroups = yukonGroupDao.getAllGroups();
        List<LiteYukonGroup> resGroups = Lists.newArrayList();
        for (LiteYukonGroup grp : allGroups) {
            if (roleDao.getRolesForGroup(grp.getGroupID()).contains(YukonRole.RESIDENTIAL_CUSTOMER)) {
                resGroups.add(grp);
            }
        }

        model.addAttribute("resGroups", resGroups);
        model.addAttribute("allGroups", allGroups);

        model.addAttribute("devRoleProperties",devRoleProperties);

        return "development/setupDatabase/rolePropertyWidget.jsp";
    }

    @RequestMapping 
    public String setupAMR(DevAMR devAMR, BindingResult bindingResult, LiteYukonUser user, FlashScope flashScope, ModelMap model) {

        amrValidator.validate(devAMR, bindingResult);

        if (bindingResult.hasErrors()) {
            flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("Unable to start Setup AMR. Check Fields."));
        } else if (!devAMRCreationService.isRunning()) {
            try {
                devAMRCreationService.executeSetup(devAMR);
                flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Successfully Setup AMR"));
            } catch (Exception e) {
                log.warn("caught exception in Setup AMR", e);
                flashScope.setError(YukonMessageSourceResolvable
                                    .createDefaultWithoutCode("Unable to setup AMR: " + e.getMessage()));
            }
        }

        LiteYukonPAObject[] allRoutes = paoDao.getAllLiteRoutes();
        model.addAttribute("allRoutes", allRoutes);

        return "development/setupDatabase/amrWidget.jsp";
    }

    @RequestMapping
    public String setupCapControl(DevCapControl devCapControl, BindingResult bindingResult, LiteYukonUser user, FlashScope flashScope, ModelMap model) {

        capControlValidator.validate(devCapControl, bindingResult);
        
        boolean simComFound = paoDao.getLiteYukonPaoByName(DevCommChannel.SIM.getName(), false).size() == 1;
        
        if (bindingResult.hasErrors()) {
            flashScope.setError(YukonMessageSourceResolvable .createDefaultWithoutCode("Unable to start Setup Cap Control. Check Fields."));
        } else if (!simComFound) {
            flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("Couldn't find comm with name " + DevCommChannel.SIM.getName() + ". Run AMR setup to create this comm."));
        } else if (!devCapControlCreationService.isRunning()) {
            try {
                devCapControlCreationService.executeSetup(devCapControl);
                flashScope.setConfirm(YukonMessageSourceResolvable .createDefaultWithoutCode("Successfully setup Cap Control"));
            } catch (Exception e) {
                log.warn("caught exception in Setup Cap Control", e);
                flashScope.setError(YukonMessageSourceResolvable
                                    .createDefaultWithoutCode("Unable to setup Cap Control: " + e.getMessage()));
            }
        }

        return "development/setupDatabase/capControlWidget.jsp";
    }

    @RequestMapping
    public String setupStars(DevStars devStars, BindingResult bindingResult, LiteYukonUser user, FlashScope flashScope, ModelMap model) {

        starsValidator.validate(devStars, bindingResult);

        String ccuName = DevCCU.SIM_711.getName();
        Integer routeId = paoDao.getRouteIdForRouteName(ccuName);
        
        if (bindingResult.hasErrors()) {
            flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("Unable to start Setup Stars. Check Fields."));
        } else if (routeId == null) {
            flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("Couldn't find route with name " + ccuName + ". Run AMR setup with Create Cart Objects to create this route."));
        } else if (!devStarsCreationService.isRunning()) {
            try {
                devStarsCreationService.executeSetup(devStars);
                flashScope.setConfirm(YukonMessageSourceResolvable .createDefaultWithoutCode("Successfully setup stars"));
            } catch (Exception e) {
                log.warn("caught exception in Setup Stars", e);
                flashScope.setError(YukonMessageSourceResolvable
                                    .createDefaultWithoutCode("Unable to setup Stars: " + e.getMessage()));
            }
        }

        List<LiteStarsEnergyCompany> allEnergyCompanies = starsDatabaseCache.getAllEnergyCompanies();
        model.addAttribute("allEnergyCompanies", allEnergyCompanies);


        return "development/setupDatabase/starsWidget.jsp";
    }

    private final Validator capControlValidator =
            new SimpleValidator<DevCapControl>(DevCapControl.class) {
        @Override
        public void doValidation(DevCapControl devCapControl, Errors errors) {
            
            if (devCapControl.getNumAreas() == null || devCapControl.getNumAreas() < 0) {
                errors.rejectValue("numAreas", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            if (devCapControl.getNumSubs() == null || devCapControl.getNumSubs() < 0) {
                errors.rejectValue("numSubs", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            if (devCapControl.getNumSubBuses() == null || devCapControl.getNumSubBuses() < 0) {
                errors.rejectValue("numSubBuses", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive"); 
            }
            
            if (devCapControl.getNumFeeders() == null || devCapControl.getNumFeeders() < 0) {
                errors.rejectValue("numFeeders", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            if (devCapControl.getNumCapBanks() == null || devCapControl.getNumCapBanks() < 0) {
                errors.rejectValue("numCapBanks", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            if (devCapControl.getNumRegulators() == null || devCapControl.getNumRegulators() < 0) {
                errors.rejectValue("numRegulators", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive"); 
            }
            
            if (devCapControl.getOffset() == null || devCapControl.getOffset() < 0) {
                errors.rejectValue("offset", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
        }
    };

    private final Validator amrValidator =
            new SimpleValidator<DevAMR>(DevAMR.class) {
        
        @Override
        public void doValidation(DevAMR devAMR, Errors errors) {
            
            if (devAMR.getNumAdditionalMeters() == null || devAMR.getNumAdditionalMeters() < 0) {
                errors.rejectValue("numAdditionalMeters", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            if (devAMR.getAddressRangeMax() == null || devAMR.getAddressRangeMax() < 0) {
                errors.rejectValue("addressRangeMax", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            if (devAMR.getAddressRangeMin() == null || devAMR.getAddressRangeMin() < 0) {
                errors.rejectValue("addressRangeMin", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive"); 
            }
            
            if (devAMR.getAddressRangeMin() != null && devAMR.getAddressRangeMax() != null 
                    && devAMR.getAddressRangeMin() >= devAMR.getAddressRangeMax()) {
                String[] arg = {"Must be smaller than Address Range Min"};
                errors.rejectValue("addressRangeMin", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.generic",arg,""); 
                String[] arg2 = {"Must be larger than Address Range Max"};
                errors.rejectValue("addressRangeMax", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.generic",arg2,""); 
            }
        }
    };

    private final Validator starsValidator =
            new SimpleValidator<DevStars>(DevStars.class) {
        @Override
        public void doValidation(DevStars devStars, Errors errors) {
            
            // ACCOUNTS
            
            if (devStars.getDevStarsAccounts().getAccountNumMax() == null) {
                errors.rejectValue("devStarsAccounts.accountNumMax", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.empty");
            } else {
                if (devStars.getDevStarsAccounts().getAccountNumMin() != null 
                            && devStars.getDevStarsAccounts().getAccountNumMin().intValue() >= devStars.getDevStarsAccounts().getAccountNumMax().intValue()) {
                    String[] arg = {"Must be smaller than Account Range # End"};
                    errors.rejectValue("devStarsAccounts.accountNumMin", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.generic",arg,""); 
                    String[] arg2 = {"Must be larger than Account Range # Start"};
                    errors.rejectValue("devStarsAccounts.accountNumMax", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.generic",arg2,""); 
                }
                if (devStars.getDevStarsAccounts().getAccountNumMax() < 0) {
                    errors.rejectValue("devStarsAccounts.accountNumMax", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
                }
            }

            if (devStars.getDevStarsAccounts().getAccountNumMin() == null) {
                errors.rejectValue("devStarsAccounts.accountNumMin", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.empty");
            } else if (devStars.getDevStarsAccounts().getAccountNumMin() < 0) {
                errors.rejectValue("devStarsAccounts.accountNumMin", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            if (devStars.getDevStarsAccounts().getNumAccounts() == null) {
                errors.rejectValue("devStarsAccounts.numAccounts", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.empty");
            } else if (devStars.getDevStarsAccounts().getNumAccounts() <= 0) {
                errors.rejectValue("devStarsAccounts.numAccounts", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            // HARDWARE
            if (devStars.getDevStarsHardware().getSerialNumMax() == null) {
                errors.rejectValue("devStarsHardware.serialNumMax", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.empty");
            } else {
                if (devStars.getDevStarsHardware().getSerialNumMin() != null 
                            && devStars.getDevStarsHardware().getSerialNumMin().intValue() >= devStars.getDevStarsHardware().getSerialNumMax().intValue()) {
                    String[] arg = {"Must be smaller than Serial Range # End"};
                    errors.rejectValue("devStarsHardware.serialNumMin", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.generic",arg,""); 
                    String[] arg2 = {"Must be larger than Serial Range # Start"};
                    errors.rejectValue("devStarsHardware.serialNumMax", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.generic",arg2,""); 
                }
                if (devStars.getDevStarsHardware().getSerialNumMax() <= 0) {
                    errors.rejectValue("devStarsHardware.serialNumMax", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
                }
            }
            
            if (devStars.getDevStarsHardware().getSerialNumMin() == null) {
                errors.rejectValue("devStarsHardware.serialNumMin", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.empty");
            } else if (devStars.getDevStarsHardware().getSerialNumMin() <= 0) {
                errors.rejectValue("devStarsHardware.serialNumMin", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            if (devStars.getDevStarsHardware().getNumPerAccount() == null) {
                errors.rejectValue("devStarsHardware.numPerAccount", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.empty");
            } else if (devStars.getDevStarsHardware().getNumPerAccount() <= 0) {
                errors.rejectValue("devStarsAccounts.numPerAccount", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            if (devStars.getDevStarsHardware().getNumExtra() == null) {
                errors.rejectValue("devStarsHardware.numExtra", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.empty");
            } else if (devStars.getDevStarsHardware().getNumExtra() <= 0) {
                errors.rejectValue("devStarsHardware.numExtra", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.mustBePositive");
            }
            
            if (devStars.getEnergyCompany() == null) {
                if (StringUtils.isBlank(devStars.getNewEnergyCompanyName())) {
                    errors.rejectValue("energyCompany", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.empty");
                } else if (ecDao.findEnergyCompanyByName(devStars.getNewEnergyCompanyName()) != null){
                    errors.rejectValue("energyCompany", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.unavailable");
                }
                
            }
            
            if (devStars.getEnergyCompany() != null
                        && devStars.getDevStarsAccounts().getAccountNumMin() != null
                        && devStars.getDevStarsAccounts().getNumAccounts() != null) {
                int initial = devStars.getDevStarsAccounts().getAccountNumMin();
                int end = initial + devStars.getDevStarsAccounts().getNumAccounts();
                for (int accountId=initial;accountId<end;accountId++) {
                    if (devStarsCreationService.doesAccountExist(Integer.toString(accountId), devStars.getEnergyCompany().getEnergyCompanyId())) {
                        String[] arg = {Integer.toString(accountId)};
                        errors.rejectValue("devStarsAccounts.accountNumMin", "yukon.web.modules.support.setupDatabase.setupDdevDatabase.error.accountExists",arg,"");
                        break;
                    }
                }
            }
        }
    };

    @InitBinder
    public void setupBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LiteStarsEnergyCompany.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String energyCompanyIdString) throws IllegalArgumentException {
                LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(Integer.valueOf(energyCompanyIdString));
                setValue(energyCompany);
            }
        });
        binder.registerCustomEditor(DevPaoType.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String paoTypeString) throws IllegalArgumentException {
                if (paoTypeString.isEmpty()) {
                    setValue(null);
                    return;
                }
                PaoType paoType = PaoType.valueOf(paoTypeString);
                DevPaoType devPaoType = new DevPaoType(paoType);
                setValue(devPaoType);
            }
        });
    }
}
