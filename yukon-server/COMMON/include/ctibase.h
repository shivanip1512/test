#pragma once

#include <rw\thr\mutex.h>
#include <rw/thr/guard.h>

#include "ctinexus.h"
#include "dllbase.h"

#define DEFAULT_YUKON_USER       ".\\yukon"
#define DEFAULT_YUKON_PASSWORD   "yukon"

#define PORTER_SHUTDOWN_EVENT    "CtiPorterShutdownEvent"
#define SCANNER_SHUTDOWN_EVENT   "CtiScannerShutdownEvent"
#define VANGOGH_SHUTDOWN_EVENT   "CtiVanGoghShutdownEvent"
