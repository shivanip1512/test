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

bool globalCtrlCFlag = false;

using namespace std;

void CCUThread(const int s, const int strtgy);

DLLIMPORT extern CtiLogger dout;

/* CtrlHandler handles is used to catch ctrl-c when run in a console */
BOOL CtrlHandler(DWORD fdwCtrlType)
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

typedef void (*CCUThreadFunPtr)(const int, const int);


template<typename FunT,
typename ParamT,
typename ParamU>
struct Adapter
{
    Adapter(FunT f, ParamT p, ParamU q) :
    f_(f), p_(p) , q_(q)
    {
    }

    void operator()()
    {
        f_(p_,q_);
    }
    private:
    FunT f_;
    ParamT p_;
    ParamU q_;
};


int main(int argc, char *argv[])
{
    vector<boost::thread *> threadVector;

    dout.start();     // fire up the logger thread
    //dout.setOwnerInfo(CompileInfo);
    dout.setOutputPath(gLogDirectory);
    dout.setOutputFile("Simulator");
    dout.setToStdOut(true);
    dout.setWriteInterval(0);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if( argc==4 )
        {   // Specify port number
            dout << "Port range " << argv[1] << " - " << argv[2] << endl;
            dout << "Strategy selected: " << argv[3] << endl;
        }
        else if( argc==3 )
        {
            dout << "Port range " << argv[1] << " - " << argv[2] << endl;
        }
        else
        {
            dout << "Invalid port range entry.  Format is:  ccu_simulator 00001 99999" << endl;
            return 0;
        }
    }

    int portNum = atoi(argv[1]);
    int portMax = atoi(argv[2]);
    int strategy = 0;
    if( argc==4 )
    {
        strategy = atoi(argv[3]);
    }

    boost::thread_group threadGroup;
    while( portNum != (portMax+1) )
    {
        boost::thread *thr1 = new boost::thread(Adapter<CCUThreadFunPtr, int, int>(CCUThread, portNum, strategy));
        //threadVector.push_back(thr1);
        threadGroup.add_thread(thr1);
        CTISleep(50);
        portNum++;
    }

    //  We need to catch ctrl-c so we can stop
    if( !SetConsoleCtrlHandler((PHANDLER_ROUTINE) CtrlHandler,  TRUE) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Could not install control handler" << endl;
    }

    threadGroup.join_all();

    if( globalCtrlCFlag )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Main function closing..." << endl;
        exit(0);
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Returning from main function..." << endl;
    }

    Sleep(1000);
    return 0;
}

void CCUThread(const int portNumber, const int strategy)
{
    try
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Port: " << portNumber << endl;
        }
    
        //InitYukonBaseGlobals();                            // Load up the config file.
    
        // Set default database connection params
        //setDatabaseParams(0, "msq15d.dll", "mn1db02\\server2005", "erooney", "erooney");   // *** THIS NEEDS TO BE CHANGED FOR ALL USERS !!!!!!!!
    
        WSADATA wsaData;
    
        std::map <int, CCU711 *> ccuList;
    
        WSAStartup(MAKEWORD (1,1), &wsaData);
    
        CTINEXUS * listenSocket;
        listenSocket = new CTINEXUS();
        listenSocket->CTINexusCreate(portNumber);   //12345 or 11234 for example
    
        CTINEXUS * newSocket;
        newSocket = new CTINEXUS();
    
        while( !(newSocket->CTINexusValid()) )
        {
            if( globalCtrlCFlag )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Listening thread closing..." << endl;
                exit(0);
            }
    
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() <<" Listening on " << portNumber << endl;
            }
    
            listenSocket->CTINexusConnect(newSocket, NULL, 10000, CTINEXUS_FLAG_READEXACTLY);
            Sleep(1000);
        }
    
        // Grab address to determine type.
        // Create the CCU Manager.
        // From now on we can all the processRequest() and it will find the right CCU type.
    
        unsigned char type = 0x00;
        unsigned char tsbuf[2];
        tsbuf[0] = 0x00;
        tsbuf[1] = 0x00;
        unsigned long br=0;
        unsigned long addr = 0x00;
    
        SimulatedCCU* ccu; 
        //  Peek at first bytes to determine which CCU this is supposed to be.
        do {
            Sleep(500);
            newSocket->CTINexusPeek(tsbuf,2, &br);
        } while (br != 2 && !globalCtrlCFlag);
    
        addr = tsbuf[1];
        type = tsbuf[0];
        if (tsbuf[0] == 0x7e)
        {
            ccu = new CCU711Manager(newSocket);
        }
        else if (tsbuf[0] != 0x00)
        {
            ccu = new CCU710Manager(newSocket);
        }
    
        ccu->setStrategy(strategy);
        
        while( !globalCtrlCFlag )
        {
            //Add request validator. instead of this condition
            if (ccu->validateRequest(type)) {
                ccu->processRequest(addr);
            }// else is now unhandled. we are set up for a ccu type.
            CTISleep(250);
    
            tsbuf[0] = 0x00;
            tsbuf[1] = 0x00;
            addr = 0x00;
    
            do {
                Sleep(500);
                br = 0;
                newSocket->CTINexusPeek(tsbuf,2, &br);
            } while (br != 2 && !globalCtrlCFlag);
    
            addr = tsbuf[1];
            type = tsbuf[0];
        }
    
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Active thread closing..." << endl;
        }
    
        listenSocket->CTINexusClose();
        newSocket->CTINexusClose();
        return;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "FATAL ERROR: Simulator for port " << portNumber << " died." << endl;
    }
}
