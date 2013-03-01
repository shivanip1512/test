package com.cannontech.openadr.service;

import ietf.params.xml.ns.icalendar_2.Properties;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.oasis_open.docs.ns.energyinterop._201110.EiResponse;
import org.oasis_open.docs.ns.energyinterop._201110.EventResponses;
import org.oasis_open.docs.ns.energyinterop._201110.EventResponses.EventResponse;
import org.oasis_open.docs.ns.energyinterop._201110.EventStatusEnumeratedType;
import org.oasis_open.docs.ns.energyinterop._201110.payloads.EiRequestEvent;
import org.openadr.oadr_2_0a._2012._07.OadrCreatedEvent;
import org.openadr.oadr_2_0a._2012._07.OadrDistributeEvent;
import org.openadr.oadr_2_0a._2012._07.OadrDistributeEvent.OadrEvent;
import org.openadr.oadr_2_0a._2012._07.OadrRequestEvent;
import org.openadr.oadr_2_0a._2012._07.OadrResponse;
import org.openadr.oadr_2_0a._2012._07.ResponseRequiredType;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.openadr.OadrResponseCode;
import com.cannontech.openadr.dao.OpenAdrEventDao;
import com.cannontech.openadr.exception.OpenAdrConfigurationException;
import com.cannontech.openadr.exception.OpenAdrEventException;
import com.cannontech.openadr.util.CertificateUtil;
import com.cannontech.openadr.util.OadrUtil;
import com.cannontech.system.dao.GlobalSettingDao;


/**
 * Service to make requests to an OpenADR VTN on an interval. Requests to see
 * if the VTN has any control events for LoadManagement.
 */
public class OpenAdrService {
    private static final Logger log = YukonLogManager.getLogger(OpenAdrService.class);
    
    private final int DEFAULT_GEAR_INDEX = 1;
    private final boolean OVERRIDE_CONSTRAINTS_NO = false;
    private final boolean OBSERVE_CONSTRAINTS_YES = true;
    private final String DEFAULT_GEAR_NAME = null; // Program service will use default if this is null.
    
    // Used to format duration strings from the VTN, i.e. P0Y0M3DT0H0M0S
    private final PeriodFormatter formatter = 
            new PeriodFormatterBuilder()
                .appendLiteral("P")
                .appendYears().appendLiteral("Y")
                .appendMonths().appendLiteral("M")
                .appendDays().appendLiteral("D")
                .appendLiteral("T")
                .appendHours().appendLiteral("H")
                .appendMinutes().appendLiteral("M")
                .appendSeconds().appendLiteral("S")
                .toFormatter();
    
    private static final String[] JAXB_OADR_NAMESPACES = {
        "ietf.params.xml.ns.icalendar_2",
        "ietf.params.xml.ns.icalendar_2_0.stream",
        "org.oasis_open.docs.ns.emix._2011._06",
        "org.oasis_open.docs.ns.energyinterop._201110",
        "org.oasis_open.docs.ns.energyinterop._201110.payloads",
        "org.openadr.oadr_2_0a._2012._07"
    };
    
    private static final String OADR_NAMESPACE_STRING = StringUtils.join(JAXB_OADR_NAMESPACES, ":");
    
    private final JAXBContext jaxbContext;
    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;
    
    @Autowired private ProgramService programService;
    @Autowired private LoadControlProgramDao loadControlProgramDao;
    @Autowired private OpenAdrEventDao openAdrEventDao;
    @Autowired private OpenAdrConfigurationService configService;
    
    @Autowired
    public OpenAdrService(GlobalSettingDao globalSettingDao) throws JAXBException {
        // Setup the JAXB things.
        jaxbContext = JAXBContext.newInstance(OADR_NAMESPACE_STRING);
        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        unmarshaller = jaxbContext.createUnmarshaller();
    }
    
    @PostConstruct
    public void startPullService() {
        System.setProperty("javax.net.ssl.keyStore", configService.getKeystorePath());
        System.setProperty("javax.net.ssl.keyStorePassword", configService.getKeystorePassword());
        System.setProperty("javax.net.ssl.trustStore", configService.getTruststorePath());
        System.setProperty("javax.net.ssl.trustStorePassword", configService.getTruststorePassword());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    final long startMillis = System.currentTimeMillis();
                    try {
                        // Send a request for any control events the VTN has.
                        final OadrDistributeEvent distributeEvent = requestControlEvents();
                        
                        OadrCreatedEvent oadrCreatedEvent = null;
                        final EventResponses eventResponses = new EventResponses();
                        final String venId = configService.getVenId();
                        
                        if (distributeEvent != null) {
                            final String requestId = distributeEvent.getRequestID();
                            final String vtnID = distributeEvent.getVtnID();
                            
                            if (StringUtils.isBlank(vtnID) || !configService.getVtnId().equals(vtnID)) {
                                // Conformance rule 21.
                                log.error("Received unexpected VTN ID.");
                                oadrCreatedEvent = OadrUtil.createOadrCreatedEvent(requestId, eventResponses, venId);
                                
                                // Change the responseCode from Success.
                                oadrCreatedEvent.getEiCreatedEvent().getEiResponse().setResponseCode(OadrResponseCode.BAD_REQUEST.codeString());
                            } else if (!distributeEvent.getOadrEvent().isEmpty()) {
                                // There are events for us to handle.
                                for (OadrEvent event : distributeEvent.getOadrEvent()) {
                                    EventResponse response = processEvent(event, requestId);
                                    if (response != null &&
                                        event.getOadrResponseRequired() != ResponseRequiredType.NEVER) {
                                        eventResponses.getEventResponse().add(response);
                                    }
                                }
                                
                                oadrCreatedEvent = 
                                    OadrUtil.createOadrCreatedEvent(
                                        requestId, 
                                        eventResponses, 
                                        venId);
                            }
                        }
                        
                        // Send responses if we had control events to do.
                        if (oadrCreatedEvent != null) {
                            sendCreatedEventMessage(oadrCreatedEvent);
                        }
                    } catch (OpenAdrConfigurationException e) {
                        // Don't crash the thread, just alert the user we're misconfigured and let them correct it.
                        log.error("Configuration error caught while attempting to process request", e);
                    }
                        
                    long requestInterval = configService.getRequestInterval();
                    long sleepMillis = requestInterval - (System.currentTimeMillis() - startMillis);
                    if (sleepMillis > 0) {
                        /* 
                         * We want to send a request about once every requestInterval millis.
                         * Sleep for the difference between that and the length it took to 
                         * perform the request.
                         */
                        try {
                            log.debug("Request thread sleeping " + sleepMillis + " millis.");
                            Thread.sleep(sleepMillis);
                        } catch (InterruptedException e) {
                            log.error("caught InterruptedException in run", e);
                        }
                    }
                }
            }
        }).start();
    }
    
    /**
     * Check to see if the modification number of a request matches what we expect.<p>
     * 
     * <b>Precondition: eventXML must NOT be blank.</b>
     * 
     * @param modNumber the modification number of the VTN request
     * @param eventXml the xml of the event from the database
     * @param eventId the event id 
     * @param requestId the VTN request id
     * @throws OpenAdrEventException if the modification number doesn't match what we expect, 
     *    or if we're unable to unmarshal the xml.
     */
    private void validateModificationNumber(long modNumber, String eventXml, String eventId, String requestId) 
        throws OpenAdrEventException {
        final StringReader xmlReader = new StringReader(eventXml);
        final StreamSource xmlSource = new StreamSource(xmlReader);
        try {
            final OadrEvent dbEvent = (OadrEvent)unmarshaller.unmarshal(xmlSource);
            final long expectedModifNum = 1 + dbEvent.getEiEvent().getEventDescriptor().getModificationNumber();
            if (modNumber != expectedModifNum) {
                // Doesn't match what we expect, sorry!
                throw new OpenAdrEventException(eventId, OadrResponseCode.BAD_REQUEST, modNumber, requestId);
            }
        } catch (JAXBException e) {
            // XML was likely bad.
            log.error("caught exception in processEvent", e);
            throw new OpenAdrEventException(eventId, OadrResponseCode.BAD_REQUEST, modNumber, requestId);
        }
    }
    
    /**
     * Attempt the proper control for a single event in a VTN DistributeEvent request.
     * @param event the event being executed
     * @param requestId the VTN request Id.
     * @return an EventResponse object populated with the outcome of the control attempt.
     */
    private EventResponse processEvent(OadrEvent event, String requestId) {
        EventResponse response = null;
        
        try {
            final String eventId = event.getEiEvent().getEventDescriptor().getEventID();
            final EventStatusEnumeratedType eventStatus = event.getEiEvent().getEventDescriptor().getEventStatus();
            final boolean isCancelRequest = eventStatus == EventStatusEnumeratedType.CANCELLED;
            final long modNumber = event.getEiEvent().getEventDescriptor().getModificationNumber();
            final String eventXml = openAdrEventDao.retrieveEventXml(eventId);
            
            // Check for errors
            if (StringUtils.isBlank(eventId)) {
                if (isCancelRequest) {
                    // No error as per conformance number 21. No response necessary, we didn't do anything.
                    return null;
                }
                
                // Error, eventId can't be blank.
                throw new OpenAdrEventException(eventId, OadrResponseCode.BAD_REQUEST, modNumber, requestId);
            } else {
                if (isCancelRequest) {
                    if (modNumber > 0) {
                        if (StringUtils.isBlank(eventXml)) {
                            // Trying to cancel a non-existent event. Conformance number 21 says no error.
                            return null;
                        }
                    } else {
                        // Cancel request with a modification number of 0? No.
                        throw new OpenAdrEventException(eventId, OadrResponseCode.NOT_ALLOWED, modNumber, requestId);
                    }
                } else {
                    if (modNumber > 0) {
                        if (StringUtils.isBlank(eventXml)) {
                            // Trying to modify a non-existent event. This is an error.
                            throw new OpenAdrEventException(eventId, OadrResponseCode.NOT_FOUND, modNumber, requestId);
                        }
                    } else if (StringUtils.isBlank(eventXml)) {
                        // Trying to create an event that already exists? No.
                        throw new OpenAdrEventException(eventId, OadrResponseCode.CONFLICT, modNumber, requestId);
                    }
                }
            }
    
            /**
             * The weird cases were handled. At this point we're in one of three situations, and in
             * all of them, we know the eventId and eventXml are both NOT blank:
             *    1. Cancel request for a modification number greater than zero - mod number check required.
             *    2. Update request (modification number greater than zero.) - mod number check required.
             *    3. Add request (modification number is zero.) - no error check required.
             *    
             * Let's try to execute the event if possible. One last bit of error checking to go.
             */
            if (modNumber > 0) {
                // This will throw if the mod number isn't what we expect.
                validateModificationNumber(modNumber, eventXml, eventId, requestId);
            }
    
            final List<String> groupIdList = event.getEiEvent().getEiTarget().getGroupID();
            for (final String groupName : groupIdList) {
                final Properties properties = event.getEiEvent().getEiActivePeriod().getProperties();
                final String duration = properties.getDuration().getDuration();
                
                if (isDurationValid(duration)) {
                    final Period period = formatter.parsePeriod(duration);
    
                    final Date now = new Date();
                    final Date start = properties.getDtstart().getDateTime().toGregorianCalendar().getTime();
                    final Date stop = new DateTime(start).plus(period).toDate();
                    
                    if (start.after(now) && stop.after(now) && stop.after(start)) {
                        final boolean isTestEvent = ! "false".equals(event.getEiEvent().getEventDescriptor().getTestEvent());
                        
                        try {
                            // See if it's a program, try control if it is.
                            final int programId = loadControlProgramDao.getProgramIdByProgramName(groupName);
                            
                            response = 
                                requestProgramControl(
                                    programId, 
                                    start, 
                                    stop, 
                                    eventId, 
                                    modNumber, 
                                    requestId,
                                    isTestEvent,
                                    eventStatus);
                        } catch (NotFoundException e) {
                            try {
                                // Wasn't a program, it better be a scenario.
                                final int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(groupName);
                                
                                response = 
                                    requestScenarioControl(
                                        scenarioId, 
                                        start, 
                                        stop, 
                                        eventId, 
                                        modNumber, 
                                        requestId,
                                        isTestEvent, 
                                        eventStatus);
                            } catch (NotFoundException f) {
                                log.error("Received control request for " + groupName + ", which is neither a program nor a scenario.");
            
                                response = 
                                    OadrUtil.createEventResponse(
                                        eventId, 
                                        OadrResponseCode.NOT_FOUND.codeString(), 
                                        modNumber, 
                                        requestId);
                            }
                        }
                        
                        if (OadrResponseCode.getForErrorCode(response.getResponseCode()) == OadrResponseCode.SUCCESS) {
                            log.debug("Event: " + event.getEiEvent().getEventDescriptor().getEventID() + 
                                      " controlling " + groupName + " from " + start + " to " + stop);
                            
                            // Things went okay, do the appropriate DB action.
                            if (isCancelRequest) {
                                openAdrEventDao.deleteEvent(eventId);
                            } else {
                                writeEvent(event, stop);
                            }
                        }
                    } else {
                        log.warn("Attempt to control failed because of invalid time elements. " +
                                 "Now: " + now + " Start: " + start + " Stop: " + stop);
                        
                        response = OadrUtil.createEventResponse(
                            eventId, 
                            OadrResponseCode.NOT_ALLOWED.codeString(), 
                            modNumber, 
                            requestId);
                    }
                } else {
                    log.error("Invalid duration received: " + duration);
                    
                    response = 
                        OadrUtil.createEventResponse(
                            eventId, 
                            OadrResponseCode.BAD_REQUEST.codeString(), 
                            modNumber, 
                            requestId);
                }
            }
        } catch (OpenAdrEventException e) {
            // Event request had a problem, create the event response for it.
            response = OadrUtil.createEventResponse(
                e.getEventId(), 
                e.getCode().codeString(), 
                e.getModNumber(), 
                e.getRequestId());
        }
        
        return response;
    }
    
    /**
     * Attempt to execute program control with the provided parameters
     * @param programId the id of the program being controlled
     * @param start the start time for the control
     * @param stop the stop time for the control
     * @param eventId the eventId the control is being attempted for
     * @param modifNum the modification number of the VTN event request
     * @param requestId the VTN request id
     * @param isTestEvent whether or not the event is a test event
     * @param eventStatus the status of the event (for cancellation checks)
     * @return an event response object populated based on the outcome of the control attempt.
     */
    private EventResponse requestProgramControl(int programId, Date start, Date stop, 
                                                String eventId, long modifNum, String requestId,
                                                boolean isTestEvent, EventStatusEnumeratedType eventStatus) {
        OadrResponseCode responseCode = OadrResponseCode.SUCCESS;
        
        ConstraintViolations violations = null;
        try {
            if (isTestEvent) {
                if (eventStatus == EventStatusEnumeratedType.CANCELLED) {
                    violations = programService.getConstraintViolationsForStopProgram(programId, DEFAULT_GEAR_INDEX, stop);
                } else {
                    violations = programService.getConstraintViolationForStartProgram(programId, DEFAULT_GEAR_INDEX, start, null, stop, null, null);
                }
            } else {
                ProgramStatus programStatus = null;
                
                if (eventStatus == EventStatusEnumeratedType.CANCELLED) {
                    programStatus = programService.stopProgram(programId, stop, OVERRIDE_CONSTRAINTS_NO, OBSERVE_CONSTRAINTS_YES);
                } else {
                    programStatus = programService.startProgram(
                        programId, 
                        start, 
                        stop, 
                        DEFAULT_GEAR_NAME, 
                        OVERRIDE_CONSTRAINTS_NO, 
                        OBSERVE_CONSTRAINTS_YES, 
                        configService.getOadrUser());
                }
                
                violations = new ConstraintViolations(programStatus.getConstraintViolations());
            }
        } catch (UserNotInRoleException | NotFoundException e) {
            log.error("caught UserNotInRoleException in requestProgramControl", e);
            responseCode = OadrResponseCode.NOT_ALLOWED;
        } catch (ConnectionException | TimeoutException e) {
            log.error("caught exception in requestProgramControl", e);
            responseCode = OadrResponseCode.RESPONDER_TIMEOUT;
        }
        
        if (violations != null && violations.isViolated()) {
            responseCode = OadrResponseCode.NOT_ALLOWED;
        }
        
        return OadrUtil.createEventResponse(eventId, responseCode.codeString(), modifNum, requestId);
    }
    
    /**
     * Attempt to execute scenario control with the provided parameters.
     * @param scenarioId the id of the scenario being controlled
     * @param start the start time for the control
     * @param stop the stop time for the control
     * @param eventId the eventId the control is being attempted for
     * @param modifNum the modification number of the VTN event request
     * @param requestId the VTN request id
     * @param isTestEvent whether or not the event is a test event
     * @param eventStatus the status of the event (for cancellation checks)
     * @return an event response object populated based on the outcome of the control attempt.
     */
    private EventResponse requestScenarioControl(int scenarioId, Date start, Date stop,
                                                 String eventId, long modifNum, String requestId,
                                                 boolean isTestEvent, EventStatusEnumeratedType eventStatus) {
        List<ConstraintViolations> programViolations = null;
        OadrResponseCode responseCode = OadrResponseCode.SUCCESS;
        
        try {
            if (isTestEvent) {
                if (eventStatus == EventStatusEnumeratedType.CANCELLED) {
                    programViolations = programService.getConstraintViolationForStopScenario(scenarioId, stop);
                } else {
                    programViolations = programService.getConstraintViolationForStartScenario(scenarioId, start, stop);
                }
            } else {
                List<ProgramStatus> statuses = new ArrayList<>();
                if (eventStatus == EventStatusEnumeratedType.CANCELLED) {
                    boolean controlAllowed = programService.getConstraintViolationForStopScenario(scenarioId, stop).isEmpty();
                    
                    if (controlAllowed) {
                        statuses = programService.stopScenarioBlocking(
                             scenarioId, 
                             stop, 
                             OVERRIDE_CONSTRAINTS_NO, 
                             OBSERVE_CONSTRAINTS_YES, 
                             configService.getOadrUser());
                    } else {
                        responseCode = OadrResponseCode.NOT_ALLOWED;
                    }
                } else {
                    boolean controlAllowed = programService.getConstraintViolationForStartScenario(scenarioId, start, stop).isEmpty();
                    
                    if (controlAllowed) {
                        statuses = programService.startScenarioBlocking(
                             scenarioId, 
                             start, 
                             stop, 
                             OVERRIDE_CONSTRAINTS_NO, 
                             OBSERVE_CONSTRAINTS_YES, 
                             configService.getOadrUser());
                    } else {
                        responseCode = OadrResponseCode.NOT_ALLOWED;
                    }
                }

                programViolations = new ArrayList<>();
                
                // Get the actual outcome of the control events.
                for (ProgramStatus programStatus : statuses) {
                    programViolations.add(new ConstraintViolations(programStatus.getConstraintViolations()));
                }
            }
        } catch (NotAuthorizedException | NotFoundException e) {
            log.error("caught exception in requestScenarioControl", e);
            responseCode = OadrResponseCode.NOT_ALLOWED;
        } catch (ConnectionException | TimeoutException e) {
            log.error("caught exception in requestScenarioControl", e);
            responseCode = OadrResponseCode.RESPONDER_TIMEOUT;
        }
        
        /**
         * This is an extreme fringe case. Our check for control said we were okay, but the actual
         * control responds with its own set of outcomes. While we expect these outcomes to be 
         * the same as the original control check, it's possible they aren't. Check each of the
         * responses from the control, and respond with a NOT_ALLOWED error if for some reason
         * control failed for EVERYTHING, otherwise leave the response code alone.
         */
        if (programViolations != null) {
            // null check to not override an exception error code.
            boolean successExists = false;
            for (ConstraintViolations programViolation : programViolations) {
                if (!programViolation.isViolated()) {
                    successExists = true;
                    break;
                }
            }
            
            responseCode = successExists ? responseCode : OadrResponseCode.NOT_ALLOWED;
        }
        
        return OadrUtil.createEventResponse(eventId, responseCode.codeString(), modifNum, requestId);
    }
    
    /**
     * Write a successful event request to the database.
     * @param event the event being written.
     * @param endDate the time the event ends.
     */
    private void writeEvent(OadrEvent event, Date endDate) {
        StringWriter stringWriter = new StringWriter();
        
        try {
            marshaller.marshal(event, stringWriter);
        } catch (JAXBException e) {
            log.error("caught exception in writeEvent", e);
        }
        
        String eventId = event.getEiEvent().getEventDescriptor().getEventID();
        
        if (event.getEiEvent().getEventDescriptor().getModificationNumber() > 0) {
            // This was a previous event. Update instead of insert.
            openAdrEventDao.updateEventAndPurgeExpired(eventId, stringWriter.toString(), endDate);
        } else {
            openAdrEventDao.insertEventAndPurgeExpired(eventId, stringWriter.toString(), endDate);
        }
    }
    
    /**
     * Determines whether the HttpsURLConnection is valid given the certificate
     * thumbprint provided by the VTN.
     * @param con
     * @return true if the connection contains a certificate whose thumbprint matches the VTN's,
     *      false otherwise.
     */
    private boolean isValidConnection(HttpsURLConnection con) throws ConnectException {
        if (con != null) {
            try {
                log.trace("Response Code : " + con.getResponseCode());
                log.trace("Cipher Suite : " + con.getCipherSuite());

                Certificate[] certs = con.getServerCertificates();
                for (Certificate cert : certs) {
                    String vtnThumbprint = configService.getVtnThumbprint();
                    String thumbprint = CertificateUtil.getThumbprint(cert, vtnThumbprint.length());
                    
                    if (thumbprint.length() < vtnThumbprint.length()) {
                        continue;
                    }
                    
                    if (vtnThumbprint.equals(thumbprint)) {
                        log.trace("Cert Type : " + cert.getType());
                        log.trace("Cert Hash Code : " + cert.hashCode());
                        log.trace("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
                        log.trace("Cert Public Key Format : " + cert.getPublicKey().getFormat());
                        
                        return true;
                    }
                }
            } catch (SSLPeerUnverifiedException e) {
                log.error("caught exception in isValidConnection", e);
            } catch (NoSuchAlgorithmException | CertificateEncodingException e) {
                log.error("caught exception in isValidConnection", e);
            } catch (IOException e) {
                log.error("caught exception in isValidConnection", e);
            }
        }
        
        return false;
    }
    
    /**
     * Determines whether or not the duration string provided matches the expected format.
     * @param duration
     * @return true if the string matches the format expected, false otherwise.
     */
    private boolean isDurationValid(String duration) {
        // Example: P0Y0M3DT0H0M0S
        String pattern = "^P[0-9]{1,2}+Y[0-9]+M[0-9]+DT[0-9]+H[0-9]+M[0-9]+S$";
        return duration.matches(pattern);
    }
    
    /**
     * Forges a connection to the VTN, requests any control events the VTN has for LoadManagement,
     * and returns the VTN's response.
     * @return An OadrDistributeEvent object containing the control events to be executed if we
     *      have a valid connection to the VTN, received a response for our request, and were able 
     *      to unmarshal the response, null otherwise.
     */
    private OadrDistributeEvent requestControlEvents() {
        OadrRequestEvent event = new OadrRequestEvent();
        EiRequestEvent eiRequestEvent = new EiRequestEvent();
        eiRequestEvent.setRequestID("BobSaget");
        eiRequestEvent.setVenID(configService.getVenId());
        long replyLimit = configService.getReplyLimit();
        if (replyLimit > 0) {
            eiRequestEvent.setReplyLimit(replyLimit);
        }
        event.setEiRequestEvent(eiRequestEvent);
        
        StringWriter stringWriter = new StringWriter();
        try {
            marshaller.marshal(event, stringWriter);
        } catch (JAXBException e) {
            log.error("Unable to marshal OadrRequestEvent", e);
            return null;
        }
        
        return submitRequestToVTN(stringWriter, OadrDistributeEvent.class);
    }
    
    /**
     * Converts the OadrCreatedEvent message into XML and submits it to the VTN.
     * @param oadrCreatedEvent the OadrCreatedEvent message being sent.
     */
    private void sendCreatedEventMessage(OadrCreatedEvent oadrCreatedEvent) {
        StringWriter stringWriter = new StringWriter();
        try {
            marshaller.marshal(oadrCreatedEvent, stringWriter);
        } catch (JAXBException e) {
            // VTN is still expecting a response, but we're kind of hosed.
            log.error("Unable to marshal OadrCreatedEvent", e);
            return;
        }
        
        OadrResponse response = submitRequestToVTN(stringWriter, OadrResponse.class);
        
        if (response != null) {
            EiResponse eiResponse = response.getEiResponse();
            if (eiResponse != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("VTN responded to oadrCreatedEvent message with Response ");
                sb.append("Code: " + eiResponse.getResponseCode());
                if (eiResponse.getResponseDescription() != null) {
                    sb.append(" and description: " + eiResponse.getResponseDescription());
                }
                log.debug(sb); 
            }
        } else {
            log.warn("No OadrResponse received from the VTN");
        }
    }
    
    /**
     * Submits a request to the VTN and returns the corresponding response.
     * @param stringWriter the StringWriter object containing the marshalled request.
     * @param returnType the type that the unmarshalled response should be.
     * @return an object of type returnType that contains the VTN's response to our request.
     */
    private <T> T submitRequestToVTN(StringWriter stringWriter, Class<T> returnType) {
      HttpsURLConnection httpsConn = null;
        try {
            String vtnUrl = configService.getVtnUrl();
            httpsConn = (HttpsURLConnection) new URL(vtnUrl).openConnection();
            httpsConn.setSSLSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault());
            
            if (isValidConnection(httpsConn)) {
                log.info("Validation of connection to " + vtnUrl + " was successful.");
            } else {
                log.warn("Validation of connection to " + vtnUrl + " was not successful.");
                return null;
            }
          
            // Set the appropriate HTTP parameters.
            httpsConn.setRequestProperty("Content-Type", "application/xml");
            httpsConn.setRequestProperty("accept-charset", "UTF-8");
            httpsConn.setDoOutput(true);
            httpsConn.setDoInput(true);
          
            log.debug("Sending XML oadrCreatedEvent message to " + vtnUrl + ": \n" + stringWriter);
          
            OutputStream outputStream = httpsConn.getOutputStream();
            outputStream.write(stringWriter.toString().getBytes());
            outputStream.close();
            
            Object unmarshalled = unmarshaller.unmarshal(httpsConn.getInputStream());
            if (returnType.isAssignableFrom(unmarshalled.getClass())) {
                return returnType.cast(unmarshalled);
            } else {
                throw new RuntimeException("Unable to ");
            }
        } catch (JAXBException e) {
            log.error("caught JAXBException in submitRequestToVTN", e);
        } catch (IOException e) {
            log.error("caught IOException in submitRequestToVTN", e);
        } finally {
            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }
        
        // We didn't successfully unmarshal an object.
        return null;
    }
}