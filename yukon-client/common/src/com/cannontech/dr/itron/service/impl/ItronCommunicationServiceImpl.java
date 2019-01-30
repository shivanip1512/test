package com.cannontech.dr.itron.service.impl;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AddServicePointRequest;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AddServicePointResponse;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.dr.itron.service.ItronException;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;


public class ItronCommunicationServiceImpl implements ItronCommunicationService {
    
    private String url;
    private String password;
    private String userName;
    @Autowired private AccountService accountService;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    private static final Logger log = YukonLogManager.getLogger(ItronCommunicationServiceImpl.class); 
    
    @Autowired
    public ItronCommunicationServiceImpl(GlobalSettingDao settingDao) {
        url = "http://localhost:8083/itronSimulatorServer";
        // settingDao.getString(GlobalSettingType.ITRON_HCM_API_URL);
        userName = settingDao.getString(GlobalSettingType.ITRON_HCM_USERNAME);
        password = settingDao.getString(GlobalSettingType.ITRON_HCM_PASSWORD);
    }
      
    @Override
    public void addDevice(Hardware hardware) {
        //TODO add event log
        String itronUrl = url + "/addHANDevice";
        log.debug("Sending request to Itron {} to add device with Mac Id {}.", itronUrl, hardware.getMacAddress());
        AddHANDeviceRequest request = DeviceManagerHelper.buildRequest(hardware.getMacAddress(), hardware.getInventoryId());
        
        //TODO send to itron
        AddHANDeviceResponse response = new AddHANDeviceResponse();
        try {
            if (!response.getErrors().isEmpty()) {
                String errors = String.join(",", response.getErrors());
                throw new ItronException("Errors recieved from Itron:" + errors, errors);
            }
        } catch (Exception e) {
            throw new ItronException("Communication error", e);
        }
        log.debug("Request to Itron {} to add device with Mac Id {} is successful.", itronUrl, hardware.getMacAddress());
        
        if(hardware.getAccountId() > 0) {
            addServicePoint(hardware.getAccountId(), hardware.getEnergyCompanyId(), hardware.getInventoryId());
        }
    }
    
    @Override
    public void addServicePoint(int accountId, int energyCompanyId, int invenoryId) {
        //TODO add event log
        /*
         * Note: The newly created Service Point is not available in the user interface until an ESI is
         * associated to it. It is, however, in the database. (see DeviceManagerHelper buildRequest) 
         */
        String itronUrl = url + "/addServicePoint";
       
        AccountDto account = accountService.getAccountDto(accountId, energyCompanyId);
        AddServicePointRequest request = ServicePointHelper.buildRequest(account, invenoryId);
        log.debug("Sending request to Itron {} to add service point for account {}.", itronUrl, account.getAccountNumber());
        //TODO send to itron
        AddServicePointResponse response= new AddServicePointResponse();
       
        log.debug("Request to Itron {} to add service point for account {} is successful.", itronUrl, account.getAccountNumber());
    }
}
