#include "precompiled.h"

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

Cti::Pil::PilServer PIL(&DeviceManager, &PorterPointManager, &RouteManager);

void PorterInterfaceThread (void *Arg)
{
    UINT sanity = 0;
    CtiTime lastTickleTime, lastReportTime;

   CTILOG_INFO(dout, "PorterInterfaceThread started");

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
                CTILOG_ERROR(dout, "PIL Threads failed to shutdown!");
            }

            break;
         }
      }

      return;
   }
   catch(const RWxmsg& x)
   {
      CTILOG_EXCEPTION_ERROR(dout, x);
   }

   return;
}


