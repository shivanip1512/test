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

    private GpuffConfig cfg = new GpuffConfig();
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
    private boolean ackWithAckReq;
    private boolean needsAck;
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
        ackWithAckReq = false;
        needsAck = false;
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
        bBuf.put(SERIAL_OFFSET, (byte) ((0xff000000 & cfg.getSerial()) >> 24));
        bBuf.put(SERIAL_OFFSET + 1, (byte) ((0x00ff0000 & cfg.getSerial()) >> 16));
        bBuf.put(SERIAL_OFFSET + 2, (byte) ((0x0000ff00 & cfg.getSerial()) >> 8));
        bBuf.put(SERIAL_OFFSET + 3, (byte) (0x000000ff & cfg.getSerial()));
        
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
        this.cfg.setSerial(serialNum);
    }

    @SuppressWarnings("unused")
    private void put(long val) {
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
        return cfg.getSerial();
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
            String tStr;
            decodeGpuffHeader();
            
            if(!isGpuffACKwithACKRequest() ) {
                
                log.info("Decoding for device: " + getSerialNumber());
                boolean fcnDecodeError = false;
                bBuf.position(FIRST_FCN); // This is the location of the first FCN
                while (!fcnDecodeError && bBuf.position() < len) {
                    byte fcn = bBuf.get(); // Gets a byte and increments position
                    
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
                            if ((dvFlags & 0x20) == 0x20) {
                                boolean tso = false;
                                if ((dvFlags & 0x80) == 0x80)
                                    tso = true;
                                // Time included
                                time = bBuf.getInt();
                                
                                Date t;
                                if (tso) {
                                    t = new Date();
                                    t.setTime(t.getTime() - time);
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
                            
                            if ((dvStatus & 0x80) == 0x80) { // No Fault/Fault
                                fault = true;
                            }
                            if ((dvStatus & 0x40) == 0x40) {
                                event = true;
                            }
                            if ((dvStatus & 0x20) == 0x20) { // Power/No Power
                                power = false;
                            }
                            
                            report += " Fault = " + fault + " Event = " + event + " Current Good = " + power;
                            
                            log.info(report);
                            break;
                        case 0x08:
                            configDecode = true;
                            int cfg_fcn_len = bBuf.get() & 0x00ff;
                            boolean ieDecodeError = false;
                            boolean ipDecode0 = false;
                            while (!ieDecodeError && bBuf.position() < cfg_fcn_len) {
                                byte ie_type = bBuf.get();
                                int ie_len = bBuf.get() & 0x00ff;
                                
                                switch (ie_type) {
                                case 0x01:
                                    int periodic = bBuf.get() & 0x00ff;
                                    cfg.setPeriodic(periodic != 0);
                                    bBuf.get();
                                    tStr = decodeString(64);
                                    log.info("Periodic             " + cfg.isPeriodic());
                                    log.info("Configured to APN    " + tStr);
                                    cfg.setApn(tStr);
                                    break;
                                case 0x02:
                                    bBuf.get();
                                    bBuf.get();
                                    String portStr = decodeString(6); // Port
                                    String IpStr = decodeString(64);
                                    
                                    if (!ipDecode0) {
                                        cfg.setIp0(IpStr);
                                        cfg.setPort0(portStr);
                                        ipDecode0 = true; // Decode them in order.
                                        log.info("Configured to URL[0] " + IpStr + ":" + portStr);
                                    } else {
                                        cfg.setIp1(IpStr);
                                        cfg.setPort1(portStr);
                                        log.info("Configured to URL[1] " + IpStr + ":" + portStr);
                                    }
                                    break;
                                case (byte) 0x85:
                                    popBytes(64); // Ignore the debug buffer.
                                bBuf.get(); // 0xaa
                                bBuf.get(); // 0x55
                                int fwMajVer = bBuf.get() & 0x00ff;
                                int fwMinVer = bBuf.get() & 0x00ff;
                                short resetCnt = bBuf.getShort();
                                short SPIErrors = bBuf.getShort();
                                int momCnt = bBuf.get() & 0x00ff;
                                int fltCnt = bBuf.get() & 0x00ff;
                                int aClrCnt = bBuf.get() & 0x00ff;
                                int pwrLossCnt = bBuf.get() & 0x00ff;
                                int resMomCnt = bBuf.get() & 0x00ff;
                                bBuf.get(); // Pull one more to make the package
                                // an even number of bytes.
                                
                                log.info("Diagnostic message received. Length: " + ie_len + ".");
                                log.info("Diagnostic - Firmware Version " + fwMajVer + "." + fwMinVer);
                                log.info("Diagnostic - Reset Count      " + resetCnt);
                                log.info("Diagnostic - SPI Errors       " + SPIErrors);
                                log.info("Diagnostic - Fault Count      " + fltCnt);
                                log.info("Diagnostic - Momentary Cnt    " + momCnt);
                                log.info("Diagnostic - Reset Mmtary Ct  " + resMomCnt);
                                log.info("Diagnostic - All Clear Count  " + aClrCnt);
                                log.info("Diagnostic - Power Loss Count " + pwrLossCnt);
                                
                                break;
                                default:
                                    log.info("Unknown Information Element Type - Update the decode");
                                ieDecodeError = true;
                                break;
                                }
                            }
                            break;
                        default:
                            log.info("Error decoding FCN " + fcn + " at buffer position " + bBuf.position());
                        fcnDecodeError = true;
                        break;
                        }
                        break;
                    case 0x02: // VAR Advisor.
                        log.info("Error decoding VAR Advisor FCN " + fcn + " at buffer position " + bBuf.position());
                        fcnDecodeError = true;
                        break;
                    }
                }
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
        appendFNCConfigHeader(gpc.getUser(), gpc.getPassword());
        appendAPNConfig(gpc.getUser(), gpc.getPassword(), gpc.getApn(), gpc.isPeriodic());
        appendIPConfig(gpc.getIp0(), gpc.getPort0());
        appendIPConfig(gpc.getIp1(), gpc.getPort1());

        // Align the Configuration FCN as a whole (should be redundant since
        // each piece is aligned, but ...
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

        for (int i = 0; i < 16; i++) {
            if (user != null && i < user.length()) {
                bBuf.put(len++, (byte) (0x00ff & user.charAt(i))); // User.
            } else {
                bBuf.put(len++, (byte) 0x00); // User.
            }
        }
        for (int i = 0; i < 8; i++) {
            if (pw != null && i < pw.length()) {
                bBuf.put(len++, (byte) (0x00ff & pw.charAt(i))); // PW.
            } else {
                bBuf.put(len++, (byte) 0x00); // PW.
            }
        }
    }

    public void appendAPNConfig(String user, String pw, String apn,
            boolean periodicReport) {
        bBuf.put(len++, (byte) 0x81); // APN Config
        int apn_pos = len;
        bBuf.put(len++, (byte) 0x00); // This is the CFG LEN Position.

        for (int i = 0; i < 16; i++) {
            if (user != null && i < user.length()) {
                bBuf.put(len++, (byte) (0x00ff & user.charAt(i))); // User.
            } else {
                bBuf.put(len++, (byte) 0x00); // User.
            }
        }
        for (int i = 0; i < 8; i++) {
            if (pw != null && i < pw.length()) {
                bBuf.put(len++, (byte) (0x00ff & pw.charAt(i))); // PW.
            } else {
                bBuf.put(len++, (byte) 0x00); // PW.
            }
        }
        bBuf.put(len++, (byte) (periodicReport ? 0x01 : 0x00)); // Periodic
                                                                // reports if
                                                                // non-zero
        bBuf.put(len++, (byte) 0x00); // Reserved
        insertConfigString(apn);
        twoByteAlign(apn_pos);

    }

    public void appendIPConfig(String ip_str, String ip_port) {
        bBuf.put(len++, (byte) 0x82); // IP Config Number
        int ip_pos = len;
        bBuf.put(len++, (byte) 0x00); // This is the LEN Position.
        bBuf.put(len++, (byte) 0x00); // Reserved
        bBuf.put(len++, (byte) 0x00); // Reserved
        insertConfigString(ip_port);
        insertConfigString(ip_str);
        twoByteAlign(ip_pos);
    }

    public String toHexString() {

        Formatter hexFormatter = new Formatter();
        for (int i = 0; i < len; i++) {
            hexFormatter.format(" %02X", (0xff & bBuf.get(i)));
        }
        return hexFormatter.out().toString();
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
        return cfg;
    }

    public void setConfig(GpuffConfig cfg) {
        this.cfg = cfg;
    }

    public int decodeSerial() {
        int serialNumber = 0;
        if (isGpuff()) {
            serialNumber = bBuf.getInt(11);
        }
        cfg.setSerial(serialNumber);
        return serialNumber;
    }

    public void decodeGpuffHeader() {

        ackWithAckReq = (0x0044 == ((bBuf.get(FLAG_OFFSET) & 0x00ff) & 0x0044));
        needsAck = (0x0040 == ((bBuf.get(FLAG_OFFSET) & 0x00ff) & 0x0040));
        len = (short) (bBuf.get(LEN_MSB) & 0x03);
        len <<= 8;
        len += (short) (bBuf.get(LEN_LSB) & 0x00ff);
        len += 4;
        decodeSerial();

        flag_len = bBuf.getShort(FLAG_OFFSET);
        cid = bBuf.getShort(CID_OFFSET);
        sequence = bBuf.getShort(SEQUENCE_OFFSET);
        deviceType = bBuf.getShort(DEVTYPE_OFFSET);
        deviceRevision = bBuf.get(DEVREV_OFFSET);
    }

    public void setConfigDecode(boolean configDecode) {
        this.configDecode = configDecode;
    }

    public boolean isConfigDecode() {
        return configDecode;
    }
}
