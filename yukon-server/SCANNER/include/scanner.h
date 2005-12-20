/*-----------------------------------------------------------------------------*
*
* File:   scanner
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SCANNER/INCLUDE/scanner.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:20:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __SCANNER_H__
#define __SCANNER_H__
#pragma warning( disable : 4786)


#include "ctitime.h"
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


#endif  //  __SCANNER_H__
