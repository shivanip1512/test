/*-----------------------------------------------------------------------------
    Filename:  clistener.cpp
                
    Programmer:  Josh Wolberg
    
    Description: Source file for CtiLMClientListener
        
    Initial Date:  2/7/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "clistener.h"
#include "lmmessage.h"
#include "lmcontrolareastore.h"
#include "lmserver.h"
#include "ctibase.h"
#include "executor.h"
#include "logger.h"

#include <rw/toolpro/inetaddr.h>

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMClientListener::CtiLMClientListener(UINT port) : _port(port), _listener(0), _doquit(FALSE)
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
        _doquit = TRUE;

        if ( _listener != NULL )
        {
            delete _listener;
            _listener = NULL;

            _listenerthr.join();
            _checkthr.join();
        }

    }
    catch ( RWxmsg& msg)
    {
        cerr << msg.why() << endl;
    }
}
/*---------------------------------------------------------------------------
    update
    
    Inherited from CtiObserver, update is called when a CtiObservable that
    self is registered with notifies its observers of and update.
    
---------------------------------------------------------------------------*/
void CtiLMClientListener::update(CtiObservable& observable)
{
    //Propagate the update to our observers
    setChanged();
    notifyObservers();
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
        _listener = new RWSocketListener( RWInetAddr( (int) _port )  );
        /*{    
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime()  << " - Listening for clients..." << endl;
        }*/
    
        do
        {
            try
            {
                {
                    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );
    
                    RWPortal portal = (*_listener)();
    
                    CtiLMConnection* conn = new CtiLMConnection(portal);
    
                    //Register the connection with us
                    //so that it is notified of updates
                    CtiLMServer::getInstance()->addObserver( *conn );
    
                    {
                        RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );
    
                        _connections.insert(conn);
                    }
                    CtiLMExecutorFactory f;
                    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
                    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
                    CtiLMExecutor* executor = f.createExecutor(new CtiLMControlAreaMsg(*(store->getControlAreas(RWDBDateTime().seconds()))));
                    try
                    {
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
            rwSleep(500);
        } while ( !_doquit );
    
        /*{    
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime()  << " - CtiLMClientListener::_listen() - exiting" << endl;
        }*/
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiLMClientListener::_check()
{
    try
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
                                dout << RWTime()  << " - CtiLMClientListener::check - deleting connection addr:  " << _connections[i] << endl;
                            }*/
        
                            //Remove the connection from the server observer list
                            CtiLMServer::getInstance()->deleteObserver( *(_connections[i]) );
        
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
                dout << RWTime()  << " - CtiLMClientListener::_listen() - closing " << _connections.entries() << " connections..." << endl;
            }*/
    
            //Before we exit try to close all the connections
            for ( int j = 0; j < _connections.entries(); j++ )
            {
                //Remove the connection from the server observer list
                 CtiLMServer::getInstance()->deleteObserver( *(_connections[j]) );
    
                _connections[j]->close();
                delete _connections[j];
            }
        }
    
    
        /*{    
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime()  << " - CtiLMClientListener::_check - exiting" << endl;
        }*/
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

