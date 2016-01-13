package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveResponse;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.amr.rfn.model.RfnMeterPlusReadingData;
import com.cannontech.amr.rfn.service.RfnChannelDataConverter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnArchiveStartupNotification;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.services.calculated.CalculatedPointDataProducer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@ManagedResource
public class MeterReadingArchiveRequestListener extends ArchiveRequestListenerBase<RfnMeterReadingArchiveRequest> {
    
    private static final Logger log = YukonLogManager.getLogger(MeterReadingArchiveRequestListener.class);
    
    @Autowired private RfnChannelDataConverter converter;
    @Autowired private CalculatedPointDataProducer calculatedProducer;
    
    private static final String archiveResponseQueueName = "yukon.qr.obj.amr.rfn.MeterReadingArchiveResponse";
    
    private List<Converter> converters; // Threads to convert channel data to point data
    private List<Calculator> calculators; // Threads to calculate point data based on converted channel data
    private AtomicInteger archivedReadings = new AtomicInteger();
    
    /**
     * Special thread class to handle archiving channel data converted point data.
     */
    public class Converter extends ConverterBase {
        public Converter(int converterNumber, int queueSize) {
            super("MeterReadingArchiveConverter", converterNumber, queueSize);
        }
        
        @Override
        public void processData(RfnDevice device, RfnMeterReadingArchiveRequest request) {
            RfnMeterPlusReadingData meterPlusReadingData = new RfnMeterPlusReadingData(device, request.getData());
            List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
            List<CalculationData> toCalculate = pointDataProducer.convert(meterPlusReadingData, messagesToSend);

            asyncDynamicDataSource.putValues(messagesToSend);
            archivedReadings.addAndGet(messagesToSend.size());

            sendAcknowledgement(request);
            incrementProcessedArchiveRequest();
            if (log.isDebugEnabled()) {
                log.debug(messagesToSend.size() + " PointDatas converted for RfnMeterReadingArchiveRequest, "
                        + toCalculate.size() + " calculations produced.");
            }

            // Converting went well which means the device exist, we can queue the corresponding calculator now.
            // We only need to do this if we found any channel data that we use to calculate other data, also we
            // only do this for interval readings for now since we are just generating per interval values and
            // load profile.
            //
            // Order is important: we update the producers cache BEFORE we queue the calculator thread so the data
            // will be there before he needs it. This also means since this is currently the only thing that seeds
            // the cache, we will never handle the first interval after startup.
            if (request.getReadingType() == RfnMeterReadingType.INTERVAL && !toCalculate.isEmpty()) {
                try {
                    calculatedProducer.updateCache(toCalculate);
                    calculators.get(getConverterNumber()).queue(toCalculate);
                    log.debug(toCalculate.size() + " calculations queued.");
                } catch (InterruptedException e) {
                    log.warn("interrupted while queuing generate request", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    /**
     * Special thread class to handle generating point data based on calculations from converted channel data.
     */
    public class Calculator extends CalculatorBase {
        public Calculator(int calculatorNumber, int queueSize) {
            super(calculatorNumber, queueSize);
        }
        
        @Override
        public void process(List<CalculationData> toCalculate) {
            List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
            calculatedProducer.calculate(toCalculate, messagesToSend);

            asyncDynamicDataSource.putValues(messagesToSend);
            log.debug(messagesToSend.size() + " PointDatas calculated for RfnMeterReadingArchiveRequest");
        }
    }
    
    @Override
    @PostConstruct
    public void init() {
        // setup as many workers as requested
        ImmutableList.Builder<Converter> converterBuilder = ImmutableList.builder();
        ImmutableList.Builder<Calculator> calculatorBuilder = ImmutableList.builder();
        
        int workerCount = getWorkerCount();
        int queueSize = getQueueSize();
        for (int i = 0; i < workerCount; ++i) {
            Converter converter = new Converter(i, queueSize);
            converterBuilder.add(converter);
            converter.start();
            
            Calculator calculator = new Calculator(i, queueSize);
            calculatorBuilder.add(calculator);
            calculator.start();
        }
        converters = converterBuilder.build();
        calculators = calculatorBuilder.build();
        
        // This message is signifying that we are ready to archive data from Network Manager
        // (we only need to send this message once... so we are doing it for no particular reason in this listener)
        RfnArchiveStartupNotification notif = new RfnArchiveStartupNotification();
        jmsTemplate.convertAndSend("yukon.notif.obj.common.rfn.ArchiveStartupNotification", notif);
    }
    
    @PreDestroy
    @Override
    protected void shutdown() {
        // should handle listener container here as well
        for (Converter converter : converters) {
            converter.shutdown();
        }
        for (Calculator calculator : calculators) {
            calculator.shutdown();
        }
    }
    
    @Override
    protected List<Converter> getConverters() {
        return converters;
    }
    
    @Override
    protected RfnMeterReadingArchiveResponse getRfnArchiveResponse(RfnMeterReadingArchiveRequest request) {
        RfnMeterReadingArchiveResponse response = new RfnMeterReadingArchiveResponse();
        response.setDataPointId(request.getDataPointId());
        response.setReadingType(request.getReadingType());
        return response;
    }

    @Override
    protected String getRfnArchiveResponseQueueName() {
        return archiveResponseQueueName;
    }

    @ManagedAttribute
    public int getArchivedReadings() {
        return archivedReadings.get();
    }
}
