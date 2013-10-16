#include "precompiled.h"
#include "dllbase.h"
#include "logger.h"
#include "amq_constants.h"
#include "mc.h"
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
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " Unknown exception in CtiMCClientListener::interrupt()" << endl;
        }
    }
}

/*----------------------------------------------------------------------------
  BroadcastMessage

  Will replicate and send a CtiMessage to all connected clients.
  takes ownership and delete the message given to it.
----------------------------------------------------------------------------*/
void CtiMCClientListener::BroadcastMessage( CtiMessage* msg, void *ConnectionPtr )
{
    try
    {
        CtiLockGuard<CtiMutex> guard(_connmutex);

        for( ConnectionVec::iterator itr = _connections.begin() ; itr != _connections.end() ; ++itr )
        {
            CtiMCConnection& connection = *itr;

            // replicate message makes a deep copy
            if( connection.isValid() && ( ConnectionPtr == NULL || connection.hasConnection( ConnectionPtr )))
            {
                std::auto_ptr<CtiMessage> replicated_msg( msg->replicateMessage() );

                if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                {
                    CtiLockGuard< CtiLogger > g(dout);
                    dout << CtiTime() << " Broadcasting classID:  " << replicated_msg->isA() << endl;
                }

                connection.write( replicated_msg.release() );
            }
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << __FILE__ << " (" << __LINE__ <<
             ")  An unknown exception has occurred." << endl;
    }

    delete msg;
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
                CtiLockGuard< CtiLogger > guard(dout);
                dout << CtiTime() << " Removing invalid connection." << endl;
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

                {
                    CtiLockGuard< CtiLogger > guard(dout);
                    dout << CtiTime() << " New connection established." << endl;
                }
            }
        }
    }
    catch(...)
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " An exception occurred in CtiMCClientListener::run()" << endl;
        }
    }

    {
        CtiLockGuard< CtiLogger > logGuard(dout);
        dout << CtiTime()  << " Closing all client connections."  << endl;
    }

    removeAllConnections();

    if( gMacsDebugLevel & MC_DEBUG_CONN )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()  << " Exiting CtiMCClientListener::run()" << endl;
    }
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

