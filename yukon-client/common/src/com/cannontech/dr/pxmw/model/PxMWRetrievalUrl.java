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
    SECURITY_TOKEN(PxMWVersion.V1, "/api/v1/security/token",
            "https://eas-all-apim-eus-dev.azure-api.net/api/security/serviceAccount/token",
            // 200,401
            List.of(HttpStatus.OK, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of(),
            false),
    DEVICES_BY_SITE_V1(PxMWVersion.V1, "/api/v1/sites/{id}/devices",
            "https://eas-all-apim-eus-dev.developer.azure-api.net/api-details#api=devices&operation=get-getsitedevices",
            // 200, 400, 401
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of("Site Guid", "dd5bf079-b8ea-430c-ad94-1cf54124fc02", "Recursive* (true, false)", "false",
                    "Include Detail* (true, false)", "false"),
            false),
    TREND_DATA_RETRIEVAL(PxMWVersion.V1, "/api/v1/devices/timeseries/",
            "https://eas-all-apim-eus-dev.developer.azure-api.net/api-details#api=devices&operation=post-gettimeseriesdata",
            // 200, 400, 401
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of(),
            true),
    COMMANDS(PxMWVersion.V1, "/api/v1/devices/{id}/commands/{command_instance_id}",
            "https://adopteriotwebapi.eaton.com/swagger/ui/index#!/Command/Command_GenericDeviceCommand",
            //200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Device Guid", "821d549c-c1b7-469e-bbf5-9d9d401883b2",
                    "Command Guid (Unique command instance id used to differentiate every command issued)",
                    "ba84bae0-2e69-4367-9162-6a14039f9bec"),
            true);
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

