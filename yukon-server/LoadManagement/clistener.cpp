/*-----------------------------------------------------------------------------
    Filename:  clistener.cpp

    Programmer:  Josh Wolberg

    Description: Source file for CtiLMClientListener

    Initial Date:  2/7/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "clistener.h"
#include "lmmessage.h"
#include "lmcontrolareastore.h"
#include "ctibase.h"
#include "executor.h"
#include "logger.h"

#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include <rw/toolpro/inetaddr.h>

using std::endl;
using std::string;

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
        string str;
        char var[128];
        LONG loadmanagementclientsport = LOADMANAGEMENTNEXUS;

        strcpy(var, "LOAD_MANAGEMENT_PORT");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            loadmanagementclientsport = atoi(str.c_str());
            if( _LM_DEBUG & LM_DEBUG_CLIENT )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << loadmanagementclientsport << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _instance = CTIDBG_new CtiLMClientListener(loadmanagementclientsport);
    }

    return _instance;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMClientListener::CtiLMClientListener(LONG port) : _port(port), _doquit(FALSE)
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
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Shutting down client listener thread..." << endl;
        }

        _doquit = TRUE;

        _listenerthr.join(5000);
        _checkthr.join(5000);

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Client listener thread shutdown." << endl;
        }
    }
    catch(RWxmsg& msg)
    {
        std::cerr << msg.why() << endl;
    }
}

void CtiLMClientListener::sendMessageToClient(std::auto_ptr<CtiMessage> msg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

    try
    {
        for each( CtiLMConnectionPtr conn in _connections )
        {
            if( conn.get() == msg->getConnectionHandle() && conn->valid() )
            {
                conn->WriteConnQue(msg.release());
                return;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << __FILE__ << " (" << __LINE__ <<
             ")  An unknown exception has occurred." << endl;
    }
}

void CtiLMClientListener::BroadcastMessage(CtiMessage* msg)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

    try
    {
        //Make a copy of msg for all the clients except the first

        if( _LM_DEBUG & LM_DEBUG_CLIENT )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Broadcasting message to " << _connections.size() << " clients" << endl;
        }

        for( int i = 1; i < _connections.size(); i++ )
        {
            // replicate message makes a deep copy
            if( _connections[i]->valid() )
            {
                CtiMessage* replicated_msg = msg->replicateMessage();
                _connections[i]->WriteConnQue(replicated_msg);
            }
        }
        //Use up the original on the first client, no waste
        if(_connections.size() > 0)
        {
            if( _connections[0]->valid())
            {
                _connections[0]->WriteConnQue(msg);
                msg = NULL;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << __FILE__ << " (" << __LINE__ <<
             ")  An unknown exception has occurred." << endl;
    }
    if( msg != NULL )
    {
        delete msg;
        msg = NULL;
    }
}

/*---------------------------------------------------------------------------
    _listen

    Listens for connections and instantiates CtiLMConnection objects as
    necessary.  Each CtiLMConnection object is a observer of self in order
    for clients to receive notification of updates.
---------------------------------------------------------------------------*/
void CtiLMClientListener::_listen()
{
    do
    {
        int i=0;

        UINT sanity = 0;

        CtiExchange *XChg;
        RWInetPort  NetPort;
        RWInetAddr  NetAddr;
        RWSocket    listenerSocket;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Client Listener Thread started TID: " << CurrentTID () << endl;
        }

        NetPort = RWInetPort((int) _port);
        NetAddr = RWInetAddr(NetPort);

        listenerSocket.listen(NetAddr);

        // This is here for looks, in reality it is rarely called.
        if( !listenerSocket.valid() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Could not open socket " << NetAddr << " for listening" << endl;

            exit(-1);
        }

        do
        {
            try
            {
                // It seems necessary to make this copy. RW does this and now so do we.
                RWSocket tempSocket = listenerSocket;
                RWSocket newSocket = tempSocket.accept();
                RWSocketPortal sock;

                if( !newSocket.valid() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Could not accept new connection " << endl;
                }
                else
                {
                    // This is very important. We tell the socket portal that we own the socket!
                    sock = RWSocketPortal(newSocket, RWSocketPortalBase::Application);

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Connection Handler Thread. New connect. " << endl;
                    }

                    {
                        XChg = CTIDBG_new CtiExchange(sock);
                        CtiLMConnectionPtr conn(CTIDBG_new CtiConnection(XChg, &_incomingQueue));

                        {
                            RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );
                            _connections.push_back(conn); // Note that valid was already set in the connection constructor.
                        }

                        conn->ThreadInitiate();     // Kick off the connection's communication threads.
                        
                    }
                }
            }
            catch(RWSockErr& msg )
            {
                if(msg.errorNumber() == RWNETENOTSOCK)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Socket error RWNETENOTSOCK" << endl;
                }

                break; // break out of first loop and re set everything up??
            }
            catch(RWxmsg& msg )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << endl << "ConnectionHandler Failed: " ;
                    dout << msg.why() << endl;
                }
                break; // break out of first loop and re set everything up??
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                break;
            }

            rwSleep(500);
        } while ( !_doquit );

        try
        {
            // This forces the listener thread to exit on shutdown.
            listenerSocket.close();
        }
        catch(...)
        {
            // Dont really care, we are shutting down.
        }

    } while( !_doquit );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Client Listener Thread shutting down " << endl;
    }
}



CtiMessage* CtiLMClientListener::getQueue(unsigned time)
{
    return _incomingQueue.getQueue(time);
}

void CtiLMClientListener::_check()
{
    try
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Client Check Thread started TID: " << CurrentTID () << endl;
        }

        do
        {
            try
            {
                {
                    RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

                    // Remove any invalid connections from our list
                    CtiLMConnectionIter i = _connections.begin();
                    while(i != _connections.end())
                    {
                        CtiLMConnectionPtr conn = *i;
                        if(!conn->valid())
                        {
                            i = _connections.erase(i);
                        }
                        else
                        {
                            i++;
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
        } while ( !_doquit );

        {
            RWRecursiveLock<RWMutexLock>::LockGuard guard( _connmutex );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime()  << " - CtiLMClientListener::_listen() - closing " << _connections.entries() << " connections..." << endl;
            }*/

            //Before we exit try to close all the connections
            _connections.clear();
        }


        /*{
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime()  << " - CtiLMClientListener::_check - exiting" << endl;
        }*/
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

