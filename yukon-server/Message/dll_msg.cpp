/*-----------------------------------------------------------------------------*
*
* File:   dll_msg
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7.14.2 $
* DATE         :  $Date: 2008/11/17 19:46:17 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <winbase.h>
#include <winsock.h>
#include <stdio.h>
#include <string.h>
#include <process.h>

#include <rw/db/db.h>
#include <rw\thr\mutex.h>

#include "dsm2.h"
#include "os2_2w32.h"
#include "cticalls.h"
#include "connection.h"

#include "dllbase.h"
#include "utility.h"



BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            identifyProject(CompileInfo);

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
            break;
        }
    }

    return TRUE;
}


