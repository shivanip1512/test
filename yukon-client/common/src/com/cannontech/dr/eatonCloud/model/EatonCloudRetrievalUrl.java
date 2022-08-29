package com.cannontech.dr.eatonCloud.model;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ImmutableMap;

//Data retrieval URLs
public enum EatonCloudRetrievalUrl {
    SECURITY_TOKEN(EatonCloudVersion.V1, "/v1/security/serviceaccount/token",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=security&operation=post-getserviceaccounttoken",
            // 200,401
            List.of(HttpStatus.OK, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of("Using secret1", "Using secret1"),
            false,
            true,
            true,
            false),
    SECURITY_TOKEN2(EatonCloudVersion.V1, "/v1/security/serviceaccount/token",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=security&operation=post-getserviceaccounttoken",
            // 200,401
            List.of(HttpStatus.OK, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of("Using secret2", "Using secret2"),
            false,
            true,
            false,
            false),
    DEVICES_BY_SITE(EatonCloudVersion.V1, "/v1/sites/{id}/devices",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=devices&operation=get-getsitedevices",
            // 200, 400, 401
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of("Site Guid", "eccdcf03-2ca8-40a9-a5f3-9446a52f515d", "Recursive* (true, false)", "false",
                    "Include Detail* (true, false)", "false"),
            false,
            false,
            true,
            false),
    TREND_DATA_RETRIEVAL(EatonCloudVersion.V1, "/v1/devices/timeseries/",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=devices&operation=post-gettimeseriesdata",
            // 200, 400, 401
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED),
            ImmutableMap.of(),
            true,
            true,
            true,
            false),
    COMMANDS(EatonCloudVersion.V1, "/v1/devices/{id}/commands/{command_instance_id}",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=devices&operation=put-senddevicecommand",
            //200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Device Guid", "821d549c-c1b7-469e-bbf5-9d9d401883b2"),
            true, 
            true,
            true,
            false),
    DEVICE_DETAIL(EatonCloudVersion.V1, "/v1/devices/{deviceId}",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=devices&operation=get-getdevicedetails-1",
            // 200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Device Guid", "b57f1f16-071f-4813-b63f-1eccf9e70dba", "Recursive* (true, false)", "false"),
            false,
            false,
            true,
            false),
    SITES(EatonCloudVersion.V1, "/v1/accesscontrol/sites",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=devices&operation=get-getsites",
            // 200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of(),
            false,
            false,
            true,
            false),
    ACCOUNT_DETAIL(EatonCloudVersion.V1, "/v1/security/serviceaccount/{serviceAccountId}",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=security&operation=get-getserviceaccountdetail",
            // 200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Service Account Guid", "beaeae03-0178-4b45-80fd-98321f43734f"),
            false,
            false,
            true,
            false),
    ROTATE_ACCOUNT_SECRET(EatonCloudVersion.V1, "/v1/security/serviceaccount/{serviceAccountId}/secret/{secretName}/rotate",
            "https://eas-dev.eastus.cloudapp.azure.com/api-details#api=security&operation=get-rotateserviceaccountsecret",
            //200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Use secret rotation UI for testing", "Use secret rotation UI for testing"),
            false, 
            true,
            false,
            false),
    JOB(EatonCloudVersion.V1, "/v1/job/immediate",
            "https://confluence-prod.tcc.etn.com/display/EASCBCD/Immediate+Job+API+proposal",
            //200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of(),
            true, 
            false,
            true,
            false),
    JOB_STATUS(EatonCloudVersion.V1, "/v1/job/immediate/{id}",
            "https://confluence-prod.tcc.etn.com/display/EASCBCD/Immediate+Job+API+proposal",
            //200, 400, 401, 404
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND),
            ImmutableMap.of("Job Guid", "Enter job guid to get status"),
            false, 
            true,
            true,
            true);
    private EatonCloudVersion version;
    private String suffix;
    private String doc;
    private List<HttpStatus> statuses;
    private Map<String, String> params;
    private boolean hasJsonParam;
    //displays success percentage entry field
    private boolean successPercentage;
    private boolean showTestButton;
    private boolean unknownPercentage;

    EatonCloudRetrievalUrl(EatonCloudVersion version, String suffix, String doc, List<HttpStatus> statuses,
            Map<String, String> params, boolean hasJsonParam, boolean successPercentage, boolean showTestButton, boolean unknownPercentage) {
        this.suffix = suffix;
        this.doc = doc;
        this.statuses = statuses;
        this.version = version;
        // * - optional, in () value examples
        this.params = params;
        this.hasJsonParam = hasJsonParam;
        this.successPercentage = successPercentage;
        this.showTestButton = showTestButton;
        this.unknownPercentage = unknownPercentage;
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
    
    public String getUrl(GlobalSettingDao settingDao, RestTemplate restTemplate) {
        String url = settingDao.getString(GlobalSettingType.EATON_CLOUD_URL) + this.getSuffix();
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

    public boolean showTestButton() {
        return showTestButton;
    }
    
    public boolean displayUnknownPercentage() {
        return unknownPercentage;
    }
}
