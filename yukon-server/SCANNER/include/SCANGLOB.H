/* Global definitions for the scanner */
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   SCANGLOB
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SCANNER/INCLUDE/SCANGLOB.H-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/07/30 21:16:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "dlldefs.h"

#define S_QUIT_EVENT                   0
#define S_SCAN_EVENT                   1
#define S_MAX_EVENT                    2                    // This  one needs to move with the rest

#define S_LOCK_MUTEX                   ((S_MAX_EVENT) + 0)

#define S_MAX_MUTEX                    ((S_MAX_EVENT) + 1)  // This  one needs to move with the rest

#define SCANNER_DEBUG_DBRELOAD          0x00000040
#define SCANNER_DEBUG_NEXTSCAN          0x00000100
#define SCANNER_DEBUG_OUTREQUESTS       0x00000200
#define SCANNER_DEBUG_INREPLYS          0x00000400

// IM_EX_SCANSUP extern HEV         ScanSem;
// IM_EX_SCANSUP extern HMTX        LockSem;
IM_EX_SCANSUP extern HANDLE      hScannerSyncs[];

IM_EX_SCANSUP extern USHORT      LogFlag;
IM_EX_SCANSUP extern USHORT      DAS08Present;
IM_EX_SCANSUP extern USHORT      SuspendLoadProfile;
IM_EX_SCANSUP extern USHORT      CCUNoQueue;
IM_EX_SCANSUP extern USHORT      DLCStatusPriority;
IM_EX_SCANSUP extern USHORT      DLCValuePriority;
IM_EX_SCANSUP extern USHORT      RTUScanRate;

