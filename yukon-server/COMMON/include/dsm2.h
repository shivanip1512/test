/*-----------------------------------------------------------------------------*
*
* File:   DSM2
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/DSM2.H-arc  $
* REVISION     :  $Revision: 1.30 $
* DATE         :  $Date: 2005/07/29 16:26:02 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef DSM2_H
#define DSM2_H
#pragma warning( disable : 4786)


#include <windows.h>

#include <time.h>
#pragma pack(push, message_packing, 1)
#include "mutex.h"
#include "guard.h"
#include "dlldefs.h"

class CTINEXUS;

extern CtiMutex gOutMessageMux;
extern ULONG gOutMessageCounter;

IM_EX_CTIBASE void incrementCount();
IM_EX_CTIBASE void decrementCount();
IM_EX_CTIBASE ULONG OutMessageCount();


#define STANDNAMLEN           20
#define MAX_VERSACOM_MESSAGE  40

/* Error TYPE definitions */
#define ERRTYPESYSTEM   1
#define ERRTYPEPROTOCOL      2
#define ERRTYPECOMM     3

#define ERRLOGENTS      10

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
#define TCPCONNECTERROR_TXT     "Error Connecting to Terminal Server"
#define TCPCONNECTERROR_TYPE    ERRTYPECOMM
#define TCPWRITEERROR           90
#define TCPWRITEERROR_TXT       "Error Writing to Terminal Server"
#define TCPWRITEERROR_TYPE      ERRTYPECOMM
#define TCPREADERROR            91
#define TCPREADERROR_TXT        "Error Reading from Terminal Server"
#define TCPREADERROR_TYPE       ERRTYPECOMM
#define ADDRESSERROR            92
#define ADDRESSERROR_TXT        "Address Does Not Match Expected Value"
#define ADDRESSERROR_TYPE       ERRTYPESYSTEM
#define ALPHABUFFERERROR        93
#define ALPHABUFFERERROR_TXT    "Bad Data Buffer for IED"
#define ALPHABUFFERERROR_TYPE   ERRTYPESYSTEM

#define BADSOCK                 98
#define BADSOCK_TXT             "Bad Nexus Specification"
#define BADSOCK_TYPE            ERRTYPESYSTEM
#define SOCKWRITE               99
#define SOCKWRITE_TXT           "Error Writing to Nexus"
#define SOCKWRITE_TYPE          ERRTYPESYSTEM


#define MAXPRIORITY     15

#ifndef FLOAT
   #define FLOAT float
#endif

#ifndef DOUBLE
   #define DOUBLE double
#endif

/* Definitions for timing routine */
#define T_AWORD         0
#define T_BWORD         1
#define T_GWORD         2

/* Port Definitions */
#define PORTMAX         120
#define MAXPORT         (PORTMAX+1)
#define MAXPROC         52

/* Misc. definitions */
#define MAXTHREADS      100
#define REVERSE         1
#define COMSIZE         132
#define NUMBUF          10
#define MAXIDLC         128
#define CCUMAX_700      3
#define CCUMAX_710      31
#define CCUMAX_711      99
#define CCUGLOBAL       127
#define RTUGLOBAL       126
#define TRACE_ON        1
#define TRACE_OFF       0
#define DELAY_ABORT     -1
#define NOMORE          -1
#define TIMEOUT         5
#define CORNTIMEOUT     20
#define PREAMLEN        3
#define REP_FIXED       0
#define REP_VAR         1
#define PORT_NUM        2
#define STAGES          3
#define CCULOOP         8+2
#define CCUREV          8+4
#define OLD_CRC         0
#define NEW_CRC         1
#define IDLC            1


/* A-word function definitions */

#define AWORDLEN        4
#define A_RESTORE       0
#define A_SHED_A        1
#define A_SHED_B        2
#define A_SHED_C        3
#define A_SHED_D        4
#define A_LATCH_OPEN    5
#define A_LATCH_CLOSE   6
#define A_SCRAM         7

/* B-word function definitions */
#define BWORDLEN        7
#define B_RESTORE       0
#define B_MASK_A        0x01
#define B_MASK_B        0x02
#define B_MASK_C        0x04
#define B_MASK_D        0x08
#define B_SCRAM         0x0f
#define B_GETSTATUS     0X3E
#define B_GETLOAD       0x35
#define B_GETARM        0x30
#define B_GETADD        0x28
#define B_GETREV        0x00
#define METER           1
#define PI_1            2
#define PI_2            3
#define PI_3            4
#define ANALOG1         5
#define ANALOG2         6
#define ANALOG3         7
#define ANALOG4         8
#define BATT            0x1f
#define B_UNI           0x22
#define CWORDLEN        7
#define DWORDLEN        7
#define RESET_GN        0x50
#define RESET_OV        0x57
#define RESET_TA        0x58


/* Time definitions */

#define TIME_7_5        0
#define TIME_15         1
#define TIME_30         2
#define TIME_60         3

/* Address definitions */
#define UNIQUE_BASE     0L
#define GOLD_BASE       60
#define SILVER_BASE     0
#define BRONZE_BASE     0x3ff000L
#define LEAD_LOAD_BASE  0x3fd000L
#define LEAD_DATA_BASE  0x3fe000L
#define UNIQUE_MAX      0x3fffffL
#define GOLD_MAX        4
#define SILVER_MAX      60
#define BRONZE_MAX      256
#define LEAD_MAX        4096
#define FCT_ADDRESS     0x155555L
#define UNIV_ADDRESS    0x3fffffL
#define RPT_UN_MIN      4190464L
#define READ_ST         0x40
#define READ_RE         0xff

typedef union
{
    UCHAR    ch[2];
    USHORT   sh;
} BYTEUSHORT;

typedef union
{
    UCHAR    ch[4];
    ULONG    ul;
} BYTEULONG;


/* Structure defintions */
typedef enum {
  CtiEventType = 0,
  CtiMutexType

} CtiSyncType_t;

typedef struct {
   CtiSyncType_t  type;
   BOOL           manualReset;
   BOOL           initState;
   CHAR *         syncObjName;
} CtiSyncDefStruct;

/*
 *  DLCROUTE is the route used to "exit" the CCU on the powerline,
 *  not to be confused by our other 72 uses of the word route
 */
typedef struct
{
  USHORT Amp;       // From Route and Transmitter Data.
  USHORT Feeder;
  USHORT Stages;
  USHORT RepFixed;
  USHORT RepVar;

} DLCROUTE;

typedef struct _ASTRUCT
{
  // USHORT Priority;
  // USHORT Retry;

   USHORT   Port;
   USHORT   Remote;

   DLCROUTE DlcRoute;

   USHORT Group;     // Addressing must be zero through 63
   USHORT Time;      // From the request
   USHORT Function;  // Which relay or zero for a zero. From the group record etc..
} ASTRUCT;

typedef struct _BSTRUCT
{
   // USHORT Priority;
   // USHORT Retry;
   // USHORT Sequence;

   USHORT   Port;             // This is the port the remote transmitter device is connected to.
   USHORT   Remote;           // This is the DeviceID of the transmitter device (CCU etc)

   DLCROUTE DlcRoute;         // The data here is used by the word builder and comes from the route

   // DLC Identifications
   ULONG  Address;            // This is the DLC address of the DLC device
   INT    SSpec;              // S-Spec of the device.
   INT    DeviceType;         // devicetypes.h devicetype of the mct.

   USHORT Function;           // Indicates the desired operation on the DLC device
   USHORT Length;             // This is the byte count expected from the DLC device based upon the request.
   BYTE   Message[36];
   USHORT IO;                 // Input or Outout? In its basic form this is a 2 bit indicator of Cti::Protocol::Emetcon::IO_Write, ::IO_Read, ::IO_Function_Write, ::IO_Function_Read
                              //    At other times additional bits are attached and stuffed in B_Word (primarily ARM bits?)
} BSTRUCT;

typedef struct
{
   BYTE PRE[1];         /* * or - depending on recipient */
   BYTE UID[3];
   BYTE VID[3];
   BYTE D[2];
   BYTE F[2];
   BYTE ADDRS[7];
   BYTE GRP[4];
   BYTE VALUE[6];
   BYTE POST[1];        /* * or - depending on recipient */
} FPPCCST;

typedef struct _FPSTRUCT
{
   USHORT   Function;
   USHORT   Length;

   union
   {
      BYTE     Message[36];
      FPPCCST  PCC;
   } u;
} FPSTRUCT;

typedef struct DSTRUCT
{
   //USHORT   Sequence;
   //LONG     DeviceID;
   //LONG     PointID;

   ULONG    Time;
   USHORT   Length;
   BYTE     Message[36];
   USHORT   RepVar;
   ULONG    Address;
   USHORT   Power;
   USHORT   Alarm;
   USHORT   TSync;
   USHORT   DSTFlag;
} DSTRUCT;

typedef struct _ESTRUCT
{
   //USHORT Sequence;
   //CHAR DeviceName[STANDNAMLEN];
   //CHAR PointName[STANDNAMLEN];

   BYTE     Message[36];
   USHORT   RepVar;
   ULONG    Address;
   USHORT   Power;
   USHORT   Alarm;
} ESTRUCT;

/* Structure used for ripple */
typedef struct _RSTRUCT
{
   // USHORT Port;
   // USHORT Remote;
   // USHORT Priority;
   // USHORT Retry;

   BYTE Message[7];
} RSTRUCT;


/* Structure used for REMS 100 */
typedef struct _REMSSTRUCT
{
   //USHORT Port;
   //USHORT Remote;
   //USHORT Priority;
   //USHORT Retry;
   USHORT AddressHigh;
   USHORT AddressLow;
   USHORT Function;
} REMSSTRUCT;

/* Structures used for VERSACOM */
typedef struct _VRELAY
{
   USHORT ControlType;    /* 0 = Cycling, 1 = Discrete */
   USHORT Relay[3];       /* 0 = Relay Not Affected, !0 = Relay Affected */
   USHORT TimeCode;       /* Versacom time code */
} VRELAY;

typedef struct _VERELAY
{
   USHORT ControlType;
   USHORT CycleType;
   USHORT Relay;
   SHORT  Percent;
   ULONG  ControlTime;
   ULONG  RandomTime;
   USHORT Window;
   USHORT Count;
   ULONG  DelayUntil;
} VERELAY;

typedef struct _VDATA
{
   INT      DataLength;
   BYTE     Data[30];
   USHORT   DataType;
} VDATA;

typedef struct _VCONFIG
{
   USHORT ConfigType;
   BYTE Data[6];
} VCONFIG;

typedef struct
{
   BOOL     Cancel;     // If set, this command cancels a prior TOOS command
   BOOL     LED_Off;    // If set, the LEDs are off during the TOOS.
   USHORT   TOOS_Time;  // TOOS time in half seconds.

} VESERVICE;

typedef struct _VSTRUCT
{
   INT      Retry;

   USHORT   CommandType;

   USHORT   UtilityID;
   USHORT   Section;
   USHORT   Class;
   USHORT   Division;
   ULONG    Address;             // This is the serial number of the switch!

   INT      RelayMask;           // 022200 CGP bitmask for relays in control! indicates relay, or relays used in a control operation

   DLCROUTE DlcRoute;            // Used iff this is to be wrapped in emetcon!...

   union {
      USHORT  Arg;               // This is an alias for all USHORT members!
      USHORT  Initiator;
      USHORT  Service;
      USHORT  PropDIT;
      USHORT  CountReset;
      VDATA   VData;
      VRELAY  Load[3];
      VERELAY ELoad;
      VCONFIG VConfig;
      VESERVICE ExService;
   };

   USHORT  NumW;
   USHORT  Nibbles;

   BYTE   Message[MAX_VERSACOM_MESSAGE];      // Added 013100 CGP

   CHAR   Action[30];                         // Added 041400 CGP for logging purposes

} VSTRUCT;

typedef struct _CSTRUCT
{
   // LONG        DeviceID;                     // 083199 CGP
   // LONG        PointID;                      // 083199 CGP
   FLOAT Value;
} CSTRUCT;


typedef struct _TAPSTRUCT
{
   // CHAR CapCode[STANDNAMLEN];
   CHAR Message[256];
   ULONG Length;
} TAPSTRUCT;

/*
 * The Dialup structures
 */

typedef struct
{
   /* Elements from BSTRUCT */
   // LONG        PortID;                       // 083199 CGP
   // LONG        DeviceID;                     // 083199 CGP
   // INT         Priority;
   // INT         Sequence;

   // USHORT      Retry;
   ULONG       Address;
   /* END - Elements from BSTRUCT */

   USHORT      Identity;
   USHORT      Command[5];
   ULONG       Request[5];
   USHORT      ReadCode;
   LONG        LastFileTime;                // Load Profile Time
   LONG        LP_Time;                     // Load Profile Time
   BYTE        Message[256];

   ULONG       Time;                        /* Porter fills */
   USHORT      DSTFlag;
} DIALUPREQUEST;

typedef struct
{
   DIALUPREQUEST     Request;

   FLOAT             FResponse[100];
   USHORT            IResponse[100];
   BYTE              SResponse[36];
} DIALUPSTRUCT;

typedef struct
{
   DIALUPREQUEST ReqSt;                    /* The original Request made somewhere */
   BYTE          Status;                   /* Device Status Byte */
   BYTE          AckNak;                   /* Last device response to comms */
   USHORT        CompFlag;                 /* Indicates progress through the various commands */
   BYTE          Message[2048];            /* Data is in here */
} DIALUPREPLY;

/*----------------------------------------------------------------------------*
 * Any and all byte sensitive boundaries should be protected within the
 * confines of these pack pragmas....  Please use your head in regards to
 * making things require this though....
 *----------------------------------------------------------------------------*/

#define COMMAND_STR_SIZE 255
typedef struct
{
   INT      BuildIt;          // 022801 CGP If !FALSE porter will analyze the CommandStr and make his own assumptions.

   VOID     *Connection;
   CHAR     CommandStr[COMMAND_STR_SIZE + 1];   // First COMMAND_STR_SIZE characters of the request string.
   INT      RouteID;          // The ID of the route which is currently being addressed
   INT      MacroOffset;      // On request, the macro offset to try.  On reply, the macro offset to try next (or zero if no more exist)
   INT      Attempt;          // On request, the number of attempts to make for this route specification. On reply, the number of attempts remaining.
   INT      SOE;              // SOE id added by client, or by pil on receipt.
   LONG     TrxID;            // Transmission ID
   LONG     UserID;           // User ID
   ULONG    CheckSum;         // 071300 CGP CheckSum generated by getUniqueIdentifier() used to match "like-connected" devices in the porter queues

} PIL_ECHO;                   // Data echo'ed through porter fro the PIL.

// 061903 Added to support the gateway product.
typedef struct
{
    INT Type;
    INT Length;
    BYTE MsgData[256];

} GWSTRUCT;

typedef struct
{
    INT Type;               // Type of response
    INT Data[10];
    DOUBLE FPData[10];
    BYTE MsgData[2048];

} GWRESPONSESTRUCT;

#define MAX_SA_MSG_SIZE 256

typedef struct
{
    BYTE _buffer[MAX_SA_MSG_SIZE];      // This MUST be first in the struct.
    int _bufferLen;                     // This is the valid size of the prepared _buffer

    INT  _commandType;
    BYTE _lbt;
    BYTE _delayToTx;                    // Time in seconds before transmitting codes.
    BYTE _maxTxTime;                    // Maximum Time in seconds to transmit codes.

    BYTE _mark;                         // SA Digital mark
    BYTE _space;                        // SA Digital space

    int _transmitterAddress;            // The address of the RTC, or RTU.
    int _groupType;                     // This must be one of the supported DCU types in the lib.. ie. SA205, GOLAY...

    BOOL _retransmit;                   // DCU205 groups use this bool to determine that this is a retransmission.
    BOOL _shed;                         // NON DCU205 groups use this bool to determine shed or restore.
    int _function;                      // This is the function to execute on the DCU....  Should be directly applied in the switch.

    int _code205;                       // This is the code to transmit iff this is an SA205 DCU type group.
    CHAR _codeSimple[7];                // This is the code to transmit iff this is NOT an SA205 DCU type group.
    CHAR _serial[33];                   // This is a 205/305 serial number.
    CHAR _code305[128];                 // This is a 305 message.

    // The parameters below are assigned typically by the parse object
    int _swTimeout;                     // Switch OFF time in seconds.
    int _cycleTime;                     // Switch on + off time in seconds.
    int _repeats;                       // Number of _cycleTimes to repeat the operation (DCU205)
    int _sTime;                         // swTimeout as needed by the protocol
    int _cTime;                         // cycleTime needed by the protocol



} CtiSAData;

/* queing  structures used by protected mode */
typedef class CtiOutMessage
{
public:

   BYTE               HeadFrame[2];                 // 082702 CGP    // Hey, it should have been in there for a long time!
   LONG               DeviceID;                     // 083199 CGP    // The device id of the transmitter device.
   LONG               TargetID;                     // 022701 CGP    // The device id of the end-of-line device. May be the same as DeviceID depending on protocol
   USHORT             Sequence;                     // 083199 CGP    // Used by CCU711 to id a CCU queue entry.
   LONG               VerificationSequence;         // 072104 mskf   // Used to track multiple OutMessage requests for the same code
                                                                     //   transmission, esp. for the purpose of logging retry success

   INT                Retry;                        // Instructions to porter!
   INT                Priority;
   INT                TimeOut;

   INT                Port;
   INT                Remote;

   ULONG              OutLength;     // We have 300 bytes available
   ULONG              InLength;      // We have about 4kB available.

   UINT               Source;
   UINT               Destination;
   UINT               Command;
   UINT               Function;
   UINT               EventCode;
   UINT               MessageFlags;

   LONG               DeviceIDofLMGroup;           // 091300 CGP Helps us track lm command's success
   UINT               TrxID;                       // 091300 CGP Helps us track lm command's success

   ULONG              ExpirationTime;               // 102703 CGP.  The PortThread will discard this message if it tries to execute after this time (and it is non-zero).

   PIL_ECHO           Request;

   CTINEXUS           *ReturnNexus;   // Connection back to requestor.
   CTINEXUS           *SaveNexus;
   union _outmess_buf
   {
      BYTE            OutMessage[300];
      ASTRUCT         ASt;
      BSTRUCT         BSt;
      FPSTRUCT        FPSt;
      RSTRUCT         RSt;
      VSTRUCT         VSt;
      REMSSTRUCT      RemsSt;
      CSTRUCT         CSt;
      TAPSTRUCT       TAPSt;
      DIALUPREQUEST   DUPReq;
      GWSTRUCT        GWSt;
      CtiSAData       SASt;
   } Buffer;
   BYTE               TailFrame[2];               // 082702 CGP    // Hey, it should have been in there for a long time!

public:

   CtiOutMessage()
   {
      memset(this, 0, sizeof(CtiOutMessage));
      ReturnNexus = NULL;
      SaveNexus = NULL;

      HeadFrame[0] = 0x02;      // STX
      HeadFrame[1] = 0xe0;
      TailFrame[0] = 0xea;
      TailFrame[1] = 0x03;      // ETX

      incrementCount();
   }

   CtiOutMessage(const CtiOutMessage &aRef)
   {
      *this = aRef;
      incrementCount();
   }

   ~CtiOutMessage()
   {
      decrementCount();
   }

   CtiOutMessage& operator=(const CtiOutMessage &aRef)
   {
      if(this != &aRef)
      {
         HeadFrame[0]           = aRef.HeadFrame[0];
         HeadFrame[1]           = aRef.HeadFrame[1];
         DeviceID               = aRef.DeviceID;
         TargetID               = aRef.TargetID;
         Sequence               = aRef.Sequence;
         VerificationSequence   = aRef.VerificationSequence;

         Retry                  = aRef.Retry;
         Priority               = aRef.Priority;
         TimeOut                = aRef.TimeOut;

         Port                   = aRef.Port;
         Remote                 = aRef.Remote;

         OutLength              = aRef.OutLength;
         InLength               = aRef.InLength;

         Source                 = aRef.Source;
         Destination            = aRef.Destination;
         Command                = aRef.Command;
         Function               = aRef.Function;
         EventCode              = aRef.EventCode;
         MessageFlags           = aRef.MessageFlags;

         DeviceIDofLMGroup      = aRef.DeviceIDofLMGroup;
         TrxID                  = aRef.TrxID;

         ExpirationTime         = aRef.ExpirationTime;

         Request                = aRef.Request;

         ReturnNexus            = aRef.ReturnNexus;
         SaveNexus              = aRef.SaveNexus;

         Buffer                 = aRef.Buffer;

         TailFrame[0]           = aRef.TailFrame[0];
         TailFrame[1]           = aRef.TailFrame[1];
      }

      return *this;
   }

   bool operator<( const CtiOutMessage &rhs ) const
   {
       return Priority > rhs.Priority; // Bigger is sorted first in terms of priority.
   }

} OUTMESS;


typedef class CTIINMESS
{
public:

   LONG        DeviceID;                     // 083199 CGP     // The device id of the transmitter device.
   LONG        TargetID;                     // 022701 CGP     // The device id of the end-of-line device. May be the same as DeviceID depending on protocol
   LONG        RouteID;                      // 083199 CGP
   INT         Sequence;                     // 083199 CGP

   INT         Port;
   INT         Remote;

   ULONG       Time;
   UINT        MilliTime;

   UINT        EventCode;
   UINT        MessageFlags;
   UINT        Priority;
   ULONG       InLength;

   PIL_ECHO    Return;

   CTINEXUS    *ReturnNexus;   // Connection back to requestor.
   CTINEXUS    *SaveNexus;

   LONG        DeviceIDofLMGroup;           // 091300 CGP Helps us track lm command's success
   UINT        TrxID;                       // 091300 CGP Helps us track lm command's success

   BYTE        IDLCStat[18];          /* do NOT put anything between (IDLCStat & Buffer.Message) these */
   union _inmess_buf
   {
      BYTE            InMessage[4096];    /* two variables !!!!!! */
      DSTRUCT         DSt;
      struct
      {
         DSTRUCT      DSt;              // This looks odd, but it lies on top of the one above.. Don't pull it out!
         DIALUPREPLY  DUPRep;
      } DUPSt;
      GWRESPONSESTRUCT GWRSt;
   } Buffer;

public:

    CTIINMESS()
    {
       memset(this, 0, sizeof(CTIINMESS));
    }

} INMESS;

#define PEXEC_DEVID        -1;
#define PORTERSU_DEVID     -2;
#define PORTPIPE_DEVID     -3;
#define PORTPORT_DEVID     -4;
#define SCANNER_DEVID      -5;


/* Definitions for the statistics system */
typedef struct _STATS
{
   ULONG   FailureCount;
   USHORT  ErrorDSTFlags;
   ULONG   LastGoodTime;
   ULONG   ErrorResetTime;
   ULONG   ErrorLog[ERRLOGENTS + 1];
   ULONG   Error24ResetTime;
   ULONG   Error24Log[ERRLOGENTS + 1];
   ULONG   ErrorPrev24ResetTime;
   ULONG   ErrorPrev24Log[ERRLOGENTS + 1];
   ULONG   Error24RollTime;
   ULONG   Error24Roll[24][ERRLOGENTS + 1];
} STATS;

#define LASTGOODDSTMASK             0x0001
#define ERRORRESETDSTMASK           0x0002
#define ERROR24RESETDSTMASK         0x0004
#define ERRORPREV24RESETDSTMASK     0x0008
#define ERROR24ROLLDSTMASK          0x0010
#define CONSECUTIVEFAILURE          0X0020
#define PERCENTLOW                  0x0040
#define PERCENT24LOW                0x0080
#define FIRSTPROCESSED              0X0100

/* Prototypes from UCTTime.C */
IM_EX_CTIBASE int            UCTSetFTime (struct timeb *);
IM_EX_CTIBASE int            UCTFTime (struct timeb *);
IM_EX_CTIBASE struct tm *    UCTLocalTime (time_t, USHORT);               //
IM_EX_CTIBASE time_t         UCTMakeTime (struct tm *);
IM_EX_CTIBASE int            Holiday (struct timeb *);
IM_EX_CTIBASE ULONG          LongTime (VOID);
IM_EX_CTIBASE ULONG          DSTFlag (VOID);
IM_EX_CTIBASE void           UCTLocoTime (time_t, USHORT, struct tm *);
IM_EX_CTIBASE void           UCTAsciiTime (time_t, USHORT, PCHAR);
IM_EX_CTIBASE PCHAR          UCTAscTime (time_t, USHORT);
IM_EX_CTIBASE ULONG          setNextInterval(time_t, ULONG);
IM_EX_CTIBASE int            FindMidNight (time_t *, USHORT, time_t *);
IM_EX_CTIBASE time_t         MidNight (time_t, USHORT);
IM_EX_CTIBASE time_t         MidNightWas (time_t, USHORT);
IM_EX_CTIBASE int            MakeDailyTime (ULONG, PCHAR);
IM_EX_CTIBASE int            MakeCtrlHours (ULONG, PCHAR);
IM_EX_CTIBASE int            MakeAsciiDate (ULONG TimeStamp, USHORT DstFlag, PCHAR MyDate);
IM_EX_CTIBASE int            MakeAsciiTime (ULONG TimeStamp, USHORT DstFlag, PCHAR MyDate);
IM_EX_CTIBASE int            GetCurrentSeason (void);
IM_EX_CTIBASE int            PutDSTTime (ULONG);
IM_EX_CTIBASE int            GetDSTTime (VOID);

/* load control season defines for UCTTime function */
#define SEASON_SUMMER  1
#define SEASON_WINTER  2
#define SEASON_FILE_NAME "DATA\\SEASON.DAT"

typedef struct _DSM2_SEASON
{
   SHORT StartMonth;
   SHORT StartDay;
   SHORT StopMonth;
   SHORT StopDay;
} DSM2_SEASON;

/* DST active flag */
#define DSTACTIVE           0x8000
#define DSTSET(q)  ((DSTFlag ()) ? (q | DSTACTIVE) : (q & ~DSTACTIVE))

/* Prototypes from PLOTROLL.C */
int            IM_EX_CTIBASE RollOverPlotData (PCHAR, PCHAR);


#define RETURN_THREAD_STACK_SIZE 8192

/* Prototypes from PEXEC.C */
void           IM_EX_CTIBASE resetOutMess(OUTMESS*);

int            IM_EX_CTIBASE xIOInit (PCHAR);
int            IM_EX_CTIBASE PortPipeInit (USHORT);
void           IM_EX_CTIBASE PortPipeCleanup (ULONG Reason);

int            IM_EX_CTIBASE nrexec (RSTRUCT *);
int            IM_EX_CTIBASE nvexec (VSTRUCT *);
int            IM_EX_CTIBASE naexec (ASTRUCT *);
int            IM_EX_CTIBASE nb1exec (BSTRUCT *);
int            IM_EX_CTIBASE nb2exec (BSTRUCT *, DSTRUCT *);
int            IM_EX_CTIBASE nb2execsq (BSTRUCT *);
int            IM_EX_CTIBASE nb2execs (BSTRUCT *);
int            IM_EX_CTIBASE nd2execs (DIALUPSTRUCT *DUPOst);
int            IM_EX_CTIBASE nd2execr (DSTRUCT *DSt, DIALUPSTRUCT *DUPst);
int            IM_EX_CTIBASE nd1execx (DIALUPSTRUCT *);
int            IM_EX_CTIBASE QueueFunctionRead (BSTRUCT *, USHORT CCUQFlag);
int            IM_EX_CTIBASE QueueFunctionWrite (BSTRUCT *);
int            IM_EX_CTIBASE nb2execr (DSTRUCT *);
int            IM_EX_CTIBASE nb2query (VOID);
VOID           IM_EX_CTIBASE ReturnThread (PVOID);
INT            IM_EX_CTIBASE LoopBack (USHORT, USHORT);
int            IM_EX_CTIBASE CCUReset (USHORT, USHORT);
int            IM_EX_CTIBASE SwitchControl (CSTRUCT *);
int            IM_EX_CTIBASE LCULockOutset (USHORT, USHORT);
int            IM_EX_CTIBASE LCULockOutReset (USHORT, USHORT);
int            IM_EX_CTIBASE DecodeDialupData(INMESS *InMessage, DIALUPSTRUCT *DUPst);

/* Prototypes from PSUP.C */
int            IM_EX_CTIBASE Float2CharFormat (FLOAT, PCHAR, USHORT, USHORT);
int            IM_EX_CTIBASE Long2CharFormat (LONG, PCHAR, USHORT);
int            IM_EX_CTIBASE Short2CharFormat (SHORT, PCHAR, USHORT);

/* Prototypes for WORDS.C */
int            IM_EX_CTIBASE A_Word (PBYTE, const ASTRUCT &, BOOL Double = FALSE);
int            IM_EX_CTIBASE B_Word (PBYTE, const BSTRUCT &, INT wordCount, BOOL Double = FALSE);
int            IM_EX_CTIBASE C_Word (PBYTE, const PBYTE, USHORT);
int            IM_EX_CTIBASE C_Words (PBYTE, const PBYTE, USHORT, INT*);
int            IM_EX_CTIBASE D1_Word (PBYTE, PBYTE, PUSHORT, PULONG, PUSHORT, PUSHORT);
int            IM_EX_CTIBASE D23_Word(PBYTE, PBYTE, PUSHORT, PUSHORT);
int            IM_EX_CTIBASE D_Words (PBYTE, USHORT, USHORT, DSTRUCT *);
int            IM_EX_CTIBASE E_Word (PBYTE, ESTRUCT *);
int            IM_EX_CTIBASE BCHCheck (PBYTE);
int            IM_EX_CTIBASE PadTst (PBYTE, USHORT, USHORT);
int            IM_EX_CTIBASE NackTst (BYTE, PUSHORT, USHORT);
int            IM_EX_CTIBASE APreamble (PBYTE, const ASTRUCT &);
int            IM_EX_CTIBASE BPreamble (PBYTE, const BSTRUCT &, INT wordsToFollow);
int            IM_EX_CTIBASE LPreamble (PBYTE, USHORT);
int            IM_EX_CTIBASE G_Word (PBYTE, const BSTRUCT &, INT dwordCount, BOOL Double = FALSE);
int            IM_EX_CTIBASE H_Word (PBYTE, PBYTE, USHORT);
int            IM_EX_CTIBASE I1_Word (PBYTE, PBYTE, PUSHORT, PULONG, PUSHORT, PUSHORT);
int            IM_EX_CTIBASE I23_Word (PBYTE, PBYTE, PUSHORT, PUSHORT);
int            IM_EX_CTIBASE I_Words (PBYTE, USHORT, USHORT, DSTRUCT *);
int            IM_EX_CTIBASE J_Word (PBYTE, ESTRUCT *);
int            IM_EX_CTIBASE I_BCHCheck (PBYTE);


#pragma pack(pop, message_packing)     // Restore the prior packing alignment..

#endif      // #ifndef DSM2_H
