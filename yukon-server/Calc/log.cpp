#include "yukon.h"

#include <crtdbg.h>
#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/cstring.h>
#include <rw/db/connect.h>

#include "connection.h"
#include "ctinexus.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "pointtypes.h"

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

int loopno;

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

        CtiConnection Connect(VANGOGHNEXUS, argv[1]);

        //  write the registration message (this is only done once, because if the database changes,
        //    the program name and such doesn't change - only our requested points.)

        RWCString regStr = "CalcLogTest";
        
        Connect.WriteConnQue( new CtiRegistrationMsg(regStr, rwThreadId( ), TRUE) );
        Connect.WriteConnQue( new CtiPointRegistrationMsg( REG_ALL_PTS_MASK ) );

        CtiMessage *incomingMsg;

        for( int i = 0, msg = 0; i < atoi( argv[2] ) && !bQuit; i++ )
        {
            //  wait up to a second for a message
            for( ; NULL == (incomingMsg = Connect.ReadConnQue( 1000 )) && !bQuit; )
                ;
            
            if( bQuit )
                continue;  // so we exit the for without inspecting the NULL message

            float nowTime = ((float)clock( )/(float)CLOCKS_PER_SEC);

            loopno = i;
            msgNum += inspectMessage( incomingMsg );

            delete incomingMsg;   //  Make sure to delete this - its on the heap
        }

        //  tell Dispatch we're going away, then leave
        Connect.WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 0) );
        Connect.ShutdownConnection();
    }
    catch( RWxmsg &msg )
    {
        cout << "Exception in Lurker: ";
        cout << msg.why() << endl;
    }

    exit(0);
}


int inspectMessage( CtiMessage *message )
{
    CtiMultiMsg *msgMulti;
    CtiPointDataMsg *pData;
    int x, retval;

    int doneID = 0;
    switch( message->isA( ) )
    {
        case MSG_POINTDATA:
            pData = (CtiPointDataMsg *)message;
                    //  loopno+1000 << pData->getId( ) << ":" << 
            cout << pData->getValue( ) << " ";
            retval = 1;
            break;
        
        case MSG_MULTI:
            msgMulti = (CtiMultiMsg *)message;
            retval = 0;
            for( x = 0; x < msgMulti->getData( ).entries( ); x++ )
            {
//                cout << x << "th time through the loop" << endl;
                int minID = 100000, cID, cy;
                for( int y = 0; y < msgMulti->getData( ).entries( ); y++ )
                {
                    cID = ((CtiPointDataMsg *)(msgMulti->getData( )[y]))->getId( );
//                    cout << "testing ID " << cID << ", index " << y << " : ";
                    if( cID < minID && doneID < cID )
                    {
                        minID = cID;
                        cy = y;
//                        cout << "min" << endl;
                    }
//                    else
//                        cout << "not min" << endl;
                }
                doneID = minID;
//                cout << "now we've done ID " << doneID << ", index " << cy << " : ";
                retval += inspectMessage( (CtiMessage *)(msgMulti->getData( )[cy]) );
//                cout << endl;
            }
            cout << endl;
            break;

        default:
            cout << __FILE__ << " (" << __LINE__ << ") I don't know how to handle messages of type \"" << message->stringID( ) << "\";  skipping" << endl;
    }
    return retval;
}


