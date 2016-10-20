package com.cannontech.dr.honeywell.service.impl;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.honeywell.HoneywellCommunicationException;
import com.cannontech.dr.honeywell.service.HoneywellCommunicationService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.Lists;

public class HoneywellCommunicationServiceImpl implements HoneywellCommunicationService {

    private static final Logger log = YukonLogManager.getLogger(HoneywellCommunicationServiceImpl.class);

    @Autowired private @Qualifier("honeywell") RestOperations restTemplate;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private static final String createDREventGroupUrlPart = "webapi/api/drEventGroups/";
    private static final String cancelDREventForDevicesUrlPart = "api/drEvents/optout/";
    private static final String removeDeviceFromDRGroupUrlPart = "api/drEventGroups/";

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

            MultiValueMap<String, Object> body;
            for (List<Integer> thermostatId : Lists.partition(thermostatIds, 2000)) {
                body = new LinkedMultiValueMap<String, Object>();
                body.add("thermostatIds", thermostatId.toArray());

                HttpEntity<?> requestEntity =
                    new HttpEntity<Object>(body, getHttpHeaders(url, HttpMethod.PUT, body.toString().getBytes("UTF-8")));

                HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

            }
        } catch (RestClientException | UnsupportedEncodingException ex) {
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
                new HttpEntity<Object>(body, getHttpHeaders(url, HttpMethod.POST, body.toString().getBytes("UTF-8")));

            HttpEntity<String> response =
                restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException | UnsupportedEncodingException ex) {
            log.error("Cancel DR event for devices for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to add device. Message: \"" + ex.getMessage() + "\".");
        }
    }

    @Override
    public void removeDeviceFromDRGroup(List<Integer> thermostatIds, int groupId) {
        log.debug("Removing specified devices from demand-response group:" + thermostatIds);
        try {
            String url = getUrlBase() + removeDeviceFromDRGroupUrlPart + "/remove";
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
            body.add("deviceIds", thermostatIds.toArray());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            HttpEntity<?> requestEntity =
                new HttpEntity<Object>(body, getHttpHeaders(url, HttpMethod.PUT, body.toString().getBytes("UTF-8")));

            HttpEntity<String> response =
                restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.PUT, requestEntity, String.class);
        } catch (RestClientException | UnsupportedEncodingException ex) {
            log.error("Removing devices from DR group for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to add device. Message: \"" + ex.getMessage() + "\".");
        }
    }

    private HttpHeaders getHttpHeaders(String url, HttpMethod httpMethod, byte[] body) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        SimpleDateFormat simFormatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        String formattedDate = simFormatter.format(date);
        HttpHeaders newheaders = new HttpHeaders();
        newheaders.add("Content-Hash", "sha1 " + getSignedContent(formattedDate, url, httpMethod, body));
        newheaders.add("Date", formattedDate);
        newheaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return newheaders;
    }

    private String getSignedContent(String formattedDate, String url, HttpMethod httpMethod, byte[] body) {
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
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(body);
                signatureData += new Base64().encodeToString(hash) + "\n" + CONTENT_TYPE;
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
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException
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
                for (String param : parameters) {
                    cannonizedStr += param.replace('=', ':') + "\n";
                }
            }
        } catch (MalformedURLException e) {
            log.error("Could not cannonize url for Honeywell \"" + e.getMessage() + "\".");
            throw new HoneywellCommunicationException("Unable to communicate with Honeywell API.", e);
        }
        return cannonizedStr;
    }

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.HONEYWELL_SERVER_URL);
    }
}
