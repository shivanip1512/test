/* Structures and defines for load management */

/* get the error file */

#ifndef GROUP_H
#define GROUP_H

//// #include "btrieve.h"
//#include "btrerrs.h"

#define GROUPFILENAME "DATA\\GROUPS.BDB"
#define GROUPMACROFILENAME "DATA\\MACGROUP.BDB"
#define GROUPHISTFILENAME "DATA\\CTLHIST.BDB"
#define GROUPRPTFILENAME "DATA\\CTLDAILY.BDB"
#define VERSACONFIGFILENAME "DATA\\VCONFIG.BDB"

/* needed for bdbcopy.exe */
#define BDB_GROUPFILENAME "GROUPS.BDB"
#define BDB_GROUPMACROFILENAME "MACGROUP.BDB"
#define BDB_GROUPRPTFILENAME "CTLDAILY.BDB"
#define BDB_VERSACONFIGFILENAME "VCONFIG.BDB"
#define BDB_GROUPHISTFILENAME "CTLHIST.BDB"

#define UNIQUE  0
#define GOLD    1
#define SILVER  2
#define BRONZE  3
#define LEAD    4

#define RELAY_A 1
#define RELAY_B 2
#define RELAY_C 3
#define RELAY_D 4

#define VERSACOM_EMESS_FLAG 0x0001      /* defined bit for e-message support */

#define PROTOCOL_EMETCON    1
#define PROTOCOL_RIPPLE     2
#define PROTOCOL_VERSACOMM  3
#define PROTOCOL_REMS100    4
#define PROTOCOL_POINT      6


/* Define the BTRIEVE Create structures */
#define GROUPNUMKEYS      24
#define GROUPMACRONUMKEYS 1
#define GROUPHISTNUMKEYS  8
#define CTLRPTNUMKEYS     6
#define VCONFIGNUMKEYS    13



/* EMETCON structure */
typedef struct _EMETCON {
    USHORT GroupLevel;
    USHORT GroupNumber;
    USHORT GoldNumber;
    USHORT Load;
} EMETCON;

/* Ripple structure */
typedef struct _RIPPLES {
    BYTE ControlMessage[7];
    BYTE RestoreMessage[7];
    USHORT ShedTime;
} RIPPLES;

/* VersaComm Structure */
typedef struct _VERSACOMM {
    USHORT UtilityID;
    USHORT Section;
    USHORT Class;
    USHORT Division;
    ULONG  Unique;
    USHORT Load;
} VERSACOMM;

/* REMS 100 structure */
typedef struct _REMS100 {
    USHORT AddressHigh;
    USHORT AddressLow;
    USHORT Function;
} REMS100;

/* DRP Pass structure */
typedef struct _DRPGROUP {
    CHAR DeviceName[23];
    CHAR PointName[24];
} DRPGROUP;

/* Group structure */
typedef struct _GROUP {
    CHAR GroupName[STANDNAMLEN];
    CHAR RouteName[STANDNAMLEN];
    CHAR ConfigName[STANDNAMLEN];
    USHORT Protocol;
    EMETCON Emetcon;
    RIPPLES Ripple;
    VERSACOMM VersaComm;
    REMS100 Rems100;
    DRPGROUP DRPGroup;
    USHORT StatusFlag;
    CHAR LoadType[10];
    FLOAT TotalKW;
    USHORT NumberInGroup;
    USHORT Alarm;
} GROUP;


/* Type for Groups in a group Macro Database */
typedef struct _MACROGROUPS {
    CHAR GroupName[STANDNAMLEN];            // user given logical Group name
    USHORT CurrentStatus;                   // current state of this group
    ULONG NextSendTime;                     // next refresh ect
    ULONG PCycNextSendTime;                 // next 7.5 min over lap
    USHORT LastPcycleCommand;               // last command for p-cycle
} MACROGROUPS;

/* Type for Group Macro Database */
typedef struct _GROUPMACRO {
    CHAR GroupMacroName[STANDNAMLEN];   // user given macro Group name
    CHAR LoadType[10];
    USHORT NextGroup;                       // next group to shed
    USHORT StartGroupNumber;                // group which was started with
    MACROGROUPS GroupInfo[30];              // user given logical Group name with info
} GROUPMACRO;


/* new field added for failover */
typedef struct _GROUP_HIST {
    CHAR GroupName[STANDNAMLEN];
    CHAR MonitorName[8];
    USHORT StatusFlag;
    ULONG ControlStartTime;
    ULONG ControlStopTime;
    ULONG DailyTotalTime;
    ULONG SeasonTotalTime;
    USHORT RepeatCount;
    SHORT CycleRate;
    SHORT Period;
    ULONG TimeOfLastChange;
} GROUP_HIST;

/* Group control history Report structure */
typedef struct _GROUP_HIST_REPORT {
    CHAR GroupName[STANDNAMLEN];
    CHAR MonitorName[8];
    USHORT StatusFlag;
    ULONG ControlStartTime;
    ULONG ControlStopTime;
    ULONG DailyTotalTime;
    ULONG SeasonTotalTime;
    ULONG StartHourTime;           // time the current hour started
    ULONG PeriodStartTime;         // time the current period started
    SHORT ActualPeriod;            // current period in minutes
    ULONG AdditionalDailyTotal;    // this is the additional daily total if we keep on track
    USHORT RepeatCount;
    SHORT CycleRate;
    SHORT Period;                  // real period same as shed log file
} GROUP_HIST_REPORT;


/*  Definition for VERSACOM Configuration DB */
/* VersaCom Load Config Structure */
typedef struct _VERSALOAD {
    USHORT CycleCount;
    USHORT ScramTime;
    USHORT ColdLoadTime;
    USHORT EnableDiscrOffset;
} VERSALOAD;



/* VersaCom Configuration Structure */
typedef struct _VERSACONFIG {
    CHAR VConfigName[STANDNAMLEN];
    USHORT UtilityID;
    USHORT Section;
    USHORT Class;
    USHORT Division;
    USHORT CyclePeriod;
    USHORT PropDispTime;
    VERSALOAD LoadConfig[3];
} VERSACONFIG;


/* Prototypes from GUPDATE.C */
IM_EX_CTIBASE INT InitGroupDB (VOID);
IM_EX_CTIBASE INT CreateGroupDB (PCHAR);
IM_EX_CTIBASE INT CreateGroupMacroDB (PCHAR);
IM_EX_CTIBASE INT CloseGroupDB(VOID);
IM_EX_CTIBASE INT GroupAdd (GROUP *);
IM_EX_CTIBASE INT GroupUpdate (GROUP *);
IM_EX_CTIBASE INT GroupDelete (GROUP *);
IM_EX_CTIBASE INT GroupGetEqual (GROUP *);
IM_EX_CTIBASE INT GroupGetGT (GROUP *);
IM_EX_CTIBASE INT GroupGetFirst (GROUP *);
IM_EX_CTIBASE INT GroupGetNext (GROUP *);
IM_EX_CTIBASE INT GroupGetPrev (GROUP *);
IM_EX_CTIBASE INT GroupGetLast (GROUP *);
IM_EX_CTIBASE INT EmetGetFirst (GROUP *);
IM_EX_CTIBASE INT EmetGetNext (GROUP *);
IM_EX_CTIBASE INT EmetGetPrev (GROUP *);
IM_EX_CTIBASE INT VersaGetEqual (GROUP *);
IM_EX_CTIBASE INT VersaGetFirst (GROUP *);
IM_EX_CTIBASE INT VersaGetNext (GROUP *);
IM_EX_CTIBASE INT VersaGetPrev (GROUP *);
IM_EX_CTIBASE INT GroupRecords (PULONG);
IM_EX_CTIBASE INT InitCtlHistDB (VOID);
IM_EX_CTIBASE INT CreateCtlHistDB (PCHAR);
IM_EX_CTIBASE INT CloseCtlHistDB(VOID);
IM_EX_CTIBASE INT InitCtlRptDB (VOID);
IM_EX_CTIBASE INT CreateCtlRptDB (PCHAR);
IM_EX_CTIBASE INT CloseCtlRptDB(VOID);
IM_EX_CTIBASE INT CtlHistAdd (GROUP_HIST *);
IM_EX_CTIBASE INT CtlHistUpdate (GROUP_HIST *);
IM_EX_CTIBASE INT CtlRptAdd (GROUP_HIST_REPORT *);
IM_EX_CTIBASE INT CtlRptUpdate (GROUP_HIST_REPORT *);
IM_EX_CTIBASE INT CtlHistGetEqual (GROUP_HIST *);
IM_EX_CTIBASE INT CtlHistGetGreaterEq (GROUP_HIST *);
IM_EX_CTIBASE INT CtlHistGetLessEq (GROUP_HIST *);
IM_EX_CTIBASE INT CtlHistGetOldestDate (GROUP_HIST *);
IM_EX_CTIBASE INT CtlHistGetNewestDate (GROUP_HIST *);

IM_EX_CTIBASE INT CtlHistGetFirst (GROUP_HIST *);
IM_EX_CTIBASE INT CtlHistGetNext (GROUP_HIST *);
IM_EX_CTIBASE INT CtlHistRecords (PULONG);
IM_EX_CTIBASE INT CtlHistGetPrev (GROUP_HIST *);
IM_EX_CTIBASE INT CtlRptGetEqual (GROUP_HIST_REPORT *);
IM_EX_CTIBASE INT CtlRptGroupGetEqual (GROUP_HIST_REPORT *);
IM_EX_CTIBASE INT CtlRptGroupGetFirst (GROUP_HIST_REPORT *);
IM_EX_CTIBASE INT CtlRptGroupGetNext (GROUP_HIST_REPORT *);
IM_EX_CTIBASE INT CtlRptGetFirst (GROUP_HIST_REPORT *);
IM_EX_CTIBASE INT CtlRptGetNext (GROUP_HIST_REPORT *);
IM_EX_CTIBASE INT CtlRptGetPrev (GROUP_HIST_REPORT *);
IM_EX_CTIBASE INT CtlHistDelete (GROUP_HIST *);
IM_EX_CTIBASE INT CtlRptDelete (GROUP_HIST_REPORT *);
IM_EX_CTIBASE INT CtlRptDeleteCurrent (void);
IM_EX_CTIBASE INT CtlRptRecords (PULONG);
IM_EX_CTIBASE INT InitVConfigDB (void);
IM_EX_CTIBASE INT CreateVConfigDB (PCHAR);
IM_EX_CTIBASE INT CloseVConfigDB(void);
IM_EX_CTIBASE INT VConfigAdd (VERSACONFIG *);
IM_EX_CTIBASE INT VConfigUpdate (VERSACONFIG *);
IM_EX_CTIBASE INT VConfigDelete (VERSACONFIG *);
// IM_EX_CTIBASE INT VConfigGetEqual (VERSACONFIG *);
IM_EX_CTIBASE INT VConfigGetFirst (VERSACONFIG *);
IM_EX_CTIBASE INT VConfigGetNext (VERSACONFIG *);
IM_EX_CTIBASE INT VConfigGetPrev (VERSACONFIG *);
IM_EX_CTIBASE INT VConfigLocate (VERSACONFIG *, GROUP *);
IM_EX_CTIBASE INT VConfigRecords (PULONG);
// IM_EX_CTIBASE INT CheckUtilID (USHORT);
// IM_EX_CTIBASE INT GetUtilID (PUSHORT);
IM_EX_CTIBASE INT GroupMacroAdd (GROUPMACRO *);
IM_EX_CTIBASE INT GroupMacroUpdate (GROUPMACRO *);
IM_EX_CTIBASE INT GroupMacroFastUpdate (GROUPMACRO *);
IM_EX_CTIBASE INT GroupMacroDelete (GROUPMACRO *);
IM_EX_CTIBASE INT GroupMacroGetEqual (GROUPMACRO *);
IM_EX_CTIBASE INT GroupMacroGetFirst (GROUPMACRO *);
IM_EX_CTIBASE INT GroupMacroGetNext (GROUPMACRO *);
IM_EX_CTIBASE INT GroupMacroGetPrev (GROUPMACRO *);
IM_EX_CTIBASE INT GroupMacroGetLast (GROUPMACRO *);
IM_EX_CTIBASE INT GroupMacroGetGE (GROUPMACRO *);
IM_EX_CTIBASE INT GroupMacroRecords (PULONG);
IM_EX_CTIBASE INT GetLoadGroupDevice (GROUP *);
IM_EX_CTIBASE INT DeleteLoadGroupMacroDevice (GROUP *);
IM_EX_CTIBASE INT DeleteLoadGroupDevice (GROUP *);
IM_EX_CTIBASE INT GetLoadGroupPoint (GROUP *);
IM_EX_CTIBASE INT DeleteLoadGroupMacroPoint (GROUP *);
IM_EX_CTIBASE INT DeleteLoadGroupPoint (GROUP *);
IM_EX_CTIBASE INT OpenCtlHistDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT CtlHistxRecords (PULONG, PBYTE);
IM_EX_CTIBASE INT FO_CopyNewCtlHistRecs(PBYTE, PULONG, PULONG);
IM_EX_CTIBASE INT FO_CopyCtlHistDB (PBYTE, PCHAR, PULONG);
IM_EX_CTIBASE INT CreateCtlHistDB (PCHAR);
IM_EX_CTIBASE INT FO_InitCtlHistDB (PCHAR);
IM_EX_CTIBASE INT FO_GetLastTimeChange (PBYTE, PULONG);
IM_EX_CTIBASE INT FO_CtlHistConvert (void);
IM_EX_CTIBASE INT Check4VConfigDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT Check4CtlRptDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT Check4CtlHistDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT Check4GroupMacroDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT Check4GroupDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT FO_CopyGroupDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT FO_CopyGroupMacroDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT FO_CopyVConfigDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT FO_CopyCtlRptDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT OpenVConfigDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT OpenGroupDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT OpenGroupMacroDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT OpenCtlRptDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT BDB_CopyGroupDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT BDB_CopyGroupMacroDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT BDB_CopyVConfigDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT BDB_CopyCtlRptDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT BDB_CopyCtlHistDB (PCHAR, PCHAR);

/* Prototypes from PSUP.C */
void IM_EX_CTIBASE GetShedTime (USHORT, PCHAR);
void IM_EX_CTIBASE GetCommandName (SHORT, USHORT, PCHAR);
IM_EX_CTIBASE INT VersacomWordFill (VSTRUCT *, GROUP *, USHORT, USHORT, USHORT, ULONG, ULONG);
IM_EX_CTIBASE INT VPercentControl (USHORT, USHORT);

#endif
