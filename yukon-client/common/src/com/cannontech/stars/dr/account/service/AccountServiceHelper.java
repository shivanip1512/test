package com.cannontech.stars.dr.account.service;

import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.google.common.collect.ImmutableList;

public class AccountServiceHelper {
    
    private AccountService accountService;
    
    /**
     * This method takes a dto with nulls and update data;  
     * Retrieves a dto from the db and copies passed dto values 
     * into retrieved dto.  Returns resulting dto.
     * @param workingAccount
     * @return UpdatableAccount
     */
    public UpdatableAccount buildFullDto(UpdatableAccount workingAccount, LiteYukonUser user) {
        AccountDto retrievedDto = accountService.getAccountDto(workingAccount.getAccountNumber(), user);
        copyNonNullValues(workingAccount, retrievedDto);
        UpdatableAccount result = new UpdatableAccount();
        result.setAccountNumber(workingAccount.getAccountNumber());
        result.setAccountDto(retrievedDto);
        return result;
    }

    public void copyNonNullValues(UpdatableAccount from, AccountDto to) {
        BeanWrapper fromDtoAccessor = PropertyAccessorFactory.forBeanPropertyAccess(from.getAccountDto());
        BeanWrapper toDtoAccessor = PropertyAccessorFactory.forBeanPropertyAccess(to);
        
        List<String> accountPropertyList = ImmutableList.of(
            "lastName",
            "firstName",
            "companyName",
            "isCommercial",
            "homePhone",
            "workPhone",
            "emailAddress",
            "streetAddress.locationAddress1",
            "streetAddress.locationAddress2",
            "streetAddress.cityName",
            "streetAddress.stateCode",
            "streetAddress.zipCode",
            "streetAddress.county",
            "billingAddress.locationAddress1",
            "billingAddress.locationAddress2",
            "billingAddress.cityName",
            "billingAddress.stateCode",
            "billingAddress.zipCode",
            "billingAddress.county",
            "userName",
            "password",
            "userGroup",
            "altTrackingNumber",
            "mapNumber",
            "homePhone",
            "siteInfo.feeder",
            "siteInfo.pole",
            "siteInfo.transformerSize",
            "siteInfo.serviceVoltage",
            "siteInfo.substationName");
        
        for(String property : accountPropertyList) {
            
            Object propertyValue = fromDtoAccessor.getPropertyValue(property);
            if(propertyValue != null) {
                toDtoAccessor.setPropertyValue(property, propertyValue);
            }
        }
        from.setAccountDto(to);
    }
    
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    
}
