package com.cannontech.dr.pxmw.model;

import java.util.List;

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
            "https://adopteriotwebapi.eaton.com/swagger/ui/index#!/Security/Security_GetSecurityToken",
            List.of(HttpStatus.OK, HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN)),
    DEVICE_PROFILE_BY_GUID_V1(PxMWVersion.V1, "/api/v1/deviceprofile/{id}",
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#device-profile",
            List.of(HttpStatus.OK)),
    DEVICES_BY_SITE_V1(PxMWVersion.V1, "/api/v1/sites/{id}/devices",
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#site-site-get-1",
            List.of(HttpStatus.OK, HttpStatus.NOT_FOUND)),
    DEVICE_CHANNEL_DETAILS_V1(PxMWVersion.V1, "/api/v1/device/{deviceId}/channels",
            "https://was-all-apim-eus-dev.portal.azure-api.net/docs/services/device-management-function-app/operations/get-getdevicechanneldetails-1?",
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND, HttpStatus.FORBIDDEN));

    private PxMWVersion version;
    private String suffix;
    private String doc;
    private List<HttpStatus> statuses;

    PxMWRetrievalUrl(PxMWVersion version, String suffix, String doc, List<HttpStatus> statuses) {
        this.suffix = suffix;
        this.doc = doc;
        this.statuses = statuses;
        this.version = version;
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
    
    public String getUrl(GlobalSettingDao settingDao, Logger log, RestTemplate restTemplate) {
        String url = settingDao.getString(GlobalSettingType.PX_MIDDLEWARE_URL) + this.getSuffix();
        log.debug("{}", url);
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
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

