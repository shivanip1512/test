package com.cannontech.dr.rfn.service.impl;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.dr.rfn.model.SimulatorSettings;

public abstract class RfnDataSimulatorService {

    protected static final int MINUTES_IN_A_DAY = 1440;
    
    protected final Random randomizer = new Random();
    protected static final int RANDOM_MIN = 1;
    protected static final int RANDOM_MAX = 100;
    
    private final Logger log = YukonLogManager.getLogger(RfnDataSimulatorService.class);
    
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    
    protected JmsTemplate jmsTemplate;
    protected boolean isScheduled = false;
    protected SimulatorSettings settings;
    
    @PostConstruct
    public void initialize() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(false);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(false);
    }
    
    /**
     * Setup a schedule to run every 1 minute.
     * In a new thread send read request for this minute.
     */
    protected void schedule() {
        if (!isScheduled) {
            isScheduled = true;
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    // creating a new thread in case the sending read requests didn't complete in 1 minute, which probably
                    // will not happen.
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                DateTime now = new DateTime();
                                int minuteOfTheDay = now.getMinuteOfDay();
                                execute(minuteOfTheDay);
                            } catch (Exception e) {
                                log.error("Error occurred during running of data simulator.", e);
                            }
                        }
                    };
                    executor.execute(thread);
                }
            }, 0, 1, TimeUnit.MINUTES);
            log.info("Scheduled a task to send read requests once a minute.");
        }
    }
    
    protected abstract void execute(int minuteOfTheDay);
    
    /**
     * Example:
     * Existing devices see approximately 10% duplicates.
     * Generate random number between 1 and 100.
     * If the number is less then 10 or equals to 10 returns true.
     * If true is returned a duplicate read archive request will be generated.
     */
    protected boolean needsDuplicate() {
        //settings can be null if cancel was called right before this method
        if (settings == null || settings.getPercentOfDuplicates() < 1) {
            return false;
        }
        int number = generateRandomNumber(RANDOM_MIN, RANDOM_MAX);
        if (number > settings.getPercentOfDuplicates()) {
            return false;
        }
        return true;
    }
    
    /**
     * Generates random number for range.
     */
    protected int generateRandomNumber(int min, int max) {
        // nextInt excludes the top value so we have to add 1 to include the top value
        int randomNum = randomizer.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    protected int getMinuteOfTheDay(String serialNumber){
        // debug - inject the data next minute
        // DateTime now = new DateTime();
        //int minuteOfTheDay = now.getMinuteOfDay() + 1;
        
        //  Make sure we get serials ending with 0-9 each minute
        int minuteOfTheDay = Integer.parseInt(serialNumber) / 10 % MINUTES_IN_A_DAY;
        return minuteOfTheDay;
    }
}
