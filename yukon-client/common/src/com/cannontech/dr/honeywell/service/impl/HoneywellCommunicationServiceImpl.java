package com.cannontech.dr.honeywell.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.dr.honeywell.HoneywellCommunicationException;
import com.cannontech.dr.honeywell.message.DREventRequest;
import com.cannontech.dr.honeywell.message.DutyCyclePeriod;
import com.cannontech.dr.honeywell.service.HoneywellCommunicationService;
import com.cannontech.dr.honeywellWifi.model.HoneywellWifiDutyCycleDrParameters;
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

    private static final String createDREventGroupUrlPart = "webapi/api/drEventGroups/";
    private static final String getGatewayByMacId = "webapi/api/gateways";
    private static final String cancelDREventForDevicesUrlPart = "api/drEvents/optout/";
    private static final String removeDeviceFromDRGroupUrlPart = "WebAPI/api/drEventGroups/";
    private static final String drEventForGroupUrlPart = "WebAPI/api/drEvents";
    
    private static final String CONTENT_TYPE = "application/json";
    // TODO:Needs to be removed after YUK-15886 is fixed.
    private static final String PRIVATE_RSA_KEY = "MIICXAIBAAKBgQCLkrVLUxQXnkHCWexPkKj0VRkkz3TYxsI8MNEWXptkAp/ksjLv\n"
                                                + "fHukdWANLthSJXheRqPi5LDqIWAovwxqph/r9iQTzU6tzDlov3lbOMsuZ2J10RtS\n"
                                                + "3xkqLQi8KRVYZqKXQuk0KZHDPDNYUiKWaXGAsqmsrl4MrxxSTN91uAi++wIDAQAB\n"
                                                + "AoGAMx5pfgwQiNHynb8XmNCPwOVGD0BYOGkbjLYIblv6J7f6XeWSWDpMgqBmrI1i\n"
                                                + "tzt4CXdv4NMHpMjSkjnez1TGzbcGKNFgUCY4bfNCTOF2SxkloKX2v5cPp7trXqp/\n"
                                                + "HMYXzloJoO1GMnY7bjgOGr4YmeDsu2oTBrfLaAWd1Gd0ffkCQQDTV/oTbo0yer4U\n"
                                                + "DXaloZkAa9bsgHgtHxbcBY1kp1NXqtoPlSF1o2v35nH5mNmaDEvoKc/+/8euQb/L\n"
                                                + "kze6t4n9AkEAqRCCdvA/au2wNU+V6O5ikQfF0K2F+4W6wurHjLSsudooy0cSiILZ\n"
                                                + "Cbo/I+ay4OoxSYRer9UzbcRQ0SlS2Q5iVwJBAI7iZ/xDPcrnGSNNhu2sN1kFj6UN\n"
                                                + "pjI7VqUiS9nFFp+qrwrh9GEoP5K2hlANevCfZ6JqwmjQXRv+78Ceo4rlE7ECQGUQ\n"
                                                + "an1BRxfbuM9VoQ7amm+KTvVdFc/y9F8azGlPhEWhpWtHNEwItEe9X4tNmLcdKJOD\n"
                                                + "HrtL3u+KQKmYY18/2wcCQB4PddTYGU72igyRRP68MqWzlAIaHsHvIfo5JiU3RPcl\n"
                                                + "Lxc5BGEVGGDcW3+ySXzhMzHqPIhhb2wAzLBB+MfzPWI=\n";

    @Override
    public void addDevicesToGroup(List<Integer> thermostatIds, int groupId) {
        log.debug("Adding honeywell device with thermostat Id " + thermostatIds);
        try {
            String url = getUrlBase() + createDREventGroupUrlPart + groupId + "/add";

            for (List<Integer> thermostatId : Lists.partition(thermostatIds, 2000)) {
                String body = JsonUtils.toJson(thermostatId);

                HttpHeaders newheaders = getHttpHeaders(url, HttpMethod.PUT, body);
                HttpEntity<String> reqEntity = new HttpEntity<String>(body, newheaders);
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
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
            body.add("deviceIds", thermostatIds.toArray());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

            HttpEntity<?> requestEntity =
                new HttpEntity<Object>(body, getHttpHeaders(url, HttpMethod.POST, body.toString()));

            HttpEntity<String> response =
                restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException ex) {
            log.error("Cancel DR event for devices for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to add device. Message: \"" + ex.getMessage() + "\".");
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
            throw new HoneywellCommunicationException("Unable to add device. Message: \"" + ex.getMessage() + "\".");
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
                                                        Boolean.FALSE,
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

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        SimpleDateFormat simFormatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        String formattedDate = simFormatter.format(date);
        
        HttpHeaders newheaders = new HttpHeaders();
        newheaders.add("Content-Hash", "sha1 " + getSignedContent(formattedDate, url, httpMethod, body));
        newheaders.add("Date", formattedDate);
        return newheaders;
    }

    private String getSignedContent(String formattedDate, String url, HttpMethod httpMethod, String body) {
        String signedContent = null;
        try {
            byte[] encoded = new Base64().decode(PRIVATE_RSA_KEY);

            // PKCS8 decode the encoded RSA private key
            java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory keyFactory;

            keyFactory = KeyFactory.getInstance("RSA");

            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // Step 1:Cannonize url
            String cannonizedURL = cannonize(url);

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

                signatureData += base64String + "\n" + "application/json";

            }
            signatureData += "\n" + formattedDate + "\n" + cannonizedURL;

            // Step 3: take UTF8 encoded signature data string from Step 2 ,compute its hash value using SHA1
            // algorithm
            String utf8EncodedSignatureData = new String(signatureData.getBytes("UTF-8"));

            // Step 4: Sign it with the certificate
            // Get the private key
            Security.addProvider(new BouncyCastleProvider());

            // Sign
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(privateKey);
            signature.update(utf8EncodedSignatureData.getBytes());

            signedContent = new Base64().encodeToString(signature.sign());
        }  catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException
                | InvalidKeySpecException e) {
                log.error("Request signing for Honeywell failed with message: \"" + e.getMessage() + "\".");
                throw new HoneywellCommunicationException("Unable to communicate with Honeywell API.", e);
            }
        return signedContent;
    }

    private static String cannonize(String url) {
        String cannonizedStr = "/";
        URL urlParser;
        try {
            urlParser = new URL(url);
            cannonizedStr += urlParser.getHost().toLowerCase() + urlParser.getPath();
            String query = urlParser.getQuery();
            if (query != null) {
                cannonizedStr += "\n";
                String args3[] = query.split("&");
                List<String> parameters = Arrays.asList(args3);
                Collections.sort(parameters);
                int paramsLeft = parameters.size();
                for (String param : parameters) {
                    paramsLeft--;
                    cannonizedStr += param.replace('=', ':');
                    if (paramsLeft > 0)
                        cannonizedStr += cannonizedStr + "\n";
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return cannonizedStr;
    }

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.HONEYWELL_SERVER_URL);
    }

    @Override
    public int getGatewayDetailsForMacId(String macId, String userId) {
        log.debug("Getting Gateway Details for given macid - " + macId);
        int deviceId = 0;
        try {
            String url = getUrlBase() + getGatewayByMacId;

            MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
            HttpHeaders newheaders = getHttpHeaders(url, HttpMethod.GET, null);

            newheaders.add("UserId", "17106");
            // UserId - '17106' is hardcoded just for testing.

            // TODO : remove above line (this hardcoded UserId) and uncomment below line after fixing
            // YUK-16052
            // newheaders.add("UserId", userId);
            HttpEntity<?> requestEntity = new HttpEntity<Object>(body, newheaders);

            UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(url).queryParam("macId", "00D02D81CF23").queryParam("allData", "true");
            // macId - '00D02D81CF23' is hardcoded just for testing.

            // TODO : remove this hardcoded macId and replace it with parameter macId after fixing YUK-16052
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
                log.error("Error occured");
            }

        } catch (RestClientException ex) {
            log.error("Get gateway details for macId for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to add device. Message: \"" + ex.getMessage() + "\".");
        }
        return deviceId;
    }
}
