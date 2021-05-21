package com.cannontech.dr.pxmw.service.impl.v1;

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
import com.cannontech.dr.pxmw.message.PxMWAuthTokenRequestV1;
import com.cannontech.dr.pxmw.message.v1.PxMWAuthTokenResponseV1;
import com.cannontech.dr.pxmw.model.PxMWException;
import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceDetail;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorHandlerV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteDevicesV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceV1;
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
    public PxMWSiteDevicesV1 getSiteDevices(String siteGuid, Boolean recursive, Boolean includeDetail)
            throws PxMWCommunicationExceptionV1, PxMWException {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (recursive != null) {
            queryParams.add("recursive", recursive.toString());
        }
        if (includeDetail != null) {
            queryParams.add("includeDetail", includeDetail.toString());
        }

        URI uri = getUri(Map.of("id", siteGuid), PxMWRetrievalUrl.DEVICES_BY_SITE);
        uri = addQueryParams(queryParams, uri);

        log.debug("Getting site info. Site Guid: {} URL: {}", siteGuid, uri);

        try {
            HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
            ResponseEntity<PxMWSiteDevicesV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, PxMWSiteDevicesV1.class);
            log.debug("Got site info. Site Guid:{} Result:{}", siteGuid,
                    new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
            return response.getBody();
        } catch (PxMWCommunicationExceptionV1 | PxMWException e) {
            throw e;
        } catch (Exception e) {
            throw new PxMWException("Exception occured while getting site devices", e);
        }
    }
    
    @Override
    public List<PxMWSiteV1> getSites(String siteGuid) throws PxMWCommunicationExceptionV1, PxMWException {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("userId", siteGuid);
        URI uri = getUri(PxMWRetrievalUrl.SITES);
        uri = addQueryParams(queryParams, uri);

        log.debug("Getting site info. Site Guid: {} URL: {}", siteGuid, uri);

        try {
            HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
            ResponseEntity<PxMWSiteV1[]> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, PxMWSiteV1[].class);
            log.debug("Got site info. Site Guid:{} Result:{}", siteGuid,
                    new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
            return Arrays.asList(response.getBody());
        } catch (PxMWCommunicationExceptionV1 | PxMWException e) {
            throw e;
        } catch (Exception e) {
            throw new PxMWException("Exception occured while getting site devices", e);
        }
    }

    @Override
    public List<PxMWTimeSeriesDeviceResultV1> getTimeSeriesValues(List<PxMWTimeSeriesDeviceV1> deviceList, Range<Instant> range) {
        URI uri = getUri(PxMWRetrievalUrl.TREND_DATA_RETRIEVAL);
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        String startTime = fmt.print(range.getMin());
        String stopTime = fmt.print(range.getMax());
        try {
            PxMWTimeSeriesDataRequestV1 request = new PxMWTimeSeriesDataRequestV1(deviceList, startTime, stopTime);
            HttpEntity<PxMWTimeSeriesDataRequestV1> requestEntity = getRequestWithAuthHeaders(request);
            log.debug("Getting time series data. Request:{} Start:{} Stop:{} URL:{}",
                    new GsonBuilder().setPrettyPrinting().create().toJson(request), startTime, stopTime, uri);
            ResponseEntity<PxMWTimeSeriesDeviceResultV1[]> response = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity,
                    PxMWTimeSeriesDeviceResultV1[].class);
            log.debug("Get time series data. Request:{} Start:{} Stop:{} URL:{} Result:{}",
                    new GsonBuilder().setPrettyPrinting().create().toJson(request), startTime, stopTime, uri,
                    new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
            return Arrays.asList(response.getBody());
        } catch (PxMWCommunicationExceptionV1 | PxMWException e) {
            throw e;
        } catch (Exception e) {
            throw new PxMWException("Exception occured while getting time series data", e);
        }
    }
    
    @Override
    public void sendCommand(String deviceGuid, PxMWCommandRequestV1 request)
            throws PxMWCommunicationExceptionV1, PxMWException {
        String commandGuid = UUID.randomUUID().toString();
        URI uri = getUri(Map.of("id", deviceGuid, "command_instance_id", commandGuid), PxMWRetrievalUrl.COMMANDS);
        log.debug("Sending command to device. Device Guid:{} Command Guid:{} Request:{} URL:{}", deviceGuid, commandGuid,
                new GsonBuilder().setPrettyPrinting().create().toJson(request),
                uri);
        try {
            HttpEntity<PxMWCommandRequestV1> requestEntity = getRequestWithAuthHeaders(request);
            ResponseEntity<PxMWCommandResponseV1> response = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity,
                    PxMWCommandResponseV1.class);
            log.debug("Sent command to device. Device Guid:{} Command Guid:{} Response:{}", deviceGuid, commandGuid,
                    new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
        } catch (PxMWCommunicationExceptionV1 | PxMWException e) {
            throw e;
        } catch (Exception e) {
            throw new PxMWException("Exception occured while sending command", e);
        }

    }
    
    @Override
    public PxMWDeviceDetail getDeviceDetails(String deviceGuid, Boolean recursive)
            throws PxMWCommunicationExceptionV1, PxMWException {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (recursive != null) {
            queryParams.add("recursive", recursive.toString());
        }

        URI uri = getUri(Map.of("deviceId", deviceGuid), PxMWRetrievalUrl.DEVICE_DETAIL);
        uri = addQueryParams(queryParams, uri);

        log.debug("Getting device info. Device Guid: {} URL: {}", deviceGuid, uri);

        try {
            HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders();
            ResponseEntity<PxMWDeviceDetail> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, PxMWDeviceDetail.class);
            log.debug("Got device info. Device Guid:{} Result:{}", deviceGuid,
                    new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
            return response.getBody();
        } catch (PxMWCommunicationExceptionV1 | PxMWException e) {
            throw e;
        } catch (Exception e) {
            throw new PxMWException("Exception occured while getting device detail", e);
        }
    }
    
    @Override
    public boolean isCreatableDevice(String deviceGuid) {
        try {
            getDeviceDetails(deviceGuid, null);
            return true;
        } catch (Exception e) {
            //404 Not Found if device doesn't exist
            log.error("Device:" + deviceGuid + " can't be created", e);
            return false;
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
