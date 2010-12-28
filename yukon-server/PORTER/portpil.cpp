#include "yukon.h"

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <iostream>

#include "mgr_device.h"
#include "mgr_route.h"
#include "mgr_point.h"
#include "pilserver.h"
#include "dlldefs.h"
#include "dllyukon.h"
#include "dllbase.h"
#include "portglob.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"

#include "logger.h"
#include "guard.h"

using namespace std;
using Cti::ThreadStatusKeeper;

// Some Global Manager types to allow us some RTDB stuff.
extern CtiDeviceManager   DeviceManager;
extern CtiPointManager    PorterPointManager;
extern CtiRouteManager    RouteManager;
extern CtiConfigManager   ConfigManager;

CtiPILServer PIL(&DeviceManager, &PorterPointManager, &RouteManager, &ConfigManager);

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
      ThreadStatusKeeper threadStatus("Porter Interface Thread");
      PIL.execute();

      for(;;)
      {
         threadStatus.monitorCheck(CtiThreadRegData::None);

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


