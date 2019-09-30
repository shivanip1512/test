package com.cannontech.dr.rfn.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.openexi.proc.common.EXIOptionsException;
import org.openexi.sax.TransmogrifierException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.util.ByteUtil;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.message.archive.RfnLcrReading;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingType;
import com.cannontech.dr.rfn.model.PqrEventType;
import com.cannontech.dr.rfn.model.PqrResponseType;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnLcrReadSimulatorDeviceParameters;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.model.jaxb.DRReport;
import com.cannontech.dr.rfn.model.jaxb.DRReport.BroadcastVerificationMessages;
import com.cannontech.dr.rfn.model.jaxb.DRReport.BroadcastVerificationMessages.Event;
import com.cannontech.dr.rfn.model.jaxb.DRReport.ControlEvents;
import com.cannontech.dr.rfn.model.jaxb.DRReport.ExtendedAddresssing;
import com.cannontech.dr.rfn.model.jaxb.DRReport.HostDeviceID;
import com.cannontech.dr.rfn.model.jaxb.DRReport.Info;
import com.cannontech.dr.rfn.model.jaxb.DRReport.LUVEvents;
import com.cannontech.dr.rfn.model.jaxb.DRReport.Relays;
import com.cannontech.dr.rfn.model.jaxb.DRReport.Relays.Relay;
import com.cannontech.dr.rfn.model.jaxb.DRReport.Relays.Relay.IntervalData;
import com.cannontech.dr.rfn.model.jaxb.DRReport.Relays.Relay.IntervalData.Interval;
import com.cannontech.dr.rfn.model.jaxb.ObjectFactory;
import com.cannontech.dr.rfn.service.ExiParsingService;
import com.cannontech.dr.rfn.service.RfnLcrDataSimulatorService;
import com.cannontech.dr.rfn.tlv.TlvTypeLength;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class RfnLcrDataSimulatorServiceImpl extends RfnDataSimulatorService  implements RfnLcrDataSimulatorService {
    /**
     * This enum is used for TLV report generation. 
     * type : Each entry has a unique type associated with it. 
     * length : Length in bytes of the value.
     * frequency : Occurrence of field entry in the report. This helps to generate TLV report in single iteration.
     */
    public enum TLVField {
        SERIAL_NUMBER((byte)0x02, 4, 1),
        SSPEC((byte)0x03, 2, 1),
        FIRMWARE_VERSION((byte)0x04, 2, 1),
        SPID((byte)0x05, 2, 1),
        GEO_ADDRESS((byte)0x06, 2, 1),
        FEEDER_ADDRESS((byte)0x07, 2, 1),
        ZIP_ADDRESS((byte)0x08, 4, 1),
        UDA_ADDRESS((byte)0x09, 2, 1),
        REQUIRED_ADDRESS((byte)0x0a, 1, 1),
        SUBSTATION_ADDRESS((byte)0x0b, 2, 1),
        BLINK_COUNT((byte)0x0c, 1, 1),
        RELAY_INTERVAL_START_TIME((byte)0x0d, 4, 1),
        RELAY_N_RUNTIME((byte)0x0e, 25, 4),
        RELAY_N_SHEDTIME((byte)0x0f, 25, 4),
        RELAY_N_PROGRAM_ADDRESS((byte)0x10, 2, 4),
        RELAY_N_SPLINTER_ADDRESS((byte)0x11, 2, 4),
        RELAY_N_REMAINING_CONTROLTIME((byte)0x12, 5, 4),
        PROTECTION_TIME_RELAY_N((byte)0x13, 3, 3),
        CLP_TIME_FOR_RELAY_N((byte)0x14, 3, 3),
        //Power Quality Response fields:
        LOV_TRIGGER((byte)0x44, 2, 1, 5),
        LOV_RESTORE((byte)0x45, 2, 1, 5),
        LOV_TRIGGER_TIME((byte)0x46, 2, 1, 5),
        LOV_RESTORE_TIME((byte)0x47, 2, 1, 5),
        LOV_EVENT_COUNT((byte)0x48, 2, 1, 5),
        LOF_TRIGGER((byte)0x49, 2, 1, 5),
        LOF_RESTORE((byte)0x4a, 2, 1, 5),
        LOF_TRIGGER_TIME((byte)0x4b, 2, 1, 5),
        LOF_RESTORE_TIME((byte)0x4c, 2, 1, 5),
        LOF_EVENT_COUNT((byte)0x4d, 2, 1, 5),
        LOF_START_RANDOM_TIME((byte)0x4e, 2, 1, 5),
        LOF_END_RANDOM_TIME((byte)0x4f, 2, 1, 5),
        LOV_START_RANDOM_TIME((byte)0x50, 2, 1, 5),
        LOV_END_RANDOM_TIME((byte)0x51, 2, 1, 5),
        LOF_MIN_EVENT_DURATION((byte)0x52, 2, 1, 5),
        LOV_MIN_EVENT_DURATION((byte)0x53, 2, 1, 5),
        LOF_MAX_EVENT_DURATION((byte)0x54, 2, 1, 5),
        LOV_MAX_EVENT_DURATION((byte)0x55, 2, 1, 5),
        MINIMUM_EVENT_SEPARATION((byte)0x56, 2, 1, 5),
        POWER_QUALITY_RESPONSE_ENABLED((byte)0x58, 1, 1, 5),
		;

        private final byte type;
        private final int length;
        private final int frequency;
        private final int version;
        
        private TLVField(byte type, int length, int frequency) {
            this.type = type;
            this.length = length;
            this.frequency = frequency;
            version = 4;
        }
        
        private TLVField(byte type, int length, int frequency, int version) {
            this.type = type;
            this.length = length;
            this.frequency = frequency;
            this.version = version;
        }
        
        public static List<TLVField> getValuesForVersion(int tlvVersion) {
            return Arrays.stream(values())
                         .filter(field -> field.version <= tlvVersion)
                         .collect(Collectors.toList());
        }
    }
    private final Logger log = YukonLogManager.getLogger(RfnLcrDataSimulatorServiceImpl.class);
    
    private static final int pqrEventBlobTlvTypeId = 87;
    private static final String lcrReadingArchiveRequestQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest";

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private ExiParsingService<SimpleXPathTemplate> exiParsingService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private PerformanceVerificationDao performanceVerificationDao;
    private static final JAXBContext jaxbContext = initJaxbContext();
    
    //minute of the day to send a request at/list of devices to send a read request to
    private SetMultimap<Integer, RfnLcrReadSimulatorDeviceParameters> rangeDevices = HashMultimap.create();
    private SetMultimap<Integer, RfnLcrReadSimulatorDeviceParameters> allDevices = HashMultimap.create();
    
    private RfnDataSimulatorStatus rangeDevicesStatus = new RfnDataSimulatorStatus();
    private RfnDataSimulatorStatus allDevicesStatus = new RfnDataSimulatorStatus();
    
    static JAXBContext initJaxbContext() {
        try {
            return JAXBContext.newInstance("com.cannontech.dr.rfn.model.jaxb");
        } catch (JAXBException e) {
            throw new Error(e);
        }
    }
    
    @Override
    public void execute(int minuteOfTheDay) {
        sendReadRequests(rangeDevices.get(minuteOfTheDay), rangeDevicesStatus);
        sendReadRequests(allDevices.get(minuteOfTheDay), allDevicesStatus);
    }
     
    /**
     * Sends read requests.
     * 
     * @param devices to send the read request to
     * @param status to keep track of the result
     */
    private void sendReadRequests(Set<RfnLcrReadSimulatorDeviceParameters> devices, RfnDataSimulatorStatus status) {
        if (status.isRunning().get()) {
            if (!devices.isEmpty()) {
                log.debug("Sending read request to " + devices.size() +" devices.");
                status.setLastInjectionTime(new Instant());
            }
            for (RfnLcrReadSimulatorDeviceParameters device : devices) {
                simulateLcrReadRequest(device, status);
                if (needsDuplicate()) {
                    log.debug("Sending duplicate read request for " + device.getRfnIdentifier());
                    simulateLcrReadRequest(device, status);
                }
            }
        }
    }
    
    /**
     * Calculate the time of the day the read request should be send at.
     *
     * @param devices - minute of the day to send a request at/list of devices to send a read request to
     */
    private void addDevice(RfnIdentifier rfnIdentifier, SetMultimap<Integer, RfnLcrReadSimulatorDeviceParameters> devices, PaoType paoType, int tlvVersion){
        int minuteOfTheDay = getMinuteOfTheDay(rfnIdentifier.getSensorSerialNumber());
                
        RfnLcrReadSimulatorDeviceParameters parameters =
                new RfnLcrReadSimulatorDeviceParameters(rfnIdentifier, 0, 0, 3, 60, 24 * 60, paoType, tlvVersion);
        devices.put(minuteOfTheDay,  parameters);
    }
    
    @Override
    public synchronized void sendMessagesToAllDevices(SimulatorSettings settings) {
        log.debug("sendMessagesToAllDevices");
        if (!allDevicesStatus.isRunning().get()) {
            allDevicesStatus = new RfnDataSimulatorStatus();
            allDevicesStatus.setRunning(new AtomicBoolean(true));
            allDevicesStatus.setStartTime(new Instant());
            if (this.settings == null) {
                this.settings = settings;
            }
            List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfLcrTypes());
            for (RfnDevice device : devices) {
                addDevice(device.getRfnIdentifier(), allDevices, device.getPaoIdentifier().getPaoType(), settings.getTlvVersion());
            }
            //check if execution was canceled
            if (!allDevices.isEmpty() && allDevicesStatus.isRunning().get()) {
                logDebugInjectionTime(allDevices);
                schedule();
            } else {
                allDevices.clear();
            }
        }
    }

    @Override
    public synchronized void sendMessagesByRange(SimulatorSettings settings) {
        log.debug("sendMessagesByRange");
        if (!rangeDevicesStatus.isRunning().get()) {
            rangeDevicesStatus = new RfnDataSimulatorStatus();
            rangeDevicesStatus.setRunning(new AtomicBoolean(true));
            rangeDevicesStatus.setStartTime(new Instant());
            saveSettings(settings);
            if (this.settings == null) {
                this.settings = settings;
            }
            createDevicesByRange(settings.getLcr6200serialFrom(), settings.getLcr6200serialTo(),
                RfnManufacturerModel.RFN_LCR_6200, 0);
            createDevicesByRange(settings.getLcr6600serialFrom(), settings.getLcr6600serialTo(),
                RfnManufacturerModel.RFN_LCR_6600, 0);
            createDevicesByRange(settings.getLcr6700serialFrom(), settings.getLcr6700serialTo(),
                RfnManufacturerModel.RFN_LCR_6700, settings.getTlvVersion());
            //check if execution was canceled
            if (!rangeDevices.isEmpty() && rangeDevicesStatus.isRunning().get()) {
                logDebugInjectionTime(rangeDevices);
                schedule();
            } else {
                rangeDevices.clear();
            }
        }
    }

    private void createDevicesByRange(int from, int to, RfnManufacturerModel model, int tlvVersion) {
        while (from <= to) {
            RfnIdentifier rfnIdentifier =
                new RfnIdentifier(String.valueOf(from), model.getManufacturer(), model.getModel());
            log.debug("createDevicesByRange="+rfnIdentifier);
            addDevice(rfnIdentifier, rangeDevices, model.getType(), tlvVersion);
            from++;
        }
    }

    @Override
    @PreDestroy
    public synchronized void stopRangeSimulator() {
        log.info("RFN_LCR range simulator shutting down...");
        stopSimulator(rangeDevicesStatus, rangeDevices);
    }

    @Override
    @PreDestroy
    public synchronized void stopAllDeviceSimulator() {
        log.info("RFN_LCR all device simulator shutting down...");
        stopSimulator(allDevicesStatus, allDevices);
    }
    
    private void stopSimulator(RfnDataSimulatorStatus status, SetMultimap<Integer, RfnLcrReadSimulatorDeviceParameters> devices){
        status.setStopTime(new Instant());
        status.setRunning(new AtomicBoolean(false));
        devices.clear();  
        if(!rangeDevicesStatus.isRunning().get() && !allDevicesStatus.isRunning().get()){
            settings = null;
        }
    }
    
    @Override
    public RfnDataSimulatorStatus getStatusByRange() {
        return rangeDevicesStatus;
    }

    @Override
    public RfnDataSimulatorStatus getAllDevicesStatus() {
        return allDevicesStatus;
    }

    private void logDebugInjectionTime(SetMultimap<Integer, RfnLcrReadSimulatorDeviceParameters> devices){
        if(log.isDebugEnabled()){
            for(Integer runAt: devices.keySet()){
                DateTime now = new DateTime();
                int minuteNow = now.getMinuteOfDay();
                DateTime injectionTime = new DateTime().withTimeAtStartOfDay();
                if(runAt < minuteNow ){
                    injectionTime = injectionTime.plusDays(1);
                }
                injectionTime = injectionTime.plusMinutes(runAt);
                log.debug("Values for "+devices.get(runAt).size()+" devices will be injected at "+runAt +" minute of the day ("+injectionTime.toString("MM/dd/YYYY HH:mm")+")");
            }
        }
    }
    
    @Override
    public SimulatorSettings getCurrentSettings() {
        if (settings == null) {
            log.debug("Getting RFN_LCR SimulatorSettings from db.");
            SimulatorSettings simulatorSettings = new SimulatorSettings();
            simulatorSettings.setLcr6200serialFrom(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6200_SERIAL_FROM));
            simulatorSettings.setLcr6200serialTo(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6200_SERIAL_TO));
            simulatorSettings.setLcr6600serialFrom(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6600_SERIAL_FROM));
            simulatorSettings.setLcr6600serialTo(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6600_SERIAL_TO));
            simulatorSettings.setLcr6700serialFrom(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6700_SERIAL_FROM));
            simulatorSettings.setLcr6700serialTo(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6700_SERIAL_TO));
            simulatorSettings.setPercentOfDuplicates(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_DUPLICATE_PERCENTAGE));
            simulatorSettings.setTlvVersion(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_TLV_VERSION));
            settings = simulatorSettings;
        }
        return settings;
    }
    
    public void saveSettings(SimulatorSettings settings) {
        log.debug("Saving RFN_LCR settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6200_SERIAL_FROM, settings.getLcr6200serialFrom());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6200_SERIAL_TO, settings.getLcr6200serialTo());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6600_SERIAL_FROM, settings.getLcr6600serialFrom());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6600_SERIAL_TO, settings.getLcr6600serialTo());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6700_SERIAL_FROM, settings.getLcr6700serialFrom());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_6700_SERIAL_TO, settings.getLcr6700serialTo());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_DUPLICATE_PERCENTAGE, settings.getPercentOfDuplicates());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_LCR_SIMULATOR_TLV_VERSION, settings.getTlvVersion());
    }

    /**
     * This method creates an RFN LCR read archive request and places it into the appropriate messaging queue.
     * 
     * @param deviceParameters Specifies the parameters of the device that generated the request.
     * @param status - tracks success and failure
     */
    private void simulateLcrReadRequest(RfnLcrReadSimulatorDeviceParameters deviceParameters, RfnDataSimulatorStatus status) {
        try {
            RfnLcrReadingArchiveRequest readArchiveRequest = createReadArchiveRequest(deviceParameters);
            jmsTemplate.convertAndSend(lcrReadingArchiveRequestQueueName, readArchiveRequest);
            status.getSuccess().incrementAndGet();
            
        } catch (RfnLcrSimulatorException | IOException e) {
            log.warn("There was a problem creating an RFN LCR archive read request for device: "
                + deviceParameters.getRfnIdentifier().getSensorManufacturer() + "/"
                + deviceParameters.getRfnIdentifier().getSensorModel() + "/"
                + deviceParameters.getRfnIdentifier().getSensorSerialNumber(), e);
            status.getFailure().incrementAndGet();
        } 
    }

    /**
     * Creates the archive request by generating a simulated XML/TLV (for LCR-6700) payload, encoding it into the EXI format(for LCR-6200 and LCR-6600) and
     * placing it into the RfnLcrReadingArchiveRequest object.
     * 
     * @param simulatorSettings The current simulation settings, including MessageId & timestamp.
     * @param deviceParameters Specifies the device that is generating the request.
     * @return The simulated request object.
     * @throws RfnLcrSimulatorException Thrown if there is a problem generating the XML or encoded EXI data.
     * @throws IOException
     */
    private RfnLcrReadingArchiveRequest createReadArchiveRequest(RfnLcrReadSimulatorDeviceParameters deviceParameters)
            throws RfnLcrSimulatorException, IOException {
        byte[] payload = null;
        if (deviceParameters.getPaoType().isTlvReporting()) {
            try {
                //TODO: This stuff should be configurable to more easily support future device types and TLV fields
                //TODO: TLV version handling will need to be more complicated if/when major/minor version ever changes
                String expresscomHeader = "E20170";
                String deviceTypeId = "043E"; //0x043E = 1086 (LCR-6700)
                String tlvMajorMinor = "00"; //TLV version = major.minor.revision. Major/minor in first byte...
                String tlvRevision = "0" + deviceParameters.getTlvVersion(); //...revision in next byte
                String headerCountTypeAndLength = "000002"; //"TLV Count" - TypeId: 0, Length: 2 bytes
                String headerCountValue = "0059"; //TLV type IDs supported by device. 
                                                  //For 0.0.4: 0x49 = 73. For 0.0.5: 0x59 = 89.
                byte[] header = Hex.decode(expresscomHeader + deviceTypeId + tlvMajorMinor + tlvRevision + 
                                           headerCountTypeAndLength + headerCountValue);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(header);
                // Add UTC 
                DateTime reportTime = new DateTime(DateTimeZone.UTC).withTimeAtStartOfDay();
                writeUTC(outputStream, reportTime);
                
                // Generate TLV values that translate to point data
                byte[] data = generateTlvPointData(deviceParameters.getTlvVersion());
                outputStream.write(data);
                
                //Add PQR event log blob
                if (deviceParameters.getTlvVersion() > 4) {
                    byte[] pqrEvents = generatePqrEvents(reportTime);
                    outputStream.write(pqrEvents);
                }
                
                // Add verification messages
                outputStream.write((0x16 >> 4) & 0x0f); // Upper nibble of first byte
                outputStream.write((0x16 & 0x0f) << 4); // Lower nibble of second byte
                List<PerformanceVerificationEventMessage> eventMessages = loadPerformanceVerificationEventMessages();
                byte messageLength;
                if (eventMessages != null && !eventMessages.isEmpty()) {
                    // Add message length (No of bytes)
                    if (eventMessages.size() >= 16) {
                        // Max message length
                        messageLength = (byte) 128;
                    } else {
                        messageLength = (byte) (eventMessages.size() * 8);
                    }
                    outputStream.write(messageLength);
                    int messageCount = 0; // Counter to limit message count upto 16
                    for (PerformanceVerificationEventMessage eventMsg : eventMessages) {
                        if (messageCount > 15) {
                            break;
                        }
                        // Write first 4 byte as messageId
                        writeLongValueAsByteArray(outputStream, eventMsg.getMessageId());
                        // Write last 4 byte as timeStamp
                        writeLongValueAsByteArray(outputStream, eventMsg.getTimeMessageSent().getMillis() / 1000);
                        messageCount++;
                    }
                } else {
                    // Add no message
                    outputStream.write(Hex.decode("00")); // 0 message length
                }

                // Add LUF events
                byte[] lufEvents = Hex.decode("01700200000180020000");
                outputStream.write(lufEvents);
                // Service (In-service) and Control state (false)
                byte[] serviceAndControlState = Hex.decode("0190010001a00100");
                outputStream.write(serviceAndControlState);

                payload = outputStream.toByteArray();
            } catch (Exception e) {
                throw new RfnLcrSimulatorException("There was an error generating the TLV payload for serial number "
                        + deviceParameters.getRfnIdentifier().getSensorSerialNumber(), e);
            }
        } else {
            // Generate raw XML for archive reading.
            String xmlData = null;
            try {
                xmlData = getLcrReadXmlPayload(deviceParameters);
                if (log.isTraceEnabled()) {
                    log.trace("RFN LCR simulator - device: " + deviceParameters.getRfnIdentifier().getSensorManufacturer()
                        + "/" + deviceParameters.getRfnIdentifier().getSensorModel() + "/"
                        + deviceParameters.getRfnIdentifier().getSensorSerialNumber() + " payload: " + xmlData);
                }
            } catch (JAXBException e) {
                throw new RfnLcrSimulatorException("There was an error generating the XML payload for serial number "
                    + deviceParameters.getRfnIdentifier().getSensorSerialNumber(), e);
            } catch (Exception e) {
                throw new RfnLcrSimulatorException("There was an error generating the XML payload for serial number "
                        + deviceParameters.getRfnIdentifier().getSensorSerialNumber(), e);
            }
            // Create EXI encoded payload with status header.
            byte[] encodedXml = null;
            try {
                encodedXml = exiParsingService.encodePayload(xmlData);
            } catch (TransmogrifierException | EXIOptionsException | IOException e) {
                throw new RfnLcrSimulatorException("There was an error encoding XML payload to EXI for serial number "
                    + deviceParameters.getRfnIdentifier().getSensorManufacturer() + "/"
                    + deviceParameters.getRfnIdentifier().getSensorModel() + "/"
                    + deviceParameters.getRfnIdentifier().getSensorSerialNumber(), e);
            }
            payload = createPayload(deviceParameters, encodedXml);
        }

        RfnLcrReadingArchiveRequest readArchiveRequest = new RfnLcrReadingArchiveRequest();
        RfnLcrReading reading = new RfnLcrReading();
        reading.setPayload(payload);
        reading.setTimeStamp(new Instant().getMillis());
        readArchiveRequest.setData(reading);
        readArchiveRequest.setRfnIdentifier(deviceParameters.getRfnIdentifier());
        readArchiveRequest.setType(RfnLcrReadingType.UNSOLICITED);
        return readArchiveRequest;
    }
    
    private byte[] generatePqrEvents(DateTime reportTime) {
        // Build a reasonable series of PqrEvents with timestamps based on reportTime
        // Values are all voltages, in 10ths of a volt
        List<List<Byte>> events = new ArrayList<>();
        
        Instant startTime = reportTime.minus(Duration.standardHours(8)).toInstant(); //reportTime - 8h
        events.add(buildEventBytes(startTime, PqrEventType.EVENT_DETECTED, PqrResponseType.OVER_VOLTAGE, (short)2421));
        
        Instant activeTime = startTime.plus(Duration.standardSeconds(2)); //startTime + 2s (1 for activation, 1 for randomization)
        events.add(buildEventBytes(activeTime, PqrEventType.EVENT_SUSTAINED, PqrResponseType.OVER_VOLTAGE, (short)2422));
        
        Instant inactiveTime = activeTime.plus(Duration.standardMinutes(5)); //activeTime + 5m
        events.add(buildEventBytes(inactiveTime, PqrEventType.RESTORE_DETECTED, PqrResponseType.OVER_VOLTAGE, (short)2409));
        
        Instant endTime = inactiveTime.plus(Duration.standardSeconds(2)); //inactiveTime + 2s (1 for deactivation, 1 for randomization)
        events.add(buildEventBytes(endTime, PqrEventType.EVENT_COMPLETE, PqrResponseType.OVER_VOLTAGE, (short)2400));
        
        // Add the PQR log blob Type and Length values
        // Each event is 8 bytes in length, so the entire field's length is (8 * the # of events)
        int eventsLengthInBytes = 8 * events.size();
        TlvTypeLength pqrEventsTypeLength = new TlvTypeLength(pqrEventBlobTlvTypeId, eventsLengthInBytes);
        
        List<Byte> bytes = new ArrayList<>();
        bytes.addAll(pqrEventsTypeLength.bytesList());
        
        //Add the event values
        bytes.addAll(events.stream()
                           .flatMap(List::stream)
                           .collect(Collectors.toList()));
        
        return ByteUtil.convertListToArray(bytes);
    }
    
    private List<Byte> buildEventBytes(Instant timestamp, PqrEventType eventType, PqrResponseType responseType, short value) {
        List<Byte> bytes = new ArrayList<>();
        bytes.add(eventType.getValue());
        bytes.addAll(ByteUtil.getTimestampBytesList(timestamp));
        bytes.add(responseType.getValue());
        ByteBuffer valueBytes = ByteBuffer.allocate(2).putShort(value);
        bytes.add(valueBytes.get(0));
        bytes.add(valueBytes.get(1));
        return bytes;
    }
    
    private void writeUTC(ByteArrayOutputStream outputStream, DateTime reportTime) {
        // UTC type
        outputStream.write((0x01 >> 4) & 0x0f);
        outputStream.write((0x01 & 0x0f) << 4);
        // UTC length as 4-byte
        outputStream.write(4);
        // Generate UTC field values (4-byte)
        long timeInSec = reportTime.getMillis() / 1000;
        writeLongValueAsByteArray(outputStream, timeInSec);
    }

    /*
     * This method converts long value into 4-byte array and writes into ByteArrayOutputStream
     */
    private void writeLongValueAsByteArray(ByteArrayOutputStream outputStream, long data) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        byte[] byteData = buffer.putLong(data).array();
        byteData = Arrays.copyOfRange(byteData, 4, byteData.length);
        for (int i = 0; i < byteData.length; i++) {
            outputStream.write(byteData[i]);
        }
    }
    
    /**
     * Generate byte Array (part of TLV report) from TLVField enum and generate values for each field.
     */
    private byte[] generateTlvPointData(int tlvVersion) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        for (TLVField field : TLVField.getValuesForVersion(tlvVersion)) {
            int fieldCount = 0;
            while (fieldCount < field.frequency) {
                int valueIndex = 0;
                
                TlvTypeLength typeLength = new TlvTypeLength(field.type, field.length);
                data.write(typeLength.byte1);
                data.write(typeLength.byte2); 
                data.write(typeLength.byte3);
                
                switch(field) {
                    case RELAY_N_RUNTIME:
                    case RELAY_N_SHEDTIME:
                    case RELAY_N_PROGRAM_ADDRESS:
                    case RELAY_N_SPLINTER_ADDRESS:
                        data.write((byte) fieldCount);
                        valueIndex++;
                        break;
                    case RELAY_N_REMAINING_CONTROLTIME:
                        // 5 elements, first is relay number, last 4 are control time in seconds.
                        int remainingSeconds = 60*60*(valueIndex+1); // relay 1 = 1 hour, relay 2 = 2 hour.... 
                        data.write((byte) fieldCount);
                        data.write((byte) (remainingSeconds >> 24));
                        data.write((byte) (remainingSeconds >> 16));
                        data.write((byte) (remainingSeconds >> 8));
                        data.write((byte) remainingSeconds);
                        valueIndex = field.length; // Took care of this field in its entirety
                        break;
                    case PROTECTION_TIME_RELAY_N:
                    case CLP_TIME_FOR_RELAY_N:
                         // 3 elements, first is relay number, the rest is a time possibly in seconds?
                        data.write((byte) fieldCount);
                        data.write((byte) 0);
                        data.write((byte) 120);
                        valueIndex = field.length; // Took care of this field in its entirety
                        break;
                    case RELAY_INTERVAL_START_TIME:
                        // Add relay interval start time in sec(24 hour before UTC field value).Hourly data will be added after this till UTC (field value).
                        long intervalStartTime = new DateTime(DateTimeZone.UTC).minusHours(24).withTimeAtStartOfDay().getMillis()/1000;
                        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                        byte[] relayStartTime = buffer.putLong(intervalStartTime).array();
                        // Add relay interval start time to byte array.
                        for (int i = 4; i < relayStartTime.length; i++) {
                            data.write(relayStartTime[i]);
                        }
                        valueIndex = field.length;
                        break;
                    case LOV_TRIGGER:
                        insertTwoByteTlvField(data, (short) 2530); // 1/10 volts
                        valueIndex = field.length;
                        break;
                    case LOV_RESTORE:
                        insertTwoByteTlvField(data, (short) 2525); // 1/10 volts
                        valueIndex = field.length;
                        break;
                    case LOV_TRIGGER_TIME:
                        insertTwoByteTlvField(data, (short) 500); // millis
                        valueIndex = field.length;
                        break;
                    case LOV_RESTORE_TIME:
                        insertTwoByteTlvField(data, (short) 500); // millis
                        valueIndex = field.length;
                        break;
                    case LOV_EVENT_COUNT:
                        insertTwoByteTlvField(data, (short) 10); // counts
                        valueIndex = field.length;
                        break;
                    case LOF_TRIGGER:
                        //1000 millis / 60hz = 16.66... millis per oscillation
                        insertTwoByteTlvField(data, (short) 17); // millis
                        valueIndex = field.length;
                        break;
                    case LOF_RESTORE:
                        //1000 millis / 60hz = 16.66... millis per oscillation
                        insertTwoByteTlvField(data, (short) 18); // millis
                        valueIndex = field.length;
                        break;
                    case LOF_TRIGGER_TIME:
                        insertTwoByteTlvField(data, (short) 167); // millis
                        valueIndex = field.length;
                        break;
                    case LOF_RESTORE_TIME:
                        insertTwoByteTlvField(data, (short) 167); // millis
                        valueIndex = field.length;
                        break;
                    case LOF_EVENT_COUNT:
                        insertTwoByteTlvField(data, (short) 10); // counts
                        valueIndex = field.length;
                        break;
                    case LOF_START_RANDOM_TIME:
                        insertTwoByteTlvField(data, (short) 167); // millis
                        valueIndex = field.length;
                        break;
                    case LOF_END_RANDOM_TIME:
                        insertTwoByteTlvField(data, (short) 167); // millis
                        valueIndex = field.length;
                        break;
                    case LOV_START_RANDOM_TIME:
                        insertTwoByteTlvField(data, (short) 500); // millis
                        valueIndex = field.length;
                        break;
                    case LOV_END_RANDOM_TIME:
                        insertTwoByteTlvField(data, (short) 500); // millis
                        valueIndex = field.length;
                        break;
                    case LOF_MIN_EVENT_DURATION:
                        insertTwoByteTlvField(data, (short) 60); // seconds
                        valueIndex = field.length;
                        break;
                    case LOV_MIN_EVENT_DURATION:
                        insertTwoByteTlvField(data, (short) 60); // seconds
                        valueIndex = field.length;
                        break;
                    case LOF_MAX_EVENT_DURATION:
                        insertTwoByteTlvField(data, (short) (10*60)); // seconds
                        valueIndex = field.length;
                        break;
                    case LOV_MAX_EVENT_DURATION:
                        insertTwoByteTlvField(data, (short) (15*60)); // seconds
                        valueIndex = field.length;
                        break;
                    case MINIMUM_EVENT_SEPARATION:
                        insertTwoByteTlvField(data, (short) 60); // seconds
                        valueIndex = field.length;
                        break;
                    case POWER_QUALITY_RESPONSE_ENABLED:
                        data.write((byte) 1); // 0 = disabled, >0 = enabled
                        valueIndex = field.length;
                        break;
                    default:
                        break;
                }
                // Fill in remaining values with numbers based on their field ID. (Affects all TLVFields not handled above).
                while (valueIndex < field.length) {
                    data.write(field.type);
                    valueIndex++;
                }
                fieldCount++;
            }
        }
        
        return data.toByteArray();
    }

    private void insertTwoByteTlvField(ByteArrayOutputStream data, short value) {
        ByteBuffer bytes = ByteBuffer.allocate(2).putShort(value);
        data.write(bytes.get(0));
        data.write(bytes.get(1));
    }
    
    /**
     * Generates the raw XML report to be encoded.
     * 
     * @param simulatorSettings The current simulation settings, including MessageId & timestamp.
     * @param deviceParameters Specifies the device that is generating the request.
     * @return The DR report XML string.
     * @throws JAXBException Thrown if there is a problem creating JAXB objects or marshaling the XML.
     */
    private String getLcrReadXmlPayload(RfnLcrReadSimulatorDeviceParameters deviceParameters) throws JAXBException {
        StringWriter sw = new StringWriter();
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            ObjectFactory objectFactory = new ObjectFactory();

            Instant now = new Instant();

            // DR report spec states the utc attribute is UTC seconds from 1970.
            DRReport drReport = objectFactory.createDRReport();
            drReport.setUtc(now.getMillis() / 1000);
            drReport.setUniqueID(Integer.parseInt(deviceParameters.getRfnIdentifier().getSensorSerialNumber()));

            HostDeviceID hostDeviceId = objectFactory.createDRReportHostDeviceID();
            hostDeviceId.setRevision(771);
            hostDeviceId.setValue(Integer.parseInt(deviceParameters.getRfnIdentifier().getSensorModel()));
            drReport.getHostDeviceID().add(hostDeviceId);

            ExtendedAddresssing extendedAddresssing = objectFactory.createDRReportExtendedAddresssing();
            extendedAddresssing.setSPID(1);
            extendedAddresssing.setGeo(0);
            extendedAddresssing.setFeeder(0);
            extendedAddresssing.setZip(0L);
            extendedAddresssing.setUDA(0);
            extendedAddresssing.setRequired(128);
            drReport.getExtendedAddresssing().add(extendedAddresssing);

            Info info = objectFactory.createDRReportInfo();
            info.setFlags(0);
            info.setReportingInterval(1440);
            info.setRecordingInterval(60);
            info.setTotalLUFEvents(0);
            info.setTotalLUVEvents(0);
            info.setBlinkCount(0);
            drReport.getInfo().add(info);

            Relays relays = objectFactory.createDRReportRelays();
            for (int i = 0; i < 3; i++) {
                Relay relay = createRelay(i, deviceParameters, objectFactory, now);
                relays.getRelay().add(relay);
            }
            drReport.getRelays().add(relays);

            ControlEvents controlEvents = objectFactory.createDRReportControlEvents();
            drReport.getControlEvents().add(controlEvents);

            LUVEvents luvEvents = objectFactory.createDRReportLUVEvents();
            drReport.getLUVEvents().add(luvEvents);

            // Create broadcast verification message ids
            BroadcastVerificationMessages verificationMessages =
                objectFactory.createDRReportBroadcastVerificationMessages();
            List<PerformanceVerificationEventMessage> eventMessages = loadPerformanceVerificationEventMessages();

            if (eventMessages != null && !eventMessages.isEmpty()) {
                for (PerformanceVerificationEventMessage eventMsg : eventMessages) {
                    Event event = objectFactory.createDRReportBroadcastVerificationMessagesEvent();
                    event.setUnused(0); // This element is not used & should be set to 0 to match firmware
                                        // message format.
                    event.setUniqueIdentifier(eventMsg.getMessageId());
                    // Sets received timestamp to the time the message was sent + up to 5 minutes depending on
                    // serial number
                    event.setReceivedTimestamp(eventMsg.getTimeMessageSent().getMillis() / 1000
                        + Integer.parseInt(deviceParameters.getRfnIdentifier().getSensorSerialNumber()) % 300);
                    verificationMessages.getEvent().add(event);
                }
            }

            drReport.getBroadcastVerificationMessages().add(verificationMessages);
            marshaller.marshal(drReport, sw);
        } catch (Exception e) {
            throw e;
        }
        return sw.toString();
    }

    /*
     * This method loads Performance Verification Event Messages for the simulator
     */
    private List<PerformanceVerificationEventMessage> loadPerformanceVerificationEventMessages() {
        Instant now = new Instant();
        Range<Instant> last24HourRange = Range.inclusive(now.minus(Duration.standardHours(24)), now);
        return performanceVerificationDao.getEventMessages(last24HourRange);
    }

    /**
     * This method combines the EXI encoded XML payload and the four-byte status header specified in the DR
     * report spec.
     * 
     * @param deviceParameters Specifies the device that is generating the request.
     * @param encodedXml The EXI encoded XML data.
     * @return The complete payload as a byte array.
     * @throws IOException
     */
    private byte[] createPayload(RfnLcrReadSimulatorDeviceParameters deviceParameters, byte[] encodedXml)
            throws IOException {
        int length = encodedXml.length + 4;
        ByteArrayOutputStream output = new ByteArrayOutputStream(length);
        writeRequestHeader(deviceParameters, length, output);
        output.write(encodedXml);
        return output.toByteArray();
    }

    /**
     * This method generates the request header which includes the following: <li>3 byte ExpressCom header.
     * The first byte is an identifier, 0xE2. The subsequent two bytes are a 16 bit integer value representing
     * the length of the remaining bytes of the message (DR Report status header & payload).</li> <li>4 byte
     * status header. This holds the RFN model number and schema version.</li>
     * 
     * @param deviceParameters Specifies the device generating the request.
     * @param length The length of the DR Report status header and payload (does not count 3 byte ExpressCom
     *        header).
     * @param output The output stream the header is written to.
     */
    private void writeRequestHeader(RfnLcrReadSimulatorDeviceParameters deviceParameters, int length,
            ByteArrayOutputStream output) {
        output.write((byte) 0xE2); // Identifying byte of the ExpressCom header for schema version 0.0.3.
        output.write((byte) (length & 0xFF00) >>> 8);
        output.write((byte) length & 0xFF);

        // RFN model number (SSPEC id) is written as a two-byte short value in the message header.
        short rfnModelNumber = Short.parseShort(deviceParameters.getRfnIdentifier().getSensorModel());
        // Can't pass the short directly to write() as it will be written incorrectly as a four-byte int.
        output.write((byte) ((rfnModelNumber & 0xFF00) >>> 8));
        output.write((byte) (rfnModelNumber & 0x00FF));

        short majorVersion = (short) deviceParameters.getMajorVersion();
        short minorVersion = (short) deviceParameters.getMinorVersion();
        short revision = (short) deviceParameters.getRevision();
        // Major and minor version are both written in the same byte of the header.
        // Major version is written as a four-bit int value in the four most-significant bits,
        // while minor version is written as a four-bit int in four the least-significant bits of the byte.
        byte majorAndMinorVersionByte = (byte) (((majorVersion & 0x0F) << 4) & (minorVersion & 0x0F));
        byte revisionByte = (byte) (revision & 0xFF);
        output.write(majorAndMinorVersionByte);
        output.write(revisionByte);
    }

    /**
     * This method creates the Java object representation of the <Relay/> element within the DR report.
     * It populates the attributes and elements of the Relay element and then generates a series of
     * <Interval/>
     * data elements that are nested within the Relay.
     * 
     * @param relayId The relay id.
     * @param serialNumber The serial number of the device (used to determine run time value).
     * @param deviceParameters Specifies the device that is generating the request.
     * @param objectFactory The factory used to generate Java elements that represent the DR report .xsd.
     * @param now Used to generate the first interval's timestamp.
     * @return
     */
    private Relay createRelay(int relayId, RfnLcrReadSimulatorDeviceParameters deviceParameters,
            ObjectFactory objectFactory, Instant now) {
        Interval interval = objectFactory.createDRReportRelaysRelayIntervalDataInterval();
        // Run time is bit shifted to the left by 8 bits per the DR report spec.
        // Using serial number mod 50 makes it easy to find devices with zero runtime by serial number.
        int serialNumber = Integer.parseInt(deviceParameters.getRfnIdentifier().getSensorSerialNumber());
        interval.setValue((serialNumber % 50) << 8);

        IntervalData intervalData = objectFactory.createDRReportRelaysRelayIntervalData();
        // DR report spec states the startTime attribute is UTC seconds from 1970.
        // This value holds the timestamp of the first VALID interval data reading.
        int numIntervals = 36;
        Instant firstIntervalTimestamp =
            now.minus(Duration.standardMinutes(numIntervals * deviceParameters.getRecordingInterval()));
        intervalData.setStartTime(firstIntervalTimestamp.getMillis() / 1000);
        for (int i = 0; i < 36; i++) {
            intervalData.getInterval().add(interval);
        }

        Relay relay = objectFactory.createDRReportRelaysRelay();
        relay.setId((short) relayId);
        relay.setFlags(0);
        relay.setProgram((short) 0);
        relay.setSplinter((short) 0);
        relay.setRemainingControlTime(0);
        relay.setKwRating(0);
        relay.setAmpType((short) 0);
        relay.getIntervalData().add(intervalData);
        return relay;
    }

    @Override
    public void startSimulatorWithCurrentSettings() {
        sendMessagesByRange(getCurrentSettings());
    }
}
