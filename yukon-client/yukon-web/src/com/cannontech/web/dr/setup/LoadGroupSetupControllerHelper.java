package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.cannontech.common.dr.setup.AddressLevel;
import com.cannontech.common.dr.setup.AddressUsage;
import com.cannontech.common.dr.setup.ControlPriority;
import com.cannontech.common.dr.setup.EmetconAddressUsage;
import com.cannontech.common.dr.setup.EmetconRelayUsage;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.dr.setup.LoadGroupDigiSep;
import com.cannontech.common.dr.setup.LoadGroupEmetcon;
import com.cannontech.common.dr.setup.LoadGroupExpresscom;
import com.cannontech.common.dr.setup.LoadGroupMCT;
import com.cannontech.common.dr.setup.LoadGroupRipple;
import com.cannontech.common.dr.setup.LoadGroupPoint;
import com.cannontech.common.dr.setup.LoadGroupVersacom;
import com.cannontech.common.dr.setup.Loads;
import com.cannontech.common.dr.setup.Relays;
import com.cannontech.common.dr.setup.RippleGroup;
import com.cannontech.common.dr.setup.RippleGroupAreaCode;
import com.cannontech.common.dr.setup.VersacomAddressUsage;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.database.data.device.lm.SepDeviceClass;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Service
public class LoadGroupSetupControllerHelper {

    @Autowired ServerDatabaseCache cache;
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    
    private static final int CONTROL_BITS_LENGTH = 50;
    private static final int RESTORE_BITS_LENGTH = 50;
    private static final int SPECIAL_RIPPLE_CONTROL_BITS_LENGTH = 34;
    private static final int SPECIAL_RIPPLE_RESTORE_BITS_LENGTH = 34;

    /**
     * Each load group can set its model attributes here.
     */
    public void buildModelMap(PaoType type, ModelMap model, HttpServletRequest request, YukonUserContext userContext, String bindingResultKey) {
        LoadGroupBase<?> loadGroupBase = (LoadGroupBase<?>) model.get("loadGroup");
        model.addAttribute("isLoadGroupSupportRoute", loadGroupBase.getType().isLoadGroupSupportRoute());
        
        PageEditMode mode = (PageEditMode) model.get("mode");
        switch (type) {
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_RFN_EXPRESSCOMM:
            if (mode == PageEditMode.VIEW) {
                LoadGroupExpresscom loadGroup = (LoadGroupExpresscom) model.get("loadGroup");

                List<AddressUsage> addressUsage = loadGroup.getAddressUsage();
                List<AddressUsage> loadAddressUsage =
                    addressUsage.stream().filter(e -> e.isLoadAddressUsage()).collect(Collectors.toList());
                List<AddressUsage> geoAddressUsage =
                    new ArrayList<>(CollectionUtils.subtract(addressUsage, loadAddressUsage));
                model.addAttribute("loadAddressUsage", loadAddressUsage);
                model.addAttribute("geoAddressUsage", geoAddressUsage);

                if (loadGroup.getAddressUsage().contains(AddressUsage.GEO)) {
                    model.addAttribute("displayGeo", true);
                }
                if (loadGroup.getAddressUsage().contains(AddressUsage.SUBSTATION)) {
                    model.addAttribute("displaySubstation", true);
                }
                if (loadGroup.getAddressUsage().contains(AddressUsage.FEEDER)) {
                    model.addAttribute("displayFeeder", true);
                    loadGroup.setFeeder(getFormattedAddress(loadGroup.getFeeder()));
                }
                if (loadGroup.getAddressUsage().contains(AddressUsage.ZIP)) {
                    model.addAttribute("displayZip", true);
                }
                if (loadGroup.getAddressUsage().contains(AddressUsage.USER)) {
                    model.addAttribute("displayUser", true);
                }
                if (loadGroup.getAddressUsage().contains(AddressUsage.SERIAL)) {
                    model.addAttribute("displaySerial", true);
                }
                if (loadGroup.getAddressUsage().contains(AddressUsage.PROGRAM)) {
                    model.addAttribute("displayProgram", true);
                }
                if (loadGroup.getAddressUsage().contains(AddressUsage.SPLINTER)) {
                    model.addAttribute("displaySplinter", true);
                }
                if (loadGroup.getAddressUsage().contains(AddressUsage.LOAD)) {
                    model.addAttribute("loadSelected", true);
                }
            } else {
                if (type == PaoType.LM_GROUP_EXPRESSCOMM) {
                    setCommunicationRoute(model, request, userContext);
                }
                model.addAttribute("protocolPriority", ControlPriority.values());
                model.addAttribute("addressUsageList", AddressUsage.getGeoAddressUsage());
                model.addAttribute("feederList", getBitAddressLevel());
                model.addAttribute("loadAddressUsageList", AddressUsage.getLoadAddressUsage());
                model.addAttribute("loadsList", Loads.values());
            }
            break;
        case LM_GROUP_ITRON:
            model.addAttribute("relayIds", ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8));
            break;
        case LM_GROUP_DIGI_SEP:
            model.addAttribute("deviceClassList", SepDeviceClass.values());
            break;
        case LM_GROUP_VERSACOM:
            LoadGroupVersacom loadGroup = (LoadGroupVersacom) model.get("loadGroup");
            if (mode == PageEditMode.VIEW) {
                List<VersacomAddressUsage> addressUsage = loadGroup.getAddressUsage();
                // Utility Address
                if (addressUsage.contains(VersacomAddressUsage.UTILITY)) { 
                    model.addAttribute("showUtilityAddress", true);
                }
                // Section Address
                if (addressUsage.contains(VersacomAddressUsage.SECTION)) { 
                    model.addAttribute("showSectionAddress", true);
                }
                // Class Address
                if (addressUsage.contains(VersacomAddressUsage.CLASS)) { 
                    model.addAttribute("showClassAddress", true);
                    loadGroup.setClassAddress(getFormattedAddress(loadGroup.getClassAddress()));
                }
                // Division Address
                if (addressUsage.contains(VersacomAddressUsage.DIVISION)) { 
                    model.addAttribute("showDivisionAddress", true);
                    loadGroup.setDivisionAddress(getFormattedAddress(loadGroup.getDivisionAddress()));
                }
                // Serial Address
                setVersacomSerialAddressUsage(loadGroup, model);
                // Relay Usage
                model.addAttribute("relayUsages", loadGroup.getRelayUsage());
                model.addAttribute("addressUsages", loadGroup.getAddressUsage());
            } else {
                setCommunicationRoute(model, request, userContext);
                setVersacomSerialAddressUsage(loadGroup, model);
                model.addAttribute("showUtilityAddress", true);
                model.addAttribute("classAddressValues", getBitAddressLevel());
                model.addAttribute("divisionAddressValues", getBitAddressLevel());
                model.addAttribute("addressUsageList",VersacomAddressUsage.getAddressUsage());
                model.addAttribute("relayUsageList",Relays.getRelays());
            }
            break;
        case LM_GROUP_EMETCON:
            setCommunicationRoute(model, request, userContext);
            break;
        case LM_GROUP_MCT:
            LoadGroupMCT loadGroupMCT = (LoadGroupMCT) model.get("loadGroup");
            setCommunicationRoute(model, request, userContext);
            model.addAttribute("isMctGroupSelected", true);
            model.addAttribute("addressLevels", AddressLevel.values());
            model.addAttribute("isMctAddressSelected", loadGroupMCT.getLevel() == AddressLevel.MCT_ADDRESS);
            model.addAttribute("mctAddressEnumVal", AddressLevel.MCT_ADDRESS);
            model.addAttribute("relayUsageList", Relays.values());
            model.addAttribute("isViewMode", mode == PageEditMode.VIEW);
            if (model.containsAttribute(bindingResultKey) && mode != PageEditMode.VIEW) {
                BindingResult result = (BindingResult) model.get(bindingResultKey);
                if (result.hasFieldErrors("mctDeviceId")) {
                    model.addAttribute("mctAddressHasError", true);
                } else {
                    model.addAttribute("mctAddressHasError", false);
                }
            }
            break;
        case LM_GROUP_RIPPLE:
            model.addAttribute("isRippleGroupSelected", true);
            model.addAttribute("shedTimeIntervals", TimeIntervals.getRippleShedtime());
            LoadGroupRipple loadGroupRipple = (LoadGroupRipple) model.get("loadGroup");
            boolean isSpecialRippleEnabled = loadGroupRipple.isSpecialRippleEnabled(userContext.getYukonUser());
            model.addAttribute("isSpecialRippleEnabled", isSpecialRippleEnabled);
            setCommunicationRoute(model, request, userContext);
            if (mode == PageEditMode.VIEW) {
                model.addAttribute("isViewMode", true);
                loadGroupRipple.setControl(getFormattedAddress(loadGroupRipple.getControl()));
                loadGroupRipple.setRestore(getFormattedAddress(loadGroupRipple.getRestore()));
            }
            if (isSpecialRippleEnabled) {
                model.addAttribute("groups", RippleGroup.values());
                model.addAttribute("areaCodes", RippleGroupAreaCode.values());
                model.addAttribute("controlBitsLength", SPECIAL_RIPPLE_CONTROL_BITS_LENGTH);
                model.addAttribute("restoreBitsLength", SPECIAL_RIPPLE_RESTORE_BITS_LENGTH);
            } else {
                model.addAttribute("controlBitsLength", CONTROL_BITS_LENGTH);
                model.addAttribute("restoreBitsLength", RESTORE_BITS_LENGTH);
            }
            break;
        case LM_GROUP_POINT:
            LoadGroupPoint loadGroupPoint = (LoadGroupPoint) model.get("loadGroup");
            model.addAttribute("isPointGroupSelected", true);
            model.addAttribute("isViewMode", mode == PageEditMode.VIEW);
            model.addAttribute("isCreateMode", mode == PageEditMode.CREATE);
            model.addAttribute("isEditMode", mode == PageEditMode.EDIT);
            // This will be updated in YUK-21025
            if (loadGroupPoint.getPointIdUsage() != null) {
                // We should set the state name here
                model.addAttribute("startState", loadGroupPoint.getStartControlRawState());
                setControlStartState(loadGroupPoint, model, request, userContext);
            }
            if (model.containsAttribute(bindingResultKey) && mode != PageEditMode.VIEW) {
                BindingResult result = (BindingResult) model.get(bindingResultKey);
                if (result.hasFieldErrors("deviceIdUsage")) {
                    model.addAttribute("deviceIdUsageHasError", true);
                } else {
                    model.addAttribute("deviceIdUsageHasError", false);
                }
            }
            break;
        }
    }

    /**
     * Sets the communication route
     */
    private void setCommunicationRoute(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        // Give API call to get all routes
        List<LiteYukonPAObject> routes = new ArrayList<>();
        String url = helper.findWebServerUrl(request, userContext, ApiURL.retrieveAllRoutesUrl);
        ResponseEntity<List<? extends Object>> response = apiRequestHelper.callAPIForList(userContext, request, url,
            LiteYukonPAObject.class, HttpMethod.GET, LiteYukonPAObject.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            routes = (List<LiteYukonPAObject>) response.getBody();
        }
        model.addAttribute("routes", routes);
    }

    /**
     * Sets the control start state
     */
    private void setControlStartState(LoadGroupPoint loadGroupPoint, ModelMap model, HttpServletRequest request,
            YukonUserContext userContext) {
        // Give API call to get all control state
        List<LMDto> startStates = new ArrayList<>();
        // This will be updated in YUK-21025
        String url = helper.findWebServerUrl(request, userContext, ApiURL.drStartStateUrl + loadGroupPoint.getPointIdUsage());
        ResponseEntity<List<? extends Object>> response = apiRequestHelper.callAPIForList(userContext, request, url,
                LMDto.class, HttpMethod.GET, LMDto.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            startStates = (List<LMDto>) response.getBody();
        }
        model.addAttribute("startStates", startStates);
    }

    /**
     * Default values for object should be set here.
     * @param liteYukonUser 
     */
    public void setDefaultValues(LoadGroupBase group, LiteYukonUser liteYukonUser) {
        switch (group.getType()) {
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_RFN_EXPRESSCOMM:
            LoadGroupExpresscom expresscomGroup = ((LoadGroupExpresscom) group);
            expresscomGroup.setServiceProvider(1);
            expresscomGroup.setProtocolPriority(ControlPriority.DEFAULT);
            break;
        case LM_GROUP_VERSACOM:
            ((LoadGroupVersacom) group).setSectionAddress(0);
            ((LoadGroupVersacom) group).setClassAddress("0");
            ((LoadGroupVersacom) group).setDivisionAddress("0");
            ((LoadGroupVersacom) group).setSerialAddress("0");
            ((LoadGroupVersacom) group).setUtilityAddress(1);
            ((LoadGroupVersacom) group).setAddressUsage(Lists.newArrayList(VersacomAddressUsage.UTILITY));
            ((LoadGroupVersacom) group).setRelayUsage(Lists.newArrayList(Relays.RELAY_1));
            break;
        case LM_GROUP_EMETCON:
            ((LoadGroupEmetcon) group).setGoldAddress(1);
            ((LoadGroupEmetcon) group).setSilverAddress(1);
            ((LoadGroupEmetcon) group).setAddressUsage(EmetconAddressUsage.GOLD);
            ((LoadGroupEmetcon) group).setRelayUsage(EmetconRelayUsage.RELAY_A);
            break;
        case LM_GROUP_DIGI_SEP:
            ((LoadGroupDigiSep) group).setRampInMinutes(30);
            ((LoadGroupDigiSep) group).setRampOutMinutes(30);
            break;
        case LM_GROUP_MCT:
            LoadGroupMCT loadGroupMCT = ((LoadGroupMCT) group);
            List relayUsages = Lists.newArrayList();
            relayUsages.add(Relays.RELAY_1);
            loadGroupMCT.setRelayUsage(relayUsages);
            break;
        case LM_GROUP_RIPPLE:
            LoadGroupRipple loadGroupRipple = (LoadGroupRipple) group;
            if (loadGroupRipple.isSpecialRippleEnabled(liteYukonUser)) {
                loadGroupRipple.setAreaCode(RippleGroupAreaCode.BELTRAMI);
                loadGroupRipple.setGroup(RippleGroup.TST);
                loadGroupRipple.setShedTime(TimeIntervals.MINUTES_7_SECONDS_30.getSeconds());
            } else {
                loadGroupRipple.setShedTime(TimeIntervals.NONE.getSeconds());
            }
        }
        // Set default value for common field.
        group.setkWCapacity(0.0);
    }
    
    public static List<Integer> getBitAddressLevel() {
        return ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
    }
    
    /**
     * Set validation messages that have to be shown in flash scope.
     */
    public void setValidationMessageInFlash(BindingResult result, FlashScope flash, PaoType type) {
        switch (type) {
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_RFN_EXPRESSCOMM:
        case LM_GROUP_VERSACOM:
            if (result.hasFieldErrors("addressUsage")) {
                flash.setError(result.getFieldError("addressUsage"));
            }
            if (result.hasFieldErrors("feeder")) {
                flash.setError(result.getFieldError("feeder"));
            }
            if (result.hasFieldErrors("relayUsage")) {
                flash.setError(result.getFieldError("relayUsage"));
            }
        }
    }

    private void setVersacomSerialAddressUsage(LoadGroupVersacom loadGroup, ModelMap model) {
        try {
            Integer serialAddress = Integer.valueOf(loadGroup.getSerialAddress());
            if (serialAddress > 0) {
                model.addAttribute("showSerialAddress", true);
            }
        } catch (Exception e) {
            // This will be handle inside validator with proper message.
        }
    }

    /**
     * Returns formatted address after converting the input string.
     */
    private static String getFormattedAddress(String address) {
        return IntStream.range(0, address.length())
                        .filter(index -> address.charAt(index) == '1')
                        .boxed()
                        .map(addressIndex -> Integer.toString(addressIndex + 1))
                        .collect(Collectors.joining(", "));
    }
}
