package com.cannontech.web.stars.dr.consumer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResult;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentRequest;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableEnrollment;

@Controller
@RequestMapping("/consumer/enrollment")
public class EnrollmentController extends AbstractConsumerController {
    private static final String KEY_PROGRAMID = "programId";
    private static final String KEY_INVENTORYID = "inventoryId";
    private static final String KEY_APPLIANCECATEGORYID = "applianceCategoryId";
    private static final String KEY_ENROLL = "enroll";
    private DisplayableEnrollmentDao displayableEnrollmentDao;
    private AuthDao authDao;
    
    @RequestMapping(method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        
        List<DisplayableEnrollment> enrollments = 
            displayableEnrollmentDao.getDisplayableEnrollments(customerAccount, yukonUserContext);
        map.addAttribute("enrollments", enrollments);
        
        boolean disableCheckBox = authDao.checkRoleProperty(yukonUserContext.getYukonUser(), ResidentialCustomerRole.DISABLE_PROGRAM_SIGNUP);
        map.addAttribute("disableCheckBox", disableCheckBox);
        
        return "consumer/enrollment/enrollment.jsp";
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
    public String update(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            String json, HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        final LiteYukonUser user = yukonUserContext.getYukonUser();
        final HttpSession session = request.getSession(false);
        
        JSONObject jsonObj = new JSONObject(json);
        List<ProgramEnrollmentRequest> requestList = new ArrayList<ProgramEnrollmentRequest>();
        
        Iterator<String> iterator = jsonObj.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject innerJSONObject = jsonObj.getJSONObject(key);

            Iterator<String> iterator2 = innerJSONObject.keys();
            while (iterator2.hasNext()) {
                String key2 = iterator2.next();
                JSONObject value = innerJSONObject.getJSONObject(key2);
                int programId = value.getInt(KEY_PROGRAMID);
                int inventoryId = value.getInt(KEY_INVENTORYID);
                int applianceCategoryId = value.getInt(KEY_APPLIANCECATEGORYID);
                boolean enroll = value.getBoolean(KEY_ENROLL);

                ProgramEnrollmentRequest enrollmentRequest = new ProgramEnrollmentRequest();
                enrollmentRequest.setProgramId(programId);
                enrollmentRequest.setInventoryId(inventoryId);
                enrollmentRequest.setApplianceCategoryId(applianceCategoryId);
                enrollmentRequest.setEnroll(enroll);
            
                /* ProgramSignUpAction only cares about programs you are enrolling into,
                   this will be changing. */
                if (enroll) {
                    requestList.add(enrollmentRequest);
                }    
            }
        }
        
        ProgramEnrollmentResult enrollmentResult = 
            programEnrollmentService.applyEnrollmentRequests(customerAccount, requestList, yukonUserContext);
        map.addAttribute("enrollmentResult", enrollmentResult);
        
        
        EventUtils.logSTARSEvent(user.getUserID(),
                                 EventUtils.EVENT_CATEGORY_ACCOUNT,
                                 YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED,
                                 customerAccount.getAccountId(),
                                 session);
        
        return "consumer/enrollment/enrollmentResult.jsp";
    }
    
    @Autowired
    public void setDisplayableEnrollmentDao(
            DisplayableEnrollmentDao displayableEnrollmentDao) {
        this.displayableEnrollmentDao = displayableEnrollmentDao;
    }
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
}
