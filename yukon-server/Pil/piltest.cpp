#include "yukon.h"

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
#include "logger.h"
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "connection.h"
#include "cmdparse.h"


BOOL  bQuit = FALSE;

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
        // cout << __LINE__ << endl;
        return TRUE;

    }
}




void main(int argc, char **argv)
{
    if(argc <= 4)
    {
        cout << "Arg 1:   PIL server machine name" << endl;
        cout << "Arg 2:   this app's registration name" << endl;
        cout << "Arg 3:   PAO ID to submit command to  " << endl;
        cout << "Arg 4:   command string to submit  " << endl;

        exit(-1);
    }

#if 1

    dout.start();                       // fire up the logger thread
    dout.setOutputPath("c:\\temp\\");
    dout.setOutputFile("piltest");
    dout.setToStdOut(true);
    dout.setWriteInterval(0);


    CtiConnection * _vgConnection = CTIDBG_new CtiConnection( VANGOGHNEXUS, argv[1] );
    _vgConnection->WriteConnQue( CTIDBG_new CtiRegistrationMsg(argv[2], rwThreadId(), TRUE) );

    CtiConnection * _pilConnection = CTIDBG_new CtiConnection( PORTERINTERFACENEXUS, argv[1] );
    _pilConnection->WriteConnQue( CTIDBG_new CtiRegistrationMsg(argv[2], rwThreadId(), TRUE) );
    _pilConnection->WriteConnQue( CTIDBG_new CtiRequestMsg( atoi(argv[3]), RWCString(argv[4])) );

    CtiMessage *pMsg;
    int count = 0;

    while( 0 != (pMsg = _pilConnection->ReadConnQue(30000)) )
    {
        if(pMsg->isA() == MSG_PCRETURN)
        {
            CtiReturnMsg *pRet = (CtiReturnMsg*)pMsg;

            if(pRet->ExpectMore())
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Got a message **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                count++;
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Expect More Count = " << count << endl;
            }
        }
    }


    _vgConnection->WriteConnQue( CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15) );
    _pilConnection->WriteConnQue( CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15) );
    Sleep(5000);

    _vgConnection->ShutdownConnection();
    _pilConnection->ShutdownConnection();

    Sleep(5000);

#else

    try
    {
        RWWinSockInfo info;

        int Op, k;

        unsigned                   pt = 1;
        CtiMessage                 *IGMsg;
        CtiCommandMsg              NoOpMsg;     // Defaults to a NoOp request.
        CtiRegistrationMsg         RegMsg(argv[2], rwThreadId(), TRUE);
        CtiRequestMsg              request( atoi(argv[3]), RWCString(argv[4]) );

        Op = CtiCommandMsg::ClientAppShutdown;

        CtiCommandMsg      TerminateMsg(Op, 15);     // I want a Shutdown request here

        RWCollectable *c;

        RWSocketPortal psck(RWInetAddr(PORTERINTERFACENEXUS, argv[1]));
        CtiExchange Ex(psck);

        RegMsg.setMessagePriority(15);

        Ex.Out() << RegMsg;
        Ex.Out().vflush();

        Sleep(5000);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        request.dump();

        Ex.Out() << request;
        Ex.Out().vflush();


        Sleep(5000);

        Ex.Out() << TerminateMsg;
        Ex.Out().vflush();

        Sleep(5000);

    }
    catch(RWxmsg &msg)
    {
        cout << "Tester Exception: ";
        cout << msg.why() << endl;
    }
#endif

    exit(0);

}
