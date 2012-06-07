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
import com.cannontech.common.model.Address;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckDevelopmentMode;
import com.cannontech.web.support.development.database.objects.DevPaoType;
import com.cannontech.web.support.development.database.objects.DevStars;
import com.cannontech.web.support.development.database.objects.DevStarsAccounts;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/development/setupDatabase/*")
@CheckDevelopmentMode
public class SetupDevDbMethodController {
    private static final Logger log = YukonLogManager.getLogger(SetupDevDbMethodController.class);
    @Autowired private DevDatabasePopulationService devDatabasePopulationService;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonGroupDao yukonGroupDao;
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
            setupStarsAccounts(devDbSetupTask.getDevStars());
            devDatabasePopulationService.executeFullDatabasePopulation(devDbSetupTask);

            flashScope.setConfirm(YukonMessageSourceResolvable
                .createDefaultWithoutCode("Setup of development database successful"));
        } catch (Exception e) {
            log.warn("caught exception in setupDevDatabase", e);
            flashScope.setError(YukonMessageSourceResolvable
                .createDefaultWithoutCode("Unable to setup development database: " + e.getMessage()));
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
    
    private void setupStarsAccounts(DevStars devStars) {
        DevStarsAccounts devStarsAccounts = devStars.getDevStarsAccounts();
        int accountNumIterator = devStarsAccounts.getAccountNumMin();
        for (int i = 0; i < devStarsAccounts.getNumAccounts(); i++) {
            UpdatableAccount account = new UpdatableAccount();
            String accountNumString = String.valueOf(accountNumIterator);
            AccountDto accountDto = new AccountDto();
            accountDto.setAccountNumber(accountNumString);
            accountDto.setLastName("last" + accountNumString);
            accountDto.setFirstName("first" + accountNumString);
            accountDto.setCompanyName("company" + accountNumString);
            accountDto.setHomePhone("555-555-5555");
            accountDto.setWorkPhone("555-555-5555");
            accountDto.setEmailAddress(accountNumString + "@test.com");
            accountDto.setStreetAddress(new Address("1234 Fake Street", "5678 Really Fake Street", "Fakeland", "MN", "55555", "Fake County"));
            accountDto.setBillingAddress(new Address("1234 Fake Street", "5678 Really Fake Street", "Fakeland", "MN", "55555", "Fake County"));
            accountDto.setUserName(accountNumString);
            accountDto.setPassword(accountNumString);
            int energyCompanyId = devStars.getEnergyCompany().getEnergyCompanyId();
            List<LiteYukonGroup> residentialGroups = ecMappingDao.getResidentialGroups(energyCompanyId);
            if (residentialGroups.isEmpty()) {
                LiteYukonGroup liteYukonGroup = yukonGroupDao.getLiteYukonGroupByName("Residential Customers Grp");
                ecMappingDao.addECToResidentialGroupMapping(energyCompanyId, Lists.newArrayList(liteYukonGroup.getGroupID()));
                accountDto.setLoginGroup(liteYukonGroup.getGroupName());
            } else {
                accountDto.setLoginGroup(residentialGroups.get(0).getGroupName());
            }
            accountDto.setMapNumber(accountNumString);
            accountDto.setAltTrackingNumber(accountNumString);
            accountDto.setIsCommercial(false);
            accountDto.setCustomerNumber(accountNumString);
//            accountDto.setCustomerStatus(customerStatus); // not sure what this is
            accountDto.setIsCustAtHome(true);
            accountDto.setPropertyNotes("Property Notes for account number: " + accountNumString);
            accountDto.setAccountNotes("Account Notes for account number: " + accountNumString);
            account.setAccountDto(accountDto);
            account.setAccountNumber(accountNumString);
            devStarsAccounts.getAccounts().add(account);
            accountNumIterator++;
        }
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
