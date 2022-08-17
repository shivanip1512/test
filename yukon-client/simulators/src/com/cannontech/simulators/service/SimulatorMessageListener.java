package com.cannontech.simulators.service;

import java.util.Set;

import javax.jms.ObjectMessage;

import org.apache.activemq.DestinationDoesNotExistException;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.simulators.SimulatorUtils;
import com.cannontech.simulators.handler.SimulatorMessageHandler;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;

/**
 * Accepts incoming simulator request messages and routes them to the appropriate simulator message handler, then sends 
 * the response.
 */
public class SimulatorMessageListener {
    private static final Logger log = YukonLogManager.getLogger(SimulatorMessageListener.class);
    
    private JmsTemplate jmsTemplate;
    private Set<SimulatorMessageHandler> messageHandlers;
    private Thread listenerThread;
    private volatile boolean isActive;
    
    public SimulatorMessageListener(JmsTemplate jmsTemplate, Set<SimulatorMessageHandler> messageHandlers) {
        this.jmsTemplate = jmsTemplate;
        this.messageHandlers = messageHandlers;
    }
    
    /**
     * Starts the listener thread, if it isn't started already.
     */
    public void start() {
        if (listenerThread != null) {
            return;
        }
        
        listenerThread = new Thread() {
            @Override
            public void run() {
                while (isActive) {
                    try {
                        Object message = jmsTemplate.receive(SimulatorUtils.SIMULATORS_REQUEST_QUEUE);
                        if (message != null && message instanceof ObjectMessage) {
                            log.debug("Processing simulator request message");
                            ObjectMessage request = (ObjectMessage) message;
                            SimulatorRequest simulatorRequest = (SimulatorRequest) request.getObject();
                            log.debug(simulatorRequest);
                            SimulatorResponse response = processRequest(simulatorRequest);
                            log.debug(response);
                            if (response != null) {
                                jmsTemplate.convertAndSend(request.getJMSReplyTo(), response);
                            }
                        }
                    } catch (Exception e) {
                        if (e instanceof DestinationDoesNotExistException) {
                            log.debug("No destination for simulator response.");
                        }
                        log.error("Error processing simulator request message", e);
                    }
                }
                log.info("Simulator message listener shutting down.");
                listenerThread = null;
            }
        };
        
        isActive = true;
        listenerThread.start();
    }
    
    /**
     * Attempts to process a SimulatorRequest by passing it to an appropriate handler. 
     * @throws IllegalArgumentException if the request has a SimulatorType that is not supported by any handler.
     */
    private SimulatorResponse processRequest(SimulatorRequest request) {
        for (SimulatorMessageHandler handler : messageHandlers) {
            if (handler.canHandle(request)) {
                return handler.handle(request);
            }
        }
        throw new IllegalArgumentException("No handler found for simulator request: " + request);
    }
    
    /**
     * Flags the listener thread to stop. (It may take some time for the thread to "see" the flag, so the thread will
     * not stop instantaneously.)
     */
    public void stop() {
        isActive = false;
    }
    
    public boolean isActive() {
        return isActive;
    }
}
