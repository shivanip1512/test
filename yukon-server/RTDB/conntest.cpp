/*-----------------------------------------------------------------------------*
*
* File:   conntest
*
* Exercise in connections
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <crtdbg.h>
#include <iostream>

#include <iomanip>
#include <iostream>
#include <exception>
#include <utility>

#include <rw\thr\thrfunc.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw/toolpro/inetaddr.h>
#include <rw\rwerr.h>
#include <rw\thr\mutex.h>

#include "dllbase.h"
#include "connection.h"
#include "msg_cmd.h"
#include "msg_trace.h"

std::set<CtiConnection *> connections;

using namespace std;

void __cdecl Purecall(void)
{
    autopsy( __FILE__, __LINE__);
}

enum OperationMode
{
    Server = 1,
    Client = 2,
};

bool bGCtrlC;
CtiConnection::Que_t          MainQueue_;
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
            bGCtrlC = true;
            Sleep(5000);
            return TRUE;
        }

    default:
        {
            return FALSE;
        }
    }
}

void ConnectionHandlerThread(int portNumber)
{
    int               i=0;
    BOOL              bQuit = FALSE;

    UINT sanity = 0;

    CtiCommandMsg     *CmdMsg   = NULL;

    RWSocket                      sock;
    RWInetPort                    NetPort;
    RWInetAddr                    NetAddr;    // This one for this server!

    CtiExchange       *XChg;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        cout << CtiTime() << " Server Connection Handler Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

    /* Up this threads priority a notch over the other procs */
    CTISetPriority(PRTYC_NOCHANGE, THREAD_PRIORITY_BELOW_NORMAL);

    try
    {
        NetPort  = RWInetPort(portNumber);
        NetAddr  = RWInetAddr(NetPort);

        sock.listen(NetAddr);

        // This is here for looks, in reality it is rarely called.
        if( !sock.valid() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Could not open socket " << NetAddr << " for listening" << endl;

            exit(-1);
        }
    }
    catch(const RWxmsg& x)
    {
        cout << "Exception: " << x.why() << endl;
        exit(-1);
    }
    catch(...)
    {
        {
            cout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            exit(-1);
        }
    }

    for(;!bQuit && !bGCtrlC;)
    {
        try
        {

            // It seems necessary to make this copy. RW does this and now so do we.
            RWSocket tempSocket = sock;
            RWSocket newSocket = tempSocket.accept();

            if( !newSocket.valid() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Could not accept new connection " << endl;
            }
            else
            {
                RWSocketPortal portal = RWSocketPortal(newSocket, RWSocketPortalBase::Application);

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Connection Handler Thread. New connect. " << endl;
                }

                {
                    XChg                      = CTIDBG_new CtiExchange(portal);
                    CtiConnection *connection = CTIDBG_new CtiConnection(XChg, &MainQueue_);

                    connection->ThreadInitiate();     // Kick off the connection's communication threads.

                    connections.insert(connection);
                    cout << CtiTime() << " New connection established" << endl;
                }
            }

        }
        catch(RWSockErr& msg )
        {
            if(msg.errorNumber() == RWNETENOTSOCK)
            {
                cout << CtiTime() << " Socket error RWNETENOTSOCK" << endl;
                bQuit = TRUE;     // get out of the for loop
            }
            else
            {
                bQuit = TRUE;
                // dout << CtiTime() << " VGConnectionHandlerThread: The KNOWN socket has been closed" << endl;
            }
        }
        catch(RWxmsg& msg )
        {
            {
                cout << endl << "VGConnectionHandler Failed: " ;
                cout << msg.why() << endl;
                bQuit = TRUE;
            }
            throw;
        }
        catch(...)
        {
            cout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}
void runServer(int portNumber)
{
    bool bQuit = false;
    RWThreadFunction ConnThread_ = rwMakeThreadFunction(&ConnectionHandlerThread, portNumber);
    ConnThread_.start();

    CtiMessage *msg;

    for(;!bQuit && !bGCtrlC;)
    {
        msg = MainQueue_.getQueue(1000);

        if( msg != NULL )
        {
            msg->dump();
        }
    }

    if(RW_THR_TIMEOUT == ConnThread_.join(2000))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Terminating connection thread " << __FILE__ << " at:" << __LINE__ << endl;
        ConnThread_.terminate();
    }

    // Shutdown all connections and exit!
    std::set<CtiConnection *>::iterator iter;
    for( iter = connections.begin(); iter != connections.end(); iter++ )
    {
        CtiConnection *conn = *iter;
        delete conn;
    }

    return;
}

void runClient(int portNumber, string portName)
{
    CtiConnection conn(portNumber, "127.0.0.1");
    conn.setName(portName);
    int i;
    bool bQuit = false;

    for(;!bQuit && !bGCtrlC;)
    {
        cout << "0: Quit" << endl;
        cout << "1: Send Trace Msg" << endl;
        cout << "Selection Please: ";
        cin >> i;

        if( i == 1 )
        {
            CtiTraceMsg * msg = CTIDBG_new CtiTraceMsg();
            msg->setTrace(portName + " sent trace msg");
            conn.WriteConnQue(msg);
        }
        else if( i == 0 )
        {
            bQuit = true;
        }
    }

    return;
}

void main(void)
{

    _set_purecall_handler(Purecall);
    //  start up Windows Sockets
    WSADATA wsaData;
    WSAStartup(MAKEWORD (1,1), &wsaData);

    if( !SetConsoleCtrlHandler(CtrlHandler,  TRUE) )
    {
        cout << "Could not install control handler" << endl;

        exit(-1);
    }

    bGCtrlC = false;
    string portName;
    int operationMode, portNumber;
    cout << "Please give me a name: ";
    cin  >> portName;
    cout << "Am I a Server (1) or Client (2): ";
    cin  >> operationMode;
    cout << "Port Number: ";
    cin  >> portNumber;

    dout.start();     // fire up the logger thread
    dout.setOutputPath(gLogDirectory);
    dout.setOutputFile("conntest");
    dout.setToStdOut(true);
    dout.setWriteInterval(1000);

    if( operationMode == Server )
    {
        runServer(portNumber);
        Sleep(10000);
    }
    else if( operationMode == Client )
    {
        runClient(portNumber, portName);
        Sleep(10000);
    }
    else
    {
        cout << "Unknown Operation Mode: "<< operationMode << ", exiting." << endl;
    }
}

