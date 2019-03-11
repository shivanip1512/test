package com.cannontech.web.dev;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.util.io.Streams;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUtil;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.ecobee.message.EcobeeJobStatus;
import com.cannontech.dr.ecobee.message.EcobeeReportJob;
import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.RuntimeReportJobRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobResponse;
import com.cannontech.dr.ecobee.message.RuntimeReportJobStatusResponse;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.cannontech.dr.ecobee.service.EcobeeStatusCode;
import com.cannontech.encryption.EcobeeSecurityService;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.tools.csv.CSVWriter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EcobeeMockApiService {

    private static final Logger log = YukonLogManager.getLogger(EcobeeMockApiService.class);

    private static final Duration fiveMinutes = Duration.standardMinutes(5);
    private Cache<String,String[]> jobURLCache = CacheBuilder.newBuilder().build();
    private Cache<String,EcobeeJobStatus> jobStatusCache = CacheBuilder.newBuilder().build();
    private static final String[] ecobeeDataHeader = new String [] {"date",
                                                                    "time", 
                                                                    "zoneCalendarEvent",
                                                                    "zoneAveTemp",
                                                                    "outdoorTemp",
                                                                    "zoneCoolTemp",
                                                                    "zoneHeatTemp",
                                                                    "compCool1",
                                                                    "compHeat1"
                                                                    };

    @Autowired private EcobeeDataConfiguration ecobeeDataConfiguration;
    @Autowired private PaoDao paoDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private EcobeeSecurityService ecobeeSecurityService;

    public HierarchyResponse getHierarchyList() {
        List<SetNode> setNodes = new ArrayList<SetNode>();
        long thermostat = 222222;
        String node = "Node";
        for (int i = 0; i < 300; i++, thermostat++) {
            List<String> thermostats = new ArrayList<String>();
            thermostats.add(String.valueOf(thermostat));
            SetNode setNode = new SetNode(node + i, "\\data", null, thermostats);
            setNodes.add(setNode);
        }

        HierarchyResponse hierarchyResponse = new HierarchyResponse(setNodes, new Status(ecobeeDataConfiguration.getHierarchy(), "Hierarchy Tested"));
        return hierarchyResponse;
    }

    public RuntimeReportJobResponse createRuntimeReportJob(RuntimeReportJobRequest request) {
        // Generate random jodId for each request.
        String jobId = UUID.randomUUID().toString().replace("-", "");
        // Set initial jobStatus = QUEUED and put it it the cache.
        EcobeeJobStatus jobStatus = EcobeeJobStatus.QUEUED;
        jobStatusCache.invalidateAll();
        jobStatusCache.put(jobId, EcobeeJobStatus.QUEUED);
        Status status = new Status(EcobeeStatusCode.SUCCESS.getCode(), "");
        RuntimeReportJobResponse runtimeReportJobResponse = new RuntimeReportJobResponse(jobId, jobStatus, status);
        //Run a thread to execute task of building data files (URLs)
        new Thread(() -> {
            try {
                runTask(jobId, request);
            } catch (Exception e) {
                log.error("Error has occured while processing simulated job for ecobee run time job.");
                jobStatusCache.put(jobId, EcobeeJobStatus.ERROR);
            }
        }).start();
        return runtimeReportJobResponse;
    }

    public RuntimeReportJobStatusResponse getRuntimeJobStatus(String jobId) {
        // Return one EcobeeReportJob for one jobId (as per the current ecobee implementation).
        List<EcobeeReportJob> jobs = new ArrayList<>(1);
        EcobeeJobStatus jobStatus;
        jobStatus = jobStatusCache.getIfPresent(jobId);
        // Get the array of URLs from cache.
        String[] files = jobURLCache.getIfPresent(jobId);
        jobs.add(new EcobeeReportJob(jobId, jobStatus, "Simulated test message", files));
        Status status = new Status(EcobeeStatusCode.SUCCESS.getCode(), "");
        return new RuntimeReportJobStatusResponse(jobs, status);
    }

    private void runTask (String jobId , RuntimeReportJobRequest request) throws Exception  {
        // Write logic to generate data URLs.
        jobURLCache.invalidateAll();
        String dirPath = CtiUtilities.getYukonBase()+ "\\Simulator\\";
        new File(dirPath).mkdirs();
        // Cleanup old mock data files
        FileUtil.cleanUpDirectory(dirPath);
        Collection<String> selectionMatch = getSelectionMatch(request);
        for (String serialNumber : selectionMatch) {
            String fileName = serialNumber + "-" + jobId + ".csv";
            String csvFile = dirPath + fileName;
            try(CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile))) {
                csvWriter.writeNext(ecobeeDataHeader);
                Instant startTime = request.getStartDate();
                Instant endTime = request.getEndDate();
                for (Instant intervalStart = startTime; intervalStart.isBefore(endTime); intervalStart =
                                        intervalStart.plus(fiveMinutes)) {
                    csvWriter.writeNext(generateEcobeeReading(intervalStart));
                }
            }
        }
        // Further process CSV file for .tar.gz.gpg
        processCSVFile(dirPath, jobId);
    }

    /**
     * Process CSV file to .tar.gz.gpg file.
     * Put location of data file to cache and also update job status in cache. 
     */
    private void processCSVFile(String source, String jobId) throws Exception  {
        String[] fileURLs = new String[1];
        String tarGzDest = source + jobId + "-1.tar.gz";
        FileUtil.createTarGZFile(source, tarGzDest, ".csv");
        encryptFile(getPublicKey(), tarGzDest);
        // Clean .tar.gz file if encrypted file has generated.
        if (new File(tarGzDest + ".gpg").exists()) {
            new File(tarGzDest).delete();
        }
        fileURLs[0] = new File(tarGzDest + ".gpg").toURI().toString();
        jobURLCache.put(jobId, fileURLs);
        // Set jobStatus = COMPLETED , after generating URLs
        jobStatusCache.put(jobId, EcobeeJobStatus.COMPLETED);
    }

    /** 
     * Generate ecobee readings. 
     * 
     */
    private String[] generateEcobeeReading(Instant instant) {
        String date = instant.toDateTime().toLocalDate().toString();
        String time = instant.toDateTime().toLocalTime().toString("HH:mm:ss");
        int minute = instant.toDateTime().getMinuteOfHour();
        // Show event in last 30 minutes of every hour
        String zoneCalendarEvent = minute > 30 ? "Yukon Cycle" : "";
        // Indoor and outdoor temps rise through the hour
        String zoneAveTemp = String.valueOf(70.0f + (0.1f * minute));
        String outdoorTemp = String.valueOf(80.0f + (0.1f * minute));
        // Static setpoint values
        String zoneCoolTemp = "70";
        String zoneHeatTemp = "80";
        int randomValue = (int) (Math.random() * (300 - 0));
        String compCool1 = "0";
        String compHeat1 = "0";
        if (randomValue % 2 == 0) {
            compCool1 = String.valueOf(randomValue);
        } else {
            compHeat1 = String.valueOf(randomValue);
        }
        return new String[] {date, time, zoneCalendarEvent, zoneAveTemp, outdoorTemp, zoneCoolTemp, zoneHeatTemp,
            compCool1, compHeat1};
    }

    /**
     * Return selectMatch based on SelectionType.
     * If SelectionType == MANAGEMENT_SET returns all the ecobee devices present in the system
     * else return selection match present in the request
     */
    private Collection<String> getSelectionMatch(RuntimeReportJobRequest request) {
        List<String> selectionMatch = new ArrayList<>();
        if (request.getSelection().getSelectionType() == SelectionType.MANAGEMENT_SET) {
            List<LiteYukonPAObject> ecobeeDevices = new ArrayList<>();
            PaoType.getEcobeeTypes().forEach(type -> ecobeeDevices.addAll(paoDao.getLiteYukonPAObjectByType(type)));
            for (LiteYukonPAObject ecobeePao : ecobeeDevices) {
                PaoIdentifier pao = ecobeePao.getPaoIdentifier();
                String ecobeeSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(pao.getPaoId());
                selectionMatch.add(ecobeeSerialNumber);
            }
            return selectionMatch;
        } else {
            return request.getSelection().getSelectionMatch();
        }
    }

    /**
     * Encrypt ecobee data file. (form .tar.gz file to .tar.gz.gpg format)
     * 
     */
    private static void encryptFile(PGPPublicKey encKey, String filePath) throws IOException, PGPException {
        Security.addProvider(new BouncyCastleFipsProvider());
        File file = new File(filePath);
        byte[] byteArray = FileUtils.readFileToByteArray(file);  
        final ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        final PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator( CompressionAlgorithmTags.ZIP );
        final OutputStream pOut = lData.open( comData.open( bOut ), PGPLiteralData.BINARY, filePath , in.available(), new Date() );
        Streams.pipeAll( in, pOut );
        comData.close();

        byte[] plainText = bOut.toByteArray();
        ByteArrayOutputStream encOut = new ByteArrayOutputStream();
        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
        new JcePGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256)
                                                    .setWithIntegrityPacket(true)
                                                    .setSecureRandom(new SecureRandom())
                                                    .setProvider("BCFIPS"));
        encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider("BCFIPS"));
        
        OutputStream cOut = encGen.open(encOut, plainText.length);
        cOut.write(plainText);
        cOut.close();

        FileOutputStream fos = new FileOutputStream(filePath + ".gpg");
        fos.write(encOut.toByteArray());
        fos.flush();
        fos.close();
    }

    @SuppressWarnings("resource")
    private PGPPublicKey getPublicKey() throws Exception {
        PGPPublicKey key = null;
        String publicKey = StringUtils.EMPTY;
        publicKey = ecobeeSecurityService.getEcobeePGPPublicKey();
        InputStream in=new ByteArrayInputStream(publicKey.getBytes());
        in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);
        JcaPGPPublicKeyRingCollection pgpPub = new JcaPGPPublicKeyRingCollection(in);
        in.close();
        Iterator<PGPPublicKeyRing> rIt = pgpPub.getKeyRings();
            while (key == null && rIt.hasNext()) {
                PGPPublicKeyRing kRing = rIt.next();
                Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
                while (key == null && kIt.hasNext()) {
                    PGPPublicKey k = kIt.next();
                    if (k.isEncryptionKey()) {
                        key = k;
                    }
                }
            }
        return key;
    }
}
