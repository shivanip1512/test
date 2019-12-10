#pragma once

#include "types.h"

#include "ctidbgmem.h"      // defines CTIDBG_new for memory tracking!

// YUKONEOT is needed to make ALL timezones operate correctly wrt the "end-of-time"
//Changed YUKONEOT to LONG_MAX from ULONG to account for CtiTime not being unsigned   -TS
#define YUKONEOT (LONG_MAX - 86400 * 2)
#define PASTDATE 402296400

#define PIL_REGISTRATION_NAME        "PIL"
#define SCANNER_REGISTRATION_NAME    "SCANNER"
#define PORTER_REGISTRATION_NAME     "PORTER"

// Load Management Active Restore Value
#define LMAR_NEWCONTROL         "N"             // This is the first entry for any new control.
#define LMAR_LOGTIMER           "L"             // This is a timed log entry.  Nothing exciting happened in this interval.
#define LMAR_CONT_CONTROL       "C"             // Previous command was repeated extending the current control interval.

#define LMAR_TIMED_RESTORE      "T"             // Control terminated based on time set in load group.
#define LMAR_MANUAL_RESTORE     "M"             // Control terminated because of an active restore or terminate command being sent.
#define LMAR_OVERRIDE_CONTROL   "O"             // Control terminated because a new command of a different nature was sent to this group.
#define LMAR_CONTROLACCT_ADJUST "A"             // Control accounting was adjusted by user.
#define LMAR_PERIOD_TRANSITION  "P"             // Control was active as we crossed a control history boundary.  This log denotes the last log in the previos interval.
#define LMAR_DISPATCH_SHUTDOWN  "S"             // Control was active as dispatch shutdown.  This entry will be used to resume control.


#define RESTORE_DURATION -1


enum CtiPaoCategory_t
{
    PAO_CATEGORY_DEVICE = 0,
    PAO_CATEGORY_PORT,
    PAO_CATEGORY_ROUTE,
    PAO_CATEGORY_LOAD_MANAGEMENT,
    PAO_CATEGORY_CAP_CONTROL,
};


/*
 *   !!!! WARNING !!!!  This enum directly affects tbl_dv_scandata.cpp/h
 */
enum CtiScanRate_t
{
    ScanRateGeneral = 0,
    ScanRateAccum,
    ScanRateStatus,
    ScanRateIntegrity,
    ScanRateLoadProfile,
    ScanRateInvalid
};

enum CtiDeviceWindow_t
{
    DeviceWindowScan = 0,
    DeviceWindowPeak,
    DeviceWindowAlternateRate,
    DeviceWindowSignaledAlternateRate,
    DeviceWindowInvalid
};


enum CtiPAOClass_t
{
    PAOClassPort,
    PAOClassRoute,
    PAOClassTransmitter,
    PAOClassRTU,
    PAOClassIED,
    PAOClassMeter,
    PAOClassRFMesh,
    PAOClassGridAdvisor,
    PAOClassCarrier,
    PAOClassGroup,
    PAOClassCapControl,
    PAOClassVirtual,
    PAOClassLoadManagement,
    PAOClassSystem,
    PAOClassMACS,

    PAOClassInvalid
};

enum CtiStatisticsType_t
{
    StatTypeHourly = 0,
    StatType24Hour,
    StatTypeMonthly,

    StatTypeInvalid
};


// Alarm Classes
enum CtiSignalClass_t
{
    SignalEvent = 1,       // Event - Not propagated to clients
    SignalAlarm0,           // Alarms span 2 to 255

    SignalInvalid = 256
};

enum CtiLogTypeClass_t
{
    GeneralLogType = 1,
    PtChangeLogType,
    LoadMgmtLogType,
    UserLogType,
    PILLogType,


    MACSLogType = 7,
    AlarmCategoryLogType = 8,
    MultispeakLogType = 9,
    CapControlLogType = 10,

    InvalidLogType
};


// Alarms
enum CtiAlarm_t
{
    NoAlarm = 0,
    NAKAlarm,       // Alarmed and Unacknowledged alarm (Not AcKnowledged)
    ACKAlarm,       // Alarmed and ACKnowledged

    InvalidAlarm
};

struct ClientErrors
{
    enum YukonErrors
    {
        None = 0,
        Abnormal,
        NoTransmitterForRoute,
        BadWordType,
        DLength,
        z_005,  //  all z_ codes are unused and may be repurposed
        z_006,
        z_007,
        BadId,
        BadRange,
        MissingParameter,
        Syntax,
        z_012,
        BadRead,
        BadState,
        BadParity,
        BadCcu,
        Word1Nack,
        Word2Nack,
        Word3Nack,
        Word1NackPadded,
        Word2NackPadded,
        Word3NackPadded,
        z_023,
        NoRequestsForCcu,
        z_025,
        BadParameter,
        BadRoute,
        BadBusSpecification,
        z_029,
        Read,
        ReadTimeout,
        BadSequence,
        Framing,
        BadCrc,
        BadLength,
        BadHdlcUaFrame,
        z_037,
        UnknownCommandReceived,
        z_039,
        z_040,
        z_041,
        z_042,
        z_043,
        z_044,
        z_045,
        ReqackFlagSet,
        z_047,
        z_048,
        RouteNotFound,
        z_050,
        z_051,
        z_052,
        PortRemoteNotFound,
        IdNotFound,
        ChildDeviceUnknown,
        TypeNotFound,
        EWordReceived,
        BWordReceived,
        SystemRelated,
        BadPort,
        QueueRead,
        QueueWrite,
        MemoryAccess,
        z_064,
        NoDcd,
        z_066,
        PortRead,
        PortWrite,
        z_069,
        z_070,
        QueueExec,
        DlcTimeout,
        NoAttempt,
        RouteFailed,
        TransmitterFailed,
        z_076,
        z_077,
        RemoteInhibited,
        CcuQueueFlushed,
        z_080,
        z_081,
        z_082,
        PortInhibited,
        AccumulatorsNotSupported,
        DeviceInhibited,
        z_086,
        DialupFailed,
        WrongAddress,
        TcpConnect,
        TcpWrite,
        TcpRead,
        Address,
        IedBufferBad,
        MissingConfig,
        z_095,
        z_096,
        z_097,
        BadSocket,
        SocketWrite,
        BadBch,

        Memory = 201,

        // Device Errors
        NoMethod,
        z_203,
        NoMethodForGeneralScan,
        NoMethodForIntegrityScan,
        NoMethodForAccumulatorScan,
        NoMethodForLoadProfileScan,
        NoMethodForProcessResult,
        NoMethodForExecuteRequest,
        NoMethodForResultDecode,
        NoMethodForErrorDecode,
        NoMethodForHandshake,
        NoMethodForGenerateCmd,
        NoMethodForDecodeResponse,
        NoMethodForDataCopy,
        GeneralScanAborted,
        z_217,

        NoConfigData,
        ConfigNotCurrent,
        ConfigCurrent,  //  this should not be an error code

        // Control Command Info
        NoRouteGroupDevice,         // No route defined in a group device
        NoRoutesInMacro,            // Route Macro contained no sub-routes.
        RouteOffsetOutOfRange,
        SubRouteIsMacro,
        ControlInhibitedOnDevice,
        ControlInhibitedOnPoint,
        Timeout,
        RequestExpired,
        NoPointControlConfiguration,
        z_230,
        z_231,
        z_232,
        z_233,

        // These three are internal Porter codes
        //   and should not be represented here
        RetrySubmitted,              // The OutMessage in question has been requeued onto the portqueue.
        QueuedToDevice,             // The OutMessage in question has been enqueud onto a device queue.
        ContinueLoop,                // The propagate a continue to the looping construct above.

        ScanDeviceInhibited,
        ScanGlobalAddress,
        ScanWindowClosed,

        // Port and dialup errors
        z_240,
        DialupConnectPort,        // Error making connection.. Assumed to be the port's fault
        DialupConnectDevice,      // Error making connection.. Assumed to be the device's fault
        PortSimulated,                 // The port is being simulated - there will be no inbound
        PortEchoedResponse,              // The port returned the exact bytes it was given - probably a hardware failure somewhere

        // Paging errors
        PageRS,                  // Invalid transaction, typ. bad pager id or password
        PageNAK,                 // TAP Repeat Requested, but retries exhausted
        PageNoResponse,

        InvalidRequest,
        z_249,

        // WCTP errors
        WctpHttpResponse,      // Invalid or unsuccessful HTTP response
        WctpXmlParser,         // XML parser initialization failed
        WctpResponse,          // Invalid WCTP response format
        WctpTimeout,           // Time out when receiving WCTP response
        Wctp300Series,         // Protocol Error 300 Series.
        Wctp400Series,
        Wctp500Series,
        Wctp600Series,

        QueuePurged,           // Queue purged to clean memory.

        z_259,

        MacsTimeout,

        InvalidFrozenReadingParity,    //  MCT freeze-related errors
        InvalidFrozenPeakTimestamp,
        InvalidFreezeCounter,

        InvalidData,           //  If any MCT reading (that will be stored to a point) is invalid, we fail the read

        FreezeNotRecorded,
        RequestCancelled,      // Cancel message received

        InvalidTimestamp,
        InvalidChannel,

        InvalidSSPEC,          // Hardware has an insufficient SSPEC/firmware revision
        VerifySSPEC,           // Need to verify SSPEC/firmware revision

        InvalidFutureData,
        UnsupportedDevice,

        PortNotInitialized,

        CommandAlreadyInProgress,

        DeviceNotConnected,

        NoDisconnect,

        TransmitterOverheating,

        NeedsChannelConfig,

        InvalidDate,

        DnsLookupFailed,

        PointLookupFailed,

        NeedsDateRangeReset,

        DataMissing,

        InvalidConfigData,

        //  Errors relating to RFN E2EDT
        //  http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/System/10446%20E2E%20Data%20Transfer%20System%20Specification_R1.2.docx
        //  see sections 1.3.3, E2E-Data.confirm, 1.3.3.4, Result Codes
        E2eUnknownAddress,
        E2eNetworkUnavailable,
        E2eRequestPacketTooLarge,
        E2eProtocolUnsupported,
        E2eInvalidNetworkServerId,
        E2eInvalidApplicationServiceId,
        E2eNetworkLoadControl,
        E2eRequestTimeout,
        NetworkManagerTimeout,
        E2eRequestPayloadTooLarge,
        E2eRequestNotAcceptable,

        NoPointsOnDevice,

        E2eNetworkServiceFailure,

        ChannelDisabled,
        ChannelUnsupported,

        // Battery node errors
        InvalidBatteryNode,
        UnknownBatteryNode,
        UnknownGateway,
        BatteryNodeFailure,

        // DNP IIN bit errors
        FunctionCodeNotImplemented,
        UnknownObject,
        ParameterError,
        OperationAlreadyExecuting,

        NoAggregateResponseEntry,

        E2eBadRequest,
        E2eCommandFailed,
        E2eErrorUnmapped,

        // Meter Programming related error codes
        MeterBricked,
        ReasonUnknown,
        ServiceUnsupported,
        InsufficientSecurityClearance,
        OperationNotPossible,
        InappropriateActionRequested,
        DeviceBusy,
        DataNotReady,
        DataLocked,
        RenegotiateRequest,
        InvalidServiceSequence,
        DownloadAborted,
        FileTooLarge,
        ConfigurationInProgress,
        UnableToGETFile,
        InsufficientMeterVersion,
        FileExpired,
        FailedRequirements,
        MalformedConfigFileRecord,
        VerificationFailed,
        WriteKeyFailed,
        CatastrophicFailure,
        NoMeterProgramAssigned
    };
};

typedef ClientErrors::YukonErrors YukonError_t;


enum CtiProtocolWrap_t
{
    ProtocolWrapNone = 0,   // NOWRAP
    ProtocolWrapIDLC,       // IDLCWRAP,

    InvalidProtocol
};


enum CtiPortDelay_t
{
    PREKEY_DELAY  = 0,  //  The prekey delay is from end of last complete sequence till start of next sequence
    PRE_RTS_DELAY = 0,

    COLDKEYTODATA_DELAY   = 1,  //  The cold repeater key to data delay is used when the repeater in the system
    RTS_TO_DATA_OUT_DELAY = 1,  //    has unkeyed (last transmission ended longer ago than the repeater tail)
                                //    so all elements of the radio system must come up to speed

    DATATOUNKEY_DELAY          = 2,  //  The end of data to unkey delay is used to let the transmit buffer finish the
    DATA_OUT_TO_RTS_DOWN_DELAY = 2,  //    data output before unkeying the radio

    UNKEYTODATA_DELAY                = 3,  //  The unkey to receive delay is used to allow the channel to switch
    DATA_OUT_TO_INBUFFER_FLUSH_DELAY = 3,  //    directions before flushing the receive buffer of any false characters
                                           //    generated by the switch and starting to receive data

    POST_REMOTE_DELAY,  //  post-communication delay

    EXTRA_DELAY,        //  additional time in seconds to allow the remote to respond

    LAST_DELAY          //  placeholder only!

};


enum CtiAmpUsage_t
{
    RouteAmpUndefined = 0,
    RouteAmpAlternating,
    RouteAmpAltFail,
    RouteAmp1,
    RouteAmp2,
    RouteAmpDefault1Fail2,
    RouteAmpDefault2Fail1,
    RouteAmpLastChoice
};


enum CtiRoute_t
{
    RouteTypeInvalid = 0,
    RouteTypeCCU,
    RouteTypeTCU,
    RouteTypeMacro,
    RouteTypeLCU,
    RouteTypeVersacom,
    RouteTypeExpresscom,
    RouteTypeTap,
    RouteTypeWCTP,
    RouteTypeRTC,
    RouteTypeSeriesVLMI,
    RouteTypeSNPP,
    RouteTypeTNPP,
    RouteTypeRDS,

    RouteTypeMax
};


enum CtiEmetconRelay_t
{
    Invalid_Relay = 0,
    Shed_A_Relay,
    Shed_B_Relay,
    Shed_C_Relay,
    Shed_D_Relay,

    Shed_Scram
};


enum CtiDBChanged_t
{
    ChangeInvalidDb = -1,
    ChangePAODb = 0,
    ChangePointDb,
    ChangeStateGroupDb,
    ChangeNotificationGroupDb,             // NotificationGroup and/or NotificationDestination changed
    ChangeNotificationRecipientDb,         // NotificationRecipient changed
    ChangeAlarmCategoryDb,                 // AlarmCategory changed 5
    ChangeCustomerContactDb,
    ChangeGraphDb,
    ChangeHolidayScheduleDb,
    ChangeEnergyCompanyDb,
    ChangeYukonUserDb, //10
    ChangeCustomerDb,
    ChangeCustomerAccountDb,
    ChangeYukonImageDb,
    ChangeBaselineDb,
    ChangeConfigDb, //15
    ChangeTagDb,
    ChangeCICustomerDb,
    ChangeLMConstraintDb,
    Number19IsntCurrentlyUsed,
    ChangeSeasonScheduleDb, //20
    ChangeTdcDb,
    ChangeDeviceTypeCommandDb,
    ChangeCommandDb,
    ChangeTOUScheduleDb,
    ChangeCBCStrategyDb, //25
    ChangePAOScheduleDB, //26
    //There is a gap here from the java code.
    ChangeCbcAdditionalInfo = 31,
    ChangeIvvcZone = 35
};


enum CtiDBChangedType_t
{
    ChangeTypeAdd = 0,
    ChangeTypeDelete,
    ChangeTypeUpdate
};


enum class CtiDBChangeCategory
{
    Invalid = -1,
    EnergyCompany = 0,                // 0
    EnergyCompanyRoute,
    EnergyCompanySubstations,
    Appliance,
    ServiceCompany,
    ServiceCompanyDesignationCode,    // 5
    Warehouse,
    YukonSelectionList,
    YukonListEntry,
    PorterResponseMonitor,
    DeviceDataMonitor,                // 10
    CCMonitorBankList,
    GlobalSetting,
    EnergyCompanySetting,
    RepeatingJob,
    DataExportFormat,                 // 15
    Monitor
};

enum CtiProtocol_t
{
    ProtocolVersacomType,
    ProtocolFisherPierceType,
    ProtocolGolayType,
    ProtocolSADigitalType,
    ProtocolSA105Type,
    ProtocolSA205Type,
    ProtocolSA305Type,
    ProtocolEmetconType,
    ProtocolExpresscomType
};


enum CtiOutMessageFlags_t
{
    MessageFlag_ApplyExclusionLogic        = 0x00000001 << 0,
    MessageFlag_RequeueCommandOnceOnFail   = 0x00000001 << 1,
    MessageFlag_ExpectMore                 = 0x00000001 << 2,
    MessageFlag_QueuedToDevice             = 0x00000001 << 3,
    MessageFlag_BroadcastOnMacroSubroutes  = 0x00000001 << 4,
    MessageFlag_PortSharing                = 0x00000001 << 5,
    MessageFlag_AddMctDisconnectSilence    = 0x00000001 << 6,
    MessageFlag_AddCcu711CooldownSilence   = 0x00000001 << 7,
    MessageFlag_StatisticsRequested        = 0x00000001 << 8,
    MessageFlag_EncryptionRequired         = 0x00000001 << 9,
};

