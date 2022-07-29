package com.cannontech.web.api.der.edge.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.edgeDr.EdgeDrDataNotification;
import com.cannontech.dr.edgeDr.EdgeDrErrorHandler;
import com.cannontech.dr.edgeDr.EdgeDrWebhookRequest;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.web.api.der.edge.service.DerEdgeResponseService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class DerEdgeResponseServiceImpl implements DerEdgeResponseService {
    private static final Logger log = YukonLogManager.getLogger(DerEdgeResponseServiceImpl.class);

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private PaoDao paodao;
    @Autowired ThriftByteDeserializer<EdgeDrDataNotification> edgeDrDataNotificationDeserializer;
    @Autowired private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Gson jsonPrinter;
    private RestTemplate apiRestTemplate;
    private LoadingCache<Integer, String> e2eIdToGuidCache;
    private static final int cacheExperationMinutes = 30;
    URI uri;

    @PostConstruct
    public void initialize() {

        apiRestTemplate = new RestTemplate();
        apiRestTemplate.setErrorHandler(new EdgeDrErrorHandler());
        apiRestTemplate.setMessageConverters(Arrays.asList(mappingJackson2HttpMessageConverter));
        jsonPrinter = new GsonBuilder().setPrettyPrinting().create();

        try {
            uri = new URI(configurationSource.getString(MasterConfigString.SETO_WEBHOOK_URL, ""));
        } catch (URISyntaxException e) {
            log.warn("caught exception in initialize", e);
        }

        e2eIdToGuidCache = CacheBuilder.newBuilder().concurrencyLevel(2)
                .expireAfterWrite(cacheExperationMinutes, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, String>() {

                    @Override
                    public String load(Integer e2eID) throws Exception {
                        return e2eIdToGuidCache.getIfPresent(e2eID);
                    }
                });
    }

    public void addCacheEntry(Integer e2eID, String guid) {
        e2eIdToGuidCache.put(e2eID, guid);
    }

    // Process messages from the edgeDrDataNotification thrift queue
    private void processNotification(byte[] thriftMessage) {

        // Read stuff off the edgeDrDataNotification thrift queue
        EdgeDrDataNotification edgeDrDataNotification = edgeDrDataNotificationDeserializer.fromBytes(thriftMessage);
        // Log initial thing
        log.info("Received EdgeDrDataNotification {} from porter", edgeDrDataNotification);

        if (!uri.getPath().isBlank()) {
            // Get the LiteYukonPAObject
            LiteYukonPAObject liteYukonPao = paodao.getLiteYukonPAO(edgeDrDataNotification.getPaoId());
            // Create the new object pieces to fire off
            String token = e2eIdToGuidCache.getIfPresent(edgeDrDataNotification.getE2eId());
            String name = liteYukonPao.getPaoName();
            String type = liteYukonPao.getPaoType().getDbString();
            String payload = new String(edgeDrDataNotification.getPayload(), StandardCharsets.UTF_8);
            String errorMsg = edgeDrDataNotification.getError().toString();

            // Build requestEntity
            EdgeDrWebhookRequest edgeDrWebhookRequest = new EdgeDrWebhookRequest(token, name, type, payload, errorMsg);
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<EdgeDrWebhookRequest> requestEntity = new HttpEntity<>(edgeDrWebhookRequest, headers);
            try {
                log.debug("EdgeDrWebhookRequest as JSON {}", jsonPrinter.toJson(edgeDrWebhookRequest));
                log.debug("Sending message {} to URL: {}", requestEntity, uri);
                ResponseEntity<? extends Object> response = apiRestTemplate.exchange(uri, HttpMethod.POST, requestEntity,
                        Object.class);
                log.info("Received response from Central Controller {}", response);
                // acknowledge success - Log it
            } catch (Exception e) {
                log.error("Exception sending EdgeDrWebhookRequest", e);
            }
        } else {
            log.warn("Unable to send data to central controller when URL is not set, please check your SETO_WEBHOOK_URL");
        }

    }
}
