package com.cannontech.dr.pxmw.service.impl.v1;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.pxmw.message.PxMWAuthTokenRequestV1;
import com.cannontech.dr.pxmw.message.v1.PxMWAuthTokenResponseV1;
import com.cannontech.dr.pxmw.model.PxMWException;
import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.dr.pxmw.model.v1.PxMWChannelValueV1;
import com.cannontech.dr.pxmw.model.v1.PxMWChannelValuesRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWChannelValuesV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceProfileV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceTimeseriesLatestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorHandlerV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.gson.GsonBuilder;

public class PxMWCommunicationServiceImplV1 implements PxMWCommunicationServiceV1 {

    private static final Logger log = YukonLogManager.getLogger(PxMWCommunicationServiceImplV1.class);

    private RequestReplyTemplate<PxMWAuthTokenResponseV1> pXMWAuthTokenRequestTemplate;
    @Autowired private ConfigurationSource configSource;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired GlobalSettingDao settingDao;
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        YukonJmsTemplate jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.PX_MW_AUTH_TOKEN);
        pXMWAuthTokenRequestTemplate = new RequestReplyTemplateImpl<>(JmsApiDirectory.PX_MW_AUTH_TOKEN.getName(),
                configSource, jmsTemplate);
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new PxMWErrorHandlerV1());
    }

    @Override
    public PxMWTokenV1 getToken() throws PxMWCommunicationExceptionV1, PxMWException {
        BlockingJmsReplyHandler<PxMWAuthTokenResponseV1> reply = new BlockingJmsReplyHandler<>(PxMWAuthTokenResponseV1.class);
        pXMWAuthTokenRequestTemplate.send(new PxMWAuthTokenRequestV1(), reply);
        try {
            PxMWAuthTokenResponseV1 response = reply.waitForCompletion();
            if (response.getError() != null) {
                // got error from Eaton Cloud
                throw response.getError();
            }
            if (response.getToken() != null) {
                return response.getToken();
            }
            throw new PxMWException("Unable to get Eaton Cloud token from SM, see SM log for details");
        } catch (ExecutionException e) {
            throw new PxMWException("Unable to send a message to SM to get Eaton Cloud token", e);
        }
    }
    
    @Override
    public void clearCache() throws PxMWException {
        BlockingJmsReplyHandler<PxMWAuthTokenResponseV1> reply = new BlockingJmsReplyHandler<>(PxMWAuthTokenResponseV1.class);
        pXMWAuthTokenRequestTemplate.send(new PxMWAuthTokenRequestV1(true), reply);
        try {
           reply.waitForCompletion();
           return;
        } catch (ExecutionException e) {
            throw new PxMWException("Unable to send a message to SM to clear cache", e);
        }
    }
    
    @Override
    public PxMWDeviceProfileV1 getDeviceProfile(String deviceProfileGuid) throws PxMWCommunicationExceptionV1, PxMWException {
        URI uri = getUri(Map.of("id", deviceProfileGuid), PxMWRetrievalUrl.DEVICE_PROFILE_BY_GUID_V1);
        log.debug("Getting device profile. Device Profile Guid: {} URL:{}", deviceProfileGuid, uri);
        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
        ResponseEntity<PxMWDeviceProfileV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                PxMWDeviceProfileV1.class);
        log.debug("Got device profile. Device Profile Guid:{} Result:{}", deviceProfileGuid,
                new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
        return response.getBody();
    }

    @Override
    public PxMWDeviceTimeseriesLatestV1 getTimeseriesLatest(String deviceGuid, List<String> tags)
            throws PxMWCommunicationExceptionV1, PxMWException {
        URI uri = getUri(Map.of("id", deviceGuid), PxMWRetrievalUrl.DEVICE_TIMESERIES_LATEST);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("tags", StringUtils.join(tags, ','));
        uri = addQueryParams(queryParams, uri);

        log.debug("Getting device timeseries latest. Device Guid:{} Tags:{} URL:{}", deviceGuid, tags, uri);
        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
        ResponseEntity<PxMWDeviceTimeseriesLatestV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                PxMWDeviceTimeseriesLatestV1.class);
        log.debug("Got device timeseries latest. Device Guid:{} Tags:{} Result:{}", deviceGuid, tags,
                new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
        return response.getBody();
    }

    @Override
    public PxMWSiteV1 getSite(String siteGuid, Boolean recursive, Boolean includeDetail)
            throws PxMWCommunicationExceptionV1, PxMWException {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (recursive != null) {
            queryParams.add("recursive", recursive.toString());
        }
        if (includeDetail != null) {
            queryParams.add("includeDetail", includeDetail.toString());
        }

        URI uri = getUri(Map.of("id", siteGuid), PxMWRetrievalUrl.DEVICES_BY_SITE_V1);
        uri = addQueryParams(queryParams, uri);

        log.debug("Getting site info. Site Guid: {} URL: {}", siteGuid, uri);

        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
        ResponseEntity<PxMWSiteV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, PxMWSiteV1.class);
        log.debug("Got site info. Site Guid:{} Result:{}", siteGuid,
                new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
        return response.getBody();
    }
    
    @Override
    public void cloudEnable(String deviceGuid, boolean enable)
            throws PxMWCommunicationExceptionV1, PxMWException {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("id", deviceGuid);
        queryParams.add("state", String.valueOf(enable));

        URI uri = getUri(PxMWRetrievalUrl.CLOUD_ENABLE);
        uri = addQueryParams(queryParams, uri);

        log.debug("Cloud enable. Device Guid: {} Enable{} URL: {}", deviceGuid, enable, uri);
        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, String.class);
        log.info("Cloud enable. Device Guid: {} Enable{} Result: {}", deviceGuid, response.getBody());
    }
    
    @Override
    public List<PxMWChannelValueV1> getChannelValues(String deviceGuid, List<String> tags)
            throws PxMWCommunicationExceptionV1, PxMWException {
        URI uri = getUri(Map.of("id", deviceGuid), PxMWRetrievalUrl.DEVICE_GET_CHANNEL_VALUES_V1);
        log.debug("Getting device channel values. Device Guid:{} URL:{}", deviceGuid, uri);
        try {
            HttpEntity<PxMWChannelValuesRequestV1> requestEntity = getRequestWithAuthHeaders(
                    new PxMWChannelValuesRequestV1(tags));
            ResponseEntity<PxMWChannelValuesV1> response = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity,
                    PxMWChannelValuesV1.class);
            int status = Integer.parseInt(response.getBody().getStatus());
            log.debug("Getting device channel values. Device Guid:{} Response Status:{} Result:{}", deviceGuid, status,
                    new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
            HttpStatus httpStatus = HttpStatus.valueOf(status);
            if (httpStatus == HttpStatus.OK) {
                return response.getBody().getValues();
            }
            throw new PxMWException(httpStatus.value(), status + ":" + response.getBody().getMsg());
        } catch (PxMWCommunicationExceptionV1 | PxMWException e) {
            throw e;
        } catch (Exception e) {
            throw new PxMWException("Exception occured while getting channel values", e);
        }
    }
    
    @Override
    public void sendCommand(String deviceGuid, String commandGuid, PxMWCommandRequestV1 request)
            throws PxMWCommunicationExceptionV1, PxMWException {
        URI uri = getUri(Map.of("id", deviceGuid, "command_instance_id", commandGuid), PxMWRetrievalUrl.COMMANDS);
        log.debug("Sending command to device. Device Guid:{} Command Guid:{} Request:{} URL:{}", deviceGuid, commandGuid,
                new GsonBuilder().setPrettyPrinting().create().toJson(request),
                uri);
        try {
            HttpEntity<PxMWCommandRequestV1> requestEntity = getRequestWithAuthHeaders(request);
            ResponseEntity<PxMWCommandResponseV1> response = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity,
                    PxMWCommandResponseV1.class);
            log.info("Sent command to device. Device Guid:{} Command Guid:{} Response:{}", deviceGuid, commandGuid,
                    new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
        } catch (PxMWCommunicationExceptionV1 | PxMWException e) {
            throw e;
        } catch (Exception e) {
            throw new PxMWException("Exception occured while getting channel values", e);
        }
    }

    /**
     * Creates URI
     */
    private URI getUri(Map<String, String> params, PxMWRetrievalUrl url) {
        URI uri = UriComponentsBuilder.fromUriString(url.getUrl(settingDao, log, restTemplate))
                .buildAndExpand(params)
                .toUri();
        return uri;
    }
    
    /**
     * Creates URI
     */
    private URI getUri(PxMWRetrievalUrl url) {
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
}
