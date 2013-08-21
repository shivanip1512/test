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

#undef private
#undef protected

#include "test_serialization.h"

#include <crtdbg.h>
#include "connection_server.h"

using namespace std;

void __cdecl Purecall(void)
{
    autopsy( __FILE__, __LINE__);
}


bool bGCtrlC;
DLLIMPORT extern CtiLogger dout;

CtiListenerConnection listenerConn( "com.cooper.eas.yukon.conntest" );

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


const unsigned int RandomSeedBase = 0;


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

        cout << "Populating " << typeid(*_tc._imsg).name() << endl;

        _tc.Populate();

        _serverConn.WriteConnQue( _tc._imsg.release() );
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
    dout.start              ();
    dout.setOutputPath      ( gLogDirectory );
    dout.setRetentionLength ( gLogRetention );
    dout.setOutputFile      ( "server_client_serialization_test" );
    dout.setToStdOut        ( true );
    dout.setWriteInterval   ( 1000 );

    for(;!bGCtrlC;)
    {
        if( !listenerConn.verifyConnection() )
        {
            listenerConn.start();
        }

        // will wait here until a new client connection is accepted.
        if( listenerConn.acceptClient() )
        {
            auto_ptr<CtiServerConnection> serverConn( new CtiServerConnection( listenerConn ));
            serverConn->start();

            cout << CtiTime() << " New server connection established, Running test." << endl;

            {
                RandomGenerator::reset( 0 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMessage>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 1 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiCommandMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 3 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiDBChangeMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 4 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiLMControlHistoryMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 5 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiMultiMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 6 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiNotifAlarmMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 7 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiNotifEmailMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 8 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiCustomerNotifEmailMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 9 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiNotifLMControlMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 10 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiRequestMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 11 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiReturnMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 12 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiPointDataMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 13 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiPointRegistrationMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 14 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiQueueDataMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 15 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiRegistrationMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 16 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiRequestCancelMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 17 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiServerRequestMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 18 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiServerResponseMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 19 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiSignalMsg>> seq( * serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 20 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiTagMsg>> seq( * serverConn );
                seq.Run();
            }

            // Message not received on JAVA client
//            {
//                RandomGenerator::reset( 21 + RandomSeedBase );
//                ServerClientTestSequence<TestCase<CtiTraceMsg>> seq( * serverConn );
//                seq.Run();
//            }

            cout << CtiTime() << " Test completed. Press Enter to restart..." << endl << endl;
            cin.ignore();
            if( cin.eof() )
            {
                break;
            }
        }
    }
}
