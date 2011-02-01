package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.LinkedList;

import org.jdom.Element;
import org.jdom.Namespace;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.OptOutHelper;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.yukon.api.loadManagement.endpoint.endpointmappers.OptOutRequestElementMapper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;


@Endpoint
public class OptOutRequestEndpoint {
    private AccountEventLogService accountEventLogService;
    private OptOutService optOutService;
    private CustomerAccountDao customerAccountDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private RolePropertyDao rolePropertyDao;
    private OptOutEventDao optOutEventDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private AuthDao authDao;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="optOutRequest")
    public Element invoke(Element optOutRequest, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(optOutRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(optOutRequest);
        OptOutHelper optOutHelper = template.evaluateAsObject("//y:optOutRequest", 
                                                              new NodeToElementMapperWrapper<OptOutHelper>(new OptOutRequestElementMapper()));

        Element resp = new Element("optOutResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        //Check if user is authorized to schedule OptOuts
        try{
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user); 
        }
        catch (NotAuthorizedException e){
            Element fe = XMLFailureGenerator.generateFailure(optOutRequest, null,  
                                                             "NoOptOutAuth", "User is not authorized to schedule opt outs");
            resp.addContent(fe);
            return resp;
        }
        
        LinkedList<Element> errorList = new LinkedList<Element>();
        
        LocalDate localStartDate = new LocalDate(optOutHelper.getStartDate()); 
        DateTimeZone userJodaTimeZone = DateTimeZone.forTimeZone(authDao.getUserTimeZone(user));
        LocalDate localUserDate = new LocalDate(userJodaTimeZone); 
           
        boolean isToday = false;
        if(localStartDate.getDayOfYear() == localUserDate.getDayOfYear() &&
                localStartDate.getYear() == localUserDate.getYear()){
            isToday = true;
        }
        
        Element failure = null;
        
        //Validate start date
        if(isToday){
            optOutHelper.setStartDate(null);
        }
        else if(localStartDate.isBefore(localUserDate)){
            failure = XMLFailureGenerator.generateFailure(optOutRequest, null,  
                                                             "InvalidStartDate", "Start date can't be set to a passed date");
        }
        else if(rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_OPT_OUT_TODAY_ONLY, user)){
            failure = XMLFailureGenerator.generateFailure(optOutRequest, null,  
                                                             "NoFutureOptOutSchedulingAuth",
                                                             "User is not authorized to schedule opt outs on future days");
        }
        
        if(failure!=null){
            resp.addContent(failure);
            return resp;
        }
            
        //Validate duration 
        boolean isValidDuration = false;
        String validDurations = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_OPT_OUT_PERIOD, user);
        if(validDurations.equals("")){
             isValidDuration = optOutHelper.getDurationInDays() == 1;
        }
        else{
            String[] validDurationStrings = validDurations.split(",");
            for(String validDurationStr: validDurationStrings){
                int validDuration = Integer.parseInt(validDurationStr.trim());
                if(optOutHelper.getDurationInDays()==validDuration){
                    isValidDuration = true;
                    break;
                }
            }
        }
        
        if(!isValidDuration){
            Element fe = XMLFailureGenerator.generateFailure(optOutRequest, null,  
                                                             "IllegalOptOutDuration",
                                                             "The provided OptOut duration is not permitted");
            errorList.add(fe);
        }
        
        CustomerAccount customerAccount = null;
        LMHardwareBase lmHardwareBase = null;
        
        // Handle invalid account number in request
        try{
            customerAccount = customerAccountDao.getByAccountNumber(optOutHelper.getAccountNumber(), user);
        }
        catch(NotFoundException e){
            Element fe = XMLFailureGenerator.generateFailure(optOutRequest, null,  
                                                             "InvalidAccountNumber", "Account with account number "+
                                                             optOutHelper.getAccountNumber()+" doesn't exist");
            errorList.add(fe);
        }

        // Handle invalid serial number in request
        try{
            lmHardwareBase = lmHardwareBaseDao.getBySerialNumber(optOutHelper.getSerialNumber());
        }
        catch(NotFoundException e){
            Element fe = XMLFailureGenerator.generateFailure(optOutRequest, null,  
                                                             "InvalidSerialNumber", "No device found with serial number "+
                                                             optOutHelper.getSerialNumber());
            errorList.add(fe);
        }
       
        if(customerAccount!=null && lmHardwareBase!=null){
            
            // Check if inventory belongs to given account
            LiteInventoryBase inventory = starsInventoryBaseDao.getByInventoryId(lmHardwareBase.getInventoryId());
            if(inventory.getAccountID() != customerAccount.getAccountId()){
                Element fe = XMLFailureGenerator.generateFailure(optOutRequest, null,  
                                                                 "InvalidAccountSerialCombo",
                                                                 "Inventory "+lmHardwareBase.getInventoryId()+ " is not assigned to " +
                                                                 "account "+ customerAccount.getAccountId());
                errorList.add(fe);
            }
            else{ 
                boolean isOptedOut = optOutEventDao.isOptedOut(lmHardwareBase.getInventoryId(), 
                                                               customerAccount.getAccountId());
              
                 // Check if device is already opted out and requested OptOut is today
                if(isOptedOut && isToday){
                    Element fe = XMLFailureGenerator.generateFailure(optOutRequest, null,  
                                                                     "DeviceAlreadyOptedOut",
                                                                     "Device "+optOutHelper.getSerialNumber()+" is currently opted out");
                    errorList.add(fe);
                }
                
                OptOutEvent event = optOutEventDao.getScheduledOptOutEvent(lmHardwareBase.getInventoryId(),     
                                                                           customerAccount.getAccountId());
                //Check if the device is already scheduled for OptOut
                if(!isToday && event != null){
                    Element fe = XMLFailureGenerator.generateFailure(optOutRequest, null,  
                                                                     "DeviceAlreadyScheduledForOptOut",
                                                                     "Device "+optOutHelper.getSerialNumber()+" is already scheduled for an OptOut");
                    errorList.add(fe);
                }
            }
        }
        
        //No errors --> Perform OptOut
        if(errorList.isEmpty()){
            LinkedList<Integer> inventoryIds = new LinkedList<Integer>();
            inventoryIds.add(lmHardwareBase.getInventoryId());

            OptOutRequest request = new OptOutRequest();
            request.setInventoryIdList(inventoryIds);
            if(optOutHelper.getStartDate()!=null){
                request.setStartDate(new Instant(optOutHelper.getStartDate()));
            }
            else{
                request.setStartDate(null);
            }
            
            accountEventLogService.optOutAttemptedThroughApi(user,
                                                             customerAccount.getAccountNumber(),
                                                             lmHardwareBase.getManufacturerSerialNumber(),
                                                             new Instant(optOutHelper.getStartDate()));
            
            request.setDurationInHours(optOutHelper.getDurationInDays() * 24);
            optOutService.optOut(customerAccount, request, user);

            resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        }
        else{ //Errors occurred, send error list back to client
            for(Element fe: errorList){
                resp.addContent(fe);
            }
        }
        
        return resp;
    }
    
   
    

    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
        this.optOutService = optOutService;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    
    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
        this.optOutEventDao = optOutEventDao;
    }

    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }

    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
}
