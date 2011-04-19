package com.cannontech.yukon.api.account.endpoint;

import org.jdom.Element;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.SiteInformation;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.UpdatableAccount;

public class AccountsRequestMapper implements ObjectMapper<Element, UpdatableAccount> {

    @Override
    public UpdatableAccount map(Element accountsRequestElement) throws ObjectMappingException {
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(accountsRequestElement);
        
        UpdatableAccount updatableAccount = new UpdatableAccount();
        AccountDto dto = new AccountDto();
        Address mainAddress = new Address();
        Address billingAddress = new Address();
        SiteInformation siteInfo = new SiteInformation();
        
        updatableAccount.setAccountNumber(template.evaluateAsString("//y:accountNumber"));
        dto.setFirstName(template.evaluateAsString("//y:firstName"));
        dto.setLastName(template.evaluateAsString("//y:lastName"));
        dto.setCompanyName(template.evaluateAsString("//y:companyName"));
        dto.setIsCommercial(template.evaluateAsBoolean("//y:isCommercial") == null ? false : template.evaluateAsBoolean("//y:isCommercial")); // Default to residential.
        dto.setHomePhone(template.evaluateAsString("//y:homePhone"));
        dto.setWorkPhone(template.evaluateAsString("//y:workPhone"));
        dto.setEmailAddress(template.evaluateAsString("//y:email"));
        dto.setAltTrackingNumber(template.evaluateAsString("//y:alternateTrackingNumber"));
        dto.setMapNumber(template.evaluateAsString("//y:mapNumber"));
        dto.setUserName(template.evaluateAsString("//y:accountUser/y:username"));
        dto.setPassword(template.evaluateAsString("//y:accountUser/y:password"));
        dto.setLoginGroup(template.evaluateAsString("//y:accountUser/y:loginGroupName"));
        
        mainAddress.setLocationAddress1(template.evaluateAsString("//y:mainAddress/y:street"));
        mainAddress.setLocationAddress2(template.evaluateAsString("//y:mainAddress/y:street2"));
        mainAddress.setCityName(template.evaluateAsString("//y:mainAddress/y:city"));
        mainAddress.setStateCode(template.evaluateAsString("//y:mainAddress/y:state"));
        mainAddress.setZipCode(template.evaluateAsString("//y:mainAddress/y:postalCode"));
        mainAddress.setCounty(template.evaluateAsString("//y:mainAddress/y:county"));
        
        billingAddress.setLocationAddress1(template.evaluateAsString("//y:billingAddress/y:street"));
        billingAddress.setLocationAddress2(template.evaluateAsString("//y:billingAddress/y:street2"));
        billingAddress.setCityName(template.evaluateAsString("//y:billingAddress/y:city"));
        billingAddress.setStateCode(template.evaluateAsString("//y:billingAddress/y:state"));
        billingAddress.setZipCode(template.evaluateAsString("//y:billingAddress/y:postalCode"));
        billingAddress.setCounty(template.evaluateAsString("//y:billingAddress/y:county"));
        
        siteInfo.setSubstationName(template.evaluateAsString("//y:siteInformation/y:substationName"));
        siteInfo.setFeeder(template.evaluateAsString("//y:siteInformation/y:feeder"));
        siteInfo.setPole(template.evaluateAsString("//y:siteInformation/y:pole"));
        siteInfo.setTransformerSize(template.evaluateAsString("//y:siteInformation/y:transformerSize"));    
        siteInfo.setServiceVoltage(template.evaluateAsString("//y:siteInformation/y:siteVoltage"));
        
        dto.setBillingAddress(billingAddress);
        dto.setStreetAddress(mainAddress);
        dto.setSiteInfo(siteInfo);
        updatableAccount.setAccountDto(dto);
        
        return updatableAccount;

    }
}
