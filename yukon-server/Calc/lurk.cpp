#include "precompiled.h"

#include <crtdbg.h>
#include <iostream>
using namespace std;

//
#include "cparms.h"
#include "message.h"
#include "connection_client.h"
#include "amq_constants.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "msg_dbchg.h"

BOOL bQuit = FALSE;

BOOL MyCtrlHandler(DWORD fdwCtrlType)
{
    cout << "CTRL+C detected - setting exit flag" << endl;
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

int inspectMessage( CtiMessage *message );

int main(int argc, char **argv)
{
    if( argc != 3 )
    {
        cout << "Arg 1:   dispatch server machine name" << endl;
        cout << "Arg 2:   # of messages to receive/seconds to wait" << endl;

        return -1;
    }

    CtiTime startTime;
    long msgNum = 0;

    if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE))
    {
        cerr << "Could not install control handler" << endl;
        return -1;
    }

    CtiClientConnection myConnection( Cti::Messaging::ActiveMQ::Queue::dispatch );
    myConnection.start();

    //  write the registration message (this is only done once, because if the database changes,
    //    the program name and such doesn't change - only our requested points.)

    string regStr = "Lurker";

    myConnection.WriteConnQue( CTIDBG_new CtiRegistrationMsg(regStr, GetCurrentThreadId(), true) );
    myConnection.WriteConnQue( CTIDBG_new CtiPointRegistrationMsg( REG_ALL_PTS_MASK ) );

    CtiMessage *incomingMsg;

    for( int i = 0, msg = 0; i < atoi( argv[2] ) && !bQuit; i++ )
    {
        //  wait up to a second for a message
        for( ; NULL == (incomingMsg = myConnection.ReadConnQue( 1000 )) && !bQuit; )
            ;

        if( bQuit )
            continue;  // so we exit the for without inspecting the NULL message

        float nowTime = ((float)clock( )/(float)CLOCKS_PER_SEC);

        cout << nowTime << " I just got message #" << ++msg << " on loop #" << (i + 1) << endl;

        msgNum += inspectMessage( incomingMsg );

        delete incomingMsg;   //  Make sure to delete this - its on the heap
    }

    //  tell Dispatch we're going away, then leave
    myConnection.WriteConnQue( CTIDBG_new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
    myConnection.close();

    cout << msgNum << " messages in " << CtiTime( ).seconds( ) - startTime.seconds( ) << " seconds" << endl;

    return 0;
}


int inspectMessage( CtiMessage *message )
{
    CtiMultiMsg *msgMulti;
    CtiPointDataMsg *pData;
    int x, retval;

    switch( message->isA( ) )
    {
        case MSG_POINTDATA:
            pData = (CtiPointDataMsg *)message;
            cout << "PointData:  point " << pData->getId( ) << ", value: " << pData->getValue( ) << endl;
            retval = 1;
            break;

        case MSG_MULTI:
            msgMulti = (CtiMultiMsg *)message;
            cout << "MultiMsg - contains " << msgMulti->getData( ).size( ) << " messages" << endl;
            retval = 0;
            for( x = 0; x < msgMulti->getData( ).size( ); x++ )
            {
                cout << "    multimsg submessage " << (x+1) << endl;
                cout << "        ";
                retval += inspectMessage( (CtiMessage *)(msgMulti->getData( )[x]) );
            }
            break;

        default:
            cout << __FILE__ << " (" << __LINE__ << ") I don't know how to handle messages of type \"" << message->stringID( ) << "\";  skipping" << endl;
    }
    return retval;
}


