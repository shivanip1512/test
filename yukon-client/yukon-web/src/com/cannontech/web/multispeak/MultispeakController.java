package com.cannontech.web.multispeak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.roleproperties.CisDetailRolePropertyEnum;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.dao.GlobalSettingUpdateDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.editor.MultispeakModel;
import com.cannontech.web.multispeak.validators.MultispeakValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RequestMapping("/setup/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP)
public class MultispeakController {

    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private com.cannontech.multispeak.dao.v5.MspObjectDao mspObjectDaoV5;
    @Autowired private MspMeterSearchService mspMeterSearchService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private GlobalSettingUpdateDao globalSettingUpdateDao;
    @Autowired private MultispeakValidator multispeakValidator;

    private MultispeakVendor defaultMspVendor;

    private static String RESULT_COLOR_ATT = "resultColor";

    // HOME
    @RequestMapping("home")
    public String yukonSetupHome(HttpServletRequest request, ModelMap map) {

        MultispeakVendor mspVendor = defaultMspVendor;
        mspVendor = multispeakDao.getMultispeakVendor(defaultMspVendor.getVendorID());
        map.addAttribute("defaultMspVendor", defaultMspVendor);
        map.addAttribute("mode", PageEditMode.VIEW);
        setUpModel(map, mspVendor, false);
        return "setup/msp_setup.jsp";
    }

    @RequestMapping("editYukonSetup")
    public String editYukonSetup(HttpServletRequest request, ModelMap map) {
        MultispeakVendor mspVendor = defaultMspVendor;
        mspVendor = multispeakDao.getMultispeakVendor(defaultMspVendor.getVendorID());
        map.addAttribute("defaultMspVendor", defaultMspVendor);
        map.addAttribute("mode", PageEditMode.EDIT);
        setUpModel(map, mspVendor, false);

        return "setup/msp_setup.jsp";

    }

    @RequestMapping("editVendorSetup/{vendorId}")
    public String editVendorSetup(HttpServletRequest request, ModelMap map, @ModelAttribute MultispeakModel multispeak,
            @PathVariable int vendorId) {
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
        map.addAttribute("defaultMspVendor", defaultMspVendor);
        map.addAttribute("mode", PageEditMode.EDIT);
        if (multispeak == null) {
            multispeak = new MultispeakModel();
        }
        multispeak.setMspVendor(mspVendor);
        addSystemModelAndViewObjects(map, mspVendor, true, multispeak, false);
        return "setup/vendor_setup.jsp";

    }

    @RequestMapping("vendorHome")
    public String vendorHome(HttpServletRequest request, ModelMap map, @ModelAttribute MultispeakModel multispeak) {

        return redirectVendorSetup(null, map, multispeak);

    }

    @RequestMapping("vendorHome/{vendorId}")
    public String vendorHome(HttpServletRequest request, ModelMap map, @ModelAttribute MultispeakModel multispeak,
            @PathVariable Integer vendorId) {

        return redirectVendorSetup(vendorId, map, multispeak);

    }

    private String redirectVendorSetup(Integer vendorId, ModelMap map, MultispeakModel multispeak) {
        MultispeakVendor mspVendor = null;
        if (vendorId != null) {
            mspVendor = multispeakDao.getMultispeakVendor(vendorId);
        }
        if (mspVendor == null && multispeakDao.getMultispeakVendors(true) != null
            && multispeakDao.getMultispeakVendors(true).size() > 0) {
            mspVendor = multispeakDao.getMultispeakVendors(true).get(0);
        }
        addSystemModelAndViewObjects(map, mspVendor, true, multispeak, false);
        map.addAttribute("mode", PageEditMode.VIEW);
        return "setup/vendor_setup.jsp";
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(HttpServletRequest request, ModelMap map, RedirectAttributes redirectAttributes,
            FlashScope flashScope, @ModelAttribute("multispeak") MultispeakModel multispeak, BindingResult result,
            YukonUserContext userContext) {
        boolean isCreateNew = multispeak.getMspVendor().getVendorID() == null;
        // Validate the request parameters before continuing on.
        multispeakValidator.validate(multispeak, result);
        if (result.hasErrors()) {
            return bindAndForward(multispeak, result, redirectAttributes, isCreateNew);
        }
        MultispeakVendor mspVendor = buildMspVendor(request, multispeak);

        try {
            addOrUpdateMspVendor(mspVendor, flashScope, isCreateNew, multispeak, userContext.getYukonUser());
        } catch (DataIntegrityViolationException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.multispeak.exception",
                    mspVendor.getCompanyName()));
            return bindAndForward(multispeak, result, redirectAttributes, isCreateNew);
        }
        map.addAttribute("mspVendorId", mspVendor.getVendorID());
        if (mspVendor.getVendorID() != null && mspVendor.getVendorID() == MultispeakVendor.CANNON_MSP_VENDORID) {
            return "redirect:home";
        } else {
            return "redirect:vendorHome/" + mspVendor.getVendorID();
        }
    }

    private String bindAndForward(MultispeakModel multispeak, BindingResult result, RedirectAttributes attrs,
            Boolean isCreateNew) {

        attrs.addFlashAttribute("multispeak", multispeak);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.multispeak", result);
        if (isCreateNew) {
            return "redirect:create";
        } else if (multispeak.getMspVendor().getVendorID() != null
            && multispeak.getMspVendor().getVendorID() == MultispeakVendor.CANNON_MSP_VENDORID) {
            return "redirect:editYukonSetup";
        } else {
            return "redirect:editVendorSetup/" + multispeak.getMspVendor().getVendorID();
        }
    }

    @RequestMapping(value = "vendorHome/{vendorId}", method = RequestMethod.DELETE)
    public String delete(ModelMap model, @PathVariable int vendorId, FlashScope flash) {
        MultispeakVendor deletedMspVendor = multispeakDao.getMultispeakVendor(vendorId);
        if (multispeakFuncs.isPrimaryCIS(deletedMspVendor)) {
            flash.setError(new YukonMessageSourceResolvable(
                "yukon.web.modules.adminSetup.vendor.deletePrimaryMessage", deletedMspVendor.getCompanyName()));
        } else {
            multispeakDao.deleteMultispeakVendor(vendorId);
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.multispeak.deleted",
                deletedMspVendor.getCompanyName()));
        }
        return "redirect:/multispeak/setup/vendorHome";

    }

    @RequestMapping("create")
    public String create(ModelMap model) {

        model.addAttribute("mode", PageEditMode.CREATE);
        addSystemModelAndViewObjects(model, null, true, null, true);
        return "setup/vendor_setup.jsp";
    }

    @RequestMapping(value = "pingURL/{serviceVersion}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> pingURL(HttpServletRequest request, ModelMap map,
            @ModelAttribute MultispeakModel multispeak, @PathVariable String serviceVersion) {
        Map<String, Object> json = new HashMap<>();
        String endpointURL = multispeak.getEndpointURL().trim();
        String mspService = multispeak.getService();
        MultiSpeakVersion version = MultiSpeakVersion.valueOf(serviceVersion);
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeak.getMspVendor().getVendorID());
        if (mspService != null) {
            try {
                if (version == MultiSpeakVersion.V3) {
                    ErrorObject[] objects = mspObjectDao.pingURL(mspVendor, mspService, endpointURL);
                    if (objects != null && objects != null && objects.length > 0) {
                        String result = "";
                        for (int i = 0; i < objects.length; i++) {
                            result += objects[i].getObjectID() + " - " + objects[i].getErrorString();
                        }

                        json.put(MultispeakDefines.MSP_RESULT_MSG, result);
                        json.put(RESULT_COLOR_ATT, "red");
                    } else {

                        json.put(MultispeakDefines.MSP_RESULT_MSG, "* " + mspService + " pingURL Successful");
                        json.put(RESULT_COLOR_ATT, "blue");
                    }
                } else {
                    com.cannontech.msp.beans.v5.commontypes.ErrorObject[] objects =
                        mspObjectDaoV5.pingURL(mspVendor, mspService, endpointURL);
                    if (objects != null && objects != null && objects.length > 0) {
                        String result = "";
                        for (int i = 0; i < objects.length; i++) {
                            result += objects[i].getErrorCode() + " - " + objects[i].getDisplayString();
                        }

                        json.put(MultispeakDefines.MSP_RESULT_MSG, result);
                        json.put(RESULT_COLOR_ATT, "red");

                    } else {
                        json.put(MultispeakDefines.MSP_RESULT_MSG, "* " + mspService + " pingURL Successful");
                        json.put(RESULT_COLOR_ATT, "blue");
                    }
                }
            } catch (MultispeakWebServiceClientException re) {

                json.put(MultispeakDefines.MSP_RESULT_MSG, re.getMessage());
                json.put(RESULT_COLOR_ATT, "red");
            }
        }
        return json;
    }

    @RequestMapping(value = "getMethods/{serviceVersion}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getMethods(HttpServletRequest request, ModelMap map,
            @ModelAttribute MultispeakModel multispeak, @PathVariable String serviceVersion) {
        Map<String, Object> json = new HashMap<>();
        String mspService = multispeak.getService();
        String endpointURL = multispeak.getEndpointURL().trim();
        MultiSpeakVersion version = MultiSpeakVersion.valueOf(serviceVersion);
        Integer vendorId = multispeak.getMspVendor().getVendorID();
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
        if (mspService != null) {
            try {
                if (version == MultiSpeakVersion.V3) {
                    List<String> supportedMethods = mspObjectDao.getMethods(mspVendor, mspService, endpointURL);
                    if (supportedMethods.isEmpty()) {

                        json.put(MultispeakDefines.MSP_RESULT_MSG, "* No methods reported for " + mspService
                            + " getMethods:\n" + mspService + " is not supported.");
                        json.put(RESULT_COLOR_ATT, "red");
                    } else {
                        String resultStr = mspService + " available methods:\n";
                        for (String method : supportedMethods) {
                            resultStr += " * " + method + "\n";
                        }

                        json.put(MultispeakDefines.MSP_RESULT_MSG, resultStr);
                        json.put(RESULT_COLOR_ATT, "blue");
                    }
                } else {
                    List<String> supportedMethods = mspObjectDaoV5.getMethods(mspVendor, mspService, endpointURL);
                    if (supportedMethods.isEmpty()) {

                        json.put(MultispeakDefines.MSP_RESULT_MSG, "* No methods reported for " + mspService
                            + " getMethods:\n" + mspService + " is not supported.");
                        json.put(RESULT_COLOR_ATT, "red");
                    } else {
                        String resultStr = mspService + " available methods:\n";
                        for (String method : supportedMethods) {
                            resultStr += " * " + method + "\n";
                        }

                        json.put(MultispeakDefines.MSP_RESULT_MSG, resultStr);
                        json.put(RESULT_COLOR_ATT, "blue");
                    }
                }
            } catch (MultispeakWebServiceClientException re) {
                json.put(MultispeakDefines.MSP_RESULT_MSG, re.getMessage());
                json.put(RESULT_COLOR_ATT, "red");
            }
        }

        // If we called getMethods on the primary CIS vendor, we should reload the search fields
        if (multispeakFuncs.isPrimaryCIS(mspVendor)) {
            mspMeterSearchService.loadMspSearchFields(vendorId);
        }

        return json;
    }

    private MultispeakVendor buildMspVendor(HttpServletRequest request, MultispeakModel multispeak) {
        List<MultispeakInterface> mspInterfaces = new ArrayList<>();
        Integer vendorId = multispeak.getMspVendor().getVendorID();
        MultispeakVendor multispeakVendor = multispeak.getMspVendor();
        String companyName = multispeakVendor.getCompanyName();
        String appName = multispeakVendor.getAppName();
        String password = multispeakVendor.getPassword();
        String username = multispeakVendor.getUserName();
        Boolean validateCertificate = multispeakVendor.getValidateCertificate();
        int maxReturnRecords =
            multispeakVendor.getMaxReturnRecords() > 0 ? multispeakVendor.getMaxReturnRecords()
                : MultispeakDefines.MSP_MAX_RETURN_RECORDS;
        long requestMessageTimeout =
            multispeakVendor.getRequestMessageTimeout() > -1 ? multispeakVendor.getRequestMessageTimeout()
                : MultispeakDefines.MSP_REQUEST_MESSAGE_TIMEOUT;
        long maxInitiateRequestObjects =
            multispeakVendor.getMaxInitiateRequestObjects() > -1 ? multispeakVendor.getMaxInitiateRequestObjects()
                : MultispeakDefines.MSP_MAX_INITIATE_REQUEST_OBJECTS;

        String templateNameDefault =
            multispeakVendor.getTemplateNameDefault() == null ? MultispeakDefines.MSP_TEMPLATE_NAME_DEFAULT
                : multispeakVendor.getTemplateNameDefault();

        String outPassword = multispeakVendor.getOutPassword();
        String outUsername = multispeakVendor.getOutUserName();
        if (multispeak.getMspVendor().getVendorID() != null
            && multispeak.getMspVendor().getVendorID() == MultispeakVendor.CANNON_MSP_VENDORID) {
            mspInterfaces = multispeak.getMspInterfaceList();
        } else {
            for (MultispeakInterface multispeakInterface : multispeak.getMspInterfaceList()) {
                if (multispeakInterface.getVersion() != null) {
                    mspInterfaces.add(multispeakInterface);
                }
            }
        }
        String mspURL = multispeakVendor.getUrl();

        if (!mspURL.endsWith("/")) {
            mspURL += "/";
        }

        MultispeakVendor mspVendor =
            new MultispeakVendor(vendorId, companyName, appName, username, password, outUsername, outPassword,
                maxReturnRecords, requestMessageTimeout, maxInitiateRequestObjects, templateNameDefault, validateCertificate);

        mspVendor.setMspInterfaces(mspInterfaces);

        return mspVendor;
    }

    private void addOrUpdateMspVendor(MultispeakVendor mspVendor, FlashScope flashScope,
            boolean add, MultispeakModel multispeak, LiteYukonUser user) {

        if (add) {
            multispeakDao.addMultispeakVendor(mspVendor);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.multispeak.added",
                    mspVendor.getCompanyName()));
        } else {
            multispeakDao.updateMultispeakVendor(mspVendor);

            // Check whether the vendor is Primary CIS Vendor or not. If yes then update the global setting if CB_Server support is removed.
            if (mspVendor.getVendorID() == multispeakFuncs.getPrimaryCIS()) {
                updatePrimaryVendorGlobalsetting(user, mspVendor);
            }

            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.multispeak.updated",
                    mspVendor.getCompanyName()));

            if (defaultMspVendor.getVendorID().intValue() == mspVendor.getVendorID().intValue()) {
                updateRolePropertyValues(user, multispeak);
            }
        }
    }

    /**
     * Update the value of MSP_PRIMARY_CB_VENDORID (Primary CIS Vendor) Global setting to 0 if the CB_Server support is removed
     */
    private void updatePrimaryVendorGlobalsetting(LiteYukonUser user, MultispeakVendor mspVendor) {
        if (mspVendor.getMspInterfaces() != null) {

            Optional<MultispeakInterface> getFirstMspInterface = mspVendor.getMspInterfaces()
                                                                          .stream()
                                                                          .filter(mspInterface -> (mspInterface.getMspInterface() != MultispeakDefines.CB_Server_STR))
                                                                          .findFirst();

            if (getFirstMspInterface.isPresent()) {
                globalSettingUpdateDao.updateSettingValue(GlobalSettingType.MSP_PRIMARY_CB_VENDORID, 0, user);
            }
        }
    }

    private void updateRolePropertyValues(LiteYukonUser user, MultispeakModel multispeak) {

        int oldMspPrimaryCIS = multispeakFuncs.getPrimaryCIS();
        int mspPrimaryCIS = multispeak.getMspPrimaryCIS();

        String oldMspPaoNameAliasExtension = multispeakFuncs.getPaoNameAliasExtension();
        boolean mspPaoNameUsesExtension = multispeak.getPaoNameUsesExtension(); // if not found, then not
                                                                                // checked

        String mspPaoNameAliasExtension = "";
        if (mspPaoNameUsesExtension) { // only use the form value if mspPaoNameUsesExtension is checked.
            mspPaoNameAliasExtension = multispeak.getPaoNameAliasExtension();
        }

        MspPaoNameAliasEnum oldMspPaoNameAlias = multispeakFuncs.getPaoNameAlias();
        MspPaoNameAliasEnum mspPaoNameAlias = multispeak.getPaoNameAlias();

        MultispeakMeterLookupFieldEnum oldMspMeterLookupField = multispeakFuncs.getMeterLookupField();
        MultispeakMeterLookupFieldEnum mspMeterLookupField = multispeak.getMeterLookupField();

        try {
            // update Primary CIS Vendor
            if (oldMspPrimaryCIS != mspPrimaryCIS) {
                globalSettingUpdateDao.updateSettingValue(GlobalSettingType.MSP_PRIMARY_CB_VENDORID, mspPrimaryCIS,
                        user);
                if (globalSettingDao.getEnum(GlobalSettingType.CIS_DETAIL_TYPE, CisDetailRolePropertyEnum.class) != CisDetailRolePropertyEnum.CAYENTA) {
                    // Manage only if not already set to CAYENTA.
                    if (mspPrimaryCIS <= MultispeakVendor.CANNON_MSP_VENDORID) {
                        globalSettingUpdateDao.updateSettingValue(GlobalSettingType.CIS_DETAIL_TYPE,
                            CisDetailRolePropertyEnum.NONE, user);
                    } else {
                        globalSettingUpdateDao.updateSettingValue(GlobalSettingType.CIS_DETAIL_TYPE,
                            CisDetailRolePropertyEnum.MULTISPEAK, user);
                    }
                }

                // reload the search field methods since primaryCIS has changed
                mspMeterSearchService.loadMspSearchFields(mspPrimaryCIS);
            }
            if (oldMspPaoNameAliasExtension != mspPaoNameAliasExtension) {
                // update PaoName Alias Extension field name
                globalSettingUpdateDao.updateSettingValue(GlobalSettingType.MSP_PAONAME_EXTENSION,
                    mspPaoNameAliasExtension, user);
            }
            if (oldMspPaoNameAlias != mspPaoNameAlias) {
                // update PaoName Alias
                globalSettingUpdateDao.updateSettingValue(GlobalSettingType.MSP_PAONAME_ALIAS,
                    String.valueOf(mspPaoNameAlias), user);
            }
            if (oldMspMeterLookupField != mspMeterLookupField) {
                // update Meter Lookup Field
                globalSettingUpdateDao.updateSettingValue(GlobalSettingType.MSP_METER_LOOKUP_FIELD,
                    String.valueOf(mspMeterLookupField), user);
            }
        } catch (Exception e) {
            CTILogger.error("Global Settings for MultiSpeak Setup not saved", e);
        }

    }

    private void setUpModel(ModelMap map, MultispeakVendor mspVendor, boolean ignoreCannon) {
        boolean showRoleProperties = false;
        boolean noVendorsExist = false;
        Object modelMultispeak = map.get("multispeak");
        MultispeakModel multispeak = null;
        if (modelMultispeak instanceof MultispeakModel) {
            multispeak = (MultispeakModel) modelMultispeak;
        }

        if (multispeak == null) {
            multispeak = new MultispeakModel();
        }
        multispeak.setMspVendor(defaultMspVendor);
        List<MultiSpeakVersion> mspVersionList =
            new ArrayList<>(Arrays.asList(MultiSpeakVersion.V3, MultiSpeakVersion.V5));
        if (mspVendor != null) {
            map.addAttribute("mspVendorId", mspVendor.getVendorID());
            showRoleProperties = (defaultMspVendor.getCompanyName().equals(mspVendor.getCompanyName()));
            map.addAttribute("showRoleProperties", showRoleProperties);
        } else {
            noVendorsExist = true;
        }
        map.addAttribute("noVendorsExist", noVendorsExist);

        map.addAttribute("mspVendorList", multispeakDao.getMultispeakVendors(ignoreCannon));
        map.addAttribute("mspCISVendorList", multispeakFuncs.getPrimaryCisVendorList());
        map.addAttribute("possibleInterfaces", MultispeakDefines.getPossibleInterfaces(mspVendor));
        map.addAttribute("paoNameAliases", MspPaoNameAliasEnum.values());
        map.addAttribute("meterLookupFields", MultispeakMeterLookupFieldEnum.values());
        List<MultispeakInterface> mspInterfaceList = new ArrayList<MultispeakInterface>();

        Map<Pair<String, MultiSpeakVersion>, MultispeakInterface> interfaceMap = mspVendor.getMspInterfaceMap();
        MultispeakDefines.getPossibleInterfaces(mspVendor).forEach(
            multispeakInterface -> {
                MultispeakInterface mspInterface = interfaceMap.get(multispeakInterface);
                if (mspInterface == null) {
                    mspInterface = new MultispeakInterface();
                    mspInterface.setMspInterface(multispeakInterface.getLeft());
                    mspInterface.setVendorID(mspVendor.getVendorID());
                    mspInterface.setMspEndpoint(mspVendor.getUrl()
                        + multispeakInterface.getRight().toString().toLowerCase() + "/" + multispeakInterface.getLeft());
                    mspInterface.setVersion(multispeakInterface.getRight());
                    mspInterface.setInterfaceEnabled(false);
                } else {
                    mspInterface.setInterfaceEnabled(true);
                }
                mspInterfaceList.add(mspInterface);
            });

        // Try to get the values from the request first, then get from the system.
        // If these values were just updated, the db change may not have been received/processed yet and
        // the values returned from multispeakFuncs may be outdated.
        multispeak.setMspPrimaryCIS(multispeakFuncs.getPrimaryCIS());
        multispeak.setMspInterfaceList(mspInterfaceList);
        multispeak.setMspVendor(mspVendor);

        multispeak.setPaoNameAlias(multispeakFuncs.getPaoNameAlias());
        String paoNameAliasExtension = multispeakFuncs.getPaoNameAliasExtension();

        multispeak.setPaoNameAliasExtension(paoNameAliasExtension);
        multispeak.setPaoNameUsesExtension(StringUtils.isNotBlank(paoNameAliasExtension));
        MultispeakMeterLookupFieldEnum mspMeterLookupField = multispeakFuncs.getMeterLookupField();
        multispeak.setMeterLookupField(mspMeterLookupField);
        map.addAttribute("mspVersionList", mspVersionList);
        map.addAttribute("multispeak", multispeak);
    }

    private void addSystemModelAndViewObjects(ModelMap map, MultispeakVendor mspVendor, boolean ignoreCannon,
            MultispeakModel multispeak, boolean isCreateNew) {
        boolean showRoleProperties = false;
        boolean noVendorsExist = false;
        Object modelMultispeak = map.get("multispeak");
        if (modelMultispeak instanceof MultispeakModel) {
            multispeak = (MultispeakModel) modelMultispeak;
        }

        if (multispeak == null) {
            multispeak = new MultispeakModel();
        }
        List<MultiSpeakVersion> mspVersionList =
            new ArrayList<>(Arrays.asList(MultiSpeakVersion.V3, MultiSpeakVersion.V5));
        List<MultiSpeakVersion> mspVersion5 = new ArrayList<>(Arrays.asList(MultiSpeakVersion.V5));
        List<MultiSpeakVersion> mspVersion3 = new ArrayList<>(Arrays.asList(MultiSpeakVersion.V3));

        map.addAttribute("mspVendor", mspVendor);
        map.addAttribute("mspVendorList", multispeakDao.getMultispeakVendors(ignoreCannon));
        map.addAttribute("mspCISVendorList", multispeakDao.getMultispeakCISVendors());
        map.addAttribute("possibleInterfaces", MultispeakDefines.getPossibleInterfaces(mspVendor));

        if (mspVendor != null) {
            map.addAttribute("mspVendorId", mspVendor.getVendorID());
            showRoleProperties = (defaultMspVendor.getCompanyName().equals(mspVendor.getCompanyName()));
            map.addAttribute("showRoleProperties", showRoleProperties);
            multispeak.setMspInterfaceList(getMSPInterfaces(mspVendor, false));
        } else {
            noVendorsExist = true;
        }
        if (isCreateNew) {
            noVendorsExist = false;
            multispeak.setMspInterfaceList(getMSPInterfaces(mspVendor, isCreateNew));
        }

        // Try to get the values from the request first, then get from the system.
        // If these values were just updated, the db change may not have been received/processed yet and
        // the values returned from multispeakFuncs may be outdated.
        Optional.ofNullable(multispeak.getMspPrimaryCIS()).filter(s -> s != null).orElse(
            multispeakFuncs.getPrimaryCIS());

        multispeak.setPaoNameAlias(Optional.ofNullable(multispeak.getPaoNameAlias()).filter(s -> s != null).orElse(
            multispeakFuncs.getPaoNameAlias()));

        String paoNameAliasExtension =
            Optional.ofNullable(multispeak.getPaoNameAliasExtension()).filter(s -> s != null).orElse(
                multispeakFuncs.getPaoNameAliasExtension());
        multispeak.setPaoNameAliasExtension(paoNameAliasExtension);
        multispeak.setPaoNameUsesExtension(StringUtils.isNotBlank(paoNameAliasExtension));

        multispeak.setMeterLookupField(Optional.ofNullable(multispeak.getMeterLookupField()).filter(s -> s != null).orElse(
            multispeakFuncs.getMeterLookupField()));

        map.addAttribute("mspVersionList", mspVersionList);
        map.addAttribute("mspVersion5", mspVersion5);
        map.addAttribute("mspVersion3", mspVersion3);
        multispeak.setMspVendor(mspVendor);

        map.addAttribute("noVendorsExist", noVendorsExist);
        map.addAttribute("multispeak", multispeak);
    }

    private List<MultispeakInterface> getMSPInterfaces(MultispeakVendor mspVendor, boolean isCreateNew) {
        List<MultispeakInterface> mspInterfaceList = new ArrayList<MultispeakInterface>();
        Map<Pair<String, MultiSpeakVersion>, MultispeakInterface> interfaceMap = new HashMap<>();
        if (mspVendor != null) {
            interfaceMap = mspVendor.getMspInterfaceMap();
        }

        for (Pair<String, MultiSpeakVersion> mspInterface : MultispeakDefines.getPossibleInterfaces(mspVendor)) {
            MultispeakInterface multispeakInterface = interfaceMap.get(mspInterface);
            if (multispeakInterface != null) {
                multispeakInterface.setInterfaceEnabled(true);
                mspInterfaceList.add(multispeakInterface);
            } else {
                if (!isCreateNew) {
                    multispeakInterface =
                        new MultispeakInterface(mspVendor.getVendorID(), mspInterface.getLeft(),
                            mspVendor.getUrl().concat(mspInterface.getLeft()), mspInterface.getRight());

                } else {
                    mspVendor = new MultispeakVendor();
                    multispeakInterface =
                        new MultispeakInterface(mspVendor.getVendorID(), mspInterface.getLeft(),
                            mspVendor.getUrl().concat(mspInterface.getLeft()), mspInterface.getRight());
                }
                multispeakInterface.setInterfaceEnabled(false);
                mspInterfaceList.add(multispeakInterface);
            }
        }
        return mspInterfaceList;
    }

    @PostConstruct
    public void init() {
        defaultMspVendor = multispeakDao.getMultispeakVendor("Cannon", "");
    }
}