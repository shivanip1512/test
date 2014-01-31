package com.cannontech.dr.rfn.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.Executor;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.openexi.proc.common.EXIOptionsException;
import org.openexi.sax.TransmogrifierException;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.dr.rfn.message.archive.RfnLcrReading;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingType;
import com.cannontech.dr.rfn.model.DRReport;
import com.cannontech.dr.rfn.model.DRReport.ControlEvents;
import com.cannontech.dr.rfn.model.DRReport.ExtendedAddresssing;
import com.cannontech.dr.rfn.model.DRReport.HostDeviceID;
import com.cannontech.dr.rfn.model.DRReport.Info;
import com.cannontech.dr.rfn.model.DRReport.LUFEvents;
import com.cannontech.dr.rfn.model.DRReport.LUVEvents;
import com.cannontech.dr.rfn.model.DRReport.Relays;
import com.cannontech.dr.rfn.model.DRReport.Relays.Relay;
import com.cannontech.dr.rfn.model.DRReport.Relays.Relay.IntervalData;
import com.cannontech.dr.rfn.model.DRReport.Relays.Relay.IntervalData.Interval;
import com.cannontech.dr.rfn.model.ObjectFactory;
import com.cannontech.dr.rfn.model.RfnLcrReadSimulatorDeviceParameters;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.service.ExiParsingService;
import com.cannontech.dr.rfn.service.RfnLcrDataSimulatorService;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class RfnLcrDataSimulatorServiceImpl implements RfnLcrDataSimulatorService {
    private final Logger log = YukonLogManager.getLogger(RfnLcrDataSimulatorServiceImpl.class);
    private static final String lcrReadingArchiveRequestQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest";  

    @Resource(name="longRunningExecutor") private Executor executor;
    @Autowired ExiParsingService exiParsingService;
    @Autowired RfnEventTestingService testingService;

    private Simulator simulator = new Simulator();

    @Override
    public void startSimulator(SimulatorSettings settings) {
        if (!simulator.isRunning()) {
            simulator.setSimulatorSettings(settings);
            executor.execute(simulator);
        } else {
            log.debug("RFN LCR data simulator is already running");
        }
    }

    @Override
    public void stopSimulator() {
        simulator.shutdown();
    }

    private class Simulator extends Thread {
        private SimulatorSettings simulatorSettings = null;
        private volatile boolean simulatorRunning = false;
        
        // # of message groups. Currently there are 1440, one for each minute of the day.
        private final int messageGroupCount = 24 * 60;
        // # of millis in a day / number of message groups. Gives milliseconds between thread wakes.
        private final int timeBetweenMessageGroups = 86400000 / messageGroupCount;  
        // The messaging group that will simulate RfnLcrReadArchiveRequest messages when the thread next wakes.
        private int currentMessagingGroup;
        
        Simulator() {
            this.simulatorSettings = new SimulatorSettings();
        }
        
        @Override
        public void run() {
            log.debug("RFN LCR data simulator starting up...");
            simulatorRunning = true;
            // Determine current message group based on system time.
            currentMessagingGroup = new Instant().get(DateTimeFieldType.minuteOfDay()) % messageGroupCount;
            while(simulatorRunning) {
                Instant executionStart = new Instant();
                if (log.isDebugEnabled()) {
                    log.debug("RFN LCR simulator waking...");
                }
                
                // Simulate the unsolicited RFN LCR read archive requests.  
                simulateRfnLcrNetwork();
                
                // Calculate how long to sleep.
                Instant executionStop = new Instant();
                long sleepDuration = timeBetweenMessageGroups - (executionStop.getMillis() - executionStart.getMillis());
                if (sleepDuration < 0) {
                    sleepDuration = 0;
                }
                if (log.isDebugEnabled()) {
                    log.debug("Simulator sleeping for " + sleepDuration + " ms.");
                }
                try {
                    sleep(sleepDuration);
                } catch (InterruptedException e) {
                    log.debug("RFN LCR data simulator thread fatally interrupted.", e);
                    simulatorRunning = false;
                    break;
                }
            }
            log.debug("Exiting RFN LCR data simulator.");
        }
        
        /**
         * Generate and send the RFN LCR archive request messages for the current messaging group.
         */
        private void simulateRfnLcrNetwork() {
            int lcr6200serialTo = simulatorSettings.getLcr6200serialTo();
            int lcr6600serialTo = simulatorSettings.getLcr6600serialTo();
            // Loop through LCR 6200 serial numbers, sending messages for those in the current messaging group.
            for (int id = simulatorSettings.getLcr6200serialFrom(); 
                    id < lcr6200serialTo;
                    id++) {
                if (id % messageGroupCount == currentMessagingGroup) {
                    RfnLcrReadSimulatorDeviceParameters deviceParameters = new RfnLcrReadSimulatorDeviceParameters(
                            new RfnIdentifier(String.valueOf(id), "CPS", "1077"),
                            0, 0, 2,
                            60, 1440);
                    simulateLcrReadRequest(deviceParameters);
                }
            }
            //Loop through LCR 6600 serial numbers, sending messages for those in the current messaging group.
            for (int id = simulatorSettings.getLcr6600serialFrom(); 
                    id < lcr6600serialTo;
                    id++) {
                if (id % messageGroupCount == currentMessagingGroup) {
                    RfnLcrReadSimulatorDeviceParameters deviceParameters = new RfnLcrReadSimulatorDeviceParameters(
                            new RfnIdentifier(String.valueOf(id), "CPS", "1082"),
                            0, 0, 2,
                            60, 1440);
                    simulateLcrReadRequest(deviceParameters);
                }
            }
            // Advance to the next messaging group for the next thread wake-up.
            currentMessagingGroup = (++currentMessagingGroup) % messageGroupCount;
        }
        
        public void setSimulatorSettings(SimulatorSettings settings) {
            this.simulatorSettings = settings;
        }
        
        public void shutdown() {
            log.debug("Initiating RFN LCR data simulator shutdown...");
            simulatorRunning = false;
        }
        
        public boolean isRunning() {
            return simulatorRunning;
        }
    }

    /** 
     * This method creates an RFN LCR read archive request and places it into the appropriate messaging queue.
     * 
     * @param deviceParameters Specifies the parameters of the device that generated the request.
     */
    private void simulateLcrReadRequest(RfnLcrReadSimulatorDeviceParameters parameters) {
        RfnLcrReadingArchiveRequest readArchiveRequest;
        try {
            readArchiveRequest = createReadArchiveRequest(parameters);
            testingService.sendArchiveRequest(lcrReadingArchiveRequestQueueName, readArchiveRequest);
        } catch (RfnLcrSimulatorException e) {
            log.debug("There was a problem creating an RFN LCR archive read request for device: "
                    + parameters.getRfnIdentifier().getSensorManufacturer()
                    + "/" + parameters.getRfnIdentifier().getSensorModel()
                    + "/" + parameters.getRfnIdentifier().getSensorSerialNumber(), e);
        }
    }

    /**
     * Creates the archive request by generating a simulated XML payload, encoding it into the EXI format and
     * placing it into the RfnLcrReadingArchiveRequest object.
     * 
     * @param parameters Specifies the device that is generating the request.
     * @return The simulated request object.
     * @throws RfnLcrSimulatorException Thrown if there is a problem generating the XML or encoded EXI data.
     */
    private RfnLcrReadingArchiveRequest createReadArchiveRequest(
            RfnLcrReadSimulatorDeviceParameters parameters) throws RfnLcrSimulatorException {
        // Generate raw XML for archive reading.
        String xmlData = null;
        try {
            xmlData = getLcrReadXmlPayload(parameters);
            if(log.isDebugEnabled()) {
                log.debug("RFN LCR simulator - device: " 
                        + parameters.getRfnIdentifier().getSensorManufacturer()
                        + "/" + parameters.getRfnIdentifier().getSensorModel()
                        + "/" + parameters.getRfnIdentifier().getSensorSerialNumber()
                        + " payload: " + xmlData);
            }
        } catch (JAXBException e) {
            throw new RfnLcrSimulatorException("There was an error generating the XML payload for serial number "
                    + parameters.getRfnIdentifier().getSensorSerialNumber());
        }
        // Create EXI encoded payload with status header.
        byte[] encodedXml = null;
        try {
            encodedXml = exiParsingService.encodePayload(xmlData);
        } catch (TransmogrifierException | EXIOptionsException | IOException e) {
            throw new RfnLcrSimulatorException("There was an error encoding XML payload to EXI for serial number "
                    + parameters.getRfnIdentifier().getSensorManufacturer()
                    + "/" + parameters.getRfnIdentifier().getSensorModel()
                    + "/" + parameters.getRfnIdentifier().getSensorSerialNumber() );
        }
        byte[] payload = createPayload(parameters, encodedXml);
        
        RfnLcrReadingArchiveRequest readArchiveRequest = new RfnLcrReadingArchiveRequest();
        RfnLcrReading reading = new RfnLcrReading();
        reading.setPayload(payload);
        reading.setTimeStamp(new Instant().getMillis());
        readArchiveRequest.setData(reading);
        readArchiveRequest.setRfnIdentifier(parameters.getRfnIdentifier());
        readArchiveRequest.setType(RfnLcrReadingType.UNSOLICITED);
        return readArchiveRequest;
    }

    /**  
     * Generates the raw XML report to be encoded.
     * 
     * @param parameters Specifies the device that is generating the request.
     * @return The DR report XML string. 
     * @throws JAXBException Thrown if there is a problem creating JAXB objects or marshaling the XML. 
     */
    private String getLcrReadXmlPayload(RfnLcrReadSimulatorDeviceParameters parameters) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("com.cannontech.dr.rfn.model");
        Marshaller marshaller = jaxbContext.createMarshaller();
        ObjectFactory objectFactory = new ObjectFactory();
        
        Instant now = new Instant();
        int deviceSerialNumber = Integer.parseInt(parameters.getRfnIdentifier().getSensorSerialNumber());
        
        // DR report spec states the utc attribute is UTC seconds from 1970 
        DRReport drReport = objectFactory.createDRReport();
        drReport.setUtc(now.getMillis() / 1000);
        drReport.setUniqueID(deviceSerialNumber);
        
        HostDeviceID hostDeviceId = objectFactory.createDRReportHostDeviceID();
        hostDeviceId.setRevision(771);
        hostDeviceId.setValue(Integer.parseInt(parameters.getRfnIdentifier().getSensorModel()));
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
            Relay relay = createRelay(i, deviceSerialNumber, parameters, objectFactory, now);
            relays.getRelay().add(relay);
        }
        drReport.getRelays().add(relays);
        
        ControlEvents controlEvents = objectFactory.createDRReportControlEvents();
        drReport.getControlEvents().add(controlEvents);
        
        LUFEvents lufEvents = objectFactory.createDRReportLUFEvents();
        drReport.getLUFEvents().add(lufEvents);
        
        LUVEvents luvEvents = objectFactory.createDRReportLUVEvents();
        drReport.getLUVEvents().add(luvEvents);
        
        StringWriter sw = new StringWriter();
        marshaller.marshal(drReport, sw);
        return sw.toString();
    }

    /**
     * This method combines the EXI encoded XML payload and the four-byte status header specified in the DR report spec.
     *   
     * @param parameters Specifies the device that is generating the request.
     * @param encodedXml The EXI encoded XML data.
     * @return The complete payload as a byte array.
     */
    private byte[] createPayload(RfnLcrReadSimulatorDeviceParameters parameters, byte[] encodedXml) {
        ByteOutputStream output = new ByteOutputStream(encodedXml.length + 4);
        writeStatusHeader(parameters, output);
        output.write(encodedXml);
        return output.getBytes();
    }

    /**
     * This method generates the payload status header, the first four bytes of the payload which holds
     * the RFN model number and schema version.
     * 
     * @param parameters Specifies the device that is generating the request.
     * @param output The output stream that the status header is written to.
     */
    private void writeStatusHeader(RfnLcrReadSimulatorDeviceParameters parameters, ByteOutputStream output) {
        // RFN model number (SSPEC id) is written as a two-byte short value in the message header.
        short rfnModelNumber = Short.parseShort(parameters.getRfnIdentifier().getSensorModel());
        // Can't pass the short directly to write() as it will be written incorrectly as a four-byte int.
        output.write((byte) ((rfnModelNumber & 0xFF00) >>> 8));
        output.write((byte) (rfnModelNumber & 0x00FF));
        
        short majorVersion = (short) parameters.getMajorVersion();
        short minorVersion = (short) parameters.getMinorVersion();
        short revision = (short) parameters.getRevision();
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
     * It populates the attributes and elements of the Relay element and then generates a series of <Interval/>
     * data elements that are nested within the Relay.
     * 
     * @param relayId The relay id.
     * @param serialNumber The serial number of the device (used to determine run time value).
     * @param parameters Specifies the device that is generating the request.
     * @param objectFactory The factory used to generate Java elements that represent the DR report .xsd.
     * @param now Used to generate the first interval's timestamp.
     * @return
     */
    private Relay createRelay(int relayId, int serialNumber, RfnLcrReadSimulatorDeviceParameters parameters,
            ObjectFactory objectFactory, Instant now) {
        Interval interval = objectFactory.createDRReportRelaysRelayIntervalDataInterval();
        // Run time is bit shifted to the left by 8 bits per the DR report spec.
        // Using serial number mod 50 makes it easy to find devices with zero runtime by serial number.
        interval.setValue((serialNumber % 50) << 8 );
        
        IntervalData intervalData = objectFactory.createDRReportRelaysRelayIntervalData();
        // DR report spec states the startTime attribute is UTC seconds from 1970.
        // This value holds the timestamp of the first VALID interval data reading. 
        int numIntervals = 36;
        Instant firstIntervalTimestamp = now.minus(Duration.standardMinutes(
                numIntervals * parameters.getRecordingInterval()));
        intervalData.setStartTime(firstIntervalTimestamp.getMillis() / 1000);
        for(int i = 0; i < 36; i++) {
            intervalData.getInterval().add(interval);
        }
        
        Relay relay = objectFactory.createDRReportRelaysRelay();
        relay.setId(relayId);
        relay.setFlags(0);
        relay.setProgram(0);
        relay.setSplinter(0);
        relay.setRemainingControlTime(0);
        relay.setKwRating(0);
        relay.setAmpType(0);
        relay.getIntervalData().add(intervalData);
        return relay;
    }

}
