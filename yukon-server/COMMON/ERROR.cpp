#pragma title ( "Error Message Database Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        ERROR.C

    Purpose:
        To open and retrieve message from the error message database

    The following procedures are contained in this module:
        InitError                   getError
        CloseError                  PrintError

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-6-93  Converted to 32 bit                             WRO


   -------------------------------------------------------------------- */
#include <windows.h>       // These next few are required for Win32
#include <iostream>
using namespace std;

#include <rw\cstring.h>
#include <rw/tvsldict.h>

#include "os2_2w32.h"
#include "cticalls.h"


#include <stdlib.h>

// #include "btrieve.h"
#include <stdio.h>
#include "dsm2.h"
#include "dsm2err.h"
#include "dllbase.h"
#include "logger.h"
#include "yukon.h"

BYTE ErrorPosBlk[128];

#define CTIERRORHASHBINS   256

INT AddErrorToHashTable(ERRSTRUCT* ErrSt);

static bool beenInitialized = false;

static RWTValSlistDictionary< USHORT, ERRSTRUCT > CtiErrors;

ERRSTRUCT CTIErrors[] = {
    { NORMAL,               NORMAL_TXT,                NORMAL_TYPE                ," N/A"}, // 0
    { NOTNORMAL,            NOTNORMAL_TXT,             NOTNORMAL_TYPE             ," N/A"}, // 1
    { BADBCH,               BADBCH_TXT,                ERRTYPEPROTOCOL            ," N/A"}, // 100 now...
    { NODWORD,              NODWORD_TXT,               ERRTYPEPROTOCOL            ," N/A"},
    { BADTYPE,              BADTYPE_TXT,               ERRTYPEPROTOCOL            ," N/A"},
    { DLENGTH,              DLENGTH_TXT,               ERRTYPEPROTOCOL            ," N/A"},
    { BADLOAD,              BADLOAD_TXT,               BADLOAD_TYPE               ," N/A"},//  5
    { BADTIME,              BADTIME_TXT,               BADTIME_TYPE               ," N/A"},
    { BADLEVEL,             BADLEVEL_TXT,              BADLEVEL_TYPE              ," N/A"},
    { BADID,                BADID_TXT,                 BADID_TYPE                 ," N/A"},
    { BADRANGE,             BADRANGE_TXT,              BADRANGE_TYPE              ," N/A"},
    { MISPARAM,             MISPARAM_TXT,              MISPARAM_TYPE              ," N/A"},// 10
    { SYNTAX,               SYNTAX_TXT,                SYNTAX_TYPE                ," N/A"},
    { BADLATCH,             BADLATCH_TXT,              BADLATCH_TYPE              ," N/A"},
    { FNI,                  FNI_TXT,                   FNI_TYPE                   ," N/A"},
    { BADSTATE,             BADSTATE_TXT,              BADSTATE_TYPE              ," N/A"},
    { BADPARITY,            BADPARITY_TXT,             BADPARITY_TYPE             ," N/A"},//  15
    { BADCCU,               BADCCU_TXT,                BADCCU_TYPE                ," N/A"},
    { NACK1,                NACK1_TXT,                 NACK1_TYPE                 ," N/A"},
    { NACK2,                NACK2_TXT,                 NACK2_TYPE                 ," N/A"},
    { NACK3,                NACK3_TXT,                 NACK3_TYPE                 ," N/A"},
    { NACKPAD1,             NACKPAD1_TXT,              NACKPAD1_TYPE              ," N/A"},// 20
    { NACKPAD2,             NACKPAD2_TXT,              NACKPAD2_TYPE              ," N/A"},
    { NACKPAD3,             NACKPAD3_TXT,              NACKPAD3_TYPE              ," N/A"},
    { BADCCUTYPE,           BADCCUTYPE_TXT,            BADCCUTYPE_TYPE            ," N/A"},
    { BADCOUNT,             BADCOUNT_TXT,              BADCOUNT_TYPE              ," N/A"},
    { BADPAUSE,             BADPAUSE_TXT,              BADPAUSE_TYPE              ," N/A"},
    { BADPARAM,             BADPARAM_TXT,              BADPARAM_TYPE              ," N/A"},
    { BADROUTE,             BADROUTE_TXT,              BADROUTE_TYPE              ," N/A"},
    { BADBUSS,              BADBUSS_TXT,               BADBUSS_TYPE               ," N/A"},
    { BADAMP,               BADAMP_TXT,                BADAMP_TYPE                ," N/A"},
    { READERR,              READERR_TXT,               READERR_TYPE               ," N/A"},// 30
    { READTIMEOUT,          READTIMEOUT_TXT,           READTIMEOUT_TYPE           ," N/A"},
    { BADSEQUENCE,          BADSEQUENCE_TXT,           BADSEQUENCE_TYPE           ," N/A"},
    { FRAMEERR,             FRAMEERR_TXT,              FRAMEERR_TYPE              ," N/A"},
    { BADCRC,               BADCRC_TXT,                BADCRC_TYPE                ," N/A"},
    { BADLENGTH,            BADLENGTH_TXT,             BADLENGTH_TYPE             ," N/A"},
    { BADUA,                BADUA_TXT,                 BADUA_TYPE                 ," N/A"},
    { ERRUNKNOWN,           UNKNOWN_TXT,               UNKNOWN_TYPE               ," N/A"},
    { BADADDRESS,           BADADDRESS_TXT,            BADADDRESS_TYPE            ," N/A"},
    { BADROLE,              BADROLE_TXT,               BADROLE_TYPE               ," N/A"},
    { INVALIDFIX,           INVALIDFIX_TXT,            INVALIDFIX_TYPE            ," N/A"},// 40
    { INVALIDVOU,           INVALIDVOU_TXT,            INVALIDVOU_TYPE            ," N/A"},
    { INVALIDVIN,           INVALIDVIN_TXT,            INVALIDVIN_TYPE            ," N/A"},
    { INVALIDSTG,           INVALIDSTG_TXT,            INVALIDSTG_TYPE            ," N/A"},
    { 44,                   BLANK_TXT,                 NORMAL_TYPE                ," N/A"},
    { BADFILE,              BADFILE_TXT,               BADFILE_TYPE               ," N/A"},
    { REQACK,               REQACK_TXT,                REQACK_TYPE                ," N/A"},
    { RTFERR,               RTFERR_TXT,                RTFERR_TYPE                ," N/A"},
    { NOTR,                 NOTR_TXT,                  NOTR_TYPE                  ," N/A"},
    { RTNF,                 RTNF_TXT,                  RTNF_TYPE                  ," N/A"},
    { FNO,                  FNO_TXT,                   FNO_TYPE                   ," N/A"},//  50
    { RONF,                 RONF_TXT,                  RONF_TYPE                  ," N/A"},
    { ROFERR,               ROFERR_TXT,                ROFERR_TYPE                ," N/A"},
    { DBFERR,               DBFERR_TXT,                DBFERR_TYPE                ," N/A"},
    { IDNF,                 IDNF_TXT,                  IDNF_TYPE                  ," N/A"},
    { TYFERR,               TYFERR_TXT,                TYFERR_TYPE                ," N/A"},
    { TYNF,                 TYNF_TXT,                  TYNF_TYPE                  ," N/A"},
    { EWORDRCV,             EWORDRCV_TXT,              EWORDRCV_TYPE              ," N/A"},
    { BADFILL,              BADFILL_TXT,               BADFILL_TYPE               ," N/A"},
    { SYSTEM,               SYSTEM_TXT,                SYSTEM_TYPE                ," N/A"},
    { BADPORT,              BADPORT_TXT,               BADPORT_TYPE               ," N/A"},//  60
    { QUEUE_READ,           QUEUE_READ_TXT,            QUEUE_READ_TYPE            ," N/A"},
    { QUEUE_WRITE,          QUEUE_WRITE_TXT,           QUEUE_WRITE_TYPE           ," N/A"},
    { MEMORY,               MEMORY_TXT,                MEMORY_TYPE                ," N/A"},
    { SEMAPHORE,            SEMAPHORE_TXT,             SEMAPHORE_TYPE             ," N/A"},
    { NODCD,                NODCD_TXT,                 NODCD_TYPE                 ," N/A"},
    { WRITETIMEOUT,         WRITETIMEOUT_TXT,          WRITETIMEOUT_TYPE          ," N/A"},
    { PORTREAD,             PORTREAD_TXT,              PORTREAD_TYPE              ," N/A"},
    { PORTWRITE,            PORTWRITE_TXT,             PORTWRITE_TYPE             ," N/A"},
    { PIPEWRITE,            PIPEWRITE_TXT,             PIPEWRITE_TYPE             ," N/A"},
    { PIPEREAD,             PIPEREAD_TXT,              PIPEREAD_TYPE              ," N/A"},//  70
    { QUEUEEXEC,            QUEUEEXEC_TXT,             QUEUEEXEC_TYPE             ," N/A"},
    { DLCTIMEOUT,           DLCTIMEOUT_TXT,            DLCTIMEOUT_TYPE            ," N/A"},
    { NOATTEMPT,            NOATTEMPT_TXT,             NOATTEMPT_TYPE             ," N/A"},
    { ROUTEFAILED,          ROUTEFAILED_TXT,           ROUTEFAILED_TYPE           ," N/A"},
    { TRANSFAILED,          TRANSFAILED_TXT,           TRANSFAILED_TYPE           ," N/A"},
    { JWORDRCV,             JWORDRCV_TXT,              JWORDRCV_TYPE              ," N/A"},
    { NOREMOTEPORTER,       NOREMOTEPORTER_TXT,        NOREMOTEPORTER_TYPE        ," N/A"},
    { REMOTEINHIBITED,      REMOTEINHIBITED_TXT,       REMOTEINHIBITED_TYPE       ," N/A"},
    { QUEUEFLUSHED,         QUEUEFLUSHED_TXT,          QUEUEFLUSHED_TYPE          ," N/A"},
    { PIPEBROKEN,           PIPEBROKEN_TXT,            PIPEBROKEN_TYPE            ," N/A"},//  80
    { PIPEWASBROKEN,        PIPEWASBROKEN_TXT,         PIPEWASBROKEN_TYPE         ," N/A"},
    { PIPEOPEN,             PIPEOPEN_TXT,              PIPEOPEN_TYPE              ," N/A"},
    { PORTINHIBITED,        PORTINHIBITED_TXT,         PORTINHIBITED_TYPE         ," N/A"},
    { ACCUMSNOTSUPPORTED,   ACCUMSNOTSUPPORTED_TXT,    ACCUMSNOTSUPPORTED_TYPE    ," N/A"},
    { DEVICEINHIBITED,      DEVICEINHIBITED_TXT ,      DEVICEINHIBITED_TYPE       ," N/A"},
    { POINTINHIBITED,       POINTINHIBITED_TXT,        POINTINHIBITED_TYPE        ," N/A"},
    { DIALUPERROR,          DIALUPERROR_TXT,           DIALUPERROR_TYPE           ," N/A"},
    { WRONGADDRESS,         WRONGADDRESS_TXT,          WRONGADDRESS_TYPE          ," N/A"},
    { TCPCONNECTERROR,      TCPCONNECTERROR_TXT,       TCPCONNECTERROR_TYPE       ," N/A"},
    { TCPWRITEERROR,        TCPWRITEERROR_TXT,         TCPWRITEERROR_TYPE         ," N/A"},//  90
    { TCPREADERROR,         TCPREADERROR_TXT,          TCPREADERROR_TYPE          ," N/A"},
    { ADDRESSERROR,         ADDRESSERROR_TXT,          ADDRESSERROR_TYPE          ," N/A"},
    { ALPHABUFFERERROR,     ALPHABUFFERERROR_TXT,      ALPHABUFFERERROR_TYPE      ," N/A"}, // 93
    { 94,                   BLANK_TXT,                 NORMAL                     ," N/A"},
    { 95,                   BLANK_TXT,                 NORMAL                     ," N/A"},
    { 96,                   BLANK_TXT,                 NORMAL                     ," N/A"},
    { 97,                   BLANK_TXT,                 NORMAL                     ," N/A"},
    { BADSOCK,              BADSOCK_TXT,               BADSOCK_TYPE               ," N/A"},
    { SOCKWRITE,            SOCKWRITE_TXT,             SOCKWRITE_TYPE             ," N/A"},

    { 200,                  "Yukon Base Error",        ERRTYPESYSTEM                , " N/A"},
    { MemoryError,          "Memory Error",            ERRTYPESYSTEM                , " N/A"},
    { NoMethod,             "No Method",               ERRTYPESYSTEM                , " N/A"},
    { NoRefreshMethod,      "No Refresh Method",       ERRTYPESYSTEM                , " N/A"},
    { NoGeneralScanMethod,  "No General Scan Method",  ERRTYPESYSTEM                , " N/A"},
    { NoIntegrityScanMethod,"No Integrity Scan Method",ERRTYPESYSTEM                , " N/A"},
    { NoAccumulatorScanMethod, "No Accum Scan Method",    ERRTYPESYSTEM                , " N/A"},
    { NoProcessResultMethod,   "No Process Result Method",ERRTYPESYSTEM                , " N/A"},
    { NoExecuteRequestMethod,  "No Exec. Req. Method",    ERRTYPESYSTEM                , " N/A"},
    { NoResultDecodeMethod, "No Result Decode Method", ERRTYPESYSTEM                , " N/A"},
    { NoErrorDecodeMethod,  "No ErrorDecode Method",     ERRTYPESYSTEM                , " N/A"},
    { NoHandShakeMethod,    "No Handshake Method",     ERRTYPESYSTEM                , " N/A"},
    { NoGenerateCmdMethod,  "No Generate Command Method",     ERRTYPESYSTEM                , " N/A"},
    { NoDecodeResponseMethod, "No DecodeResponse Method",     ERRTYPESYSTEM                , " N/A"},
    { NoDataCopyMethod,     "No Data Copy Method",     ERRTYPESYSTEM                , " N/A"},
    { NotNumeric,           "Not Numeric",             ERRTYPESYSTEM                , " N/A"},
    { NoConfigData,         "No Config Data Found",    ERRTYPESYSTEM                , " N/A"},
    { NoRouteGroupDevice,   "No Route for Group Dev.", ERRTYPESYSTEM                , " N/A"},
    { NoRoutesInMacro,      "No Routes for Macro Rte", ERRTYPESYSTEM                , " N/A"},
    { RouteOffsetOutOfRange, "Macro Offset does not exist in Macro Rte", ERRTYPESYSTEM                , " N/A"},
    { SubRouteIsMacro,      "Macro Offset refers to a macro sub-rte", ERRTYPESYSTEM                , " N/A"},
    { ControlInhibitedOnDevice, "Device is control disabled", ERRTYPESYSTEM                , " N/A"},
    { ControlInhibitedOnPoint,  "Point is control disabled", ERRTYPESYSTEM                , " N/A"},
    { ControlRequestComplete, "Control Completed",       ERRTYPESYSTEM                , " N/A"},
    { ErrRequestExpired,    "Requested operation expired due to time",       ERRTYPESYSTEM                , " N/A"},
    { ErrorNexusRead,       "Error Reading Nexus",     ERRTYPESYSTEM                , " N/A"},

    { InThreadTerminated,   "CtiConnection: InThread Terminated",     ERRTYPESYSTEM                , " N/A"},
    { OutThreadTerminated,  "CtiConnection: OutThread Terminated",     ERRTYPESYSTEM                , " N/A"},
    { InboundSocketBad,     "CtiConnection: Inbound Socket Bad",     ERRTYPESYSTEM                , " N/A"},
    { OutboundSocketBad,    "CtiConnection: Outbound Socket Bad",     ERRTYPESYSTEM                , " N/A"},
    { ErrPortInitFailed,    "Port Init Failed",     ERRTYPESYSTEM                , " N/A"},
    { ErrPortDialupConnect_Port,    "Diailup connection failed. Port in error",     ERRTYPECOMM    , " N/A"},
    { ErrPortDialupConnect_Device,  "Diailup connection failed. Device in error",   ERRTYPESYSTEM  , " N/A"},
    { ErrPortSimulated,  "Port is simulated, no inbound data available",   ERRTYPEPROTOCOL  , " N/A"},

    { RETRY_SUBMITTED,      "Retry Resubmitted",       ERRTYPESYSTEM                , " N/A"},
    { SCAN_ERROR_DEVICE_INHIBITED, "Scanned device is inhibited",   ERRTYPESYSTEM                , " N/A"},
    { SCAN_ERROR_GLOBAL_ADDRESS,   "Illegal scan of global device", ERRTYPESYSTEM                , " N/A"},
    { SCAN_ERROR_DEVICE_WINDOW_CLOSED, "Device window is closed",   ERRTYPESYSTEM                , " N/A"},
    { ErrorPageRS,  "Invalid transaction, typ. bad pager id or password", ERRTYPESYSTEM                , " N/A"},
    { ErrorPageNAK, "TAP Repeat Requested, but retries exhausted", ERRTYPESYSTEM                , " N/A"},
    { ErrorPageNoResponse, "No response from TAP terminal", ERRTYPESYSTEM                , " N/A"},

    { ErrorHttpResponse,    "Invalid or unsuccessful HTTP response", ERRTYPESYSTEM                , " N/A"},
    { ErrorXMLParser,       "XML parser initialization failed", ERRTYPESYSTEM                , " N/A"},
    { ErrorWctpResponse,    "Invalid WCTP response format", ERRTYPESYSTEM                , " N/A"},
    { ErrorWctpTimeout,     "Time out when receiving WCTP response", ERRTYPESYSTEM                , " N/A"},
    { ErrorWctp300Series,   "Protocol Error 300 Series", ERRTYPESYSTEM                , " N/A"},
    { ErrorWctp400Series,   "Protocol Error 400 Series", ERRTYPESYSTEM                , " N/A"},
    { ErrorWctp500Series,   "Protocol Error 500 Series", ERRTYPESYSTEM                , " N/A"},
    { ErrorWctp600Series,   "Protocol Error 600 Series", ERRTYPESYSTEM                , " N/A"},

    { ErrorQueuePurged, "Queue purged to limit memory usage", ERRTYPESYSTEM                , " N/A"},

    { CtiInvalidRequest,    "Invalid/Incomplete Request",    ERRTYPESYSTEM                , " N/A"},
    { UnknownError,         "Unknown Error",           ERRTYPESYSTEM                , " N/A"},

};

/* Routine to open up the error file */
INT InitError (VOID)
{
    INT rc, i;

    CtiErrors.clear();

    for(i = 0 ; i < (sizeof(CTIErrors)/ sizeof(ERRSTRUCT)); i++)
    {
        CtiErrors.insertKeyAndValue( (CTIErrors[i].Error), CTIErrors[i]);
    }

    beenInitialized = true;
    return(NORMAL);
}


/* Routine to close the error file */
INT CloseError(VOID)
{
    return(NORMAL);
}


/* Routine to retrive and print an error message in C */
IM_EX_CTIBASE INT PrintError (USHORT Error)
{
    ERRSTRUCT ESt;

    if( !CtiErrors.findValue( Error, ESt) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Error in Error Message Routine with error " << Error << endl;
        }
        return(!NORMAL);
    }

    /* and print it */
    printf ("Error %04hd:  %s\n", Error, ESt.ErrorMessage);

    return(NORMAL);
}

IM_EX_CTIBASE RWCString FormatError(USHORT Error)
{
    char err[256];

    GetErrorString(Error, err);

    return RWCString(err);
}

/* Routine to retrive and return an error message's text */
IM_EX_CTIBASE INT GetErrorString (USHORT Error, char *ErrStr)
{
    ERRSTRUCT ESt;
    SHORT i;

    ESt.Error = Error;

    GetError( ESt );

    /* and copy it */
    strcpy(ErrStr, ESt.ErrorMessage);

    return(NORMAL);
}


/* routine to retrieve error messages from error message file */
IM_EX_CTIBASE INT GetError(ERRSTRUCT &ESt)
{
    INT status = NORMAL;
    USHORT err = ESt.Error;

    if(!beenInitialized)
    {
        InitError();
    }

    if( !CtiErrors.findValue( err, ESt ) )
    {
        SetUnknown(ESt);
        status = !NORMAL;
    }

    return(status);
}


/* routine to retrieve error messages from error message file */
IM_EX_CTIBASE CHAR* GetError (INT err)
{

    USHORT enumber = (USHORT) err;
    if(!beenInitialized)
    {
        InitError();
    }

    if( CtiErrors.contains( enumber ) )
    {
        // It is in there
        return CtiErrors[ enumber ].ErrorMessage;
    }
    else if(CtiErrors.contains( ERRUNKNOWN ))
    {
        return CtiErrors[ ERRUNKNOWN ].ErrorMessage;
    }
    else
    {
        return NULL;
    }

    return NULL;
}

/* routine to retrieve error messages from error message file */
IM_EX_CTIBASE INT GetErrorType(INT err)
{
    USHORT enumber = (USHORT) err;
    if(!beenInitialized)
    {
        InitError();
    }

    if( CtiErrors.contains( enumber ) )
    {
        return CtiErrors[ enumber ].Type;
    }
    else if(CtiErrors.contains( ERRUNKNOWN ))
    {
        return CtiErrors[ ERRUNKNOWN ].Type;
    }

    return 0;
}

INT SetUnknown( ERRSTRUCT &ESt )
{
    strcpy(ESt.ErrorMessage, UNKNOWN_TXT);
    ESt.Type          = UNKNOWN_TYPE;

    return NORMAL;
}
