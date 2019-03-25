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

#include "connection_server.h"

#define private public
#define protected public

#include "CapControlPao.h"
#include "ccarea.h"
#include "ccAreaBase.h"
#include "cccapbank.h"
#include "ccfeeder.h"
#include "ccmonitorpoint.h"
#include "ccsparea.h"
#include "ccstate.h"
#include "ccsubstation.h"
#include "ccsubstationbus.h"
#include "DynamicCommand.h"
#include "GangOperatedVoltageRegulator.h"
#include "MsgAreas.h"
#include "MsgBankMove.h"
#include "MsgCapBankStates.h"
#include "MsgCapControlCommand.h"
#include "MsgCapControlMessage.h"
#include "MsgCapControlServerResponse.h"
#include "MsgCapControlShutdown.h"
#include "MsgChangeOpState.h"
#include "MsgDeleteItem.h"
#include "MsgItemCommand.h"
#include "MsgObjectMove.h"
#include "MsgSpecialAreas.h"
#include "MsgSubStationBus.h"
#include "MsgSubStations.h"
#include "MsgSystemStatus.h"
#include "MsgVerifyBanks.h"
#include "MsgVerifyInactiveBanks.h"
#include "MsgVerifySelectedBank.h"
#include "MsgVoltageRegulator.h"
#include "PhaseOperatedVoltageRegulator.h"
#include "VoltageRegulator.h"

#undef private
#undef protected

#include "test_cc_serialization.h"
#include "logManager.h"

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


const unsigned int RandomSeedBase = 200;


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
    doutManager.setOutputPath   ( gLogDirectory );
    doutManager.setOutputFile   ( "cc_server_client_serialization_test" );
    doutManager.setToStdOut     ( true );

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

            cout << CtiTime() << " New server connection established, Running test." << endl;

            // CtiCCShutdown is not process by java client
//            {
//                RandomGenerator::reset( 0 + RandomSeedBase );
//                ServerClientTestSequence<TestCase<CtiCCShutdown>> seq( *serverConn );
//                seq.Run();
//            }

            {
                RandomGenerator::reset( 1 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiCCServerResponse>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 2 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiCCCapBankStatesMsg>> seq( *serverConn );
                seq.Run();
            }

            // CtiCCObjectMoveMsg not found in java client?
//            {
//                RandomGenerator::reset( 3 + RandomSeedBase );
//                ServerClientTestSequence<TestCase<CtiCCObjectMoveMsg>> seq( *serverConn );
//                seq.Run();
//            }

            {
                RandomGenerator::reset( 4 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiCCGeoAreasMsg>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 5 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiCCSpecialAreasMsg>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 6 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiCCSubstationBusMsg>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 7 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiCCSubstationsMsg>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 8 + RandomSeedBase );
                ServerClientTestSequence<TestCase<DeleteItem>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 9 + RandomSeedBase );
                ServerClientTestSequence<TestCase<SystemStatus>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 10 + RandomSeedBase );
                ServerClientTestSequence<TestCase<VoltageRegulatorMessage>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 12 + RandomSeedBase );
                ServerClientTestSequence<TestCase<ItemCommand>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 13 + RandomSeedBase );
                ServerClientTestSequence<TestCase<ChangeOpState>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 14 + RandomSeedBase );
                ServerClientTestSequence<TestCase<CtiCCCapBankMoveMsg>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 15 + RandomSeedBase );
                ServerClientTestSequence<TestCase<VerifyBanks>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 16 + RandomSeedBase );
                ServerClientTestSequence<TestCase<VerifyInactiveBanks>> seq( *serverConn );
                seq.Run();
            }

            {
                RandomGenerator::reset( 17 + RandomSeedBase );
                ServerClientTestSequence<TestCase<VerifySelectedBank>> seq( *serverConn );
                seq.Run();
            }

            cout << CtiTime() << " Test completed. Press Enter to restart..." << endl << endl;
            cin.ignore();
            if( cin.eof() )
            {
                break;
            }
        }
    }
}
