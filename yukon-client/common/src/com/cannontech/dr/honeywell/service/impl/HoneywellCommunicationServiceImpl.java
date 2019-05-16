package com.cannontech.dr.honeywell.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.dr.honeywell.HoneywellCommunicationException;
import com.cannontech.dr.honeywell.message.AccessControlListItemRequest;
import com.cannontech.dr.honeywell.message.DREventRequest;
import com.cannontech.dr.honeywell.message.DutyCyclePeriod;
import com.cannontech.dr.honeywell.service.HoneywellCommunicationService;
import com.cannontech.dr.honeywellWifi.model.HoneywellDREvent;
import com.cannontech.dr.honeywellWifi.model.HoneywellWifiDutyCycleDrParameters;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;
import com.cannontech.stars.dr.hardware.dao.HoneywellWifiThermostatDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;

public class HoneywellCommunicationServiceImpl implements HoneywellCommunicationService {

    private static final Logger log = YukonLogManager.getLogger(HoneywellCommunicationServiceImpl.class);

    @Autowired private @Qualifier("honeywell") RestOperations restTemplate;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private HoneywellWifiThermostatDao honeywellDao;
    @Autowired private EncryptedRouteDao encryptedRouteDao;

    private static final String createDREventGroupUrlPart = "webapi/api/drEventGroups/";
    private static final String getGatewayByMacIdUrlPart = "webapi/api/gateways";
    private static final String cancelDREventForDevicesUrlPart = "webapi/api/drEvents/optout/";
    private static final String removeDeviceFromDRGroupUrlPart = "WebAPI/api/drEventGroups/";
    private static final String drEventForGroupUrlPart = "WebAPI/api/drEvents";
    private static final String registerDeviceOnACLUrlPart = "WebAPI/api/accessControlList";
    private static final String drEventsForDeviceUrlPart = "WebAPI/api/drEvents/";
    
    private static final String APPLICATION_JSON = "application/json";

    @Override
    public void registerDevice(String macAddress, Integer deviceVendorUserId) {

        log.debug("Registering honeyWell device with Mac Address " + macAddress);
        try {
            int deviceId = getGatewayDetailsForMacId(macAddress, String.valueOf(deviceVendorUserId));
            String url = getUrlBase() + registerDeviceOnACLUrlPart + "?userId=" + deviceVendorUserId;
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

            AccessControlListItemRequest accessControlListItemRequest = new AccessControlListItemRequest(deviceId, true);
            List<AccessControlListItemRequest> controlListItemRequests = new ArrayList<>();
            controlListItemRequests.add(accessControlListItemRequest);

            String body = JsonUtils.toJson(controlListItemRequests);
            HttpHeaders httpHeaders = getHttpHeaders(url, HttpMethod.PUT, body);

            httpHeaders.add("userId", String.valueOf(deviceVendorUserId));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(body, httpHeaders);

            HttpEntity<String> response =
                restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.PUT, requestEntity, String.class);
            log.debug(response);

        } catch (RestClientException | JsonProcessingException ex) {
            log.error("Unable to register Honeywell device in Access Control List. Message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to register honeywell device. Message: \""
                + ex.getMessage() + "\".");
        }
    }

    @Override
    public void deleteDevice(String macAddress, Integer deviceVendorUserId) {
        var endpoint = getUrlBase() + registerDeviceOnACLUrlPart;
        log.debug("Deleting honeyWell device with Mac Address " + macAddress);
        try {
            var userId = Integer.toString(deviceVendorUserId);
            int deviceId = getGatewayDetailsForMacId(macAddress, userId);
            var builder = new URIBuilder(endpoint);
            
            builder.addParameter("userId", userId);
            builder.addParameter("deviceId", Integer.toString(deviceId));
            builder.addParameter("applicationId", getApplicationId());
            
            var uri = builder.build();
            String url = uri.toString();

            HttpHeaders httpHeaders = getHttpHeaders(url, HttpMethod.DELETE, null);
            httpHeaders.add("UserId", userId);

            HttpEntity<?> requestEntity = new HttpEntity<Object>(httpHeaders);

            HttpEntity<String> response =
                restTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, String.class);
            log.debug(response);

        } catch (URISyntaxException ex) {
            log.error("URI syntax error while creating builder for " + endpoint, ex);
            throw new HoneywellCommunicationException("Unable to build endpoint to delete honeywell device", ex);
        } catch (RestClientException ex) {
            log.error("Unable to delete Honeywell device from Access Control List. Message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to delete Honeywell device. Message: \""
                + ex.getMessage() + "\".");
        }
    }

    @Override
    public void addDevicesToGroup(List<Integer> thermostatIds, int groupId) {
        log.debug("Adding honeywell device with thermostat Id " + thermostatIds);
        try {
            String url = getUrlBase() + createDREventGroupUrlPart + groupId + "/add";

            for (List<Integer> thermostatId : Lists.partition(thermostatIds, 2000)) {
                String body = JsonUtils.toJson(thermostatId);

                HttpHeaders newheaders = getHttpHeaders(url, HttpMethod.PUT, body);
                HttpEntity<String> reqEntity = new HttpEntity<>(body, newheaders);
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

                HttpEntity<String> response =
                    restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.PUT, reqEntity, String.class);
                log.debug(response);
            }
        } catch (RestClientException | JsonProcessingException ex) {
            log.error("Add devices for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to add device. Message: \"" + ex.getMessage() + "\".");
        }

    }

    @Override
    public void cancelDREventForDevices(List<Integer> thermostatIds, int eventId, boolean immediateCancel) {
        log.debug("Cancelling DR event for devices " + thermostatIds);
        try {
            String url = getUrlBase() + cancelDREventForDevicesUrlPart + eventId + "/" + immediateCancel;
            String body = JsonUtils.toJson(thermostatIds.toArray());
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

            HttpEntity<?> requestEntity =
                new HttpEntity<Object>(body, getHttpHeaders(url, HttpMethod.POST, body));

            HttpEntity<String> response =
                restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, requestEntity, String.class);
            log.debug(response);
        } catch (RestClientException | JsonProcessingException ex) {
            log.error("Cancel DR event for devices for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to cancel device. Message: \"" + ex.getMessage() + "\".");
        }
    }

    @Override
    public void removeDeviceFromDRGroup(List<Integer> thermostatIds, int groupId) {
        log.debug("Removing specified devices from demand-response group:" + thermostatIds);
        try {
            String url = getUrlBase() + removeDeviceFromDRGroupUrlPart + groupId + "/remove";

            String body = JsonUtils.toJson(thermostatIds.toArray());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            HttpEntity<?> requestEntity = new HttpEntity<Object>(body, getHttpHeaders(url, HttpMethod.PUT, body));

            HttpEntity<String> response =
                restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.PUT, requestEntity, String.class);
            log.debug(response);
        } catch (RestClientException | JsonProcessingException ex) {
            log.error("Removing devices from DR group for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to remove device. Message: \"" + ex.getMessage() + "\".");
        }
    }

    @Override
    public void sendDREventForGroup(HoneywellWifiDutyCycleDrParameters parameters) {
        log.debug("Sending DR event for yukon LM group " + parameters.getGroupId());
        try {

            int honeywellGroupId = honeywellDao.getHoneywellGroupId(parameters.getGroupId());
            String url = getUrlBase() + drEventForGroupUrlPart + "?groupId=" + honeywellGroupId;

            DREventRequest request = new DREventRequest(parameters.getEventId(),
                                                        parameters.getStartTime(),
                                                        Boolean.TRUE, //allow opt-out on Honeywell portal & device
                                                        parameters.getRandomizationInterval(),
                                                        DutyCyclePeriod.HALFHOUR,
                                                        1,
                                                        parameters.getDutyCyclePercent(),
                                                        parameters.getDurationSeconds());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            HttpEntity<?> requestEntity = new HttpEntity<Object>(request,
                                                                 getHttpHeaders(url,
                                                                                HttpMethod.POST,
                                                                                JsonUtils.toJson(request)));

            if (log.isDebugEnabled()) {
                try {
                    log.debug("Sending honeywell duty cycle DR:");
                    log.debug("Headers: " + requestEntity.getHeaders());
                    log.debug("Body: " + JsonUtils.toJson(request));
                } catch (JsonProcessingException e) {
                    log.warn("Error parsing json in debug.", e);
                }
            }

            HttpEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(),
                                                                HttpMethod.POST,
                                                                requestEntity,
                                                                String.class);
            log.debug(response);
        } catch (RestClientException | JsonProcessingException ex) {
            log.error("Send DR event for group for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to send DR . Message: \"" + ex.getMessage() + "\".");
        }
    }

    @Override
    public void cancelDREventForGroup(int groupId, int eventId, boolean immediateCancel) {
        log.debug("Sending cancel DR event for yukon LM group " + groupId);
        try {
            int honeywellGroupId = honeywellDao.getHoneywellGroupId(groupId);
            String url = getUrlBase() + drEventForGroupUrlPart + "/optout/" + eventId + "/" + immediateCancel + "?groupId=" + honeywellGroupId;

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

            HttpEntity<?> requestEntity = new HttpEntity<Object>(getHttpHeaders(url,
                                                                                HttpMethod.POST,
                                                                                null));

            HttpEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(),
                                                                HttpMethod.POST,
                                                                requestEntity,
                                                                String.class);
            log.debug(response);
        } catch (RestClientException ex) {
            log.error("Send cancel DR event for group for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to send DR . Message: \"" + ex.getMessage() + "\".");
        }
    }
    
    private HttpHeaders getHttpHeaders(String url, HttpMethod httpMethod, String body) {
        Date date = new Date();
        SimpleDateFormat simFormatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        simFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String formattedDate = simFormatter.format(date);
        
        HttpHeaders newheaders = new HttpHeaders();
        newheaders.add("Content-Hash", "sha1 " + getSignedContent(formattedDate, url, httpMethod, body));
        newheaders.add("Date", formattedDate);
        return newheaders;
    }

    private String getSignedContent(String formattedDate, String url, HttpMethod httpMethod, String body) {
        String signedContent = null;
        try {
            char[] password = CryptoUtils.getSharedPasskey();
            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
            
            Optional<EncryptionKey> honeywellEncryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Honeywell);
            if (honeywellEncryptionKey.isEmpty()) {
                log.error("Honeywell Encryption key not found.");
                throw new HoneywellCommunicationException("Honeywell Encryption key not found");
            }

            String decryptedPrivateKey = encrypter.decryptHexStr(honeywellEncryptionKey.get().getPrivateKey());
            byte[] encoded = new Base64().decode(decryptedPrivateKey);

            // PKCS8 decode the encoded RSA private key
            java.security.Security.addProvider(new BouncyCastleFipsProvider());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory keyFactory;

            keyFactory = KeyFactory.getInstance("RSA");

            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // Step 1: Canonize url
            String canonizedURL = canonize(url);

            // Step 2: compose a signature data string that contains these items
            // from the request:
            /*
             * 2.1 HTTP method in uppercase.
             * 2.2 Base64 encoded SHA256 hash of UTF8 encoded content (if the request has body).
             * 2.3 Content type (if the request has body).
             * 2.4 Value of the Date header which is RFC1123 encoded UTC date and time(Eg. Mon, 15 Jun 2009
             * 20:45:30 GMT).
             * 2.5 Canonized request URL.
             */
            String signatureData = httpMethod.toString() + "\n";

            if (body == null) {
                signatureData += "\n";
            } else {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(body.getBytes("UTF-8"));
                byte[] aMessageDigest = md.digest();
                String base64String = new String(Base64.encodeBase64(aMessageDigest));

                signatureData += base64String + "\n" + APPLICATION_JSON;
            }
            signatureData += "\n" + formattedDate + "\n" + canonizedURL;

            // Step 3: take UTF8 encoded signature data string from Step 2 ,compute its hash value using SHA1
            // algorithm
            String utf8EncodedSignatureData = new String(signatureData.getBytes("UTF-8"));

            // Step 4: Sign it with the certificate
            // Get the private key
            Security.addProvider(new BouncyCastleFipsProvider());

            // Sign
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(privateKey);
            signature.update(utf8EncodedSignatureData.getBytes());

            signedContent = new Base64().encodeToString(signature.sign());
        }  catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException
                | InvalidKeySpecException | CryptoException | IOException | JDOMException | DecoderException e) {
                log.error("Request signing for Honeywell failed with message: \"" + e.getMessage() + "\".");
                throw new HoneywellCommunicationException("Unable to communicate with Honeywell API.", e);
            }
        return signedContent;
    }

    private static String canonize(String url) {
        String canonizedStr = "/";
        URL urlParser;
        try {
            urlParser = new URL(url);
            canonizedStr += urlParser.getHost().toLowerCase() + urlParser.getPath();
            String query = urlParser.getQuery();
            if (query != null) {
                canonizedStr += "\n";
                String args3[] = query.split("&");
                List<String> parameters = Arrays.asList(args3);
                Collections.sort(parameters);
                int paramsLeft = parameters.size();
                for (String param : parameters) {
                    paramsLeft--;
                    canonizedStr += param.replace('=', ':');
                    if (paramsLeft > 0)
                        canonizedStr += canonizedStr + "\n";
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return canonizedStr;
    }

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.HONEYWELL_SERVER_URL);
    }

    @Override
    public int getGatewayDetailsForMacId(String macId, String userId) {
        log.debug("Getting Gateway Details for given macid - " + macId);
        int deviceId = 0;
        try {
            String url = getUrlBase() + getGatewayByMacIdUrlPart;

            MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
            HttpHeaders newheaders = getHttpHeaders(url, HttpMethod.GET, null);

            newheaders.add("UserId", userId);
            HttpEntity<?> requestEntity = new HttpEntity<Object>(body, newheaders);
            
            macId = macId.replace(":", StringUtils.EMPTY);
            UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(url).queryParam("macId", macId).queryParam("allData", "true");

            HttpEntity<String> response =
                restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class);

            log.debug(response);
            String responseString = response.getBody().toString();
            try {
                Map<String, Object> data = JsonUtils.fromJson(responseString, Map.class);
                ArrayList<Object> jsonArray = (ArrayList<Object>) data.get("devices");
                for (Object mapObject : jsonArray) {
                    Map<String, Object> deviceMap = (Map<String, Object>) mapObject;
                    deviceId = (int) deviceMap.get("deviceID");
                }
            } catch (IOException e) {
                log.error("Error occurred");
            }

        } catch (RestClientException ex) {
            log.error("Get gateway details for macId for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to add device. Message: \"" + ex.getMessage() + "\".");
        }
        return deviceId;
    }
    
    private String getApplicationId() {
        return settingDao.getOptionalString(GlobalSettingType.HONEYWELL_APPLICATIONID)
                .orElseThrow(()-> new HoneywellCommunicationException("Honeywell Application ID not entered in GlobalSettings"));
    }
    
    @Override
    public List<HoneywellDREvent> getDREventsForDevice(Integer thermostatId, String userId) {
        log.debug("Get DR events for device " + thermostatId);

        List<HoneywellDREvent> drEvents = new ArrayList<HoneywellDREvent>();
        try {
            String url = getUrlBase() + drEventsForDeviceUrlPart + thermostatId;
            HttpHeaders newheaders = getHttpHeaders(url, HttpMethod.GET, null);
            newheaders.add("UserId", userId);
            HttpEntity<?> requestEntity = new HttpEntity<Object>(newheaders);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            ResponseEntity<List<HoneywellDREvent>> response =
                restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity,
                    new ParameterizedTypeReference<List<HoneywellDREvent>>() {
                    });
            log.debug(response);
            drEvents = response.getBody();
        } catch (RestClientException ex) {
            log.error("Get DR event details of the devices for Honeywell failed with message: \"" + ex.getMessage()
                + "\".");
            throw new HoneywellCommunicationException("Unable to get DR event details for the device. Message: \""
                + ex.getMessage() + "\".");
        }
        return drEvents;
    }
}
