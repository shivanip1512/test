package com.cannontech.dr.pxmw.service.impl.v1;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import com.cannontech.dr.pxmw.message.PxMWAuthTokenRequest;
import com.cannontech.dr.pxmw.message.PxMWAuthTokenResponse;
import com.cannontech.dr.pxmw.model.PxMWException;
import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceChannelDetailsV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceProfileV1;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorHandlerV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.gson.GsonBuilder;

public class PxMWCommunicationServiceImplV1 implements PxMWCommunicationServiceV1 {

    private static final Logger log = YukonLogManager.getLogger(PxMWCommunicationServiceImplV1.class);

    private RequestReplyTemplate<PxMWAuthTokenResponse> pXMWAuthTokenRequestTemplate;
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
        BlockingJmsReplyHandler<PxMWAuthTokenResponse> reply = new BlockingJmsReplyHandler<>(PxMWAuthTokenResponse.class);
        PxMWAuthTokenRequest request = new PxMWAuthTokenRequest();
        pXMWAuthTokenRequestTemplate.send(request, reply);
        try {
            PxMWAuthTokenResponse response = reply.waitForCompletion();
            if (response.getError() != null) {
                // got error from PX White
                throw (PxMWCommunicationExceptionV1) response.getError();
            }
            if (response.getToken() != null) {
                return (PxMWTokenV1) response.getToken();
            }
            throw new PxMWException("Unable to get to PX White token from SM, see SM log for details");
        } catch (ExecutionException e) {
            log.error("Error getting PX White token", e);
            throw new PxMWException("Unable to send a message to SM to get a PX White token", e);
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
    public PxMWDeviceChannelDetailsV1 getDeviceChannelDetails(String deviceGuid)
            throws PxMWCommunicationExceptionV1, PxMWException {
        URI uri = getUri(Map.of("deviceId", deviceGuid), PxMWRetrievalUrl.DEVICE_CHANNEL_DETAILS_V1);
        log.debug("Getting device channel details. Device Guid: {} URL:{} token:{}", deviceGuid, uri);
        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
        ResponseEntity<PxMWDeviceChannelDetailsV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                PxMWDeviceChannelDetailsV1.class);
        log.debug("Got device channel. Device Guid:{} Result:{}", deviceGuid,
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
