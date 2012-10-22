package com.cannontech.web.multispeak;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingUpdateDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ServletRequestEnumUtils;

@RequestMapping("/setup/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP)
public class MultispeakController {
    
    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MspMeterSearchService mspMeterSearchService;
    @Autowired private GlobalSettingUpdateDao globalSettingUpdateDao;
    
    private MultispeakVendor defaultMspVendor;

    private static String RESULT_COLOR_ATT = "resultColor";
    
    // HOME
    @RequestMapping
    public String home(HttpServletRequest request, ModelMap map) throws Exception {

        MultispeakVendor mspVendor = defaultMspVendor;
        
        // Look for "New" button, if found a new MultispeakVendor object will need to be used.
        String newButton = ServletRequestUtils.getStringParameter(request, "New");
        if(newButton != null) {
            mspVendor = new MultispeakVendor();
            map.addAttribute("isCreateNew", true);  //flag for altering form fields for create vs edit/view
        } else {
            
            Integer vendorId = ServletRequestUtils.getIntParameter(request, "mspVendorId", defaultMspVendor.getVendorID());
            if( vendorId != null) {
                mspVendor = multispeakDao.getMultispeakVendor(vendorId);
            }
        }
        addSystemModelAndViewObjects(request, map, mspVendor);
        return "setup/msp_setup.jsp";
    }

    // CANCEL
    @RequestMapping(value = "save", method = RequestMethod.POST, params = "Cancel")
    public String cancel() throws Exception {
        return "redirect:home";
    }

    // SAVE
    @RequestMapping(value = "save", method = RequestMethod.POST, params = "!Cancel")
    public String save(HttpServletRequest request, ModelMap map, FlashScope flashScope) throws Exception {
        
        MultispeakVendor mspVendor = buildMspVendor(request);
        
        //Validate the request parameters before continuing on.
        boolean isValid = isValidMspRequest(request, flashScope);
        if (isValid) {
            try {
                boolean isCreateNew = ServletRequestUtils.getBooleanParameter(request, "isCreateNew", false);
                addOrUpdateMspVendor(request, mspVendor, flashScope, isCreateNew);
            } catch (DuplicateException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.exception",e.getMessage()));
            }
    
        } else {
            //When not valid, return to the setup page so populated form data remains, do not redirect.
            boolean isCreateNew = ServletRequestUtils.getBooleanParameter(request, "isCreateNew", false);
            map.addAttribute("isCreateNew", isCreateNew);
            addSystemModelAndViewObjects(request, map, mspVendor);
            return "setup/msp_setup.jsp";
        }
        
        map.addAttribute("mspVendorId", mspVendor.getVendorID());
        return "redirect:home";
    }
    
    // DELETE
    @RequestMapping
    public String delete(HttpServletRequest request, FlashScope flashScope) throws Exception {
        Integer vendorId = ServletRequestUtils.getIntParameter(request, "mspVendorId", defaultMspVendor.getVendorID());

        if( defaultMspVendor.getVendorID().intValue() == vendorId.intValue()) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.deleteDefaultMessage",defaultMspVendor.getCompanyName()));
        }else {
            MultispeakVendor deletedMspVendor = multispeakDao.getMultispeakVendor(vendorId);
            if( multispeakFuncs.isPrimaryCIS(deletedMspVendor)){
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.deletePrimaryMessage", deletedMspVendor.getCompanyName()));
            } else {
                multispeakDao.deleteMultispeakVendor(vendorId);
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.deleted", deletedMspVendor.getCompanyName()));
            }
        }
        return "redirect:home";
    }

    // PINGURL
    @RequestMapping
    public String pingURL(HttpServletRequest request, ModelMap map) throws Exception {
        
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
                    map.addAttribute(MultispeakDefines.MSP_RESULT_MSG, result);
                    map.addAttribute(RESULT_COLOR_ATT, "red");
                }
                else {
                    map.addAttribute( MultispeakDefines.MSP_RESULT_MSG, "* " + mspService + " pingURL Successful");
                    map.addAttribute(RESULT_COLOR_ATT, "blue");
                }
            }catch (RemoteException re) {
                map.addAttribute( MultispeakDefines.MSP_RESULT_MSG, re.getMessage());
                map.addAttribute(RESULT_COLOR_ATT, "red");
            }
        }

        map.addAttribute("mspVendorId", vendorId);
        return "redirect:home";
    }

    // GETMETHODS
    @RequestMapping
    public String getMethods(HttpServletRequest request, ModelMap map) throws Exception {
        
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
                    map.addAttribute(MultispeakDefines.MSP_RESULT_MSG, resultStr);
                    map.addAttribute(RESULT_COLOR_ATT, "blue");
                }
                else
                {
                    map.addAttribute(MultispeakDefines.MSP_RESULT_MSG, "* No methods reported for " + mspService +" getMethods:\n" + mspService + " is not supported.");
                    map.addAttribute(RESULT_COLOR_ATT, "red");
                }
            }catch (RemoteException re) {
                map.addAttribute( MultispeakDefines.MSP_RESULT_MSG, re.getMessage());
                map.addAttribute(RESULT_COLOR_ATT, "red");
            }
        }
        
        // If we called getMethods on the primary CIS vendor, we should reload the search fields
        if( multispeakFuncs.isPrimaryCIS(mspVendor)) {
            mspMeterSearchService.loadMspSearchFields(vendorId);            
        }
        
        map.addAttribute("mspVendorId", vendorId);
        return "redirect:home";
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
    private boolean isValidMspRequest(HttpServletRequest request, FlashScope flashScope) {

        List<MessageSourceResolvable> messages = new ArrayList<MessageSourceResolvable>();

        String param = request.getParameter("mspCompanyName");
        if( StringUtils.isBlank(param) ) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.invalidCompanyName"));
        }
        
        param = request.getParameter("mspMaxInitiateRequestObjects");
        if( StringUtils.isBlank(param) || !StringUtils.isNumeric(param)) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.invalidMaxInitiateRequestObjects"));
        }
        
        param = request.getParameter("mspRequestMessageTimeout");
        if( StringUtils.isBlank(param) || !StringUtils.isNumeric(param)) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.invalidRequestMessageTimeout"));
        }
        
        param = request.getParameter("mspMaxReturnRecords");
        if( StringUtils.isBlank(param) || !StringUtils.isNumeric(param)) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.invalidMaxReturnRecords"));
        }
        
        param = request.getParameter("mspTemplateNameDefault");
        if( StringUtils.isBlank(param)) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.invalidTemplateNameDefault"));
        }

        boolean mspPaoNameUsesExtension = ServletRequestUtils.getBooleanParameter(request, "mspPaoNameUsesExtension", false);   //if not found, then it wasn't checked
        if (mspPaoNameUsesExtension) {  //if using extensions, then must have an extension name
            String mspPaoNameAliasExtension = ServletRequestUtils.getStringParameter(request, "mspPaoNameAliasExtension", null);
            if (StringUtils.isBlank(mspPaoNameAliasExtension)) {
                messages.add(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.invalidPaoNameAliasExtension"));
            }               
        }
        
        param = request.getParameter("mspURL");
        try {
            new URL(param);
        } catch (MalformedURLException e) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.invalidURL"));
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
    private void addOrUpdateMspVendor(HttpServletRequest request, MultispeakVendor mspVendor, FlashScope flashScope, boolean add) throws Exception, DuplicateException {

        if (add) {
            multispeakDao.addMultispeakVendor(mspVendor);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.added", mspVendor.getCompanyName()));
        } else {
            multispeakDao.updateMultispeakVendor(mspVendor);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.interfaces.updated", mspVendor.getCompanyName()));

            if(defaultMspVendor.getVendorID().intValue() == mspVendor.getVendorID().intValue()) {
                updateRolePropertyValues(request);
            }
        }   
    }
    
    /**
     * Updates the Yukon Grp MultiSpeak role property values.
     * @param request - the http request
     * @throws Exception
     */
    private void updateRolePropertyValues(HttpServletRequest request) throws Exception {

        int oldMspPrimaryCIS = multispeakFuncs.getPrimaryCIS();
        int mspPrimaryCIS = ServletRequestUtils.getIntParameter(request, "mspPrimaryCIS", oldMspPrimaryCIS);
        
        String oldMspPaoNameAliasExtension = multispeakFuncs.getPaoNameAliasExtension();
        boolean mspPaoNameUsesExtension = ServletRequestUtils.getBooleanParameter(request, "mspPaoNameUsesExtension", false);   //if not found, then not checked

        String mspPaoNameAliasExtension = "";
        if (mspPaoNameUsesExtension) {  // only use the form value if mspPaoNameUsesExtension is checked.
            mspPaoNameAliasExtension = ServletRequestUtils.getStringParameter(request, "mspPaoNameAliasExtension", oldMspPaoNameAliasExtension);
        }
        
        MspPaoNameAliasEnum oldMspPaoNameAlias = multispeakFuncs.getPaoNameAlias();
        MspPaoNameAliasEnum mspPaoNameAlias = ServletRequestEnumUtils.getEnumParameter(request, MspPaoNameAliasEnum.class, "mspPaoNameAlias", oldMspPaoNameAlias);
        
        MultispeakMeterLookupFieldEnum oldMspMeterLookupField = multispeakFuncs.getMeterLookupField();
        MultispeakMeterLookupFieldEnum mspMeterLookupField = ServletRequestEnumUtils.getEnumParameter(request, MultispeakMeterLookupFieldEnum.class, "mspMeterLookupField", oldMspMeterLookupField);
        
        try {
            YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
            LiteYukonUser user = yukonUserContext.getYukonUser();
            // update Primary CIS Vendor
            if (oldMspPrimaryCIS != mspPrimaryCIS) {
                globalSettingUpdateDao.updateSettingValue(GlobalSettingType.MSP_PRIMARY_CB_VENDORID, String.valueOf(mspPrimaryCIS), user);
                //reload the search field methods since primaryCIS has changed
                mspMeterSearchService.loadMspSearchFields(mspPrimaryCIS);
            }
            if (oldMspPaoNameAliasExtension != mspPaoNameAliasExtension) {
                // update PaoName Alias Extension field name
                globalSettingUpdateDao.updateSettingValue(GlobalSettingType.MSP_PAONAME_EXTENSION, mspPaoNameAliasExtension, user);
            }            
            if (oldMspPaoNameAlias != mspPaoNameAlias) {
                // update PaoName Alias
                globalSettingUpdateDao.updateSettingValue(GlobalSettingType.MSP_PAONAME_ALIAS, String.valueOf(mspPaoNameAlias), user);
            }
            if ( oldMspMeterLookupField != mspMeterLookupField) {
                // update Meter Lookup Field
                globalSettingUpdateDao.updateSettingValue(GlobalSettingType.MSP_METER_LOOKUP_FIELD, String.valueOf(mspMeterLookupField), user);
            }
        } catch (Exception e) {
            CTILogger.error( "Global Settings for MultiSpeak Setup not saved", e );
        }

    }
    
    /**
     * Helper method to add the common model and view objects for this controller.
     * @param request - the http request
     * @param mav - the modelAndView to add objects to
     * @param mspVendor - the mspVendor to add
     * @return
     */
    private void addSystemModelAndViewObjects(HttpServletRequest request, ModelMap map, MultispeakVendor mspVendor) throws ServletRequestBindingException {
        
        map.addAttribute("mspVendorId", mspVendor.getVendorID());
        map.addAttribute("mspVendor", mspVendor);
        map.addAttribute("mspVendorList", multispeakDao.getMultispeakVendors());        
        map.addAttribute("mspCISVendorList", multispeakDao.getMultispeakCISVendors());
        map.addAttribute("possibleInterfaces", MultispeakDefines.getPossibleInterfaces(mspVendor));
        
        boolean showRoleProperties = (defaultMspVendor.getCompanyName().equals(mspVendor.getCompanyName()));
        map.addAttribute("showRoleProperties", showRoleProperties);
        
        //  Try to get the values from the request first, then get from the system.
        //  If these values were just updated, the db change may not have been received/processed yet and 
        //    the values returned from multispeakFuncs may be outdated.
        map.addAttribute("primaryCIS", ServletRequestUtils.getIntParameter(request, "mspPrimaryCIS", multispeakFuncs.getPrimaryCIS()));
        
        MspPaoNameAliasEnum mspPaoNameAlias = ServletRequestEnumUtils.getEnumParameter(request, MspPaoNameAliasEnum.class, "mspPaoNameAlias", multispeakFuncs.getPaoNameAlias());
        map.addAttribute("paoNameAlias", mspPaoNameAlias);
        
        String paoNameAliasExtension = ServletRequestUtils.getStringParameter(request, "mspPaoNameAliasExtension", multispeakFuncs.getPaoNameAliasExtension());
        map.addAttribute("paoNameAliasExtension", paoNameAliasExtension);
        map.addAttribute("paoNameUsesExtension", StringUtils.isNotBlank(paoNameAliasExtension));

        MultispeakMeterLookupFieldEnum mspMeterLookupField = ServletRequestEnumUtils.getEnumParameter(request, MultispeakMeterLookupFieldEnum.class, "mspMeterLookupField", multispeakFuncs.getMeterLookupField());
        map.addAttribute("meterLookupField", mspMeterLookupField);

        String resultMsg = ServletRequestUtils.getStringParameter(request, MultispeakDefines.MSP_RESULT_MSG, null);
        if (resultMsg != null) {
            map.addAttribute(MultispeakDefines.MSP_RESULT_MSG, resultMsg);
            map.addAttribute(RESULT_COLOR_ATT, ServletRequestUtils.getStringParameter(request, RESULT_COLOR_ATT, "black"));
        }
    }

    @PostConstruct 
    public void init() throws Exception {
        defaultMspVendor = multispeakDao.getMultispeakVendor("Cannon", "");
    }
}