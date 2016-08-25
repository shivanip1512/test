/* Global definitions for the scanner */

#include "dlldefs.h"
#include "scanner_debug.h"

#define S_QUIT_EVENT                   0
#define S_SCAN_EVENT                   1
#define S_MAX_EVENT                    2                    // This  one needs to move with the rest

#define S_LOCK_MUTEX                   ((S_MAX_EVENT) + 0)

#define S_MAX_MUTEX                    ((S_MAX_EVENT) + 1)  // This  one needs to move with the rest

extern HANDLE      hScannerSyncs[];

extern bool SuspendLoadProfile;

namespace Cti {
namespace Scanner {
extern void CreateSyncEvents();
extern void DestroySyncEvents();
}}
