/*-----------------------------------------------------------------------------
    Filename:  ccclientlistener.cpp
                
    Programmer:  Josh Wolberg
    
    Description: Source file for CtiCCClientListener
        
    Initial Date:  9/04/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "ccclientlistener.h"
#include "ccmessage.h"
#include "ccsubstationbusstore.h"
#include "ccserver.h"
#include "ctibase.h"
#include "ccexecutor.h"
#include "logger.h"

#include <rw/toolpro/inetaddr.h>

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCClientListener::CtiCCClientListener(UINT port) : _port(port), _listener(0), _doquit(FALSE)
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
    _listener = new RWSocketListener( RWInetAddr( (int) _port )  );

    do
    {
        try
        {
            /*{    
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime()  << " - Listening for clients..." << endl;
            }*/

            {
                RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

                RWPortal portal = (*_listener)();

                CtiCCClientConnection* conn = new CtiCCClientConnection(portal);

                //Register the connection with us
                //so that it is notified of updates
                CtiCCServer::getInstance()->addObserver( *conn );

                {
                    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

                    _connections.insert(conn);
                }
                CtiCCExecutorFactory f;
                RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
                CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
                CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(*(store->getCCSubstationBuses())));
                try
                {
                    executor->Execute(queue);
                    delete executor;
                    executor = f.createExecutor(new CtiCCCapBankStatesMsg(store->getCCCapBankStates()));
                    executor->Execute(queue);
                    delete executor;
                    executor = f.createExecutor(new CtiCCGeoAreasMsg(store->getCCGeoAreas()));
                    executor->Execute(queue);
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                delete executor;
            }
        }
        catch(RWSockErr& msg)
        {
            if( msg.errorNumber() == 10004 )
            {    
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << "CtiLMClientListener thread interupted" << endl;
                break;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << "CtiLMClientListener hickup: " << msg.errorNumber() << endl;
                }
                delete _listener;
                _listener = new RWSocketListener( RWInetAddr( (int) _port )  );
            }
        }
        catch(RWxmsg& msg)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << "CtiLMClientListener hickup (RWxmsg&): " << msg.why() << endl;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    } while ( !_doquit );

    /*{    
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime()  << " - CtiCCClientListener::_listen() - exiting" << endl;
    }*/
}

void CtiCCClientListener::_check()
{
    do        
    {
        try
        {
            {
                RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

                for ( int i = 0; i < _connections.entries(); i++ )
                {
                    if ( !_connections[i]->isValid() )
                    {
                        /*{    
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime()  << " - CtiCCListener::check - deleting connection addr:  " << _connections[i] << endl;
                        }*/

                        //Remove the connection from the server observer list
                        CtiCCServer::getInstance()->deleteObserver( *(_connections[i]) );

                        delete _connections[i];
                        _connections.removeAt(i);
                        break;
                    }
                }
            }   //Release mutex
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        rwSleep(500);
    } while ( !_doquit );

    {   
        RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

        /*{    
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime()  << " - CtiCCClientListener::_listen() - closing " << _connections.entries() << " connections..." << endl;
        }*/

        //Before we exit try to close all the connections
        for ( int j = 0; j < _connections.entries(); j++ )
        {
            //Remove the connection from the server observer list
             CtiCCServer::getInstance()->deleteObserver( *(_connections[j]) );

            _connections[j]->close();
            delete _connections[j];
        }
    }


    /*{    
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime()  << " - CtiClientListener::_check - exiting" << endl;
    }*/
}

