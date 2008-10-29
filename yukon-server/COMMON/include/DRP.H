/* Include file for the data router process */
#ifndef DRP_H
#define DRP_H

#define DESTSIZE        10

/* definitions for the interprocess messages */

typedef struct _DRPNULL {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
} DRPNULL;

typedef struct _DRPVALUE {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    USHORT Quality;
    ULONG TimeStamp;
    USHORT AlarmState;
    FLOAT Value;
} DRPVALUE;

typedef struct _DRPTEXT {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
    CHAR Message[80];
    ULONG MSAlarmPoint;
} DRPTEXT;

typedef struct _DRPFBLCMODE {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
    USHORT Mode;
} DRPFBLCMODE;

typedef struct _DRPSHUTDOWN {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
} DRPSHUTDOWN;

/* DRP Type definitions */
#define DRPTYPEDEFAULT              0
#define DRPTYPEVALUE                1
#define DRPTYPEALARM                2
#define DRPTYPETIMESYNC             50
#define DRPTYPESHUTDOWN             51
#define DRPTYPESEASONCHANGE         52
#define DRPTYPELOADSCRAM            53
#define DRPTYPELOADSTRATEGY         54
#define DRPTYPETEXTALARM            55
#define DRPTYPENULL                 56
#define DRPTYPETEXTMESSAGE          57
#define DRPTYPEFBLCMODE             58
#define DRPTYPERENAMEDDEFAULT       100
#define DRPTYPERENAMEDVALUE         101
#define DRPTYPERENAMEDALARM         102

/* Prototypes from DRPINT.C */
VOID DRPThread (PVOID);
VOID DRPDADInThread (PVOID);

IM_EX_CTIBASE INT xInitDRP (PCHAR);
IM_EX_CTIBASE INT xInitDRPOutOnly (PCHAR);
IM_EX_CTIBASE INT xInitDRPDAD (VOID);
IM_EX_CTIBASE INT GetDRPPoint (DRPVALUE *);
IM_EX_CTIBASE INT WaitDRPPoint (DRPVALUE *);
IM_EX_CTIBASE INT SendDRPPoint (DRPVALUE *);
IM_EX_CTIBASE INT DRPQueryQueue (VOID);
IM_EX_CTIBASE INT xSendDRPTextAlarm (PCHAR);
IM_EX_CTIBASE INT SendDRPFBLCMode (USHORT);
IM_EX_CTIBASE INT xSendDRPTextMessage (PCHAR);
VOID IM_EX_CTIBASE DRPIntCleanUp (ULONG);
IM_EX_CTIBASE INT SendDRP (PVOID, USHORT);

#endif      // #ifndef DRP_H

