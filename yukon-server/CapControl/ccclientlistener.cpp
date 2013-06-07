#include "precompiled.h"

#include "ccclientlistener.h"
#include "ccmessage.h"
#include "ccsubstationbusstore.h"
#include "ctibase.h"
#include "ccexecutor.h"
#include "logger.h"
#include "utility.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"

#include <rw/toolpro/inetaddr.h>

#include "amq_constants.h"

using std::endl;

extern unsigned long _CC_DEBUG;

using Cti::ThreadStatusKeeper;

/*------------------------------------------------------------------------
    static _instance


---------------------------------------------------------------------------*/
CtiCCClientListener CtiCCClientListener::_instance = CtiCCClientListener();


/*------------------------------------------------------------------------
    getInstance

    Returns a pointer to the singleton instance of the client listener.
---------------------------------------------------------------------------*/
CtiCCClientListener& CtiCCClientListener::getInstance()
{
    return _instance;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCClientListener::CtiCCClientListener() :
    _doquit(false),
    _listenerConnection( Cti::Messaging::ActiveMQ::Queue::capcontrol )
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCClientListener::~CtiCCClientListener()
{
    stop();
}

/*---------------------------------------------------------------------------
    start

    Starts listening at the specified socket address.
---------------------------------------------------------------------------*/
void CtiCCClientListener::start()
{
    RWThreadFunction thr_func = rwMakeThreadFunction( *this, &CtiCCClientListener::_listen );
    RWThreadFunction check_thr_func = rwMakeThreadFunction( *this, &CtiCCClientListener::_check );

    _listenerthr = thr_func;
    _checkthr = check_thr_func;

    thr_func.start();
    check_thr_func.start();
}

/*---------------------------------------------------------------------------
    stop

    Stops listening at the specified socket address
-----------------------------------------------------------------------------*/
void CtiCCClientListener::stop()
{
    try
    {
        if( _CC_DEBUG & CC_DEBUG_CLIENT )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " - Shutting down client listener thread..." << endl;
        }

        _doquit = true;

        try{
            _listenerConnection.close();
        }
        catch(...)
        {
            if( _CC_DEBUG & CC_DEBUG_CLIENT )
            {
                CtiLockGuard< CtiLogger > guard(dout);
                dout << CtiTime() << " Unknown exception in CtiCCClientListener::stop()" << endl;
            }
        }

        // wait for the threads to stop
        _listenerthr.join();
        _checkthr.join();
    }
    catch(RWxmsg& msg)
    {
        std::cerr << msg.why() << endl;
    }
}

void CtiCCClientListener::BroadcastMessage(CtiMessage* msg)
{
    bool testValid = false;
    if( _CC_DEBUG & CC_DEBUG_CLIENT )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " BroadcastMessage() called." << endl;
    }

    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );
        for( int i = 0; i < _connections.size(); i++ )
        {
            try
            {
                // replicate message makes a deep copy

                try
                {
                     testValid = _connections[i].isValid();
                }
                catch(...)
                {
                     CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                if(testValid)
                {
                    CtiMessage* replicated_msg = NULL;
                    try
                    {
                        replicated_msg = msg->replicateMessage();
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }

                    try
                    {
                        if( _CC_DEBUG & CC_DEBUG_CLIENT )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " Broadcasting classID:  " << replicated_msg->isA() << endl;
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                    try
                    {
                        _connections[i].write(replicated_msg);
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << __FILE__ << " (" << __LINE__ <<
             ")  An unknown exception has occurred." << endl;
    }
    if( _CC_DEBUG & CC_DEBUG_CLIENT )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " BroadcastMessage() finished." << endl;
    }
}

/*---------------------------------------------------------------------------
    _listen

    Listens for connections and instantiates CtiCCConnection objects as
    necessary.  Each CtiCCConnection object is a observer of self in order
    for clients to receive notification of updates.
---------------------------------------------------------------------------*/
void CtiCCClientListener::_listen()
{
    try
    {
        // main loop
        for(;!_doquit;)
        {
            if( _listenerConnection.verifyConnection() != NORMAL )
            {
                removeAllConnections();

                // proceed with (re)connection
                _listenerConnection.establishConnection();
            }

            if( _listenerConnection.acceptClient() == NORMAL )
            {
                // Create new connection manager
                std::auto_ptr<CtiCCClientConnection> new_conn( CTIDBG_new CtiCCClientConnection( _listenerConnection ));

                // Kick off the connection's communication threads.
                new_conn->start();

                _connections.push_back( new_conn.release() );

                {
                    CtiLockGuard< CtiLogger > logger_guard(dout);
                    dout << CtiTime() << " New connection established." << endl;
                }
            }
        }
    }
    catch(RWxmsg& msg)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "CtiCCClientListener hickup (RWxmsg&): " << msg.why() << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime()  << " Closing all client connections."  << endl;
    }

    removeAllConnections();
}

/*---------------------------------------------------------------------------
    check for any connection that are not valid and remove them
---------------------------------------------------------------------------*/
void CtiCCClientListener::_check()
{
    ThreadStatusKeeper threadStatus("CapControl _clientCheck");

    do
    {
        try
        {
            {
                RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

                CtiCCConnectionVec::iterator itr = _connections.begin();
                while( itr != _connections.end() )
                {
                    if ( (*itr).isValid() != TRUE )
                    {
                        if( _CC_DEBUG & CC_DEBUG_CLIENT )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime()  << " - Removing Client Connection: " << endl;
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

        threadStatus.monitorCheck();

    } while ( !_doquit );

    //Before we exit try to close all the connections
    removeAllConnections();
}


void CtiCCClientListener::removeAllConnections()
{
    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );
        _connections.clear();
    }
}
