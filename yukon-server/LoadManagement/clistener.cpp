#include "precompiled.h"

#include "clistener.h"
#include "lmmessage.h"
#include "lmcontrolareastore.h"
#include "dllbase.h"
#include "executor.h"
#include "logger.h"
#include "amq_constants.h"
#include "connection_listener.h"

using std::endl;
using std::string;

extern ULONG _LM_DEBUG;


/*------------------------------------------------------------------------
    static _instance

    boost shared pointer to a CtiCCClientListener instance
---------------------------------------------------------------------------*/
CtiLMClientListener CtiLMClientListener::_instance;

/*------------------------------------------------------------------------
    getInstance

    Returns a pointer to the singleton instance of the client listener.
---------------------------------------------------------------------------*/
CtiLMClientListener& CtiLMClientListener::getInstance()
{
    return _instance;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMClientListener::CtiLMClientListener() :
    _started(false),
    _doquit(false)
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMClientListener::~CtiLMClientListener()
{
    if( _started )
    {
        stop();
    }
}

/*---------------------------------------------------------------------------
    start

    Starts listening at the specified socket address.
---------------------------------------------------------------------------*/
void CtiLMClientListener::start()
{
    CTILOG_DEBUG(dout, "Starting the LM Client Listener");

    _started = true;

    _listenerthr = boost::thread( &CtiLMClientListener::_listen, this );
    _checkthr    = boost::thread( &CtiLMClientListener::_check, this );

    CTILOG_DEBUG(dout, "LM Client Listener is running");
}

/*---------------------------------------------------------------------------
    stop

    Stops listening at the specified socket address
-----------------------------------------------------------------------------*/
void CtiLMClientListener::stop()
{
    CTILOG_DEBUG(dout, "Stopping the LM Client Listener");

    _doquit = true;

    CTILOG_INFO(dout, "Closing all client connections.");

    _listenerthr.interrupt();
    _checkthr.interrupt();

    if ( ! _listenerthr.timed_join( boost::posix_time::seconds( 60 ) ) )
    {
        CTILOG_WARN( dout, "Client Listener listen thread did not shutdown gracefully. "
                           "Attempting a forced shutdown" );

        TerminateThread( _listenerthr.native_handle(), EXIT_SUCCESS );
    }

    if ( ! _checkthr.timed_join( boost::posix_time::seconds( 30 ) ) )
    {
        CTILOG_WARN( dout, "Client Listener check thread did not shutdown gracefully. "
                           "Attempting a forced shutdown" );

        TerminateThread( _checkthr.native_handle(), EXIT_SUCCESS );
    }

    removeAllConnections();

    _started = false;

    CTILOG_DEBUG(dout, "LM Client Listener is stopped");
}

void CtiLMClientListener::sendMessageToClient(std::unique_ptr<CtiMessage> msg)
{
    CtiLockGuard<CtiCriticalSection> guard( _connmutex );

    try
    {
        for( auto& conn : _connections )
        {
            if( conn && conn->getConnectionId() == msg->getConnectionHandle().getConnectionId() && conn->isViable() )
            {
                conn->WriteConnQue(msg.release(), CALLSITE);
                return;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiLMClientListener::BroadcastMessage(CtiMessage* msg)
{
    CtiLockGuard<CtiCriticalSection> guard( _connmutex );

    try
    {
        //Make a copy of msg for all the clients except the first

        if( _LM_DEBUG & LM_DEBUG_CLIENT )
        {
            CTILOG_DEBUG(dout, "Broadcasting message to " << _connections.size() << " clients");
        }

        for( int i = 1; i < _connections.size(); i++ )
        {
            // replicate message makes a deep copy
            if( _connections[i]->isViable() )
            {
                CtiMessage* replicated_msg = msg->replicateMessage();
                _connections[i]->WriteConnQue(replicated_msg, CALLSITE);
            }
        }
        //Use up the original on the first client, no waste
        if(_connections.size() > 0)
        {
            if( _connections[0]->isViable() )
            {
                _connections[0]->WriteConnQue(msg, CALLSITE);
                msg = NULL;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    if( msg != NULL )
    {
        delete msg;
        msg = NULL;
    }
}

/*---------------------------------------------------------------------------
    _listen

    Listens for connections and instantiates CtiLMConnection objects as
    necessary.  Each CtiLMConnection object is a observer of self in order
    for clients to receive notification of updates.
---------------------------------------------------------------------------*/
void CtiLMClientListener::_listen()
{
    CTILOG_DEBUG(dout, "LM Client Listener listen thread is starting");

    try
    {
        const auto queueName = Cti::Messaging::Qpid::Queue::loadmanagement;

        CtiListenerConnection _listenerConnection(queueName);

        while ( ! _doquit )
        {
            if( auto replyTo =_listenerConnection.acceptClient(); ! replyTo.empty() )
            {
                // Create new connection manager
                auto new_conn = std::make_shared<CtiServerConnection>( replyTo, queueName, &_incomingQueue );

                // Log the outQueue memory consumption every additional 100 messages queued or 5 minutes
                new_conn->setOutQueueLogging( 100, std::chrono::minutes( 5 ) );

                // Kick off the connection's communication threads.
                new_conn->start();

                {
                    CtiLockGuard<CtiCriticalSection> guard( _connmutex );
                    _connections.push_back( new_conn );
                }

                CTILOG_INFO(dout, "New connection established.");
            }
        }
    }
    catch (boost::thread_interrupted) 
    {
        CTILOG_DEBUG(dout, "LM Client Listener listen thread Interrupted, exiting");
    } 
    catch (...) 
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_INFO(dout, "LM Client Listener listen thread is stopping");
}


CtiMessage* CtiLMClientListener::getQueue(unsigned time)
{
    return _incomingQueue.getQueue(time);
}


void CtiLMClientListener::_check()
{
    CTILOG_DEBUG(dout, "LM Client Listener check thread is starting");

    while ( ! _doquit )
    {
        try
        {
            CtiLockGuard<CtiCriticalSection> guard( _connmutex );

            // Remove any invalid connections from our list
            CtiLMConnectionVec::iterator itr = _connections.begin();
            while( itr != _connections.end() )
            {
                if( ! (*itr)->isConnectionUsable() )
                {
                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CTILOG_DEBUG(dout, "Removing Client Connection: ");
                    }

                    // return an iterator pointing to the new location of the element that followed the last element erased
                    itr = _connections.erase(itr);
                }
                else
                {
                    ++itr;
                }
            }
        }
        catch (boost::thread_interrupted) 
        {
            CTILOG_DEBUG(dout, "LM Client Listener check thread Interrupted, exiting");
            break;
        } 
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

        Sleep(500);
    }

    CTILOG_INFO(dout, "LM Client Listener check thread is stopping");
}


void CtiLMClientListener::removeAllConnections()
{
    CtiLockGuard<CtiCriticalSection> guard( _connmutex );

    _connections.clear();
}

