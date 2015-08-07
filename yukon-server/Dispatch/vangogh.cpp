#include "precompiled.h"

#include <iomanip>
#include <iostream>

#include "cparms.h"
#include "ctivangogh.h"
#include "dllbase.h"
#include "dlldefs.h"
#include "dllvg.h"
#include "stdexcepthdlr.h"
#include "logger.h"
#include "module_util.h"

using namespace std;

DLLIMPORT extern BOOL  bGCtrlC;

static _CRT_ALLOC_HOOK pfnOldCrtAllocHook = NULL;

static int MyAllocHook(int nAllocType, void *pvData,
                       size_t nSize, int nBlockUse, long lRequest,
                       const unsigned char * szFileName, int nLine );



int DispatchMainFunction(int argc, char **argv)
{
    CtiVanGogh VanGogh;

    SET_CRT_OUTPUT_MODES;
    if(gConfigParms.isOpt("DEBUG_MEMORY") && gConfigParms.isTrue("DEBUG_MEMORY") )
        ENABLE_CRT_SHUTDOWN_CHECK;

    pfnOldCrtAllocHook = _CrtSetAllocHook(MyAllocHook);

    int i = VanGogh.execute();

    while( !bGCtrlC )
    {
        dout->poke();  //  called every 3 seconds

        if( Cti::isTimeToReportMemory(CtiTime::now()) )
        {
            CTILOG_INFO(dout, Cti::reportPrivateBytes(CompileInfo));
            CTILOG_INFO(dout, Cti::reportProcessTimes(CompileInfo));
            CTILOG_INFO(dout, Cti::reportProcessorTimes());
        }

        Sleep(3000);
    }

    if( ! VanGogh.join((gConfigParms.getValueAsInt("SHUTDOWN_TERMINATE_TIME", 300))*1000) )
    {
        CreateMiniDump("dispatch");

        CTILOG_ERROR(dout, "Could not Join Dispatch - Terminating");
        VanGogh.terminate();
    }

    _CrtSetAllocHook(pfnOldCrtAllocHook);

    return i;
}


static int MyAllocHook(int nAllocType, void *pvData,
                       size_t nSize, int nBlockUse, long lRequest,
                       const unsigned char * szFileName, int nLine )
{
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


