/*-----------------------------------------------------------------------------*
*
* File:   porttest
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/porttest.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:24:02 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
#include <exception>
using namespace std;

#include "dllbase.h"
#include "mgr_port.h"
#include "rtdb.h"
#include "logger.h"

void ApplyDump(const long key, CtiPortManager::ptr_type Port, void* d)
{
    dout << " APPLY FUNC " << endl;
    Port->Dump();
    return;
}


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
    dout.setOutputFile("porttester");
    dout.setToStdOut(true);
    dout.setWriteInterval(1);


    cout << "Porttester is starting up now" << endl;

    for(int i = 0; i < cnt; i++)
    {
        InitYukonBaseGlobals();

        PortManager.RefreshList();
        PortManager.DumpList();
        CtiPortManager::ptr_type psPort(PortManager.PortGetEqual(1));
        if(psPort)
        {
            dout << endl << psPort->getName() << " is good. use_count " << psPort.use_count() << endl;
        }

        CtiPortManager::ptr_type psPort2(PortManager.PortGetEqual(5));
        if(psPort2)
        {
            dout << endl << psPort2->getName() << " is good. use_count " << psPort2.use_count() << endl << endl;
        }

        try
        {
            PortManager.apply( ApplyDump, NULL );
        }
        catch(exception &e)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  " << e.what() << endl;
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }


        if(stime > 0)
        {
            Sleep(stime);
        }
    }

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    return;
}
