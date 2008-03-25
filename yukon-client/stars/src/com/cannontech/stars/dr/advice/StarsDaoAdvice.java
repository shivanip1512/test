package com.cannontech.stars.dr.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;

@Aspect
public class StarsDaoAdvice {
    private ECMappingDao mappingDao;

    @Pointcut("execution(* *.update(..)) || execution(* *.remove(..))")
    public void methodNamePointCut() {}
    
    @Around("bean(customerAccountDao) && methodNamePointCut() && args(account)")
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

    @Autowired
    public void setMappingDao(ECMappingDao mappingDao) {
        this.mappingDao = mappingDao;
    }
    
}
