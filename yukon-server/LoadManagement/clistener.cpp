#include "precompiled.h"

#include "clistener.h"
#include "lmmessage.h"
#include "lmcontrolareastore.h"
#include "ctibase.h"
#include "executor.h"
#include "logger.h"

#include <rw/thr/thrfunc.h>

#include "amq_constants.h"

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
    _doquit(false),
    _listenerConnection( Cti::Messaging::ActiveMQ::Queue::loadmanagement )
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
    _started = true;

    RWThreadFunction thr_func = rwMakeThreadFunction( *this, &CtiLMClientListener::_listen );
    RWThreadFunction check_thr_func = rwMakeThreadFunction( *this, &CtiLMClientListener::_check );

    _listenerthr = thr_func;
    _checkthr = check_thr_func;

    thr_func.start();
    check_thr_func.start();
}

/*---------------------------------------------------------------------------
    stop

    Stops listening at the specified socket address
-----------------------------------------------------------------------------*/
void CtiLMClientListener::stop()
{
    try
    {
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Shutting down client listener thread...");
        }

        _doquit = true;

        try{
            _listenerConnection.close();
        }
        catch(...)
        {
            if(_LM_DEBUG & LM_DEBUG_STANDARD)
            {
                CTILOG_DEBUG(dout, "Unknown exception in CtiLMClientListener::stop()");
            }
        }

        _listenerthr.join(5000);
        _checkthr.join(5000);

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Client listener thread shutdown.");
        }
    }
    catch(RWxmsg& msg)
    {
        std::cerr << msg.why() << endl;
    }
}

void CtiLMClientListener::sendMessageToClient(std::auto_ptr<CtiMessage> msg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

    try
    {
        for each( CtiLMConnectionPtr conn in _connections )
        {
            if( conn.get() == msg->getConnectionHandle() && conn->isViable() )
            {
                conn->WriteConnQue(msg.release());
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
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

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
                _connections[i]->WriteConnQue(replicated_msg);
            }
        }
        //Use up the original on the first client, no waste
        if(_connections.size() > 0)
        {
            if( _connections[0]->isViable() )
            {
                _connections[0]->WriteConnQue(msg);
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
    try
    {
        // main loop
        for(;!_doquit;)
        {
            if( !_listenerConnection.verifyConnection() )
            {
                removeAllConnections();

                _listenerConnection.start();
            }

            if( _listenerConnection.acceptClient() )
            {
                // Create new connection manager
                CtiLMConnectionPtr new_conn( CTIDBG_new CtiServerConnection( _listenerConnection, &_incomingQueue ));

                // Kick off the connection's communication threads.
                new_conn->start();

                {
                    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );
                    _connections.push_back( new_conn );
                }

                CTILOG_INFO(dout, "New connection established.");
            }
        }
    }
    catch(RWxmsg& msg)
    {
        CTILOG_EXCEPTION_ERROR(dout, msg, "ConnectionHandler Failed");
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_INFO(dout, "Closing all client connections.");

    removeAllConnections();

    CTILOG_INFO(dout, "Client Listener Thread shutting down ");
}


CtiMessage* CtiLMClientListener::getQueue(unsigned time)
{
    return _incomingQueue.getQueue(time);
}


void CtiLMClientListener::_check()
{
    try
    {
        CTILOG_INFO(dout, "Client Check Thread started");

        do
        {
            try
            {
                {
                    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

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
                }   //Release mutex
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
            rwSleep(500);

        } while ( !_doquit );

        //Before we exit try to close all the connections
        removeAllConnections();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


void CtiLMClientListener::removeAllConnections()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

    _connections.clear();
}

