package com.cannontech.dr.nest.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.CriticalEvent;
import com.cannontech.dr.nest.model.NestControlHistory;
import com.cannontech.dr.nest.model.NestError;
import com.cannontech.dr.nest.model.NestEventId;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestFileType;
import com.cannontech.dr.nest.model.NestURLTypes;
import com.cannontech.dr.nest.model.NestUploadInfo;
import com.cannontech.dr.nest.model.SchemaViolationResponse;
import com.cannontech.dr.nest.model.StandardEvent;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class NestCommunicationServiceImpl implements NestCommunicationService{
    
    private final RestTemplate restTemplate;
    private static final Logger log = YukonLogManager.getLogger(NestCommunicationServiceImpl.class); 
    private Proxy proxy;
    private HttpHost host;
    private GlobalSettingDao settingDao;
    private NestDao nestDao;
         
    @Autowired
    public NestCommunicationServiceImpl(GlobalSettingDao settingDao) {
        this.settingDao = settingDao;
        proxy = YukonHttpProxy.fromGlobalSetting(settingDao)
                .map(YukonHttpProxy::getJavaHttpProxy)
                .orElse(null);
        host = YukonHttpProxy.fromGlobalSetting(settingDao)
                .map(YukonHttpProxy::getJavaHttpHost)
                .orElse(null);
        restTemplate = new RestTemplate();
        
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        if(proxy != null) {
            factory.setProxy(proxy);
        }
        restTemplate.setRequestFactory(factory);
    }
    public static final String critical = NestURLTypes.CONTROL_CRITICAL.getUrl();
    public static final String standard = NestURLTypes.CONTROL_STANDARD.getUrl();

    /**
     * curl https://enterprise-api.nest.com/api/energy/v2/rush_hour_rewards/events/standard -v -x proxy.etn.com:8080 -H "Authorization:Basic U2FtdWVsVEpvaG5zdG9uQGVhdG9uLmNvbTo3MjRiYzkwMWQ3MDE0YWUyNjA5OGJhZjk1ZjVjMTRiNA==" -H "Content-Type: application/json" -d "{\"start_time\":\"2018-09-14T00:00:00.000Z\",\"duration\":\"PT30M\",\"groups\":[\"Test\"],\"load_shaping_options\":{\"preparation_load_shaping\":\"STANDARD\",\"peak_load_shaping\":\"STANDARD\",\"post_peak_load_shaping\":\"STANDARD\"}}"
     */
    @Override
    public Optional<NestError> sendStandardEvent(StandardEvent event) {
        log.debug("Sending request to create standard event");
        String requestUrl = settingDao.getString(GlobalSettingType.NEST_SERVER_URL) + standard;
        log.debug("Request url {}", requestUrl);
        String response = getNestResponse(requestUrl, event);
        
        return processNestReply(response, event);
    }
    
    @Override
    public Optional<NestError> sendCriticalEvent(CriticalEvent event) {
        log.debug("Sending request to create critical event");
        String requestUrl = settingDao.getString(GlobalSettingType.NEST_SERVER_URL) + critical;
        String response = getNestResponse(requestUrl, event);
        
        return processNestReply(response, event);
    }
    
    @Override
    public boolean cancelEvent(NestControlHistory history) {
        log.debug("Canceling event {}", history);
        String requestUrl = settingDao.getString(GlobalSettingType.NEST_SERVER_URL) + NestURLTypes.CONTROL + history.getKey();
        log.debug("Request url {}", requestUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodeAuthorization());
        
        HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
            nestDao.updateCancelRequestTime(history.getId());
            HttpEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, entity, String.class);
            //204 = On success
            //Can't test yet as I can't start event yet
            //I am not sure what to store as a response yet
            nestDao.updateNestResponse(history.getId(), response.getBody());
            return true;
        } catch (HttpClientErrorException e) {
            log.error("Error canceling Nest event", e);
        }
        
        return true;
    }
    
    private Optional<NestError> processNestReply(String response, CriticalEvent event) {
        NestError nestError = null;
        try {
            NestEventId nestId = JsonUtils.fromJson(response, NestEventId.class);
            //This table will only have an entry if response is success
            NestControlHistory history = new NestControlHistory();
            history.setStartTime(event.getStart());
            history.setStopTime(event.getStop());
            history.setKey(nestId.getId());
            nestDao.createControlHistory(history);
        } catch (IOException e) {
            try {
                SchemaViolationResponse error = JsonUtils.fromJson(response, SchemaViolationResponse.class);
                log.error("Reply from Nest contains an error="+ error);
                if (error.getViolations() != null && !error.getViolations().isEmpty()) {
                    //schema violation
                    throw new NestException("Reply from Nest contains an error. Error:" + error);
                }
                nestError = error.getError();
            } catch (IOException e1) {
                throw new NestException("Error getting valid reponse from Nest. Reponse:" + response);
            }
        }
        return Optional.of(nestError);
    }
    
    @Override
    public String getNestResponse(String url, CriticalEvent event) {
        log.debug("Request url {}", url);
        try {
            log.debug(JsonUtils.toJson(event));
        } catch (JsonProcessingException e) {
            throw new NestException("Unable to convert " + event + " to json");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodeAuthorization());
 
        HttpEntity<CriticalEvent> entity = new HttpEntity<>(event, headers);
        String response;
        try {
            response = restTemplate.postForObject(url, entity, String.class);
        } catch (HttpClientErrorException e) {
            response = e.getResponseBodyAsString();
        }
        log.debug("response:" + response);
        return response;
    }
        
    @Override
    public List<NestExisting> downloadExisting() {
        log.debug("Downloading Nest existing file");
        InputStream inputStream = getFileInputStream(NestFileType.EXISTING);
        List<NestExisting> existing = parseExistingCsvFile(inputStream);
        log.debug("Download of the Nest file complete");
        return existing; 
    }
        
    private InputStream getFileInputStream(NestFileType type) {
        InputStream inputStream = null;
        String nestUrl = settingDao.getString(GlobalSettingType.NEST_SERVER_URL);
        String stringUrl = nestUrl + type.getUrl() + "/" + type.getFile();
        log.debug("Nest Url:"+stringUrl);
        // curl https://enterprise-api.nest.com/api/v1/users/pending/latest.csv -v -x proxy.etn.com:8080 -H "Authorization:Basic U2FtdWVsVEpvaG5zdG9uQGVhdG9uLmNvbTo3MjRiYzkwMWQ3MDE0YWUyNjA5OGJhZjk1ZjVjMTRiNA=="
        try {
            URLConnection connection =
                useProxy(stringUrl) ? new URL(stringUrl).openConnection(proxy) : new URL(stringUrl).openConnection();
            connection.setRequestProperty("Authorization", encodeAuthorization());
            inputStream = connection.getInputStream();
        } catch (NestException | IOException e) {
            log.error("Error connecting to "+stringUrl, e);
            throw new NestException("Error connecting to ", e);
        }
        return inputStream;
    }
    
    private boolean useProxy(String stringUrl) {
        if (proxy == null) {
            return false;
        } else if ((stringUrl.contains("localhost") || stringUrl.contains("127.0.0.1"))) {
            return false;
        }
        return true;
    }
    
    private String encodeAuthorization() {
        
        String key = settingDao.getString(GlobalSettingType.NEST_USERNAME) + ":" + settingDao.getString(GlobalSettingType.NEST_PASSWORD);
        try {
            return "Basic " + DatatypeConverter.printBase64Binary(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new NestException("Error encoding Nest key", e);
        }
    }
    
    @Override
    public List<NestExisting> parseExistingCsvFile(InputStream inputStream) {
        List<NestExisting> existing = new ArrayList<>();
        if (inputStream != null) {
            CsvSchema schema = CsvSchema.emptySchema().withHeader().withNullValue("");
            try {
                MappingIterator<NestExisting> it =
                    new CsvMapper().readerFor(NestExisting.class).with(schema).readValues(inputStream);
                existing.addAll(it.readAll());
            } catch (IOException e) {
                throw new NestException("Unable to parse exising file ", e);
            }
        }
        log.debug(existing);
        return existing;
    }
    
    /**
     * Example of error response from Nest if group doesn't exist.
     * {"num_group_changes":0,"num_dissolved":0,"errors":[{"row_number":2,"header":["REF","YEAR","MONTH","DAY","NAME","EMAIL","SERVICE_ADDRESS","SERVICE_CITY","SERVICE_STATE","SERVICE_POSTAL_CODE","ACCOUNT_NUMBER","CONTRACT_APPROVED","PROGRAMS","WINTER_RHR","SUMMER_RHR","ASSIGN_GROUP","GROUP","DISSOLVE","DISSOLVE_REASON","DISSOLVE_NOTES"],"fields":["a5a1305e-79d2-4a98-9fc9-f6112a39f9b2","2018","8","21","Marina Feldman","******","ATRIA Corporate Center1","Plymouth","MN","55441","1","2018-08-20 11:32:06","SUMMER_RHR","","09AA01AC35170N2N","Y","A","","",""],"errors":["invalid value for GROUP: 'A'"]}]}
     * @return 
     */
    @Override
    public NestUploadInfo uploadExisting(List<NestExisting> existing) {
        log.debug("Uploading file {}", existing);
        File uploadFile = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String requestUrl =
                settingDao.getString(GlobalSettingType.NEST_SERVER_URL) + NestFileType.EXISTING.getUrl();
            HttpPost httppost = new HttpPost(requestUrl);
            httppost.addHeader("content-type", "text/csv");
            httppost.addHeader("Accept", "application/json");
            httppost.setHeader("Authorization", encodeAuthorization());
            if (host != null && useProxy(requestUrl)) {
                RequestConfig requestConfig = RequestConfig.custom().setProxy(host).build();
                httppost.setConfig(requestConfig);
            }

            uploadFile = NestCommunicationService.createFile(CtiUtilities.getNestDirPath(), "upload");
            ObjectWriter writer =
                new CsvMapper().writerFor(NestExisting.class).with(NestFileType.EXISTING.getSchema().withHeader());
            writer.writeValues(uploadFile).writeAll(existing).close();
            httppost.setEntity(new FileEntity(uploadFile));

            log.debug(EntityUtils.toString(httppost.getEntity()));
            log.debug("executing request {} {}", httppost.getRequestLine(), httppost.getConfig());
            HttpResponse response = httpclient.execute(httppost);
            org.apache.http.HttpEntity resEntity = response.getEntity();
            log.debug(response.getStatusLine());
            if (resEntity != null) {
                String str = EntityUtils.toString(resEntity);
                log.debug(str);
                NestUploadInfo info = JsonUtils.fromJson(str, NestUploadInfo.class);
                log.debug(info);
                return info;
            }
        } catch (IOException e) {
            throw new NestException("Error uploading existing file to Nest", e);
        } finally {
            if (uploadFile != null) {
                uploadFile.delete();
            }
        }
        return null;
    }
}
