#pragma once

#include "dlldefs.h"
#include <string>

struct ERRSTRUCT {
    INT  Error;
    CHAR ErrorMessage[160];
    INT  Type;
};

/* Prototypes from ERROR.C */
INT SetUnknown( ERRSTRUCT &ESt );

IM_EX_CTIBASE CHAR* GetError (INT err);
IM_EX_CTIBASE INT GetErrorType (INT err);
IM_EX_CTIBASE INT GetError (ERRSTRUCT &);
IM_EX_CTIBASE INT InitError (void);
IM_EX_CTIBASE INT PrintError (USHORT);
IM_EX_CTIBASE INT GetErrorString (USHORT Error, char *ErrStr);
IM_EX_CTIBASE std::string FormatError(USHORT Error);


/* Error TYPE definitions */
#define ERRTYPESYSTEM   1
#define ERRTYPEPROTOCOL      2
#define ERRTYPECOMM     3

#define BLANK_TXT       "Error table entry not defined"
/* Error definitions */
#ifndef NORMAL
   #define NORMAL      0
#endif
#define NORMAL_TXT      "Normal (Success) Return"
#define NORMAL_TYPE     NORMAL
#define NOTNORMAL       1
#define NOTNORMAL_TXT   "Not Normal (Unsuccessful) Return"
#define NOTNORMAL_TYPE  ERRTYPESYSTEM
#define BADBCH          100
#define BADBCH_TXT      "Bad BCH"
#define BADBCH_TYPE     ERRTYPEPROTOCOL
#define NODWORD         2
#define NODWORD_TXT     "No D word"
#define NODWORD_TYPE    ERRTYPEPROTOCOL
#define BADTYPE         3
#define BADTYPE_TXT     "Bad Message Type"
#define BADTYPE_TYPE    ERRTYPEPROTOCOL
#define DLENGTH         4
#define DLENGTH_TXT     "D Word Wrong length"
#define DLENGTH_TYPE    ERRTYPEPROTOCOL
#define BADLOAD         5
#define BADLOAD_TXT     "Bad Load Specification"
#define BADLOAD_TYPE    ERRTYPESYSTEM
#define BADTIME         6
#define BADTIME_TXT     "Bad Time Specification"
#define BADTIME_TYPE    ERRTYPESYSTEM
#define BADLEVEL        7
#define BADLEVEL_TXT    "Bad Level Specification"
#define BADLEVEL_TYPE   ERRTYPESYSTEM
#define BADID           8
#define BADID_TXT       "Bad ID Specification"
#define BADID_TYPE      ERRTYPESYSTEM
#define BADRANGE        9
#define BADRANGE_TXT    "Parameter out of Range"
#define BADRANGE_TYPE   ERRTYPESYSTEM
#define MISPARAM        10
#define MISPARAM_TXT    "Missing Parameter"
#define MISPARAM_TYPE   ERRTYPESYSTEM
#define SYNTAX          11
#define SYNTAX_TXT      "Syntax Error"
#define SYNTAX_TYPE     ERRTYPESYSTEM
#define BADLATCH        12
#define BADLATCH_TXT    "Bad Latch Control Specification"
#define BADLATCH_TYPE   ERRTYPESYSTEM
#define FNI             13
#define FNI_TXT         "Feature Not Implemented"
#define FNI_TYPE        ERRTYPESYSTEM
#define BADSTATE        14
#define BADSTATE_TXT    "Bad State Specification"
#define BADSTATE_TYPE   ERRTYPESYSTEM
#define BADPARITY       15
#define BADPARITY_TXT   "Parity Error"
#define BADPARITY_TYPE  ERRTYPECOMM
#define BADCCU          16
#define BADCCU_TXT      "Bad CCU Specification"
#define BADCCU_TYPE     ERRTYPECOMM
#define NACK1           17
#define NACK1_TXT       "Word 1 NACK"
#define NACK1_TYPE      ERRTYPEPROTOCOL
#define NACK2           18
#define NACK2_TXT       "Word 2 NACK"
#define NACK2_TYPE      ERRTYPEPROTOCOL
#define NACK3           19
#define NACK3_TXT       "Word 3 NACK"
#define NACK3_TYPE      ERRTYPEPROTOCOL
#define NACKPAD1        20
#define NACKPAD1_TXT    "Word 1 NACK Padded"
#define NACKPAD1_TYPE   ERRTYPEPROTOCOL
#define NACKPAD2        21
#define NACKPAD2_TXT    "Word 2 NACK Padded"
#define NACKPAD2_TYPE   ERRTYPEPROTOCOL
#define NACKPAD3        22
#define NACKPAD3_TXT    "Word 3 NACK Padded"
#define NACKPAD3_TYPE   ERRTYPEPROTOCOL
#define BADCCUTYPE      23
#define BADCCUTYPE_TXT  "Bad CCU Type"
#define BADCCUTYPE_TYPE ERRTYPESYSTEM
#define BADCOUNT        24
#define BADCOUNT_TXT    "Bad Repeat Count Specification"
#define BADCOUNT_TYPE   ERRTYPESYSTEM
#define BADPAUSE        25
#define BADPAUSE_TXT    "Bad Pause Interval Specification"
#define BADPAUSE_TYPE   ERRTYPESYSTEM
#define BADPARAM        26
#define BADPARAM_TXT    "Bad Parameter"
#define BADPARAM_TYPE   ERRTYPESYSTEM
#define BADROUTE        27
#define BADROUTE_TXT    "Bad Route Specification"
#define BADROUTE_TYPE   ERRTYPESYSTEM
#define BADBUSS         28
#define BADBUSS_TXT     "Bad Bus Specification"
#define BADBUSS_TYPE    ERRTYPESYSTEM
#define BADAMP          29
#define BADAMP_TXT      "Bad Amp Specification"
#define BADAMP_TYPE     ERRTYPESYSTEM
#define READERR         30
#define READERR_TXT     "Read Error"
#define READERR_TYPE    ERRTYPESYSTEM
#define READTIMEOUT     31
#define READTIMEOUT_TXT "Timeout Reading from Port"
#define READTIMEOUT_TYPE ERRTYPECOMM
#define BADSEQUENCE     32
#define BADSEQUENCE_TXT "Sequence Reject Frame Received... Sequencing Ajusted"
#define BADSEQUENCE_TYPE ERRTYPECOMM
#define FRAMEERR        33
#define FRAMEERR_TXT    "Framing Error"
#define FRAMEERR_TYPE   ERRTYPECOMM
#define BADCRC          34
#define BADCRC_TXT      "Bad CRC on Message"
#define BADCRC_TYPE     ERRTYPECOMM
#define BADLENGTH       35
#define BADLENGTH_TXT   "Bad Length Specification"
#define BADLENGTH_TYPE  ERRTYPESYSTEM
#define BADUA           36
#define BADUA_TXT       "Bad HDLC UA Frame"
#define BADUA_TYPE      ERRTYPECOMM
#define ERRUNKNOWN      37
#define UNKNOWN_TXT     "Unknown Error"
#define UNKNOWN_TYPE    ERRTYPESYSTEM
#define BADADDRESS      38
#define BADADDRESS_TXT  "Bad Unique Repeater Address"
#define BADADDRESS_TYPE ERRTYPESYSTEM
#define BADROLE         39
#define BADROLE_TXT     "Bad Repeater Role Number"
#define BADROLE_TYPE    ERRTYPESYSTEM
#define INVALIDFIX      40
#define INVALIDFIX_TXT  "Invalid Repeater Fixed Number"
#define INVALIDFIX_TYPE ERRTYPESYSTEM
#define INVALIDVOU      41
#define INVALIDVOU_TXT  "Invalid Repeater Out Value"
#define INVALIDVOU_TYPE ERRTYPESYSTEM
#define INVALIDVIN      42
#define INVALIDVIN_TXT  "Invalid Repeater In Value"
#define INVALIDVIN_TYPE ERRTYPESYSTEM
#define INVALIDSTG      43
#define INVALIDSTG_TXT  "Invalid Repeater Stages"
#define INVALIDSTG_TYPE ERRTYPESYSTEM
#define BADFILE         45
#define BADFILE_TXT     "Bad or Missing File"
#define BADFILE_TYPE    ERRTYPESYSTEM
#define REQACK          46
#define REQACK_TXT      "REQACK Flag set-- Frame Unexecutable"
#define REQACK_TYPE     ERRTYPECOMM
#define RTFERR          47
#define RTFERR_TXT      "Route File Error"
#define RTFERR_TYPE     ERRTYPESYSTEM
#define NOTR            48
#define NOTR_TXT        "No Time Routes Found"
#define NOTR_TYPE       ERRTYPESYSTEM
#define RTNF            49
#define RTNF_TXT        "Route Not Found"
#define RTNF_TYPE       ERRTYPESYSTEM
#define FNO             50
#define FNO_TXT         "File Not Open"
#define FNO_TYPE        ERRTYPESYSTEM
#define RONF            51
#define RONF_TXT        "Role Not Found"
#define RONF_TYPE       ERRTYPESYSTEM
#define ROFERR          52
#define ROFERR_TXT      "Role File Error"
#define ROFERR_TYPE     ERRTYPESYSTEM
#define DBFERR          53
#define DBFERR_TXT      "DataBase File Error"
#define DBFERR_TYPE     ERRTYPESYSTEM
#define IDNF            54
#define IDNF_TXT        "ID Not Found"
#define IDNF_TYPE       ERRTYPESYSTEM
#define TYFERR          55
#define TYFERR_TXT      "Type File Error"
#define TYFERR_TYPE     ERRTYPESYSTEM
#define TYNF            56
#define TYNF_TXT        "Function and/or Type Not Found"
#define TYNF_TYPE       ERRTYPESYSTEM
#define EWORDRCV        57
#define EWORDRCV_TXT    "E-Word Received in Returned Message"
#define EWORDRCV_TYPE   ERRTYPEPROTOCOL
#define BADFILL         58
#define BADFILL_TXT     "Error Filling Fill Area of Command"
#define BADFILL_TYPE    ERRTYPESYSTEM
#define SYSTEM          59
#define SYSTEM_TXT      "OS or System Error"
#define SYSTEM_TYPE     ERRTYPESYSTEM
#define BADPORT         60
#define BADPORT_TXT     "Bad Port Specification"
#define BADPORT_TYPE    ERRTYPESYSTEM
#define QUEUE_READ      61
#define QUEUE_READ_TXT  "Error Reading Queue"
#define QUEUE_READ_TYPE ERRTYPESYSTEM
#define QUEUE_WRITE     62
#define QUEUE_WRITE_TXT "Error Writing Queue"
#define QUEUE_WRITE_TYPE ERRTYPESYSTEM
#define MEMORY          63
#define MEMORY_TXT      "Error Alocating or Manipulating Memory"
#define MEMORY_TYPE     ERRTYPESYSTEM
#define SEMAPHORE       64
#define SEMAPHORE_TXT   "Error Handling Semaphore"
#define SEMAPHORE_TYPE  ERRTYPESYSTEM
#define NODCD           65
#define NODCD_TXT       "No DCD on Return Message"
#define NODCD_TYPE      ERRTYPECOMM
#define WRITETIMEOUT     66
#define WRITETIMEOUT_TXT "Timeout Writing to Port"
#define WRITETIMEOUT_TYPE ERRTYPECOMM
#define PORTREAD        67
#define PORTREAD_TXT    "Error Reading from Port"
#define PORTREAD_TYPE   ERRTYPECOMM
#define PORTWRITE       68
#define PORTWRITE_TXT   "Error Writing to Port"
#define PORTWRITE_TYPE  ERRTYPECOMM
#define PIPEWRITE       69
#define PIPEWRITE_TXT   "Error Writing to Named Pipe"
#define PIPEWRITE_TYPE  ERRTYPESYSTEM
#define PIPEREAD        70
#define PIPEREAD_TXT    "Error Reading from Named Pipe"
#define PIPEREAD_TYPE   ERRTYPESYSTEM
#define QUEUEEXEC       71
#define QUEUEEXEC_TXT   "Error Executing CCU Queue Entry"
#define QUEUEEXEC_TYPE  ERRTYPEPROTOCOL
#define DLCTIMEOUT      72
#define DLCTIMEOUT_TXT  "DLC Read Timeout on CCU Queue Entry"
#define DLCTIMEOUT_TYPE ERRTYPEPROTOCOL
#define NOATTEMPT       73
#define NOATTEMPT_TXT   "No Attempt Made on CCU Queue Entry"
#define NOATTEMPT_TYPE  ERRTYPESYSTEM
#define ROUTEFAILED     74
#define ROUTEFAILED_TXT "Route Failed on CCU Queue Entry"
#define ROUTEFAILED_TYPE ERRTYPEPROTOCOL
#define TRANSFAILED     75
#define TRANSFAILED_TXT "Transponder Communication Failed on CCU Queue Entry"
#define TRANSFAILED_TYPE ERRTYPEPROTOCOL
#define JWORDRCV        76
#define JWORDRCV_TXT    "J-Word Received in Returned Message"
#define JWORDRCV_TYPE   ERRTYPEPROTOCOL
#define NOREMOTEPORTER  77
#define NOREMOTEPORTER_TXT  "Remote Porter Can Not be Reached"
#define NOREMOTEPORTER_TYPE  ERRTYPESYSTEM
#define REMOTEINHIBITED  78
#define REMOTEINHIBITED_TXT   "Communications Attempted With Inhibited Remote"
#define REMOTEINHIBITED_TYPE  ERRTYPESYSTEM
#define QUEUEFLUSHED    79
#define QUEUEFLUSHED_TXT     "CCU Queue was Flushed... Entries Lost in Drain"
#define QUEUEFLUSHED_TYPE    ERRTYPESYSTEM
#define PIPEBROKEN      80
#define PIPEBROKEN_TXT       "Pipe Connect is Broken"
#define PIPEBROKEN_TYPE      ERRTYPESYSTEM
#define PIPEWASBROKEN   81
#define PIPEWASBROKEN_TXT    "Pipe Connect was Broken"
#define PIPEWASBROKEN_TYPE   ERRTYPESYSTEM
#define PIPEOPEN                82
#define PIPEOPEN_TXT            "Pipe Not Opened"
#define PIPEOPEN_TYPE           ERRTYPESYSTEM
#define PORTINHIBITED           83
#define PORTINHIBITED_TXT       "Communications Attempted Over Inhibited Port"
#define PORTINHIBITED_TYPE      ERRTYPESYSTEM
#define ACCUMSNOTSUPPORTED      84
#define ACCUMSNOTSUPPORTED_TXT  "Device Does Not Support Accumulaors"
#define ACCUMSNOTSUPPORTED_TYPE ERRTYPESYSTEM
#define DEVICEINHIBITED         85
#define DEVICEINHIBITED_TXT     "Operation Attempted on Inhibited Device"
#define DEVICEINHIBITED_TYPE    ERRTYPESYSTEM
#define POINTINHIBITED          86
#define POINTINHIBITED_TXT      "Operation Attempted on Inhibited Point"
#define POINTINHIBITED_TYPE     ERRTYPESYSTEM
#define DIALUPERROR             87
#define DIALUPERROR_TXT         "Error Dialing Up Remote"
#define DIALUPERROR_TYPE        ERRTYPECOMM
#define WRONGADDRESS            88
#define WRONGADDRESS_TXT  "Wrong Unique Address Received"
#define WRONGADDRESS_TYPE ERRTYPECOMM
#define TCPCONNECTERROR         89
#define TCPCONNECTERROR_TXT     "Error Connecting to TCP socket"
#define TCPCONNECTERROR_TYPE    ERRTYPECOMM
#define TCPWRITEERROR           90
#define TCPWRITEERROR_TXT       "Error Writing to TCP socket"
#define TCPWRITEERROR_TYPE      ERRTYPECOMM
#define TCPREADERROR            91
#define TCPREADERROR_TXT        "Error Reading from TCP socket"
#define TCPREADERROR_TYPE       ERRTYPECOMM
#define ADDRESSERROR            92
#define ADDRESSERROR_TXT        "Address Does Not Match Expected Value"
#define ADDRESSERROR_TYPE       ERRTYPESYSTEM
#define ALPHABUFFERERROR        93
#define ALPHABUFFERERROR_TXT    "Bad Data Buffer for IED"
#define ALPHABUFFERERROR_TYPE   ERRTYPESYSTEM
#define MISCONFIG               94
#define MISCONFIG_TXT           "Missing Required Configuration Entry"
#define MISCONFIG_TYPE          ERRTYPESYSTEM

#define BADSOCK                 98
#define BADSOCK_TXT             "Bad Nexus Specification"
#define BADSOCK_TYPE            ERRTYPESYSTEM
#define SOCKWRITE               99
#define SOCKWRITE_TXT           "Error Writing to Nexus"
#define SOCKWRITE_TYPE          ERRTYPESYSTEM

