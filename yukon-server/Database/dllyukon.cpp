#include "precompiled.h"

#include <iostream>
using namespace std;

#include "tbl_route.h"
#include "tbl_rtcarrier.h"
#include "tbl_rtcomm.h"
#include "tbl_rtmacro.h"
#include "tbl_rtrepeater.h"
#include "tbl_rtversacom.h"

#include "dlldefs.h"
#include "msg_pcrequest.h"

#include "pt_base.h"
#include "tbl_state_grp.h"
#include "mutex.h"

map< long, CtiTableStateGroup >     _stateGroups;
CtiMutex                            _stateGroupMux;


// Global DLL Object... for C interface...

// DLLEXPORT CtiDeviceManager gPortManager;

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
   switch( ul_reason_for_call )
   {
   case DLL_PROCESS_ATTACH:
      {
         // cout << "Yukon DB Miner DLL is initializing!" << endl;
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
         // cout << "Yukon DB Miner RTDB DLL is exiting!" << endl;
         break;
      }
   }
   return TRUE;
}


DLLEXPORT void ReloadStateNames(void)
{
    CtiLockGuard<CtiMutex> guard(_stateGroupMux, 5000);

    if(guard.isAcquired())
    {
        bool reloadFailed = false;

        for( auto &kv : _stateGroups )
        {
            CtiTableStateGroup &theGroup = kv.second;
            if(!theGroup.Restore())
            {
                reloadFailed = true;
                break;
            }
        }

        if(reloadFailed)
        {
            _stateGroups.clear();          // All stategroups will be reloaded on their next usage..  This shouldn't happen very often

            CTILOG_WARN(dout, "State Group Set reset.");
        }
    }
    else
    {
        CTILOG_ERROR(dout, "State group names were not reloaded this pass.  Exclusion could not be obtained.");
    }
}


/*
 *  returns true if the point has a valid state group with this raw value
 */
DLLEXPORT string ResolveStateName(LONG grpid, LONG rawValue)
{
    string rStr;

    if(grpid)
    {
        CtiLockGuard<CtiMutex> guard(_stateGroupMux);

        auto sgit = _stateGroups.find( grpid );

        if( sgit == _stateGroups.end() )
        {
            CtiTableStateGroup mygroup( grpid );

            // We need to load it up, and/or then insert it!
            mygroup.Restore();

            // Try to insert. Return indicates success.
            auto resultpair = _stateGroups.emplace( grpid, mygroup );

            if(resultpair.second == true)
            {
                sgit = resultpair.first;      // Iterator which points to the set entry.
            }
        }

        if( sgit != _stateGroups.end() )
        {
            // git should be an iterator which represents the group now!
            CtiTableStateGroup &theGroup = sgit->second;
            rStr = theGroup.getRawState(rawValue);
        }
    }

    return rStr;
}


