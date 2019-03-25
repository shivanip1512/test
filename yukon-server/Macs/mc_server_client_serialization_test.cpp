#include "precompiled.h"

#define private public
#define protected public

#include "message.h"
#include "msg_cmd.h"
#include "msg_dbchg.h"
#include "msg_lmcontrolhistory.h"
#include "msg_multi.h"
#include "msg_notif_alarm.h"
#include "msg_notif_email.h"
#include "msg_notif_lmcontrol.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_queuedata.h"
#include "Msg_reg.h"
#include "msg_requestcancel.h"
#include "msg_server_req.h"
#include "msg_server_resp.h"
#include "msg_signal.h"
#include "msg_tag.h"
#include "msg_trace.h"

#include "mc_msg.h"
#include "mc_sched.h"
#include "mc_script.h"

#undef private
#undef protected

#include "test_mc_serialization.h"

#include "logManager.h"

#include <crtdbg.h>
#include "connection_server.h"

using namespace std;

void __cdecl Purecall(void)
{
    autopsy(CALLSITE);
}


bool bGCtrlC;

CtiListenerConnection listenerConn( "com.eaton.eas.yukon.conntest" );

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
            listenerConn.close();
            Sleep(5000);
            return TRUE;
        }
    default:
        {
            return FALSE;
        }
    }
}


const unsigned int RandomSeedBase = 100;


template<typename testcase_t>
struct ServerClientTestSequence
{
    testcase_t           _tc;
    CtiServerConnection& _serverConn;

    ServerClientTestSequence( CtiServerConnection& serverConn ) :
        _serverConn( serverConn )
    {
    }

    void Run()
    {
        _tc.Create();

        CTILOG_INFO(dout, "Populating "<< typeid(*_tc._imsg).name());

        _tc.Populate();

        _serverConn.WriteConnQue(_tc._imsg.release(), CALLSITE);
    }
};


void main(void)
{
    _set_purecall_handler(Purecall);

    if( !SetConsoleCtrlHandler(CtrlHandler,  TRUE) )
    {
        cout << " Could not install control handler " << endl;
        exit(-1);
    }

    bGCtrlC = false;

    // fire up the logger thread
    doutManager.setOutputPath    ( gLogDirectory );
    doutManager.setOutputFile    ( "mc_server_client_serialization_test" );
    doutManager.setToStdOut      ( true );

    doutManager.start();

    for(;!bGCtrlC;)
    {
        if( !listenerConn.verifyConnection() )
        {
            listenerConn.start();
        }

        // will wait here until a new client connection is accepted.
        if( listenerConn.acceptClient() )
        {
            unique_ptr<CtiServerConnection> serverConn( new CtiServerConnection( listenerConn ));
            serverConn->start();

            CTILOG_INFO(dout, "New server connection established, Running test.");

            {
                RandomGenerator::reset( 0 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMCSchedule>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 1 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMCUpdateSchedule>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 2 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMCAddSchedule>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 3 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMCDeleteSchedule>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 4 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMCRetrieveSchedule>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 5 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMCRetrieveScript>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 7 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMCOverrideRequest>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 8 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMCInfo>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 9 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMCScript>> seq( *serverConn );
                seq.Run();
            }

            CTILOG_INFO(dout, "Test completed. Press Enter to restart...");

            cin.ignore();
            if( cin.eof() )
            {
                break;
            }
        }
    }
}
