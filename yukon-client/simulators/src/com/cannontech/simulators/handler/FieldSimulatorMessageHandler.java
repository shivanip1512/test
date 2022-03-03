package com.cannontech.simulators.handler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.ThriftRequestReplyTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.messaging.serialization.thrift.serializer.fieldSimulator.FieldSimulatorStatusRequestSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.fieldSimulator.FieldSimulatorStatusResponseSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.fieldSimulator.ModifyFieldSimulatorRequestSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.fieldSimulator.ModifyFieldSimulatorResponseSerializer;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.FieldSimulatorStatusRequest;
import com.cannontech.simulators.message.request.ModifyFieldSimulatorRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.FieldSimulatorStatusResponse;
import com.cannontech.simulators.message.response.ModifyFieldSimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;

public class FieldSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(FieldSimulatorMessageHandler.class);

    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    
    ThriftRequestReplyTemplate<FieldSimulatorStatusRequest, FieldSimulatorStatusResponse> statusRequest;
    ThriftRequestReplyTemplate<ModifyFieldSimulatorRequest, ModifyFieldSimulatorResponse> configRequest;
    
    public FieldSimulatorMessageHandler() {
        super(SimulatorType.FIELD_SIMULATOR);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest request) {
        
        try {
            if (request instanceof ModifyFieldSimulatorRequest) {
                var simulatorRequest = (ModifyFieldSimulatorRequest) request;

                log.info("Attempting to modify Field Simulator settings:\n{}", simulatorRequest.getFieldSimulatorSettings());
                
                return modify(simulatorRequest);
            } else if (request instanceof FieldSimulatorStatusRequest) {
                return getStatus();
            } else {
                throw new IllegalArgumentException("Unsupported request type received: " + request.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error("Exception handling request: " + request);
            throw e;
        }
    }

    private ModifyFieldSimulatorResponse modify(ModifyFieldSimulatorRequest simulatorRequest) {
        var future = new CompletableFuture<ModifyFieldSimulatorResponse>();
        
        configRequest.send(simulatorRequest, future);
        
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.catching(e);
            return null;
        }
    }
    
    private FieldSimulatorStatusResponse getStatus() {
        var future = new CompletableFuture<FieldSimulatorStatusResponse>();
        
        statusRequest.send(new FieldSimulatorStatusRequest(), future);
        
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.catching(e);
            return null;
        }
    }
    
    @PostConstruct
    public void initialize() {
        statusRequest = new ThriftRequestReplyTemplate<>(
                jmsTemplateFactory.createTemplate(JmsApiDirectory.FIELD_SIMULATOR_STATUS),
                new FieldSimulatorStatusRequestSerializer(),
                new FieldSimulatorStatusResponseSerializer());

        configRequest = new ThriftRequestReplyTemplate<>(
                jmsTemplateFactory.createTemplate(JmsApiDirectory.FIELD_SIMULATOR_CONFIGURATION),
                new ModifyFieldSimulatorRequestSerializer(),
                new ModifyFieldSimulatorResponseSerializer());
    }
}
