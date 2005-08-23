/*-----------------------------------------------------------------------------*
*
* File:   portpil
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/08/23 20:05:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#pragma title ( "PIL Server Routines" )
#pragma subtitle ( "CTI Copyright (c) 1999-2000" )
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
using namespace std;

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

// Some Global Manager types to allow us some RTDB stuff.
extern CtiDeviceManager   DeviceManager;
extern CtiRouteManager    RouteManager;

CtiPILServer PIL(&DeviceManager, &RouteManager);

VOID PorterInterfaceThread (VOID *Arg)
{
    UINT sanity = 0;

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " PorterInterfaceThread started as TID  " << CurrentTID() << endl;
   }

   try
   {
      PIL.execute();

      for(;;)
      {
         //Thread Monitor Begins here**************************************************
         if(!(++sanity % SANITY_RATE))
         {
             {//This is not necessary and can be annoying, but if you want it (which you might) here it is.
                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                 dout << RWTime() << " Porter Interface Thread active. TID:  " << rwThreadId() << endl;
             }
       
             CtiThreadRegData *data = new CtiThreadRegData( GetCurrentThreadId(), "Porter Interface Thread", CtiThreadRegData::None, 400 );
             ThreadMonitor.tickle( data );
         }
         //End Thread Monitor Section
        
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


