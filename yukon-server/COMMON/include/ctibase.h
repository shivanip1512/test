
#ifndef __CTIBASE_H__
#define __CTIBASE_H__

/*-----------------------------------------------------------------------------*
*
* File:   ctibase
*
* Date:   10/5/1999
*
* Author: Corey G. Plender
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw\thr\mutex.h>
#include <rw/thr/guard.h>

#include "ctinexus.h"
#include "dllbase.h"

#define DEFAULT_YUKON_USER       ".\\yukon"
#define DEFAULT_YUKON_PASSWORD   "yukon"

#define PORTER_SHUTDOWN_EVENT    "CtiPorterShutdownEvent"
#define SCANNER_SHUTDOWN_EVENT   "CtiScannerShutdownEvent"
#define VANGOGH_SHUTDOWN_EVENT   "CtiVanGoghShutdownEvent"

#endif // #ifndef __CTIBASE_H__
