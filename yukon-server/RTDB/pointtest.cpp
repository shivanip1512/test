/*-----------------------------------------------------------------------------*
*
* File:   pointtest
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pointtest.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:24:02 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;

#include "logger.h"
#include "mgr_point.h"
#include "rtdb.h"

void main(int argc, char** argv)
{
   int                  Loops = 1;
   int                  Delay = 500;
   CtiPointManager      PointManager;

   dout.start();     // fire up the logger thread
   dout.setOutputPath("c:\\temp\\");
   dout.setOutputFile("temp");
   dout.setToStdOut(true);
   dout.setWriteInterval(0);

   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Point Tester is starting up now" << endl;
   }


   if(argc > 1)
   {
      Loops = atoi(argv[1]);
   }

   if(argc > 2)
   {
      Delay = atoi(argv[2]);
   }

   for(int i = 0; i < Loops; i++)
   {
      PointManager.refreshList();
      PointManager.DumpList();
      Sleep(Delay);
   }

   PointManager.DeleteList();

   dout.interrupt(CtiThread::SHUTDOWN);
   dout.join();


   return;
}
