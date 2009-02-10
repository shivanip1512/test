package com.cannontech.stars.dr.advice;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;

@Aspect
public class StarsDaoAdvice {
    private ECMappingDao mappingDao;
    private CustomerAccountDao customerAccountDao;

    @Pointcut("execution(* *.update(..)) || execution(* *.remove(..))")
    public void updateMethodNamePointCut() {
    }

    @Pointcut("execution(* *.save(..))")
    public void saveMethodNamePointCut() {
    }
    
    @Pointcut("execution(* *.addAdditionalContact(..))")
    public void additionalContactMethodNamePointCut() {}
    
    @Pointcut("execution(* *.removeAdditionalContact(..))")
    public void removeAdditionalContactMethodNamePointCut() {}

    @After("bean(customerEventDao) && saveMethodNamePointCut() && args(event)")
    public void doManualEventAction(ThermostatManualEvent event)
            throws Throwable {

        Integer thermostatId = event.getThermostatId();
        LiteStarsEnergyCompany energyCompany = mappingDao.getInventoryEC(thermostatId);

        // Clear and reload account info cache entry
        CustomerAccount account = customerAccountDao.getAccountByInventoryId(thermostatId);
        int accountId = account.getAccountId();

        LiteStarsCustAccountInformation custAccountInformation = energyCompany.getCustAccountInformation(accountId,
                                                                                                         false);
        energyCompany.deleteCustAccountInformation(custAccountInformation);

    }

    @After("bean(thermostatScheduleDao) && saveMethodNamePointCut() && args(schedule, energyCompany)")
    public void doThermostatScheduleAction(ThermostatSchedule schedule, 
    		LiteStarsEnergyCompany energyCompany)
    	throws Throwable {

        Integer accountId = schedule.getAccountId();
        if(accountId != null) {
	
	        // Clear and reload account info cache entry
	        LiteStarsCustAccountInformation custAccountInformation = energyCompany.getCustAccountInformation(accountId,
	                                                                                                         false);
	        energyCompany.deleteCustAccountInformation(custAccountInformation);
        }

    }

    @Autowired
    public void setMappingDao(ECMappingDao mappingDao) {
        this.mappingDao = mappingDao;
    }

    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

}
