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

import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableMap;


public enum DeviceError {
    // NOTE: Remember to add any new error codes to error-code.xml and deviceErrors.xml
    //       New error categories must also be added to DeviceErrorCategory.java
    
    //PORTER ERRORS
    ABNORMAL_RETURN(YUKON_SYSTEM, 1),
    NO_TRANSMITTER_FOR_ROUTE(YUKON_SYSTEM, 2),
    BAD_MESSAGE_TYPE(BULK_COMMUNICATIONS, 3),
    D_WORD_LENGTH_WRONG(BULK_COMMUNICATIONS, 4),
    BAD_ID_SPECIFICATION(YUKON_SYSTEM, 8),
    PARAMETER_OUT_OF_RANGE(DATA_VALIDATION, 9),
    MISSING_PARAMETER(YUKON_SYSTEM, 10),
    SYNTAX_ERROR(YUKON_SYSTEM, 11),
    BAD_READ(YUKON_SYSTEM, 13),
    BAD_STATE_SPECIFICATION(YUKON_SYSTEM, 14),
    PARITY_ERROR(BULK_COMMUNICATIONS, 15),
    BAD_CCU_SPECIFICATION(BULK_COMMUNICATIONS, 16),
    WORD_1_NACK(POWERLINE_CARRIER, 17),
    WORD_2_NACK(POWERLINE_CARRIER, 18),
    WORD_3_NACK(POWERLINE_CARRIER, 19),
    WORD_1_NACK_PADDED(POWERLINE_CARRIER, 20),
    WORD_2_NACK_PADDED(POWERLINE_CARRIER, 21),
    WORD_3_NACK_PADDED(POWERLINE_CARRIER, 22),
    NO_REQUESTS_FOR_CCU_QUEUE(YUKON_SYSTEM, 24),
    BAD_PARAMETER(YUKON_SYSTEM, 26),
    BAD_ROUTE_SPECIFICATION(YUKON_SYSTEM, 27),
    BAD_BUS_SPECIFICATION(YUKON_SYSTEM, 28),
    READ_ERROR(YUKON_SYSTEM, 30),
    TIMEOUT_READING_FROM_PORT(BULK_COMMUNICATIONS, 31),
    SEQUENCE_REJECT(BULK_COMMUNICATIONS, 32),
    FRAMING_ERROR(BULK_COMMUNICATIONS, 33),
    BAD_CRC_ON_MESSAGE(BULK_COMMUNICATIONS, 34),
    BAD_LENGTH_SPECIFICATION(YUKON_SYSTEM, 35),
    BAD_HDLC_UA_FRAME(POWERLINE_CARRIER, 36),
    UNKNOWN_COMMAND_RECEIVED(POWERLINE_CARRIER, 38),
    REQACK_FLAG_SET(BULK_COMMUNICATIONS, 46),
    ROUTE_NOT_FOUND(YUKON_SYSTEM, 49),
    PORT_REMOTE_NOT_FOUND(YUKON_SYSTEM, 53),
    DEVICE_ID_NOT_FOUND(YUKON_SYSTEM, 54),
    CHILD_DEVICE_UNKNOWN(YUKON_SYSTEM, 55),
    FUNCTION_OR_TYPE_NOT_FOUND(YUKON_SYSTEM, 56),
    EWORD_RECEIVED(POWERLINE_CARRIER, 57),
    BWORD_RECEIVED(POWERLINE_CARRIER, 58),
    OS_OR_SYSTEM_ERROR(YUKON_SYSTEM, 59),
    BAD_PORT_SPECIFICATION(YUKON_SYSTEM, 60),
    ERROR_READING_QUEUE(YUKON_SYSTEM, 61),
    ERROR_WRITING_QUEUE(YUKON_SYSTEM, 62),
    ERROR_ALLOCATING_OR_MANIPULATING_MEMORY(YUKON_SYSTEM, 63),
    NO_DCD_ON_RETURN_MESSAGE(BULK_COMMUNICATIONS, 65),
    ERROR_READING_FROM_PORT(BULK_COMMUNICATIONS, 67),
    ERROR_WRITING_TO_PORT_1(BULK_COMMUNICATIONS, 68),
    ERROR_EXECUTING_CCU_QUEUE_ENTRY(BULK_COMMUNICATIONS, 71),
    DLC_READ_TIMEOUT(POWERLINE_CARRIER, 72),
    NO_ATTEMPT_MADE_ON_CCU_QUEUE_ENTRY(YUKON_SYSTEM, 73),
    ROUTE_FAILED(POWERLINE_CARRIER, 74),
    TRANSPONDER_COMMUNICATION_FAILED(POWERLINE_CARRIER, 75),
    COMM_ATTEMPTED_WITH_INHIBITED_REMOTE(YUKON_SYSTEM, 78),
    ENTRIES_LOST_IN_CCU_FLUSH(YUKON_SYSTEM, 79),
    INHIBITED_PORT(YUKON_SYSTEM, 83),
    DEVICE_DOES_NOT_SUPPORT_ACCUMULATORS(YUKON_SYSTEM, 84),
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
    MEMORY_ERROR(YUKON_SYSTEM, 201),
    NOMETHOD_OR_INVALID_COMMAND(YUKON_SYSTEM, 202),
    NO_GENERAL_SCAN_METHOD(YUKON_SYSTEM, 204),
    NO_INTEGRITY_SCAN_METHOD(YUKON_SYSTEM, 205),
    NO_ACCUM_SCAN_METHOD(YUKON_SYSTEM, 206),
    NO_PROCESS_RESULT_METHOD(YUKON_SYSTEM, 208),
    NO_EXEC_REQ_METHOD(YUKON_SYSTEM, 209),
    NO_RESULT_DECODE_METHOD(YUKON_SYSTEM, 210),
    NO_ERROR_DECODE_METHOD(YUKON_SYSTEM, 211),
    NO_HANDSHAKE_METHOD(YUKON_SYSTEM, 212),
    NO_GENERATE_COMMAND_METHOD(YUKON_SYSTEM, 213),
    NO_DECODE_RESPONSE_METHOD(YUKON_SYSTEM, 214),
    NO_DATA_COPY_METHOD(YUKON_SYSTEM, 215),
    NO_CONFIG_DATA_FOUND(YUKON_SYSTEM, 218),
    CONFIG_NOT_CURRENT(YUKON_SYSTEM, 219),
    CONFIG_CURRENT(YUKON_SYSTEM, 220),
    NO_ROUTE_FOR_GROUP_DEV(YUKON_SYSTEM, 221),
    NO_ROUTES_FOR_MACRO_ROUTE(YUKON_SYSTEM, 222),
    MACRO_OFFSET_NOT_IN_MACRO_ROUTE(YUKON_SYSTEM, 223),
    MACRO_OFFSET_REFERS_TO_MACRO_SUB_ROUTE(YUKON_SYSTEM, 224),
    DEVICE_CONTROL_DISABLED(YUKON_SYSTEM, 225),
    INHIBITED_CONTROL(YUKON_SYSTEM, 226),
    REQUESTED_OPERATION_EXPIRED(YUKON_SYSTEM, 228),
    NO_POINT_CONTROL_CONFIG(YUKON_SYSTEM, 229),
    RETRY_RESUBMITTED(YUKON_SYSTEM, 234),
    SCANNED_DEVICE_INHIBITED(YUKON_SYSTEM, 237),
    ILLEGAL_GLOBAL_DEVICE_SCAN(YUKON_SYSTEM, 238),
    DEVICE_WINDOW_CLOSED(YUKON_SYSTEM, 239),
    DIALUP_FAILED_PORT_ERROR(BULK_COMMUNICATIONS, 241),
    DIALUP_FAILED_DEVICE_ERROR(YUKON_SYSTEM, 242),
    PORT_SIMULATED_NO_DATA_AVAILABLE(POWERLINE_CARRIER, 243),
    PORT_ECHOED_BYTES(BULK_COMMUNICATIONS, 244),
    BAD_PAGER_ID_OR_PASSWORD(YUKON_SYSTEM, 245),
    RETRIES_EXHAUSTED(YUKON_SYSTEM, 246),
    NO_RESPONSE_FROM_TAP(YUKON_SYSTEM, 247),
    INVALID_OR_INCOMPLETE_REQUEST(YUKON_SYSTEM, 248),
    INVALID_OR_UNSUCCESSFUL_HTTP_RESPONSE(YUKON_SYSTEM, 250),
    XML_PARSER_INIT_FAILED(YUKON_SYSTEM, 251),
    INVALID_WCTP_RESPONSE(YUKON_SYSTEM, 252),
    WCTP_TIMEOUT(YUKON_SYSTEM, 253),
    PROTOCOL_ERROR_300_SERIES(YUKON_SYSTEM, 254),
    PROTOCOL_ERROR_400_SERIES(YUKON_SYSTEM, 255),
    PROTOCOL_ERROR_500_SERIES(YUKON_SYSTEM, 256),
    PROTOCOL_ERROR_600_SERIES(YUKON_SYSTEM, 257),
    QUEUE_PURGED(YUKON_SYSTEM, 258),
    MACS_TIMED_OUT(YUKON_SYSTEM, 260),
    FAILED_FREEZE_CHECK_FAILED(DATA_VALIDATION, 261),
    INVALID_FROZEN_PEAK_TIMESTAMP(DATA_VALIDATION, 262),
    INVALID_FREEZE_COUNTER(DATA_VALIDATION, 263),
    INVALID_DATA(DATA_VALIDATION, 264),
    NO_RECORD_OF_THE_LAST_FREEZE_SENT(DATA_VALIDATION, 265),
    REQUEST_CANCELLED(DATA_VALIDATION, 266),
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
    DEVICE_HAS_NO_POINTS(NETWORK_MANAGER, 296),
    NETWORK_SERVICE_FAILURE(NETWORK_MANAGER, 297),
    CHANNEL_NOT_ENABLED(NETWORK_MANAGER, 298),
    CHANNEL_NOT_SUPPORTED(NETWORK_MANAGER, 299),
    INVALID_WATER_NODE_DEVICE(NETWORK_MANAGER, 300),
    UNKNOWN_WATER_NODE(NETWORK_MANAGER, 301),
    UNKNOWN_GATEWAY(NETWORK_MANAGER, 302),
    UNSPECIFIED_WATER_NODE_FAILURE(NETWORK_MANAGER, 303),
    FUNCTION_CODE_NOT_SUPPORTED(NETWORK_MANAGER, 304),
    UNKNOWN_OBJECT(NETWORK_MANAGER, 305),
    PARAMETER_ERROR(NETWORK_MANAGER, 306),
    OPERATION_ALREADY_EXECUTING(NETWORK_MANAGER, 307),
    RESPONSE_DID_NOT_INCLUDE_ENTRY_FOR_COMMAND(NETWORK_MANAGER, 308),
    BAD_REQUEST(NETWORK_MANAGER, 309),
    COMMAND_FAILED(NETWORK_MANAGER, 310),
    E2E_ERROR_UNMAPPED(NETWORK_MANAGER, 311),
    
    // C&I METER PROGRAMMING ERRORS
    METER_BRICKED(NETWORK_MANAGER, 312),
    REASON_UNKNOWN(NETWORK_MANAGER, 313),
    SERVICE_UNSUPPORTED(NETWORK_MANAGER, 314),
    INSUFFICIENT_SECURITY_CLEARANCE(NETWORK_MANAGER, 315),
    OPERATION_NOT_POSSIBLE(NETWORK_MANAGER, 316),
    INAPPROPRIATE_ACTION_REQUESTED(NETWORK_MANAGER, 317),
    DEVICE_BUSY(NETWORK_MANAGER, 318),
    DATA_NOT_READY(NETWORK_MANAGER, 319),
    DATA_LOCKED(NETWORK_MANAGER, 320),
    RENEGOTIATE_REQUEST(NETWORK_MANAGER, 321),
    INVALID_SERVICE_SEQUENCE(NETWORK_MANAGER, 322),
    DOWNLOAD_ABORTED(NETWORK_MANAGER, 323),
    FILE_TOO_LARGE(NETWORK_MANAGER, 324),
    CONFIGURATION_IN_PROGRESS(NETWORK_MANAGER, 325),
    UNABLE_TO_GET_FILE(NETWORK_MANAGER, 326),
    INSUFFICIENT_METER_VERSION(NETWORK_MANAGER, 327),
    FILE_EXPIRED(NETWORK_MANAGER, 328),
    FAILED_REQUIREMENTS(NETWORK_MANAGER, 329),
    MALFORMED_CONFIG_FILE_RECORD(NETWORK_MANAGER, 330),
    VERIFICATION_FAILED(NETWORK_MANAGER, 331),
    WRITE_KEY_FAILED(NETWORK_MANAGER, 332),
    CATASTROPHIC_FAILURE(NETWORK_MANAGER, 333),
    NO_METER_PROGRAM_ASSIGNED(YUKON_SYSTEM, 334),

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
    SUCCESS(NA, 0),
    
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
        return new YukonMessageSourceResolvable(BASE_KEY + getCode() + ".porter");
    }
    
    public MessageSourceResolvable getDescriptionResolvable() {
        return new YukonMessageSourceResolvable(BASE_KEY + getCode() + ".description");
    }
    
    public MessageSourceResolvable getTroubleshootingResolvable() {
        return new YukonMessageSourceResolvable(BASE_KEY + getCode() + ".troubleshooting");
    }
    
    public static Map<Integer, DeviceError> getErrorsMap() {
        return errors;
    }

    // Eventually, RfnMeterReadingReplyType could be updated to include the errorCode (requires NM/Yukon changes)
    public static DeviceError of(RfnMeterReadingReplyType type) {
        switch (type) {
        case OK:
            return SUCCESS;
        case NO_NODE:
            return NO_NODE;
        case NO_GATEWAY:
            return NO_GATEWAY;
        case TIMEOUT:   // Yukon specific timeout
            return TIMEOUT;
        case FAILURE:
        default:
            return FAILURE;
        }
    }
    
    // Eventually, RfnMeterReadingDataReplyType could be updated to include the errorCode (requires NM/Yukon changes)
    public static DeviceError of(RfnMeterReadingDataReplyType type) {
        switch (type) {
        case OK:
            return SUCCESS;
        case NETWORK_TIMEOUT:
            return NM_TIMEOUT;
        case TIMEOUT:   // Yukon specific timeout
            return TIMEOUT;
        case FAILURE:
        default:
            return FAILURE;
        }
    }
}
