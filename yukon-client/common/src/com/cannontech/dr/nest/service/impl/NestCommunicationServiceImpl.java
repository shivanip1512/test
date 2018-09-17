package com.cannontech.dr.nest.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.nest.model.CriticalEvent;
import com.cannontech.dr.nest.model.NestEventId;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestFileType;
import com.cannontech.dr.nest.model.NestPending;
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
    
    private enum Action{
        UPLOAD,
        DOWNLOAD
    }
    
    private final RestTemplate restTemplate;
    private static final Logger log = YukonLogManager.getLogger(NestCommunicationServiceImpl.class); 
    private Proxy proxy;
    private HttpHost host;
    private GlobalSettingDao settingDao;
         
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
    private static final String controlCritical = "/energy/v2/rush_hour_rewards/events/critical";
    private static final String controlStandard = "/energy/v2/rush_hour_rewards/events/standard";
    
    /**
     * curl https://enterprise-api.nest.com/api/energy/v2/rush_hour_rewards/events/standard -v -x proxy.etn.com:8080 -H "Authorization:Basic U2FtdWVsVEpvaG5zdG9uQGVhdG9uLmNvbTo3MjRiYzkwMWQ3MDE0YWUyNjA5OGJhZjk1ZjVjMTRiNA==" -H "Content-Type: application/json" -d "{\"start_time\":\"2018-09-14T00:00:00.000Z\",\"duration\":\"PT30M\",\"groups\":[\"Group1\"],\"load_shaping_options\":{\"preparation_load_shaping\":\"STANDARD\",\"peak_load_shaping\":\"STANDARD\",\"post_peak_load_shaping\":\"STANDARD\"}}"
     */
    @Override
    public NestEventId createStandardEvent(StandardEvent event) {
        log.debug("Sending request to create standard event");
        String requestUrl = settingDao.getString(GlobalSettingType.NEST_SERVER_URL) + controlStandard;
        log.debug("Request url:"+requestUrl);
        //String response = getNestResponseViaHttpRequest(requestUrl, event);
        String response = getNestResponseViaRest(requestUrl, event);
        
        return getNestEventId(response);
    }
    
    @Override
    public NestEventId createCriticalEvent(CriticalEvent event) {
        log.debug("Sending request to create critical event");
        String requestUrl = settingDao.getString(GlobalSettingType.NEST_SERVER_URL) + controlCritical;
        log.debug("Request url:"+requestUrl);
        String response = getNestResponseViaHttpRequest(requestUrl, event);
        //String response = getNestResponseViaRest(requestUrl, event);
        
        return getNestEventId(response);
    }
    
    private NestEventId getNestEventId(String response) {
        try {
            NestEventId nestId = JsonUtils.fromJson(response, NestEventId.class);
            return nestId;
        } catch (IOException e) {
            try {
                SchemaViolationResponse error = JsonUtils.fromJson(response, SchemaViolationResponse.class);
                log.debug("error="+ error);
                //build a user friendly error and log as event log?
                return null;
            } catch (IOException e1) {
                throw new NestException("Error getting valid reponse from Nest. Reponse:" + response);
            }
        }
    }
    
    private String getNestResponseViaRest(String url, CriticalEvent event) {
        String requestJson;
        try {
            requestJson = JsonUtils.toJson(event);
            log.debug(requestJson);
        } catch (JsonProcessingException e) {
            throw new NestException("Unable to convert " + event + " to json");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodeAuthorization());
        //requestJson = "{\"start_time\":\"2018-09-14T00:00:00.000Z\",\"duration\":\"PT30M\",\"groups\":[\"Group1\"],\"load_shaping_options\":{\"preparation_load_shaping\":\"STANDARD\",\"peak_load_shaping\":\"STANDARD\",\"post_peak_load_shaping\":\"STANDARD\"}}";
 
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        String response = restTemplate.postForObject(url, entity, String.class);
        log.debug("response:"+response);
        return response;
    }
    
    private String getNestResponseViaHttpRequest(String url, CriticalEvent event) {
        try {
            log.debug(JsonUtils.toJson(event));
        } catch (JsonProcessingException e) {
            throw new NestException("Unable to convert " + event + " to json");
        }
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("content-type", "application/json");
            httppost.setHeader("Authorization", encodeAuthorization());
            if (host != null) {
                RequestConfig requestConfig = RequestConfig.custom().setProxy(host).build();
                httppost.setConfig(requestConfig);
            }

            StringEntity entity = new StringEntity(JsonUtils.toJson(event), ContentType.APPLICATION_FORM_URLENCODED);

            httppost.setEntity(entity);
            log.debug("executing request: " + httppost.getRequestLine() + httppost.getConfig());
            HttpResponse response = httpclient.execute(httppost);
            org.apache.http.HttpEntity resEntity = httpclient.execute(httppost).getEntity();
            log.debug(response.getStatusLine());
            if (resEntity != null) {
                String str = EntityUtils.toString(resEntity);
                log.debug("response:" + str);
                return str;
            }
            throw new NestException("Error getting response from Nest");
        } catch (IOException e) {
            throw new NestException("Error sending event message to Nest", e);
        }
    }
    
    @Override
    public List<NestExisting> downloadExisting(Date date) {
        InputStream inputStream = getFileInputStream(NestFileType.EXISTING);
        List<NestExisting> existing = parseExistingCsvFile(inputStream);
        writeExistingFile(existing, Action.DOWNLOAD, date);
        return existing; 
    }
        
    private InputStream getFileInputStream(NestFileType type) {
        InputStream inputStream = null;
        // curl https://enterprise-api.nest.com/api/v1/users/pending/latest.csv -v -x proxy.etn.com:8080 -H "Authorization:Basic U2FtdWVsVEpvaG5zdG9uQGVhdG9uLmNvbTo3MjRiYzkwMWQ3MDE0YWUyNjA5OGJhZjk1ZjVjMTRiNA=="
        try {
            String nestUrl = settingDao.getString(GlobalSettingType.NEST_SERVER_URL);
            String stringUrl = nestUrl + type.getUrl() + "/" + type.getFile();
            URLConnection connection = proxy == null ? new URL(stringUrl).openConnection() : new URL(stringUrl).openConnection(proxy);
            connection.setRequestProperty("X-Requested-With", "Curl");
            connection.setRequestProperty("Authorization", encodeAuthorization());
            inputStream = connection.getInputStream();
        } catch (NestException | IOException e) {
            log.error(e);
        }
        return inputStream;
    }
    
    private String encodeAuthorization() {
        
        String key = settingDao.getString(GlobalSettingType.NEST_USERNAME) + ":" + settingDao.getString(GlobalSettingType.NEST_PASSWORD);
        try {
            return "Basic " + DatatypeConverter.printBase64Binary(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new NestException("Error encoding Nest key", e);
        }
    }
    
    private File writeExistingFile(List<NestExisting> existing, Action action, Date date) {
        ObjectWriter writer =
            new CsvMapper().writerFor(NestExisting.class).with(NestFileType.EXISTING.getSchema().withHeader());
        File file = getDebugFile(NestFileType.EXISTING, action, date);
        try {
            writer.writeValues(file).writeAll(existing);
            return file;
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }

    private List<NestExisting> parseExistingCsvFile(InputStream inputStream) {
        List<NestExisting> existing = new ArrayList<>();
        if (inputStream != null) {
            CsvSchema schema = CsvSchema.emptySchema().withHeader().withNullValue("");
            try {
                MappingIterator<NestExisting> it =
                    new CsvMapper().readerFor(NestExisting.class).with(schema).readValues(inputStream);
                existing.addAll(it.readAll());
            } catch (IOException e) {
                log.error(e);
            }
        }
        log.debug(existing);
        return existing;
    }
        
    private File getDebugFile(NestFileType type, Action action, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
        return new File(CtiUtilities.getNestDirPath(),
            type.toString() + "_" + action + "_" + formatter.format(date) + ".csv");
    }

    @Override
    public void uploadExisting(List<NestExisting> existing, Date date) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost(NestFileType.EXISTING.getUrl());
            httppost.addHeader("content-type", "text/csv");
            httppost.addHeader("Accept", "application/json");
            httppost.setHeader("Authorization", encodeAuthorization());
            if (host != null) {
                RequestConfig requestConfig = RequestConfig.custom().setProxy(host).build();
                httppost.setConfig(requestConfig);
            }
            // File file = new File(CtiUtilities.getNestDirPath(), "EXISTING.csv");
            File file = writeExistingFile(existing, Action.UPLOAD, date);
            FileEntity entity = new FileEntity(file);
            log.debug(EntityUtils.toString(entity));
            httppost.setEntity(entity);
            log.debug("executing request " + httppost.getRequestLine() + httppost.getConfig());
            HttpResponse response = httpclient.execute(httppost);
            org.apache.http.HttpEntity resEntity = response.getEntity();
            log.debug(response.getStatusLine());
            if (resEntity != null) {
                String str =EntityUtils.toString(resEntity);
                log.debug(str);
            }
        } catch (NestException | IOException e) {
            log.error(e);
        }
    }
    
    
    @Override
    public List<NestPending> downloadPending(Date date) {
        InputStream inputStream = getFileInputStream(NestFileType.PENDING);
        List<NestPending> pending = parsePendingCsvFile(inputStream);
        writePendingFile(pending, Action.DOWNLOAD, date);
        return pending; 
    }
    
    private void writePendingFile(List<NestPending> pending, Action action, Date date) {
        ObjectWriter writer =
            new CsvMapper().writerFor(NestPending.class).with(NestFileType.PENDING.getSchema().withHeader());
        File file = getDebugFile(NestFileType.PENDING, action, date);
        try {
            writer.writeValues(file).writeAll(pending);
        } catch (IOException e) {
            log.error(e);
        }
    }
    
    private List<NestPending> parsePendingCsvFile(InputStream inputStream) {
        List<NestPending> pending = new ArrayList<>();
        if (inputStream != null) {
            CsvSchema schema = CsvSchema.emptySchema().withHeader().withNullValue("");
            try {
                MappingIterator<NestPending> it =
                    new CsvMapper().readerFor(NestPending.class).with(schema).readValues(inputStream);
                pending.addAll(it.readAll());
            } catch (IOException e) {
                log.error(e);
            }
        }
        return pending;
    }
}

