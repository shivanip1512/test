/* Include file for the data router process */
#ifndef DRP_H
#define DRP_H

/* get the error file */
//// #include "btrieve.h"
//#include "btrerrs.h"

#define MAXDESTTABENTS  30
#define MAXDESTINATIONS 20
#define DESTSIZE        10
#define MAXDADS         10

#define DRPFILENAME "DATA\\DRP.BDB"
#define BDB_DRPFILENAME "DRP.BDB"

/* Define the types */
#define TYPE_NORMAL     0
#define TYPE_CS         1
#define TYPE_MS         2
#define TYPE_CF         3
#define TYPE_MPC        4

/* Define Load Control Max's */
#define MAXTYPES                30
#define MAXHOURS                24
#define MAXTOWERS               30

/* drp Btrieve definitions */
#define DRPNUMKEYELEM   3

typedef struct _DRPRECORD {
    USHORT Type;
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    CHAR NewDeviceName[STANDNAMLEN];
    CHAR NewPointName[STANDNAMLEN];
    CHAR Destination[MAXDESTINATIONS][DESTSIZE];
} DRPRECORD;

/* definitions for the interprocess messages */
typedef struct _DRPMESS {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
    union {
        struct {
            CHAR DeviceName[STANDNAMLEN];
            CHAR PointName[STANDNAMLEN];
            USHORT Quality;
            ULONG TimeStamp;
            USHORT AlarmState;
            FLOAT Value;
        } DRPValue;
        struct {
            ULONG FinishTime;
            USHORT Level[MAXTYPES];
        } DRPScram;
        struct {
            ULONG StartTime;
            ULONG FinishTime;
            USHORT Level[MAXTYPES][24];
        } DRPStrategy;
        struct {
            CHAR Message[80];
            ULONG MSAlarmPoint;
        } DRPTextMessage;
        struct {
            USHORT Mode;
        } DRPFBLCMode;
        struct {
            struct timeb TimeB;
        } DRPTimeSync;
    } DRPUnion;
} DRPMESS;

typedef struct _DRPTIMESYNC {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
    struct timeb TimeB;
} DRPTIMESYNC;

typedef struct _DRPSEASONCHANGE {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
} DRPSEASONCHANGE;

typedef struct _DRPNULL {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
} DRPNULL;

typedef struct _DRPSCRAM {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
    ULONG FinishTime;
    USHORT Level[MAXTYPES];
} DRPSCRAM;

typedef struct _DRPSTRATEGY {
    USHORT Type;
    CHAR SourceName[DESTSIZE];
    ULONG StartTime;
    ULONG FinishTime;
    USHORT Level[MAXTYPES][24];
} DRPSTRATEGY;

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

#define DRPTYPEDADVALUE             1001
#define DRPTYPEDADALARM             1002

/* Thread stack size definitions */
#define DRPINPUTTHREADSTACKSIZE 32768
#define INPUTTHREADSTACKSIZE 32768
#define DRPTIMESYNCTHREADSTACKSIZE 32768
#define NETWORKCHECKTHREADSTACKSIZE 32768
#define VCONTROLTHREADSTACKSIZE 32768
#define DADTHREADSTACKSIZE 32768
#define REVERSETHREADSTACKSIZE 32768


/* Prototypes from DRUPDATE.C */
IM_EX_CTIBASE INT InitDRPDB (VOID);
IM_EX_CTIBASE INT CreateDRPDB (PCHAR);
IM_EX_CTIBASE INT CloseDRPDB (VOID);
IM_EX_CTIBASE INT DRPAdd (DRPRECORD *);
IM_EX_CTIBASE INT DRPUpdate (DRPRECORD *);
IM_EX_CTIBASE INT DRPDelete (DRPRECORD *);
IM_EX_CTIBASE INT DRPGetEqual (DRPRECORD *);
IM_EX_CTIBASE INT DRPGetFirst (DRPRECORD *);
IM_EX_CTIBASE INT DRPGetNext (DRPRECORD *);
IM_EX_CTIBASE INT DRPGetNextGT (DRPRECORD *);
IM_EX_CTIBASE INT DRPGetPrev (DRPRECORD *);
IM_EX_CTIBASE INT DRPRecords (PULONG);
IM_EX_CTIBASE INT Check4DrpDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT OpenDRPDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT FO_CopyDRPDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT BDB_CopyDRPDB (PCHAR, PCHAR);

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

/* Prototypes from drpcont.c */
VOID IM_EX_CTIBASE VControlThread (PVOID);
IM_EX_CTIBASE INT VComStrategyStop (PCHAR, PCHAR);
IM_EX_CTIBASE INT VComStrategyStart (PCHAR, PCHAR);
IM_EX_CTIBASE INT VComControlWaive (PCHAR);
IM_EX_CTIBASE INT VComControlUnWaive (PCHAR);
IM_EX_CTIBASE INT FBLCPauseMP (PCHAR);
IM_EX_CTIBASE INT FBLCUnPauseMP (PCHAR, USHORT);

/* Prototypes from drp.c */
IM_EX_CTIBASE INT UpdatePoint (DRPVALUE *);
IM_EX_CTIBASE INT DRPDispatch (PCHAR, ULONG, DRPMESS *, ULONG, CHAR[][DESTSIZE]);
VOID IM_EX_CTIBASE InputThread (PVOID);
VOID IM_EX_CTIBASE APIENTRY DRPCleanUp (ULONG);
VOID IM_EX_CTIBASE DRPInputThread (PVOID);
VOID IM_EX_CTIBASE NetworkCheckThread (PVOID);
IM_EX_CTIBASE INT SendDAD (DRPVALUE *);
VOID IM_EX_CTIBASE DRPDADThread (PVOID);
VOID IM_EX_CTIBASE DRPInOnlyThread (PVOID);
VOID IM_EX_CTIBASE DRPTimeSyncThread (PVOID);

/* Prototypes from DRPSUP.C */
IM_EX_CTIBASE INT DRPRouteMessage (PCHAR, PCHAR, ULONG, DRPMESS *, ULONG);
IM_EX_CTIBASE INT StartProcess (PCHAR);
IM_EX_CTIBASE INT FindSlot (PCHAR, PUSHORT);
IM_EX_CTIBASE INT GetSlot (PCHAR, PUSHORT);
IM_EX_CTIBASE INT TypeNumberToName (USHORT, PCHAR);
IM_EX_CTIBASE INT NetFail (PCHAR);
IM_EX_CTIBASE INT NetSuccess (PCHAR);
IM_EX_CTIBASE INT QueueDRPValue (DRPVALUE *);
IM_EX_CTIBASE INT MOAUploads (PCHAR);
IM_EX_CTIBASE INT MSLCUpdate (USHORT);
IM_EX_CTIBASE INT SeasonChange (VOID);
IM_EX_CTIBASE INT AllocateDRPValue (DRPVALUE **);
IM_EX_CTIBASE INT SendTextAlarm (PCHAR, PCHAR);
IM_EX_CTIBASE INT StartGraph (PCHAR GraphName);
VOID IM_EX_CTIBASE ReverseThread (PVOID);
IM_EX_CTIBASE INT DRPSendTimeSync (PCHAR);



/* Prototypes from DRPCSUP.C */
IM_EX_CTIBASE INT InitLCType (VOID);
IM_EX_CTIBASE INT CSLevelset (USHORT, USHORT);
IM_EX_CTIBASE INT CSPriorityset (USHORT, USHORT);
IM_EX_CTIBASE INT CSLoadReductionset (USHORT, FLOAT);
IM_EX_CTIBASE INT CSFBLCModeset (USHORT);
IM_EX_CTIBASE INT CSFBLCModeGet (PUSHORT);
IM_EX_CTIBASE INT CSControlWaivedGet (VOID);
IM_EX_CTIBASE INT MSLevelset (USHORT, USHORT);
IM_EX_CTIBASE INT CSFBLCPause (VOID);
IM_EX_CTIBASE INT CSFBLCUnPause (USHORT);
IM_EX_CTIBASE INT MSFBLCPause (USHORT);
IM_EX_CTIBASE INT MSFBLCUnPause (USHORT, USHORT);
IM_EX_CTIBASE INT SaveLCStatus (VOID);
IM_EX_CTIBASE INT RestoreLCStatus (VOID);
IM_EX_CTIBASE INT CheckMinTime (USHORT, ULONG);
IM_EX_CTIBASE INT FBLCConnect (VOID);

#endif      // #ifndef DRP_H





