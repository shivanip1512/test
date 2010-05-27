package com.cannontech.web.multispeak;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;
import com.cannontech.web.common.flashScope.FlashScope;

@Controller
@RequestMapping("/setup/*")
public class MultispeakController extends MultiActionController implements InitializingBean {
    
    private MultispeakDao multispeakDao;
    private MultispeakFuncs multispeakFuncs;
    private MspObjectDao mspObjectDao;
    private RoleDao roleDao;
    private MspMeterSearchService mspMeterSearchService;
    
    private MultispeakVendor defaultMspVendor;
    
    // HOME
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception {
       
        ModelAndView mav = new ModelAndView("setup/msp_setup.jsp");
        
        addSystemModelAndViewObjects(request, mav, defaultMspVendor);
        
        return mav;
    }

    // SAVE
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("setup/msp_setup.jsp");
        
        MultispeakVendor mspVendor = buildMspVendor(request);
        
        //Validate the request parameters before continuing on.
        boolean isValid = isValidMspRequest(request);
        if (isValid) {
            try {
                boolean isCreateNew = ServletRequestUtils.getBooleanParameter(request, "isCreateNew", false);
                addOrUpdateMspVendor(request, mspVendor, isCreateNew);
            } catch (DuplicateException e) {
                FlashScope flashScope = new FlashScope(request);
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.exception",e.getMessage()));
            }
    
        } else {
            boolean isCreateNew = ServletRequestUtils.getBooleanParameter(request, "isCreateNew", false);
            mav.addObject("isCreateNew", isCreateNew);
        }
        
        addSystemModelAndViewObjects(request, mav, mspVendor);
        return mav;
    }
    
    // CREATE
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("setup/msp_setup.jsp");
        
        MultispeakVendor newMspVendor = new MultispeakVendor();
        addSystemModelAndViewObjects(request, mav, newMspVendor);
        mav.addObject("isCreateNew", true);
        
        return mav;
    }
    
    // DELETE
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("redirect:/spring/multispeak/setup/home");
        Integer vendorId = ServletRequestUtils.getIntParameter(request, "mspVendorId", defaultMspVendor.getVendorID());

        if( defaultMspVendor.getVendorID().intValue() == vendorId.intValue()) {
            FlashScope flashScope = new FlashScope(request);
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.deleteDefaultMessage",defaultMspVendor.getCompanyName()));
        }else {
            MultispeakVendor deletedMspVendor = multispeakDao.getMultispeakVendor(vendorId);
            if( multispeakFuncs.isPrimaryCIS(deletedMspVendor)){
                FlashScope flashScope = new FlashScope(request);
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.deletePrimaryMessage", deletedMspVendor.getCompanyName()));
            } else {
                multispeakDao.deleteMultispeakVendor(vendorId);
                FlashScope flashScope = new FlashScope(request);
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.deleted", deletedMspVendor.getCompanyName()));
            }
        }
        return mav;
    }
    
    // CHANGEVENDOR
    public ModelAndView changeVendor(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("setup/msp_setup.jsp");
        Integer vendorId = ServletRequestUtils.getIntParameter(request, "mspVendorId", defaultMspVendor.getVendorID());
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
        addSystemModelAndViewObjects(request, mav, mspVendor);
        
        return mav;

    }
    
    // PINGURL
    public ModelAndView pingURL(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("setup/msp_setup.jsp");
        Integer vendorId = ServletRequestUtils.getIntParameter(request, "mspVendorId", defaultMspVendor.getVendorID());
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
        
        String mspService = ServletRequestUtils.getStringParameter(request, "actionService");
        if( mspService != null) {
            try {
                ErrorObject[] objects = mspObjectDao.pingURL(mspVendor, mspService);
                if( objects != null && objects != null  && objects.length > 0){
                    String result = "";
                    for (int i = 0; i < objects.length; i++) {
                        result += objects[i].getObjectID() + " - " + objects[i].getErrorString();
                    }
                    mav.addObject(MultispeakDefines.MSP_RESULT_MSG, result);
                    mav.addObject("resultColor", "red");
                }
                else {
                    mav.addObject( MultispeakDefines.MSP_RESULT_MSG, "* " + mspService + " pingURL Successful");
                    mav.addObject("resultColor", "blue");
                }
            }catch (RemoteException re) {
                mav.addObject( MultispeakDefines.MSP_RESULT_MSG, re.getMessage());
                mav.addObject("resultColor", "red");
            }
        }
        addSystemModelAndViewObjects(request, mav, mspVendor);
        return mav;
    }

    // GETMETHODS
    public ModelAndView getMethods(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("setup/msp_setup.jsp");
        Integer vendorId = ServletRequestUtils.getIntParameter(request, "mspVendorId", defaultMspVendor.getVendorID());
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);

        String mspService = ServletRequestUtils.getStringParameter(request, "actionService");
        if( mspService != null) {
            try {
                String[] objects = mspObjectDao.getMethods(mspVendor, mspService);
                if( objects != null && objects != null)
                {
                    String resultStr = mspService + " available methods:\n";
                    if( objects.length > 0)
                    {
                        resultStr += " * " + objects[0] + "\n";
                        for (int i = 1; i < objects.length; i++)
                            resultStr += " * " + objects[i] + "\n";
                    }
                    mav.addObject(MultispeakDefines.MSP_RESULT_MSG, resultStr);
                    mav.addObject("resultColor", "blue");
                }
                else
                {
                    mav.addObject(MultispeakDefines.MSP_RESULT_MSG, "* No methods reported for " + mspService +" getMethods:\n" + mspService + " is not supported.");
                    mav.addObject("resultColor", "red");
                }
            }catch (RemoteException re) {
                mav.addObject( MultispeakDefines.MSP_RESULT_MSG, re.getMessage());
                mav.addObject("resultColor", "red");
            }
        }
        
        // If we called getMethods on the primary CIS vendor, we should reload the search fields
        if( multispeakFuncs.isPrimaryCIS(mspVendor)) {
            mspMeterSearchService.loadMspSearchFields();            
        }
        
        addSystemModelAndViewObjects(request, mav, mspVendor);
        return mav;
    }
    
    /**
     * Helper method to build a MultiSpeakVendor object from the request parameters.
     * @param request
     * @return
     * @throws Exception
     */
    private MultispeakVendor buildMspVendor(HttpServletRequest request) throws Exception{
        Integer vendorId = ServletRequestUtils.getIntParameter(request, "mspVendorId");
        String companyName = ServletRequestUtils.getStringParameter(request, "mspCompanyName");
        String appName = ServletRequestUtils.getStringParameter(request, "mspAppName");
        String password = ServletRequestUtils.getStringParameter(request, "mspPassword");
        String username = ServletRequestUtils.getStringParameter(request, "mspUserName");

        int maxReturnRecords = ServletRequestUtils.getIntParameter(request, "mspMaxReturnRecords", MultispeakDefines.MSP_MAX_RETURN_RECORDS);
        long requestMessageTimeout = ServletRequestUtils.getLongParameter(request, "mspRequestMessageTimeout", MultispeakDefines.MSP_REQUEST_MESSAGE_TIMEOUT);
        long maxInitiateRequestObjects = ServletRequestUtils.getLongParameter(request, "mspMaxInitiateRequestObjects", MultispeakDefines.MSP_MAX_INITIATE_REQUEST_OBJECTS);

        String templateNameDefault = ServletRequestUtils.getStringParameter(request, "mspTemplateNameDefault", MultispeakDefines.MSP_TEMPLATE_NAME_DEFAULT);

        String outPassword = ServletRequestUtils.getStringParameter(request, "outPassword");
        String outUsername = ServletRequestUtils.getStringParameter(request, "outUserName");
        String[] mspInterfaces = ServletRequestUtils.getStringParameters(request, "mspInterface");
        String[] mspEndpoints = ServletRequestUtils.getStringParameters(request, "mspEndpoint");
        String mspURL = ServletRequestUtils.getStringParameter(request, "mspURL", "");
        if( !mspURL.endsWith("/")) {
            mspURL += "/";
        }
        
        MultispeakVendor mspVendor = new MultispeakVendor(vendorId,companyName, appName, 
                                                          username, password, outUsername, outPassword, 
                                                          maxReturnRecords, requestMessageTimeout,
                                                          maxInitiateRequestObjects, templateNameDefault, 
                                                          mspURL);
        
        List<MultispeakInterface> mspInterfaceList = new ArrayList<MultispeakInterface>();
        if( mspInterfaces != null && mspEndpoints != null) {
            for (int i = 0; i < mspInterfaces.length; i++ )
            {
                MultispeakInterface mspInterface = new MultispeakInterface(vendorId, mspInterfaces[i], mspEndpoints[i]);
                mspInterfaceList.add(mspInterface);
            }
        }
        mspVendor.setMspInterfaces(mspInterfaceList);
        
        return mspVendor;
    }
    
    /**
     * Validates that the request object has data in required fields.
     * Returns true if the request is contains no errors (has data in all necessary fields).
     * Returns false if the request has one or more error messages.
     * @param mspVendor
     * @return boolean
     */
    private boolean isValidMspRequest(HttpServletRequest request) {

        FlashScope flashScope = new FlashScope(request);
        List<MessageSourceResolvable> messages = new ArrayList<MessageSourceResolvable>();

        String param = request.getParameter("mspCompanyName");
        if( StringUtils.isBlank(param) ) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.invalidCompanyName"));
        }
        
        param = request.getParameter("mspMaxInitiateRequestObjects");
        if( StringUtils.isBlank(param) || !StringUtils.isNumeric(param)) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.invalidMaxInitiateRequestObjects"));
        }
        
        param = request.getParameter("mspRequestMessageTimeout");
        if( StringUtils.isBlank(param) || !StringUtils.isNumeric(param)) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.invalidRequestMessageTimeout"));
        }

        
        param = request.getParameter("mspMaxReturnRecords");
        if( StringUtils.isBlank(param) || !StringUtils.isNumeric(param)) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.invalidMaxReturnRecords"));
        }
        
        param = request.getParameter("mspTemplateNameDefault");
        if( StringUtils.isBlank(param)) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.invalidTemplateNameDefault"));
        }

        param = request.getParameter("mspURL");
        try {
            new URL(param);
        } catch (MalformedURLException e) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.invalidURL"));
        }
        flashScope.setError(messages);
        return (messages.size() == 0);
    }
    
    /**
     * Adds or updates the mspVendor object.
     * If mspVendor is the default system vendor, then the role property values will also be updated.
     * @param request - the http request
     * @param mspVendor - the vendor to add or update
     * @param add - when true, a mspVendor will be created, else mspVendor will be updated. 
     * @throws Exception
     * @throws DuplicateException
     */
    private void addOrUpdateMspVendor(HttpServletRequest request, MultispeakVendor mspVendor, boolean add) throws Exception, DuplicateException {

        if (add) {
            multispeakDao.addMultispeakVendor(mspVendor);
            FlashScope flashScope = new FlashScope(request);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.added", mspVendor.getCompanyName()));
        } else {
            multispeakDao.updateMultispeakVendor(mspVendor);
            FlashScope flashScope = new FlashScope(request);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.mspSetup.updated", mspVendor.getCompanyName()));

            if(defaultMspVendor.getVendorID().intValue() == mspVendor.getVendorID().intValue()) {
                updateRolePropertyValues(request);
            }
        }   
    }
    
    /**
     * Updates the Yukon Grp MultiSpeak role property values (primaryCIS, paoNameAlias, and meterLookupField).
     * @param request - the http request
     * @throws Exception
     */
    private void updateRolePropertyValues(HttpServletRequest request) throws Exception {

        int oldMspPrimaryCIS = multispeakFuncs.getPrimaryCIS();
        int mspPrimaryCIS = ServletRequestUtils.getIntParameter(request, "mspPrimaryCIS", oldMspPrimaryCIS);
        
        int oldMspPaoNameAlias = multispeakFuncs.getPaoNameAlias();
        int mspPaoNameAlias = ServletRequestUtils.getIntParameter(request, "mspPaoNameAlias", oldMspPaoNameAlias);
        
        
        MultispeakMeterLookupFieldEnum oldMspMeterLookupField = multispeakFuncs.getMeterLookupField();
        MultispeakMeterLookupFieldEnum mspMeterLookupField = oldMspMeterLookupField;
        String param = request.getParameter("mspMeterLookupField");
        if (param != null) {
            mspMeterLookupField = MultispeakMeterLookupFieldEnum.valueOf(param);
        }
        
        try {
            LiteYukonGroup liteYukonGroup = roleDao.getGroup( YukonGroupRoleDefs.GRP_YUKON );
            
            // update Primary CIS Vendor
            if (oldMspPrimaryCIS != mspPrimaryCIS) {
                roleDao.updateGroupRoleProperty(liteYukonGroup, 
                                                YukonRole.MULTISPEAK.getRoleId(),
                                                YukonRoleProperty.MSP_PRIMARY_CB_VENDORID.getPropertyId(),
                                                String.valueOf(mspPrimaryCIS));
                
                mspMeterSearchService.loadMspSearchFields();
            }
            if (oldMspPaoNameAlias != mspPaoNameAlias) {
                // update PaoName Alias
                roleDao.updateGroupRoleProperty(liteYukonGroup, 
                                                YukonRole.MULTISPEAK.getRoleId(),
                                                YukonRoleProperty.MSP_PAONAME_ALIAS.getPropertyId(),
                                                String.valueOf(mspPaoNameAlias));
            }
            if ( oldMspMeterLookupField != mspMeterLookupField) {
                // update Meter Lookup Field
                roleDao.updateGroupRoleProperty(liteYukonGroup, 
                                                YukonRole.MULTISPEAK.getRoleId(),
                                                YukonRoleProperty.MSP_METER_LOOKUP_FIELD.getPropertyId(),
                                                String.valueOf(mspMeterLookupField));
            }
        } catch (Exception e) {
            CTILogger.error( "Role Properties for MultiSpeak Setup not saved", e );
        }

    }
    
    /**
     * Helper method to add the common model and view objects for this controller.
     * @param request - the http request
     * @param mav - the modelAndView to add objects to
     * @param mspVendor - the mspVendor to add
     * @return
     */
    private ModelAndView addSystemModelAndViewObjects(HttpServletRequest request, ModelAndView mav, MultispeakVendor mspVendor) {
        
        mav.addObject("mspVendor", mspVendor);
        mav.addObject("mspVendorList", multispeakDao.getMultispeakVendors());        
        mav.addObject("mspCISVendorList", multispeakDao.getMultispeakCISVendors());
        mav.addObject("possibleInterfaces", MultispeakDefines.getPossibleInterfaces(mspVendor));
        
        boolean showRoleProperties = (defaultMspVendor.getCompanyName().equals(mspVendor.getCompanyName()));
        mav.addObject("showRoleProperties", showRoleProperties);
        
        //  Try to get the values from the request first, then get from the system.
        //  If these values were just updated, the db change may not have been received/processed yet and 
        //    the values returned from multispeakFuncs may be outdated.
        mav.addObject("primaryCIS", ServletRequestUtils.getIntParameter(request, "mspPrimaryCIS", multispeakFuncs.getPrimaryCIS()));
        mav.addObject("paoNameAlias", ServletRequestUtils.getIntParameter(request, "mspPaoNameAlias", multispeakFuncs.getPaoNameAlias()));
        String meterLookupField = request.getParameter("mspMeterLookupField");
        mav.addObject("meterLookupField", meterLookupField != null ? 
                                            MultispeakMeterLookupFieldEnum.valueOf(meterLookupField) : multispeakFuncs.getMeterLookupField());

        return mav;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        defaultMspVendor = multispeakDao.getMultispeakVendor("Cannon", "");
    }
    
    @Autowired
    public void setMultispeakDao(MultispeakDao multispeakDao) {
        this.multispeakDao = multispeakDao;
    }
    
    @Autowired
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    
    @Autowired
    public void setMspObjectDao(MspObjectDao mspObjectDao) {
        this.mspObjectDao = mspObjectDao;
    }
    
    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    
    @Autowired
    public void setMspMeterSearchService(
            MspMeterSearchService mspMeterSearchService) {
        this.mspMeterSearchService = mspMeterSearchService;
    }
}
