package com.cannontech.dr.rfn.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
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
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.dr.rfn.message.archive.RfnLcrReading;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingType;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnLcrReadSimulatorDeviceParameters;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.model.jaxb.DRReport;
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
        RELAY_N_RUNTIME((byte)0x0e, 25, 3),
        RELAY_N_SHEDTIME((byte)0x0f, 25, 3),
        RELAY_N_PROGRAM_ADDRESS((byte)0x10, 2, 3),
        RELAY_N_SPLINTER_ADDRESS((byte)0x11, 2, 3),
        RELAY_N_REMAINING_CONTROLTIME((byte)0x12, 5, 3),
        PROTECTION_TIME_RELAY_N((byte)0x13, 3, 3),
        CLP_TIME_FOR_RELAY_N((byte)0x14, 3, 3),
        BROADCAST_VERIFICATION_MESSAGES((byte)0x16, 8, 1);

        private final byte type;
        private final int length;
        private final int frequency;

        private TLVField(byte type, int length, int frequency) {
            this.type = type;
            this.length = length;
            this.frequency = frequency;
        }
    }
    private final Logger log = YukonLogManager.getLogger(RfnLcrDataSimulatorServiceImpl.class);

    private final static String lcrReadingArchiveRequestQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest";

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private ExiParsingService<SimpleXPathTemplate> exiParsingService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    
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
    private void addDevice(RfnIdentifier rfnIdentifier, SetMultimap<Integer, RfnLcrReadSimulatorDeviceParameters> devices, PaoType paoType){
        int minuteOfTheDay = getMinuteOfTheDay(rfnIdentifier.getSensorSerialNumber());
                
        RfnLcrReadSimulatorDeviceParameters parameters =
                new RfnLcrReadSimulatorDeviceParameters(rfnIdentifier, 0, 0, 3, 60, 24 * 60, paoType);
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
                addDevice(device.getRfnIdentifier(), allDevices, device.getPaoIdentifier().getPaoType());
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
                RfnManufacturerModel.RFN_LCR_6200);
            createDevicesByRange(settings.getLcr6600serialFrom(), settings.getLcr6600serialTo(),
                RfnManufacturerModel.RFN_LCR_6600);
            createDevicesByRange(settings.getLcr6700serialFrom(), settings.getLcr6700serialTo(),
                RfnManufacturerModel.RFN_LCR_6700);
            //check if execution was canceled
            if (!rangeDevices.isEmpty() && rangeDevicesStatus.isRunning().get()) {
                logDebugInjectionTime(rangeDevices);
                schedule();
            } else {
                rangeDevices.clear();
            }
        }
    }

    private void createDevicesByRange(int from, int to, RfnManufacturerModel model) {
        while (from <= to) {
            RfnIdentifier rfnIdentifier =
                new RfnIdentifier(String.valueOf(from), model.getManufacturer(), model.getModel());
            log.debug("createDevicesByRange="+rfnIdentifier);
            addDevice(rfnIdentifier, rangeDevices, model.getType());
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
        if (deviceParameters.getPaoType() == PaoType.LCR6700_RFN) {
            try {
                //Header with expresscom Message response 0xE2 and schema version 0.0.4
                byte[] header = Hex.decode("E20170043e00040000020044");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(header);
                // Add UTC 
                writeUTC(outputStream);
                // Generate TLV message
                byte[] message = getTLVMessage();
                outputStream.write(message);
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
        }
        else {
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

    private void writeUTC(ByteArrayOutputStream outputStream) {
        // UTC type
        outputStream.write((0x01 >> 4) & 0x0f);
        outputStream.write((0x01 & 0x0f) << 4);
        // UTC length as 4-byte
        outputStream.write(4);
        // Generate UTC field values (4-byte)
        long timeInSec = new DateTime(DateTimeZone.UTC).withTimeAtStartOfDay().getMillis()/1000;
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        byte[] utc = buffer.putLong(timeInSec).array();
        utc = Arrays.copyOfRange(utc, 4, utc.length);

        for (int i = 0; i < utc.length; i++) {
            outputStream.write(utc[i]);
        }
    }
    /**
     * Generate byte Array (part of TLV report) from TLVField enum and generate random values for each field.
     */
    private byte[] getTLVMessage() {
        // Fixed length message (assuming 3-relay are present)
        byte[] data = new byte[333];
        int index = 0;
        for (TLVField field : TLVField.values()) {
            int fieldCount = 0;
             while (fieldCount < field.frequency) {
                    int valueIndex = 0;
                    // Upper Nibble of first bye and Lower nibble of second byte makes FieldType in TLV format
                    data[index++] = (byte) ((field.type >> 4) & 0x0f);
                    data[index++] = (byte) ((field.type & 0x0f) << 4);
                    data[index++] = (byte) (field.length);
                    if (field.equals(TLVField.RELAY_N_RUNTIME) || field.equals(TLVField.RELAY_N_SHEDTIME)
                        || field.equals(TLVField.RELAY_N_PROGRAM_ADDRESS)
                        || field.equals(TLVField.RELAY_N_SPLINTER_ADDRESS)) {
                        data[index++] = (byte) fieldCount;
                        valueIndex++;
                    }

                    if(field.equals(TLVField.RELAY_INTERVAL_START_TIME)) {
                        // Add relay interval start time in sec(24 hour before UTC field value).Hourly data will be added after this till UTC (field value).
                        long intervalStartTime = new DateTime(DateTimeZone.UTC).minusHours(24).withTimeAtStartOfDay().getMillis()/1000;
                        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                        byte[] relayStartTime = buffer.putLong(intervalStartTime).array();
                        // Add relay interval start time to byte array.
                        for (int i = 4; i < relayStartTime.length; i++) {
                            data[index] = relayStartTime[i];
                            index++;
                        }
                        valueIndex = field.length;
                    }
                    // Generate random byte data for specified field type. 
                    while (valueIndex < field.length) {
                        data[index] = (byte) (Math.random() * (15 - 0));
                        index++;
                        valueIndex++;
                    }
                    fieldCount++;
                }
        }
        return data;
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

            marshaller.marshal(drReport, sw);
        } catch (Exception e) {
            throw e;
        }
        return sw.toString();
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
