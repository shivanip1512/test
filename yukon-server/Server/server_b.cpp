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
    CTILOG_ENTRY(dout, "");

    CtiServerExclusion server_guard(_server_exclusion);

    mConnectionTable.getMap().clear();
    MainQueue_.clearAndDestroy();
}

void  CtiServer::clientConnect(CtiServer::ptr_type &CM)
{
    CTILOG_ENTRY(dout, CM->getConnectionId());

    CtiServerExclusion server_guard(_server_exclusion);
    CTILOG_INFO(dout, "inserting CM id " << CM->getConnectionId() << ", use_count=" << CM.use_count());
    mConnectionTable.insert(CM->getConnectionId(), CM);
}

void CtiServer::clientShutdown(CtiServer::ptr_type &CM)
{
    CTILOG_ENTRY(dout, CM->getConnectionId());
    if(CM)
    {
        CtiServerExclusion server_guard(_server_exclusion);      // Get a lock on it.

        // Must have propagated the shutdown message to the OutThread before this gets called.
        // This call will block until the threads have exited
        CTILOG_INFO(dout, "Client Shutdown on connection id " << CM->getConnectionId() << " for " << CM->getClientName() << " / " << CM->getClientAppId() << " / " << CM->getPeer() << " / " << CM->who());

        CTILOG_INFO(dout, "removing CM id " << CM->getConnectionId() << ", use_count=" << CM.use_count());
        mConnectionTable.remove(CM->getConnectionId());  // Get it out of the list, if it is in there.

        CTILOG_INFO(dout, "CM id " << CM->getConnectionId() << " removed, use_count=" << CM.use_count());
    }
}


YukonError_t CtiServer::clientRegistration(CtiServer::ptr_type &CM)
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

                        Mgr->WriteConnQue(pCmd, CALLSITE);  // Ask the old guy to respond to us..

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

        CM->WriteConnQue( CTIDBG_new CtiCommandMsg( CtiCommandMsg::Shutdown, 15 ), CALLSITE );  // Ask the new guy to blow off..

        {
            if(mConnectionTable.remove(CM->getConnectionId()))
            {
                // This connection manager is abandoned now...
                // delete CM;   // destructor occurs if no references remain.
            }
        }
    }

    if(removeMgr && Mgr)
    {
        if(mConnectionTable.remove(Mgr->getConnectionId()))
        {
            CTILOG_INFO(dout, "Connection id " << Mgr->getConnectionId() << " " << Mgr->getClientName() <<" / "<< Mgr->getClientAppId() <<" / "<< Mgr->getPeer() <<" just _lost_ the client arbitration.");
        }
    }

    return nRet;
}


/*----------------------------------------------------------------------------*
 * This is a passthrough to handle specific commands on the server.
 *----------------------------------------------------------------------------*/
void CtiServer::commandMsgHandler(CtiCommandMsg *Cmd)
{
    ptr_type pConn = findConnectionManager(Cmd->getConnectionHandle());

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
                pConn->WriteConnQue( CTIDBG_new CtiCommandMsg( *Cmd ), CALLSITE );
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
                        pConn->WriteConnQue( Cmd->replicateMessage(), CALLSITE );
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
int  CtiServer::clientArbitrationWinner(CtiServer::ptr_type &CM)
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
            CTILOG_INFO(dout, "Connection id "<< Mgr->getConnectionId() << " " << Mgr->getClientName() <<" to "<< Mgr->getPeer() <<" has been denied, entry will be deleted.");

            Mgr->WriteConnQue( CTIDBG_new CtiCommandMsg( CtiCommandMsg::Shutdown, 15 ), CALLSITE );  // Ask the new guy to blow off..

            mConnectionTable.remove(Mgr->getConnectionId());

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

        CTILOG_DEBUG(dout, "Considering confront on " << Mgr->getClientName() << " (id=" << Mgr->getConnectionId()
            << "), use_count=" << Mgr.use_count() << " " << " Last Receipt was " << (Now.seconds() - Mgr->getLastReceiptTime().seconds())
            << " Expiration is " << Mgr->getClientExpirationDelay());

        if( (Now.seconds() - Mgr->getLastReceiptTime().seconds()) > Mgr->getClientExpirationDelay() )
        {
            Mgr->setClientQuestionable(TRUE);
            CTILOG_DEBUG(dout, "Sending AreYouThere to connection id " << Mgr->getConnectionId() << " for "
                << Mgr->getClientName() << " / " << Mgr->getClientAppId()
                << " / " << Mgr->getPeer() << " / " << Mgr->who());

            CtiCommandMsg *Cmd = CTIDBG_new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15);

            Cmd->setSource(getMyServerName());
            Cmd->setOpString(CompileInfo.version);

            Mgr->WriteConnQue( Cmd, CALLSITE );
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
        CTILOG_DEBUG(dout, "Purging connection id " << Mgr->getConnectionId() << " for "
            << Mgr->getClientName() << " / " << Mgr->getClientAppId()
            << " / " << Mgr->getPeer() << " / " << Mgr->who() << " use_count=" << Mgr.use_count() << " ");

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
    CTILOG_DEBUG( dout, "CtiServer::~CtiServer()" );

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
void CtiServer::terminateServerThread()
{
    TerminateThread(_mainThread.native_handle(), EXIT_SUCCESS); //This is a last resort
}

string CtiServer::getMyServerName() const
{
    return string("Server Base");
}

CtiServer::ptr_type CtiServer::findConnectionManager(const Cti::ConnectionHandle handle)
{
    return mConnectionTable.find(handle.getConnectionId());
}

