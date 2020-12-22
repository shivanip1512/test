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
            // used by simulator
            "https://adopteriotwebapi.eaton.com/swagger/ui/index#!/Security/Security_GetSecurityToken",
            //200,401,403
            List.of(HttpStatus.OK, HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN),
            ImmutableMap.of()),
    DEVICE_PROFILE_BY_GUID_V1(PxMWVersion.V1, "/api/v1/deviceprofile/{id}",
            // used by simulator
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#device-profile",
            //200
            List.of(HttpStatus.OK),
            ImmutableMap.of("Profile Guid", "08bc1c6f-f4fd-43c5-8797-cba3a7b5d625")),
    DEVICES_BY_SITE_V1(PxMWVersion.V1, "/api/v1/sites/{id}/devices",
            // used by simulator
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#site-site-get-1",
            //200, 404
            List.of(HttpStatus.OK, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Site Guid", "dd5bf079-b8ea-430c-ad94-1cf54124fc02", "Recursive* (true, false)", "false",
                    "Include Detail* (true, false)", "false")),
    DEVICE_TIMESERIES_LATEST(PxMWVersion.V1, "/api/v1/devices/{id}/timeseries/latest",
            // used by simulator
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#timeseries-timeseries-get",
            //200, 404
            List.of(HttpStatus.OK, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Device Guid", "3b4dd0db-2144-4fb2-a819-99f7f0a4d5cf", "List of Channel Tags", "10230,10231")),
    DEVICE_GET_CHANNEL_VALUES_V1(PxMWVersion.V1, "/api/v1/devices/{id}/commands/getchannelvalues",
            // used by simulator
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#command-command-put-2",
            // 200, 400, 401, 404, 501, 503, 400 (skip), 500
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    HttpStatus.INTERNAL_SERVER_ERROR),
            // 400: Resend, or 500: Terminated. The device determines the appropriate response code.The last BAD_REQUEST and
            // INTERNAL_SERVER_ERROR returned in response body
            // For the simulator we are not returning 400 as BAD_REQUEST but as a 200 with 400 in the response body
            ImmutableMap.of("Device Guid", "c2c6460b-3a2a-48c5-af03-4326d6598284", "List of Channel Tags", "1123,1124")),
    CLOUD_ENABLE(PxMWVersion.V1, "/api/v1/devices/cloudenable",
            // used by simulator
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#device-device-put-1",
            //200, 404
            List.of(HttpStatus.OK, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Device Guid", "ba55b347-cbc9-4a75-81bb-48058b0aa887", "Enable(true)/Disable(false)", "true"));

    private PxMWVersion version;
    private String suffix;
    private String doc;
    private List<HttpStatus> statuses;
    private Map<String, String> params;

    PxMWRetrievalUrl(PxMWVersion version, String suffix, String doc, List<HttpStatus> statuses, Map<String, String> params) {
        this.suffix = suffix;
        this.doc = doc;
        this.statuses = statuses;
        this.version = version;
        // * - optional, in () value examples
        this.params = params;
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

