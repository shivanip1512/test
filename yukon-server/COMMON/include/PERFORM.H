/* Declare the type that we will use to hold the globals */

class DEVICEPERF;

typedef struct {
    USHORT MinCommPercentCalc;
    USHORT PortMinPercent;
    ULONG PortMaxFails;
    USHORT RemoteMinPercent;
    ULONG RemoteMaxFails;
    USHORT RouteMinPercent;
    ULONG RouteMaxFails;
    USHORT DeviceMinPercent;
    ULONG DeviceMaxFails;
} PERFGLOBS;

IM_EX_CTIBASE extern PERFGLOBS    *PerfGlobs;
// IM_EX_CTIBASE extern USHORT       PrintLogEvent;

/* Prototypes for the various performance log files */

/* Prototypes from PERFORM.C */
IM_EX_CTIBASE INT InitPerfGlobs (VOID);
IM_EX_CTIBASE INT PerfPrintLogOff (VOID);
IM_EX_CTIBASE INT PerfPrintLogOn (VOID);
IM_EX_CTIBASE INT RoutePerfUpdate (ROUTEPERF *);
IM_EX_CTIBASE INT DevicePerfUpdate (DEVICEPERF *, ERRSTRUCT *);
IM_EX_CTIBASE INT RouteFailures (STATS *);
IM_EX_CTIBASE INT RoutePerform (STATS *, PUSHORT);
IM_EX_CTIBASE INT Route24Perform (STATS *, PUSHORT);
IM_EX_CTIBASE INT DeviceFailures (STATS *);
IM_EX_CTIBASE INT DevicePerform (STATS *, PUSHORT);
IM_EX_CTIBASE INT Device24Perform (STATS *, PUSHORT);
IM_EX_CTIBASE INT LogCommPercentLow (PCHAR, USHORT, struct timeb *, USHORT, USHORT);
IM_EX_CTIBASE INT LogTooManyFailures (PCHAR, USHORT, struct timeb *, ULONG);
IM_EX_CTIBASE INT LogNewSuccess (PCHAR, USHORT, struct timeb *);


/* Prototypes from PERFORM2.C */
INT RemotePerfUpdate (REMOTEPERF *, ERRSTRUCT *);
INT PortFailures (STATS *);
INT PortPerform (STATS *, PUSHORT);
INT Port24Perform (STATS *, PUSHORT);
INT RemoteFailures (STATS *);
INT RemotePerform (STATS *, PUSHORT);
INT Remote24Perform (STATS *, PUSHORT);





