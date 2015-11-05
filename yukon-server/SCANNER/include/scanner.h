#pragma once

#include "ctitime.h"
#include "dlldefs.h"
#include "dllbase.h"

#define SCANNER_APPLICATION_NAME "Scanner Service"

#define RESULT_THREAD_STK_SIZE 100000
#define DAS08_THREAD_STK_SIZE  50000

#define NEVER_SCAN_TIME         0xFFFFFFFF
#define TARDY_TIME_DELAY        3600L

/* Prototypes from SCANNER.C */
void ResultThread (PVOID);
void ScannerCleanUp (void);
