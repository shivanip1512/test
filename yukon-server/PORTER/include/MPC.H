/*-----------------------------------------------------------------------------*
*
* File:   MPC
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/INCLUDE/MPC.H-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/12/20 21:02:09 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


/* Define the stack sizes for the various threads */
#define MPCINTHREADSTACKSIZE            32756
#define MPCOUTTHREADSTACKSIZE           32756
#define MPCWRITETHREADSTACKSIZE         32756
#define MPCTELEGRAPHTHREADSTACKSIZE     32756
#define MPCTELEGRAPHSENTTHREADSTACKSIZE 32756
#define MPCCHECKTHREADSTACKSIZE         32756
#define MPCTELEGRAPHCHECKTHREADSTACKSIZE 32756
#define MPCNULLTHREADSTACKSIZE          32756


/* Structure of messages for MPC */
typedef struct {
    CHAR Stx[1];
    CHAR Function[2];
    CHAR TimeStamp[17];
    CHAR Telegraph[50];
    CHAR Crc[4];
    CHAR Cr[1];
} MPCTELEGRAPHSTART;

typedef struct {
    CHAR Stx[1];
    CHAR Function[2];
    CHAR TimeStamp[17];
    CHAR Crc[4];
    CHAR Cr[1];
} MPCTELEGRAPHEND;

typedef struct {
    CHAR Stx[1];
    CHAR Function[2];
    CHAR TimeStamp[17];
    CHAR MilliSeconds[3];
    CHAR Crc[4];
    CHAR Cr[1];
} MPCTIMESYNC;

typedef struct {
    CHAR Stx[1];
    CHAR Function[2];
    CHAR TimeStamp[17];
    CHAR Descriptor[6];
    CHAR State[1];
    CHAR Crc[4];
    CHAR Cr[1];
} MPCSTATUS;

typedef struct {
    CHAR Stx[1];
    CHAR Function[2];
    CHAR TimeStamp[17];
    CHAR Descriptor[6];
    CHAR Value[10];
    CHAR Crc[4];
    CHAR Cr[1];
} MPCVALUE;

typedef struct {
    CHAR Stx[1];
    CHAR Function[2];
    CHAR TimeStamp[17];
    CHAR Descriptor[6];
    CHAR State[1];
    CHAR Crc[4];
    CHAR Cr[1];
} MPCCONTROL;


typedef struct {
    CHAR Stx[1];
    CHAR Function[2];
    CHAR TimeStamp[17];
    CHAR Descriptor[6];
    CHAR Alarm[3];
    CHAR Crc[4];
    CHAR Cr[1];
} MPCALARM;

typedef struct {
    CHAR Stx[1];
    CHAR Function[2];
    CHAR TimeStamp[17];
    CHAR Descriptor[6];
    CHAR Crc[4];
    CHAR Cr[1];
} MPCALARMACK;

typedef struct {
    CHAR Stx[1];
    CHAR Function[2];
    CHAR TimeStamp[17];
    CHAR Crc[4];
    CHAR Cr[1];
} MPCNULL;


#define TELEGRAPHSENT           1
#define TELEGRAPHEND_NORMAL     2
#define TELEGRAPHEND_BLOCK      3
#define TELEGRAPHEND_ERROR      4
#define TELEGRAPH_CONNECT_FAIL  99      /* send when pipe to MPC fails */

#pragma pack(1)         /* force byte alignment */

typedef struct {
    ULONG Function;
    CHAR Telegraph[7];
} TELEGRAPH;

#pragma pack()          /* set back to compiler default alignment */



/* Function Prototypes from MPC.C */
VOID MPCInThread (PVOID);
int MPCStatusDecode (MPCSTATUS *);
int MPCValueDecode (MPCVALUE *);
int MPCControlDecode (MPCCONTROL *);
int MPCAlarmAckDecode (MPCALARMACK *);
int MPCTimeSyncDecode (MPCTIMESYNC *);
VOID MPCOutThread (PVOID);
int SendCheck (PCHAR, FLOAT, ULONG);
VOID MPCNullThread (PVOID);
int SendMPCNull (VOID);
int SendMPCValue (PCHAR, FLOAT, ULONG, USHORT);
int SendMPCStatus (PCHAR, FLOAT, ULONG, USHORT);
int SendMPCControl (PCHAR,  FLOAT, ULONG, USHORT);
int SendMPCAlarm  (PCHAR,  PCHAR, ULONG, USHORT);
int CTIToMPCTime (ULONG, USHORT, PCHAR);
int MPCToCTITime (PCHAR, PULONG, PUSHORT);
int CTIToMPCName (PCHAR, PCHAR);
int MPCToCTIName (PCHAR, PCHAR);
int CTIToMPCAlarm (PCHAR, PCHAR);
int MPCCrcCalc (PVOID, USHORT, PCHAR);
VOID MPCWriteThread (PVOID);
VOID MPCCheckThread (PVOID);
VOID MPCTelegraphSentThread (PVOID);
int WriteData9350 (PCHAR);
int SendMPCTelegraphStart (PCHAR);
int SendMPCTelegraphEnd (VOID);
VOID MPCTelegraphThread (PVOID);
int SendMPC (CHAR);
int SendTelegraphToPorter (BYTE *);
VOID MPCTelegraphCheckThread (PVOID);
VOID APIENTRY MPCCleanUp (ULONG);

/* Function prototypes from RIPPLE.C */
int SendBitPatternToLogger (const CHAR *, const BYTE *, int len = 7);
int SendDOToLogger (const CHAR *, const BYTE *);
//int SendTelegraphStatusToMPC (ULONG, const BYTE *);
int SendTelegraphStatusToMPC (ULONG, const BYTE *, CtiDeviceBase *dev );
/* Definitions related to Telegraph Interface */
#define MPC_WAITING                 00
#define MPC_READY                   01

#define MPC_WAITFORWRITEADDRESS     10
#define MPC_WAITFORWRITELENGTH      11
#define MPC_WAITFORWRITEBYTES       12
#define MPC_WAITFORWRITEEOT         13

#define MPC_WAITFORREADADDRESS      20
#define MPC_WAITFORREADLENGTH       21
#define MPC_WAITFORREADEOT          22

#define MPC_WAITFORCLREOT           30

#define MPC_WAITFORCSTDATA          40
#define MPC_WAITFORCSTEOT           41

#define MPC_RDY                     0X02
#define MPC_EOT                     0X04
#define MPC_ACK                     0X06
#define MPC_OK                      0X07
#define MPC_CST                     0X11
#define MPC_RPM                     0X12
#define MPC_WRM                     0X13
#define MPC_NACK                    0X15
#define MPC_CLR                     0X19


