#ifndef __PORTER_STRUCTS_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   porter_structs
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/INCLUDE/porter_structs.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:27 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __PORTER_STRUCTS_H__

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
} CtiPortStruct;

typedef struct _REMOTE {
    CHAR RemoteName[STANDNAMLEN];
    USHORT Port;
    USHORT Remote;
    USHORT Type;
    USHORT Radio;
    USHORT PostDelay;
    USHORT Status;
    ULONG ResetTime;
    STATS  Stats;         /* Start of dynamic portion of record */
} REMOTE;

/* Structures and defines for Device database */
typedef struct _DEVICE {
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
    STATS Stats;
} DEVICE;


#endif      // #ifndef __PORTER_STRUCTS_H__

