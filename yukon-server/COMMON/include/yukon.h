#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   yukon
*
* Date:   5/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/yukon.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/04/24 21:37:52 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __YUKON_H__
#define __YUKON_H__

#include <rw\thr\mutex.h>
#include "dlldefs.h"
#include "dllbase.h"

// YUKONEOT is needed to make ALL timezones operate correctly wrt the "end-of-time"
#define YUKONEOT (ULONG_MAX - 86400 * 2)

#define PIL_REGISTRATION_NAME    "PIL"
#define SCANNER_REGISTRATION_NAME    "SCANNER"

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

#endif

#define RESTORE_DURATION -1


typedef enum
{
    PAO_CATEGORY_DEVICE = 0,
    PAO_CATEGORY_PORT,
    PAO_CATEGORY_ROUTE,
    PAO_CATEGORY_LOAD_MANAGEMENT,
    PAO_CATEGORY_CAP_CONTROL,

} CtiPaoCategory_t;


typedef enum
{
    CommState_Normal = 0,
    CommState_Failed,
    CommState_Disabled,

    CommState_Invalid

} CtiCommState_t;


enum
{
    PrimaryRoute = 0,
    AlternateRoute1,
    AlternateRoute2,
    InvalidRoute
};


/*
 *   !!!! WARNING !!!!  This enum directly affects tbl_dv_scandata.cpp/h
 */
enum
{
    ScanRateGeneral = 0,
    ScanRateAccum,
    ScanRateStatus,
    ScanRateIntegrity,
    ScanRateLoadProfile,
    ScanRateInvalid
};

enum
{
    DeviceWindowScan = 0,
    DeviceWindowPeak,
    DeviceWindowAlternateRate,
    DeviceWindowSignaledAlternateRate,
    DeviceWindowInvalid
};


enum
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

enum
{
    DeviceStateNormal = 0,
    DeviceStateDisabled,
    DeviceStateFailed,

    DeviceStateInvalid
};

enum
{
    PortStateNormal = 0,
    PortStateDisabled,
    PortStateFailed,

    PortStateInvalid
};

enum
{
    StatTypeHourly = 0,
    StatType24Hour,
    StatTypeMonthly,

    StatTypeInvalid
};

// Alarm Classes
typedef enum
{
    SignalEvent = 1,       // Event - Not propagated to clients
    SignalAlarm0,           // Alarms span 2 to 255

    SignalInvalid = 256

} CtiSignalClass_t;

typedef enum
{
    GeneralLogType = 1,
    PtChangeLogType,
    LoadMgmtLogType,
    UserLogType,

    InvalidLogType

} CtiLogTypeClass_t;

// Alarms
typedef enum
{
    NoAlarm = 0,
    NAKAlarm,         // Alarmed and Unacknowledged alarm (Not AcKnowledged)
    ACKAlarm,         // Alarmed and ACKnowledged

    InvalidAlarm

} CtiAlarm_t;

typedef enum
{
    NoError = 0,

    // General Errors
    YukonBaseError = 200,            // Account for the old DSM/2 Errors
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
    NoRouteGroupDevice,           // No route defined in a group device
    NoRoutesInMacro,              // Route Macro contained no sub-routes.
    RouteOffsetOutOfRange,
    SubRouteIsMacro,
    ControlInhibitedOnDevice,
    ControlInhibitedOnPoint,
    ControlRequestComplete,

    // Nexus Errors
    ErrorNexusRead,

    // CtiConnection Errors
    InThreadTerminated,
    OutThreadTerminated,
    InboundSocketBad,
    OutboundSocketBad,

    // Non-zero return codes
    RETRY_SUBMITTED,
    MESSAGEREQUEUED,

    SCAN_ERROR_DEVICE_INHIBITED,
    SCAN_ERROR_GLOBAL_ADDRESS,
    SCAN_ERROR_DEVICE_WINDOW_CLOSED,

    ErrPortInitFailed,

    // Paging errors
    ErrorPageRS,                  // Invalid transaction, typ. bad pager id or password
    ErrorPageNAK,                 // TAP Repeat Requested, but retries exhausted
    ErrorPageNoResponse,

    CtiInvalidRequest,
    UnknownError,
    FinalError

} YukonError_t;



/* Define protocol wraps */
typedef enum
{
    ProtocolWrapNone = 0,   // NOWRAP
    ProtocolWrapIDLC,       // IDLCWRAP,

    InvalidProtocol

} CtiProtocolWrap_t;

#define IDLCWRAP_TXT "IDLC"

typedef enum
{
    /* The prekey delay is from end of last complete sequence till start of next sequence */
    PREKEY_DELAY=0,
    PRE_RTS_DELAY=0,
    /* The cold repeater key to data delay is used when the repeater in the system
       has unkeyed (last transmission ended longer ago than the repeater tail)
       so all elements of the radio system must come up to speed */
    COLDKEYTODATA_DELAY = 1,
    RTS_TO_DATA_OUT_DELAY = 1,

    /* The end of data to unkey delay is used to let the transmit buffer finish the
       data output before unkeying the radio */
    DATATOUNKEY_DELAY = 2,
    DATA_OUT_TO_RTS_DOWN_DELAY=2,

    /* The unkey to receive delay is used to allow the channel to switch directions
       before flushing the receive buffer of any false characters generated by the
       switch and starting to receive data */
    UNKEYTODATA_DELAY = 3,
    DATA_OUT_TO_INBUFFER_FLUSH_DELAY = 3,

    /* Post Communicatino Delay... */

    POST_REMOTE_DELAY,

    /* Additional time in seconds to allow the remote to respond. */
    EXTRA_DELAY,

    /* A Place holder only! */
    LAST_DELAY

} CtiPortDelay_t;

/*
 *  The types below CANNOT EVER overlap the TYPE_ defines found in devicetypes.h
 *  BREAKAGE MAY OCCUR.  6000 to 6100 have been reserved by comment in that file.
 */
typedef enum
{
    PortTypeLocalDirect = 6000,
    PortTypeLocalDialup,
    PortTypeTServerDirect,
    PortTypeTServerDialup,

    PortTypeInvalid

} CtiPort_t;

typedef enum
{
    RouteAmpUndefined = 0,
    RouteAmpAlternating,
    RouteAmpAltFail,
    RouteAmp1,
    RouteAmp2,
    RouteAmpDefault1Fail2,
    RouteAmpDefault2Fail1,
    RouteAmpLastChoice

} CtiAmpUsage_t;

typedef enum
{

    InvalidRouteType = 0,
    CCURouteType,
    TCURouteType,
    MacroRouteType,
    LCURouteType,
    RepeaterRouteType,
    VersacomRouteType,
    TapRouteType,

    MaxRouteType

} CtiRoute_t;

typedef enum
{
    Invalid_Relay = 0,
    Shed_A_Relay,
    Shed_B_Relay,
    Shed_C_Relay,
    Shed_D_Relay,

    Shed_Scram

} CtiEmetconRelay_t;

typedef enum
{
    ChangePAODb = 0,
    ChangePointDb,
    ChangeStateGroupDb,
    ChangeNotificationGroupDb,             // NotificationGroup and/or NotificationDestination changed
    ChangeNotificationRecipientDb,         // NotificationRecipient changed
    ChangeAlarmCategoryDb,                 // AlarmCategory changed
    ChangeCustomerContactDb,
    ChangeGraphDb,
    ChangeHolidaySchedule,
    ChangeEnergyCompany

} CtiDBChanged_t;

typedef enum
{
    ChangeTypeAdd = 0,
    ChangeTypeDelete,
    ChangeTypeUpdate

} CtiDBChangedType_t;

typedef enum
{
    ProtocolVersacomType,
    ProtocolFisherPierceType,
    ProtocolSA105Type,
    ProtocolSA205Type,
    ProtocolSA305Type,
    ProtocolEmetconType

} CtiProtocol_t;


#endif // __YUKON_H__
