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
#include "configparms.h"
#include "ctibase.h"
#include "ccexecutor.h"
#include "logger.h"

#include <rw/toolpro/inetaddr.h>

extern BOOL _CC_DEBUG;

CtiCCClientListener* CtiCCClientListener::_instance = NULL;

/*------------------------------------------------------------------------
    getInstance
    
    Returns a pointer to the singleton instance of the client listener.
---------------------------------------------------------------------------*/
CtiCCClientListener* CtiCCClientListener::getInstance()
{
    if ( _instance == NULL )
    {
        RWCString str;
        char var[128];
        UINT capcontrolclientsport = CAPCONTROLNEXUS;

        strcpy(var, "CAP_CONTROL_PORT");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            UINT capcontrolclientsport = atoi(str.data());
            if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << capcontrolclientsport << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _instance = new CtiCCClientListener(capcontrolclientsport);
    }

    return _instance;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCClientListener::CtiCCClientListener(UINT port) : _port(port), _doquit(FALSE), _socketListener(NULL)
{  
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCClientListener::~CtiCCClientListener()
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

void CtiCCClientListener::BroadcastMessage(CtiMessage* msg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

    try
    {
        for( int i = 0; i < _connections.entries(); i++ )
        {
            // replicate message makes a deep copy
            if( _connections[i]->isValid() )
            {
                CtiMessage* replicated_msg = msg->replicateMessage();

                /*if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " Broadcasting classID:  " << replicated_msg->isA() << endl;
                }*/
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

/*---------------------------------------------------------------------------
    _listen
    
    Listens for connections and instantiates CtiCCConnection objects as 
    necessary.  Each CtiCCConnection object is a observer of self in order
    for clients to receive notification of updates.
---------------------------------------------------------------------------*/
void CtiCCClientListener::_listen()
{  
    _socketListener = new RWSocketListener( RWInetAddr( (int) _port )  );

    do
    {
        try
        {
            /*{    
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime()  << " - Listening for clients..." << endl;
            }*/

            {
                RWPortal portal = (*_socketListener)();

                CtiCCClientConnection* conn = new CtiCCClientConnection(portal);

                {
                    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

                    _connections.insert(conn);
                }

                ULONG secondsFrom1901 = RWDBDateTime().seconds();
                CtiCCExecutorFactory f;
                CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
                CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(*store->getCCSubstationBuses(secondsFrom1901)));
                try
                {
                    executor->Execute();
                    delete executor;
                    executor = f.createExecutor(new CtiCCCapBankStatesMsg(*store->getCCCapBankStates(secondsFrom1901)));
                    executor->Execute();
                    delete executor;
                    executor = f.createExecutor(new CtiCCGeoAreasMsg(*store->getCCGeoAreas(secondsFrom1901)));
                    executor->Execute();
                    delete executor;
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }
        }
        catch(RWSockErr& msg)
        {
            if( msg.errorNumber() == 10004 )
            {    
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << "CtiCCClientListener thread interupted" << endl;
                break;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << "CtiCCClientListener hickup: " << msg.errorNumber() << endl;
                }
            }
        }
        catch(RWxmsg& msg)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << "CtiCCClientListener hickup (RWxmsg&): " << msg.why() << endl;
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

                        CtiCCClientConnection* toDelete = _connections.removeAt(i);
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
            dout << RWTime()  << " - CtiCCClientListener::_listen() - closing " << _connections.entries() << " connections..." << endl;
        }*/

        _connections.clearAndDestroy();
    }

    /*{    
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime()  << " - CtiClientListener::_check - exiting" << endl;
    }*/
}

