package com.cannontech.stars.dr.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.Thermostat;
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

    @Around("bean(customerAccountDao) && updateMethodNamePointCut() && args(account)")
    public Object doCustomerAccountAction(ProceedingJoinPoint pjp,
            CustomerAccount account) throws Throwable {
        // get Energy Company before the mapping is removed by pjp.proceed()
        LiteStarsEnergyCompany energyCompany = mappingDao.getCustomerAccountEC(account);

        Object returnValue = pjp.proceed(); // required

        LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(account.getAccountId(),
                                                                                               false);
        if (liteAcctInfo != null) {
            energyCompany.deleteCustAccountInformation(liteAcctInfo);
        }

        return returnValue; // required
    }

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
        energyCompany.getCustAccountInformation(accountId, true);

        // Clear inventory cache entry
        energyCompany.reloadInventory(thermostatId);

    }

    @After("bean(thermostatScheduleDao) && saveMethodNamePointCut() && args(schedule)")
    public void doThermostatScheduleAction(ThermostatSchedule schedule)
            throws Throwable {

        Integer accountId = schedule.getAccountId();
        LiteStarsEnergyCompany energyCompany = mappingDao.getCustomerAccountEC(accountId);

        // Clear and reload account info cache entry
        LiteStarsCustAccountInformation custAccountInformation = energyCompany.getCustAccountInformation(accountId,
                                                                                                         false);
        energyCompany.deleteCustAccountInformation(custAccountInformation);
        energyCompany.getCustAccountInformation(accountId, true);

        // Clear inventory cache entry
        Integer inventoryId = schedule.getInventoryId();
        if (inventoryId != 0) {
            energyCompany.reloadInventory(inventoryId);
        }

    }
    
    @After("bean(inventoryDao) && saveMethodNamePointCut() && args(thermostat)")
	public void doThermostatSaveAction(Thermostat thermostat) throws Throwable {

		Integer inventoryId = thermostat.getId();
		LiteStarsEnergyCompany energyCompany = mappingDao
				.getInventoryEC(inventoryId);

		// Clear inventory cache entry
		if (inventoryId != 0) {
			energyCompany.reloadInventory(inventoryId);
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
