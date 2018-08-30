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

import org.apache.http.HttpEntity;
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

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.nest.NestCommunicationException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestFileType;
import com.cannontech.dr.nest.model.NestPending;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.system.dao.GlobalSettingDao;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class NestCommunicationServiceImpl implements NestCommunicationService{
    
    private enum Action{
        UPLOAD,
        DOWNLOAD
    }
    
    @Autowired private GlobalSettingDao settingDao;
    private static final Logger log = YukonLogManager.getLogger(NestCommunicationServiceImpl.class);
    
    //move to global settings
    private static final String email = "SamuelTJohnston@eaton.com";
    private static final String nestKey = "724bc901d7014ae26098baf95f5c14b4";
    private static final String url = "https://enterprise-api.nest.com/api";

    private Proxy getProxy() {
        return YukonHttpProxy.fromGlobalSetting(settingDao)
                .map(YukonHttpProxy::getJavaHttpProxy)
                .orElse(null);
    }
    
    private HttpHost getHttpHost() {
        return YukonHttpProxy.fromGlobalSetting(settingDao)
                .map(YukonHttpProxy::getJavaHttpHost)
                .orElse(null);
    }

    @Override
    public List<NestPending> downloadPending(Date date) {
        InputStream inputStream = getFileInputStream(NestFileType.PENDING);
        List<NestPending> pending = parsePendingCsvFile(inputStream);
        writePendingFile(pending, Action.DOWNLOAD, date);
        return pending; 
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
            String stringUrl = url + type.getUrl() + "/" + type.getFile();
            Proxy proxy = getProxy();
            URLConnection connection = proxy == null ? new URL(stringUrl).openConnection() : new URL(stringUrl).openConnection(getProxy());
            connection.setRequestProperty("X-Requested-With", "Curl");
            connection.setRequestProperty("Authorization", encodeAuthorization());
            inputStream = connection.getInputStream();
        } catch (NestCommunicationException | IOException e) {
            log.error(e);
        }
        return inputStream;
    }
    
    private String encodeAuthorization() throws UnsupportedEncodingException {
        String key = email + ":" + nestKey;
        return "Basic " + DatatypeConverter.printBase64Binary(key.getBytes("UTF-8"));
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
            HttpHost host = getHttpHost();
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
            HttpEntity resEntity = response.getEntity();
            log.debug(response.getStatusLine());
            if (resEntity != null) {
                String str =EntityUtils.toString(resEntity);
                log.debug(str);
            }
        } catch (NestCommunicationException | IOException e) {
            log.error(e);
        }
    }
}
