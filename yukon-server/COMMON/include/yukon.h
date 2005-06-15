/*-----------------------------------------------------------------------------*
*
* File:   yukon
*
* Date:   5/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/yukon.h-arc  $
* REVISION     :  $Revision: 1.41 $
* DATE         :  $Date: 2005/06/15 19:14:35 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __YUKON_H__
#define __YUKON_H__
#pragma warning( disable : 4786)

#include "precompiled.h"

#include "ctidbgmem.h"      // defines CTIDBG_new for memory tracking!

#include <rw\thr\mutex.h>

// YUKONEOT is needed to make ALL timezones operate correctly wrt the "end-of-time"
#define YUKONEOT (ULONG_MAX - 86400 * 2)

#define PIL_REGISTRATION_NAME        "PIL"
#define SCANNER_REGISTRATION_NAME    "SCANNER"
#define PORTER_REGISTRATION_NAME     "PORTER"

// Load Management Active Restore Value
#if 0

    #define LMAR_CONTINUE   "C"
    #define LMAR_RESTORE    "R"
    #define LMAR_TIMED      "T"
    #define LMAR_LATCH      "L"
    #define LMAR_MANUAL     "M"

#else

    #define LMAR_NEWCONTROL         "N"             // This is the first entry for any new control.
    #define LMAR_LOGTIMER           "L"             // This is a timed log entry.  Nothing exciting happened in this interval.
    #define LMAR_CONT_CONTROL       "C"             // Previous command was repeated extending the current control interval.

    #define LMAR_TIMED_RESTORE      "T"             // Control terminated based on time set in load group.
    #define LMAR_MANUAL_RESTORE     "M"             // Control terminated because of an active restore or terminate command being sent.
    #define LMAR_OVERRIDE_CONTROL   "O"             // Control terminated because a new command of a different nature was sent to this group.
    #define LMAR_CONTROLACCT_ADJUST "A"             // Control accounting was adjusted by user.
    #define LMAR_PERIOD_TRANSITION  "P"             // Control was active as we crossed a control history boundary.  This log denotes the last log in the previos interval.
    #define LMAR_DISPATCH_SHUTDOWN  "S"             // Control was active as dispatch shutdown.  This entry will be used to resume control.

#endif

#define RESTORE_DURATION -1


//  used by dev_manager and tbl_dyn_paoinfo
enum CtiApplication_t
{
    Application_Dispatch = 100,  //  just so if we ever run into a case where it's initialized to 0,
    Application_Porter,          //    there'll be no confusion - it'll be recognized as invalid
    Application_Scanner,
    Application_CapControl,
    Application_LoadManagement,
    Application_CalcLogic,

    Application_Invalid  =  -1
};


enum CtiPaoCategory_t
{
    PAO_CATEGORY_DEVICE = 0,
    PAO_CATEGORY_PORT,
    PAO_CATEGORY_ROUTE,
    PAO_CATEGORY_LOAD_MANAGEMENT,
    PAO_CATEGORY_CAP_CONTROL,
};


enum CtiCommState_t
{
    CommState_Normal = 0,
    CommState_Failed,
    CommState_Disabled,

    CommState_Invalid
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
    PAOClassCarrier,
    PAOClassGroup,
    PAOClassCapControl,
    PAOClassVirtual,
    PAOClassLoadManagement,
    PAOClassSystem,
    PAOClassMACS,

    PAOClassInvalid
};

enum CtiDeviceState_t
{
    DeviceStateNormal = 0,
    DeviceStateDisabled,
    DeviceStateFailed,

    DeviceStateInvalid
};

enum CtiPortState_t
{
    PortStateNormal = 0,
    PortStateDisabled,
    PortStateFailed,

    PortStateInvalid
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

    MACSLogType = 7,
    AlarmCategoryLogType = 8,

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

enum YukonError_t
{
    NoError = 0,

    // General Errors
    YukonBaseError = 200,       //  Account for the old DSM/2 Errors
    MemoryError,

    // Device Errors
    NoMethod,
    NoRefreshMethod,
    NoGeneralScanMethod,
    NoIntegrityScanMethod,
    NoAccumulatorScanMethod,
    NoLoadProfileScanMethod,
    NoProcessResultMethod,
    NoExecuteRequestMethod,
    NoResultDecodeMethod,
    NoErrorDecodeMethod,
    NoHandShakeMethod,
    NoGenerateCmdMethod,
    NoDecodeResponseMethod,
    NoDataCopyMethod,
    GeneralScanAborted,

    // Cmd Line Errors
    NotNumeric,

    // Connection Errors
    NoConfigData,

    // Control Command Info
    NoRouteGroupDevice,         // No route defined in a group device
    NoRoutesInMacro,            // Route Macro contained no sub-routes.
    RouteOffsetOutOfRange,
    SubRouteIsMacro,
    ControlInhibitedOnDevice,
    ControlInhibitedOnPoint,
    ControlRequestComplete,
    ErrRequestExpired,

    // Nexus Errors
    ErrorNexusRead,

    // CtiConnection Errors
    InThreadTerminated,
    OutThreadTerminated,
    InboundSocketBad,
    OutboundSocketBad,

    // Non-zero return codes
    RETRY_SUBMITTED,                    // The OutMessage in question has been requeued onto the portqueue.
    QUEUED_TO_DEVICE,                   // The OutMessage in question has been enqueud onto a device queue.
    CONTINUE_LOOP,                      // The propagate a continue to the looping construct above.

    SCAN_ERROR_DEVICE_INHIBITED,
    SCAN_ERROR_GLOBAL_ADDRESS,
    SCAN_ERROR_DEVICE_WINDOW_CLOSED,

    // Port and dialup errors
    ErrPortInitFailed,
    ErrPortDialupConnect_Port,        // Error making connection.. Assumed to be the port's fault
    ErrPortDialupConnect_Device,      // Error making connection.. Assumed to be the device's fault
    ErrPortSimulated,                 // The port is being simulated - there will be no inbound
    ErrPortEchoResponse,              // The port returned the exact bytes it was given - probably a hardware failure somewhere

    // Paging errors
    ErrorPageRS,                  // Invalid transaction, typ. bad pager id or password
    ErrorPageNAK,                 // TAP Repeat Requested, but retries exhausted
    ErrorPageNoResponse,

    CtiInvalidRequest,
    UnknownError,

    // WCTP errors
    ErrorHttpResponse,          // Invalid or unsuccessful HTTP response
    ErrorXMLParser,             // XML parser initialization failed
    ErrorWctpResponse,          // Invalid WCTP response format
    ErrorWctpTimeout,           // Time out when receiving WCTP response
    ErrorWctp300Series,         // Protocol Error 300 Series.
    ErrorWctp400Series,
    ErrorWctp500Series,
    ErrorWctp600Series,

    ErrorQueuePurged,           // Queue purged to clean memory.

    FinalError
};


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


/*
 *  The types below CANNOT EVER overlap the TYPE_ defines found in devicetypes.h
 *  BREAKAGE MAY OCCUR.  6000 to 6100 have been reserved by comment in that file.
 */
enum CtiPort_t
{
    PortTypeLocalDirect = 6000,
    PortTypeLocalDialup,
    PortTypeTServerDirect,
    PortTypeTServerDialup,
    PortTypeLocalDialBack,
    PortTypeTServerDialBack,
    PortTypePoolDialout,

    PortTypeInvalid
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
    RouteTypeRepeater,
    RouteTypeVersacom,
    RouteTypeTap,
    RouteTypeWCTP,
    RouteTypeRTC,
    RouteTypeSeriesVLMI,

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
    ChangeAlarmCategoryDb,                 // AlarmCategory changed
    ChangeCustomerContactDb,
    ChangeGraphDb,
    ChangeHolidayScheduleDb,
    ChangeEnergyCompanyDb,
    ChangeYukonUserDb,
    ChangeCustomerDb,
    ChangeCustomerAccountDb,
    ChangeYukonImageDb,
    ChangeBaselineDb,
    ChangeConfigDb,
    ChangeTagDb,
    ChangeCICustomerDb,
    ChangeLMConstraintDb,
    Number19IsntCurrentlyUsed,
    ChangeSeasonScheduleDb
};


enum CtiDBChangedType_t
{
    ChangeTypeAdd = 0,
    ChangeTypeDelete,
    ChangeTypeUpdate
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
    ProtocolExpresscomType,
    ProtocolEnergyProType
};


enum CtiOutMessageFlags_t
{
    MSGFLG_APPLY_EXCLUSION_LOGIC          = 0x00000001 << 0,
    MSGFLG_REQUEUE_CMD_ONCE_ON_FAIL       = 0x00000001 << 1,
    MSGFLG_ROUTE_TO_PORTER_GATEWAY_THREAD = 0x00000001 << 2,
    MSGFLG_ROUTE_TO_PORTER_DNPUDP_THREAD  = 0x00000001 << 3,
    MSGFLG_EXPECT_MORE                    = 0x00000001 << 4,
    MSGFLG_QUEUED_TO_DEVICE               = 0x00000001 << 5
};


#endif // __YUKON_H__
