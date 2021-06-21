package com.cannontech.dr.pxmw.model;

import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

import org.apache.logging.log4j.core.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

//Data retrieval URLs
public enum PxMWRetrievalUrl {
    SECURITY_TOKEN(PxMWVersion.V1, "api/v1/security/serviceaccount/token",
            "https://eas-all-apim-eus-dev.azure-api.net/api/security/serviceaccount/token",
            // 200,401
            List.of(HttpStatus.OK, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of(),
            false),
    DEVICES_BY_SITE(PxMWVersion.V1, "api/v1/sites/{id}/devices",
            "https://eas-all-apim-eus-dev.developer.azure-api.net/api-details#api=devices&operation=get-getsitedevices",
            // 200, 400, 401
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of("Site Guid", "eccdcf03-2ca8-40a9-a5f3-9446a52f515d", "Recursive* (true, false)", "false",
                    "Include Detail* (true, false)", "false"),
            false),
    TREND_DATA_RETRIEVAL(PxMWVersion.V1, "api/v1/devices/timeseries/",
            "https://eas-all-apim-eus-dev.developer.azure-api.net/api-details#api=devices&operation=post-gettimeseriesdata",
            // 200, 400, 401
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of(),
            true),
    COMMANDS(PxMWVersion.V1, "api/v1/devices/{id}/commands/{command_instance_id}",
            "https://adopteriotwebapi.eaton.com/swagger/ui/index#!/Command/Command_GenericDeviceCommand",
            //200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Device Guid", "821d549c-c1b7-469e-bbf5-9d9d401883b2"),
            true),
    DEVICE_DETAIL(PxMWVersion.V1, "api/v1/devices/{deviceId}",
            "https://eas-all-apim-eus-dev.developer.azure-api.net/api-details#api=devices&operation=get-getsitedevices",
            // 200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Device Guid", "b57f1f16-071f-4813-b63f-1eccf9e70dba", "Recursive* (true, false)", "false"),
            false),
    SITES(PxMWVersion.V1, "api/v1/accesscontrol/sites",
            "https://eas-all-apim-eus-dev.developer.azure-api.net/api-details#api=devices&operation=get-getsites",
            // 200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of(),
            false);
    private PxMWVersion version;
    private String suffix;
    private String doc;
    private List<HttpStatus> statuses;
    private Map<String, String> params;
    private boolean hasJsonParam;

    PxMWRetrievalUrl(PxMWVersion version, String suffix, String doc, List<HttpStatus> statuses, Map<String, String> params, boolean hasJsonParam) {
        this.suffix = suffix;
        this.doc = doc;
        this.statuses = statuses;
        this.version = version;
        // * - optional, in () value examples
        this.params = params;
        this.hasJsonParam = hasJsonParam;
    }
    
    public boolean hasJsonParam() {
        return hasJsonParam;
    }
    
    public String getSuffix() {
        return suffix;
    }

    public String getDoc() {
        return doc;
    }

    public List<HttpStatus> getStatuses() {
        return statuses;
    }

    public PxMWVersion getVersion() {
        return version;
    }
    
    public Map<String, String> getParams() {
        return params;
    }
    
    public String getUrl(GlobalSettingDao settingDao, Logger log, RestTemplate restTemplate) {
        String url = settingDao.getString(GlobalSettingType.PX_MIDDLEWARE_URL) + this.getSuffix();
        log.debug("Eaton Cloud URL: {}", url);
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setOutputStreaming(false);
        if (useProxy(url)) {
            YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(httpProxy -> {
                factory.setProxy(httpProxy.getJavaHttpProxy());
            });
        }
        restTemplate.setRequestFactory(factory);
        return url;
    }

    public boolean useProxy(String stringUrl) {
        if ((stringUrl.contains("localhost") || stringUrl.contains("127.0.0.1"))) {
            return false;
        }
        return true;
    }
}

