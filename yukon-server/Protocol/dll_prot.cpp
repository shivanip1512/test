/*-----------------------------------------------------------------------------*
*
* File:   dll_prot
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2007/07/10 21:00:52 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/db/db.h>
#include <rw\thr\mutex.h>

#include "dll_prot.h"
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

