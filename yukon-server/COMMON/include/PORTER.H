#ifndef PORTER_H
#define PORTER_H

// Forward Declarations...

class CtiPort;
class CtiDeviceBase;
class CtiHashKey;
class DEVICE;
class ROUTE;

/* Include the btrieve error defines */
#include "dsm2.h"
#include "dsm2err.h"
#include "devicetypes.h"
#include "queues.h"

/* stack sizes for various threads */
#define PORT_THREAD_STK_SIZE 32768
#define PIPE_THREAD_STK_SIZE 32768
#define ROUTER_THREAD_STK_SIZE 32768
#define REASSURE_THREAD_STK_SIZE 8192
#define QUEUE_THREAD_STK_SIZE 32768
#define RESPONSE_THREAD_STK_SIZE 32768
#define KICKER_THREAD_STK_SIZE 32768
#define TIMESYNC_THREAD_STK_SIZE 16384
#define FILLER_THREAD_STK_SIZE 16384
#define VCONFIG_THREAD_STK_SIZE 16384
#define PERF_THREAD_STK_SIZE 16384
#define PERF_UPDATE_THREAD_STK_SIZE 16384
#define PORT_TCPSERVER_THREAD_STK_SIZE 32768

#define EXT_PORT_COUNT_MAX  50

/* Definitions for minimum and maximum tcpip transaction threads */
#define MINTRANSACTIONTHREADS 10
#define MAXTRANSACTIONTHREADS 1024

/* Port and Remote Btrieve create structures */
#define PORTNUMKEYS 3
#define REMOTENUMKEYS 6

#define SANITY_RATE              300    //Number from van goh that slows down reporting rate for threads, note this can be tweaked or used differently
#define SANITY_RATE_MED_SLEEPERS  75    //for medium rate sleepers!
#define SANITY_RATE_LONG_SLEEPERS 10    //this is for objects that can sleep for a very long time
/* Data structures used to access Btrieve records */
typedef struct _PORT {
    USHORT Port;
    CHAR Description[STANDNAMLEN];
    CHAR PortName[STANDNAMLEN];
    USHORT BaudRate;
    USHORT Protocol;
    USHORT Unused;
    USHORT RadioDelay[8];
    USHORT RTSSense;
    USHORT DCDSense;
    USHORT Status;
    STATS Stats;           /* Start of dynamic portion of record */
} PORT;

class REMOTE {
    CHAR RemoteName[STANDNAMLEN];
    USHORT Port;
    USHORT Remote;
    USHORT Type;
    USHORT Radio;
    USHORT PostDelay;
    USHORT Status;
    ULONG  ResetTime;
    STATS  Stats;         /* Start of dynamic portion of record */
};


/* Structure used for performance update */
typedef struct _REMOTEPERF {
    USHORT Port;
    USHORT Remote;
    USHORT Error;
} REMOTEPERF;


/* Definitions used inside porter for flags in PortFlags */
#define REMOTEPORT          0X0001

/* Define the range of the ports (for now) */
#define PORTDIRECTMIN       0
#define PORTDIRECTMAX       99
#define PORTTCPMIN          0
#define PORTTCPMAX          99
#define PORTMODEMPOOLMIN    100
#define PORTMODEMPOOLMAX    109
#define PORTTCPMODEMPOOLMIN 110
#define PORTTCPMODEMPOOLMAX 119
#define PORTTCPSERVERMIN    PORTMAX
#define PORTTCPSERVERMAX    PORTMAX


/* IDLC command definitions */

#define HDLC_UD         0x13
#define BROADCAST       127
#define RESET_REQ       0x1f
#define RESET_ACK       0x73
/* Note that check for a reject has been softened to deal with 711 bug */
// #define REJ             0x19
#define REJ             0x09
#define SREJ            0x1d
#define NO_OP           00
#define CLFLT           11
#define CLPWR           21
#define CLDMN           23
#define CLCLD           24
#define CLRAL           1
#define CLRDY           2
#define START           3
#define HOPRO           5
#define ENPRO           6
#define COLD            16
#define USTM0           24
#define SEL0            64
#define SHD0            96
#define CMND_ACTIN      00
#define CMND_RMEMY      04
#define CMND_WMEMY      01
#define CMND_WMEMS      9
#define CMND_RMEMS      12
#define CMND_DTRAN      38
#define CMND_XTIME      42
#define CMND_ITIME      17
#define CMND_RCONT      05
#define CMND_CQENS      34
#define CMND_RQENS      36
#define CMND_RQDIR      37
#define CMND_RCOLQ      39
#define CMND_LGRPQ      43
#define SECTN_ILEXRTU   127
#define SECTN_MASTERCOM 126
#define SECTN_CCU700    125
#define SECTN_PROTWRAP  124
#define SECTN_SES92RTU  120
#define ITIME_LEN       10
#define PCONFIG         0
#define PCONFIG_LEN     3
#define PREIDL          6
#define PREIDLEN        PREIDL+1
#define MAXDATA         253
#define MAXQUEENTLEN    30
#define MAXQUEENTRIES   32
#define T_RTSON         1010
#define T_RTSON_LEN     1
#define T_CTSTO         1011
#define T_CTSTO_LEN     1
#define T_KEYOFF        1012
#define T_KEYOFF_LEN    1
#define T_INTRATO       1013
#define T_INTRATO_LEN   1
#define BA_TRIG         1100
#define BA_TRIG_LEN     2
#define AMP_MODE        1003
#define AMP_MODE_LEN    1
#define AMP_LAST        20000
#define AMP_LAST_LEN    1
#define AMP_STAT_BASE   20001
#define AMP_STAT_LEN    1
#define AMP_FEED_BASE   20003
#define AMP_FEED_LEN    1
#define TS_PERIOD       1101
#define TS_PERIOD_LEN   2
#define BA_TIMBAS       40300
#define BA_TIMBAS_LEN   11

/* IDLC process definitions */
#define DEST_BASE       0
#define DEST_QUEUE      1
#define DEST_DLC        2
#define DEST_TSYNC      4
#define DEST_LM         5

/* IDLC status definitions */
#define STAT_POWER      0x0001      // STATS byte 7
#define STAT_FAULTC     0x0002      // STATS byte 7
#define STAT_DEADMN     0x0004      // STATS byte 7
#define STAT_COLDST     0x0008      // STATS byte 7
#define STAT_NSADJ      0x0010      // STATS byte 7
#define STAT_ALGFLT     0x0020      // STATS byte 7
#define STAT_REQACK     0x0040      // STATS byte 7
#define STAT_BROADC     0x0080      // STATS byte 7
#define STAT_BATTRY     0x0001      // STATS byte 8
#define STAT_BADTIM     0x0008      // STATS byte 8
#define STAT_ALG0ST     0x00C0      // STATS byte 9
#define STAT_ALG1ST     0x0030      // STATS byte 9
#define STAT_ALG2ST     0x000C      // STATS byte 9
#define STAT_ALG3ST     0x0003      // STATS byte 9
#define STAT_ALG4ST     0x00C0      // STATS byte 10
#define STAT_ALG5ST     0x0030      // STATS byte 10
#define STAT_ALG6ST     0x000C      // STATS byte 10
#define STAT_ALG7ST     0x0003      // STATS byte 10
#define STAT_DLCFLT     0x0080      // STATD byte 11

/* Define the algorithm status's */
#define ALGO_HALTED          0
#define ALGO_ENABLED         1
#define ALGO_RUNNING         2
#define ALGO_SUSPENDED       3


/* Define protocol wraps */
#define NOWRAP          0
#define IDLCWRAP        1
#define UCAWRAP         2
#define DNPWRAP         3

/* Amp Mode masks */
#define AMP_FAILOVER    0x0001
#define AMP_SWAPPING    0x0002

/* queue commands */
#define NOWAIT          0x0000
#define NORESULT        0x0000
#define WAIT            0x0001
#define RESULT          0x0002
#define QUEUED          0x0004
#define ACTIN           0x0008
#define AWORD           0x0010
#define BWORD           0x0020
#define DTRAN           0x0040
#define RCONT           0x0080
#define RIPPLE          0x0100
#define STAGE           0x0200
#define VERSACOM        0x0400
#define TSYNC           0x0800
#define REMS            0x1000   // This can never be used now.... CGP Corey.
#define FISHERPIERCE    0x1000
#define ENCODED         0x4000
#define DECODED         0x4000
#define COMMANDCODE     0x8000

#define STOPALL         0x0010
#define LOOPBACKCOMMAND 0x0030
#define REMOTECONTROL   0x0040
#define ANALOGLOOP      0x0050

#define COMMANDMASK     ~(COMMANDCODE | WAIT | RESULT | QUEUED | ACTIN)

#define ARML            0x60
#define ARMS            0x61
#define ARMC            0x62
#define WRITE           0x0000
#define READ            0x0001
#define Q_ARML          0x0020
#define Q_ARMS          0x0040
#define Q_ARMC          0x0080

/* Defines used for TCP/IP Interfaces */
#define TCP_SES92       1
#define TCP_CCU710      2
#define TCP_WELCO       3
#define TCP_DUALWELCO   4


typedef struct _QUEENT {
    USHORT InUse;
    ULONG TimeSent;
    CTINEXUS *ReturnNexus;
    CTINEXUS *SaveNexus;
    USHORT OriginalOutMessageSequence;
    USHORT QueueEntrySequence;
    USHORT EventCode;
    USHORT Priority;
    USHORT Length;
    LONG TargetID;
    ULONG Address;

    PIL_ECHO Request;         //

} QUEENT;

/* structure used to keep track of sequencing */
typedef struct _REMOTESEQUENCE {
    USHORT Reply;
    USHORT Request;
} REMOTESEQUENCE;

/* Status definitions */
#define POWERFAILED     0x0001
#define RESETTING       0x0002
#define DLCFAULT        0x0004
#define INRCONT         0x0008
#define INRCOLQ         0x0010
#define NEEDSRESET      0x0020
#define TIMESYNCED      0x0040
#define LOWBATTRY       0x0080
#define SETSLIST        0x0100
#define INLGRPQ         0x0200
#define INACTNSHED      0x0400
#define INUSE           0x0001
#define INCCU           0x0002

/* This information chokes the compiler and isn't needed to compile Windows apps */
/* Information on CCU 711's*/
typedef struct _CCUINFO {
    HANDLE           StatusSem;
    HCTIQUEUE        QueueHandle;
    HCTIQUEUE        ActinQueueHandle;
    USHORT           Type;
    USHORT           Status;

    USHORT           PortQueueEnts;
    USHORT           PortQueueConts;
    USHORT           RContInLength;
    USHORT           ReadyN;
    USHORT           NCsets;
    USHORT           NCOcts;
    USHORT           FreeSlots;
    USHORT           RColQMin;
    QUEENT           QueTable[MAXQUEENTRIES];

    REMOTESEQUENCE   RemoteSequence;
    USHORT           FiveMinuteCount;
    ULONG            StageTime;
    ULONG            NextCommandTime;
    ULONG            LCUFlags;
    OUTMESS          *ControlOutMessage;
    STATS            Stats;
} CCUINFO;

typedef CCUINFO *PCCUINFO;

typedef struct _PORTSTATS {
        STATS Stats;
} PORTSTATS;

typedef PORTSTATS *PPORTSTATS;

typedef struct _REMSHAREDSTRUCT {
        ULONG WaitSem;
        OUTMESS *OutMessage;
        CTINEXUS *MyNexusHandle;
} REMSHAREDSTRUCT;

#ifdef OLD_CODE

INT AnalogLoop (OUTMESS *, CtiPort *, CtiDeviceBase *);

/* Prototypes for PTPRINT.C */
INT TraceIn (PBYTE, USHORT, USHORT, USHORT, USHORT);
INT TraceOut (PBYTE, USHORT, USHORT, USHORT);
INT TPrint (PBYTE, USHORT);
INT BinPrint (BYTE);

/* Prototypes from VERSACOM.C */
INT VersacomSend (OUTMESS *);
INT setNibble (PBYTE, INT, INT);
INT VersaCommSend (OUTMESS *);

/* Prototypes from PUPDATE.C */
IM_EX_CTIBASE INT InitPorterDB (VOID);
IM_EX_CTIBASE INT InitPortDB(VOID);
IM_EX_CTIBASE INT CreatePortDB (PCHAR);
IM_EX_CTIBASE INT InitRemoteDB(VOID);
IM_EX_CTIBASE INT CreateRemoteDB (PCHAR);
IM_EX_CTIBASE INT ClosePorterDB (VOID);
IM_EX_CTIBASE INT ClosePortDB(VOID);
IM_EX_CTIBASE INT CloseRemoteDB(VOID);

//IM_EX_CTIBASE INT PortAdd (CtiPort *);
//IM_EX_CTIBASE INT PortUpdate (CtiPort *);
//IM_EX_CTIBASE INT PortFastUpdate (CtiPort *);
//IM_EX_CTIBASE INT PortDelete (CtiPort *);
//IM_EX_CTIBASE INT PortGetEqual (CtiPort *);
//IM_EX_CTIBASE INT PortLock (CtiPort *);
//IM_EX_CTIBASE INT PortUnLock (CtiPort *);
//IM_EX_CTIBASE INT PortGetFirst (CtiPort *);
//IM_EX_CTIBASE INT PortGetNext (CtiPort *);
//IM_EX_CTIBASE INT PortGetGT (CtiPort *);
//IM_EX_CTIBASE INT PortGetPrev (CtiPort *);

//IM_EX_CTIBASE INT RemoteAdd (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteUpdate (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteFastUpdate (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteDelete (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteGetEqual (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteLock (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteUnLock (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteGetPortRemoteEqual (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteGetFirst (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteGetNext (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteGetPrev (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteGetPortFirst (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteGetPortGT (CtiDeviceBase *);
//IM_EX_CTIBASE INT RemoteGetGT (CtiDeviceBase *);

IM_EX_CTIBASE INT PortRecords (PULONG);
IM_EX_CTIBASE INT RemoteRecords (PULONG);
IM_EX_CTIBASE INT PortClearAll (VOID);
IM_EX_CTIBASE INT PortClearEqual (CtiPort *);
IM_EX_CTIBASE INT RemoteClearAll (VOID);
IM_EX_CTIBASE INT RemoteClearEqual (CtiDeviceBase *);
IM_EX_CTIBASE INT OpenPortDB(PCHAR, PBYTE);
IM_EX_CTIBASE INT OpenRemoteDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT FO_CopyPortDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT FO_CopyRemoteDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT FO_IntegrityCheckRemoteRecs (PCHAR SrcPathName, PCHAR DestPathName,
                                 PULONG RecCnt);
IM_EX_CTIBASE INT FO_IntegrityReportRemoteRecs (PCHAR SrcPathName, PCHAR DestPathName,
                                 PULONG RecCnt);
IM_EX_CTIBASE INT FO_CompareRemoteSourceToTarGet (CtiDeviceBase *SrcRecord, CtiDeviceBase *TarGet);
IM_EX_CTIBASE INT Check4PortDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT Check4RemoteDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT BDB_CopyPortDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT BDB_CopyRemoteDB (PCHAR, PCHAR);

extern HANDLE hStdOut;

#endif  // OLD_CODE

#endif
