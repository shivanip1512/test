#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   clistener
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/clistener.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/05/26 20:57:43 $
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

#include <rw/toolpro/inetaddr.h>

ostream& operator<<( ostream& ostrm, CtiMCClientListener& client_listener )
{
    ostrm << "hi!" << endl;
    return ostrm;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiMCClientListener::CtiMCClientListener(UINT port) :
_port(port), _listener(0), _doquit(false), _conn_in_queue(NULL)
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
        return;

   try
   {
       if ( _listener != NULL )
       {
           // this causes the _listener() to throw
           delete _listener;
           _listener = NULL;
       }
   }
   catch(...)
   {
       if( gMacsDebugLevel & MC_DEBUG_CONN )
       {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " Unknown exception in CtiMCClientListener::interrupt()" << endl;
       }
   }
}

/*----------------------------------------------------------------------------
  BroadcastMessage

  Will replicate and send a CtiMessage to all connected clients.
  Does NOT delete the message given to it.

----------------------------------------------------------------------------*/

void CtiMCClientListener::BroadcastMessage(CtiMessage* msg)
{
    RWMutexLock::LockGuard conn_guard( _connmutex );

    try
    {
        for( int i = 0; i < _connections.entries(); i++ )
        {
            // replicate message makes a deep copy
            if( _connections[i]->isValid() )
            {
                CtiMessage* replicated_msg = msg->replicateMessage();

                if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                {
                    CtiLockGuard< CtiLogger > g(dout);
                    dout << RWTime() << " Broadcasting classID:  " << replicated_msg->isA() << endl;
                }
                _connections[i]->write(replicated_msg);
            }
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << RWTime() << __FILE__ << " (" << __LINE__ <<
             ")  An unknown exception has occurred." << endl;
    }
    delete msg;
}

void CtiMCClientListener::checkConnections()
{
    RWMutexLock::LockGuard conn_guard( _connmutex );

    CtiMCConnection* conn;
    vector< CtiMCConnection* > to_remove;

    for( int i = 0; i < _connections.entries(); i++ )
    {
        conn = _connections[i];
        if( !conn->isValid() )
        {
            to_remove.push_back(conn);
        }
    }

    for( int j = 0; j < to_remove.size(); j++ )
    {
        conn = to_remove[j];
        conn->deleteObserver( (CtiObserver&) *this);
        conn->close();
        _connections.remove(conn);

        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " Removing invalid connection." << endl;
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

        CtiMCConnection* to_remove = _connections.remove((CtiMCConnection*) &observable);

        if( to_remove != NULL )
        {
            to_remove->deleteObserver((CtiObserver&) *this);
            delete to_remove;
        }
        else
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > dout_guard(dout);
            dout << RWTime() << " CtiMCClientListener attempted to remove an unknown connection" << endl;
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
        _listener = new RWSocketListener( RWInetAddr( (int) _port )  );

        {
                CtiLockGuard< CtiLogger > guard(dout);
                dout << RWTime()  << " Waiting for client connections." << endl;
        }
        for ( ; ; )
        {
            // This returns each time a new connection is made
            RWPortal portal = (*_listener)();

            {
                CtiLockGuard< CtiLogger > guard(dout);
                dout << RWTime()  << " Accepted a client connection." << endl;
            }

            CtiMCConnection* conn = new CtiMCConnection();
            conn->addObserver((CtiObserver&) *this);

            {
                RWMutexLock::LockGuard guard( _connmutex );
                _connections.insert(conn);
            }

	    conn->initialize(portal);
        }
    }
    catch ( RWxmsg& msg )
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " An exception occurred in CtiMCClientListener::run() - " << msg.why() << endl;
        }
    }

    {
        RWMutexLock::LockGuard guard( _connmutex );

        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime()  << " Closing all client connections."  << endl;
        }

        for ( int i = 0; i < _connections.entries(); i++ )
        {
            CtiMCConnection* conn = _connections[i];
            conn->deleteObserver((CtiObserver&) *this);
            conn->close();
        }

        _connections.clearAndDestroy();
    }

    if( gMacsDebugLevel & MC_DEBUG_CONN )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime()  << " Exiting CtiMCClientListener::run()" << endl;
    }
}

void CtiMCClientListener::setQueue(CtiQueue< CtiMessage, less<CtiMessage> >* queue )
{
    _conn_in_queue = queue;
}

#ifdef PIGS_FLY
void CtiMCClientListener::_check()
{
    try
    {
        CtiMultiMsg* multi = new CtiMultiMsg();

        // Each time we wake up, check for invalid connections and
        // service the broadcast queue
        do
        {
            {
                RWMutexLock::LockGuard conn_guard( _connmutex );

                for ( int i = 0; i < _connections.entries(); i++ )
                {
                    if ( !_connections[i]->isValid() )
                    {
                        {
                            CtiLockGuard< CtiLogger > guard(dout);
                            dout << RWTime()  << " Connection closed, removing it." << _connections[i] << endl;
                        }

                        delete _connections[i];
                        _connections.removeAt(i);
                        continue;
                    }
                }
            }   //Release mutex

            RWCollectable* msg = NULL;
            while( _broadcast_queue.tryRead(msg) )
            {
                // careful... we just forced these things to be CtiMessages!
                // why oh why does multi message force this
                multi->insert( (CtiMessage*) msg);
            }

            if( multi->getCount() > 0 )
            {
                RWMutexLock::LockGuard conn_guard( _connmutex );

                for( int i = 0; i < _connections.entries(); i++ )
                {
                    // replicateMessage does a deep copy
                    CtiMultiMsg* replicated_multi = (CtiMultiMsg*) multi->replicateMessage();
                    _connections[i]->write(replicated_multi);
                }

                // clear our multi for next use
                multi->clear();
            }

            rwSleep(1000);
        } while ( !_doquit );

        {
            RWMutexLock::LockGuard guard( _connmutex );

            {
                CtiLockGuard< CtiLogger > g(dout);
                dout << RWTime()  << " Client listener shutting down all connections." << _connections.entries() << " connections..." << endl;
            }

            //Before we exit try to close all the connections
            for ( int j = 0; j < _connections.entries(); j++ )
            {
                _connections[j]->close();
                delete _connections[j];
            }
        }

    } catch ( RWxmsg& msg )
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            RWMutexLock::LockGuard guard(coutMux);
            cout << RWTime()  << " - CtiMCClientListener::check - " << msg.why() << endl;
        }
    }

    if( gMacsDebugLevel & MC_DEBUG_CONN )
    {
        RWMutexLock::LockGuard guard(coutMux);
        cout << RWTime()  << " - CtiClientListener::_check - exiting" << endl;
    }
}

#endif
