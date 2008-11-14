/*****************************************************************************
*
*    FILE NAME: serverNexus.cpp
*
*    DATE: 7/1/2007
*
*    AUTHOR: Eric Rooney
*
*    PURPOSE: CCU Simulator
*
*    DESCRIPTION: Simulate the behavior of CCUs
*
*    Copyright (C) 2007 Cannon Technologies, Inc.  All rights reserved.
*****************************************************************************/
#include "yukon.h"

#include <windows.h>
#include <iostream>
#include <vector>

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/bind.hpp>
#include <rw/db/datetime.h>

#include "cticalls.h"
#include "ctinexus.h"
#include "dsm2.h"
#include "color.h"
#include "ctiTime.h"
#include "dbaccess.h"
#include "mctStruct.h"
#include "SimulatedCCU.h"
#include "CCU711Manager.h"
#include "CCU710Manager.h"
#include "logger.h"

#include "dllbase.h"

#include "math.h"

PROJECT_ID("CCU Simulator");

bool globalCtrlCFlag = false;

using namespace std;
using namespace boost;

void CCUThread(int portNumber, int strategy);

DLLIMPORT extern CtiLogger dout;

/* CtrlHandler handles is used to catch ctrl-c when run in a console */
BOOL WINAPI CtrlHandler(DWORD fdwCtrlType)
{
    switch( fdwCtrlType )
    {
    case CTRL_C_EVENT:
    case CTRL_SHUTDOWN_EVENT:
    case CTRL_CLOSE_EVENT:
    case CTRL_BREAK_EVENT:
    case CTRL_LOGOFF_EVENT:
        {
            globalCtrlCFlag = true;
            Sleep(50000);
            return TRUE;
        }

    default:
        {
            return FALSE;
        }
    }
}

int main(int argc, char *argv[])
{
    int strategy = 0,
        port_min = 0,
        port_max = 0;

    switch( argc )
    {
        case 4:  strategy = atoi(argv[3]);
        case 3:  port_max = atoi(argv[2]);
        case 2:  port_min = atoi(argv[1]);  break;

        default:
        {
            cout << "Usage:  ccu_simulator.exe <min_port> [max_port] [strategy #]" << endl;

            exit(-1);
        }
    }

    if( port_max && port_min > port_max )
    {
        cout << "Invalid port range [" << port_min << " - " << port_max << "]" << endl;

        exit(-1);
    }

    port_max = max(port_max, port_min);

    dout.start();     // fire up the logger thread
    dout.setOutputPath(gLogDirectory);
    dout.setOutputFile("ccu_simulator");
    dout.setToStdOut(true);
    dout.setWriteInterval(0);

    identifyProject(CompileInfo);
    setConsoleTitle(CompileInfo);

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << "Port range [" << port_min << " - " << port_max << "], strategy " << strategy << endl;
    }

    //  We need to catch ctrl-c so we can stop
    if( !SetConsoleCtrlHandler(CtrlHandler,  TRUE) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Could not install control handler" << endl;
    }

    //  start up Windows Sockets
    WSADATA wsaData;
    WSAStartup(MAKEWORD (1,1), &wsaData);

    boost::thread_group threadGroup;

    for( ; port_min <= port_max; ++port_min )
    {
        threadGroup.add_thread(new boost::thread(boost::bind(CCUThread, port_min, strategy)));
    }

    threadGroup.join_all();

    return 0;
}

void CCUThread(int portNumber, int strategy)
{
    CTINEXUS *listenSocket, *newSocket;

    try
    {
        listenSocket = new CTINEXUS();
        listenSocket->CTINexusCreate(portNumber);

        newSocket = new CTINEXUS();

        for( int i = 0; !(newSocket->CTINexusValid()) && !globalCtrlCFlag; ++i )
        {
            if( !(i % 10) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() <<" Port " << setw(4) << portNumber << " listening" << endl;
            }

            //  wait to connect for 1 second
            listenSocket->CTINexusConnect(newSocket, NULL, 999);
        }

        //  done with the listener
        listenSocket->CTINexusClose();
        delete listenSocket;

        const unsigned char type_index = 0,
                            addr_index = 1;

        unsigned char peek_buffer[2];
        unsigned long bytes_read = 0;

        SimulatedCCU *ccu_manager = NULL;

        //  Peek at first bytes to determine which CCU this is supposed to be.
        while( bytes_read != 2 && !globalCtrlCFlag )
        {
            Sleep(500);
            newSocket->CTINexusPeek(peek_buffer, 2, &bytes_read);
        }

        if( peek_buffer[type_index] == 0x7e )
        {
            ccu_manager = new CCU711Manager(newSocket, strategy);
        }
        else
        {
            ccu_manager = new CCU710Manager(newSocket, strategy);
        }

        while( !globalCtrlCFlag )
        {
            if( ccu_manager->validateRequest(peek_buffer[type_index]) )
            {
                ccu_manager->processRequest(peek_buffer[addr_index]);
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port " << portNumber << " - Unhandled request ";
                dout << hex << "(" << peek_buffer[0] << " " << peek_buffer[addr_index] << ")" << endl;
            }

            bytes_read = 0;

            while( bytes_read != 2 && !globalCtrlCFlag )
            {
                Sleep(500);

                newSocket->CTINexusPeek(peek_buffer, 2, &bytes_read);
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Active thread closing..." << endl;
        }

        if( ccu_manager != NULL )
        {
            delete ccu_manager;
            ccu_manager = NULL;
        }

        newSocket->CTINexusClose();
        delete newSocket;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "FATAL ERROR: Simulator for port " << portNumber << " died." << endl;
    }
}
