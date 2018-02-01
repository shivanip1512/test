package com.cannontech.amr.errors.dao;

import static com.cannontech.amr.errors.dao.DeviceErrorCategory.BULK_COMMUNICATIONS;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.DATA_VALIDATION;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.NA;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.NETWORK_MANAGER;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.POWERLINE_CARRIER;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.TRANSMITTER;
import static com.cannontech.amr.errors.dao.DeviceErrorCategory.YUKON_SYSTEM;

import java.util.Arrays;
import java.util.Map;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.stream.StreamUtils;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableMap;


public enum DeviceError {
    // NOTE: Remember to add any new error codes to error-code.xml and deviceErrors.xml
    //       New error categories must also be added to DeviceErrorCategory.xml
    
    //PORTER ERRORS
    BAD_MESSAGE_TYPE(BULK_COMMUNICATIONS, 3),
    D_WORD_LENGTH_WRONG(BULK_COMMUNICATIONS, 4),
    PARAMETER_OUT_OF_RANGE(DATA_VALIDATION, 9),
    MISSING_PARAMETER(YUKON_SYSTEM, 10),
    PARITY_ERROR(BULK_COMMUNICATIONS, 15),
    BAD_CCU_SPECIFICATION(BULK_COMMUNICATIONS, 16),
    WORD_1_NACK(POWERLINE_CARRIER, 17),
    WORD_2_NACK(POWERLINE_CARRIER, 18),
    WORD_3_NACK(POWERLINE_CARRIER, 19),
    WORD_1_NACK_PADDED(POWERLINE_CARRIER, 20),
    WORD_2_NACK_PADDED(POWERLINE_CARRIER, 21),
    WORD_3_NACK_PADDED(POWERLINE_CARRIER, 22),
    BAD_PARAMETER(YUKON_SYSTEM, 26),
    TIMEOUT_READING_FROM_PORT(BULK_COMMUNICATIONS, 31),
    SEQUENCE_REJECT(BULK_COMMUNICATIONS, 32),
    FRAMING_ERROR(BULK_COMMUNICATIONS, 33),
    BAD_CRC_ON_MESSAGE(BULK_COMMUNICATIONS, 34),
    BAD_HDLC_UA_FRAME(POWERLINE_CARRIER, 36),
    PORTER_UNKNOWN(YUKON_SYSTEM, 37),
    REQACK_FLAG_SET(BULK_COMMUNICATIONS, 46),
    DEVICE_ID_NOT_FOUND(YUKON_SYSTEM, 54),
    EWORD_RECEIVED(POWERLINE_CARRIER, 57),
    BWORD_RECEIVED(POWERLINE_CARRIER, 58),
    NO_DCD_ON_RETURN_MESSAGE(BULK_COMMUNICATIONS, 65),
    ERROR_READING_FROM_PORT(BULK_COMMUNICATIONS, 67),
    ERROR_WRITING_TO_PORT_1(BULK_COMMUNICATIONS, 68),
    DLC_READ_TIMEOUT(POWERLINE_CARRIER, 72),
    ROUTE_FAILED(POWERLINE_CARRIER, 74),
    INHIBITED_PORT(YUKON_SYSTEM, 83),
    INHIBITED_DEVICE(YUKON_SYSTEM, 85),
    ERROR_DIALING_UP_REMOTE(BULK_COMMUNICATIONS, 87),
    WRONG_UNIQUE_ADDRESS_RECEIVED(POWERLINE_CARRIER, 88),
    ERROR_CONNECTING_TO_TERMINAL_SERVER(BULK_COMMUNICATIONS, 89),
    ERROR_WRITING_TO_TERMINAL_SERVER(BULK_COMMUNICATIONS, 90),
    ERROR_READING_FROM_TERMINAL_SERVER(BULK_COMMUNICATIONS, 91),
    INVALID_ADDRESS(POWERLINE_CARRIER, 92),
    BAD_DATA_BUFFER_FOR_IED(POWERLINE_CARRIER, 93),
    MISSING_CONFIGURATION_ENTRY(YUKON_SYSTEM, 94),
    BAD_NEXUS_SPECIFICATION(YUKON_SYSTEM, 98),
    ERROR_WRITING_TO_PORT_2(YUKON_SYSTEM, 99),
    BAD_BCH(POWERLINE_CARRIER, 100),
    NOMETHOD_OR_INVALID_COMMAND(YUKON_SYSTEM, 202),
    NO_CONFIG_DATA_FOUND(YUKON_SYSTEM, 218),
    CONFIG_NOT_CURRENT(YUKON_SYSTEM, 219),
    CONFIG_CURRENT(YUKON_SYSTEM, 220),
    INHIBITED_CONTROL(YUKON_SYSTEM, 226),
    MACS_TIMED_OUT(YUKON_SYSTEM, 260),
    FAILED_FREEZE_CHECK_FAILED(DATA_VALIDATION, 261),
    INVALID_FROZEN_PEAK_TIMESTAMP(DATA_VALIDATION, 262),
    INVALID_FREEZE_COUNTER(DATA_VALIDATION, 263),
    INVALID_DATA(DATA_VALIDATION, 264),
    NO_RECORD_OF_THE_LAST_FREEZE_SENT(DATA_VALIDATION, 265),
    INVALID_TIME(DATA_VALIDATION, 267),
    INVALID_CHANNEL(DATA_VALIDATION, 268),
    INVALID_SSPEC_REVISION(DATA_VALIDATION, 269),
    REQUIRED_VERIFICATION_OF_SSPEC_REVISION(DATA_VALIDATION, 270),
    INVALID_FUTURE_DATA(DATA_VALIDATION, 271),
    DEVICE_NOT_SUPPORTED_BY_PORTER(YUKON_SYSTEM, 272),
    PORT_IS_NOT_INITIALIZED(BULK_COMMUNICATIONS, 273),
    READ_REQUEST_ALREADY_IN_PROGRESS(DATA_VALIDATION, 274),
    DEVICE_IS_NOT_CONNECTED(BULK_COMMUNICATIONS, 275),
    DISCONNECT_IS_NOT_CONFIGURED(YUKON_SYSTEM, 276),
    TRANSMITTER_IS_OVERHEATING(TRANSMITTER, 277),
    CHANNEL_NEEDS_CONFIGURATION(YUKON_SYSTEM, 278),
    COMMAND_REQUIRES_A_VALID_DATE(YUKON_SYSTEM, 279),
    FAILED_TO_RESOLVE_AN_IP(YUKON_SYSTEM, 280),
    CONFIGURATION_DATA_IS_INVALID(YUKON_SYSTEM, 284),

    // NETWORK MANAGER ERRORS
    UNKNOWN_ADDRESS(NETWORK_MANAGER, 285),
    NETWORK_UNAVAILABLE(NETWORK_MANAGER, 286),
    REQUEST_PACKET_TOO_LARGE(NETWORK_MANAGER, 287),
    PROTOCOL_UNSUPPORTED(NETWORK_MANAGER, 288),
    INVALID_NETWORK_SERVER_ID(NETWORK_MANAGER, 289),
    INVALID_APPLICATION_SERVICE_ID(NETWORK_MANAGER, 290),
    NETWORK_LOAD_CONTROL(NETWORK_MANAGER, 291),
    REQUEST_TIMEOUT(NETWORK_MANAGER, 292),
    UNACKNOWLEDGED(NETWORK_MANAGER, 293),
    REQUEST_PAYLOAD_TOO_LARGE(NETWORK_MANAGER, 294),
    REQUEST_NOT_ACCEPTABLE(NETWORK_MANAGER, 295),
    NETWORK_SERVICE_FAILURE(NETWORK_MANAGER, 297),
    
    NO_NODE(NETWORK_MANAGER, 1024),
    NO_GATEWAY(NETWORK_MANAGER, 1025),
    FAILURE(NETWORK_MANAGER, 1026),
    NM_TIMEOUT(NETWORK_MANAGER, 1027),
    
    FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT(NETWORK_MANAGER, 1028),
    FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT(NETWORK_MANAGER, 1029),
    FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD(NETWORK_MANAGER, 1030),

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

    private final static ImmutableMap<Integer, DeviceError> errors = 
            ImmutableMap.copyOf(
                Arrays.stream(values()).collect(
                    StreamUtils.mapToSelf(DeviceError::getCode)));

    private final int code;
    private final DeviceErrorCategory category;

    private final MessageSourceResolvable porterResolvable = new YukonMessageSourceResolvable(BASE_KEY + getCode() + ".porter");
    private final MessageSourceResolvable descriptionResolvable = new YukonMessageSourceResolvable(BASE_KEY + getCode()
        + ".description");
    private final MessageSourceResolvable troubleshootingResolvable = new YukonMessageSourceResolvable(BASE_KEY + getCode()
        + ".troubleshooting");
    
    public DeviceErrorCategory getCategory() {
        return category;
    }

    public int getCode() {
        return code;
    }
    
    public static DeviceError getErrorByCode(int code){
        DeviceError error = errors.get(code);
        if(error == null){
            error = DeviceError.UNKNOWN;
        }
        return error;
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
    
    public static Map<Integer, DeviceError> getErrorsMap() {
        return errors;
    }
}
