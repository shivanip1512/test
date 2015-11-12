#include "precompiled.h"

#include "queue.h"
#include "netports.h"
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "connection_client.h"
#include "amq_constants.h"
#include "pointtypes.h"

#include <iostream>
using namespace std;

BOOL           bQuit = FALSE;

BOOL MyCtrlHandler(DWORD fdwCtrlType)
{
    switch( fdwCtrlType )
    {

        /* Handle the CTRL+C signal. */

        case CTRL_C_EVENT:

        case CTRL_CLOSE_EVENT:

        case CTRL_BREAK_EVENT:

        case CTRL_LOGOFF_EVENT:

        case CTRL_SHUTDOWN_EVENT:

        default:

            bQuit = TRUE;
            return TRUE;

    }
}


int main(int argc, char **argv)
{
    INT point_type;

    if( argc < 5 )
    {
        cout << "Arg 1:   vangogh server machine name" << endl;
        cout << "Arg 2:   point to change" << endl;
        cout << "Arg 3:   value to update point to" << endl;
        cout << "Arg 4:   number of times to change point" << endl;
        cout << "Arg 5:   sleep duration between loops" << endl;

        return -1;
    }

    int Op, k;

    unsigned    pt = 1;

    if( !SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE) )
    {
        cerr << "Could not install control handler" << endl;
        return -1;
    }

    CtiClientConnection Connect( Cti::Messaging::ActiveMQ::Queue::dispatch );
    Connect.start();

    Connect.WriteConnQue( CTIDBG_new CtiRegistrationMsg( "point changer", GetCurrentThreadId(), true ), CALLSITE );

    CtiPointRegistrationMsg *ptReg = CTIDBG_new CtiPointRegistrationMsg( 0 );
    ptReg->insert( atol( argv[2] ) );
    Connect.WriteConnQue(ptReg, CALLSITE);

    CtiMessage *incoming = Connect.ReadConnQue( );
    CtiMultiMsg *ifIHaveTo;
    CtiPointType_t ptType;

    if( incoming->isA( ) == MSG_MULTI )
        ptType = (*(CtiMultiMsg *)incoming)[0]->getType( );
    else if( incoming->isA( ) == MSG_POINTDATA )
        ptType = ((CtiPointDataMsg *)incoming)->getType( );

    for( int i = 0; i < atoi( argv[4] ); i++ )
    {
        Connect.WriteConnQue(
            CTIDBG_new CtiPointDataMsg( atoi( argv[2] ), atof( argv[3] ) + i, NormalQuality, ptType, "Individual Point Change" ), CALLSITE );
        Sleep( atoi( argv[5] ) );
    }

    Connect.WriteConnQue( CTIDBG_new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 0 ), CALLSITE );
    Connect.close( );

    return 0;
}
