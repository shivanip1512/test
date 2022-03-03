package com.cannontech.support.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.LocationData;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiRequest;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponse;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUtil;
import com.cannontech.common.util.jms.JmsReplyReplyHandler;
import com.cannontech.common.util.jms.RequestReplyReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.support.rfn.message.RfnSupportBundleRequest;
import com.cannontech.support.rfn.message.RfnSupportBundleResponse;
import com.cannontech.support.rfn.message.RfnSupportBundleResponseType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ImmutableSet;

public class RFNetworkSupportBundleService {

    private final static Logger log = YukonLogManager.getLogger(RFNetworkSupportBundleService.class);
    private final static String downloadRfSupportBundleURL = "/nmclient/DownloadRfSupportBundleServlet?fileName=";
    private final static String supportBundleDirectory = "/Server/SupportBundles/";
    private final static String locationDataDir = "locationData";
    private final static String networkSnapshotDataDir = "networkSnapshotData_Yukon";
    private final static int batchsize = 1000;
    private final static int meterLocationcolumnCount = 11;
    private final static int relayLocationcolumnCount = 9;
    private final static int electricNodecolumnCount = 13;
    private final static String meterLocationFileName = "MeterLocationsInYukon";
    private final static String relayLocationFileName = "RelayLocationsInYukon";
    private final static String gatewayLocationFileName = "GatewayLocationsInYukon";
    private final static String electricNodeLocationFileName = "yukonData";
    private static final int PAST_RF_BUNDLES_TO_KEEP = 5;
    private final static ImmutableSet<PaoType> rfGatewayTypes = ImmutableSet.of(PaoType.RFN_GATEWAY, PaoType.GWY800, PaoType.GWY801);

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired GlobalSettingDao globalSettingDao;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private RfnGatewayDataCache dataCache;
    @Autowired private RfnGatewayService rfnGatewayService;

    private RequestReplyReplyTemplate<RfnSupportBundleResponse, RfnSupportBundleResponse> template;
    private RequestReplyTemplate<RfnMetadataMultiResponse> metaDataMultiRequestTemplate;
    private RfnSupportBundleResponseType responseStatus;

    @PostConstruct
    public void initialize() {
        YukonJmsTemplate jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_SUPPORT_BUNDLE);
        template = new RequestReplyReplyTemplate<>("RF_SUPPORT_BUNDLE", configurationSource, jmsTemplate);
        YukonJmsTemplate metadataMultiJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_METADATA_MULTI);
        metaDataMultiRequestTemplate = new RequestReplyTemplateImpl<>("RF_METADATA_MULTI", configurationSource, metadataMultiJmsTemplate, true);
    }

    public void send(RfnSupportBundleRequest request) {
        JmsReplyReplyHandler<RfnSupportBundleResponse, RfnSupportBundleResponse> handler = new JmsReplyReplyHandler<>() {

            @Override
            public void complete() {
                log.info("Completed Rf network support request.");
            }

            @Override
            public void handleException(Exception e) {
                log.error("Rf Network data collection failed", e);
                responseStatus = RfnSupportBundleResponseType.FAILED;
            }

            @Override
            public boolean handleReply1(RfnSupportBundleResponse statusReply) {
                log.info(request + " - received reply1(" + statusReply.getResponseType() + ") from NM ");
                if (RfnSupportBundleResponseType.INPROGRESS == statusReply.getResponseType()) {
                    return false;
                }
                responseStatus = statusReply.getResponseType();
                return true;
            }

            @Override
            public void handleReply2(RfnSupportBundleResponse statusReply) {
                log.info(request + " - received reply2(" + statusReply.getResponseType() + ") from NM ");
                String token = statusReply.getToken();
                if (RfnSupportBundleResponseType.COMPLETED == statusReply.getResponseType()) {
                    File cutomerDir = createCutomerDirectory(request.getFileName());
                    sendRfSupportBundleDownloadRequest(token, cutomerDir.getPath(), request.getFileName());
                    sendLocationDataRequest(request);
                    sendElectricNodeDataRequest(request);
                    zipRfSupportBundle(cutomerDir.getAbsolutePath());
                    removeOldFiles();
                }
                responseStatus = statusReply.getResponseType();
            }

            @Override
            public void handleTimeout1() {
                log.info(request + " - RF network data collection request timed out.");
                responseStatus = RfnSupportBundleResponseType.TIMEOUT;
            }

            @Override
            public void handleTimeout2() {
                log.info(request + " - RF network data collection request timed out.");
                responseStatus = RfnSupportBundleResponseType.TIMEOUT;
            }

            @Override
            public Class<RfnSupportBundleResponse> getExpectedType1() {
                return RfnSupportBundleResponse.class;
            }

            @Override
            public Class<RfnSupportBundleResponse> getExpectedType2() {
                return RfnSupportBundleResponse.class;
            }
        };
        template.send(request, handler);
    }

    public RfnSupportBundleResponseType getStatus() {
        return responseStatus;
    }

    /**
     * Send data collection request for location data i.e meterLocation, relay location and gatewayLocation.
     */
    private void sendLocationDataRequest(RfnSupportBundleRequest request) {
        String dateTime = getFormatedDateStr(request.getFromTimestamp());
        String destDir = supportBundleDirectory + "RfNetworkData/" + request.getFileName() + File.separator + locationDataDir
                + "_" + dateTime;
        buildAndSendMutiDataRequest(request, PaoType.getRfMeterTypes(), destDir, meterLocationFileName, meterLocationcolumnCount,
                RfnNetworkDataType.LOCATIONDATA, RfnMetadataMulti.NODE_DATA);
        buildAndSendMutiDataRequest(request, PaoType.getRfRelayTypes(), destDir, relayLocationFileName, relayLocationcolumnCount,
                RfnNetworkDataType.LOCATIONDATA, RfnMetadataMulti.NODE_DATA);
        buildAndWriteGatewayLocationData(request, destDir, gatewayLocationFileName);
    }
    
    /**
     * Send electric node data request for network snapshot.
     */
    private void sendElectricNodeDataRequest(RfnSupportBundleRequest request) {
        String dateTime = getFormatedDateStr(request.getFromTimestamp());
        String destDir = supportBundleDirectory + "RfNetworkData/" + request.getFileName() + File.separator
                + networkSnapshotDataDir + "_" + dateTime;
        buildAndSendMutiDataRequest(request, PaoType.getRftypes(), destDir, electricNodeLocationFileName,
                electricNodecolumnCount, RfnNetworkDataType.NETWORKSNAPSHOTDATA, RfnMetadataMulti.NODE_DATA,
                RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM);
    }

    /**
     * Zip network snapshot, location data and Rf Support Bundle directory, and delete the original directory once zipping is
     * done.
     */
    private void zipRfSupportBundle(String dirPath) {
        try {
            File tmpDir = new File(dirPath);
            if (!tmpDir.exists() && !tmpDir.mkdir()) {
                String errorMsg = "Unable to create directory at " + dirPath;
                if (log.isErrorEnabled()) {
                    log.error(errorMsg);
                }
            }
            String zipFileExtention = ".zip";
            File[] files = tmpDir.listFiles((file) -> {
                return !file.getAbsolutePath().contains(zipFileExtention) && file.isDirectory();
            });
            if (files != null && files.length > 0) {
                for (File file : files) {
                    FileUtil.zipDir(file.getPath(), file.list(), file.getPath() + zipFileExtention);
                    FileUtil.deleteAllFilesInDirectory(file.getPath());
                }
            }

            String zippedDirPath = dirPath + ".zip";
            FileUtil.zipFolder(dirPath, zippedDirPath);
            FileUtil.deleteAllFilesInDirectory(dirPath);
            Files.deleteIfExists(Paths.get(dirPath));
        } catch (IOException ex) {
            log.error("Error fould while zipping Rfn Support Bundle directory.");
        }
    }

    /**
     * Send https request to Network Manager server for downloading RfSupportBundle.
     * @param token : token which is generated at NM server side and shared through RfnSupportBundleResponse.
     * @param fileName : Name of file which needs to be download from NM server.
     */
    private void sendRfSupportBundleDownloadRequest(String token, String destDir, String fileName) {
        initializeHttpsSetting();
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);
        HttpURLConnection conn = null;
        URL url = null;
        try {
            String zipFileName = fileName + ".zip";
            url = new URL(getNetworkManagerUrl() + downloadRfSupportBundleURL + zipFileName);
            
            log.info("Sending rf support bundle request for " + fileName + " to " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.connect();

            InputStream in = conn.getInputStream();
            // Write support bundle data to the destination directory.
            writeSupportBundleData(in, destDir);
        } catch (FileNotFoundException ex) {
            log.error("Error while sending DownloadRfSupportBundle request. Check global settings for Network Manager server URL. " + ex);
        } catch (Exception ex) {
            log.error("Error while sending DownloadRfSupportBundle request. " + ex);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Collect data from InputStream and write to the destDir.
     * @param in : InputStream
     * @param destDir : Destination directory
     * @throws IOException
     */
    private void writeSupportBundleData(InputStream in, String destDir) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(in);
        ZipEntry entry;
        new File(destDir).mkdir();
        while ((entry = zipIn.getNextEntry()) != null) {
            String filePath = destDir + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it and write to the file.
                extractAndWriteFileData(zipIn, filePath);
            }
            zipIn.closeEntry();
        }
    }

    private void extractAndWriteFileData(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    /**
     * Create customer directory inside Rf support bundle directory.
     */
    private File createCutomerDirectory(String customerDir) {
        File file = getRfBundleDir();
        if(!file.isDirectory()) {
            file.mkdir();
        }
        File customerDataDir = new File (file + File.separator + customerDir);
        customerDataDir.mkdir();
        return customerDataDir;
    }

    private void initializeHttpsSetting() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509ExtendedTrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
                    throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
                    throws CertificateException {
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            log.error("Could not initialize HTTPS settings " + e);
        }
    }

    /**
     * Return value of Network Manager server URL.
     */
    private String getNetworkManagerUrl() {
        return globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_ADDRESS);
    }

    /**
     * Return File for Rf Support bundle directory.
     */
    private File getRfBundleDir() {
        String supportBundleDir = CtiUtilities.getYukonBase() + supportBundleDirectory;
        if (!new File(supportBundleDir).exists()) {
            File file = new File(supportBundleDir);
            file.mkdir();
        }
        return new File(supportBundleDir + "RfNetworkData");
    }

    /**
     * Deleted Old RF Support Bundle files from RfNetworkData directory.
     */
    private void removeOldFiles() {
        if (PAST_RF_BUNDLES_TO_KEEP >= 0) {
            List<File> allFiles = FileUtil.filterAndOrderZipFile(getRfBundleDir());
            for (int i = PAST_RF_BUNDLES_TO_KEEP; i < allFiles.size(); i++) {
                log.info("Deleted Old RF Support Bundle: " + allFiles.get(i).getName());
                allFiles.get(i).delete();
            }
        }
    }

    /**
     * Return date string in yyyyMMddHHmmss format.
     */
    private String getFormatedDateStr(long millis) {
        return new DateTime(millis).toString(DateTimeFormat.forPattern("yyyyMMddHHmmss"));
    }
    
    /**
     * Build gateway location data from RfnGatewayDataCache and write to the gateway location file.
     */
    private void buildAndWriteGatewayLocationData(RfnSupportBundleRequest request, String destDir, String fileName) {
        int startIndex = 1;
        int endIndex = batchsize;
        List<LocationData> dataList = null;
        while (dataList == null || dataList.size() >= batchsize) {
            dataList = paoLocationDao.getLocationDetailForPaoType(rfGatewayTypes, startIndex, endIndex);
            if (dataList != null && !dataList.isEmpty()) {
                SupportBundleHelper.buildAndWriteGatewayLocationDataToDir(dataList, destDir, fileName, dataCache);
            }
            startIndex = startIndex + batchsize;
            endIndex = endIndex + batchsize;
        }
    }

    /**
     * Build Metadata Request, Send request to NM and write data to the csv file.
     */
    private void buildAndSendMutiDataRequest(RfnSupportBundleRequest bundleRequest, Set<PaoType> paoTypes, String destDir,
            String fileName, int coloumCount, RfnNetworkDataType networkType, RfnMetadataMulti... rfnMetadatas) {
        int startIndex = 1;
        int endIndex = batchsize;
        List<LocationData> dataList = null;
        while (dataList == null || dataList.size() >= batchsize) {
            dataList = paoLocationDao.getLocationDetailForPaoType(paoTypes, startIndex, endIndex);
            if (dataList != null && !dataList.isEmpty()) {
                BlockingJmsReplyHandler<RfnMetadataMultiResponse> replyHandler = new BlockingJmsReplyHandler<>(
                        RfnMetadataMultiResponse.class);
                try {
                    // Build Metadata Request
                    RfnMetadataMultiRequest request = new RfnMetadataMultiRequest();
                    request.setRfnMetadatas(rfnMetadatas);
                    Set<RfnIdentifier> rfnIdentifiers = new HashSet<RfnIdentifier>();
                    dataList.stream().forEach(data -> rfnIdentifiers.add(data.getRfnIdentifier()));
                    request.setRfnIdentifiers(rfnIdentifiers);

                    // Send Request to collect Node data.
                    metaDataMultiRequestTemplate.send(request, replyHandler);
                    RfnMetadataMultiResponse response = replyHandler.waitForCompletion();
                    // Write data to csv file.
                    if (networkType == RfnNetworkDataType.NETWORKSNAPSHOTDATA) {
                        SupportBundleHelper.buildAndWriteElectricNodeDataToDir(response, dataList, destDir, fileName,
                                rfnGatewayService, coloumCount);
                    } else if (networkType == RfnNetworkDataType.LOCATIONDATA) {
                        SupportBundleHelper.buildAndWriteLocationDataToDir(response, dataList, destDir, fileName, coloumCount);
                    }
                    startIndex = startIndex + batchsize;
                    endIndex = endIndex + batchsize;
                } catch (ExecutionException | IOException ex) {
                    log.error("Error found while sending RfnMetadataMultiRequest for " + fileName + " node data.", ex);
                }
            } else {
                log.info("No data found for " + fileName);
            }
        }
    }
}
