#include "precompiled.h"

#include <iomanip>
#include <iostream>

#include <rw\thr\thrfunc.h>
#include <rw\rwerr.h>
#include <rw\thr\mutex.h>

#include "cparms.h"
#include "ctivangogh.h"
#include "dllbase.h"
#include "dlldefs.h"
#include "dllvg.h"
#include "stdexcepthdlr.h"
#include "logger.h"

using namespace std;

DLLIMPORT extern BOOL  bGCtrlC;
DLLIMPORT extern CtiLogger dout;

static _CRT_ALLOC_HOOK pfnOldCrtAllocHook = NULL;

static int MyAllocHook(int nAllocType, void *pvData,
                       size_t nSize, int nBlockUse, long lRequest,
                       const unsigned char * szFileName, int nLine );



int DispatchMainFunction(int argc, char **argv)
{
    try
    {
        CtiVanGogh VanGogh;

        SET_CRT_OUTPUT_MODES;
        if(gConfigParms.isOpt("DEBUG_MEMORY") && gConfigParms.isTrue("DEBUG_MEMORY") )
            ENABLE_CRT_SHUTDOWN_CHECK;

        pfnOldCrtAllocHook = _CrtSetAllocHook(MyAllocHook);

        int i = VanGogh.execute();

        while( !bGCtrlC )
        {
            Sleep(3000);
        }

        if( RW_THR_COMPLETED != VanGogh.join( (gConfigParms.getValueAsInt("SHUTDOWN_TERMINATE_TIME", 300))*1000) )
        {
            CreateMiniDump("dispatch");

            cerr << "***** EXCEPTION ******* Terminating Dispatch" << endl;
            VanGogh.terminate();
        }

        _CrtSetAllocHook(pfnOldCrtAllocHook);

        return(i);
    }
    catch(const RWxmsg& x)
    {
        cout << "main() Exception: " << x.why() << endl;
    }

    return 0;
}


static int MyAllocHook(int nAllocType, void *pvData,
                       size_t nSize, int nBlockUse, long lRequest,
                       const unsigned char * szFileName, int nLine )
{
    #if 0
    static ULONG lastAlloc = 0;
    static ULONG prevLastAlloc = 0;
    static ULONG pprevLastAlloc = 0;

    int twnetyfourcnt = 0;

    if(lRequest > 1000000)
    {
        if( (nSize == 24) )
        {
            if(lastAlloc == (lRequest - 1))
            {
                twnetyfourcnt++;

                if(prevLastAlloc == (lRequest - 2))
                {
                    twnetyfourcnt++;

                    if(pprevLastAlloc == (lRequest - 3))
                    {
                        twnetyfourcnt++;
                    }
                    pprevLastAlloc = prevLastAlloc;
                }
                prevLastAlloc = lastAlloc;
            }

            twnetyfourcnt++;

            lastAlloc = lRequest;
        }
        else if((nSize == 19) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 176) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 52) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 416) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 76) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 88) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 80) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 144) )
        {
            twnetyfourcnt++;
        }
    }
    #endif

#ifdef IGNORE_CRT_ALLOC
    if(_BLOCK_TYPE(nBlockUse) == _CRT_BLOCK)  // Ignore internal C runtime library allocations
        return TRUE;
#endif
    extern int _crtDbgFlag;

    if( ((_CRTDBG_ALLOC_MEM_DF & _crtDbgFlag) == 0) && ( (nAllocType == _HOOK_ALLOC) || (nAllocType == _HOOK_REALLOC) ) )
    {
        // Someone has disabled that the runtime should log this allocation
        // so we do not log this allocation
        if(pfnOldCrtAllocHook != NULL)
            pfnOldCrtAllocHook(nAllocType, pvData, nSize, nBlockUse, lRequest, szFileName, nLine);
        return TRUE;
    }

    // call the previous alloc hook
    if(pfnOldCrtAllocHook != NULL)
        pfnOldCrtAllocHook(nAllocType, pvData, nSize, nBlockUse, lRequest, szFileName, nLine);
    return TRUE; // allow the memory operation to proceed
}


