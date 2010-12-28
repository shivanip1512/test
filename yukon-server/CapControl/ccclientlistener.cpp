#include "yukon.h"

#include "ccclientlistener.h"
#include "ccmessage.h"
#include "ccsubstationbusstore.h"
#include "configparms.h"
#include "ctibase.h"
#include "ccexecutor.h"
#include "logger.h"
#include "utility.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"

#include <rw/toolpro/inetaddr.h>

extern ULONG _CC_DEBUG;

using Cti::ThreadStatusKeeper;

CtiCCClientListener* CtiCCClientListener::_instance = NULL;

/*------------------------------------------------------------------------
    getInstance

    Returns a pointer to the singleton instance of the client listener.
---------------------------------------------------------------------------*/
CtiCCClientListener* CtiCCClientListener::getInstance()
{
    if ( _instance == NULL )
    {
        string str;
        char var[128];
        LONG capcontrolclientsport = CAPCONTROLNEXUS;

        strcpy(var, "CAP_CONTROL_PORT");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            capcontrolclientsport = atoi(str.c_str());
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << capcontrolclientsport << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _instance = new CtiCCClientListener(capcontrolclientsport);
    }

    return _instance;
}

std::vector<CtiCCClientConnection*>& CtiCCClientListener::getClientConnectionList()
{

  return _connections;
}


/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCClientListener::CtiCCClientListener(LONG port) : _port(port), _doquit(FALSE), _socketListener(NULL)
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCClientListener::~CtiCCClientListener()
{
    _socketListener = NULL;
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
        if (_socketListener != NULL)
        {
            //This delete must happen to interupt the thread.
            delete _socketListener;
            _socketListener = NULL;

            _listenerthr.join();
            _checkthr.join();
        }
    }
    catch(RWxmsg& msg)
    {
        std::cerr << msg.why() << endl;
    }
}

void CtiCCClientListener::BroadcastMessage(CtiMessage* msg)
{
    bool testValid = false;
    if( _CC_DEBUG & CC_DEBUG_CLIENT )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " BroadcastMessage() called." << endl;
    }

    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );
        for( int i = 0; i < _connections.size(); i++ )
        {
            try
            {
                // replicate message makes a deep copy

                try
                {
                     testValid = _connections[i]->isValid();
                }
                catch(...)
                {
                     CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                if(testValid)
                {
                    CtiMessage* replicated_msg = NULL;
                    try
                    {
                        replicated_msg = msg->replicateMessage();
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }

                    try
                    {
                        if( _CC_DEBUG & CC_DEBUG_CLIENT )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " Broadcasting classID:  " << replicated_msg->isA() << endl;
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                    try
                    {
                        _connections[i]->write(replicated_msg);
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << __FILE__ << " (" << __LINE__ <<
             ")  An unknown exception has occurred." << endl;
    }
    if( _CC_DEBUG & CC_DEBUG_CLIENT )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " BroadcastMessage() finished." << endl;
    }
}

/*---------------------------------------------------------------------------
    _listen

    Listens for connections and instantiates CtiCCConnection objects as
    necessary.  Each CtiCCConnection object is a observer of self in order
    for clients to receive notification of updates.
---------------------------------------------------------------------------*/
void CtiCCClientListener::_listen()
{
    //This must remain a member variable so it can be deleted to trigger an interupt.
    _socketListener = new RWSocketListener( RWInetAddr( (int) _port )  );

    do
    {
        try
        {
            {
                RWPortal portal = (*_socketListener)();

                CtiCCClientConnection* conn = new CtiCCClientConnection(portal);

                {
                    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );
                    _connections.push_back(conn);
                }
            }
        }
        catch(RWSockErr& msg)
        {
            if( msg.errorNumber() == 10004 )
            {
                /*CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << "CtiCCClientListener thread interupted" << endl;*/
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
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }



    } while ( !_doquit );


}

void CtiCCClientListener::_check()
{
    ThreadStatusKeeper threadStatus("CapControl _clientCheck");

    do
    {
        try
        {
            {
                RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

                std::vector<CtiCCClientConnection*>::iterator itr = _connections.begin();
                while( itr != _connections.end() )
                {
                    CtiCCClientConnection* toDelete = *itr;
                    if ( !toDelete->isValid() )
                    {
                        if( _CC_DEBUG & CC_DEBUG_CLIENT )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime()  << " - Removing Client Connection: " << toDelete->getConnectionName() << endl;
                        }
                        itr = _connections.erase(itr);
                        delete toDelete;
                        break;
                    }
                    else
                    {
                        ++itr;
                    }
                }
            }   //Release mutex
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        rwSleep(500);

        threadStatus.monitorCheck();

    } while ( !_doquit );

    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );
        delete_container(_connections);
        _connections.clear();
    }
}

