/*-----------------------------------------------------------------------------*
*
* File:   routetest
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/routetest.cpp-arc  $
* REVISION     :  $Revision: 1.6.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>


#include "logManager.h"
#include "mgr_route.h"
#include "rtdb.h"

using std::endl;

void main(int argc, char** argv)
{
    CtiRouteManager     RouteManager;

    int cnt = 1;
    int stime = 0;

    if(argc > 1) cnt = atoi(argv[1]);
    if(argc > 2) stime = atoi(argv[2]);


    doutManager.setOutputPath("c:\\temp\\");
    doutManager.setOutputFile("temp");
    doutManager.setToStdOut(true);
    doutManager.start();     // fire up the logger thread

    CTILOG_INFO(dout, "Route Tester is starting up now");

    for(int i = 0; i < cnt; i++)
    {
        RouteManager.RefreshList();
        CTILOG_INFO(dout, RouteManager);

        if(stime > 0)
        {
            Sleep(stime);
        }
    }
}
