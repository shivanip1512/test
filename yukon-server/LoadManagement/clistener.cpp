/*-----------------------------------------------------------------------------
    Filename:  clistener.cpp
                
    Programmer:  Josh Wolberg
    
    Description: Source file for CtiLMClientListener
        
    Initial Date:  2/7/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "clistener.h"
#include "lmmessage.h"
#include "lmcontrolareastore.h"
#include "configparms.h"
#include "ctibase.h"
#include "executor.h"
#include "logger.h"

#include <rw/toolpro/inetaddr.h>

extern ULONG _LM_DEBUG;

CtiLMClientListener* CtiLMClientListener::_instance = NULL;

/*------------------------------------------------------------------------
    getInstance
    
    Returns a pointer to the singleton instance of the client listener.
---------------------------------------------------------------------------*/
CtiLMClientListener* CtiLMClientListener::getInstance()
{
    if ( _instance == NULL )
    {
        RWCString str;
        char var[128];
        LONG loadmanagementclientsport = LOADMANAGEMENTNEXUS;

        strcpy(var, "LOAD_MANAGEMENT_PORT");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            LONG loadmanagementclientsport = atoi(str.data());
            if( _LM_DEBUG & LM_DEBUG_CLIENT )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << loadmanagementclientsport << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _instance = new CtiLMClientListener(loadmanagementclientsport);
    }

    return _instance;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMClientListener::CtiLMClientListener(LONG port) : _port(port), _doquit(FALSE), _socketListener(NULL)
{  
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMClientListener::~CtiLMClientListener()
{
    if( _instance != NULL )
    {
        delete _instance;
        _instance = NULL;
    }
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
        if ( _socketListener != NULL )
        {
            delete _socketListener;
            _socketListener = NULL;

            _listenerthr.join();
            _checkthr.join();
        }
    }
    catch(RWxmsg& msg)
    {
        cerr << msg.why() << endl;
    }
}

void CtiLMClientListener::BroadcastMessage(CtiMessage* msg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

    try
    {
	//Make a copy of msg for all the clients except the first
        for( int i = 1; i < _connections.entries(); i++ )
        {
            // replicate message makes a deep copy
            if( _connections[i]->isValid() )
            {
                CtiMessage* replicated_msg = msg->replicateMessage();
                _connections[i]->write(replicated_msg);
            }
        }
	//Use up the original on the first client, no waste
	if(_connections.entries() > 0)
	{
	    if( _connections[0]->isValid())
	    {
		_connections[0]->write(msg);
		msg = 0;
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
        _socketListener = new RWSocketListener( RWInetAddr( (int) _port )  );
        /*{    
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime()  << " - Listening for clients..." << endl;
        }*/
    
        do
        {
            try
            {
                {
                    RWPortal portal = (*_socketListener)();
    
                    CtiLMConnection* conn = new CtiLMConnection(portal);
    
                    {
                        RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );
    
                        _connections.insert(conn);
                    }
                    CtiLMExecutorFactory f;
                    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
                    {
                        RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

                        CtiLMExecutor* executor = f.createExecutor(new CtiLMControlAreaMsg(*store->getControlAreas(RWDBDateTime().seconds())));
                        try
                        {
                            executor->Execute();
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        delete executor;
                    }
                }
            }
            catch(RWSockErr& msg)
            {
                if( msg.errorNumber() == 10004 )
                {    
                    /*CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << "CtiLMClientListener thread interupted" << endl;*/
                    break;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "CtiLMClientListener hickup: " << msg.errorNumber() << endl;
                    }
                    delete _socketListener;
                    _socketListener = new RWSocketListener( RWInetAddr( (int) _port )  );
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
                            CtiLMConnection* toDelete = _connections.removeAt(i);
                            delete toDelete;
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
            _connections.clearAndDestroy();
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

