#include "yukon.h"

#include <crtdbg.h>
#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/cstring.h>
//#include <rw/db/connect.h>

#include "cparms.h"
#include "message.h"
#include "connection.h"
#include "ctinexus.h"
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

void main(int argc, char **argv)
{
    if( argc != 3 )
    {
        cout << "Arg 1:   dispatch server machine name" << endl;
        cout << "Arg 2:   # of messages to receive/seconds to wait" << endl;

        exit(-1);
    }

    RWTime startTime;
    long msgNum = 0;
    

    try
    {
        if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE))
        {
            cerr << "Could not install control handler" << endl;
            return;
        }

        CtiConnection myConnection(VANGOGHNEXUS, argv[1]);

        //  write the registration message (this is only done once, because if the database changes,
        //    the program name and such doesn't change - only our requested points.)

        RWCString regStr = "Lurker";
        
        myConnection.WriteConnQue( new CtiRegistrationMsg(regStr, rwThreadId( ), TRUE) );
        myConnection.WriteConnQue( new CtiPointRegistrationMsg( REG_ALL_PTS_MASK ) );

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
        myConnection.WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
        myConnection.ShutdownConnection();

    }
    catch( RWxmsg &msg )
    {
        cout << "Exception in Lurker: ";
        cout << msg.why() << endl;
    }

    cout << msgNum << " messages in " << RWTime( ).seconds( ) - startTime.seconds( ) << " seconds" << endl;

    exit(0);
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
            cout << "MultiMsg - contains " << msgMulti->getData( ).entries( ) << " messages" << endl;
            retval = 0;
            for( x = 0; x < msgMulti->getData( ).entries( ); x++ )
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


