#include "precompiled.h"

#include "pilserver.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"

#include "logger.h"

using namespace std;
using Cti::ThreadStatusKeeper;

// Some Global Manager types to allow us some RTDB stuff.
extern CtiDeviceManager   DeviceManager;
extern CtiPointManager    PorterPointManager;
extern CtiRouteManager    RouteManager;

Cti::Pil::PilServer PIL(DeviceManager, PorterPointManager, RouteManager);

void PorterInterfaceThread()
{
   CTILOG_INFO(dout, "PorterInterfaceThread started");

   SetThreadName(-1, "PrtIntrfc");

   try
   {
      ThreadStatusKeeper threadStatus("Porter Interface Thread");
      PIL.execute();

      for(;;)
      {
         threadStatus.monitorCheck(CtiThreadRegData::None);

         Cti::WorkerThread::sleepFor(Cti::Timing::Chrono::seconds(1));
      }
   }
   catch( Cti::WorkerThread::Interrupted & )
   {
      PIL.shutdown();

      if( ! PIL.join(5000) )
      {
          CTILOG_ERROR(dout, "PIL Threads failed to shutdown!");
      }
   }
   catch( ... )
   {
      CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
   }
}


