package com.cannontech.sensus;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Formatter;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class GPUFFProtocol {
    private Logger log = YukonLogManager.getLogger(GPUFFProtocol.class);

    private static final int FLAG_OFFSET = 2;
    private static final int LEN_MSB = 2;
    private static final int LEN_LSB = 3;
    private static final int CID_OFFSET = 4; // Customer Id
    private static final int SEQUENCE_OFFSET = 6;
    private static final int DEVTYPE_OFFSET = 8;
    private static final int DEVREV_OFFSET = 10;
    private static final int SERIAL_OFFSET = 11;
    private static final int FIRST_FCN = 15;

    private static final int NAME_SIZE = 128;
    private static final float DEFAULT_SCALING = 1000.0f;
    private static final float GEO_SCALING = 10000.0f;

    private static final int BUFFER_LEN = 1024;

    static final short DEVICE_TYPE_FCI = 0x0001;
    static final short DEVICE_TYPE_CBNM = 0x0002;
    static final short DEVICE_TYPE_SENSUSFCI = 0x0003;

    private short len = 0;
    private short flag_len = 0;
    private short cid = 0;
    private short sequence = 0;
    private short deviceType = 0;
    private byte deviceRevision = 0;
    private short crc = 0;
    private ByteBuffer bBuf = ByteBuffer.allocate(BUFFER_LEN);

    private GpuffConfig cfg = null;
    // May need a list of more than one buffer to represent the given inbound

    // Parameters for the Commissioning Message
    private boolean cmSet = false;
    private byte cmOpValues = 0;
    private float cmLatitude = 0.0f;
    private float cmLongitude = 0.0f;
    private String cmName;
    private short cmAmpT = 0; // Amperage Threshold valid only for neutral
    // current sensor device type.

    // Parameters for FCI Device Values Messages.
    // The Sensus FCI has only battery, time, temp, and status.
    private boolean dvSet = false;
    private byte dvFlags = 0;
    private byte dvStatus = 0;
    private Date dvTime = new Date();
    private float dvBattery = 0.0f;
    private short dvTemp = 0;
    private boolean gpuffMsg;
    private boolean crcIncluded;
    private boolean ackWithAckReq;
    private boolean needsAck;
    private boolean ackResponseBit;
    private boolean configDecode;

    public GPUFFProtocol() {
        init();
    }

    public GPUFFProtocol(byte[] buf, int length) {
        assignData(buf, length);
    }

    private void init() {
        len = 0;
        gpuffMsg = false;
        crcIncluded = false;
        ackWithAckReq = false;
        needsAck = false;
        ackResponseBit = false;
        configDecode = false;
        bBuf.clear();
    }

    public void primeHeader() {
        len = 0;
        bBuf.put(len++, (byte) 0xa5); // header
        bBuf.put(len++, (byte) 0x96); // header
        bBuf.put(len++, (byte) 0x00); // 2 byte flags/len
        bBuf.put(len++, (byte) 0x00);
        bBuf.put(len++, (byte) 0x00); // 2 byte CID
        bBuf.put(len++, (byte) 0x00);
        bBuf.put(len++, (byte) 0x00); // 2 byte SEQ
        bBuf.put(len++, (byte) 0x00);
        bBuf.put(len++, (byte) 0x00); // 2 byte device type
        bBuf.put(len++, (byte) 0x00);
        bBuf.put(len++, (byte) 0x00); // Device Revision
        bBuf.put(len++, (byte) 0x00); // 4 byte serial number
        bBuf.put(len++, (byte) 0x00);
        bBuf.put(len++, (byte) 0x00);
        bBuf.put(len++, (byte) 0x00);

        bBuf.put(FLAG_OFFSET, (byte) ((0x0000ff00 & flag_len) >> 8));
        bBuf.put(FLAG_OFFSET + 1, (byte) (0x000000ff & flag_len));
        bBuf.put(CID_OFFSET, (byte) ((0x0000ff00 & cid) >> 8));
        bBuf.put(CID_OFFSET + 1, (byte) (0x000000ff & cid));
        bBuf.put(SEQUENCE_OFFSET, (byte) ((0x0000ff00 & sequence) >> 8));
        bBuf.put(SEQUENCE_OFFSET + 1, (byte) (0x000000ff & sequence));
        bBuf.put(DEVTYPE_OFFSET, (byte) ((0x0000ff00 & deviceType) >> 8));
        bBuf.put(DEVTYPE_OFFSET + 1, (byte) (0x000000ff & deviceType));
        bBuf.put(DEVREV_OFFSET, (byte) (0x000000ff & deviceRevision));
        bBuf.put(SERIAL_OFFSET, (byte) ((0xff000000 & getGpuffConfig().getSerial()) >> 24));
        bBuf.put(SERIAL_OFFSET + 1, (byte) ((0x00ff0000 & getGpuffConfig().getSerial()) >> 16));
        bBuf.put(SERIAL_OFFSET + 2, (byte) ((0x0000ff00 & getGpuffConfig().getSerial()) >> 8));
        bBuf.put(SERIAL_OFFSET + 3, (byte) (0x000000ff & getGpuffConfig().getSerial()));

    }

    public void primeResponse(byte[] buf) {
        init();
        for (int i = 0; i < 15; i++) { // Copy the header from the inbound
            // message.
            bBuf.put(len++, buf[i]);
        }
    }

    public void buildDeviceCommissioningInfo() {
        String name = getSerialNumber() + "." + getCmName() + ".SensusFlexNet";

        primeHeader();

        put((byte) 0x00); // This is the START of the GPUFF - FPLD block. Device
        // Config is a 0x00
        put((byte) 0x00); // FLAGS
        put((int) (getCmLatitude() * GEO_SCALING));
        put((int) (getCmLongitude() * GEO_SCALING));

        for (int i = 0; i < NAME_SIZE; i++) {
            if (i < name.length()) {
                put((byte) name.charAt(i));
            } else {
                put((byte) 0x00);
            }
        }

        return;
    }

    public void buildDeviceValuesMessage(boolean fault, boolean event, boolean noAC) {

        primeHeader();

        put((byte) 0x01); // This is the START of the GPUFF - FPLD block. Device
        // Values is a 0x01
        put((byte) 0x38); // FLAGS: Time & Voltage included.
        put((byte) ((fault ? 0x80 : 0x00) | (event ? 0x40 : 0x00) | (noAC ? 0x20 : 0x00)));
        put((int) (dvTime.getTime() / 1000));
        put((short) (getDvBattery() * DEFAULT_SCALING));
        put(getDvTemp());

        return;
    }

    public byte[] getBytes() {
        byte[] retBytes = new byte[len];

        setLength();
        for (int i = 0; i < len; i++) {
            retBytes[i] = bBuf.get(i);
        }

        return retBytes;
    }

    public short getCrc() {
        return crc;
    }

    public void setCrc(short crc) {
        this.crc = crc;
    }

    public byte getDeviceRevision() {
        return deviceRevision;
    }

    public void setDeviceRevision(byte deviceRevision) {
        this.deviceRevision = deviceRevision;
    }

    public short getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(short deviceType) {
        this.deviceType = deviceType;
    }

    public short getFlag_len() {
        return flag_len;
    }

    public void setFlag_len(short flag_len) {
        this.flag_len = flag_len;
    }

    public short getSequence() {
        return sequence;
    }

    public void setSequence(short sequence) {
        this.sequence = sequence;
    }

    public void setSerialNumber(int serialNum) {
        getGpuffConfig().setSerial(serialNum);
    }

    @SuppressWarnings("unused") private void put(long val) {
        bBuf.put(len++, (byte) ((0xff000000 & val) >> 24));
        bBuf.put(len++, (byte) ((0x00ff0000 & val) >> 16));
        bBuf.put(len++, (byte) ((0x0000ff00 & val) >> 8));
        bBuf.put(len++, (byte) (0x000000ff & val));
        return;
    }

    private void put(int val) {
        bBuf.put(len++, (byte) ((0xff000000 & val) >> 24));
        bBuf.put(len++, (byte) ((0x00ff0000 & val) >> 16));
        bBuf.put(len++, (byte) ((0x0000ff00 & val) >> 8));
        bBuf.put(len++, (byte) (0x000000ff & val));
        return;
    }

    private void put(short val) {
        bBuf.put(len++, (byte) ((0x0000ff00 & val) >> 8));
        bBuf.put(len++, (byte) (0x000000ff & val));
        return;
    }

    private void put(byte val) {
        bBuf.put(len++, (byte) (0x000000ff & val));
        return;
    }

    public short getCmAmpT() {
        return cmAmpT;
    }

    public void setCmAmpT(short cmAmpT) {
        this.cmAmpT = cmAmpT;
    }

    public float getCmLatitude() {
        return cmLatitude;
    }

    public void setCmLatitude(float cmLatitude) {
        this.cmLatitude = cmLatitude;
    }

    public float getCmLongitude() {
        return cmLongitude;
    }

    public void setCmLongitude(float cmLongitude) {
        this.cmLongitude = cmLongitude;
    }

    public String getCmName() {
        return cmName;
    }

    public void setCmName(String cmName) {
        this.cmName = cmName;
    }

    public byte getCmOpValues() {
        return cmOpValues;
    }

    public void setCmOpValues(byte cmOpValues) {
        this.cmOpValues = cmOpValues;
    }

    public boolean isCmSet() {
        return cmSet;
    }

    public void setCmSet(boolean cmSet) {
        this.cmSet = cmSet;
    }

    public float getDvBattery() {
        return dvBattery;
    }

    public void setDvBattery(float dvBattery) {
        this.dvBattery = dvBattery;
    }

    public byte getDvFlags() {
        return dvFlags;
    }

    public void setDvFlags(byte dvFlags) {
        this.dvFlags = dvFlags;
    }

    public boolean isDvSet() {
        return dvSet;
    }

    public void setDvSet(boolean dvSet) {
        this.dvSet = dvSet;
    }

    public byte getDvStatus() {
        return dvStatus;
    }

    public void setDvStatus(byte dvStatus) {
        this.dvStatus = dvStatus;
    }

    public Date getDvTime() {
        return dvTime;
    }

    public void setDvTime(Date dvTime) {
        this.dvTime = dvTime;
    }

    public int getSerialNumber() {
        return getGpuffConfig().getSerial();
    }

    public short getCid() {
        return cid;
    }

    public void setCid(short cid) {
        this.cid = cid;
    }

    short getDvTemp() {
        return dvTemp;
    }

    void setDvTemp(short dvTemp) {
        this.dvTemp = dvTemp;
    }

    public void assignData(byte[] buf, int length) {
        init();
        len = 0;
        // Copy the header from the in-bound message.
        for (int i = 0; i < length && len < BUFFER_LEN; i++) {
            bBuf.put(len++, buf[i]);
        }
    }

    public void decode() {

        gpuffMsg = ((bBuf.get(0) & 0x00ff) == 0x00a5 && (bBuf.get(1) & 0x00ff) == 0x0096);

        if (isGpuff()) {
            try {
                decodeGpuffHeader();

                if (!isGpuffACKwithACKRequest()) {

                    log.info("Decoding for device: " + getSerialNumber());
                    boolean fcnDecodeError = false;
                    bBuf.position(FIRST_FCN); // This is the location of the
                    // first
                    // FCN
                    while (!fcnDecodeError) {
                        if (bBuf.position() >= len - (crcIncluded ? 2 : 0))
                            break;

                        byte fcn = bBuf.get(); // Gets a byte and increments

                        switch (deviceType) {
                        case 0x01:
                        case 0x03:
                            switch (fcn) {
                            case 0x01:
                                long time = 0;
                                boolean fault = false,
                                event = false,
                                power = true;
                                short battV,
                                temp,
                                ampn,
                                ampp;
                                dvFlags = bBuf.get();
                                dvStatus = bBuf.get();

                                String report = "FCI Values report: ";
                                if ((dvFlags & 0x20) == 0x20) { // Time included
                                    boolean tso = false;
                                    if ((dvFlags & 0x80) == 0x80) {
                                        tso = true;
                                    }
                                    time = bBuf.getInt();
                                    Date t;
                                    if (tso) {
                                        t = new Date();
                                        t.setTime(t.getTime() - time * 1000L);
                                    } else {
                                        t = new Date(time * 1000);
                                    }

                                    report += t.toString();
                                }
                                if ((dvFlags & 0x10) == 0x10) {
                                    battV = bBuf.getShort();
                                    report += " Battery Voltage: " + (float) (battV / 1000.0);
                                }
                                if ((dvFlags & 0x08) == 0x08) {
                                    temp = bBuf.getShort();
                                    report += " Temperature: " + (float) (temp / 100.0);
                                }
                                if ((dvFlags & 0x04) == 0x04) {
                                    ampn = bBuf.getShort();
                                    report += " Avg Amps: " + ampn;
                                }
                                if ((dvFlags & 0x02) == 0x02) {
                                    ampp = bBuf.getShort();
                                    report += " Peak Amps: " + ampp;
                                }

                                if ((dvStatus & 0x80) == 0x80) { // No
                                    // Fault/Fault
                                    fault = true;
                                }
                                if ((dvStatus & 0x40) == 0x40) {
                                    event = true;
                                }
                                if ((dvStatus & 0x20) == 0x20) { // Power/No
                                    // Power
                                    power = false;
                                }

                                report += " Fault = " + fault + " Event = " + event + " Current Good = " + power;

                                log.info(report);
                                break;
                            case 0x03:
                                dvFlags = bBuf.get();

                                report = "FCI Current Survey Report: ";
                                boolean tso = false;
                                if ((dvFlags & 0x80) == 0x80) {
                                    tso = true;
                                }
                                time = bBuf.getInt();
                                Date tDate;
                                if (tso) {
                                    tDate = new Date();
                                    tDate.setTime(tDate.getTime() - time * 1000L);
                                } else {
                                    tDate = new Date(time * 1000);
                                }

                                short rate_cnt = bBuf.getShort();
                                int rate = (int) (rate_cnt >> 10);
                                int count = (int) (rate_cnt & 0x03ff);
                                long time_delta = 60000L; // 1 minute.
                                switch (rate) {
                                case 0:
                                    time_delta *= 60; // 60 minutes.
                                case 1:
                                    time_delta *= 30;
                                case 2:
                                    time_delta *= 15;
                                case 3:
                                    time_delta *= 5;
                                case 4:
                                    time_delta *= 120;
                                case 5:
                                    time_delta *= 240;
                                }
                                log.info(report);

                                for (int i = 0; i < count; i++) {
                                    if(bBuf.position() >= len - (crcIncluded ? 2 : 0))
                                    {
                                        log.warn("**** DECODING BEYOND BUFFER BOUNDARY - This should not occur - please check the decode.");
                                        break;
                                    }
                                    ampn = bBuf.getShort();
                                    log.info(tDate + " Record[" + i + "] = " + ampn);
                                    tDate.setTime(tDate.getTime() + time_delta);
                                }

                                break;
                            case 0x05:
                                time = 0;

                                dvFlags = bBuf.get();
                                short momCount = bBuf.getShort();

                                report = "FCI Momentary Count: " + momCount + " ";
                                if ((dvFlags & 0x20) == 0x20) { // Time included
                                    tso = false;
                                    if ((dvFlags & 0x80) == 0x80)
                                        tso = true;

                                    time = bBuf.getInt();

                                    if (tso) {
                                        tDate = new Date();
                                        tDate.setTime(tDate.getTime() - time * 1000L);
                                    } else {
                                        tDate = new Date(time * 1000);
                                    }

                                    report += tDate.toString();
                                }

                                if ((dvFlags & 0x04) == 0x04) {
                                    // Nominal Amps
                                    ampn = bBuf.getShort();
                                    report += " Nominal Amps = " + ampn;
                                }

                                if ((dvFlags & 0x02) == 0x02) {
                                    // Nominal Amps
                                    ampp = bBuf.getShort();
                                    report += " Peak Amps = " + ampp;
                                }

                                log.info(report);
                                break;
                            case 0x06:
                                Date t = new Date();
                                fault = false;
                                event = false;
                                power = true;
                                boolean calibrated = false;
                                boolean chgCktEnabled = false;

                                this.dvFlags = bBuf.get();
                                this.dvStatus = bBuf.get();
                                int record_count = bBuf.get() & 0xFF;

                                report = "FCI Status report: ";
                                if ((this.dvFlags & 0x20) == 32) {
                                    tso = false;
                                    if ((this.dvFlags & 0x80) == 0x80) {
                                        tso = true;
                                    }
                                    time = bBuf.getInt();

                                    if (tso) {
                                        // report += " TSO delta: " + time +
                                        // " ";
                                        t.setTime(t.getTime() - time * 1000L);
                                    } else {
                                        t = new Date(time * 1000L);
                                    }

                                    report += t.toString();
                                }

                                if ((this.dvStatus & 0x80) == 0x80) {
                                    fault = true;
                                }
                                if ((this.dvStatus & 0x40) == 0x40) {
                                    event = true;
                                }
                                if ((this.dvStatus & 0x20) == 0x20) {
                                    power = false;
                                }
                                if ((this.dvStatus & 0x10) == 0x10) {
                                    calibrated = true;
                                }
                                if ((this.dvStatus & 0x08) == 0x08) {
                                    chgCktEnabled = true;
                                }

                                report += " Fault = " + fault + " Event = " + event + " Current Good = " + power + " Calibrated = " + calibrated + " Charge Circuit Enabled = " + chgCktEnabled;

                                if ((this.dvFlags & 0x10) == 0x10) {
                                    battV = bBuf.getShort();
                                    report += " Battery Voltage: " + ((float) (battV / 1000.0D));
                                }
                                if ((this.dvFlags & 0x08) == 0x08) {
                                    temp = bBuf.getShort();
                                    report += " Temperature: " + ((float) (temp / 100.0D));
                                }
                                if ((this.dvFlags & 0x04) == 0x04) {
                                    ampn = bBuf.getShort();
                                    report += " Avg Amps: " + (ampn);
                                }
                                if ((this.dvFlags & 0x02) == 0x02) {
                                    ampp = bBuf.getShort();
                                    report += " Peak Amps: " + (ampp);
                                }

                                momCount = bBuf.getShort();

                                short th_high = bBuf.getShort();
                                short rpt_max = bBuf.getShort();
                                short rpt_min = bBuf.getShort();

                                report += " Momentary Count: " + momCount + " High Threshold " + th_high + " Report Max/Min " + rpt_max + "/" + rpt_min;

                                log.info(report);

                                t.setTime(t.getTime() - record_count * 3600 * 1000);
                                for (int i = 0; i < record_count; i++) {
                                    if(bBuf.position() >= len - (crcIncluded ? 2 : 0))
                                    {
                                        log.warn("**** DECODING BEYOND BUFFER BOUNDARY - This should not occur - please check the decode.");
                                        break;
                                    }

                                    t.setTime(t.getTime() + 3600000L);
                                    ampp = bBuf.getShort();
                                    log.info(t + " Amp Reading = " + ampp);
                                }

                                if ((dvFlags & 0x1) == 0x01) {
                                    ampp = bBuf.getShort();
                                    log.info(t + " Amp Reading rec[e] = " + ampp);
                                }
                                break;
                            case 0x08:
                                setConfigDecode(true);
                                int cfg_fcn_len = bBuf.get() & 0x00ff;
                                boolean ieDecodeError = false;
                                while (!ieDecodeError && bBuf.position() < cfg_fcn_len) {
                                    byte ie_type = bBuf.get();
                                    int ie_len = bBuf.get() & 0x00ff;
                                    int ie_position = bBuf.position();

                                    switch (ie_type) {
                                    case 0x01:
                                        int periodic = bBuf.get() & 0x00ff;
                                        getGpuffConfig().setPeriodic(periodic);
                                        bBuf.get();
                                        String tStr = decodeString(64);
                                        log.info("Periodic             " + getGpuffConfig().getPeriodic());
                                        log.info("Configured to APN    " + tStr);
                                        getGpuffConfig().setApn(tStr);
                                        break;
                                    case 0x02:
                                        byte ipFlag = (byte) (bBuf.get() & 0xFF);
                                        bBuf.get();
                                        String portStr = decodeString(6); // Port
                                        String IpStr = decodeString(64);

                                        if ((ipFlag & 0x01) == 0x01 || getGpuffConfig().getIp0() != null) {
                                            getGpuffConfig().setIp1(IpStr);
                                            getGpuffConfig().setPort1(portStr);
                                            log.info("Configured to URL[1] " + IpStr + ":" + portStr);
                                        } else {
                                            getGpuffConfig().setIp0(IpStr);
                                            getGpuffConfig().setPort0(portStr);
                                            log.info("Configured to URL[0] " + IpStr + ":" + portStr);
                                        }
                                        break;
                                    case 0x03:
                                        long cooperSN_hi = decodeUnsignedInt();
                                        long cooperSN_lo = decodeUnsignedInt();
                                        String radioSN = decodeString(16);
                                        log.info(new StringBuilder().append("Cooper Serial Number ").append(cooperSN_hi).append(" / ").append(cooperSN_lo).append(" Radio Serial Number ").append(radioSN).toString());
                                        bBuf.position(ie_position + ie_len);
                                        break;
                                    case 0x04:
                                        cooperSN_hi = decodeUnsignedInt();
                                        cooperSN_lo = decodeUnsignedInt();
                                        radioSN = decodeString(24);
                                        log.info(new StringBuilder().append("Long Cooper Serial Number ").append(cooperSN_hi).append(" / ").append(cooperSN_lo).append(" Radio Serial Number ").append(radioSN).toString());
                                        bBuf.position(ie_position + ie_len);
                                        break;
                                    case (byte) 0x85:
                                    case 0x05:
                                    case 0x06:
                                        if(ie_type != 0x06) {
                                            popBytes(64); // Ignore the debug buffer.
                                            bBuf.get(); // 0xaa
                                            bBuf.get(); // 0x55
                                        }
                                        int fwMajVer = bBuf.get() & 0x00ff;
                                        int fwMinVer = bBuf.get() & 0x00ff;
                                        short resetCnt = bBuf.getShort();
                                        short SPIErrors = bBuf.getShort();
                                        int momCnt = bBuf.get() & 0x00ff;
                                        int fltCnt = bBuf.get() & 0x00ff;
                                        int aClrCnt = bBuf.get() & 0x00ff;
                                        int pwrLossCnt = bBuf.get() & 0x00ff;
                                        int resMomCnt = bBuf.get() & 0x00ff;
                                        int revertCnt = bBuf.get() & 0x00ff;
                                        // an even number of bytes.

                                        getGpuffConfig().setFwMajor(fwMajVer);
                                        getGpuffConfig().setFwMinor(fwMinVer);
                                        getGpuffConfig().setResetCount(resetCnt);

                                        log.info("Diagnostic message received. Length: " + ie_len + ".");
                                        log.info("Diagnostic - Firmware Version " + fwMajVer + "." + fwMinVer);
                                        log.info("Diagnostic - Reset Count      " + resetCnt);
                                        log.info("Diagnostic - SPI Errors       " + SPIErrors);
                                        log.info("Diagnostic - Fault Count      " + fltCnt);
                                        log.info("Diagnostic - Momentary Cnt    " + momCnt);
                                        log.info("Diagnostic - Reset Mmtary Ct  " + resMomCnt);
                                        log.info("Diagnostic - All Clear Count  " + aClrCnt);
                                        log.info("Diagnostic - Power Loss Count " + pwrLossCnt);
                                        log.info("Diagnostic - APN Revert Count " + revertCnt);

                                        if ((deviceRevision >= 4) && (bBuf.position() < ie_position + ie_len)) {
                                            int abfw_major = bBuf.get() & 0xFF;
                                            int abfw_minor = bBuf.get() & 0xFF;
                                            int rssi = bBuf.get() & 0xFF;
                                            int ber = bBuf.get() & 0xFF;
                                            int rs_cnt = bBuf.get() & 0xFF;
                                            log.info("Diagnostic - AB FW Revision   " + abfw_major + "." + abfw_minor);
                                            log.info("Diagnostic - RSSI             " + rssi);
                                            log.info("Diagnostic - BER              " + ber);
                                            log.info("Diagnostic - Reed Switch Cnt  " + rs_cnt);
                                        }

                                        break;
                                    case 0x12:
                                        int report_period_hours = bBuf.get() & 0xFF;
                                        bBuf.get();
                                        short high_current_threshold = bBuf.getShort();
                                        bBuf.getShort();
                                        log.info("Report Period Hours    " + report_period_hours);
                                        log.info("High Current Threshold " + high_current_threshold);

                                        break;
                                    default:
                                        log.info("Unknown Information Element Type - Update the decode");
                                        ieDecodeError = true;
                                        break;
                                    }
                                }
                                break;
                            default:
                                log.info("Unknown GCVTx fcn code: " + fcn + " at buffer position " + bBuf.position());
                                fcnDecodeError = true;
                                break;
                            }
                            break;
                        case 0x02: // VAR Advisor.
                            int cfg_fcn_len;
                            boolean ieDecodeError;
                            switch (fcn) {
                            case 0x03:
                                String report = null;
                                byte flags = bBuf.get();
                                boolean hasTime = (flags & 0x10) == 0x10;
                                boolean calibrated = (flags & 0x02) == 0x02;
                                boolean reedSwitch = (flags & 0x01) == 0x01;
                                long time = 0L;
                                float battery = 0.0F;
                                float temperature = 0.0F;

                                Date now = new Date();
                                Date t = new Date();
                                report = "GVAR(x) value report: ";
                                if (hasTime) {
                                    time = bBuf.getInt();
                                    if ((flags & 0x80) == 0x80) {
                                        // report += " TSO delta: "+ time + " ";
                                        t.setTime(t.getTime() - time * 1000L);
                                    } else {
                                        t = new Date(time * 1000L);
                                    }
                                }
                                report += t + " GVAR(x) Report ";

                                if ((flags & 0x8) == 8) {
                                    battery = (float) (bBuf.getShort() / 1000.0D);
                                    report += " Battery Voltage: " + battery;
                                }
                                if ((flags & 0x4) == 4) {
                                    temperature = (float) (bBuf.getShort() / 100.0D);
                                    report += " Temperature: " + temperature;
                                }
                                report += (calibrated ? " Calibrated " : " UNCALIBRATED ");
                                report += (reedSwitch ? " Reed Triggered " : " Non Reed Triggered ");

                                int intervalCnt = 24;
                                int sampleRate = 3600;
                                int rate = 86400;
                                switch ((flags & 0x60) >> 5) {
                                case 0:
                                    intervalCnt = 24;
                                    sampleRate = 3600;
                                    rate = intervalCnt * sampleRate;
                                    break;
                                case 1:
                                    intervalCnt = 3;
                                    sampleRate = 300;
                                    rate = intervalCnt * sampleRate;
                                }

                                short thresholdHigh = bBuf.getShort();
                                short thresholdLow = bBuf.getShort();
                                short reportMax = bBuf.getShort();
                                short reportMin = bBuf.getShort();
                                byte recordCount = bBuf.get();
                                byte rptFlags = bBuf.get();
                                byte rptPeriod = (byte) (rptFlags & 0x3F);
                                boolean over = (rptFlags & 0x80) == 0x80;
                                boolean reset = (rptFlags & 0x40) == 0x40;

                                t.setTime(t.getTime() - rate * recordCount);
                                long maxtime,
                                mintime;
                                for (int i = 0; i < recordCount; i++) {
                                    t.setTime(t.getTime() + rate);
                                    short max = bBuf.getShort();
                                    short min = bBuf.getShort();
                                    byte tsMax = (byte) (bBuf.get() & 0x3F);
                                    byte tsMin = (byte) (bBuf.get() & 0x3F);
                                    mintime = t.getTime() - (intervalCnt - tsMin - 1) * sampleRate;
                                    maxtime = t.getTime() - (intervalCnt - tsMax - 1) * sampleRate;
                                }

                                short max = bBuf.getShort();
                                short min = bBuf.getShort();
                                byte tsMax = bBuf.get();
                                boolean valid = (tsMax & 0x40) == 0x40;
                                tsMax = (byte) (tsMax & 0x3F);
                                byte tsMin = (byte) (bBuf.get() & 0x3F);

                                if (valid) {
                                    if (reedSwitch) {
                                        maxtime = mintime = now.getTime();
                                    } else {
                                        if (reset) {
                                            mintime = t.getTime() + (tsMin + 1) * sampleRate;
                                            if (hasTime)
                                                maxtime = t.getTime() + (tsMax + 1) * sampleRate;
                                            else
                                                maxtime = 0L;
                                        } else if (over) {
                                            maxtime = t.getTime() + (tsMax + 1) * sampleRate;
                                            if (hasTime)
                                                mintime = t.getTime() + (tsMin + 1) * sampleRate;
                                            else {
                                                mintime = 0L;
                                            }
                                        }
                                    }
                                }

                                log.info(report);
                                break;
                            case 0x08:
                                setConfigDecode(true);
                                cfg_fcn_len = bBuf.get() & 0xFF;
                                ieDecodeError = false;

                                while ((!ieDecodeError) && (bBuf.position() < cfg_fcn_len)) {
                                    byte ie_type = bBuf.get();
                                    int ie_len = bBuf.get() & 0xFF;
                                    int ie_position = bBuf.position();

                                    switch (ie_type) {
                                    case 0x01:
                                        int periodic = bBuf.get() & 0xFF;
                                        getGpuffConfig().setPeriodic(periodic);
                                        bBuf.get();
                                        String tStr = decodeString(64);
                                        log.info(new StringBuilder().append("Periodic             ").append(getGpuffConfig().getPeriodic()).toString());
                                        log.info(new StringBuilder().append("Configured to APN    ").append(tStr).toString());
                                        getGpuffConfig().setApn(tStr);
                                        break;
                                    case 0x02:
                                        byte ipFlag = (byte) (bBuf.get() & 0xFF);
                                        bBuf.get();
                                        String portStr = decodeString(6);
                                        String IpStr = decodeString(64);

                                        if ((ipFlag & 0x01) == 0x01 || getGpuffConfig().getIp0() != null) {
                                            getGpuffConfig().setIp1(IpStr);
                                            getGpuffConfig().setPort1(portStr);
                                            log.info("Configured to URL[1] " + IpStr + ":" + portStr);
                                        } else {
                                            getGpuffConfig().setIp0(IpStr);
                                            getGpuffConfig().setPort0(portStr);
                                            log.info("Configured to URL[0] " + IpStr + ":" + portStr);
                                        }
                                        break;
                                    case 0x03:
                                        long cooperSN_hi = decodeUnsignedInt();
                                        long cooperSN_lo = decodeUnsignedInt();
                                        String radioSN = decodeString(16);
                                        log.info(new StringBuilder().append("Cooper Serial Number ").append(cooperSN_hi).append(" / ").append(cooperSN_lo).append(" Radio Serial Number ").append(radioSN).toString());
                                        bBuf.position(ie_position + ie_len);
                                        break;
                                    case 0x04:
                                        cooperSN_hi = decodeUnsignedInt();
                                        cooperSN_lo = decodeUnsignedInt();
                                        radioSN = decodeString(24);
                                        log.info(new StringBuilder().append("Long Cooper Serial Number ").append(cooperSN_hi).append(" / ").append(cooperSN_lo).append(" Radio Serial Number ").append(radioSN).toString());
                                        bBuf.position(ie_position + ie_len);
                                        break;
                                    case 0x11:
                                        int reportPeriod = bBuf.get() & 0xFF;
                                        bBuf.get();
                                        short overCurrent = bBuf.getShort();
                                        short resetCurrent = bBuf.getShort();
                                        if ((getGpuffConfig() instanceof GpuffConfigGVAR)) {
                                            try {
                                                ((GpuffConfigGVAR) getGpuffConfig()).setReportPeriod(reportPeriod);
                                                ((GpuffConfigGVAR) getGpuffConfig()).setOverCurrentLevel(overCurrent);
                                                ((GpuffConfigGVAR) getGpuffConfig()).setResetLevel(resetCurrent);
                                            } catch (Exception e) {
                                                log.info(getGpuffConfig().toString());
                                                e.printStackTrace();
                                            }
                                        } else {
                                            log.info("Wrong type of configuration - Check your config databases please (duplicates)?");
                                        }

                                        log.info(new StringBuilder().append("Report Period:           ").append(reportPeriod).toString());
                                        log.info(new StringBuilder().append("Over Current Threshold:  ").append(overCurrent).toString());
                                        log.info(new StringBuilder().append("Reset Current Threshold: ").append(resetCurrent).toString());
                                        bBuf.position(ie_position + ie_len);
                                        break;
                                    case (byte) 0x85:
                                    case 0x05:
                                    case 0x06:
                                        if (ie_type != 0x06) {
                                            popBytes(64);
                                            bBuf.get();
                                            bBuf.get();
                                        }
                                        int fwMajVer = bBuf.get() & 0xFF;
                                        int fwMinVer = bBuf.get() & 0xFF;
                                        short resetCnt = bBuf.getShort();
                                        short SPIErrors = bBuf.getShort();
                                        int abFwVerMaj = bBuf.get() & 0xFF;
                                        int abFwVerMin = bBuf.get() & 0xFF;
                                        int rssi = bBuf.get() & 0xFF;
                                        int ber = bBuf.get() & 0xFF;
                                        int resMomCnt = bBuf.get() & 0xFF;
                                        int revertCnt = bBuf.get() & 0xFF;
                                        bBuf.position(ie_position + ie_len);

                                        log.info(new StringBuilder().append("Diagnostic message received. Length: ").append(ie_len).append(".").toString());
                                        log.info(new StringBuilder().append("Diagnostic - Firmware Version  ").append(fwMajVer).append(".").append(fwMinVer).toString());
                                        log.info(new StringBuilder().append("Diagnostic - Reset Count       ").append(resetCnt).toString());
                                        log.info(new StringBuilder().append("Diagnostic - SPI Errors        ").append(SPIErrors).toString());
                                        log.info(new StringBuilder().append("Diagnostic - Analog FW Version ").append(abFwVerMaj).append(".").append(abFwVerMin).toString());
                                        log.info(new StringBuilder().append("Diagnostic - RSSI              ").append(rssi).toString());
                                        log.info(new StringBuilder().append("Diagnostic - BER               ").append(ber).toString());
                                        log.info(new StringBuilder().append("Diagnostic - Reset Momentary   ").append(resMomCnt).toString());
                                        log.info(new StringBuilder().append("Diagnostic - APN Revert Count  ").append(revertCnt).toString());

                                        break;
                                    default:
                                        log.info(new StringBuilder().append("Unknown Information Element Type ").append(ie_type).append(" - Update the decode").toString());
                                        ieDecodeError = true;
                                    }
                                }
                            }

                            break;
                        default:
                            log.info("I have not been trained to decode VAR Advisor FCN " + fcn + " at buffer position " + bBuf.position());
                            fcnDecodeError = true;
                            break;

                        }
                    }
                }
                decodeCRC();

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isGpuff() {
        return gpuffMsg;
    }

    public boolean isGpuffACKwithACKRequest() {
        return ackWithAckReq;
    }

    public boolean isGpuffNeedingACK() {
        if (isGpuffACKwithACKRequest()) {
            return false;
        }
        return needsAck;
    }

    public void buildACKMessage(byte[] srcDat) {
        primeResponse(srcDat);
        bBuf.put(FLAG_OFFSET, (byte) 0x04); // FLG/LEN ACK/NO CRC.
        setLength();
    }

    public void buildReconfigPacket(GpuffConfig gpc, byte[] srcDat) {
        // generate RECONFIG response
        primeResponse(srcDat);

        bBuf.put(FLAG_OFFSET, (byte) 0x04); // FLG/LEN ACK/NO CRC.
        int cfg_fnc_pos = len; // Position of the FNC!
        appendFNCConfigHeader(gpc.getFromUser(), gpc.getFromPassword());
        appendAPNConfig(gpc.getUser(), gpc.getPassword(), gpc.getApn(), gpc.getPeriodic());
        appendIPConfig(gpc.getIp0(), gpc.getPort0(), false);
        appendIPConfig(gpc.getIp1(), gpc.getPort1(), true);

        if (!(gpc instanceof GpuffConfigGCVT)) {
            if ((gpc instanceof GpuffConfigGVAR)) {
                appendGVARConfig((GpuffConfigGVAR) gpc);
            }
        }

        // Align the Configuration FCN as a whole (should be redundant since each piece is aligned, but ...
        twoByteAlign(cfg_fnc_pos + 1);
        setLength();
    }

    private void appendGVARConfig(GpuffConfigGVAR gpc) {
        bBuf.put(len++, (byte) 0x91); // -111);
        int gvlen_pos = len;
        bBuf.put(len++, (byte) 0x00);
        bBuf.put(len++, (byte) gpc.getReportPeriod());
        bBuf.put(len++, (byte) 0x00);
        bBuf.putShort(len, (short) gpc.getOverCurrentLevel());
        len = (short) (len + 2);
        bBuf.putShort(len, (short) gpc.getResetLevel());
        len = (short) (len + 2);
        twoByteAlign(gvlen_pos);
    }

    public void buildConfigRequestPacket(GpuffConfig gpc, byte[] srcDat) {
        // generate CONFIG REQUEST + ACK response
        primeResponse(srcDat);

        bBuf.put(FLAG_OFFSET, (byte) 0x04); // FLG/LEN ACK/NO CRC.
        int cfg_fnc_pos = len; // Position of the FNC! Used below to two-byte
        // align message.
        appendFNCConfigRequestHeader();
        twoByteAlign(cfg_fnc_pos + 1);
        setLength();
    }

    private void setLength() {
        int dat_len = len - 4;
        byte flg = (byte) (bBuf.get(FLAG_OFFSET) & (byte) 0xfc);
        byte flg_len = (byte) (flg | ((dat_len & 0x0300) >> 8));

        bBuf.put(LEN_MSB, flg_len);
        bBuf.put(LEN_LSB, (byte) (dat_len & 0x00ff));
    }

    public void insertConfigString(String cfg_str) {
        if (cfg_str != null) {
            for (int i = 0; i < cfg_str.length(); i++) {
                bBuf.put(len++, (byte) (0x00ff & cfg_str.charAt(i)));
            }
        }
        bBuf.put(len++, (byte) '\0'); // null terminate ascii
    }

    public void twoByteAlign(int start_pos) {
        if (((len - start_pos - 1) % 2) != 0)
            bBuf.put(len++, (byte) 0x00);
        bBuf.put(start_pos, (byte) (len - start_pos - 1));
    }

    public void appendFNCConfigHeader(String user, String pw) {
        bBuf.put(len++, (byte) 0x88);
        bBuf.put(len++, (byte) 0x00); // This is the CFG LEN Position.

        for (int i = 0; i < GpuffConfig.GPUFF_USER_SIZE; i++) {
            if (user != null && i < user.length()) {
                bBuf.put(len++, (byte) (0x00ff & user.charAt(i))); // User.
            } else {
                bBuf.put(len++, (byte) 0x00); // User.
            }
        }
        for (int i = 0; i < GpuffConfig.GPUFF_PW_SIZE; i++) {
            if (pw != null && i < pw.length()) {
                bBuf.put(len++, (byte) (0x00ff & pw.charAt(i))); // PW.
            } else {
                bBuf.put(len++, (byte) 0x00); // PW.
            }
        }
    }

    public void appendAPNConfig(String user, String pw, String apn, int periodicReport) {
        bBuf.put(len++, (byte) 0x81); // APN Config
        int apn_pos = len;
        bBuf.put(len++, (byte) 0x00); // This is the CFG LEN Position.

        for (int i = 0; i < GpuffConfig.GPUFF_USER_SIZE; i++) {
            if (user != null && i < user.length()) {
                bBuf.put(len++, (byte) (0x00ff & user.charAt(i))); // User.
            } else {
                bBuf.put(len++, (byte) 0x00); // User.
            }
        }
        for (int i = 0; i < GpuffConfig.GPUFF_PW_SIZE; i++) {
            if (pw != null && i < pw.length()) {
                bBuf.put(len++, (byte) (0x00ff & pw.charAt(i))); // PW.
            } else {
                bBuf.put(len++, (byte) 0x00); // PW.
            }
        }
        bBuf.put(len++, (byte) periodicReport); // Periodic report interval
        bBuf.put(len++, (byte) 0x00); // Reserved
        insertConfigString(apn);
        twoByteAlign(apn_pos);

    }

    public void appendIPConfig(String ip_str, String ip_port, boolean ip_one) {
        bBuf.put(len++, (byte) 0x82); // IP Config Number
        int ip_pos = len;
        bBuf.put(len++, (byte) 0x00); // LEN Slot
        bBuf.put(len++, (byte) (ip_one ? 0x01: 0x00)); // This is the LEN Position.
        bBuf.put(len++, (byte) 0x00); // Reserved

        ip_port = (ip_port == null ? "00000" : ip_port);
        for (int i = 0; i < GpuffConfig.GPUFF_PORT_SIZE - ip_port.length() - 1; i++) {
            log.info(new StringBuilder().append("Padding port number with zeros.  Please fix configuration db for serial ").append(getSerialNumber()).toString());
            bBuf.put(len++, (byte) '0'); // Probably a formatter for the string but I don't know it.  Needs to be zero padded 5 chars long.
        }

        insertConfigString(ip_port);
        insertConfigString(ip_str);
        twoByteAlign(ip_pos);
    }

    public void appendRPnThreshold(int report_period, short th_high) {
        bBuf.put(len++, (byte) 0x92); // IP Config Number
        int ie_len_pos = len;
        bBuf.put(len++, (byte) 0x00); // len slot
        bBuf.put(len++, (byte) report_period); // Reserved
        bBuf.put(len++, (byte) 0x00); // Reserved
        bBuf.putShort(len++, th_high);
        bBuf.put(len++, (byte) 0x00); // Reserved
        bBuf.put(len++, (byte) 0x00); // Reserved
        
        twoByteAlign(ie_len_pos);
    }

    public void appendFNCConfigRequestHeader() {
        bBuf.put(len++, (byte) 0x89);
        bBuf.put(len++, (byte) 0x00); // This is the CFG LEN Position.
    }

    public String toHexString() {

        Formatter hexFormatter = new Formatter();
        for (int i = 0; i < len; i++) {
            hexFormatter.format(" %02X", (0xff & bBuf.get(i)));
        }
        return hexFormatter.out().toString();
    }

    private final long decodeUnsignedInt() {
        long value = bBuf.get() << 24 & 0xFF000000;
        value |= bBuf.get() << 16 & 0xFF0000;
        value |= bBuf.get() << 8 & 0xFF00;
        value |= bBuf.get() & 0xFF;
        return value;
    }

    private String decodeString(int max_len) {
        byte[] tbuf = new byte[max_len + 1];

        // Assume strings are in the buffer as an even number of bytes.
        // The first occurrence of a 0x00 byte ends the string!
        int i;
        for (i = 0; i < max_len; i++) {
            tbuf[i] = bBuf.get();
            if (tbuf[i] == 0x00)
                break;
        }

        if (i % 2 == 0)
            bBuf.get(); // Pop another byte. An even count here indicates an odd
        // number of bytes (0 is the fence post)

        return new String(tbuf, 0, i);
    }

    private void popBytes(int popCnt) {
        byte[] dst = new byte[popCnt];
        bBuf.get(dst, 0, popCnt);
    }

    public GpuffConfig getConfig() {
        return getGpuffConfig();
    }

    // Need to reset the configuration to make sure the next decode does not
    // have clutter in it.
    public void resetConfig() {
        cfg = null;
    }

    public int decodeSerial() {
        int serialNumber = 0;
        if (isGpuff()) {
            serialNumber = bBuf.getInt(11);
        }
        getGpuffConfig().setSerial(serialNumber);
        return serialNumber;
    }

    public void decodeGpuffHeader() {

        resetConfig();

        crcIncluded = (0x80 == (bBuf.get(2) & 0xFF & 0x80));
        needsAck = (0x0040 == ((bBuf.get(FLAG_OFFSET) & 0x00ff) & 0x0040));
        ackResponseBit = (0x04 == (bBuf.get(2) & 0xFF & 0x04));
        ackWithAckReq = (needsAck && ackResponseBit);
        len = (short) (bBuf.get(LEN_MSB) & 0x03);
        len <<= 8;
        len += (short) (bBuf.get(LEN_LSB) & 0x00ff);
        len += 4;

        deviceType = bBuf.getShort(DEVTYPE_OFFSET);
        decodeSerial();

        flag_len = bBuf.getShort(FLAG_OFFSET);
        cid = bBuf.getShort(CID_OFFSET);
        sequence = bBuf.getShort(SEQUENCE_OFFSET);
        deviceRevision = bBuf.get(DEVREV_OFFSET);
    }

    public void decodeCRC() {
        if (this.crcIncluded) {
            bBuf.get();
            bBuf.get();
        }
    }

    public void setConfigDecode(boolean configDecode) {
        this.configDecode = configDecode;
    }

    public boolean isConfigDecode() {
        return configDecode;
    }

    public GpuffConfig getGpuffConfig() {
        if (cfg == null) {
            switch (deviceType) {
            case 0x01:
            case 0x03:
            default:
                cfg = new GpuffConfigGCVT();
                break;
            case 0x02:
                cfg = new GpuffConfigGVAR();
            }
        }

        return cfg;
    }

    public boolean isAckResponseBit() {
        return this.ackResponseBit;
    }

}
