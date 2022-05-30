package com.cannontech.web.dev;

import java.beans.PropertyEditorSupport;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.development.model.DemandResponseSetup;
import com.cannontech.development.model.DevAmr;
import com.cannontech.development.model.DevCCU;
import com.cannontech.development.model.DevCommChannel;
import com.cannontech.development.model.DevPaoType;
import com.cannontech.development.service.DevAmrCreationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.simulators.RegulatorVoltageControlMode;
import com.cannontech.simulators.message.request.AmrCreationSimulatorRequest;
import com.cannontech.simulators.message.request.AmrCreationSimulatorStatusRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dev.database.objects.DevCapControl;
import com.cannontech.web.dev.database.objects.DevEventLog;
import com.cannontech.web.dev.database.objects.DevRoleProperties;
import com.cannontech.web.dev.database.objects.DevStars;
import com.cannontech.web.dev.database.service.DevCapControlCreationService;
import com.cannontech.web.dev.database.service.DevRolePropUpdaterService;
import com.cannontech.web.dev.database.service.DevStarsCreationService;
import com.cannontech.web.dev.database.service.impl.DevEventLogCreationService;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/setupDatabase/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class SetupDevDbMethodController {
    
    private static final Logger log = YukonLogManager.getLogger(SetupDevDbMethodController.class);
    
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private DevRolePropUpdaterService devRolePropUpdaterService;
    @Autowired private DevAmrCreationService devAmrCreationService;
    @Autowired private DevCapControlCreationService devCapControlCreationService;
    @Autowired private DevStarsCreationService devStarsCreationService;
    @Autowired private DevEventLogCreationService devEventLogCreationService;
    @Autowired private RoleDao roleDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
    @Autowired private IDatabaseCache databaseCache;

    @RequestMapping("main")
    public void main(ModelMap model) {

        model.addAttribute("devRoleProperties",new DevRoleProperties());
        model.addAttribute("devAmr",  new DevAmr());
        model.addAttribute("devCapControl", new DevCapControl());
        model.addAttribute("devDemandResponse", new DemandResponseSetup());
        model.addAttribute("devStars", new DevStars());
        model.addAttribute("devEventLog",new DevEventLog());

        List<LiteYukonGroup> allGroups = yukonGroupDao.getAllGroups();
        List<LiteYukonGroup> resGroups = Lists.newArrayList();
        for (LiteYukonGroup grp : allGroups) {
            if (roleDao.getRolesForGroup(grp.getGroupID()).contains(YukonRole.RESIDENTIAL_CUSTOMER)) {
                resGroups.add(grp);
            }
        }

        model.addAttribute("resGroups", resGroups);
        model.addAttribute("allGroups", allGroups);

        model.addAttribute("allRoutes", databaseCache.getAllRoutes());

        List<LiteStarsEnergyCompany> allEnergyCompanies = starsDatabaseCache.getAllEnergyCompanies();
        model.addAttribute("allEnergyCompanies", allEnergyCompanies);
        
        model.addAttribute("eventSourceList", Lists.newArrayList(EventSource.values()));
        model.addAttribute("controlModeTypes", RegulatorVoltageControlMode.values());
        
        model.addAttribute("allPrograms", databaseCache.getAllLMPrograms());
        model.addAttribute("drPaoTypes", PaoType.getTwoWayLcrTypes());
        
    }

    @RequestMapping("checkAvailability")
    @ResponseBody
    public Map<String, Object> checkAvailability() {
        
        Map<String, Object> json = Maps.newHashMapWithExpectedSize(8);

        json.put("roleProperties", !devRolePropUpdaterService.isRunning());
        json.put("amr", !devAmrCreationService.isRunning());
        json.put("capControl", !devCapControlCreationService.isRunning());
        json.put("capControlProgress", devCapControlCreationService.getPercentComplete());
        json.put("demandResponse", true);
        json.put("demandResponseProgress", 100);
        //json.put("demandResponse", !devDemandResponseCreationService.isRunning());
        //json.put("demandResponseProgress", devDemandResponseCreationService.getPercentComplete());
        json.put("stars", !devStarsCreationService.isRunning());
        json.put("starsProgress", devStarsCreationService.getPercentComplete());
        json.put("eventLog", !devEventLogCreationService.isRunning());
        json.put("eventLogProgress", devEventLogCreationService.getPercentComplete());

        return json;
    }
    
    @RequestMapping("setupRoleProperties")
    public String setupRoleProperties(DevRoleProperties devRoleProperties, FlashScope flashScope, ModelMap model) {
        
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

        return "setupDatabase/rolePropertyWidget.jsp";
    }

    @RequestMapping("setupAMR")
    public String setupAMR(DevAmr devAmr, BindingResult bindingResult, FlashScope flashScope, ModelMap model) {

        amrValidator.validate(devAmr, bindingResult);

        if (bindingResult.hasErrors()) {
            flashScope.setError(
                YukonMessageSourceResolvable.createDefaultWithoutCode("Unable to start Setup AMR. Check Fields."));
        } else {
            try {
                AmrCreationSimulatorRequest request = new AmrCreationSimulatorRequest(devAmr);
                SimulatorResponse response =
                    simulatorsCommunicationService.sendRequest(request, SimulatorResponseBase.class);
                if (response.isSuccessful()) {
                    flashScope.setConfirm(
                        YukonMessageSourceResolvable.createDefaultWithoutCode("Devices are being created."));
                } else {
                    flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode(
                        "Can't create devices. AMR Creation Service is already running."));
                }
            } catch (Exception e) {
                log.error(e);
                flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                    "Unable to send message to Simulator Service: " + e.getMessage()));
            }
        }

        model.addAttribute("allRoutes", databaseCache.getAllRoutes());

        return "setupDatabase/amrWidget.jsp";
    }
    
    @RequestMapping("status")
    public String status(FlashScope flashScope, ModelMap model, final RedirectAttributes redirectAttributes) {
        try {
            SimulatorResponse response = simulatorsCommunicationService.sendRequest(
                new AmrCreationSimulatorStatusRequest(), SimulatorResponseBase.class);
            if (response.isSuccessful()) {
                flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("AMR Creation Service is already running."));
            } else {
                flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("AMR Creation Service is ready to create devices."));
            }
        } catch (Exception e) {
            log.error(e);
            flashScope.setError(
                YukonMessageSourceResolvable.createDefaultWithoutCode("Unable to send message to Simulator Service: " + e.getMessage()));
        }

        //ModelAndView mav = new ModelAndView("redirect:main");
       // mav.addObject("selectedTab", 2);
       // return mav;
        redirectAttributes.addFlashAttribute("selectedTab", 2);
        return "redirect:main";
        //model.addAttribute("selectedTab", 2);
       // return new ModelAndView("redirect:main");
    }

    @RequestMapping("setupCapControl")
    public String setupCapControl(DevCapControl devCapControl,
            BindingResult bindingResult, FlashScope flashScope, ModelMap model) {

        capControlValidator.validate(devCapControl, bindingResult);

        boolean simComFound = paoDao.getLiteYukonPaoByName(
                DevCommChannel.SIM.getName(), false).size() == 1;

        if (bindingResult.hasErrors()) {
            flashScope
                    .setError(YukonMessageSourceResolvable
                            .createDefaultWithoutCode("Unable to start Setup Cap Control. Check Fields."));
        } else if (!simComFound) {
            flashScope.setError(YukonMessageSourceResolvable
                    .createDefaultWithoutCode("Couldn't find comm with name "
                            + DevCommChannel.SIM.getName()
                            + ". Run AMR setup to create this comm."));
        } else if (!devCapControlCreationService.isRunning()) {
            try {
                devCapControlCreationService.executeSetup(devCapControl);
                flashScope
                        .setConfirm(YukonMessageSourceResolvable
                                .createDefaultWithoutCode("Successfully setup Cap Control"));
            } catch (Exception e) {
                log.warn("caught exception in Setup Cap Control", e);
                flashScope
                        .setError(YukonMessageSourceResolvable
                                .createDefaultWithoutCode("Unable to setup Cap Control: "
                                        + e.getMessage()));
            }
        }
        model.addAttribute("controlModeTypes", RegulatorVoltageControlMode.values());
        return "setupDatabase/capControlWidget.jsp";
    }
    
    @RequestMapping("setupDemandResponse")
    public String setupDemandResponse(@ModelAttribute("devDemandResponse") DemandResponseSetup devDemandResponse,
            BindingResult bindingResult, FlashScope flashScope, ModelMap model) {
        
        demandResponseValidator.validate(devDemandResponse, bindingResult);
        
        if (bindingResult.hasErrors()) {
            flashScope.setError(YukonMessageSourceResolvable
                            .createDefaultWithoutCode("Unable to start Setup Demand Response. Check Fields."));
        } 
        
/*        else if (!devDemandResponseCreationService.isRunning()) {
            try {
                devDemandResponseCreationService.executeSetup(devDemandResponse);
                flashScope
                        .setConfirm(YukonMessageSourceResolvable
                                .createDefaultWithoutCode("Successfully setup Demand Response"));
            } catch (Exception e) {
                log.warn("caught exception in Setup Demand Response", e);
                flashScope
                        .setError(YukonMessageSourceResolvable
                                .createDefaultWithoutCode("Unable to setup Demand Response: "
                                        + e.getMessage()));
            }
        }*/
        
        model.addAttribute("allPrograms", databaseCache.getAllLMPrograms());
        model.addAttribute("drPaoTypes", PaoType.getTwoWayLcrTypes());

        return "setupDatabase/demandResponseWidget.jsp";
    }

    @RequestMapping("setupEventLog")
    public String setupEventLog(DevEventLog devEventLog,
            BindingResult bindingResult, FlashScope flashScope, ModelMap model) {

        try {
            devEventLogCreationService.execute(devEventLog);
            flashScope.setConfirm(YukonMessageSourceResolvable
                    .createDefaultWithoutCode("Successfully setup Event Log"));
        } catch (Exception e) {
            log.warn("caught exception in Setup Event Log", e);
            flashScope.setError(YukonMessageSourceResolvable
                    .createDefaultWithoutCode("Unable to setup Event Log: "
                            + e.getMessage()));
        }

        model.addAttribute("eventSourceList",
                Lists.newArrayList(EventSource.values()));

        return "setupDatabase/eventLogWidget.jsp";
    }
    
    @RequestMapping("setupStars")
    public String setupStars(DevStars devStars, BindingResult bindingResult,
            LiteYukonUser user, FlashScope flashScope, ModelMap model) {
        
        starsValidator.validate(devStars, bindingResult);

        String ccuName = DevCCU.SIM_711.getName();
        Integer routeId = paoDao.getRouteIdForRouteName(ccuName);

        if (bindingResult.hasErrors()) {
            flashScope
                    .setError(YukonMessageSourceResolvable
                            .createDefaultWithoutCode("Unable to start Setup Stars. Check Fields."));
        } else if (routeId == null) {
            flashScope
                    .setError(YukonMessageSourceResolvable
                            .createDefaultWithoutCode("Couldn't find route with name "
                                    + ccuName
                                    + ". Run AMR setup with Create Cart Objects to create this route."));
        } else if (!devStarsCreationService.isRunning()) {

            try {
                devStarsCreationService.executeEnergyCompanyCreation(devStars);
                devStarsCreationService.executeStarsAccountCreation(devStars);
                flashScope.setConfirm(YukonMessageSourceResolvable
                        .createDefaultWithoutCode("Successfully setup stars"));
            } catch (Exception e) {
                log.warn("caught exception in Setup Stars", e);
                flashScope.setError(YukonMessageSourceResolvable
                        .createDefaultWithoutCode("Unable to setup Stars: "
                                + e.getMessage()));
            }
        }

        List<LiteStarsEnergyCompany> allEnergyCompanies = starsDatabaseCache.getAllEnergyCompanies();
        model.addAttribute("allEnergyCompanies", allEnergyCompanies);

        return "setupDatabase/starsWidget.jsp";
    }
    
    /**
     * This method will insert fake locations (latitude and longitudes) for all paos 
     * that are of class carrier, transmitter, capcontrol or rfmesh and that 
     * do not currently have a pao location.
     */
    @RequestMapping("fake-locations")
    public @ResponseBody void addFakePaoLocations() {
        
        DecimalFormat df = new DecimalFormat("##.######");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select PAObjectId");
        sql.append("from YukonPAObject");
        sql.append("where category = 'DEVICE'");
        sql.append("and  PAOClass in ('CARRIER', 'TRANSMITTER', 'CAPCONTROL', 'RFMESH')");
        sql.append("and PAObjectId not in (select PAObjectId from PaoLocation)");
        
        List<Integer> paoIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
        for (Integer paoId : paoIds) {
            
            double latitude = Double.parseDouble(df.format((Math.random() * (45.3 - 45.0)) + 45.0));
            double longitude = Double.parseDouble(df.format(((Math.random() * (93.5 - 93.0)) + 93.0) * -1));
            
            sql = new SqlStatementBuilder();
            SqlParameterSink insertInto = sql.insertInto("PaoLocation");
            insertInto.addValue("PAObjectId", paoId);
            insertInto.addValue("Latitude", latitude);
            insertInto.addValue("Longitude", longitude);
            jdbcTemplate.update(sql);
        }
        
    }

    private final Validator capControlValidator = new SimpleValidator<DevCapControl>(DevCapControl.class) {
        
        @Override
        public void doValidation(DevCapControl devCapControl, Errors errors) {

            if (devCapControl.getNumAreas() == null
                    || devCapControl.getNumAreas() < 0) {
                errors.rejectValue("numAreas",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devCapControl.getNumSubs() == null
                    || devCapControl.getNumSubs() < 0) {
                errors.rejectValue("numSubs",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devCapControl.getNumSubBuses() == null
                    || devCapControl.getNumSubBuses() < 0) {
                errors.rejectValue("numSubBuses",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devCapControl.getNumFeeders() == null
                    || devCapControl.getNumFeeders() < 0) {
                errors.rejectValue("numFeeders",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devCapControl.getNumCapBanks() == null
                    || devCapControl.getNumCapBanks() < 0) {
                errors.rejectValue("numCapBanks",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devCapControl.getNumRegulators() == null
                    || devCapControl.getNumRegulators() < 0) {
                errors.rejectValue("numRegulators",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devCapControl.getOffset() == null
                    || devCapControl.getOffset() < 0) {
                errors.rejectValue("offset",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }
        }
    };

    private final Validator amrValidator = new SimpleValidator<DevAmr>(DevAmr.class) {

        @Override
        public void doValidation(DevAmr devAmr, Errors errors) {

            if (devAmr.getNumAdditionalMeters() == null
                    || devAmr.getNumAdditionalMeters() < 0) {
                errors.rejectValue("numAdditionalMeters",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devAmr.getAddressRangeMax() == null
                    || devAmr.getAddressRangeMax() < 0) {
                errors.rejectValue("addressRangeMax",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devAmr.getAddressRangeMin() == null
                    || devAmr.getAddressRangeMin() < 0) {
                errors.rejectValue("addressRangeMin",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devAmr.getAddressRangeMin() != null
                    && devAmr.getAddressRangeMax() != null
                    && devAmr.getAddressRangeMin() >= devAmr
                            .getAddressRangeMax()) {
                String[] arg = { "Must be smaller than Address Range Min" };
                errors.rejectValue(
                        "addressRangeMin",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.generic",
                        arg, "");
                String[] arg2 = { "Must be larger than Address Range Max" };
                errors.rejectValue(
                        "addressRangeMax",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.generic",
                        arg2, "");
            }
        }
    };
    
    private final Validator demandResponseValidator = new SimpleValidator<DemandResponseSetup>(DemandResponseSetup.class) {

        @Override
        public void doValidation(DemandResponseSetup demandResponseSetup, Errors errors) {
            
            if (demandResponseSetup.getTemplateName().isBlank()) {
                errors.rejectValue("templateName", "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.empty");
            }

            if (demandResponseSetup.getScenarios() < 0) {
                errors.rejectValue("scenarios",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }
            
            if (demandResponseSetup.getControlAreas() < 0) {
                errors.rejectValue("controlAreas",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }
            
            if (demandResponseSetup.getPrograms() < 0) {
                errors.rejectValue("programs",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }
            
            if (demandResponseSetup.getDevices() < 0) {
                errors.rejectValue("devices",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }
            
            if (demandResponseSetup.getTypes().isEmpty()) {
                errors.rejectValue("types", "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.empty");
            }
        }
    };

    private final Validator starsValidator = new SimpleValidator<DevStars>(DevStars.class) {
        @Override
        public void doValidation(DevStars devStars, Errors errors) {

            // ACCOUNTS

            if (devStars.getDevStarsAccounts().getAccountNumMax() == null) {
                errors.rejectValue("devStarsAccounts.accountNumMax",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.empty");
            } else {
                if (devStars.getDevStarsAccounts().getAccountNumMin() != null
                        && devStars.getDevStarsAccounts().getAccountNumMin()
                                .intValue() >= devStars.getDevStarsAccounts()
                                .getAccountNumMax().intValue()) {
                    String[] arg = { "Must be smaller than Account Range # End" };
                    errors.rejectValue(
                            "devStarsAccounts.accountNumMin",
                            "yukon.web.modules.dev.setupDatabase.setupDevDatabase.generic",
                            arg, "");
                    String[] arg2 = { "Must be larger than Account Range # Start" };
                    errors.rejectValue(
                            "devStarsAccounts.accountNumMax",
                            "yukon.web.modules.dev.setupDatabase.setupDevDatabase.generic",
                            arg2, "");
                }
                if (devStars.getDevStarsAccounts().getAccountNumMax() < 0) {
                    errors.rejectValue("devStarsAccounts.accountNumMax",
                            "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
                }
            }

            if (devStars.getDevStarsAccounts().getAccountNumMin() == null) {
                errors.rejectValue("devStarsAccounts.accountNumMin",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.empty");
            } else if (devStars.getDevStarsAccounts().getAccountNumMin() < 0) {
                errors.rejectValue("devStarsAccounts.accountNumMin",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devStars.getDevStarsAccounts().getNumAccounts() == null) {
                errors.rejectValue("devStarsAccounts.numAccounts",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.empty");
            } else if (devStars.getDevStarsAccounts().getNumAccounts() <= 0) {
                errors.rejectValue("devStarsAccounts.numAccounts",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            // HARDWARE
            if (devStars.getDevStarsHardware().getSerialNumMax() == null) {
                errors.rejectValue("devStarsHardware.serialNumMax",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.empty");
            } else {
                if (devStars.getDevStarsHardware().getSerialNumMin() != null
                        && devStars.getDevStarsHardware().getSerialNumMin()
                                .intValue() >= devStars.getDevStarsHardware()
                                .getSerialNumMax().intValue()) {
                    String[] arg = { "Must be smaller than Serial Range # End" };
                    errors.rejectValue(
                            "devStarsHardware.serialNumMin",
                            "yukon.web.modules.dev.setupDatabase.setupDevDatabase.generic",
                            arg, "");
                    String[] arg2 = { "Must be larger than Serial Range # Start" };
                    errors.rejectValue(
                            "devStarsHardware.serialNumMax",
                            "yukon.web.modules.dev.setupDatabase.setupDevDatabase.generic",
                            arg2, "");
                }
                if (devStars.getDevStarsHardware().getSerialNumMax() <= 0) {
                    errors.rejectValue("devStarsHardware.serialNumMax",
                            "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
                }
            }

            if (devStars.getDevStarsHardware().getSerialNumMin() == null) {
                errors.rejectValue("devStarsHardware.serialNumMin",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.empty");
            } else if (devStars.getDevStarsHardware().getSerialNumMin() <= 0) {
                errors.rejectValue("devStarsHardware.serialNumMin",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devStars.getDevStarsHardware().getNumPerAccount() == null) {
                errors.rejectValue("devStarsHardware.numPerAccount",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.empty");
            } else if (devStars.getDevStarsHardware().getNumPerAccount() < 0) {
                errors.rejectValue("devStarsHardware.numPerAccount",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devStars.getDevStarsHardware().getNumExtra() == null) {
                errors.rejectValue("devStarsHardware.numExtra",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.empty");
            } else if (devStars.getDevStarsHardware().getNumExtra() < 0) {
                errors.rejectValue("devStarsHardware.numExtra",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.mustBePositive");
            }

            if (devStars.getEnergyCompany() == null) {
                if (StringUtils.isBlank(devStars.getNewEnergyCompanyName())) {
                    errors.rejectValue("energyCompany",
                            "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.empty");
                } else if (ecDao.findEnergyCompany(devStars
                        .getNewEnergyCompanyName()) != null) {
                    errors.rejectValue("energyCompany",
                            "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.unavailable");
                }

            }

            if (devStars.getEnergyCompany() != null
                    && devStars.getDevStarsAccounts().getAccountNumMin() != null
                    && devStars.getDevStarsAccounts().getNumAccounts() != null) {
                int initial = devStars.getDevStarsAccounts().getAccountNumMin();
                int end = initial
                        + devStars.getDevStarsAccounts().getNumAccounts();
                for (int accountId = initial; accountId < end; accountId++) {
                    if (devStarsCreationService.doesAccountExist(Integer
                            .toString(accountId), devStars.getEnergyCompany()
                            .getEnergyCompanyId())) {
                        String[] arg = { Integer.toString(accountId) };
                        errors.rejectValue(
                                "devStarsAccounts.accountNumMin",
                                "yukon.web.modules.dev.setupDatabase.setupDevDatabase.error.accountExists",
                                arg, "");
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