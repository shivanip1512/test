package com.cannontech.yukon.api.consumer.endpoint;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Endpoint
public class HardwareSummaryListRequestEndpoint {

    @Autowired private InventoryDao inventoryDao;
    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(HardwareSummaryListRequestEndpoint.class);
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="hardwareSummaryListRequest")
    public Element invoke(Element hardwareSummaryListRequest, CustomerAccount customerAccount) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(hardwareSummaryListRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(hardwareSummaryListRequest);
        
        //build response
        Element response = new Element("hardwareSummaryListResponse", ns);
        XmlVersionUtils.addVersionAttribute(response, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        try{
            //get the hardware summary for the specified account
            List<String> requestedHardwareTypes = requestTemplate.evaluateAsStringList("//y:hardwareClass");
            List<HardwareSummary> hardwareSummaryList = Lists.newArrayList();
            if(requestedHardwareTypes.isEmpty()){
                //get all the hardware
                hardwareSummaryList = inventoryDao.getAllHardwareSummaryForAccount(customerAccount.getAccountId());
            }else{
                //gather up the types
                Set<HardwareType> hardwareTypes = Sets.newHashSet();
                for(String requestedHardwareType : requestedHardwareTypes){
                    hardwareTypes.addAll(HardwareType.getForClass(HardwareClass.valueOf(requestedHardwareType)));
                }
                hardwareSummaryList = inventoryDao.getAllHardwareSummaryForAccount(customerAccount.getAccountId(), hardwareTypes);
            }
            
            Element hardwareSummaryListResponse = new Element("hardwareSummaryList", ns);
    
            for(HardwareSummary hardwareSummary : hardwareSummaryList){
                hardwareSummaryListResponse.addContent(addHardwareSummary(hardwareSummary, ns));
            }
            
            response.addContent(hardwareSummaryListResponse);
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(hardwareSummaryListRequest, e, "UserNotAuthorized", "The user is not authorized to retrieve a hardware summary.");
            response.addContent(fe);
            return response;
        }  catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(hardwareSummaryListRequest, e, "OtherException", "An exception has been caught.");
            response.addContent(fe);
            log.error(e.getMessage(), e);
        }
        
        return response;
    }
    
    private Element addHardwareSummary(HardwareSummary hardwareSummary, Namespace ns){
        Element hardwareSummaryElement = new Element("hardwareSummary", ns);
        hardwareSummaryElement.addContent(XmlUtils.createStringElement("serialNumber", ns, hardwareSummary.getSerialNumber()));
        hardwareSummaryElement.addContent(XmlUtils.createStringElement("deviceLabel", ns, hardwareSummary.getDeviceLabel()));
        
        Element inventoryIdentifierElement = new Element("inventoryIdentifier", ns);
        inventoryIdentifierElement.addContent(XmlUtils.createIntegerElement("inventoryId", ns, hardwareSummary.getInventoryId()));
        inventoryIdentifierElement.addContent(XmlUtils.createStringElement("hardwareType", ns, hardwareSummary.getHardwareType().toString()));
        
        //add the inventory element
        hardwareSummaryElement.addContent(inventoryIdentifierElement);
        
        return hardwareSummaryElement;
    }
}
