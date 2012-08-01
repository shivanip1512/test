#pragma once

// Forward Declarations...

class CtiPort;
class CtiDeviceBase;
class CtiHashKey;

/* Include the btrieve error defines */
#include "dsm2.h"
#include "dsm2err.h"
#include "devicetypes.h"
#include "queues.h"

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
#define START_ALGORITHM 3
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
#define FISHERPIERCE    0x1000
#define ENCODED         0x4000
#define DECODED         0x4000
#define COMMANDCODE     0x8000

#define STOPALL         0x0010
#define LOOPBACKCOMMAND 0x0030
#define REMOTECONTROL   0x0040
#define ANALOGLOOP      0x0050

#define COMMANDMASK     ~(COMMANDCODE | WAIT | RESULT | QUEUED | ACTIN)

/* Defines used for TCP/IP Interfaces */
#define TCP_SES92       1
#define TCP_CCU710      2
#define TCP_WELCO       3
#define TCP_DUALWELCO   4


struct QUEENT
{
    USHORT InUse;
    ULONG TimeSent;
    CtiConnect *ReturnNexus;
    USHORT OriginalOutMessageSequence;
    USHORT QueueEntrySequence;
    USHORT EventCode;
    USHORT Priority;
    USHORT Length;
    LONG TargetID;
    ULONG Address;
    UINT MessageFlags;//Perhaps we should keep the whole out message and not have to do this?

    PIL_ECHO Request;
};

/* structure used to keep track of sequencing */
struct REMOTESEQUENCE
{
    USHORT Reply;
    USHORT Request;
};

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
