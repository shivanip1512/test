#pragma warning( disable : 4786 )
/*-----------------------------------------------------------------------------*
*
* File:   porttest
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/porttest.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:17 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
#include <crtdbg.h>
using namespace std;

#include "dllbase.h"
#include "mgr_port.h"
#include "rtdb.h"
#include "logger.h"

void main(int argc, char** argv)
{
   int cnt = 1;
   int stime = 0;

   if(argc > 1)
      cnt = atoi(argv[1]);

   if(argc > 2)
      stime = atoi(argv[2]);

   CtiPortManager     PortManager(NULL);

   InitYukonBaseGlobals();

   dout.start();     // fire up the logger thread
   dout.setOutputPath(gLogDirectory.data());
   dout.setOutputFile("porter");
   dout.setToStdOut(true);
   dout.setWriteInterval(1);


   cout << "Porttester is starting up now" << endl;

   for(int i = 0; i < cnt; i++)
   {
      InitYukonBaseGlobals();

      PortManager.RefreshList();
      PortManager.DumpList();

      if(stime > 0)
      {
         Sleep(stime);
      }
   }

   dout.interrupt(CtiThread::SHUTDOWN);
   dout.join();

   return;
}
