/*-----------------------------------------------------------------------------
    Filename:  clistener.cpp
                
    Programmer:  Josh Wolberg
    
    Description: Source file for CtiCCClientListener
        
    Initial Date:  8/15/2000
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/

#include "clistener.h"
#include "ccmessage.h"
#include "strategystore.h"
#include "capcontrol.h"
#include "ctibase.h"
#include "executor.h"
#include "logger.h"

#include <rw/toolpro/inetaddr.h>

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCClientListener::CtiCCClientListener(LONG port) : _port(port), _listener(0), _doquit(FALSE)
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
        _doquit = TRUE;

        if ( _listener != NULL )
        {
            delete _listener;
            _listener = NULL;

            _listenerthr.join();
            _checkthr.join();
        }

    } catch ( RWxmsg& msg)
    {
        cerr << msg.why() << endl;
    }
}
/*---------------------------------------------------------------------------
    update
    
    Inherited from CtiObserver, update is called when a CtiObservable that
    self is registered with notifies its observers of and update.
    
---------------------------------------------------------------------------*/
void CtiCCClientListener::update(CtiObservable& observable)
{
    //Propagate the update to our observers
    setChanged();
    notifyObservers();
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
        _listener = new RWSocketListener( RWInetAddr( (int) _port )  );

        for ( ; ; )
        {
            
            /*{    
                RWMutexLock::LockGuard guard(coutMux);
                cout << RWTime()  << " - Listening for clients..." << endl;
            }*/

            RWPortal portal = (*_listener)();

            CtiCCConnection* conn = new CtiCCConnection(portal);

            //Register the connection with us
            //so that it is notified of updates
            CtiCCServer::Instance()->addObserver( *conn );

            {
                RWMutexLock::LockGuard guard( _connmutex );

                _connections.insert(conn);
            }
            CtiCCExecutorFactory f;
            RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
            CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();
            CtiCCExecutor* executor = f.createExecutor(new CtiCCStrategyListMsg(store->Strategies()));
            executor->Execute(queue);
            delete executor;
            executor = f.createExecutor(new CtiCCStateListMsg(store->StateList()));
            executor->Execute(queue);
            delete executor;
            executor = f.createExecutor(new CtiCCAreaListMsg(store->AreaList()));
            executor->Execute(queue);
            delete executor;
        }
    } catch ( RWxmsg& msg )
    {
        /*{    
            RWMutexLock::LockGuard guard(coutMux);
            cout << "CtiCCClientListener::_listen - " << msg.why() << endl;
        }*/
    }
    catch ( ... )
    {
        {    
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - " << "Client Listener thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    /*{    
        RWMutexLock::LockGuard guard(coutMux);
        cout << RWTime()  << " - CtiCCClientListener::_listen() - exiting" << endl;
    }*/
}

void CtiCCClientListener::_check()
{
    try
    {
        do        
        {
            
            {
                RWMutexLock::LockGuard guard( _connmutex );

                for ( int i = 0; i < _connections.entries(); i++ )
                {
                    if ( !_connections[i]->isValid() )
                    {
                        /*{    
                            RWMutexLock::LockGuard guard(coutMux);
                            cout << RWTime()  << " - CtiCCListener::check - deleting connection addr:  " << _connections[i] << endl;
                        }*/

                        //Remove the connection from the server observer list
                        CtiCCServer::Instance()->deleteObserver( *(_connections[i]) );

                        delete _connections[i];
                        _connections.removeAt(i);
                        break;
                    }
                }
            }   //Release mutex

            rwSleep(500);
        } while ( !_doquit );

        {   
            RWMutexLock::LockGuard guard( _connmutex );

            /*{    
                RWMutexLock::LockGuard guard(coutMux);
                cout << RWTime()  << " - CtiCCClientListener::_listen() - closing " << _connections.entries() << " connections..." << endl;
            }*/

            //Before we exit try to close all the connections
            for ( int j = 0; j < _connections.entries(); j++ )
            {
                //Remove the connection from the server observer list
                 CtiCCServer::Instance()->deleteObserver( *(_connections[j]) );

                _connections[j]->close();
                delete _connections[j];
            }
        }

    } catch ( RWxmsg& msg )
    {
        /*{    
            RWMutexLock::LockGuard guard(coutMux);
            cout << RWTime()  << " - CtiCCClientListener::check - " << msg.why() << endl;
        }*/
    }

    /*{    
        RWMutexLock::LockGuard guard(coutMux);
        cout << RWTime()  << " - CtiClientListener::_check - exiting" << endl;
    }*/
}

