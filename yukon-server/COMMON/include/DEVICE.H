/* include the btrieve interface routines error definitions */
#ifndef DEVICE_H
#define DEVICE_H

#include "devicetypes.h"
#include "dlldefs.h"

/* Device and point status defines */
#define INHIBITED                0x0001
#define CONTROLINHIBIT           0x0002
#define ALARMINHIBIT             0x0004

#define DRPPASS                  0x0008
#define PLUGWARNINGS             0x0010
#define PLUGLIMITS               0x0020
#define MANUALENTRY              0x0040
#define INTERNALCONTROLINHIBIT   0x0100
#define CALCNOTIMECHECK          0X0200

/* Scan status defines */
#define SCANNEWRECORD        0x0000
#define SCANSTARTING         0x0000
#define SCANFROZEN           0x0002
#define SCANFREEZEPENDING    0x0004
#define SCANSCANPENDING      0x0008
#define SCANFREEZEFAILED     0x0010
#define SCANRESETTING        0x0020
#define SCANRESETFAILED      0x0040
#define TOUREGISTER          0x0080
#define METERREAD            0x0100
#define ALARMED              0x0200         // alarmed state
#define SCANFORCED           0x0400         // Flag set when we are forcing a scan because of a control operation
#define SCANEXCEPTION        0x0800         // Flag set when RTU is exception scanned
#define SCANRTUINTEGTOGGLE   0x1000         // Toggled on successive RTU Integrity Scans.. Sync'd by intermediate full scans (accum type scans)

/* points type */
#define DEFAULT                 0
#define TWOSTATEPOINT           1
#define THREESTATEPOINT         2
#define ANALOGPOINT             3
#define ACCUMULATORPOINT        4
#define DEMANDACCUMPOINT        5
#define CONTROLPOINT            6
#define PSEUDOVALUEPOINT        7
#define PSEUDOSTATUSPOINT       8
#define PSEUDOALARMPOINT        9
#define PSEUDOCONTROLPOINT      10


/* Device Types have been moved to devicetypes.h */


#define HILIMITMAX              1000000
#define LOWLIMITMAX             -1000000

/* UOM defines */
#define UOM_UNDEFINED   99
#define UOM_MWH         1
#define UOM_MW          2
#define UOM_KWH         3
#define UOM_KW          4
#define UOM_KQH         5
#define UOM_KQ          6
#define UOM_KVARH       7
#define UOM_KVAR        8
#define UOM_VH          9
#define UOM_V2H         10
#define UOM_VOLTS       11
#define UOM_AMPS        12
#define UOM_PFACTOR     13
#define UOM_TEMPF       14
#define UOM_TEMPC       15
#define UOM_WATER_CF    16
#define UOM_GAS_CF      17
#define UOM_FEET        18
#define UOM_GAL_PM      19
#define UOM_PERCENT     20
#define UOM_PULES       21
#define UOM_LEVEL       22
#define UOM_KVA         23


/* Structure definitions for the device and point databases */

#define DEVICENUMKEYS 10
#define POINTNUMKEYS 10
#define CONTROLNUMKEYS 4


#define STANDNAMLEN 20

/* Structures and defines for Device database */
class DEVICE {
public:
    CHAR DeviceName[STANDNAMLEN];
    USHORT DeviceType;
    ULONG Address;
    USHORT Status;          /* Inhibited, Tagged, Alarm Inhibit, TOU Reg, Metering Status */
    USHORT LSInterval;
    USHORT LeadLoadAddress;
    USHORT LeadMeterAddress;
    CHAR ConfigName[8];
    CHAR RouteName[3][STANDNAMLEN];
    USHORT SecurityLevel;
    ULONG ScanRate;
    ULONG AccumScanRate;
    ULONG InitTime;             /* Start of dynamic portion of record */
    ULONG NextScan;
    ULONG NextAccumScan;
    ULONG LastFullScan;
    ULONG LastExceptionScan;
    USHORT ScanStatus;
    ULONG LastFreezeS;
    USHORT LastFreezeMs;
    USHORT LastFreezeNumber;
    ULONG PrevFreezeS;
    USHORT PrevFreezeMs;
    USHORT PrevFreezeNumber;
    USHORT SSpec;
    ULONG TimeOfLastChange;     /* added this failover mirroring */
    // STATS Stats;
};


/* Point Data Quality Mask definitions  */
#define QUALITYSTATUSMASK   0x003f
#define QUALITYMASK         0x3fc0
#define DATAINVALID         0X0200
#define UNREASONABLE        0x0400
#define PLUGGED             0x0800
#define MANUAL              0x1000
#define OUTOFSCAN           0x2000
#define DSTACTIVE2          0x4000

/* Point Data Quality Status definitions  */
#define INVALIDDATA  0x0001
#define UNKNOWNDATA  0x0002
#define HLDATA  0x0003
#define HWDATA  0x0004
#define LLDATA  0x0005
#define LWDATA  0x0006
#define OVERFLOWDATA  0x0010
#define DEVFILLERDATA  0x0011
#define POWERFAILDATA  0x0012
#define PARTIALDATA  0x0013
#define HARDARMDATA  0x0014
#define QUESTIONDATA  0x0016
#define MSFILLERDATA  0x0017
#define MSSHIFTDATA  0x0018
#define PFSHIFTDATA  0x0019
#define STATECHANGEDATA  0x001A

/* Max number of dq counts */
#define DQCOUNTMAX   9999

class CTIPOINT {
public:
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    USHORT PointType;
    USHORT PointOffset;
    USHORT MeasuredUOM;
    USHORT CalculatedUOM;
    USHORT Status;          // Alarm Inhibit, Enabled, Tagged, DRP Pass
    FLOAT Multiplier;
    FLOAT Offset;
    FLOAT PlugValue;
    FLOAT DeadBand;
    FLOAT HiWarning;
    FLOAT HiLimit;
    FLOAT LowWarning;
    FLOAT LowLimit;
    ULONG FileNumber;
    USHORT OpenTime;      // this field is also the Normal State value for statuses
    USHORT CloseTime;
    ULONG AlarmMask;
    USHORT AudibleMask;
    USHORT AlarmDRPMask;
    USHORT AlarmStatus;
    USHORT SecurityLevel;
    ULONG LogInterval;
    ULONG ArchiveInterval;
    SHORT StateNameNumber;
    FLOAT CurrentValue;         /* Start of dynamic portion of record */
    ULONG CurrentTime;
    USHORT CurrentQuality;
    FLOAT PreviousValue;
    ULONG PreviousTime;
    USHORT PreviousQuality;            // also used for alarm state
    USHORT PresentPulses;
    USHORT PreviousPulses;
    ULONG NextLogTime;
    ULONG NextArchiveTime;
    USHORT DQCount;
    ULONG TimeOfLastChange;     /* added this failover mirroring */
};

/* Structures that relate to the control point database */
class CONTROLMAP {
    CHAR ControlDeviceName[STANDNAMLEN];
    CHAR ControlPointName[STANDNAMLEN];
    CHAR StatusDeviceName[STANDNAMLEN];
    CHAR StatusPointName[STANDNAMLEN];
};

/* Structure for updating device performace statistics */
class DEVICEPERF {
    CHAR DeviceName[STANDNAMLEN];
    USHORT Error;
};

/* archive structure for master station data */
typedef struct _LSFILETYPE {
    ULONG TimeStamp;
    FLOAT Value;
    USHORT DataQuality;
} LSFILETYPE;


/* Prototypes from DUPDATE.C */
#ifdef MONKEYS_FLY_NAKED_TO_THE_MOON

INT InitDPCDB (VOID);
IM_EX_CTIBASE INT InitPointDB (VOID);
IM_EX_CTIBASE INT CreatePointDB (PCHAR);
IM_EX_CTIBASE INT OpenPointDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT InitDeviceDB (VOID);
IM_EX_CTIBASE INT CreateDeviceDB (PCHAR);
IM_EX_CTIBASE INT OpenDeviceDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT InitControlDB (VOID);
IM_EX_CTIBASE INT CreateControlDB (PCHAR);
IM_EX_CTIBASE INT CloseDPCDB (VOID);
IM_EX_CTIBASE INT CloseDeviceDB (VOID);
IM_EX_CTIBASE INT ClosePointDB (VOID);
IM_EX_CTIBASE INT CloseControlDB (VOID);
IM_EX_CTIBASE INT DeviceAdd (DEVICE *);
IM_EX_CTIBASE INT DeviceAddRepeat (DEVICE *);
IM_EX_CTIBASE INT DeviceUpdate (DEVICE *);
IM_EX_CTIBASE INT DeviceGetAddressEqual (DEVICE *);
IM_EX_CTIBASE INT DeviceGetAddressNext (DEVICE *);
IM_EX_CTIBASE INT DeviceGetAddressTypeEqual (DEVICE *);
IM_EX_CTIBASE INT DeviceUpdateRepeat (DEVICE *);
IM_EX_CTIBASE INT DeviceFastUpdate (DEVICE *);
IM_EX_CTIBASE INT DeviceDelete (DEVICE *);
IM_EX_CTIBASE INT DeviceGetEqual (DEVICE *);
IM_EX_CTIBASE INT DeviceLock (DEVICE *);
IM_EX_CTIBASE INT DeviceUnLock (DEVICE *);
IM_EX_CTIBASE INT DeviceGetFirst (DEVICE *);
IM_EX_CTIBASE INT DeviceGetNext (DEVICE *);
IM_EX_CTIBASE INT DeviceGetGT (DEVICE *);
IM_EX_CTIBASE INT DeviceGetPrev (DEVICE *);
IM_EX_CTIBASE INT DeviceGetFirstLeadMeter (DEVICE *);
IM_EX_CTIBASE INT DeviceGetNextLeadMeter (DEVICE *);
IM_EX_CTIBASE INT DeviceGetFirstLeadLoad (DEVICE *);
IM_EX_CTIBASE INT DeviceGetNextLeadLoad (DEVICE *);
IM_EX_CTIBASE INT DeviceGetTypeGEFirst (DEVICE *);
IM_EX_CTIBASE INT DeviceGetTypeGENext (DEVICE *);
IM_EX_CTIBASE INT DeviceGetNextScanLE (DEVICE *);
IM_EX_CTIBASE INT DeviceGetNextScanGT (DEVICE *);
IM_EX_CTIBASE INT DeviceGetNextAccumScanLE (DEVICE *);
IM_EX_CTIBASE INT DeviceGetNextAccumScanGT (DEVICE *);
IM_EX_CTIBASE INT ResetDevicePerf (DEVICE *);
IM_EX_CTIBASE INT PointAdd (CTIPOINT *);
IM_EX_CTIBASE INT PointUpdate (CTIPOINT *);
IM_EX_CTIBASE INT PointFastUpdate (CTIPOINT *);
IM_EX_CTIBASE INT PointDelete (CTIPOINT *);
IM_EX_CTIBASE INT PointGetEqual (CTIPOINT *);
IM_EX_CTIBASE INT PointLock (CTIPOINT *);
IM_EX_CTIBASE INT PointUnLock (CTIPOINT *);
IM_EX_CTIBASE INT PointGetFirst (CTIPOINT *);
IM_EX_CTIBASE INT PointGetNext (CTIPOINT *);
IM_EX_CTIBASE INT PointGetGT (CTIPOINT *);
IM_EX_CTIBASE INT PointGetPrev (CTIPOINT *);
IM_EX_CTIBASE INT PointGetLastFileNumber (CTIPOINT *);
IM_EX_CTIBASE INT PointGetFileNumber (CTIPOINT *);
IM_EX_CTIBASE INT PointGetDeviceFirst (CTIPOINT *);
IM_EX_CTIBASE INT PointGetDeviceNext (CTIPOINT *);
IM_EX_CTIBASE INT PointGetDeviceTypeOffsetEqual (CTIPOINT *);
IM_EX_CTIBASE INT PointGetDeviceTypeFirst (CTIPOINT *);
IM_EX_CTIBASE INT PointGetDeviceTypeNext (CTIPOINT *);
IM_EX_CTIBASE INT ControlAdd (CONTROLMAP *);
IM_EX_CTIBASE INT ControlUpdate (CONTROLMAP *);
IM_EX_CTIBASE INT ControlDelete (CONTROLMAP *);
IM_EX_CTIBASE INT ControlGetEqual (CONTROLMAP *);
IM_EX_CTIBASE INT ControlGetFirst (CONTROLMAP *);
IM_EX_CTIBASE INT ControlGetNext (CONTROLMAP *);
IM_EX_CTIBASE INT ControlGetPrev (CONTROLMAP *);
IM_EX_CTIBASE INT DeviceRecords (PULONG);
IM_EX_CTIBASE INT PointRecords (PULONG);
IM_EX_CTIBASE INT ControlRecords (PULONG);
IM_EX_CTIBASE INT DeviceGetFirstDevType (DEVICE *);
IM_EX_CTIBASE INT DeviceGetNextEqDevType (DEVICE *);
IM_EX_CTIBASE INT DeviceGetNextDevType (DEVICE *);
IM_EX_CTIBASE INT DeviceGetGreaterDevType (DEVICE *);
IM_EX_CTIBASE INT PointGetFirstType (CTIPOINT *);
IM_EX_CTIBASE INT PointGetNextType (CTIPOINT *);
IM_EX_CTIBASE INT PointGetNextEqType (CTIPOINT *);
IM_EX_CTIBASE INT PointGetGreaterType (CTIPOINT *);
IM_EX_CTIBASE INT PointOffsetGetEqual (CTIPOINT *);
IM_EX_CTIBASE INT PointGetOffset (CTIPOINT *);
IM_EX_CTIBASE INT DeviceClearAll (VOID);
IM_EX_CTIBASE INT DeviceClearEqual (DEVICE *);
IM_EX_CTIBASE INT FO_CopyDeviceDB (PBYTE, PCHAR);
IM_EX_CTIBASE INT BDB_CopyDeviceDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT FO_CopyNewDeviceRecs (PBYTE, PULONG, PULONG);
IM_EX_CTIBASE INT FO_IntegrityCheckDeviceRecs (PBYTE FOPosBlk, PULONG RecCnt);
IM_EX_CTIBASE INT FO_IntegrityReportDeviceRecs (PCHAR SrcPathName,PCHAR DestPathName,PULONG RecCnt);
IM_EX_CTIBASE INT FO_CompareDeviceSourceToTarget (DEVICE *SrcRecord, DEVICE *Target);
IM_EX_CTIBASE INT DevicexRecords (PULONG, PBYTE);
IM_EX_CTIBASE INT PointxRecords (PULONG, PBYTE);
IM_EX_CTIBASE INT FO_CopyPointDB (PBYTE, PCHAR);
IM_EX_CTIBASE INT BDB_CopyPointDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT FO_CopyNewPointRecs (PBYTE, PULONG, PULONG);
IM_EX_CTIBASE INT FO_IntegrityCheckPointRecs (PBYTE FOPosBlk, PULONG RecCnt);
IM_EX_CTIBASE INT FO_IntegrityReportPointRecs (PCHAR SrcPathName, PCHAR DestPathName, PULONG RecCnt);
IM_EX_CTIBASE INT FO_ComparePointSourceToTarget (CTIPOINT *SrcRecord, CTIPOINT *Target);
IM_EX_CTIBASE INT FO_InitDeviceDB (PCHAR);
IM_EX_CTIBASE INT FO_InitPointDB (PCHAR);
IM_EX_CTIBASE INT FO_PointConvert (void);
IM_EX_CTIBASE INT FO_DeviceConvert (void);
IM_EX_CTIBASE INT Check4ControlDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT Check4PointDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT Check4DeviceDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT OpenControlDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT FO_CopyControlDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT BDB_CopyControlDB (PCHAR, PCHAR);
#endif

IM_EX_CTIBASE INT nfpexec (USHORT, USHORT, DEVICE *);

/* Prototypes from PSUP.C */
IM_EX_CTIBASE INT CheckDataStateQuality (DEVICE *, CTIPOINT *, FLOAT *, USHORT, ULONG, USHORT, USHORT, USHORT);
IM_EX_CTIBASE INT SavePointHistory (CTIPOINT *);
void IM_EX_CTIBASE GetUomLabel (USHORT, PCHAR);
IM_EX_CTIBASE INT LogPointValue (CTIPOINT *);
IM_EX_CTIBASE INT GetDQLabel (USHORT, PCHAR);
IM_EX_CTIBASE INT setPlugValue (USHORT, FLOAT *, CTIPOINT *);
IM_EX_CTIBASE INT ClearAlarmStatus (CTIPOINT *);
IM_EX_CTIBASE INT SaveLSHistory (CTIPOINT  *, ULONG, FLOAT, USHORT, ULONG);

#endif
