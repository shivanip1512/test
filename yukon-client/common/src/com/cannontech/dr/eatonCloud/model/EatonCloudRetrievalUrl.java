package com.cannontech.dr.eatonCloud.model;

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
public enum EatonCloudRetrievalUrl {
    SECURITY_TOKEN(EatonCloudVersion.V1, "/v1/security/serviceaccount/token",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=security&operation=post-getserviceaccounttoken",
            // 200,401
            List.of(HttpStatus.OK, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of(),
            false,
            false),
    DEVICES_BY_SITE(EatonCloudVersion.V1, "/v1/sites/{id}/devices",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=devices&operation=get-getsitedevices",
            // 200, 400, 401
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of("Site Guid", "eccdcf03-2ca8-40a9-a5f3-9446a52f515d", "Recursive* (true, false)", "false",
                    "Include Detail* (true, false)", "false"),
            false,
            false),
    TREND_DATA_RETRIEVAL(EatonCloudVersion.V1, "/v1/devices/timeseries/",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=devices&operation=post-gettimeseriesdata",
            // 200, 400, 401
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of(),
            true,
            true),
    COMMANDS(EatonCloudVersion.V1, "/v1/devices/{id}/commands/{command_instance_id}",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=devices&operation=put-senddevicecommand",
            //200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Device Guid", "821d549c-c1b7-469e-bbf5-9d9d401883b2"),
            true, 
            true),
    DEVICE_DETAIL(EatonCloudVersion.V1, "/v1/devices/{deviceId}",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=devices&operation=get-getdevicedetails-1",
            // 200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Device Guid", "b57f1f16-071f-4813-b63f-1eccf9e70dba", "Recursive* (true, false)", "false"),
            false,
            false),
    SITES(EatonCloudVersion.V1, "/v1/accesscontrol/sites",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=devices&operation=get-getsites",
            // 200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of(),
            false,
            false),
    ACCOUNT_DETAIL(EatonCloudVersion.V1, "/v1/security/serviceaccount/{serviceAccountId}",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=security&operation=get-getserviceaccountdetail",
            // 200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Service Account Guid", "beaeae03-0178-4b45-80fd-98321f43734f"),
            false,
            false),
    ROTATE_ACCOUNT_SECRET(EatonCloudVersion.V1, "/v1/security/serviceaccount/{serviceAccountId}/secret/{secretName}/rotate",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=security&operation=get-rotateserviceaccountsecret",
            //200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Service Account Guid", "821d549c-c1b7-469e-bbf5-9d9d401883b2", "Secret Name", "secret1"),
            false, 
            false);
    private EatonCloudVersion version;
    private String suffix;
    private String doc;
    private List<HttpStatus> statuses;
    private Map<String, String> params;
    private boolean hasJsonParam;
    //displays success percentage entry field
    private boolean successPercentage;

    EatonCloudRetrievalUrl(EatonCloudVersion version, String suffix, String doc, List<HttpStatus> statuses,
            Map<String, String> params, boolean hasJsonParam, boolean successPercentage) {
        this.suffix = suffix;
        this.doc = doc;
        this.statuses = statuses;
        this.version = version;
        // * - optional, in () value examples
        this.params = params;
        this.hasJsonParam = hasJsonParam;
        this.successPercentage = successPercentage;
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

    public EatonCloudVersion getVersion() {
        return version;
    }
    
    public Map<String, String> getParams() {
        return params;
    }
    
    public String getUrl(GlobalSettingDao settingDao, Logger log, RestTemplate restTemplate) {
        String url = settingDao.getString(GlobalSettingType.EATON_CLOUD_URL) + this.getSuffix();
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

    public boolean displaySuccessPercentage() {
        return successPercentage;
    }
}

