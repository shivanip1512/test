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
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;

@Aspect
public class StarsDaoAdvice {
    private ECMappingDao mappingDao;
    private CustomerAccountDao customerAccountDao;

    @Pointcut("execution(* *.update(..)) || execution(* *.remove(..))")
    public void updateMethodNamePointCut() {}
    
    @Pointcut("execution(* *.save(..))")
    public void saveMethodNamePointCut() {}
    
    @Around("bean(customerAccountDao) && updateMethodNamePointCut() && args(account)")
    public Object doCustomerAccountAction(ProceedingJoinPoint pjp, CustomerAccount account) throws Throwable {
        // get Energy Company before the mapping is removed by pjp.proceed()
        LiteStarsEnergyCompany energyCompany = mappingDao.getCustomerAccountEC(account);

        Object returnValue = pjp.proceed(); //required

        LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(account.getAccountId(), false);
        if (liteAcctInfo != null) {
            energyCompany.deleteCustAccountInformation(liteAcctInfo);
        }

        return returnValue; //required
    }
    
    @After("bean(manualEventDao) && saveMethodNamePointCut() && args(event)")
    public void doManualEventAction(ThermostatManualEvent event) throws Throwable {

        Integer thermostatId = event.getThermostatId();
        LiteStarsEnergyCompany energyCompany = mappingDao.getInventoryEC(thermostatId);

        // Clear account info cache entry
        CustomerAccount account = customerAccountDao.getAccountByInventoryId(thermostatId);
        int accountId = account.getAccountId();

        energyCompany.deleteStarsCustAccountInformation(accountId);

        // Clear inventory cache entry
        energyCompany.deleteInventory(thermostatId);

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
