
/*-----------------------------------------------------------------------------*
*
* File:   devtest
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/devtest.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:42 $
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

    dout << "Device Tester is starting up now" << endl;

    for(int i = 0 ; i < cnt; i++)
    {
        Manager.RefreshList();
        Manager.RefreshScanRates();
        Manager.DumpList();

        while( 1 )
        {
            CtiDeviceManager::val_pair vt = Manager.getMap().find(isNotScannable, NULL);

            if(vt.first == NULL)
            {
                break;
            }

            hKey = vt.first;
            DeviceRecord = vt.second;

            try
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Deleting Key **** " << hKey->getID() << " for device " << DeviceRecord->getName() << endl;
                }
                Manager.getMap().remove( hKey );
            }
            catch( ... )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }

            try
            {
                if(hKey != NULL)
                {
                    delete hKey;
                    hKey = NULL;
                }

                if(DeviceRecord != NULL)
                {
                    delete DeviceRecord;
                    DeviceRecord = NULL;
                }
            }
            catch( ... )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        }

        Sleep(500);
    }

    Manager.DeleteList();

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    return;
}

