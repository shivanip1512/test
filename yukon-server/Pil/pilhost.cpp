#include "yukon.h"

#include <windows.h>
#include <iomanip>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\cstring.h>
#include <rw\thr\thrfunc.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw/toolpro/inetaddr.h>
#include <rw\rwerr.h>
#include <rw\thr\mutex.h>

#include "mgr_device.h"
#include "mgr_route.h"
#include "pilserver.h"
#include "dlldefs.h"
#include "dlldev.h"


DLLIMPORT extern RWMutexLock coutMux;

BOOL              bAllDone = FALSE;
CtiDeviceManager  DevMgr(Application_Invalid);
CtiRouteManager   RteMgr;

void CleanupHost()
{
   RteMgr.DeleteList();
   DevMgr.deleteList();

   bAllDone = TRUE;
}

BOOL MyCtrlHandler(DWORD fdwCtrlType)
{
   switch(fdwCtrlType)
   {

   /* Handle the CTRL+C signal. */

   case CTRL_C_EVENT:

   case CTRL_CLOSE_EVENT:

   case CTRL_BREAK_EVENT:

   case CTRL_LOGOFF_EVENT:

   case CTRL_SHUTDOWN_EVENT:

   default:

      CleanupHost();
      return TRUE;

   }

}


int main(int argc, char **argv)
{
   try
   {
      if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE))
      {
         cerr << "Could not install control handler" << endl;
         return (-1);
      }

      RteMgr.RefreshList();
      DevMgr.refresh();

      /* Make routes associate with devices */
      attachRouteManagerToDevices(&DevMgr, &RteMgr);
      attachTransmitterDeviceToRoutes(&DevMgr, &RteMgr);

      CtiPILServer PIL(&DevMgr, &RteMgr);

      /*
       *  Initialization Code for PIL as a whole
       */
      SetConsoleTitle("Porter Interface Layer - YUKON v 0.995");

      int i = PIL.execute();

      while(!bAllDone)
      {
         Sleep(1000);
      }

      PIL.shutdown();
      PIL.join();    // Wait until I am shutdown as a whole....

      CleanupHost();

      return(i);
   }
   catch(const RWxmsg& x)
   {
      RWMutexLock::TryLockGuard guard(coutMux);
      if(guard.isAcquired())
      {
         cout << "main() Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
      }
   }

   return 0;
}

