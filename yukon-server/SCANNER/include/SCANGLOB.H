/* Global definitions for the scanner */

#include "dlldefs.h"

#define S_QUIT_EVENT                   0
#define S_SCAN_EVENT                   1
#define S_MAX_EVENT                    2                    // This  one needs to move with the rest

#define S_LOCK_MUTEX                   ((S_MAX_EVENT) + 0)

#define S_MAX_MUTEX                    ((S_MAX_EVENT) + 1)  // This  one needs to move with the rest

#define SCANNER_DEBUG_ACCUMSCAN         0x00000002
#define SCANNER_DEBUG_INTEGRITYSCAN     0x00000004
#define SCANNER_DEBUG_GENERALSCAN       0x00000008
#define SCANNER_DEBUG_LPSCAN            0x00000010
#define SCANNER_DEBUG_DEVICEANALYSIS    0x00000020
#define SCANNER_DEBUG_DBRELOAD          0x00000040
#define SCANNER_DEBUG_OUTLIST           0x00000080
#define SCANNER_DEBUG_NEXTSCAN          0x00000100
#define SCANNER_DEBUG_OUTREQUESTS       0x00000200
#define SCANNER_DEBUG_INREPLIES         0x00000400
#define SCANNER_DEBUG_DYNAMICDATA       0x00001000

IM_EX_SCANSUP extern HANDLE      hScannerSyncs[];

IM_EX_SCANSUP extern bool SuspendLoadProfile;

