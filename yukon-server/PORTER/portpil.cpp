/*-----------------------------------------------------------------------------*
*
* File:   portpil
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2007/07/23 15:36:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


/*---------------------------------------------------------------------
    Copyright (c) 1999-2000 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        Corey G. Plender

    FileName:
        PORTPIL.C

    Purpose:
        To Accept request from other process's to Remotes and Devices

    The following procedures are contained in this module:


    Initial Date:
        Unknown

    Revision History:
         12-16-99    Stolen from portpipe.cpp                     CGP

   -------------------------------------------------------------------- */
#include <windows.h>
#include <iostream>

#include "mgr_device.h"
#include "mgr_route.h"
#include "pilserver.h"
#include "dlldefs.h"
#include "dllyukon.h"
#include "dllbase.h"
#include "portglob.h"
#include "thread_monitor.h"

#include "logger.h"
#include "guard.h"

using namespace std;

// Some Global Manager types to allow us some RTDB stuff.
extern CtiDeviceManager   DeviceManager;
extern CtiRouteManager    RouteManager;
extern CtiConfigManager   ConfigManager;

CtiPILServer PIL(&DeviceManager, &RouteManager, &ConfigManager);

VOID PorterInterfaceThread (VOID *Arg)
{
    UINT sanity = 0;
    CtiTime lastTickleTime, lastReportTime;

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " PorterInterfaceThread started as TID  " << CurrentTID() << endl;
   }

   SetThreadName(-1, "PrtIntrfc");

   try
   {
      PIL.execute();

      for(;;)
      {
         if(lastTickleTime.seconds() < (lastTickleTime.now().seconds() - CtiThreadMonitor::StandardTickleTime))
         {
             if(lastReportTime.seconds() < (lastReportTime.now().seconds() - CtiThreadMonitor::StandardMonitorTime))
             {
                 lastReportTime = lastReportTime.now();
                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                 dout << CtiTime() << " Porter Interface Thread active. TID:  " << rwThreadId() << endl;
             }
         
             CtiThreadRegData *data;
             data = CTIDBG_new CtiThreadRegData( GetCurrentThreadId(), "Porter Interface Thread", CtiThreadRegData::None, CtiThreadMonitor::StandardMonitorTime );
             ThreadMonitor.tickle( data );
             lastTickleTime = lastTickleTime.now();
         }
        
         try
         {
            rwServiceCancellation();
            rwSleep(1000);
         }
         catch(RWCancellation &c)
         {
            PIL.shutdown();

            if(PIL.join(5000) == RW_THR_TIMEOUT) // Wait until I am shutdown as a whole....
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << "PIL Threads failed to shutdown! " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
         }
      }

      return;
   }
   catch(const RWxmsg& x)
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "PorterInterfaceThread Exception: " << x.why() << endl;
   }

   return;
}


