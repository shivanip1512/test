/*-----------------------------------------------------------------------------*
*
* File:   dll_main
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2007/07/10 21:00:37 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <stdio.h>
#include <string.h>
#include <process.h>
#include <windows.h>
#include <winbase.h>

#include "cticalls.h"
#include "ctinexus.h"
#include "proclog.h"
#include "errclient.h"
#include "errserver.h"

#include "dllmain.h"

#include "logger.h"
#include "utility.h"

#pragma data_seg(".shr")                  // A single instance of these guys per DLL load.
INT   Initialized  = 0;                   // Shared proclogger.
INT   ConnectCount = 0;                   // Count of remotes
INT   ErrorThreadCount = 0;
#pragma data_seg()

CErrClient     *ErrClient     = NULL;     // Global Client Member for "C" function calls

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            identifyProject(CompileInfo);

            ErrClient = new CErrClient;      // This is the client side connection... for "C" functions mostly
            break;
        }
        case DLL_THREAD_ATTACH:
        {
            break;
        }
        case DLL_THREAD_DETACH:
        {
            break;
        }
        case DLL_PROCESS_DETACH:
        {
            if( ErrClient != NULL )
            {
                delete ErrClient;                // Clean up a bit!
                ErrClient = NULL;                // Clean up a bit!
            }

            break;
        }
    }

    return TRUE;
}
