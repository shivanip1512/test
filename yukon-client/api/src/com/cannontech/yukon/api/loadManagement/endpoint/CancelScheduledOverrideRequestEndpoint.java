package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.CancelOptOutHelper;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.exception.CancelOptOutException;
import com.cannontech.stars.dr.optout.exception.OptOutException;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutEventState;
import com.cannontech.yukon.api.loadManagement.endpoint.endpointmappers.CancelOverrideRequestMapper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class CancelScheduledOverrideRequestEndpoint extends OverrideRequestEndpointBase{

    private OptOutEventDao optOutEventDao;

    private Namespace ns = YukonXml.getYukonNamespace();

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "cancelScheduledOverrideRequest")
    public Element invoke(Element newOptOutCancelRequest, LiteYukonUser user) throws Exception {

        XmlVersionUtils.verifyYukonMessageVersion(newOptOutCancelRequest,
                                                  XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(newOptOutCancelRequest);
        CancelOptOutHelper cancelOptOutHelper = template.evaluateAsObject("//y:cancelScheduledOverrideRequest", 
                                                              new NodeToElementMapperWrapper<CancelOptOutHelper>(new CancelOverrideRequestMapper()));

        Element resp = new Element("cancelScheduledOverrideResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        Element fe = null;
        
        CustomerAccount customerAccount = null;
        LMHardwareBase lmHardwareBase = null;
        
        try {
            accountEventLogService.scheduledOptOutCancelAttemptedThroughApi(user, cancelOptOutHelper.getAccountNumber(), 
                                                                            cancelOptOutHelper.getSerialNumber());
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
           
            customerAccount = getCustomerAccount(cancelOptOutHelper.getAccountNumber(), user);
            lmHardwareBase = getLMHardwareBase(cancelOptOutHelper.getSerialNumber());
            
            List<OptOutEventDto> events =
                optOutEventDao.getCurrentOptOuts(customerAccount.getAccountId(), lmHardwareBase.getInventoryId());
            
            LinkedList<Integer> eventIdList = new LinkedList<Integer>();
            
            boolean scheduledFound = false;
            
            for (OptOutEventDto event: events) {
                if (event.getState() == OptOutEventState.SCHEDULED) {
                    scheduledFound = true;
                    eventIdList.add(event.getEventId());
                }
            }
            
            if (scheduledFound) {
                optOutService.cancelOptOut(eventIdList, user);
                resp.addContent(XmlUtils.createStringElement("success", ns, ""));
            } else {
                throw new CancelOptOutException("No scheduled opt outs found for device " + lmHardwareBase.getManufacturerSerialNumber());
            }
         
        } catch (NotAuthorizedException e) {
            fe = XMLFailureGenerator.generateFailure(newOptOutCancelRequest, e, "UserNotAuthorized", e.getMessage());
        } catch (OptOutException e) {
            fe = XMLFailureGenerator.generateFailure(newOptOutCancelRequest, e, e.getErrorCode(), e.getMessage());
        } catch (AccountNotFoundException e){
            fe = XMLFailureGenerator.generateFailure(newOptOutCancelRequest, e, "InvalidAccountNumber", e.getMessage());
        } catch (InventoryNotFoundException e){
            fe = XMLFailureGenerator.generateFailure(newOptOutCancelRequest, e, "InvalidSerialNumber", e.getMessage());
        } finally {
            if (fe!=null) {
                resp.addContent(fe);
            }
        }
        
        return resp;
    }
    
    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
        this.optOutEventDao = optOutEventDao;
    }
}
