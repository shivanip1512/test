/*-----------------------------------------------------------------------------
    Filename:  ccserver.cpp

    Programmer:  Josh Wolberg
    
    Description:  Source file for CtiCCServer.

    Initial Date:  9/04/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#include "ccserver.h"
#include "ctibase.h"
#include "logger.h"
#include "configparms.h"

extern BOOL _CC_DEBUG;

//The singleton instance of the server                                       
CtiCCServer* CtiCCServer::_instance = NULL;

//The default port where the server will listen for client connections
int CtiCCServer::_capcontrolclientsport = CAPCONTROLNEXUS;

/*------------------------------------------------------------------------
    Instance
    
    Returns a pointer to the singleton instance of the server.
---------------------------------------------------------------------------*/
CtiCCServer* CtiCCServer::getInstance()
{
    if ( _instance == NULL )
        _instance = new CtiCCServer();

    return _instance;
}

/*---------------------------------------------------------------------------
    start
    
    Starts up the server
---------------------------------------------------------------------------*/
void CtiCCServer::start()
{
    RWCString str;
    char var[128];

    strcpy(var, "CAP_CONTROL_PORT");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        _capcontrolclientsport = atoi(str.data());
        if( _CC_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << _capcontrolclientsport << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    if ( !_running && !_dostop )
    {
        _running = TRUE;

        RWThreadFunction func = rwMakeThreadFunction( *this, &CtiCCServer::_checkstatus );
        _checkthr = func;

        func.start();

        if ( _listener == NULL )
            _listener = new CtiCCClientListener( _capcontrolclientsport );

        _listener->start();
    }
}

/*---------------------------------------------------------------------------
    stop
    
    Stops the server
---------------------------------------------------------------------------*/    
void CtiCCServer::stop()
{
    // _dostop = TRUE;

    if ( _checkthr.isValid() )
    {
        _checkthr.requestCancellation();
        _checkthr.join();
    }
}

/*---------------------------------------------------------------------------
    Broadcast
    
    Notifies all objects that are observing this that a new message is 
    available to broadcast.  The mutex lock gives all the observers a 
    chance to broadcast the current message before the next message
    comes in.  Observers must deal with this notification quickly to avoid
    new messages from piling up here.
---------------------------------------------------------------------------*/
void CtiCCServer::Broadcast(CtiMessage* message)
{
    //Only allow one broadcast at a time!
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_broadcastmutex);

    _currentmessage = message;

    /*if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Outgoing Message has class ID of " << message->isA() << endl;
    }*/

    setChanged();
    notifyObservers();
    delete message;
}

/*---------------------------------------------------------------------------
    BroadcastMessage
    
    Returns the CtiCCMessage that is currently being broadcast
---------------------------------------------------------------------------*/
CtiMessage* CtiCCServer::getBroadcastMessage()
{
    return _currentmessage;
}

/*---------------------------------------------------------------------------
    _checkstatus
    
    Periodically checks to see whether the cap control server should be
    shut down and then does so if necessary.
---------------------------------------------------------------------------*/
void CtiCCServer::_checkstatus()
{
    try
    {
        for ( ; ; )
        {
            rwRunnable().serviceCancellation();
            rwRunnable().sleep( 500 );
        }
    }
    catch ( RWxmsg& msg )
    {
        if ( _listener != NULL )
        {
            _listener->stop();
            delete _listener;
            _listener = NULL;

            /*{
                RWMutexLock::LockGuard guard(coutMux);
                cout << RWTime()  << " - CtiCCServer::_checkstatus - " << msg.why() << endl;
            }*/
        }

        _running = FALSE;
        _dostop = FALSE;
    }                                                            
}
    
