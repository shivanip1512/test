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
    _doquit(FALSE),
    _listenerConnection( Cti::Messaging::ActiveMQ::Queue::loadmanagement )
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMClientListener::~CtiLMClientListener()
{
    stop();
}

/*---------------------------------------------------------------------------
    start

    Starts listening at the specified socket address.
---------------------------------------------------------------------------*/
void CtiLMClientListener::start()
{
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
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Shutting down client listener thread..." << endl;
        }

        _doquit = TRUE;

        try{
            _listenerConnection.close();
        }
        catch(...)
        {
            if(_LM_DEBUG & LM_DEBUG_STANDARD)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " Unknown exception in CtiLMClientListener::stop()" << endl;
            }
        }

        _listenerthr.join(5000);
        _checkthr.join(5000);

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Client listener thread shutdown." << endl;
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
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << __FILE__ << " (" << __LINE__ <<
             ")  An unknown exception has occurred." << endl;
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
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Broadcasting message to " << _connections.size() << " clients" << endl;
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
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << __FILE__ << " (" << __LINE__ <<
             ")  An unknown exception has occurred." << endl;
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

                {
                    CtiLockGuard< CtiLogger > logger_guard(dout);
                    dout << CtiTime() << " New connection established." << endl;
                }
            }
        }
    }
    catch(RWxmsg& msg)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << "ConnectionHandler Failed: " << msg.why() << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    {
        CtiLockGuard< CtiLogger > doubt_guard(dout);
        dout << CtiTime()  << " Closing all client connections."  << endl;
    }

    removeAllConnections();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Client Listener Thread shutting down " << endl;
    }
}


CtiMessage* CtiLMClientListener::getQueue(unsigned time)
{
    return _incomingQueue.getQueue(time);
}


void CtiLMClientListener::_check()
{
    try
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Client Check Thread started TID: " << CurrentTID () << endl;
        }

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
                        if( (*itr)->verifyConnection() != ClientErrors::None )
                        {
                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " - Removing Client Connection: " << endl;
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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            rwSleep(500);

        } while ( !_doquit );

        //Before we exit try to close all the connections
        removeAllConnections();

        /*{
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime()  << " - CtiLMClientListener::_check - exiting" << endl;
        }*/
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiLMClientListener::removeAllConnections()
{
    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

        /*{
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime()  << " - CtiLMClientListener::removeAllConnections() " << _connections.entries() << " connections..." << endl;
        }*/

        _connections.clear();
    }
}

