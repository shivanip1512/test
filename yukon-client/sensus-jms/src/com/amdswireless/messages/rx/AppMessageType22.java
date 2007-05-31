/**
 * Copyright (C) 2006 Sensus MS, all rights reserved.
 */

package com.amdswireless.messages.rx;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;

/**
 * @author Xuming.Chen
 * @since: 11/15/2006
 *
 * @version: AppMessageType18.java v 1.0  11/20/2006   xumingc
 */

/*
 * This is a parser for message type 22 (App Code 34)
 * The bit field looks like this:
 *  Byte         Field
 *  ------------------------------------------------
 *  0        Current Device Status
 *  1        Current Device Temperature
 *  2-3      Current Battery Voltage
 *  4        Last Tx Temperature
 *  5        Last Tx Battery Voltage
 *  6        Last Event Flags
 *  7-10     Time Since Last Event
 *  11       1st Historical Event Flags
 *  12-15    Time Between 1st Historical Event
 *  16       2nd Historical Event Flags
 *  17-20    Time Between 2nd Historical Evnet and 1st Historical Event
 *  21       3rd Historical Event Flags
 *  22-25    Time Between 3rd Historical Event and 2nd Historical Event
 *  26-27    Reserved
 *  ------------------------------------------------
 */

public class AppMessageType22 extends ReadMessage implements AppMessage, Serializable
{
    /**
     *
     */
    private transient static final long serialVersionUID = -2877651348462135109L;
    private transient static final int offset = 31;

    private boolean statusNo60HzOrUnderLineCurrent;
    private boolean statusLatchedFault;
    private boolean statusEventTransBit;
    private int currentDeviceTemperature;
    private float currentBatteryVoltage;
    private int lastTxTemperature;
    private float lastTxBatteryVoltage;
    private Event lastEvent = new Event();
    private Event firstHistoricalEvent = new Event();
    private Event secondHistoricalEvent = new Event();
    private Event thirdHistoricalEvent = new Event();

    private transient List<Event> allEvents = new AbstractList<Event>() {

        @Override
        public Event get(int index) {
            switch (index) {
            case 0:
                return lastEvent;
            case 1:
                return firstHistoricalEvent;
            case 2:
                return secondHistoricalEvent;
            case 3:
                return thirdHistoricalEvent;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public int size() {
            return 4;
        }

    };


    public class Event {
        protected boolean populated;
        protected boolean restoreAfterFault;
        protected boolean no60HzDetectedFollowingFault;
        protected boolean faultDetected;
        protected long secondsSinceEvent; // always computed since now, as opposed to wire format
        public boolean isFaultDetected() {
            return faultDetected;
        }
        public boolean isNo60HzDetectedFollowingFault() {
            return no60HzDetectedFollowingFault;
        }
        public boolean isRestoreAfterFault() {
            return restoreAfterFault;
        }
        public long getSecondsSinceEvent() {
            return secondsSinceEvent;
        }
        public boolean isPopulated() {
            return populated;
        }
        @Override
        public String toString() {
            ToStringCreator creator = new ToStringCreator(this)
                .append("populated", populated)
                .append("restoreAfterFault", restoreAfterFault)
                .append("no60HzDetectedFollowingFault", no60HzDetectedFollowingFault)
                .append("faultDetected", faultDetected)
                .append("secondsSinceEvent", secondsSinceEvent);
            return creator.toString();
        }
    }

    /**
     *  Default constructor for encoding.
     */
    public AppMessageType22() {
        super();
        super.setAppCode(0x22);
        super.setMessageType(0x22);
    }

    /**
     *  Default constructor for decoding. Extract information from Andorian message.
     *  @param msg
     */
    public AppMessageType22( char[] msg ) {
        super(msg);
        super.setAppCode(0x22);
        super.setMessageType(0x22);

        statusNo60HzOrUnderLineCurrent = (msg[0 + offset] & 0x2) == 0x2;
        statusLatchedFault = (msg[0 + offset] & 0x4) == 0x4;              // If event bit is NOT set, this defines a fault.
        statusEventTransBit = (msg[0 + offset] & 0x80) == 0x80;
        byte tempByte = 0;
        tempByte |= msg[1 + offset]; // put bits in a signed type
        currentDeviceTemperature = tempByte; // allow sign extension to occur
        currentBatteryVoltage = (float)(((msg[3 + offset] & 0xff) << 8) | (msg[2 + offset] & 0xff)) / 64 + 2;
        tempByte = 0;
        tempByte |= msg[1 + offset];
        lastTxTemperature = tempByte;
        lastTxBatteryVoltage = (float)(msg[5 + offset] & 0xff) / 64 + 2;

        lastEvent.populated = false;
        firstHistoricalEvent.populated = false;
        secondHistoricalEvent.populated = false;
        thirdHistoricalEvent.populated = false;

        // last event
        lastEvent.restoreAfterFault = (msg[6 + offset] & 0x1) == 0x1;
        lastEvent.no60HzDetectedFollowingFault = (msg[6 + offset] & 0x2) == 0x2;
        lastEvent.faultDetected = (msg[6 + offset] & 0x4) == 0x4;
        lastEvent.secondsSinceEvent = extractTime(msg, 7);
        if ((msg[6 + offset] & 0x05) != 0x05) {
            lastEvent.populated = lastEvent.secondsSinceEvent != 0xffffffffL;
        }

        // first historical
        firstHistoricalEvent.restoreAfterFault = (msg[11 + offset] & 0x1) == 0x1;
        firstHistoricalEvent.no60HzDetectedFollowingFault = (msg[11 + offset] & 0x2) == 0x2;
        firstHistoricalEvent.faultDetected = (msg[11 + offset] & 0x4) == 0x4;
        firstHistoricalEvent.secondsSinceEvent = extractTime(msg, 12);
        firstHistoricalEvent.secondsSinceEvent += lastEvent.secondsSinceEvent;
        if ((msg[11 + offset] & 0x05) != 0x05) {
            firstHistoricalEvent.populated = firstHistoricalEvent.secondsSinceEvent != 0xffffffffL;
        }

        // second historical
        secondHistoricalEvent.restoreAfterFault = (msg[16 + offset] & 0x1) == 0x1;
        secondHistoricalEvent.no60HzDetectedFollowingFault = (msg[16 + offset] & 0x2) == 0x2;
        secondHistoricalEvent.faultDetected = (msg[16 + offset] & 0x4) == 0x4;
        secondHistoricalEvent.secondsSinceEvent = extractTime(msg, 17);
        secondHistoricalEvent.secondsSinceEvent += firstHistoricalEvent.secondsSinceEvent;
        if ((msg[16 + offset] & 0x05) != 0x05) {
            secondHistoricalEvent.populated = secondHistoricalEvent.secondsSinceEvent != 0xffffffffL;
        }

        // third historical
        thirdHistoricalEvent.restoreAfterFault = (msg[21 + offset] & 0x1) == 0x1;
        thirdHistoricalEvent.no60HzDetectedFollowingFault = (msg[21 + offset] & 0x2) == 0x2;
        thirdHistoricalEvent.faultDetected = (msg[21 + offset] & 0x4) == 0x4;
        thirdHistoricalEvent.secondsSinceEvent = extractTime(msg, 22);
        thirdHistoricalEvent.secondsSinceEvent += secondHistoricalEvent.secondsSinceEvent;
        if ((msg[21 + offset] & 0x05) != 0x05) {
            thirdHistoricalEvent.populated = thirdHistoricalEvent.secondsSinceEvent != 0xffffffffL;
        }
    }

    private long extractTime(char[] bytes, int start) {
        long t = 0;
        t |= (long)(bytes[start + 3 + offset] & 0xff) << 24;
        t |= (long)(bytes[start + 2 + offset] & 0xff) << 16;
        t |= (long)(bytes[start + 1 + offset] & 0xff) << 8;
        t |= (long)bytes[start + offset] & 0xff;
        if (t > 0xff000000l) {
            t = 0;
        }
        return t;
    }

    public float getCurrentBatteryVoltage() {
        return currentBatteryVoltage;
    }

    public int getCurrentDeviceTemperature() {
        return currentDeviceTemperature;
    }

    public Event getFirstHistoricalEvent() {
        return firstHistoricalEvent;
    }

    public Event getLastEvent() {
        return lastEvent;
    }

    public float getLastTxBatteryVoltage() {
        return lastTxBatteryVoltage;
    }

    public int getLastTxTemperature() {
        return lastTxTemperature;
    }

    public Event getSecondHistoricalEvent() {
        return secondHistoricalEvent;
    }

    public boolean isStatusEventTransBit() {
        return statusEventTransBit;
    }

    public boolean isStatusLatchedFault() {
        return statusLatchedFault;
    }

    public boolean isStatusNo60HzOrUnderLineCurrent() {
        return statusNo60HzOrUnderLineCurrent;
    }

    public Event getThirdHistoricalEvent() {
        return thirdHistoricalEvent;
    }

    public List<Event> getAllEvents() {
        return allEvents;
    }

    @Override
    public String toString() {
        ToStringCreator creator = new ToStringCreator(this)
            .append("appSeq", getAppSeq())
            .append("statusNo60HzOrUnderLineCurrent", statusNo60HzOrUnderLineCurrent)
            .append("statusLatchedFault", statusLatchedFault)
            .append("statusEventTransBit", statusEventTransBit)
            .append("currentDeviceTemperature", currentDeviceTemperature)
            .append("currentBatteryVoltage", currentBatteryVoltage)
            .append("lastTxTemperature", lastTxTemperature)
            .append("lastTxBatteryVoltage", lastTxBatteryVoltage)
            .append("lastEvent", lastEvent)
            .append("firstHistoricalEvent", firstHistoricalEvent)
            .append("secondHistoricalEvent", secondHistoricalEvent)
            .append("thirdHistoricalEvent", thirdHistoricalEvent);

        return creator.toString() + getRawMessage();
    }

}