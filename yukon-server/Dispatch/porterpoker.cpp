

/*-----------------------------------------------------------------------------*
*
* File:   porterpoker
*
* Date:   8/26/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/08/24 13:51:36 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <crtdbg.h>
#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>
#include <rw\cstring.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "queue.h"
#include "exchange.h"
#include "netports.h"
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "connection.h"
#include "counter.h"
#include "pointtypes.h"
#include "numstr.h"

BOOL           bQuit = FALSE;

BOOL MyCtrlHandler(DWORD fdwCtrlType)
{
    switch(fdwCtrlType)
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

    dout.start();     // fire up the logger thread
    dout.setOutputPath(gLogDirectory.data());
    dout.setOutputFile(argv[0]);
    dout.setToStdOut(true);
    dout.setWriteInterval(0);

    /*
    dout << "Alt - C     Reset (cold start) all CCU 711's in system" << endl;
    dout << "Alt - D     Send Emetcon \"Doubles\" to field devices" << endl;
    dout << "Alt - E     Trace error communications only" << endl;
    dout << "Alt - F     Toggle trace filtering off, or reload from environment." << endl;
    dout << "             PORTER_TRACE_PORT    " << endl;
    dout << "             PORTER_TRACE_REMOTE  " << endl;
    dout << "Alt - H     This help screen" << endl;
    dout << "Alt - L     Toggle printer logging" << endl;
    dout << "Alt - P     Purge all port queues. (Careful)" << endl;
    dout << "Alt - Q     Display port queue counts / stats" << endl;
    dout << "Alt - R     Download all CCU Default Routes" << endl;
    dout << "Alt - S     Issue a system wide timesync" << endl;
    dout << "Alt - T     Trace all communications" << endl;
    */


    if(argc < 3)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl;
            dout << endl;
            dout << "Arg 1:  DISPATCH.EXE server machine name" << endl;
            dout << "Arg 2:  Command to request of porter " << endl;
            dout << endl;
            dout << "         0x74 - Trace all communications" << endl;
            dout << "         0x65 - Trace error communications only" << endl;
            dout << "         0x72 - Download all CCU Default Routes" << endl;
            dout << "         0x64 - Send Emetcon \"Doubles\" to field devices" << endl;
//             dout << "         0x66" << endl; // Not available!
            dout << "         0x6d - Dump memory (if compiled in)" << endl;
            dout << "         0x6c - Toggle printer logging" << endl;
            dout << "         0x63 - Reset (cold start) all CCU 711's in system" << endl;
            dout << "         0x6b - Reset the PIL interface" << endl;
            dout << "         0x73 - Timesync all syncable devices" << endl;
            dout << "         0x70 - Purge port queues" << endl;
            dout << "         0x71 - Print port queue information" << endl;
            dout << "         0x01 0xXXXXXXXX - Set Porter's PorterDebugLevel" << endl;
            dout << "         0x02 0xXXXXXXXX - Set Porter's DBDEBUGLEVEL" << endl;
        }
    }

    if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) MyCtrlHandler,  TRUE))
    {
        cerr << "Could not install control handler" << endl;
        return;
    }


    if(argc >= 3)
    {
        RWWinSockInfo info;

        try
        {
            char *pch;

            int command = (int)strtoul(argv[2], &pch, 0);
            int dblvl = 0;

            CtiConnection  Connect(VANGOGHNEXUS, argv[1]);
            Connect.WriteConnQue(new CtiRegistrationMsg(argv[0], rwThreadId(), FALSE));
            CtiPointRegistrationMsg    *PtRegMsg = CTIDBG_new CtiPointRegistrationMsg(REG_NOTHING);
            Connect.WriteConnQue(PtRegMsg);

            CtiCommandMsg *pCmd = new CtiCommandMsg(CtiCommandMsg::PorterConsoleInput, 15);

            pCmd->getOpArgList().append(-1);    // Token
            pCmd->getOpArgList().append(command);

            if( (command == 0x01 || command == 0x02) )
            {
                if( argc >= 4 )
                {
                    dblvl = strtoul(argv[3], &pch, 0);
                    pCmd->getOpArgList().append(dblvl);
                }
                else
                {
                    pCmd->getOpArgList().append(0x00000000);
                }
            }

            Connect.WriteConnQue(pCmd);

            Connect.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 0));

            CtiMessage *pMsg;
            while( 0 != (pMsg = Connect.ReadConnQue(1000)) )
            {
                delete pMsg;
                pMsg = 0;
            }

            Sleep(2500);
        }
        catch(RWxmsg &msg)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << msg.why() << endl;
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

    }

    // Make sure all the logs get output and done!
    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();


    exit(0);
}
