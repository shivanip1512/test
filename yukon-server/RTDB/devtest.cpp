/*-----------------------------------------------------------------------------*
*
* File:   devtest
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/devtest.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2004/05/05 15:31:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )


#include <windows.h>
#include <crtdbg.h>
#include <iostream>
using namespace std;

#include <rw\cstring.h>

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

    CtiDeviceManager     Manager;

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

        Manager.dumpList();

        Sleep(500);
    }
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    Manager.deleteList();

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    return;
}

