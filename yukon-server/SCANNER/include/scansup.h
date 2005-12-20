/*-----------------------------------------------------------------------------*
*
* File:   scansup
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SCANNER/INCLUDE/scansup.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:20:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __SCANSUP_H__
#define __SCANSUP_H__
#pragma warning( disable : 4786)


#include "ctitime.h"

/* Prototypes from SCANSUP.C */
IM_EX_SCANSUP CtiTime   FirstScan(const CtiTime&, ULONG);
IM_EX_SCANSUP INT      ReportError (CtiDeviceBase *, USHORT);
IM_EX_SCANSUP INT      TracePrint (PBYTE, USHORT);

//IM_EX_SCANSUP INT      InitDLCScanning (VOID);
//IM_EX_SCANSUP INT      Time2ScanDLCValue (CtiTime &, const CtiTime &);
//IM_EX_SCANSUP INT      Time2ScanDLCStatus(CtiTime &, const CtiTime &);

IM_EX_SCANSUP INT      ProcessDLCStatus (INT, DSTRUCT *, CtiDeviceBase *);
IM_EX_SCANSUP ULONG    NextScan (ULONG, ULONG, USHORT);

/* Prototypes from SCAN_DLC.C */

#if 0
IM_EX_SCANSUP INT      RequestDLCValue (CtiDeviceBase *, SCANPOINT *);
IM_EX_SCANSUP INT      ProcessDLCValue (USHORT, DSTRUCT *, CtiDeviceBase *);
IM_EX_SCANSUP INT      BPreambleLoad (ROUTE *, BSTRUCT *);
IM_EX_SCANSUP INT      InsertPlugData (CtiDeviceBase *);
IM_EX_SCANSUP INT      InitOneScanPoint(SCANPOINT *, CtiDeviceBase *);
IM_EX_SCANSUP INT      InitOneStatusPoint(STATPOINT *);
IM_EX_SCANSUP INT      RequestDLCStatus (CtiDeviceBase *, STATPOINT *);
IM_EX_SCANSUP INT      InsertPlugStatus (CtiDeviceBase *);
IM_EX_SCANSUP FLOAT    TranslateStatusValue (USHORT, CTIPOINT *, PUSHORT);
#endif

#endif // #ifndef __SCANSUP_H__
