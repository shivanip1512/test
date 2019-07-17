package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.cannontech.common.dr.setup.AddressUsage;
import com.cannontech.common.dr.setup.ControlPriority;
import com.cannontech.common.dr.setup.EmetconAddressUsage;
import com.cannontech.common.dr.setup.EmetconRelayUsage;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.dr.setup.LoadGroupDigiSep;
import com.cannontech.common.dr.setup.LoadGroupEmetcon;
import com.cannontech.common.dr.setup.LoadGroupExpresscom;
import com.cannontech.common.dr.setup.Loads;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.lm.SepDeviceClass;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.ImmutableList;

@Service
public class LoadGroupSetupControllerHelper {

    @Autowired ServerDatabaseCache cache;
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;

    /**
     * Each load group can set its model attributes here.
     */
    public void buildModelMap(PaoType type, ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        switch (type) {
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_RFN_EXPRESSCOMM:
            PageEditMode mode = (PageEditMode) model.get("mode");
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
                model.addAttribute("feederList", getFeederList());
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
        case LM_GROUP_EMETCON:
            setCommunicationRoute(model, request, userContext);
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
     * Default values for object should be set here.
     */
    public void setDefaultValues(LoadGroupBase group) {
        switch (group.getType()) {
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_RFN_EXPRESSCOMM:
            LoadGroupExpresscom expresscomGroup = ((LoadGroupExpresscom) group);
            expresscomGroup.setServiceProvider(1);
            expresscomGroup.setProtocolPriority(ControlPriority.DEFAULT);
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
        }
        // Set default value for common field.
        group.setkWCapacity(0.0);
    }
    
    public static List<Integer> getFeederList() {
        return ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
    }
    
    /**
     * Set validation messages that have to be shown in flash scope.
     */
    public void setValidationMessageInFlash(BindingResult result, FlashScope flash, PaoType type) {
        switch (type) {
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_RFN_EXPRESSCOMM:
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
}
