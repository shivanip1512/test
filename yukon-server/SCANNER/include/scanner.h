
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   scanner
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SCANNER/INCLUDE/scanner.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw\rwtime.h>
#include "dlldefs.h"
#include "dllbase.h"

#define RESULT_THREAD_STK_SIZE 100000
#define DAS08_THREAD_STK_SIZE  50000

#define NEVER_SCAN_TIME         0xFFFFFFFF
#define TARDY_TIME_DELAY        3600L

#define PRIORITY_STATUS    9
#define PRIORITY_VALUE     10

/* Prototypes from SCANNER.C */
VOID ResultThread (PVOID);
VOID ScannerCleanUp (VOID);

