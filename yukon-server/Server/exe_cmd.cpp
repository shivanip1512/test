
#pragma warning( disable : 4786 )
/*-----------------------------------------------------------------------------*
*
* File:   exe_cmd
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/exe_cmd.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
#include <exception>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\threadid.h>

#include "dlldefs.h"
#include "con_mgr.h"
#include "message.h"

// #include "que_exec.h"
#include "server_b.h"
#include "msg_cmd.h"

#include "exe_cmd.h"
#include "logger.h"
#include "yukon.h"

INT CtiCommandExecutor::ServerExecute(CtiServer *Svr)
{
    INT nRet = NoError;      // Everything was ok, please clean up my message memory

    CtiCommandMsg* Cmd = (CtiCommandMsg*)getMessage();

    try
    {
        switch(Cmd->getOperation())
        {
        case (CtiCommandMsg::Shutdown):
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " MainThread: SHUTDOWN received from queue" << endl;
                }

                /*
                 *  We must wait until the requesting connection closes up the barnyard doors,
                 *  Then we release VGMain to do the same for all remaining connections.
                 */
                // This will block on return until the Out and In threads have stopped executing
                CtiConnectionManager *pConn = getConnectionHandle();
                Svr->clientShutdown(pConn);

                /*
                 *  Shutdown / delete the VGMain Listener socket which releases ConnectionHandler
                 *  Send a Client Shutdown message to each ConnecionManager's InThread to init
                 *  A general closeout from VGMain.
                 */
                Svr->shutdown();
                break;
            }
        case (CtiCommandMsg::LoopClient):
        case (CtiCommandMsg::NoOp):
            {
                // cout << "VGMain: Looping the Client " << endl;
                // "new" memory is deleted in the connection machinery!.
                // use the copy constructor to return to the client.
                getConnectionHandle()->WriteConnQue(new CtiCommandMsg(*Cmd));
                break;
            }
        case (CtiCommandMsg::AreYouThere):
            {
                if(getConnectionHandle()->getClientQuestionable())
                {
                    if( DebugLevel & 0x00001000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Client " << getConnectionHandle()->getClientName() <<
                        " responded to AreYouThere " << endl;
                    }
                    // OK, the client responded... drop our bad connection flag...
                    getConnectionHandle()->setClientQuestionable(FALSE);
                    getConnectionHandle()->setClientRegistered(TRUE);

                    Svr->clientArbitrationWinner( getConnectionHandle() );
                }
                else  // Client wants to hear from us?
                {
                    getConnectionHandle()->WriteConnQue(new CtiCommandMsg(*Cmd));
                }

                break;
            }
        case (CtiCommandMsg::ClientAppShutdown):
            {
                try
                {
                    RWCString name;

                    if(getConnectionHandle() != NULL)
                    {
                        name = getConnectionHandle()->getClientName();
                        if( DebugLevel & 0x00001000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " MainThread: Client " << name << " has requested shutdown via InThread" << endl;
                        }
                    }
                    // This will block on return until the Out and In threads have stopped executing
                    CtiConnectionManager *pConn = getConnectionHandle();
                    Svr->clientShutdown(pConn);

                    if( DebugLevel & 0x00001000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " MainThread: Connection " << name << "'s threads have terminated" << endl;
                    }
                }
                catch(...)
                {
                    cout << "**** Exception caught **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                break;
            }
        case (CtiCommandMsg::NewClient):
            {
                if( DebugLevel & 0x00001000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " MainThread: " << rwThreadId() << " New connection" << endl;
                }
                Svr->clientConnect((CtiConnectionManager*)getConnectionHandle());

                break;
            }
        default:
            {
                Svr->commandMsgHandler( Cmd );
            }
        }
    }
    catch(...)
    {
        {
            cout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return nRet;
}
CtiCommandExecutor::CtiCommandExecutor(CtiMessage *p) :
CtiExecutor(p)
{}

CtiCommandExecutor::~CtiCommandExecutor()
{}
