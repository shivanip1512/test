package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Collections;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.exception.OptOutException;
import com.cannontech.stars.dr.optout.model.OptOutHelper;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.yukon.api.loadManagement.endpoint.endpointmappers.OverrideRequestElementMapper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class OverrideRequestEndpoint extends OverrideRequestEndpointBase{

    private Namespace ns = YukonXml.getYukonNamespace();

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "overrideRequest")
    public Element invoke(Element optOutRequest, LiteYukonUser user) throws Exception {

        XmlVersionUtils.verifyYukonMessageVersion(optOutRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(optOutRequest);
        OptOutHelper optOutHelper = template.evaluateAsObject("//y:overrideRequest", 
                                                              new NodeToElementMapperWrapper<OptOutHelper>(new OverrideRequestElementMapper()));

        Element resp = new Element("overrideResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        Element fe = null;
        try {
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
            
            CustomerAccount customerAccount = getCustomerAccount(optOutHelper.getAccountNumber(), user);
            LMHardwareBase lmHardwareBase = getLMHardwareBase(optOutHelper.getSerialNumber());
            
            accountEventLogService.optOutAttemptedThroughApi(user, customerAccount.getAccountNumber(),
                                                             lmHardwareBase.getManufacturerSerialNumber(),
                                                             optOutHelper.getStartDate());
            
            OptOutRequest request = new OptOutRequest();
            List<Integer>inventoryIds = Collections.singletonList(lmHardwareBase.getInventoryId());
            request.setInventoryIdList(inventoryIds);  
            request.setDurationInHours(optOutHelper.getDuration().toPeriod().toStandardHours().getHours());
          
            if (optOutHelper.getStartDate() != null) {
                request.setStartDate(optOutHelper.getStartDate());
            }
           
            optOutService.optOutWithValidation(customerAccount, request, user, optOutHelper.getOptOutCounts());
            
            resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        } catch (OptOutException e) {
            fe = XMLFailureGenerator.generateFailure(optOutRequest,e, e.getErrorCode(), e.getMessage());
        } catch(NotAuthorizedException e) {
            fe = XMLFailureGenerator.generateFailure(optOutRequest, e, "UserNotAuthorized", "Insufficent privileges to perform this operation.");
        } catch (AccountNotFoundException e){
            fe = XMLFailureGenerator.generateFailure(optOutRequest, e, "InvalidAccountNumber", e.getMessage());
        } catch (InventoryNotFoundException e){
            fe = XMLFailureGenerator.generateFailure(optOutRequest, e, "InvalidSerialNumber", e.getMessage());
        } finally {
            if (fe != null) {
                resp.addContent(fe);
            }
        }

        return resp;  
    }
}
