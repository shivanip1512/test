package com.cannontech.dr.rfn.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PreDestroy;
import javax.jms.ConnectionFactory;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.message.archive.RfnLcrReading;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingType;
import com.cannontech.dr.rfn.model.RfnLcrDataSimulatorStatus;
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
import com.google.common.collect.Lists;

public class RfnLcrDataSimulatorServiceImpl implements RfnLcrDataSimulatorService {
    private final Logger log = YukonLogManager.getLogger(RfnLcrDataSimulatorServiceImpl.class);

    private final static String lcrReadingArchiveRequestQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest";

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private ExiParsingService exiParsingService;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private PerformanceVerificationDao performanceVerificationDao;
    
    private RfnLcrDataSimulatorStatus rfnLcrDataSimulatorStatus = new RfnLcrDataSimulatorStatus();
    private RfnLcrDataSimulatorStatus rfnLcrExistingDataSimulatorStatus = new RfnLcrDataSimulatorStatus();
    private static List<Future<?>> futures;
    private final static Map<Integer,Long> perMinuteMsgCount = new ConcurrentHashMap<>();
    private List<PerformanceVerificationEventMessage> eventMessages = null;
    

    public long getPerMinuteMsgCount() {
        int minuteOfHour = new Instant().get(DateTimeFieldType.minuteOfHour());

        if (perMinuteMsgCount.isEmpty() || !perMinuteMsgCount.containsKey(minuteOfHour)) {
            return 0;
        } else {
            return perMinuteMsgCount.get(minuteOfHour);
        }
    }

    public RfnLcrDataSimulatorStatus getRfnLcrDataSimulatorStatus() {
        return rfnLcrDataSimulatorStatus;
    }

    public RfnLcrDataSimulatorStatus getRfnLcrExistingDataSimulatorStatus() {
        return rfnLcrExistingDataSimulatorStatus;
    }

    private Simulator simulator = new Simulator();
    private ScheduledFuture<?> simulatorFuture = null;
    private ScheduledFuture<?> msgSimulatorFt = null;
    private boolean msgSimulatorRunning = true;
    private final Integer devicePartitionCount = 1000;
    
    @Override
    public synchronized void sendLcrDeviceMessages(List<RfnDevice> rfnLcrDeviceList) {
        futures = new ArrayList<Future<?>>();
        final AtomicBoolean isRunning6200 = rfnLcrExistingDataSimulatorStatus.getIsRunning6200();
        isRunning6200.set(true);
        final AtomicBoolean isRunning6600 = rfnLcrExistingDataSimulatorStatus.getIsRunning6600();
        isRunning6600.set(true);

        Collections.shuffle(rfnLcrDeviceList);  //  make sure they aren't sent in DB order
        
        try {
            if (msgSimulatorRunning || msgSimulatorFt.isDone()) {
                msgSimulatorRunning = false;
                // increment counter (minute) for Threads created for X number devices per list
                int minsCounter = 0;
                loadPerformanceVerificationEventMessages();
                for (List<RfnDevice> partition : Lists.partition(rfnLcrDeviceList, devicePartitionCount)) {
                    msgSimulatorFt =
                        executor.scheduleAtFixedRate(new MessageSimulator(partition), minsCounter++, 24 * 60,
                            TimeUnit.MINUTES);
                    futures.add(msgSimulatorFt);
                }
            } else {
                log.debug("RF LCR Data Simulator on existing devices is already running.");
            }
        } catch (Exception e) {
            log.error("RF LCR Data Simulator on existing devices has encountered an unexpected error.", e);
            rfnLcrExistingDataSimulatorStatus.setErrorMessage(e.getMessage());
        }
    }

    @Override
    public synchronized void startSimulator(SimulatorSettings settings) {
        final AtomicBoolean isRunning6200 = rfnLcrDataSimulatorStatus.getIsRunning6200();
        isRunning6200.set(true);
        final AtomicBoolean isRunning6600 = rfnLcrDataSimulatorStatus.getIsRunning6600();
        isRunning6600.set(true);
        try {
            if (!simulator.isRunning) {
                simulator.initializeSimulator(settings);
                simulator.isRunning = true;
                simulatorFuture = executor.scheduleAtFixedRate(simulator, 0, 1, TimeUnit.MINUTES);
            } else {
                log.debug("RFN LCR data simulator is already running.");
            }
        } catch (Exception ex) {
            log.error("Data Simulator has encountered an unexpected error.", ex);
            rfnLcrDataSimulatorStatus.setErrorMessage(ex.getMessage());
            isRunning6200.set(false);
            isRunning6600.set(false);
        }
    }

    @Override
    @PreDestroy
    public synchronized void stopSimulator() {
        log.debug("RFN LCR data simulator shutting down...");
        simulator.isRunning = false;
        if (simulatorFuture != null) {
            simulatorFuture.cancel(true);
        }
        
        RfnLcrDataSimulatorStatus.reInitializeStatus(rfnLcrDataSimulatorStatus);
        if (!rfnLcrExistingDataSimulatorStatus.getIsRunning6200().get()) {
            // Flush the counter if both simulators are off
            perMinuteMsgCount.clear();
        }
    }

    @Override
    @PreDestroy
    public synchronized void stopMessageSimulator() {
        log.debug("RFN LCR message simulator shutting down...");
        Iterator<Future<?>> futureIter = futures.iterator();
        while (futureIter.hasNext()) {
            futureIter.next().cancel(true);
        }
        msgSimulatorRunning = true;
        RfnLcrDataSimulatorStatus.reInitializeStatus(rfnLcrExistingDataSimulatorStatus);
        if (!rfnLcrDataSimulatorStatus.getIsRunning6200().get()) {
            // Flush the counter if both simulators are off
            perMinuteMsgCount.clear();
        }
    }

    @Override
    public boolean isRunning() {
        return simulator.isRunning;
    }

    @Override
    public SimulatorSettings getCurrentSettings() {
        return simulator.simulatorSettings;
    }

    private class Simulator implements Runnable {
        private SimulatorSettings simulatorSettings = null;
        private volatile boolean isRunning = false;
        // # of message groups. Currently there are 1440, one for each minute of the day.
        private static final int messageGroupCount = 24 * 60;
        // The messaging group that will simulate RfnLcrReadArchiveRequest messages when the thread next
        // wakes.
        private int currentMessagingGroup ;

        @Override
        public void run() {
           
            log.debug("RFN LCR data simulator sending next message group...");
            if (simulatorSettings != null) {
                // Simulate the unsolicited RFN LCR read archive requests.
                if (new Instant().get(DateTimeFieldType.minuteOfDay()) == 0) {
                    // Reinitialize counter for the fresh day
                    rfnLcrDataSimulatorStatus.resetCompletionState();
                }
                simulateRfnLcrNetwork();
            } else {
                // The simulator settings haven't been set, so we may as well shut down.
                log.debug("RFN LCR data simulator settings have not been initialized. Messages will not be sent.");
            }
            log.info("Data Simulator  worker has finished.");
           
        }

        /**
         * Generate and send the RFN LCR archive request messages for the current messaging group.
         */
        private void simulateRfnLcrNetwork() {
            
            AtomicInteger id6200 = new AtomicInteger();
            AtomicInteger id6600 = new AtomicInteger();
            boolean useCurrentMsgGrp6200 = true;
            boolean useCurrentMsgGrp6600 = true;
            boolean hasFailed = false;
            int lcr6200serialTo = simulatorSettings.getLcr6200serialTo();
            int lcr6600serialTo = simulatorSettings.getLcr6600serialTo();
            AtomicLong numComplete6200 = rfnLcrDataSimulatorStatus.getNumComplete6200();
            AtomicLong numComplete6600 = rfnLcrDataSimulatorStatus.getNumComplete6600();
            RfnLcrReadSimulatorDeviceParameters deviceParameters = null;
            try {
                // Loop through LCR 6200 serial numbers, sending messages for those in the current messaging
                // group.
                if (simulatorSettings.getLcr6200serialFrom() < currentMessagingGroup) {
                    useCurrentMsgGrp6200 = false;
                    id6200.set(simulatorSettings.getLcr6200serialFrom());
                } else {
                    id6200.set(simulatorSettings.getLcr6200serialFrom() + currentMessagingGroup);
                }

                while (id6200.get() < lcr6200serialTo) {
                    deviceParameters =
                        new RfnLcrReadSimulatorDeviceParameters(
                            new RfnIdentifier(String.valueOf(id6200), "CPS", "1077"), 0, 0, 3, 60, 24 * 60);
                    
                    hasFailed = simulateLcrReadRequest(simulatorSettings, deviceParameters);
                    if (!hasFailed) {
                        numComplete6200.incrementAndGet();
                        rfnLcrDataSimulatorStatus.setLastFinishedInjection6200(Instant.now());
                        if (useCurrentMsgGrp6200) {
                            id6200.set(simulatorSettings.getLcr6200serialFrom() + currentMessagingGroup);
                        } else {
                            id6200.incrementAndGet();
                        }
                    }
                }
                // Loop through LCR 6600 serial numbers, sending messages for those in the current messaging
                // group.
                if (simulatorSettings.getLcr6600serialFrom() < currentMessagingGroup) {
                    useCurrentMsgGrp6600 = false;
                    id6600.set(simulatorSettings.getLcr6600serialFrom());
                } else {
                    id6600.set(simulatorSettings.getLcr6600serialFrom() + currentMessagingGroup);
                }
                while (id6600.get() < lcr6600serialTo) {
                    deviceParameters =
                        new RfnLcrReadSimulatorDeviceParameters(
                            new RfnIdentifier(String.valueOf(id6600), "CPS", "1082"), 0, 0, 3, 60, 24 * 60);
                    
                    hasFailed = simulateLcrReadRequest(simulatorSettings, deviceParameters);
                    if (!hasFailed) {
                        numComplete6600.incrementAndGet();
                        rfnLcrDataSimulatorStatus.setLastFinishedInjection6600(Instant.now());
                        if (useCurrentMsgGrp6600) {
                            id6600.set(simulatorSettings.getLcr6600serialFrom() + currentMessagingGroup);
                        } else {
                            id6600.incrementAndGet();
                        }
                    }
                }
                // Advance to the next messaging group for the next thread wake-up.
                currentMessagingGroup = (++currentMessagingGroup) % messageGroupCount;
            } catch (Exception e) {
                log.error("Data Simulator has encountered an unexpected error.", e);
                rfnLcrDataSimulatorStatus.setErrorMessage(e.getMessage());
            }
        }

        public void initializeSimulator(SimulatorSettings simulatorSettings) {
            // Determine current message group based on system time.
            this.simulatorSettings = simulatorSettings;
            int ticks = (int) (simulatorSettings.getDaysBehind() * messageGroupCount);

            currentMessagingGroup = (new Instant().get(DateTimeFieldType.minuteOfDay()) - ticks) % messageGroupCount;

            // kick off the backed-up readings
            for (int i = 0; i < ticks; ++i) {
                simulateRfnLcrNetwork();
            }
        }
    }

    /**
     * Loop through LCR meters and send the messages.
     */
    private class MessageSimulator implements Runnable {
        private static final int BROADCAST_EVENTS_LOOKUP_INTERVAL = 10;
        List<RfnDevice> rfnLcrDeviceList = null;
        
        MessageSimulator(List<RfnDevice> rfnLcrDeviceList) {
            this.rfnLcrDeviceList = rfnLcrDeviceList;
        }

        @Override
        public void run() {
            log.debug("RFN LCR message simulator sending message...");
            if (rfnLcrDeviceList != null) {
                if (new Instant().get(DateTimeFieldType.minuteOfDay()) == 0) {
                    // Reinitialize counter for the fresh day
                    rfnLcrExistingDataSimulatorStatus.resetCompletionState();
                }
                insertRfnLcrMessages();
            } else {
                log.debug("RFN LCR message simulator settings have not been initialized. Messages will not be sent.");
            }
            log.debug("RFN LCR message simulator sleeping...");
        }

        /**
         * Send the RFN LCR archive request messages for existing LCR meters.
         */
        private void insertRfnLcrMessages() {

            AtomicLong lcr6200NumComplete = rfnLcrExistingDataSimulatorStatus.getNumComplete6200();
            AtomicLong lcr6600NumComplete = rfnLcrExistingDataSimulatorStatus.getNumComplete6600();
            SimulatorSettings settings = new SimulatorSettings(0, 0, 0, 0, 0, 0, 0);
            RfnLcrReadSimulatorDeviceParameters deviceParameters = null;
            boolean hasFailed = false;

            int minuteOfHour = new Instant().get(DateTimeFieldType.minuteOfHour());
            if (minuteOfHour % BROADCAST_EVENTS_LOOKUP_INTERVAL == 0) {
             // Every 10th minute of an hour the events will be looked up, since broadcast can happen every 15 min
                loadPerformanceVerificationEventMessages();
            }

            for (RfnDevice device : rfnLcrDeviceList) {
                RfnIdentifier rfnIdentifier = device.getRfnIdentifier();
                deviceParameters = new RfnLcrReadSimulatorDeviceParameters(rfnIdentifier, 0, 0, 3, 60, 24 * 60);
                hasFailed = simulateLcrReadRequest(settings, deviceParameters);
                if (!hasFailed) {
                    if (device.getPaoIdentifier().getPaoType().equals(PaoType.LCR6200_RFN)) {
                        lcr6200NumComplete.incrementAndGet();
                        rfnLcrExistingDataSimulatorStatus.setLastFinishedInjection6200(Instant.now());
                    } else if (device.getPaoIdentifier().getPaoType().equals(PaoType.LCR6600_RFN)) {
                        lcr6600NumComplete.incrementAndGet();
                        rfnLcrExistingDataSimulatorStatus.setLastFinishedInjection6600(Instant.now());
                    }
                }
            }
        }
    }

    /**
     * This method loads Performance Verification Event Messages for the simulator
     */
    private void loadPerformanceVerificationEventMessages() {
        Instant now = new Instant();
        Range<Instant> last24HourRange = Range.inclusive(now.minus(Duration.standardHours(24)), now);
        eventMessages = performanceVerificationDao.getEventMessages(last24HourRange);
    }
    /**
     * This method creates an RFN LCR read archive request and places it into the appropriate messaging queue.
     * 
     * @param deviceParameters Specifies the parameters of the device that generated the request.
     */
    private boolean simulateLcrReadRequest(SimulatorSettings simulatorSettings,
            RfnLcrReadSimulatorDeviceParameters deviceParameters) {
        RfnLcrReadingArchiveRequest readArchiveRequest;
        long msgCount = 0;
        boolean hasFailed = false;
        try {
            readArchiveRequest = createReadArchiveRequest(simulatorSettings, deviceParameters);
            sendArchiveRequest(lcrReadingArchiveRequestQueueName, readArchiveRequest);
            
        } catch (RfnLcrSimulatorException | IOException e) {
            hasFailed = true ;
            log.warn("There was a problem creating an RFN LCR archive read request for device: "
                + deviceParameters.getRfnIdentifier().getSensorManufacturer() + "/"
                + deviceParameters.getRfnIdentifier().getSensorModel() + "/"
                + deviceParameters.getRfnIdentifier().getSensorSerialNumber(), e);
        } finally {
            if (!hasFailed) {
                int minuteOfHour = new Instant().get(DateTimeFieldType.minuteOfHour());
                if (perMinuteMsgCount.containsKey(minuteOfHour)) {
                    msgCount = perMinuteMsgCount.get(minuteOfHour);
                }
                msgCount++;
                perMinuteMsgCount.clear();
                // the record will have the number of messages sent for the current minute only
                perMinuteMsgCount.put(minuteOfHour, msgCount);
            }
        }
        return hasFailed;
        
    }

    /**
     * This method places an LCR Read Archive Request message onto the specified JMS queue.
     * 
     * @param queueName The queue to palce the message on.
     * @param archiveRequest The read archive request message being sent.
     */
    private <R extends RfnIdentifyingMessage> void sendArchiveRequest(String queueName, R archiveRequest) {
        JmsTemplate jmsTemplate;
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(false);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.convertAndSend(queueName, archiveRequest);
    }

    /**
     * Creates the archive request by generating a simulated XML payload, encoding it into the EXI format and
     * placing it into the RfnLcrReadingArchiveRequest object.
     * 
     * @param simulatorSettings The current simulation settings, including MessageId & timestamp.
     * @param deviceParameters Specifies the device that is generating the request.
     * @return The simulated request object.
     * @throws RfnLcrSimulatorException Thrown if there is a problem generating the XML or encoded EXI data.
     * @throws IOException
     */
    private RfnLcrReadingArchiveRequest createReadArchiveRequest(SimulatorSettings simulatorSettings,
            RfnLcrReadSimulatorDeviceParameters deviceParameters) throws RfnLcrSimulatorException, IOException {
        // Generate raw XML for archive reading.
        String xmlData = null;
        try {
            xmlData = getLcrReadXmlPayload(simulatorSettings, deviceParameters);
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
        byte[] payload = createPayload(deviceParameters, encodedXml);

        RfnLcrReadingArchiveRequest readArchiveRequest = new RfnLcrReadingArchiveRequest();
        RfnLcrReading reading = new RfnLcrReading();
        reading.setPayload(payload);
        reading.setTimeStamp(new Instant().getMillis());
        readArchiveRequest.setData(reading);
        readArchiveRequest.setRfnIdentifier(deviceParameters.getRfnIdentifier());
        readArchiveRequest.setType(RfnLcrReadingType.UNSOLICITED);
        return readArchiveRequest;
    }

    /**
     * Generates the raw XML report to be encoded.
     * 
     * @param simulatorSettings The current simulation settings, including MessageId & timestamp.
     * @param deviceParameters Specifies the device that is generating the request.
     * @return The DR report XML string.
     * @throws JAXBException Thrown if there is a problem creating JAXB objects or marshaling the XML.
     */
    private String getLcrReadXmlPayload(SimulatorSettings simulatorSettings,
            RfnLcrReadSimulatorDeviceParameters deviceParameters) throws JAXBException {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("com.cannontech.dr.rfn.model.jaxb");
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

            // Create broadcast verification message ids.
            BroadcastVerificationMessages verificationMessages =
                objectFactory.createDRReportBroadcastVerificationMessages();

            if (eventMessages != null && !eventMessages.isEmpty()) {
                for (PerformanceVerificationEventMessage eventMsg : eventMessages) {
                    Event event = objectFactory.createDRReportBroadcastVerificationMessagesEvent();
                    event.setUnused(0); //This element is not used & should be set to 0 to match firmware message format.
                    event.setUniqueIdentifier(eventMsg.getMessageId());
                    // Sets received timestamp to the time the message was sent + up to 5 minutes depending on serial number
                    event.setReceivedTimestamp(eventMsg.getTimeMessageSent().getMillis() / 1000
                        + Integer.parseInt(deviceParameters.getRfnIdentifier().getSensorSerialNumber()) % 300);
                    verificationMessages.getEvent().add(event);
                }
            } else {
                Event event = objectFactory.createDRReportBroadcastVerificationMessagesEvent();
                event.setUnused(0); //This element is not used & should be set to 0 to match firmware message format.
                event.setUniqueIdentifier(simulatorSettings.getMessageId());
                event.setReceivedTimestamp(simulatorSettings.getMessageIdTimestamp());
                verificationMessages.getEvent().add(event);
            }

            drReport.getBroadcastVerificationMessages().add(verificationMessages);
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
}
