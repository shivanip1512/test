#include "precompiled.h"

#include "server_b.h"
#include "msg_cmd.h"
#include "id_svr.h"

#include <boost/date_time/posix_time/posix_time_duration.hpp>

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
    CTILOG_INFO(dout, "Client Connect on handle " << (unsigned long)CM.get() << " for " << CM->getClientName() << " / " << CM->getClientAppId() << " / " << CM->getPeer() << " / " << CM->who());
    mConnectionTable.insert((long)CM.get(), CM);
}

void CtiServer::clientShutdown(CtiServer::ptr_type CM)
{
    if(CM)
    {
        CtiServerExclusion server_guard(_server_exclusion);      // Get a lock on it.

        // Must have propagated the shutdown message to the OutThread before this gets called.
        // This call will block until the threads have exited
        CTILOG_INFO(dout, "Client Shutdown on handle " << (unsigned long)CM.get() << " for " << CM->getClientName() << " / " << CM->getClientAppId() << " / " << CM->getPeer() << " / " << CM->who());

        mConnectionTable.remove((unsigned long)CM.get());  // Get it out of the list, if it is in there.

        // This connection manager is abandoned now...
        // delete CM;       // Smart Pointer is no longer held here.  It will destruct on last release.
    }
}


YukonError_t CtiServer::clientRegistration(CtiServer::ptr_type CM)
{
    YukonError_t nRet = ClientErrors::None;
    CtiTime  NowTime;
    bool     validEntry(TRUE);
    bool     removeMgr(FALSE);
    bool     questionedEntry(FALSE);
    ptr_type Mgr;

    CtiServerExclusion server_guard(_server_exclusion);

    CTILOG_INFO(dout, "Validating "<< CM->getClientName() <<" / "<< CM->getClientAppId() <<" / "<< CM->getPeer());
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
                if(CM->getClientAppId() && Mgr->getClientAppId() == CM->getClientAppId())
                {
                    // This guy is already registered. Might have lost his connection??
                    CTILOG_INFO(dout, CM->getClientName() <<" just re-registered.");

                    removeMgr = TRUE;
                    break;
                }
                else if(Mgr->getClientUnique())
                {
                    if( Mgr->getClientQuestionable() )       // has this guy been priviously pinged and not responded?
                    {
                        CTILOG_INFO(dout, CM->getClientName() <<" / "<< CM->getClientAppId() <<" / "<< CM->getPeer() <<" just won a client arbitration.");

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
                        CTILOG_INFO(dout, "New client \""<< CM->getClientName() <<"\" conflicts with an existing client.");

                        validEntry = TRUE;
                    }

                    break; // the for
                }
                else if(CM->getClientUnique())
                {
                    // New one wanted to be the only one
                    CTILOG_INFO(dout, "New client is not unique as requested.");

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
        CTILOG_WARN(dout, "Connection rejected, entry will be deleted.");

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
            CTILOG_INFO(dout, Mgr->getClientName() <<" / "<< Mgr->getClientAppId() <<" / "<< Mgr->getPeer() <<" just _lost_ the client arbitration.");
            // This connection manager is abandoned now...
            // delete Mgr; // delete occurs if no references remain.
        }
    }

    return nRet;
}


/*----------------------------------------------------------------------------*
 * This is a passthrough to handle specific commands on the server.
 *----------------------------------------------------------------------------*/
void CtiServer::commandMsgHandler(CtiCommandMsg *Cmd)
{
    ptr_type pConn = mConnectionTable.find((long)Cmd->getConnectionHandle());

    if(pConn)
    {
        switch(Cmd->getOperation())
        {
        case (CtiCommandMsg::Shutdown):
            {
                CTILOG_INFO(dout, "MainThread: SHUTDOWN received from queue.  Ignored.");

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
                        CTILOG_DEBUG(dout, "Client "<< pConn->getClientName() <<" responded to AreYouThere");
                    }

                    if( !(Cmd->getOpString().empty()) )
                    {
                        if( CompileInfo.version != Cmd->getOpString() )
                        {
                            // This is a mismatch.  We should yelp about it.
                            CTILOG_WARN(dout, "Client "<< pConn->getClientName() <<" has a version mismatch.  Client revision "<< Cmd->getOpString());
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
                        CTILOG_DEBUG(dout, "MainThread: Client "<< name <<" has requested shutdown via InThread");
                    }
                    // This will block on return until the Out and In threads have stopped executing
                    clientShutdown(pConn);

                    if( DebugLevel & 0x00001000)
                    {
                        CTILOG_DEBUG(dout, "MainThread: Connection "<< name <<"'s threads have terminated");
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                break;
            }
        case (CtiCommandMsg::NewClient):
            {
                if( DebugLevel & 0x00000001)
                {
                    CTILOG_DEBUG(dout, "NewClient request (*** WARNING *** Use of this message is deprecated)");
                }
                clientConnect(pConn);

                break;
            }
        default:
            {
                CTILOG_WARN(dout, "Unhandled command message "<< Cmd->getOperation() <<" sent to Main..");
            }
        }
    }
}


/*----------------------------------------------------------------------------*
 * The client CM has just responded that he's the man and the other guy
 * which made him become questionable should be blown away,.. find him.
 *----------------------------------------------------------------------------*/
int  CtiServer::clientArbitrationWinner(CtiServer::ptr_type CM)
{
    int status = ClientErrors::None;
    ptr_type Mgr;

    CtiServerExclusion server_guard(_server_exclusion);      // Get a lock on it.

    // Now that it is locked, get an iterator
    spiterator itr;

    for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
    {
        Mgr = itr->second;

        if((Mgr != CM)                                     &&      // Don't match on yourself
           (Mgr->getClientName() == CM->getClientName())   &&
           (Mgr->getClientRegistered() == false))
        {
            // The connection Mgr has been refuted by the prior manager. Shut Mgr down...
            CTILOG_INFO(dout, "Connection "<< Mgr->getClientName() <<" to "<< Mgr->getPeer() <<" has been denied, entry will be deleted.");

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
    int status = ClientErrors::None;
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
            CTILOG_DEBUG(dout, "Sending AreYouThere to " << (unsigned long)Mgr.get() << " for " 
                << Mgr->getClientName() << " / " << Mgr->getClientAppId() 
                << " / " << Mgr->getPeer() << " / " << Mgr->who());

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
    int status = ClientErrors::None;
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
    _mainThread.join();     // He will have waited for this to terminate.
}
bool CtiServer::join(unsigned long millis)
{
    return _mainThread.timed_join(boost::posix_time::milliseconds(millis));
}

/* Use Only as a last resort */
void CtiServer::terminate()
{
    TerminateThread(_mainThread.native_handle(), EXIT_SUCCESS); //This is a last resort
}

string CtiServer::getMyServerName() const
{
    return string("Server Base");
}

CtiServer::ptr_type CtiServer::findConnectionManager( long cid )
{
    return mConnectionTable.find(cid);
}

