package com.cannontech.amr.errors.dao;

import static com.cannontech.amr.errors.dao.DeviceErrorCategory.BULK_COMMUNICATIONS;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.DATA_VALIDATION;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.NA;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.NETWORK_MANAGER;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.POWERLINE_CARRIER;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.TRANSMITTER;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.YUKON_SYSTEM;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;


public enum DeviceError {
    //PORTOR ERRORS
    BAD_MESSAGE_TYPE(BULK_COMMUNICATIONS, 3),
    D_WORD_LENTH_WRONG(BULK_COMMUNICATIONS, 4),
    PARAMETER_OUT_OF_RANGE(DATA_VALIDATION, 9),
    PARITY_ERROR(BULK_COMMUNICATIONS, 15),
    BAD_CCU_SPECIFICATION(BULK_COMMUNICATIONS, 16),
    WORD_1_NACK(POWERLINE_CARRIER, 17),
    WORD_2_NACK(POWERLINE_CARRIER, 18),
    WORD_3_NACK(POWERLINE_CARRIER, 19),
    WORD_1_NACK_PADDED(POWERLINE_CARRIER, 20),
    WORD_2_NACK_PADDED(POWERLINE_CARRIER, 21),
    WORD_3_NACK_PADDED(POWERLINE_CARRIER, 22),
    TIMEOUT_READING_FROM_PORT(BULK_COMMUNICATIONS, 31),
    SEQUENCE_REJECT(BULK_COMMUNICATIONS, 32),
    FRAMING_ERROR(BULK_COMMUNICATIONS, 33),
    BAD_CRC_ON_MESSAGE(BULK_COMMUNICATIONS, 34),
    BAD_HDLC_UA_FRAME(POWERLINE_CARRIER, 36),
    REQACK_FLAG_SET(BULK_COMMUNICATIONS, 46),
    DEVICE_ID_NOT_FOUND(YUKON_SYSTEM, 54),
    EWORD_RECEIVED(POWERLINE_CARRIER, 57),
    NO_DCD_ON_RETURN_MESSAGE(BULK_COMMUNICATIONS, 65),
    ERROR_READING_FROM_PORT(BULK_COMMUNICATIONS, 67),
    ERROR_WRITING_TO_PORT_1(BULK_COMMUNICATIONS, 68),
    DLC_READ_TIMEOUT(POWERLINE_CARRIER, 72),
    ROUTE_FAILED(POWERLINE_CARRIER, 74),
    INHIBITED_PORT(YUKON_SYSTEM, 83),
    INHIBITED_DEVICE(YUKON_SYSTEM, 85),
    ERROR_DIALING_UP_REMOTE(BULK_COMMUNICATIONS, 87),
    WRONG_UNIQUE_ADDRESS_RECEIVED(POWERLINE_CARRIER, 88),
    ERROR_CONNECTING_TO_TERMINAL_SERVER(POWERLINE_CARRIER, 89),
    ERROR_WRITING_TO_TERMINAL_SERVER(BULK_COMMUNICATIONS, 90),
    ERROR_READING_FROM_TERMINAL_SERVER(BULK_COMMUNICATIONS, 91),
    INVALID_ADDRESS(POWERLINE_CARRIER, 92),
    BAD_DATA_BUFFER_FOR_IED(POWERLINE_CARRIER, 93),
    MISSING_CONFIGURATION_ENTRY(YUKON_SYSTEM, 94),
    BAD_NEXUS_SPECIFICATION(YUKON_SYSTEM, 98),
    ERROR_WRITING_TO_PORT_2(YUKON_SYSTEM, 99),
    BAD_BCH(POWERLINE_CARRIER, 100),
    NOMETHOD_OR_INVALID_COMMAND(YUKON_SYSTEM, 202),
    INHIBITED_CONTROL(YUKON_SYSTEM, 226),
    FAILED_FREEZE_CHECK_FAILED(DATA_VALIDATION, 261),
    INVALID_FROZEN_PEAK_TIMESTAMP(DATA_VALIDATION, 262),
    INVALID_FREEZE_COUNTER(DATA_VALIDATION, 263),
    INVALID_DATA(DATA_VALIDATION, 264),
    NO_RECORD_OF_THE_LAST_FREEZE_SENT(DATA_VALIDATION, 265),
    INVALID_TIME(DATA_VALIDATION, 267),
    INVALID_CHANNEL(DATA_VALIDATION, 268),
    INVALID_SSPEC_REVISION(DATA_VALIDATION, 269),
    REQUIRED_VERIFICATION_OF_SSPEC_REVISION(DATA_VALIDATION, 270),
    TRANSMITTER_IS_BUSY(TRANSMITTER, 271),
    PORT_IS_NOT_INITIALIZED(BULK_COMMUNICATIONS, 273),
    READ_REQUEST_ALREADY_IN_PROGRESS(DATA_VALIDATION, 274),
    DEVICE_IS_NOT_CONNECTED(BULK_COMMUNICATIONS, 275),
    DISCONNECT_IS_NOT_CONFIGURED(YUKON_SYSTEM, 276),
    TRANSMITTER_IS_OVERHEATING(TRANSMITTER, 277),
    CHANNEL_NEEDS_CONFIGURATION(YUKON_SYSTEM, 278),
    COMMAND_REQUIRES_A_VALID_DATE(YUKON_SYSTEM, 279),
    FAILED_TO_RESOLVE_AN_IP(YUKON_SYSTEM, 280),

    // NETWORK MANAGER ERRORS
    NO_NODE(NETWORK_MANAGER, 1024),
    NO_GATEWAY(NETWORK_MANAGER, 1025),
    FAILURE(NETWORK_MANAGER, 1026),
    NM_TIMEOUT(NETWORK_MANAGER, 1027),

    // COMMON ERRORS
    INVALID_ACTION(YUKON_SYSTEM, 2000), 
    TIMEOUT(YUKON_SYSTEM, 227),
    NO_POINT(YUKON_SYSTEM, 281),
    NO_TIMESTAMP(YUKON_SYSTEM, 282),
    TIMESTAMP_OUT_OF_RANGE(YUKON_SYSTEM, 283),
    
    // SUCCESS
    SUCCESSFUL_READ(NA, 0),
    
    // replaces * 
    UNKNOWN(NA, -1);
    
    DeviceError(DeviceErrorCategory category, int code) {
        this.category = category;
        this.code = code;
    }
    
    private final String BASE_KEY = "yukon.web.error.code.";

    private static final Map<Integer, DeviceError> errors = Collections.unmodifiableMap(initializeMapping()); 
    private final int code;
    private final DeviceErrorCategory category;

    private final MessageSourceResolvable porterResolvable = new YukonMessageSourceResolvable(BASE_KEY + getCode() + ".porter");
    private final MessageSourceResolvable descriptionResolvable = new YukonMessageSourceResolvable(BASE_KEY + getCode()
        + ".description");
    private final MessageSourceResolvable troubleshootingResolvable = new YukonMessageSourceResolvable(BASE_KEY + getCode()
        + ".troubleshooting");
    
    private static Map<? extends Integer, ? extends DeviceError> initializeMapping() {
        Map<Integer, DeviceError> map = new HashMap<Integer, DeviceError>();
        for (DeviceError error : DeviceError.values()) {
            map.put(error.getCode(), error);
        }
        return map;
    }
    public DeviceErrorCategory getCategory() {
        return category;
    }

    public int getCode() {
        return code;
    }
    
    public static DeviceError getErrorByCode(int code){
        return errors.get(code);
    }
    
    public MessageSourceResolvable getPorterResolvable() {
        return porterResolvable;
    }
    
    public MessageSourceResolvable getDescriptionResolvable() {
        return descriptionResolvable;
    }
    
    public MessageSourceResolvable getTroubleshootingResolvable() {
        return troubleshootingResolvable;
    }
}
