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
            if( _listenerConnection.verifyConnection() != NORMAL )
            {
                removeAllConnections();

                _listenerConnection.establishConnection();
            }

            if( _listenerConnection.acceptClient() == NORMAL )
            {
                // Create and add new connection manager
                _connections.push_back( CTIDBG_new CtiMCConnection( _listenerConnection ));

                CtiMCConnection& new_connection = _connections.back();

                new_connection.addObserver((CtiObserver&) *this);
                new_connection.start(); // Kick off the connection's communication threads.

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
        RWMutexLock::LockGuard guard( _connmutex );

        {
            CtiLockGuard< CtiLogger > logGuard(dout);
            dout << CtiTime()  << " Closing all client connections."  << endl;
        }

        removeAllConnections();
    }

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
                vector<CtiMCConnection*>::iterator itr = _connections.begin();
                while( itr != _connections.end() ){
                {
                    CtiMCConnection* temp = *itr;
                    if ( !temp->isValid() )
                    {
                        {
                            CtiLockGuard< CtiLogger > guard(dout);
                            dout << CtiTime()  << " Connection closed, removing it." << temp << endl;
                        }

                        delete temp;
                        itr = _connections.erase(itr);
                        continue;
                    }else
                        ++itr;
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

                for( int i = 0; i < _connections.size(); i++ )
                {
                    // replicateMessage does a deep copy
                    CtiMultiMsg* replicated_multi = (CtiMultiMsg*) multi->replicateMessage();
                    _connections[i].write(replicated_multi);
                }

                // clear our multi for next use
                multi->clear();
            }

            rwSleep(1000);
        } while ( !_doquit );

        delete multi;
        multi = NULL;

        {
            RWMutexLock::LockGuard guard( _connmutex );

            {
                CtiLockGuard< CtiLogger > g(dout);
                dout << CtiTime()  << " Client listener shutting down all connections." << _connections.size() << " connections..." << endl;
            }

            //Before we exit try to close all the connections
            for ( int j = 0; j < _connections.size(); j++ )
            {
                _connections[j].close();
            }
            _connections.clear();//TS added this
        }

    } catch ( RWxmsg& msg )
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            RWMutexLock::LockGuard guard(coutMux);
            cout << CtiTime()  << " - CtiMCClientListener::check - " << msg.why() << endl;
        }
    }

    if( gMacsDebugLevel & MC_DEBUG_CONN )
    {
        RWMutexLock::LockGuard guard(coutMux);
        cout << CtiTime()  << " - CtiClientListener::_check - exiting" << endl;
    }
}

#endif
