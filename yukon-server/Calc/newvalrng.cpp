#include "yukon.h"

#include <crtdbg.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "queue.h"
#include "exchange.h"
#include "netports.h"
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "connection.h"
#include "pointtypes.h"

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


void main(int argc, char **argv)
{
    INT point_type, numEntries;

    if( argc < 5 )
    {
        cout << "Arg 1:   vangogh server machine name" << Cti::endl;
        cout << "Arg 2:   start point to init" << Cti::endl;
        cout << "Arg 3:   end point to init" << Cti::endl;
        exit(-1);
    }

    try
    {
        int Op, k;

        unsigned    pt = 1;

        if( !SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE) )
        {
            cerr << "Could not install control handler" << Cti::endl;
            return;
        }

        CtiConnection Connect(VANGOGHNEXUS, argv[1]);

        Connect.WriteConnQue( CTIDBG_new CtiRegistrationMsg("point changer", rwThreadId(), TRUE) );

        CtiPointRegistrationMsg *ptReg = CTIDBG_new CtiPointRegistrationMsg( 0 );

        for( int l = atoi( argv[2] ); l < atoi( argv[3] ); l++ )
            ptReg->insert( l );
        Connect.WriteConnQue( ptReg );

        CtiMessage *incoming = Connect.ReadConnQue( );
        CtiMultiMsg *ifIHaveTo;
        CtiPointType_t ptType;
        int ptID;

        if( incoming->isA( ) == MSG_MULTI )
        {
            ifIHaveTo = (CtiMultiMsg *)incoming;
            numEntries = ifIHaveTo->getCount( );
            incoming = (*ifIHaveTo)[0];
        }
        else if( incoming->isA( ) == MSG_POINTDATA )
        {
            numEntries = 1;
        }

        for( int i = 0; i < numEntries; i++ )
        {
            ptType = ((CtiPointDataMsg *)incoming)->getType( );
            ptID   = ((CtiPointDataMsg *)incoming)->getId( );
            Connect.WriteConnQue( CTIDBG_new CtiPointDataMsg( ptID, (double)1.0, NormalQuality, ptType, "Individual Point Change") );
            if( numEntries - i - 1 )
                incoming = (*ifIHaveTo)[i+1];
        }

        Connect.WriteConnQue( CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0) );
        Connect.ShutdownConnection( );
    }
    catch( RWxmsg &msg )
    {
        cout << "Point Changer Exception: ";
        cout << msg.why( ) << Cti::endl;
    }

    exit( 0 );
}
