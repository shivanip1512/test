#include "precompiled.h"

#include "dllbase.h"
#include "logger.h"
#include "amq_constants.h"
#include "mc.h"
#include "std_helper.h"
#include "clistener.h"


using std::endl;


/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiMCClientListener::CtiMCClientListener() :
    _doquit(false),
    _conn_in_queue(NULL),
    _listenerConnection( Cti::Messaging::ActiveMQ::Queue::macs )
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiMCClientListener::~CtiMCClientListener()
{
}

/*---------------------------------------------------------------------------
    interrupt

    Stops listening at the specified socket address
-----------------------------------------------------------------------------*/
void CtiMCClientListener::interrupt(int id)
{
    CtiThread::interrupt( id );

    if( id != CtiThread::SHUTDOWN )
    {
        return;
    }

    _doquit = true;

    try{
        _listenerConnection.close();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*----------------------------------------------------------------------------
  BroadcastMessage

  Will replicate and send a CtiMessage to all connected clients.
  takes ownership and delete the message given to it.
----------------------------------------------------------------------------*/
void CtiMCClientListener::BroadcastMessage( CtiMessage* msg, Cti::ConnectionHandle connectionHandle )
{
    std::auto_ptr<CtiMessage> toSend(msg); // take ownership

    try
    {
        if( connectionHandle ) // send message to a single connection
        {
            CtiLockGuard<CtiMutex> guard(_connmutex);

            auto conn = Cti::findIfRef(_connections, [=](const CtiMCConnection &conn){ return conn.hasConnection(connectionHandle); });

            if( conn && conn->isValid() )
            {
                if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                {
                    CTILOG_DEBUG(dout, "Broadcasting classID: "<< toSend->isA());
                }

                conn->write(toSend.release());
            }
        }
        else // broadcast message to all
        {
            CtiLockGuard<CtiMutex> guard(_connmutex);

            for( ConnectionVec::iterator connItr = _connections.begin() ; connItr != _connections.end() ; ++connItr )
            {
                if( connItr->isValid() )
                {
                    if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                    {
                        CTILOG_DEBUG(dout, "Broadcasting classID: "<< toSend->isA());
                    }

                    connItr->write(toSend->replicateMessage());
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*---------------------------------------------------------------------------
    Check all connections, removes invalid connection
---------------------------------------------------------------------------*/
void CtiMCClientListener::checkConnections()
{
    CtiLockGuard<CtiMutex> guard(_connmutex);

    ConnectionVec::iterator itr = _connections.begin();
    while( itr != _connections.end() )
    {
        CtiMCConnection& connection = *itr;
        if( ! connection.isValid() )
        {
            itr = _connections.erase(itr); // Removing invalid connections.
            if( gMacsDebugLevel & MC_DEBUG_CONN )
            {
                CTILOG_DEBUG(dout, "Removing invalid connection.");
            }
        }
        else
        {
            ++itr;
        }
    }
}

/*---------------------------------------------------------------------------
    run

    Listens for connections and instantiates CtiMCConnection objects as
    necessary.
---------------------------------------------------------------------------*/
void CtiMCClientListener::run()
{
    try
    {
        // main loop
        for(;!_doquit;)
        {
            checkConnections();

            if( !_listenerConnection.verifyConnection() )
            {
                removeAllConnections();

                _listenerConnection.start();
            }

            if( _listenerConnection.acceptClient() )
            {
                // Create and add new connection manager
                std::auto_ptr<CtiMCConnection> new_connection( new CtiMCConnection( _listenerConnection, _conn_in_queue ));

                new_connection->start(); // Kick off the connection's communication threads.

                {
                    CtiLockGuard<CtiMutex> guard(_connmutex);
                    _connections.push_back( new_connection.release() );
                }

                CTILOG_INFO(dout, "New connection established.");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_INFO(dout, "Closing all client connections.");

    removeAllConnections();

    CTILOG_INFO(dout, "Exiting");
}

/*---------------------------------------------------------------------------
    Set queue to receive messages
---------------------------------------------------------------------------*/
void CtiMCClientListener::setQueue( CtiConnection::Que_t* queue )
{
    _conn_in_queue = queue;
}

/*---------------------------------------------------------------------------
    Close and delete all server connections
---------------------------------------------------------------------------*/
void CtiMCClientListener::removeAllConnections()
{
    CtiLockGuard<CtiMutex> guard(_connmutex);

    _connections.clear(); // deletes all server connections
}

