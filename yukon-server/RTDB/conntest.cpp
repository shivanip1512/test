#include "precompiled.h"

#include <crtdbg.h>
#include <iostream>

#include <iomanip>
#include <iostream>
#include <exception>
#include <utility>

#include <rw\thr\thrfunc.h>
#include <rw\rwerr.h>
#include <rw\thr\mutex.h>

#include "dllbase.h"
#include "connection_client.h"
#include "connection_server.h"
#include "amq_constants.h"
#include "msg_cmd.h"
#include "msg_trace.h"

std::vector<CtiServerConnection*> connections;

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
CtiConnection::Que_t MainQueue_;
DLLIMPORT extern CtiLogger dout;

// CtrlHandler handles is used to catch ctrl-c when run in a console
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

CtiListenerConnection listenerConnection( "com.eaton.eas.yukon.conntest" );

void ConnectionHandlerThread( string name )
{
    bool bQuit = false;

    cout << CtiTime() << " Server Connection Handler Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;

    try
    {
        for(;!bQuit && !bGCtrlC;)
        {
            if( !listenerConnection.verifyConnection() )
            {
                connections.clear();

                listenerConnection.start();
            }

            if( listenerConnection.acceptClient() )
            {
                auto_ptr<CtiServerConnection> new_conn( new CtiServerConnection( listenerConnection, &MainQueue_ ));

                new_conn->setName( name );
                new_conn->start();

                connections.push_back( new_conn.release() );

                cout << CtiTime() << " New client connection established " << endl;
            }
        }
    }
    catch(RWxmsg& msg )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << "ConnectionHandler Failed: " << msg.why() << endl;
        }
        exit(-1);
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        exit(-1);
    }
}

void runServer( string name )
{
    bool bQuit = false;
    RWThreadFunction ConnThread_ = rwMakeThreadFunction( &ConnectionHandlerThread, name );
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

    listenerConnection.close();

    if(RW_THR_TIMEOUT == ConnThread_.join(2000))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Terminating connection thread " << __FILE__ << " at:" << __LINE__ << endl;
        ConnThread_.terminate();
    }

    return;
}

void runClient( string name )
{
    cout << CtiTime() << " Starting Client Connection " << endl;

    CtiClientConnection conn( "com.eaton.eas.yukon.conntest" );

    conn.setName( name );
    conn.start();

    if( ! conn.isConnectionUsable() )
    {
        cout << CtiTime() << " Client connection is failed" << endl;
        return;
    }

    bool bQuit = false;

    for(;!bQuit && !bGCtrlC;)
    {
        int i = -1;

        cout << "0: Quit" << endl;
        cout << "1: Send Trace Msg" << endl;
        cout << "Selection Please: ";
        cin >> i;

        if( i == 1 )
        {
            auto_ptr<CtiTraceMsg> msg( new CtiTraceMsg() );
            msg->setTrace( conn.who() + " sent trace msg");
            conn.WriteConnQue( msg.release() );
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

    if( !SetConsoleCtrlHandler(CtrlHandler,  TRUE) )
    {
        cout << " Could not install control handler " << endl;
        exit(-1);
    }

    bGCtrlC = false;
    string connectionName;
    int operationMode;
    cout << "Please give me a name: ";
    cin  >> connectionName;
    cout << "Am I a Server (1) or Client (2): ";
    cin  >> operationMode;

    // fire up the logger thread
    dout.start              ();
    dout.setOutputPath      ( gLogDirectory );
    dout.setRetentionLength ( gLogRetention );
    dout.setOutputFile      ( "conntest" );
    dout.setToStdOut        ( true );
    dout.setWriteInterval   ( 1000 );

    if( operationMode == Server )
    {
        runServer( connectionName );
        Sleep( 10000 );
    }
    else if( operationMode == Client )
    {
        runClient( connectionName );
        Sleep( 10000 );
    }
    else
    {
        cout << " Unknown Operation Mode: "<< operationMode << ", exiting. " << endl;
    }
}

