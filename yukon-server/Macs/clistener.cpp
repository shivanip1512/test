#include "precompiled.h"


/*-----------------------------------------------------------------------------*
*
* File:   clistener
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/clistener.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2008/06/30 21:23:26 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*-----------------------------------------------------------------------------
    Filename:  clistener.cpp

    Programmer:  Aaron Lauinger

    Description: Source file for CtiMCClientListener

    Initial Date:  5/12/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/

#include "clistener.h"
#include "dllbase.h"
#include "utility.h"

using std::ostream;
using std::vector;
using std::endl;

#include <rw/toolpro/inetaddr.h>

#include "amq_constants.h"

ostream& operator<<( ostream& ostrm, CtiMCClientListener& client_listener )
{
    ostrm << "hi!" << endl;
    return ostrm;
}

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
  Does NOT delete the message given to it.

----------------------------------------------------------------------------*/

void CtiMCClientListener::BroadcastMessage(CtiMessage* msg, void *ConnectionPtr)
{
    RWMutexLock::LockGuard conn_guard( _connmutex );

    try
    {
        for( int i = 0; i < _connections.size(); i++ )
        {
            // replicate message makes a deep copy
            if( _connections[i].isValid() && (ConnectionPtr == NULL || &_connections[i] == ConnectionPtr) )
            {
                CtiMessage* replicated_msg = msg->replicateMessage();

                if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                {
                    CtiLockGuard< CtiLogger > g(dout);
                    dout << CtiTime() << " Broadcasting classID:  " << replicated_msg->isA() << endl;
                }
                _connections[i].write(replicated_msg);
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

void CtiMCClientListener::checkConnections()
{
    RWMutexLock::LockGuard conn_guard( _connmutex );

    //Removing invalid connections.
    boost::ptr_vector<CtiMCConnection>::iterator itr = _connections.begin();
    while (itr != _connections.end())
    {
        CtiMCConnection& connection = *itr;
        if( !connection.isValid() )
        {
            itr = _connections.erase(itr);

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

void CtiMCClientListener::update(CtiObservable& observable)
{
    CtiMCConnection& conn = (CtiMCConnection&) observable;

    // The connection could be notifying this if it has closed
    // or it received a new message
    if( !conn.isValid() )
    {
        RWMutexLock::LockGuard conn_guard( _connmutex );

        boost::ptr_vector<CtiMCConnection>::iterator itr = _connections.begin();
        while( itr != _connections.end() )
        {
            CtiMCConnection& to_remove = *itr;
            if( ((CtiMCConnection*) &observable) == &to_remove )
            {
                to_remove.deleteObserver((CtiObserver&) *this);
                _connections.erase(itr);
                break;
            }
            else
            {
                ++itr;
            }
        }
    }
    else
    {
        // connection is valid so see if there are any messages
        if( _conn_in_queue != NULL )
        {
            CtiMessage* msg;
            while( (msg = (CtiMessage*) conn.read(0)) != NULL )
            {
                msg->setConnectionHandle((void *)&conn);
                _conn_in_queue->putQueue(msg);
            }
        }
    }

    return;
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
                std::auto_ptr<CtiMCConnection> new_connection( CTIDBG_new CtiMCConnection( _listenerConnection ));

                new_connection->addObserver((CtiObserver&) *this);
                new_connection->start(); // Kick off the connection's communication threads.

                {
                    RWMutexLock::LockGuard conn_guard( _connmutex );
                    _connections.push_back( new_connection.release() );
                }

                {
                    CtiLockGuard< CtiLogger > guard(dout);
                    dout << CtiTime() << " New connection established." << endl;
                }
            }
        }
    }
    catch ( RWxmsg& msg )
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " An exception occurred in CtiMCClientListener::run() - " << msg.why() << endl;
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


void CtiMCClientListener::setQueue(CtiQueue< CtiMessage, std::greater<CtiMessage> >* queue )
{
    _conn_in_queue = queue;
}


void CtiMCClientListener::removeAllConnections()
{
    RWMutexLock::LockGuard guard( _connmutex );

    boost::ptr_vector<CtiMCConnection>::iterator itr = _connections.begin();

    while( itr != _connections.end() )
    {
        CtiMCConnection& connection = *itr;
        connection.deleteObserver((CtiObserver&) *this);
        connection.close();
        ++itr;
    }

    _connections.clear();
}

