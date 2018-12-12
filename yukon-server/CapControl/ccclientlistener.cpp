#include "precompiled.h"

#include "ccclientlistener.h"
#include "ccmessage.h"
#include "dllbase.h"
#include "logger.h"
#include "utility.h"
#include "ThreadStatusKeeper.h"
#include "amq_constants.h"

extern unsigned long _CC_DEBUG;

using Cti::ThreadStatusKeeper;

/*------------------------------------------------------------------------
    static _instance


---------------------------------------------------------------------------*/
CtiCCClientListener CtiCCClientListener::_instance;


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
    _started(false),
    _doquit(false),
    _listenerConnection( Cti::Messaging::ActiveMQ::Queue::capcontrol )
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCClientListener::~CtiCCClientListener()
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
void CtiCCClientListener::start()
{
    CTILOG_DEBUG(dout, "Starting the Client Listener");

    _started = true;

    _listenerThr = boost::thread( &CtiCCClientListener::_listen, this );
    _checkThr    = boost::thread( &CtiCCClientListener::_check, this );

    CTILOG_DEBUG(dout, "Client Listener is running");
}

/*---------------------------------------------------------------------------
    stop

    Stops listening at the specified socket address
-----------------------------------------------------------------------------*/
void CtiCCClientListener::stop()
{
    CTILOG_DEBUG(dout, "Stopping the Client Listener");

    _doquit = true;

    _listenerThr.interrupt();
    _checkThr.interrupt();

    CTILOG_INFO(dout, "Closing all client connections.");

    // wait for the threads to stop

    if ( ! _listenerThr.timed_join( boost::posix_time::seconds( 60 ) ) )
    {
        CTILOG_WARN( dout, "Client Listener listen thread did not shutdown gracefully. "
                           "Attempting a forced shutdown" );

        TerminateThread( _listenerThr.native_handle(), EXIT_SUCCESS );
    }

    if ( ! _checkThr.timed_join( boost::posix_time::seconds( 30 ) ) )
    {
        CTILOG_WARN( dout, "Client Listener check thread did not shutdown gracefully. "
                           "Attempting a forced shutdown" );

        TerminateThread( _checkThr.native_handle(), EXIT_SUCCESS );
    }

    removeAllConnections();

    try
    {
        _listenerConnection.close();
    }
    catch(...)
    {
        if( _CC_DEBUG & CC_DEBUG_CLIENT )
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }

    _started = false;

    CTILOG_DEBUG(dout, "Client Listener is stopped");
}

void CtiCCClientListener::BroadcastMessage(CtiMessage* msg)
{
    bool testValid = false;
    if( _CC_DEBUG & CC_DEBUG_CLIENT )
    {
        CTILOG_DEBUG(dout, "BroadcastMessage() called.");
    }

    try
    {
        CTILOCKGUARD( CtiCriticalSection, guard, _connmutex );

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
                     CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }

                    try
                    {
                        if( _CC_DEBUG & CC_DEBUG_CLIENT )
                        {
                            CTILOG_DEBUG(dout, "Broadcasting classID:  " << replicated_msg->isA());
                        }
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                    try
                    {
                        _connections[i].write(replicated_msg);
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    if( _CC_DEBUG & CC_DEBUG_CLIENT )
    {
        CTILOG_DEBUG(dout, "BroadcastMessage() finished.");
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
    CTILOG_DEBUG(dout, "Client Listener listen thread is starting");

    ThreadStatusKeeper threadStatus("CapControl clientListener listen");

    try
    {
        // main loop
        while ( ! _doquit )
        {
            if( !_listenerConnection.verifyConnection() )
            {
                removeAllConnections();

                // proceed with (re)connection
                _listenerConnection.start();
            }

            if( _listenerConnection.acceptClient() )
            {
                // Create new connection manager
                std::unique_ptr<CtiCCClientConnection> new_conn( CTIDBG_new CtiCCClientConnection( _listenerConnection ));

                // Kick off the connection's communication threads.
                new_conn->start();

                {
                    CTILOCKGUARD( CtiCriticalSection, guard, _connmutex );
                    _connections.push_back( new_conn.release() );
                }

                CTILOG_INFO(dout, "New connection established.");
            }

            threadStatus.monitorCheck();
        }
    }
    catch (boost::thread_interrupted) 
    {
        CTILOG_DEBUG(dout, "Client Listener listen thread Interrupted, exiting");
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_DEBUG(dout, "Client Listener listen thread is stopping");
}

/*---------------------------------------------------------------------------
    check for any connection that are not valid and remove them
---------------------------------------------------------------------------*/
void CtiCCClientListener::_check()
{
    CTILOG_DEBUG(dout, "Client Listener check thread is starting");

    ThreadStatusKeeper threadStatus("CapControl clientListener check");

    while ( ! _doquit )
    {
        try
        {
            CTILOCKGUARD( CtiCriticalSection, guard, _connmutex );

            CtiCCConnectionVec::iterator itr = _connections.begin();
            while( itr != _connections.end() )
            {
                if ( (*itr).isValid() != TRUE )
                {
                    if( _CC_DEBUG & CC_DEBUG_CLIENT )
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
            CTILOG_DEBUG(dout, "Client Listener check thread Interrupted, exiting");
            break;
        } 
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

        threadStatus.monitorCheck();

        Sleep( 500 );
    }

    CTILOG_DEBUG(dout, "Client Listener check thread is stopping");
}

void CtiCCClientListener::removeAllConnections()
{
    CTILOCKGUARD( CtiCriticalSection, guard, _connmutex );
    _connections.clear();
}

