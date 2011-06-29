/*-----------------------------------------------------------------------------*
*
* File:   server_b
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/server_b.cpp-arc  $
* REVISION     :  $Revision: 1.25.14.1 $
* DATE         :  $Date: 2008/11/17 19:46:17 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "server_b.h"
#include "executor.h"
#include "msg_cmd.h"
#include "numstr.h"
#include "logger.h"
#include "utility.h"
#include "id_svr.h"

using namespace std;

DLLEXPORT bool isQuestionable(CtiServer::ptr_type &ptr, void* narg)
{
    bool bstatus = false;

    if(ptr->getClientQuestionable() == TRUE)
    {
        bstatus = true;
    }

    return bstatus;
}

void  CtiServer::shutdown()
{
    CtiServerExclusion server_guard(_server_exclusion);

    mConnectionTable.getMap().clear();
    MainQueue_.clearAndDestroy();
}

void  CtiServer::clientConnect(CtiServer::ptr_type CM)
{
    CtiServerExclusion server_guard(_server_exclusion);
    mConnectionTable.insert((long)CM.get(), CM);
}

void CtiServer::clientShutdown(CtiServer::ptr_type CM)
{
    if(CM)
    {
        CtiServerExclusion server_guard(_server_exclusion);      // Get a lock on it.

        // Must have propagated the shutdown message to the OutThread before this gets called.
        // This call will block until the threads have exited
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Client Shutdown " << CM->getClientName() << " / " << CM->getClientAppId() << " / " << CM->getPeer() << endl;
        }

        mConnectionTable.remove((long)CM.get());  // Get it out of the list, if it is in there.

        // This connection manager is abandoned now...
        // delete CM;       // Smart Pointer is no longer held here.  It will destruct on last release.
    }
}


int  CtiServer::clientRegistration(CtiServer::ptr_type CM)
{
    int         nRet = NoError;
    CtiTime      NowTime;
    RWBoolean   validEntry(TRUE);
    RWBoolean   removeMgr(FALSE);
    RWBoolean   questionedEntry(FALSE);
    ptr_type Mgr;

    CtiServerExclusion server_guard(_server_exclusion);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << NowTime.now() << " Validating " << CM->getClientName() << " / " << CM->getClientAppId() << " / " << CM->getPeer() << endl;
    }

    /*
     *  OK, now we need to check if the registration jives with our other client connections!
     *  if not, we will not allow this guy to have any operations though us.
     */

    spiterator itr;

    for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
    {
        Mgr = itr->second;

        if((Mgr != CM))      // Not me loser.
        {
            if(Mgr->getClientName() == CM->getClientName())
            {
                // Names match, what do I do.
                if(CM->getClientAppId() != (RWThreadId)0 && (Mgr->getClientAppId() == CM->getClientAppId()))
                {
                    // This guy is already registered. Might have lost his connection??
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime.now() << " " << CM->getClientName() << " just re-registered." << endl;
                    removeMgr = TRUE;
                    break;
                }
                else if(Mgr->getClientUnique())
                {
                    if( Mgr->getClientQuestionable() )       // has this guy been priviously pinged and not responded?
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << NowTime.now() << " " << CM->getClientName() << " / " << CM->getClientAppId() << " / " << CM->getPeer() << " just won a client arbitration." << endl;
                        removeMgr = TRUE;    // Make the old one go away...
                        break;
                    }
                    else
                    {
                        Mgr->setClientQuestionable(TRUE);

                        CtiCommandMsg *pCmd = CTIDBG_new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15);

                        pCmd->setSource(getMyServerName());
                        pCmd->setOpString(CompileInfo.version);

                        Mgr->WriteConnQue(pCmd);  // Ask the old guy to respond to us..

                        questionedEntry = TRUE;

                        // Old one wanted to be the only one
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << NowTime.now() << " New client \"" << CM->getClientName() << "\" conflicts with an existing client." << endl;

                        validEntry = TRUE;
                    }

                    break; // the for
                }
                else if(CM->getClientUnique())
                {
                    // New one wanted to be the only one
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime.now() << " New client is not unique as requested." << endl;
                    validEntry = FALSE;
                    break; // the for
                }
            }
        }
    }

    if(validEntry && !questionedEntry)
    {
        if(!CM->getClientQuestionable())      // This guy saw no conflicts...
        {
            CM->setClientRegistered();
        }
    }
    else
    {
        // For some reason, the connection has been refused. Shut it down...
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << NowTime.now() << " Connection rejected, entry will be deleted." << endl;
        }

        CM->WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::Shutdown, 15));  // Ask the new guy to blow off..

        {
            if(mConnectionTable.remove((long)CM.get()))
            {
                // This connection manager is abandoned now...
                // delete CM;   // destructor occurs if no references remain.
            }
        }
    }

    if(removeMgr && Mgr)
    {
        if(mConnectionTable.remove((long)Mgr.get()))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << NowTime.now() << " " << Mgr->getClientName() << " / " << Mgr->getClientAppId() << " / " << Mgr->getPeer() << " just _lost_ the client arbitration." << endl;
            }

            // This connection manager is abandoned now...
            // delete Mgr; // delete occurs if no references remain.
        }
    }

    return nRet;
}


/*----------------------------------------------------------------------------*
 * This is a passthrough to handle specific commands on the server.
 *----------------------------------------------------------------------------*/
int  CtiServer::commandMsgHandler(CtiCommandMsg *Cmd)
{
    int status = NORMAL;

    // CtiConnectionManager *pConn = (CtiConnectionManager *)Cmd->getConnectionHandle();
    ptr_type pConn = mConnectionTable.find((long)Cmd->getConnectionHandle());

    if(pConn)
    {
        switch(Cmd->getOperation())
        {
        case (CtiCommandMsg::Shutdown):
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " MainThread: SHUTDOWN received from queue.  Ignored." << endl;
                }

                /*
                 *  We must wait until the requesting connection closes up the barnyard doors,
                 *  Then we release VGMain to do the same for all remaining connections.
                 */
                // This will block on return until the Out and In threads have stopped executing

                // clientShutdown(pConn);

                /*
                 *  Shutdown / delete the VGMain Listener socket which releases ConnectionHandler
                 *  Send a Client Shutdown message to each ConnecionManager's InThread to init
                 *  A general closeout from VGMain.
                 */

                // shutdown();
                break;
            }
        case (CtiCommandMsg::LoopClient):
        case (CtiCommandMsg::NoOp):
            {
                // cout << "VGMain: Looping the Client " << endl;
                // "new" memory is deleted in the connection machinery!.
                // use the copy constructor to return to the client.
                pConn->WriteConnQue(CTIDBG_new CtiCommandMsg(*Cmd));
                break;
            }
        case (CtiCommandMsg::AreYouThere):
            {
                if(pConn->getClientQuestionable())
                {
                    if( DebugLevel & 0x00001000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Client " << pConn->getClientName() << " responded to AreYouThere " << endl;
                    }

                    if( !(Cmd->getOpString().empty()) )
                    {
                        if( CompileInfo.version != Cmd->getOpString() )
                        {
                            // This is a mismatch.  We should yelp about it.

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " !!!! WARNING !!!!   Client " << pConn->getClientName() << " has a version mismatch.  Client revision " << Cmd->getOpString() << endl;
                            }
                        }
                    }

                    // OK, the client responded... drop our bad connection flag...
                    pConn->setClientQuestionable(FALSE);
                    pConn->setClientRegistered(TRUE);

                    clientArbitrationWinner( pConn );
                }
                else  // Client wants to hear from us?
                {
                    if( !ciStringEqual(getMyServerName(),Cmd->getSource()) )
                        pConn->WriteConnQue(Cmd->replicateMessage());
                }

                break;
            }
        case (CtiCommandMsg::ClientAppShutdown):
            {
                try
                {
                    string name;

                    name = pConn->getClientName();
                    if( DebugLevel & 0x00001000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " MainThread: Client " << name << " has requested shutdown via InThread" << endl;
                    }
                    // This will block on return until the Out and In threads have stopped executing
                    clientShutdown(pConn);

                    if( DebugLevel & 0x00001000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " MainThread: Connection " << name << "'s threads have terminated" << endl;
                    }
                }
                catch(...)
                {
                    cout << "**** Exception caught **** " << __FILE__ << " (" << __LINE__ << ")" << Cti::endl;
                }

                break;
            }
        case (CtiCommandMsg::NewClient):
            {
                if( DebugLevel & 0x00000001)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " MainThread: " << rwThreadId() << " New connection" << endl;
                    dout << CtiTime() << " *** WARNING *** Use of this message is deprecated " << endl;
                }
                clientConnect(pConn);

                break;
            }
        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unhandled command message " << Cmd->getOperation() << " sent to Main.." << endl;
            }
        }
      }

    return status;
}


/*----------------------------------------------------------------------------*
 * The client CM has just responded that he's the man and the other guy
 * which made him become questionable should be blown away,.. find him.
 *----------------------------------------------------------------------------*/
int  CtiServer::clientArbitrationWinner(CtiServer::ptr_type CM)
{
    int status = NORMAL;
    ptr_type Mgr;

    CtiServerExclusion server_guard(_server_exclusion);      // Get a lock on it.

    // Now that it is locked, get an iterator
    spiterator itr;

    for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
    {
        Mgr = itr->second;

        if((Mgr != CM)                                     &&      // Don't match on yourself
           (Mgr->getClientName() == CM->getClientName())   &&
           (Mgr->getClientRegistered() == RWBoolean(FALSE)))
        {
            // The connection Mgr has been refuted by the prior manager. Shut Mgr down...
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Connection " << Mgr->getClientName() << " to " << Mgr->getPeer() << " has been denied, entry will be deleted." << endl;
            }

            Mgr->WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::Shutdown, 15));  // Ask the new guy to blow off..

            if(mConnectionTable.remove((long)Mgr.get()))
            {
                // delete Mgr; // This connection manager is abandoned now...
                // deleted on last reference release.
            }

            break;
        }
    }

    return status;
}

/*----------------------------------------------------------------------------*
 * This method sweeps the table asking all connections to respond to an
 * AreYouThere
 *----------------------------------------------------------------------------*/
int  CtiServer::clientConfrontEveryone(PULONG pClientCount)
{
    int status = NORMAL;
    ptr_type Mgr;

    CtiTime      Now;

    CtiServerExclusion server_guard(_server_exclusion);      // Get a lock on it.
    if(pClientCount != NULL) *pClientCount = 0;

    // Now that it is locked, get an iterator
    spiterator itr;

    for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
    {
        Mgr = itr->second;

        if( (Now.seconds() - Mgr->getLastReceiptTime().seconds()) > Mgr->getClientExpirationDelay() )
        {
            Mgr->setClientQuestionable(TRUE);

            CtiCommandMsg *Cmd = CTIDBG_new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15);

            Cmd->setSource(getMyServerName());
            Cmd->setOpString(CompileInfo.version);

            Mgr->WriteConnQue(Cmd);
        }

        if(pClientCount != NULL)
        {
            (*pClientCount)++;
        }
    }

    return status;
}


/*----------------------------------------------------------------------------*
 * This method sweeps the table blasting anyone marked as questionable.
 *----------------------------------------------------------------------------*/
int  CtiServer::clientPurgeQuestionables(PULONG pDeadClients)
{
    int status = NORMAL;
    ptr_type Mgr;

    CtiServerExclusion server_guard(_server_exclusion);      // Get a lock on it.

    if(pDeadClients != NULL) *pDeadClients = 0;

    while( (Mgr = mConnectionTable.remove(isQuestionable, NULL)) )
    {
        if(pDeadClients != NULL) (*pDeadClients)++;

        // delete Mgr; // This connection manager is abandoned now...
    }

    return status;
}


CtiServer::CtiServer() :
mConnectionTable(),
MainQueue_()
{
}

CtiServer::~CtiServer()
{
    shutdown();
}

void CtiServer::join()
{
    MainThread_.join();     // He will have waited for this to terminate.
}
RWWaitStatus CtiServer::join(unsigned long millis)
{
    return MainThread_.join(millis);     // He will have waited for this to terminate.
}

/* Use Only as a last resort */
void CtiServer::terminate()
{
    MainThread_.terminate(); //This is a last resort
}

string CtiServer::getMyServerName() const
{
    return string("Server Base");
}

CtiServer::ptr_type CtiServer::findConnectionManager( long cid )
{
    return mConnectionTable.find(cid);
}

