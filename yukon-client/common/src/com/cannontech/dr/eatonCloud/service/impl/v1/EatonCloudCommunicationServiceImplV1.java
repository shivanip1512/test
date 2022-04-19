package com.cannontech.dr.eatonCloud.service.impl.v1;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.core.Logger;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.eatonCloud.message.v1.EatonCloudAuthTokenRequestV1;
import com.cannontech.dr.eatonCloud.message.v1.EatonCloudAuthTokenResponseV1;
import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandResponseV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudDeviceDetailV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudErrorHandlerV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSecretValueV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudServiceAccountDetailV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteDevicesV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDataRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDeviceResultV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDeviceV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTokenV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EatonCloudCommunicationServiceImplV1 implements EatonCloudCommunicationServiceV1 {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudCommunicationServiceImplV1.class);

    private RequestReplyTemplate<EatonCloudAuthTokenResponseV1> eatonCloudAuthTokenRequestTemplate;
    @Autowired private ConfigurationSource configSource;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired GlobalSettingDao settingDao;
    @Autowired private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
    private RestTemplate restTemplate;
    private Gson jsonPrinter;

    @PostConstruct
    public void init() {
        YukonJmsTemplate jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.EATON_CLOUD_AUTH_TOKEN);
        jmsTemplate.disableCommLogging();
        eatonCloudAuthTokenRequestTemplate = new RequestReplyTemplateImpl<>(JmsApiDirectory.EATON_CLOUD_AUTH_TOKEN.getName(),
                configSource, jmsTemplate);
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new EatonCloudErrorHandlerV1());
        restTemplate.setMessageConverters(Arrays.asList(mappingJackson2HttpMessageConverter));
        jsonPrinter = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public EatonCloudTokenV1 getToken() throws EatonCloudCommunicationExceptionV1 {
        BlockingJmsReplyHandler<EatonCloudAuthTokenResponseV1> reply = new BlockingJmsReplyHandler<>(EatonCloudAuthTokenResponseV1.class);
        eatonCloudAuthTokenRequestTemplate.send(new EatonCloudAuthTokenRequestV1(), reply);
        try {
            EatonCloudAuthTokenResponseV1 response = reply.waitForCompletion();
            if (response.getError() != null) {
                // got error from Eaton Cloud
                throw response.getError();
            }
            if (response.getToken() != null) {
                return response.getToken();
            }
            throw new EatonCloudCommunicationExceptionV1();
        } catch (ExecutionException e) {
            throw new EatonCloudCommunicationExceptionV1(e);
        }
    }
    
    @Override
    public void clearCache() throws EatonCloudCommunicationExceptionV1 {
        BlockingJmsReplyHandler<EatonCloudAuthTokenResponseV1> reply = new BlockingJmsReplyHandler<>(EatonCloudAuthTokenResponseV1.class);
        eatonCloudAuthTokenRequestTemplate.send(new EatonCloudAuthTokenRequestV1(true), reply);
        try {
           reply.waitForCompletion();
           return;
        } catch (ExecutionException e) {
            throw new EatonCloudCommunicationExceptionV1(e);
        }
    }
 
    @Override
    public EatonCloudSiteDevicesV1 getSiteDevices(String siteGuid, Boolean recursive, Boolean includeDetail)
            throws EatonCloudCommunicationExceptionV1 {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (recursive != null) {
            queryParams.add("recursive", recursive.toString());
        }
        if (includeDetail != null) {
            queryParams.add("includeDetail", includeDetail.toString());
        }

        URI uri = getUri(Map.of("id", siteGuid), EatonCloudRetrievalUrl.DEVICES_BY_SITE);
        uri = addQueryParams(queryParams, uri);

        log.debug("Getting site info. Site Guid: {} URL: {}", siteGuid, uri);

        try {
            HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
            ResponseEntity<EatonCloudSiteDevicesV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                    EatonCloudSiteDevicesV1.class);
            log.debug("Got site info. Site Guid:{} Result:{}", siteGuid,
                    deferredJson(response.getBody()));
            return response.getBody();
        } catch (EatonCloudCommunicationExceptionV1 e) {
            throw e;
        } catch (Exception e) {
            throw new EatonCloudCommunicationExceptionV1(e);
        }
    }
    
    @Override
    public List<EatonCloudSiteV1> getSites(String userGuid) throws EatonCloudCommunicationExceptionV1 {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("userId", userGuid);
        URI uri = getUri(EatonCloudRetrievalUrl.SITES);
        uri = addQueryParams(queryParams, uri);

        log.debug("Getting site list. User Guid: {} URL: {}", userGuid, uri);

        try {
            HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
            ResponseEntity<EatonCloudSiteV1[]> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, EatonCloudSiteV1[].class);
            log.debug("Got site list. User Guid:{} Result:{}", userGuid, deferredJson(response.getBody()));
            return Arrays.asList(response.getBody());
        } catch (EatonCloudCommunicationExceptionV1 e) {
            throw e;
        } catch (Exception e) {
            throw new EatonCloudCommunicationExceptionV1(e);
        }
    }

    @Override
    public List<EatonCloudTimeSeriesDeviceResultV1> getTimeSeriesValues(List<EatonCloudTimeSeriesDeviceV1> deviceList, Range<Instant> range) {
        URI uri = getUri(EatonCloudRetrievalUrl.TREND_DATA_RETRIEVAL);
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        String startTime = fmt.print(range.getMin());
        String stopTime = fmt.print(range.getMax());
        try {
            EatonCloudTimeSeriesDataRequestV1 request = new EatonCloudTimeSeriesDataRequestV1(deviceList, startTime, stopTime);
            HttpEntity<EatonCloudTimeSeriesDataRequestV1> requestEntity = getRequestWithAuthHeaders(request);
            int totalChannels = 0;
            if (log.isDebugEnabled()) {
                for(EatonCloudTimeSeriesDeviceV1 device: deviceList) {
                    totalChannels = totalChannels + Arrays.asList(device.getTagTrait().split(",")).size();
                }
            }
            log.debug("Getting time series data. Request:{} Total Channels:{} Start:{} Stop:{} URL:{}",
                    deferredJson(request), totalChannels, startTime, stopTime, uri);
            ResponseEntity<EatonCloudTimeSeriesDeviceResultV1[]> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity,
                    EatonCloudTimeSeriesDeviceResultV1[].class);
            log.debug("Get time series data. Request:{} Start:{} Stop:{} URL:{} Result:{}",
                    deferredJson(request), startTime, stopTime, uri,
                    deferredJson(response.getBody()));
            return Arrays.asList(response.getBody());
        } catch (EatonCloudCommunicationExceptionV1 e) {
            throw e;
        } catch (Exception e) {
            throw new EatonCloudCommunicationExceptionV1(e);
        }
    }
    
    @Override
    public EatonCloudDeviceDetailV1 getDeviceDetails(String deviceGuid, Boolean recursive)
            throws EatonCloudCommunicationExceptionV1 {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (recursive != null) {
            queryParams.add("recursive", recursive.toString());
        }

        URI uri = getUri(Map.of("deviceId", deviceGuid), EatonCloudRetrievalUrl.DEVICE_DETAIL);
        uri = addQueryParams(queryParams, uri);

        log.debug("Getting device info. Device Guid: {} URL: {}", deviceGuid, uri);

        try {
            HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
            ResponseEntity<EatonCloudDeviceDetailV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                    EatonCloudDeviceDetailV1.class);
            log.debug("Got device info. Device Guid:{} Result:{}", deviceGuid,
                    deferredJson(response.getBody()));
            return response.getBody();
        } catch (EatonCloudCommunicationExceptionV1 e) {
            throw e;
        } catch (Exception e) {
            throw new EatonCloudCommunicationExceptionV1(e);
        }
    }
    
    @Override
    public EatonCloudCommandResponseV1 sendCommand(String deviceGuid, EatonCloudCommandRequestV1 request)
            throws EatonCloudCommunicationExceptionV1 {
        String commandGuid = UUID.randomUUID().toString();
        URI uri = getUri(Map.of("id", deviceGuid, "command_instance_id", commandGuid), EatonCloudRetrievalUrl.COMMANDS);
        log.debug("Sending command to device. Device Guid:{} Command Guid:{} Request:{} URL:{}", deviceGuid, commandGuid,
                deferredJson(request),
                uri);
        try {
            HttpEntity<EatonCloudCommandRequestV1> requestEntity = getRequestWithAuthHeaders(request);
            ResponseEntity<EatonCloudCommandResponseV1> response = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity,
                    EatonCloudCommandResponseV1.class);
            log.debug("Sent command to device. Device Guid:{} Command Guid:{} Response:{}", deviceGuid, commandGuid,
                    deferredJson(response.getBody()));
            return response.getBody();
        } catch (EatonCloudCommunicationExceptionV1 e) {
            throw e;
        } catch (Exception e) {
            throw new EatonCloudCommunicationExceptionV1(e);
        }
    }
    
    @Override
    public EatonCloudServiceAccountDetailV1 getServiceAccountDetail()
            throws EatonCloudCommunicationExceptionV1 {
        String serviceAccountId = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        URI uri = getUri(Map.of("serviceAccountId", serviceAccountId), EatonCloudRetrievalUrl.ACCOUNT_DETAIL);

        log.debug("Getting account info. Service Account: {} URL: {}", serviceAccountId, uri);

        try {
            HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
            ResponseEntity<EatonCloudServiceAccountDetailV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                    EatonCloudServiceAccountDetailV1.class);
            log.debug("Got account info. Service Account:{} Result:{}", serviceAccountId,
                    deferredJson(response.getBody()));
            return response.getBody();
        } catch (EatonCloudCommunicationExceptionV1 e) {
            throw e;
        } catch (Exception e) {
            throw new EatonCloudCommunicationExceptionV1(e);
        }
    }
    
    @Override
    public EatonCloudSecretValueV1 rotateAccountSecret(int secretNumber)
            throws EatonCloudCommunicationExceptionV1 {
        String serviceAccountId = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        String secret = "secret" + secretNumber;
        URI uri = getUri(Map.of("serviceAccountId", serviceAccountId, "secretName", secret),
                EatonCloudRetrievalUrl.ROTATE_ACCOUNT_SECRET);

        log.debug("Rotating {}. Service Account: {} Secret: {} URL: {}", secret, serviceAccountId, uri);

        try {
            HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
            ResponseEntity<EatonCloudSecretValueV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                    EatonCloudSecretValueV1.class);
            log.debug("Rotating {}. Service Account:{} Result:{}", secret, serviceAccountId,
                    deferredJson(response.getBody()));
            return response.getBody();
        } catch (EatonCloudCommunicationExceptionV1 e) {
            throw e;
        } catch (Exception e) {
            throw new EatonCloudCommunicationExceptionV1(e);
        }
    }
       
    /**
     * Creates URI
     */
    private URI getUri(Map<String, String> params, EatonCloudRetrievalUrl url) {
        URI uri = UriComponentsBuilder.fromUriString(url.getUrl(settingDao, log, restTemplate))
                .buildAndExpand(params)
                .toUri();
        return uri;
    }
    
    /**
     * Creates URI
     */
    private URI getUri(EatonCloudRetrievalUrl url) {
        URI uri = UriComponentsBuilder.fromUriString(url.getUrl(settingDao, log, restTemplate))
                .build()
                .toUri();
        return uri;
    }
    /**
     * Adds query params to URI
     */
    private URI addQueryParams(MultiValueMap<String, String> queryParams, URI uri) {
        if (!queryParams.isEmpty()) {
            uri = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParams(queryParams)
                    .build()
                    .toUri();
        }
        return uri;
    }

    private HttpEntity<String> getEmptyRequestWithAuthHeaders() {
        return getRequestWithAuthHeaders("");
    }
    
    private <T> HttpEntity<T> getRequestWithAuthHeaders(T requestObject) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getToken().getToken());
        return new HttpEntity<>(requestObject, headers);
    }
    
    private Object deferredJson(Object element) {
        return new Object() {
            @Override
            public String toString() {
                return jsonPrinter.toJson(element);
            }
        };
    }
}
