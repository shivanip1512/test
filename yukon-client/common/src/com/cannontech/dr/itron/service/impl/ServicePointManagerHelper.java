package com.cannontech.dr.itron.service.impl;

import java.util.Set;

import javax.xml.transform.Source;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.ErrorFault;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AccountType;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AddServicePointRequest;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AddServicePointType;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.LocationType;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.LocationTypeEnumeration;
import com.cannontech.dr.itron.service.impl.ItronCommunicationServiceImpl.Manager;
import com.cannontech.stars.dr.account.model.AccountDto;

public class ServicePointManagerHelper implements SoapFaultParser {
    /*
     * <urn:AddServicePointRequest>
     *   <urn:ServicePoint>
     *      <urn:UtilServicePointID>WS-addSP-1</urn:UtilServicePointID>
     *      <!--Optional:-->
     *      <urn:FeederID>102938</urn:FeederID>
     *      <!--Optional:-->
     *      <urn:SubstationID>-920134</urn:SubstationID>
     *      <!--Optional:-->
     *      <urn:TransformerID>383465</urn:TransformerID>
     *      <!--Optional:-->
     *      <urn:TransmissionID>-73465</urn:TransmissionID>
     *      <!--Optional:-->
     *      <urn:Attribute1>attr-1</urn:Attribute1>
     *      <!--Optional:-->
     *      <urn:Attribute2>attr-2</urn:Attribute2>
     *      <!--Optional:-->
     *      <urn:Attribute3>attr-3</urn:Attribute3>
     *      <!--Optional:-->
     *      <urn:Attribute4>attr-4</urn:Attribute4>
     *      <!--Optional:-->
     *      <urn:Attribute5>attr-5</urn:Attribute5>
     *          <urn:Account>
     *              <urn1:UtilAccountID>WS-addAID-1</urn1:UtilAccountID>
     *              <!--Optional:-->
     *              <urn1:CustomerName>Customer-1</urn1:CustomerName>
     *              <!--Optional:-->
     *              <urn1:Description>customer-1 account</urn1:Description>
     *              <!--Optional:-->
     *              <urn1:Email>customer-1@SP-1.net</urn1:Email>
     *              <!--Optional:-->
     *              <urn1:PhoneNumber>1-222-555-1212</urn1:PhoneNumber>
     *          </urn:Account>
     *          <urn:Location>
     *              <!--Optional:-->
     *              <urn1:Address1>123 bldg. one</urn1:Address1>
     *              <!--Optional:-->
     *              <urn1:Address2>apt-2A</urn1:Address2>
     *              <!--Optional:-->
     *              <urn1:City>city-1</urn1:City>
     *              <!--Optional:-->
     *              <urn1:County>county-1</urn1:County>
     *              <!--Optional:-->
     *              <urn1:State>CA</urn1:State>
     *              <!--Optional:-->
     *              <urn1:PostCode>90001</urn1:PostCode>
     *              <!--Optional:-->
     *              <urn1:CrossStreet>not needed</urn1:CrossStreet>
     *              <!--Optional:-->
     *              <urn1:Country>USA</urn1:Country>
     *              <urn1:LocationType>LOCATION_TYPE_PREMISE</urn1:LocationType>
     *              <urn1:PremiseUtilId>WS-PID-1</urn1:PremiseUtilId>
     *              <!--Optional:-->
     *              <urn1:TZID>PST</urn1:TZID>
     *              <!--Optional:-->
     *              <urn1:Longitude>10000.23</urn1:Longitude>
     *              <!--Optional:-->
     *              <urn1:Latitude>23.00001</urn1:Latitude>
     *          </urn:Location>
     *     </urn:ServicePoint>
     * </urn:AddServicePointRequest>
     */
   public static AddServicePointRequest buildAddRequest(AccountDto accountDto) {
        AddServicePointType servicePoint = new AddServicePointType();
        
        servicePoint.setUtilServicePointID(accountDto.getAccountNumber());
        
        AccountType account = new AccountType();
        account.setUtilAccountID(accountDto.getAccountNumber());
        account.setCustomerName(accountDto.getFirstName() + " " +accountDto.getLastName());
        if(!StringUtils.isEmpty(accountDto.getEmailAddress())) {
            account.setEmail(accountDto.getEmailAddress());
        }
        if(!StringUtils.isEmpty(accountDto.getHomePhone())) {
            account.setPhoneNumber(accountDto.getHomePhone());
        }
        servicePoint.setAccount(account);
        
        LocationType location = new LocationType();
        location.setPremiseUtilId(accountDto.getAltTrackingNumber());
        location.setLocationType(LocationTypeEnumeration.LOCATION_TYPE_PREMISE);
        if(accountDto.getStreetAddress() != null) {
            if(!StringUtils.isEmpty(accountDto.getStreetAddress().getLocationAddress1())) {
                location.setAddress1(accountDto.getStreetAddress().getLocationAddress1());
            }
            if(!StringUtils.isEmpty(accountDto.getStreetAddress().getLocationAddress2())) {
                location.setAddress2(accountDto.getStreetAddress().getLocationAddress2());
            }
            if(!StringUtils.isEmpty(accountDto.getStreetAddress().getCityName())) {
                location.setCity(accountDto.getStreetAddress().getCityName());
            }
            if(!StringUtils.isEmpty(accountDto.getStreetAddress().getZipCode())) {
                location.setTZID(accountDto.getStreetAddress().getZipCode());
            }
            if(!StringUtils.isEmpty(accountDto.getStreetAddress().getStateCode())) {
                location.setState(accountDto.getStreetAddress().getStateCode());
            }
            if(!StringUtils.isEmpty(accountDto.getStreetAddress().getCounty())) {
                location.setCountry(accountDto.getStreetAddress().getCounty());
            }
        }
        servicePoint.setLocation(location);
        AddServicePointRequest request = new AddServicePointRequest();
        request.setServicePoint(servicePoint);
        return request;
    }
   
   @Override
   public void handleSoapFault(SoapFaultClientException e, Set<String> faultCodesToIgnore, Logger log) {
       SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
       soapFaultDetail.getDetailEntries().forEachRemaining(detail -> {
           SoapFaultDetailElement detailElementChild =
               (SoapFaultDetailElement) soapFaultDetail.getDetailEntries().next();
           Source detailSource = detailElementChild.getSource();
           ErrorFault fault = (ErrorFault) Manager.PROGRAM.getMarshaller().unmarshal(detailSource);
           log.debug(XmlUtils.getPrettyXml(fault));
           fault.getErrors().forEach(error -> checkIfErrorShouldBeIgnored(error.getErrorCode(),
               error.getErrorMessage(), faultCodesToIgnore, log));
       });
   }

   @Override
   public boolean isSupported(Manager manager) {
       return Manager.PROGRAM == manager;
   }
}
