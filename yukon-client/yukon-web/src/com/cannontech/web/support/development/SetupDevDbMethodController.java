package com.cannontech.web.support.development;

import java.beans.PropertyEditorSupport;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckDevelopmentMode;
import com.cannontech.web.support.development.database.objects.DevPaoType;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;

@Controller
@RequestMapping("/development/setupDatabase/*")
@CheckDevelopmentMode
public class SetupDevDbMethodController {
    private static final Logger log = YukonLogManager.getLogger(SetupDevDbMethodController.class);
    @Autowired private DevDatabasePopulationService devDatabasePopulationService;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private PaoDao paoDao;

    @RequestMapping
    public void main(ModelMap model) {
        DevDbSetupTask devDbSetupTask = devDatabasePopulationService.getExecuting();
        if (devDbSetupTask == null) {
            devDbSetupTask = new DevDbSetupTask();
        }
        model.addAttribute("devDbSetupTask", devDbSetupTask);
        
        LiteYukonPAObject[] allRoutes = paoDao.getAllLiteRoutes();
        model.addAttribute("allRoutes", allRoutes);
        
        List<LiteStarsEnergyCompany> allEnergyCompanies = starsDatabaseCache.getAllEnergyCompanies();
        model.addAttribute("allEnergyCompanies", allEnergyCompanies);
    }

    @RequestMapping
    public String setupDatabase(@ModelAttribute("devDbSetupTask") DevDbSetupTask devDbSetupTask,
                                   LiteYukonUser user, FlashScope flashScope, ModelMap model) {
        try {
            devDatabasePopulationService.executeFullDatabasePopulation(devDbSetupTask);

            flashScope.setConfirm(YukonMessageSourceResolvable
                .createDefaultWithoutCode("Setup of development database successful"));
        } catch (Exception e) {
            log.warn("caught exception in setupDevDatabase", e);
            flashScope.setError(YukonMessageSourceResolvable
                .createDefaultWithoutCode("Database setup encountered a problem and may not have successfully completed: " + e.getMessage()));
        }
        setupPage(model, devDbSetupTask);

        return "redirect:main";
    }
    
    @RequestMapping
    public String cancelExecution() {
        devDatabasePopulationService.cancelExecution();
        return "redirect:main";
    }
    
    private void setupPage(ModelMap model, DevDbSetupTask devDbSetupTask) {
        model.addAttribute("devDbSetupTask", devDbSetupTask);
        List<LiteStarsEnergyCompany> allEnergyCompanies = starsDatabaseCache.getAllEnergyCompanies();
        model.addAttribute("allEnergyCompanies", allEnergyCompanies);
    }
    
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
