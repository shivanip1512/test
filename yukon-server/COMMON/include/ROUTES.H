/* Include the btrieve error defines */
#ifndef ROUTES_H
#define ROUTES_H

// #include "device.h"

/* define the filename used for the route tables */

#define ROUTESINMACRO 30

/* Define the different types of route masks */
#define ROUTETYPEEMETCON    0x0001
#define ROUTETYPEVERSACOM   0x0002
#define ROUTETYPERIPPLE     0x0004
#define ROUTETYPECONTROL    0x0008
#define ROUTETYPEPCU        0x0010

/* Route and Route Macro Structures for Creating Btrieve files */
#define ROUTENUMKEYS 4
#define ROUTEMACRONUMKEYS 1

/* Define variable types for Route Database */
typedef struct _REPEATER {
    CHAR RepeaterName[STANDNAMLEN];     // repeater name
    USHORT VarBits;             // repeater variable bit 0 - 7
} REPEATER;

/* Type for Route Database */
class ROUTE {
    CHAR RouteName[STANDNAMLEN];                // user given logical route name
    CHAR RemoteName[STANDNAMLEN];       // Remote name
    USHORT BusNum;              // ccu bus number 0 - 7
    USHORT AmpNum;              // ccu amp card 1 or 2
    USHORT FixBit;              // repeater fixed bits 0 - 31
    USHORT VarBit;              // var bits
    REPEATER Repeater[7];
    USHORT Timed;               // Time route Y or N
    USHORT CCUTableEntry;
    STATS Stats;                /* Start of dynamic portion of record */
};

/* Type For Route Macro Database */
typedef struct _ROUTEMACRO {
    CHAR RouteMacroName[STANDNAMLEN];   // user given macro route name
    CHAR RouteName[ROUTESINMACRO][STANDNAMLEN];    // user given logical route name
} ROUTEMACRO;

/* structure for route error logging */
typedef struct _ROUTEPERF {
    CHAR RouteName[STANDNAMLEN];
    USHORT Type;
} ROUTEPERF;

#ifdef OLD_CODE
class DEVICE;
class CtiRoute;

/* Prototypes from RUPDATE.C */
IM_EX_CTIBASE INT InitRouteDB (VOID);
IM_EX_CTIBASE INT InRouteDB(VOID);
IM_EX_CTIBASE INT CreateRouteDB (PCHAR);
IM_EX_CTIBASE INT OpenRouteDB(PCHAR, PBYTE);
IM_EX_CTIBASE INT CreateRouteMacroDB (PCHAR);
IM_EX_CTIBASE INT CloseRouteDB (VOID);
IM_EX_CTIBASE INT ClRouteDB(VOID);
IM_EX_CTIBASE INT Check4RouteDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT Check4RouteMacDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT RouteAdd (ROUTE *);
IM_EX_CTIBASE INT RouteUpdate (ROUTE *);
IM_EX_CTIBASE INT RouteFastUpdate (ROUTE *);
IM_EX_CTIBASE INT RouteDelete (ROUTE *);
// IM_EX_CTIBASE INT RouteGetEqual (ROUTE *);
IM_EX_CTIBASE INT RouteLock (ROUTE *);
IM_EX_CTIBASE INT RouteUnLock (ROUTE *);
IM_EX_CTIBASE INT RouteGetFirst (ROUTE *);
IM_EX_CTIBASE INT RouteGetNext (ROUTE *);
IM_EX_CTIBASE INT RouteGetGT (ROUTE *);
IM_EX_CTIBASE INT RouteGetTypeFirst (ROUTE *, USHORT);
IM_EX_CTIBASE INT RouteGetTypeNext (ROUTE *, USHORT);
IM_EX_CTIBASE INT RouteGetTypePrev (ROUTE *, USHORT);
IM_EX_CTIBASE INT RouteTypeRecords (PULONG, USHORT);
IM_EX_CTIBASE INT RouteCheckType (USHORT, USHORT);
IM_EX_CTIBASE INT RouteGetRemoteFirst (ROUTE *);
IM_EX_CTIBASE INT RouteGetRemoteGT (ROUTE *);
IM_EX_CTIBASE INT RouteGetPrev (ROUTE *);
IM_EX_CTIBASE INT RouteGetLast (ROUTE *);
IM_EX_CTIBASE INT RouteMacroAdd (ROUTEMACRO *);
IM_EX_CTIBASE INT RouteMacroUpdate (ROUTEMACRO *);
IM_EX_CTIBASE INT RouteMacroDelete (ROUTEMACRO *);
IM_EX_CTIBASE INT RouteMacroGetEqual (ROUTEMACRO *);
IM_EX_CTIBASE INT RouteMacroGetFirst (ROUTEMACRO *);
IM_EX_CTIBASE INT RouteMacroGetNext (ROUTEMACRO *);
IM_EX_CTIBASE INT RouteMacroGetPrev (ROUTEMACRO *);
IM_EX_CTIBASE INT RouteMacroGetLast (ROUTEMACRO *);
IM_EX_CTIBASE INT RouteMacroGetGE (ROUTEMACRO *);
IM_EX_CTIBASE INT RouteRecords (PULONG);
IM_EX_CTIBASE INT RouteMacroRecords (PULONG);
IM_EX_CTIBASE INT RouteClearAll (VOID);
IM_EX_CTIBASE INT RouteClearEqual (ROUTE *);
IM_EX_CTIBASE INT GetStages (CtiRoute *);
IM_EX_CTIBASE INT GetRoute (CHAR [][STANDNAMLEN], USHORT, PCHAR);
IM_EX_CTIBASE INT GetNextRoute (CHAR [][STANDNAMLEN], PUSHORT, PCHAR);
IM_EX_CTIBASE INT GetVTUPort (DEVICE *);
IM_EX_CTIBASE INT CheckVTURoute (DEVICE *);
IM_EX_CTIBASE INT FO_CopyRouteDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT FO_CopyRouteMacroDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT OpenRouteMacroDB(PCHAR, PBYTE);
IM_EX_CTIBASE INT BDB_CopyRouteDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT BDB_CopyRouteMacroDB (PCHAR, PCHAR);

#endif // OLD_CODE

#endif
