/*-----------------------------------------------------------------------------*
*
* File:   routetest
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/routetest.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:36:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )


#include <windows.h>
#include <iostream>
using namespace std;

#include "logger.h"
#include "mgr_route.h"
#include "rtdb.h"

void main(int argc, char** argv)
{
    CtiRouteManager     RouteManager;

    int cnt = 1;
    int stime = 0;

    if(argc > 1) cnt = atoi(argv[1]);
    if(argc > 2) stime = atoi(argv[2]);

    dout.start();     // fire up the logger thread
    dout.setOutputPath("c:\\temp\\");
    dout.setOutputFile("temp");
    dout.setToStdOut(true);
    dout.setWriteInterval(0);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Route Tester is starting up now" << endl;
    }

    for(int i = 0; i < cnt; i++)
    {
        RouteManager.RefreshList();
        RouteManager.DumpList();

        if(stime > 0)
        {
            Sleep(stime);
        }
    }

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    return;
}
