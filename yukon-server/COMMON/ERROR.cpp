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
#include "precompiled.h"

// These next few are required for Win32
#include <iostream>
using namespace std;

#include <rw/tvsldict.h>

#include "os2_2w32.h"
#include "cticalls.h"


#include <stdlib.h>

#include <stdio.h>
#include "dsm2.h"
#include "dsm2err.h"
#include "dllbase.h"
#include "logger.h"

BYTE ErrorPosBlk[128];

#define CTIERRORHASHBINS   256

INT AddErrorToHashTable(ERRSTRUCT* ErrSt);

static bool beenInitialized = false;

static RWTValSlistDictionary< USHORT, ERRSTRUCT > CtiErrors;

ERRSTRUCT CTIErrors[] = {
    { NORMAL,               NORMAL_TXT,                NORMAL_TYPE                }, // 0
    { NOTNORMAL,            NOTNORMAL_TXT,             NOTNORMAL_TYPE             }, // 1
    { BADBCH,               BADBCH_TXT,                ERRTYPEPROTOCOL            }, // 100 now...
    { NODWORD,              NODWORD_TXT,               ERRTYPEPROTOCOL            },
    { BADTYPE,              BADTYPE_TXT,               ERRTYPEPROTOCOL            },
    { DLENGTH,              DLENGTH_TXT,               ERRTYPEPROTOCOL            },
    { BADLOAD,              BADLOAD_TXT,               BADLOAD_TYPE               },//  5
    { BADTIME,              BADTIME_TXT,               BADTIME_TYPE               },
    { BADLEVEL,             BADLEVEL_TXT,              BADLEVEL_TYPE              },
    { BADID,                BADID_TXT,                 BADID_TYPE                 },
    { BADRANGE,             BADRANGE_TXT,              BADRANGE_TYPE              },
    { MISPARAM,             MISPARAM_TXT,              MISPARAM_TYPE              },// 10
    { SYNTAX,               SYNTAX_TXT,                SYNTAX_TYPE                },
    { BADLATCH,             BADLATCH_TXT,              BADLATCH_TYPE              },
    { FNI,                  FNI_TXT,                   FNI_TYPE                   },
    { BADSTATE,             BADSTATE_TXT,              BADSTATE_TYPE              },
    { BADPARITY,            BADPARITY_TXT,             BADPARITY_TYPE             },//  15
    { BADCCU,               BADCCU_TXT,                BADCCU_TYPE                },
    { NACK1,                NACK1_TXT,                 NACK1_TYPE                 },
    { NACK2,                NACK2_TXT,                 NACK2_TYPE                 },
    { NACK3,                NACK3_TXT,                 NACK3_TYPE                 },
    { NACKPAD1,             NACKPAD1_TXT,              NACKPAD1_TYPE              },// 20
    { NACKPAD2,             NACKPAD2_TXT,              NACKPAD2_TYPE              },
    { NACKPAD3,             NACKPAD3_TXT,              NACKPAD3_TYPE              },
    { BADCCUTYPE,           BADCCUTYPE_TXT,            BADCCUTYPE_TYPE            },
    { BADCOUNT,             BADCOUNT_TXT,              BADCOUNT_TYPE              },
    { BADPAUSE,             BADPAUSE_TXT,              BADPAUSE_TYPE              },
    { BADPARAM,             BADPARAM_TXT,              BADPARAM_TYPE              },
    { BADROUTE,             BADROUTE_TXT,              BADROUTE_TYPE              },
    { BADBUSS,              BADBUSS_TXT,               BADBUSS_TYPE               },
    { BADAMP,               BADAMP_TXT,                BADAMP_TYPE                },
    { READERR,              READERR_TXT,               READERR_TYPE               },// 30
    { READTIMEOUT,          READTIMEOUT_TXT,           READTIMEOUT_TYPE           },
    { BADSEQUENCE,          BADSEQUENCE_TXT,           BADSEQUENCE_TYPE           },
    { FRAMEERR,             FRAMEERR_TXT,              FRAMEERR_TYPE              },
    { BADCRC,               BADCRC_TXT,                BADCRC_TYPE                },
    { BADLENGTH,            BADLENGTH_TXT,             BADLENGTH_TYPE             },
    { BADUA,                BADUA_TXT,                 BADUA_TYPE                 },
    { ERRUNKNOWN,           UNKNOWN_TXT,               UNKNOWN_TYPE               },
    { BADADDRESS,           BADADDRESS_TXT,            BADADDRESS_TYPE            },
    { BADROLE,              BADROLE_TXT,               BADROLE_TYPE               },
    { INVALIDFIX,           INVALIDFIX_TXT,            INVALIDFIX_TYPE            },// 40
    { INVALIDVOU,           INVALIDVOU_TXT,            INVALIDVOU_TYPE            },
    { INVALIDVIN,           INVALIDVIN_TXT,            INVALIDVIN_TYPE            },
    { INVALIDSTG,           INVALIDSTG_TXT,            INVALIDSTG_TYPE            },
    { 44,                   BLANK_TXT,                 NORMAL_TYPE                },
    { BADFILE,              BADFILE_TXT,               BADFILE_TYPE               },
    { REQACK,               REQACK_TXT,                REQACK_TYPE                },
    { RTFERR,               RTFERR_TXT,                RTFERR_TYPE                },
    { NOTR,                 NOTR_TXT,                  NOTR_TYPE                  },
    { RTNF,                 RTNF_TXT,                  RTNF_TYPE                  },
    { FNO,                  FNO_TXT,                   FNO_TYPE                   },//  50
    { RONF,                 RONF_TXT,                  RONF_TYPE                  },
    { ROFERR,               ROFERR_TXT,                ROFERR_TYPE                },
    { DBFERR,               DBFERR_TXT,                DBFERR_TYPE                },
    { IDNF,                 IDNF_TXT,                  IDNF_TYPE                  },
    { TYFERR,               TYFERR_TXT,                TYFERR_TYPE                },
    { TYNF,                 TYNF_TXT,                  TYNF_TYPE                  },
    { EWORDRCV,             EWORDRCV_TXT,              EWORDRCV_TYPE              },
    { BADFILL,              BADFILL_TXT,               BADFILL_TYPE               },
    { SYSTEM,               SYSTEM_TXT,                SYSTEM_TYPE                },
    { BADPORT,              BADPORT_TXT,               BADPORT_TYPE               },//  60
    { QUEUE_READ,           QUEUE_READ_TXT,            QUEUE_READ_TYPE            },
    { QUEUE_WRITE,          QUEUE_WRITE_TXT,           QUEUE_WRITE_TYPE           },
    { MEMORY,               MEMORY_TXT,                MEMORY_TYPE                },
    { SEMAPHORE,            SEMAPHORE_TXT,             SEMAPHORE_TYPE             },
    { NODCD,                NODCD_TXT,                 NODCD_TYPE                 },
    { WRITETIMEOUT,         WRITETIMEOUT_TXT,          WRITETIMEOUT_TYPE          },
    { PORTREAD,             PORTREAD_TXT,              PORTREAD_TYPE              },
    { PORTWRITE,            PORTWRITE_TXT,             PORTWRITE_TYPE             },
    { PIPEWRITE,            PIPEWRITE_TXT,             PIPEWRITE_TYPE             },
    { PIPEREAD,             PIPEREAD_TXT,              PIPEREAD_TYPE              },//  70
    { QUEUEEXEC,            QUEUEEXEC_TXT,             QUEUEEXEC_TYPE             },
    { DLCTIMEOUT,           DLCTIMEOUT_TXT,            DLCTIMEOUT_TYPE            },
    { NOATTEMPT,            NOATTEMPT_TXT,             NOATTEMPT_TYPE             },
    { ROUTEFAILED,          ROUTEFAILED_TXT,           ROUTEFAILED_TYPE           },
    { TRANSFAILED,          TRANSFAILED_TXT,           TRANSFAILED_TYPE           },
    { JWORDRCV,             JWORDRCV_TXT,              JWORDRCV_TYPE              },
    { NOREMOTEPORTER,       NOREMOTEPORTER_TXT,        NOREMOTEPORTER_TYPE        },
    { REMOTEINHIBITED,      REMOTEINHIBITED_TXT,       REMOTEINHIBITED_TYPE       },
    { QUEUEFLUSHED,         QUEUEFLUSHED_TXT,          QUEUEFLUSHED_TYPE          },
    { PIPEBROKEN,           PIPEBROKEN_TXT,            PIPEBROKEN_TYPE            },//  80
    { PIPEWASBROKEN,        PIPEWASBROKEN_TXT,         PIPEWASBROKEN_TYPE         },
    { PIPEOPEN,             PIPEOPEN_TXT,              PIPEOPEN_TYPE              },
    { PORTINHIBITED,        PORTINHIBITED_TXT,         PORTINHIBITED_TYPE         },
    { ACCUMSNOTSUPPORTED,   ACCUMSNOTSUPPORTED_TXT,    ACCUMSNOTSUPPORTED_TYPE    },
    { DEVICEINHIBITED,      DEVICEINHIBITED_TXT ,      DEVICEINHIBITED_TYPE       },
    { POINTINHIBITED,       POINTINHIBITED_TXT,        POINTINHIBITED_TYPE        },
    { DIALUPERROR,          DIALUPERROR_TXT,           DIALUPERROR_TYPE           },
    { WRONGADDRESS,         WRONGADDRESS_TXT,          WRONGADDRESS_TYPE          },
    { TCPCONNECTERROR,      TCPCONNECTERROR_TXT,       TCPCONNECTERROR_TYPE       },
    { TCPWRITEERROR,        TCPWRITEERROR_TXT,         TCPWRITEERROR_TYPE         },//  90
    { TCPREADERROR,         TCPREADERROR_TXT,          TCPREADERROR_TYPE          },
    { ADDRESSERROR,         ADDRESSERROR_TXT,          ADDRESSERROR_TYPE          },
    { ALPHABUFFERERROR,     ALPHABUFFERERROR_TXT,      ALPHABUFFERERROR_TYPE      }, // 93
    { MISCONFIG,            MISCONFIG_TXT,             MISCONFIG_TYPE             },
    { 95,                   BLANK_TXT,                 NORMAL                     },
    { 96,                   BLANK_TXT,                 NORMAL                     },
    { 97,                   BLANK_TXT,                 NORMAL                     },
    { BADSOCK,              BADSOCK_TXT,               BADSOCK_TYPE               },
    { SOCKWRITE,            SOCKWRITE_TXT,             SOCKWRITE_TYPE             },

    { 200,                          "Yukon Base Error",                             ERRTYPESYSTEM   },
    { MemoryError,                  "Memory Error",                                 ERRTYPESYSTEM   },
    { NoMethod,                     "No Method",                                    ERRTYPESYSTEM   },
    { NoRefreshMethod,              "No Refresh Method",                            ERRTYPESYSTEM   },
    { NoGeneralScanMethod,          "No General Scan Method",                       ERRTYPESYSTEM   },
    { NoIntegrityScanMethod,        "No Integrity Scan Method",                     ERRTYPESYSTEM   },
    { NoAccumulatorScanMethod,      "No Accum Scan Method",                         ERRTYPESYSTEM   },
    { NoProcessResultMethod,        "No Process Result Method",                     ERRTYPESYSTEM   },
    { NoExecuteRequestMethod,       "No Exec. Req. Method",                         ERRTYPESYSTEM   },
    { NoResultDecodeMethod,         "No Result Decode Method",                      ERRTYPESYSTEM   },
    { NoErrorDecodeMethod,          "No ErrorDecode Method",                        ERRTYPESYSTEM   },
    { NoHandShakeMethod,            "No Handshake Method",                          ERRTYPESYSTEM   },
    { NoGenerateCmdMethod,          "No Generate Command Method",                   ERRTYPESYSTEM   },
    { NoDecodeResponseMethod,       "No DecodeResponse Method",                     ERRTYPESYSTEM   },
    { NoDataCopyMethod,             "No Data Copy Method",                          ERRTYPESYSTEM   },
    { NotNumeric,                   "Not Numeric",                                  ERRTYPESYSTEM   },
    { NoConfigData,                 "No Config Data Found",                         ERRTYPESYSTEM   },
    { NoRouteGroupDevice,           "No Route for Group Dev.",                      ERRTYPESYSTEM   },
    { NoRoutesInMacro,              "No Routes for Macro Rte",                      ERRTYPESYSTEM   },
    { RouteOffsetOutOfRange,        "Macro Offset does not exist in Macro Rte",     ERRTYPESYSTEM   },
    { SubRouteIsMacro,              "Macro Offset refers to a macro sub-rte",       ERRTYPESYSTEM   },
    { ControlInhibitedOnDevice,     "Device is control disabled",                   ERRTYPESYSTEM   },
    { ControlInhibitedOnPoint,      "Point is control disabled",                    ERRTYPESYSTEM   },
    { ControlRequestComplete,       "Control Completed",                            ERRTYPESYSTEM   },
    { ErrRequestExpired,            "Requested operation expired due to time",      ERRTYPESYSTEM   },
    { ErrorNexusRead,               "Error Reading Nexus",                          ERRTYPESYSTEM   },

    { InThreadTerminated,           "CtiConnection: InThread Terminated",           ERRTYPESYSTEM   },
    { OutThreadTerminated,          "CtiConnection: OutThread Terminated",          ERRTYPESYSTEM   },
    { InboundSocketBad,             "CtiConnection: Inbound Socket Bad",            ERRTYPESYSTEM   },
    { OutboundSocketBad,            "CtiConnection: Outbound Socket Bad",           ERRTYPESYSTEM   },
    { ErrPortInitFailed,            "Port Init Failed",                             ERRTYPESYSTEM   },
    { ErrPortDialupConnect_Port,    "Dialup connection failed. Port in error",      ERRTYPECOMM     },
    { ErrPortDialupConnect_Device,  "Dialup connection failed. Device in error",    ERRTYPESYSTEM   },
    { ErrPortSimulated,             "Port is simulated, no inbound data available", ERRTYPEPROTOCOL },
    { ErrPortEchoResponse,          "Port echoed request bytes",                    ERRTYPECOMM     },

    { RETRY_SUBMITTED,              "Retry Resubmitted",                            ERRTYPESYSTEM   },
    { SCAN_ERROR_DEVICE_INHIBITED,  "Scanned device is inhibited",                  ERRTYPESYSTEM   },
    { SCAN_ERROR_GLOBAL_ADDRESS,    "Illegal scan of global device",                ERRTYPESYSTEM   },
    { SCAN_ERROR_DEVICE_WINDOW_CLOSED,  "Device window is closed",                  ERRTYPESYSTEM   },
    { ErrorPageRS,                  "Invalid transaction, typ. bad pager id or password",   ERRTYPESYSTEM   },
    { ErrorPageNAK,                 "TAP Repeat Requested, but retries exhausted",  ERRTYPESYSTEM   },
    { ErrorPageNoResponse,          "No response from TAP terminal",                ERRTYPESYSTEM   },

    { ErrorHttpResponse,            "Invalid or unsuccessful HTTP response",        ERRTYPESYSTEM   },
    { ErrorXMLParser,               "XML parser initialization failed",             ERRTYPESYSTEM   },
    { ErrorWctpResponse,            "Invalid WCTP response format",                 ERRTYPESYSTEM   },
    { ErrorWctpTimeout,             "Time out when receiving WCTP response",        ERRTYPESYSTEM   },
    { ErrorWctp300Series,           "Protocol Error 300 Series",                    ERRTYPESYSTEM   },
    { ErrorWctp400Series,           "Protocol Error 400 Series",                    ERRTYPESYSTEM   },
    { ErrorWctp500Series,           "Protocol Error 500 Series",                    ERRTYPESYSTEM   },
    { ErrorWctp600Series,           "Protocol Error 600 Series",                    ERRTYPESYSTEM   },

    { ErrorQueuePurged,             "Queue purged to limit memory usage",           ERRTYPESYSTEM   },
    { ErrorRequestCancelled,        "Request was cancelled",                        ERRTYPESYSTEM   },

    { ErrorInvalidTimestamp,        "Invalid time returned OR time outside of requested range.", ERRTYPESYSTEM   },
    { ErrorInvalidChannel,          "Invalid channel returned by daily read.",                   ERRTYPESYSTEM   },

    { ErrorDeviceIPUnknown,         "Device has not reported in, outbound IP unknown",  ERRTYPESYSTEM   },

    { ErrorMACSTimeout,             "MACS timed out on this message",               ERRTYPESYSTEM   },

    { ErrorInvalidFrozenReadingParity,  "The freeze check bit in the frozen reading does not match the last recorded freeze sent to the device.", ERRTYPEPROTOCOL },
    { ErrorInvalidFrozenPeakTimestamp,  "The frozen peak timestamp is outside of the expected range.",                                            ERRTYPEPROTOCOL },
    { ErrorInvalidFreezeCounter,        "The freeze counter is less than the expected value.",                                                    ERRTYPEPROTOCOL },

    { ErrorInvalidData,             "Invalid data was was received for one or more data points.",   ERRTYPEPROTOCOL },

    { ErrorFreezeNotRecorded,       "There is no record of the last freeze sent to this device.",   ERRTYPEPROTOCOL },

    { ErrorInvalidRequest,          "Invalid/Incomplete Request",                   ERRTYPESYSTEM   },

    { ErrorInvalidSSPEC,            "Insufficient SSPEC/Firmware Revision",         ERRTYPESYSTEM   },
    { ErrorVerifySSPEC,             "Verify SSPEC/Firmware Revision",               ERRTYPESYSTEM   },

    { ErrorTransmitterBusy,         "Transmitter is busy",                          ERRTYPEPROTOCOL },
    { ErrorUnsupportedDevice,       "Device Not Supported",                         ERRTYPESYSTEM   },
    { ErrorPortNotInitialized,      "Port not initialized",                         ERRTYPECOMM },

    { ErrorCommandAlreadyInProgress,"Command already in progress",                  ERRTYPESYSTEM   },

    { ErrorDeviceNotConnected,      "Device is not connected",                      ERRTYPESYSTEM   },

    { ErrorNoDisconnect,            "No disconnect configured on this device",      ERRTYPESYSTEM   },

    { ErrorTransmitterOverheating,  "Transmitter is overheating",                   ERRTYPEPROTOCOL },

    { ErrorNeedsChannelConfig,      "Command needs channel config to continue.",    ERRTYPESYSTEM   },

    { UnknownError,                 "Unknown Error",                                ERRTYPESYSTEM   },

};

/* Routine to open up the error file */
INT InitError (void)
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


/* Routine to retrive and print an error message in C */
IM_EX_CTIBASE INT PrintError (USHORT Error)
{
    ERRSTRUCT ESt;

    if( !CtiErrors.findValue( Error, ESt) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Error in Error Message Routine with error " << Error << endl;
        }
        return(!NORMAL);
    }

    /* and print it */
    printf ("Error %04hd:  %s\n", Error, ESt.ErrorMessage);

    return(NORMAL);
}

IM_EX_CTIBASE string FormatError(USHORT Error)
{
    char err[256];

    GetErrorString(Error, err);

    return string(err);
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
