/*-----------------------------------------------------------------------------*
*
* File:   dllyukon
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/dllyukon.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/17 19:02:57 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/db/db.h>


#include "tbl_route.h"
#include "tbl_rtcarrier.h"
#include "tbl_rtcomm.h"
#include "tbl_rtmacro.h"
#include "tbl_rtroute.h"
#include "tbl_rtrepeater.h"
#include "tbl_rtversacom.h"


#include "dlldefs.h"
#include "msg_pcrequest.h"


#include "pt_base.h"
#include "tbl_state_grp.h"
#include "mutex.h"

typedef set< CtiTableStateGroup >   CtiStateGroupSet_t;
CtiStateGroupSet_t                  _stateGroupSet;
bool                                _stateGroupsLoaded = false;
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
        CtiStateGroupSet_t::iterator sgit;

        bool reloadFailed = false;

        for(sgit = _stateGroupSet.begin(); sgit != _stateGroupSet.end(); sgit++ )
        {
            CtiTableStateGroup &theGroup = *sgit;
            if(theGroup.Restore().errorCode() != RWDBStatus::ok)
            {
                reloadFailed = true;
                break;
            }
        }

        if(reloadFailed)
        {
            _stateGroupSet.clear();          // All stategroups will be reloaded on their next usage..  This shouldn't happen very often
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " State Group Set reset. " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " INFO: State group names were not reloaded this pass.  Exclusion could not be obtained." << endl;
    }
}


/*
 *  returns true if the point has a valid state group with this raw value
 */
DLLEXPORT RWCString ResolveStateName(LONG grpid, LONG rawValue)
{
    RWCString rStr;

/*    if( !_stateGroupsLoaded )
    {
        loadStateNames();
    }
*/
    if(grpid > 0)
    {
        CtiTableStateGroup mygroup( grpid );

        CtiLockGuard<CtiMutex> guard(_stateGroupMux);

        CtiStateGroupSet_t::iterator sgit = _stateGroupSet.find( mygroup );

        if( sgit == _stateGroupSet.end() )
        {
            // We need to load it up, and/or then insert it!
            mygroup.Restore();

            pair< CtiStateGroupSet_t::iterator, bool > resultpair;

            // Try to insert. Return indicates success.
            resultpair = _stateGroupSet.insert( mygroup );

            if(resultpair.second == true)
            {
                sgit = resultpair.first;      // Iterator which points to the set entry.
            }
        }

        if( sgit != _stateGroupSet.end() )
        {
            // git should be an iterator which represents the group now!
            CtiTableStateGroup &theGroup = *sgit;
            rStr = theGroup.getRawState(rawValue);
        }
    }

    return rStr;
}


