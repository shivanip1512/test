/*-----------------------------------------------------------------------------*
*
* File:   devtest
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/devtest.cpp-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2008/07/17 20:27:22 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>
#include <crtdbg.h>
#include <iostream>



#include "hashkey.h"
#include "mgr_device.h"
#include "rtdb.h"
#include "dev_base.h"
#include "logger.h"

void main(int argc, char** argv)
{
    CtiDevice *DeviceRecord;
    CtiHashKey *hKey;

    int cnt = 1;

    if(argc > 1)
        cnt = atoi(argv[1]);

    CtiDeviceManager     Manager(Application_Invalid);

    dout.start();     // fire up the logger thread
    dout.setOutputPath("c:\\temp\\");
    dout.setOutputFile("temp");
    dout.setToStdOut(true);
    dout.setWriteInterval(0);

    INT origDBL = getDebugLevel();
    DebugLevel = 0x80000000;            // OK, trick it!

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Device Tester is starting up now" << endl;
    }

    for(int i = 0 ; i < cnt; i++)
    {
        Manager.refresh();

        DebugLevel = origDBL;

        Manager.test_dumpList();

        Sleep(500);
    }
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    Manager.deleteList();

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    return;
}

