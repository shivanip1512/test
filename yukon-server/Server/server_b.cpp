
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
* File:   server_b
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/server_b.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:20:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw\thr\mutex.h>

#include "executor.h"
#include "server_b.h"
#include "msg_cmd.h"
#include "yukon.h"
#include "logger.h"



DLLEXPORT bool isQuestionable(const CtiConnectionManager *ptr, void *narg)
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
    CtiLockGuard<CtiMutex> guard(server_mux);

    if(Listener) delete Listener;
    Listener = NULL;

    mConnectionTable.clearAndDestroy(); // The destructor of the List entries will close the In/OutThreads
}

void  CtiServer::clientConnect(CtiConnectionManager *CM)
{
    CtiLockGuard<CtiMutex> guard(server_mux);
    mConnectionTable.insert(CM);
}

void CtiServer::clientShutdown(CtiConnectionManager *&CM)
{
    if(CM != NULL)
    {
        CtiLockGuard<CtiMutex> server_guard(server_mux);      // Get a lock on it.

        // Must have propagated the shutdown message to the OutThread before this gets called.
        // This call will block until the threads have exited
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Client Shutdown " << CM->getClientName() << " / " << CM->getClientAppId() << " / " << CM->getPeer() << endl;
        }

        if(mConnectionTable.remove(CM))
        {
            // This connection manager is abandoned now...
            delete CM;
            CM = NULL;
        }
    }
}


int  CtiServer::clientRegistration(CtiConnectionManager *CM)
{
    int         nRet = NoError;
    RWTime      NowTime;
    RWBoolean   validEntry(TRUE);
    RWBoolean   removeMgr(FALSE);
    RWBoolean   questionedEntry(FALSE);
    CtiConnectionManager *Mgr;

    CtiLockGuard<CtiMutex> server_guard(server_mux);
    RWTPtrHashMultiSetIterator< CtiConnectionManager, vg_hash, equal_to<CtiConnectionManager> >  iter(mConnectionTable);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << NowTime.now() << " Validating " << CM->getClientName() << " / " << CM->getClientAppId() << " / " << CM->getPeer() << endl;
    }

    /*
     *  OK, now we need to check if the registration jives with our other client connections!
     *  if not, we will not allow this guy to have any operations though us.
     */

    for(;(Mgr = (CtiConnectionManager *)iter());)
    {
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
                        Mgr->WriteConnQue(new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15));  // Ask the old guy to respond to us..

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

        CM->WriteConnQue(new CtiCommandMsg(CtiCommandMsg::Shutdown, 15));  // Ask the new guy to blow off..

        {
            if(mConnectionTable.remove (CM))
            {
                // This connection manager is abandoned now...
                delete CM;
            }
        }
    }

    if(removeMgr && Mgr != NULL)
    {
        if(mConnectionTable.remove(Mgr))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << NowTime.now() << " " << Mgr->getClientName() << " / " << Mgr->getClientAppId() << " / " << Mgr->getPeer() << " just _lost_ the client arbitration." << endl;

            // This connection manager is abandoned now...
            delete Mgr;
        }
    }

    return nRet;
}


/*----------------------------------------------------------------------------*
 * This is a passthrough to handle specific commands on the server.
 *----------------------------------------------------------------------------*/
int  CtiServer::commandMsgHandler(const CtiCommandMsg *Cmd)
{
    int status = NORMAL;

    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "Unhandled command message " << Cmd->getOperation() << " sent to Main.." << endl;

    return status;
}


/*----------------------------------------------------------------------------*
 * The client CM has just responded that he's the man and the other guy
 * which made him become questionable should be blown away,.. find him.
 *----------------------------------------------------------------------------*/
int  CtiServer::clientArbitrationWinner(CtiConnectionManager *CM)
{
    int status = NORMAL;
    CtiConnectionManager *Mgr;

    CtiLockGuard<CtiMutex> server_guard(server_mux);      // Get a lock on it.

    // Now that it is locked, get an iterator
    RWTPtrHashMultiSetIterator< CtiConnectionManager, vg_hash, equal_to<CtiConnectionManager> >  iter(mConnectionTable);


    for(;(Mgr = (CtiConnectionManager *)iter());)
    {
        if((Mgr != CM)                                     &&      // Don't match on yourself
           (Mgr->getClientName() == CM->getClientName())   &&
           (Mgr->getClientRegistered() == RWBoolean(FALSE)))
        {
            // The connection Mgr has been refuted by the prior manager. Shut Mgr down...
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Connection " << Mgr->getClientName() << " to " << Mgr->getPeer() << " has been denied, entry will be deleted." << endl;
            }

            Mgr->WriteConnQue(new CtiCommandMsg(CtiCommandMsg::Shutdown, 15));  // Ask the new guy to blow off..

            if(mConnectionTable.remove(Mgr))
            {
                delete Mgr; // This connection manager is abandoned now...
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
    CtiConnectionManager *Mgr;

    RWTime      Now;

    CtiLockGuard<CtiMutex> server_guard(server_mux);      // Get a lock on it.

    // Now that it is locked, get an iterator
    RWTPtrHashMultiSetIterator< CtiConnectionManager, vg_hash, equal_to<CtiConnectionManager> >  iter(mConnectionTable);

    if(pClientCount != NULL)
    {
        *pClientCount = 0;
    }

    for(;(Mgr = (CtiConnectionManager *)iter());)
    {
        if( (Now.seconds() - Mgr->getLastReceiptTime().seconds()) > Mgr->getClientExpirationDelay() )
        {
            CtiCommandMsg *Cmd = new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15);
            Cmd->setOpString("Are You There");

            Mgr->WriteConnQue(Cmd);
            Mgr->setClientQuestionable(TRUE);
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
    CtiConnectionManager *Mgr;

    CtiLockGuard<CtiMutex> server_guard(server_mux);      // Get a lock on it.

    if(pDeadClients != NULL) *pDeadClients = 0;

    while( (Mgr = (CtiConnectionManager *)mConnectionTable.remove(isQuestionable, NULL)) != NULL)
    {
        if(pDeadClients != NULL) (*pDeadClients)++;

        delete Mgr; // This connection manager is abandoned now...
    }

    return status;
}


CtiServer::CtiServer(int QueueSize) :
_listenerAvailable(0),
mConnectionTable(),
MainQueue_(QueueSize)
{
}

CtiServer::~CtiServer()
{
    shutdown();
}

void CtiServer::CmdLine(int argc, char **argv)
{
    CtiLockGuard<CtiMutex> guard(server_mux);
    Options_.setOpts(argc, argv);
    Options_.Puke();
    return;
}

void CtiServer::join()
{
    MainThread_.join();     // He will have waited for this to terminate.
}
RWWaitStatus CtiServer::join(unsigned long milliseconds)
{
    return MainThread_.join(milliseconds);     // He will have waited for this to terminate.
}
