/*-----------------------------------------------------------------------------
    Filename:  capcontrol.cpp
                    
    Programmer:  Josh Wolberg
    
    Description:  Source file for CtiCCServer.
        
    Initial Date:  8/15/2000
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/

#include "capcontrol.h"
#include "ctibase.h"
#include "logger.h"
#include "configparms.h"

extern BOOL _CAP_DEBUG;

//The singleton instance of the server                                       
CtiCCServer* CtiCCServer::_instance = NULL;

//The default port where the server will listen for client connections
int CtiCCServer::_defaultport = CAPCONTROLNEXUS;

/*------------------------------------------------------------------------
    Instance
    
    Returns a pointer to the singleton instance of the server.
---------------------------------------------------------------------------*/
CtiCCServer* CtiCCServer::Instance()
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
    char temp[80];

    HINSTANCE hLib = LoadLibrary("cparms.dll");

    if (hLib)
    {
        CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

        bool trouble = FALSE;

        if ( (*fpGetAsString)("CAP_CONTROL_PORT", temp, 80) )
        {
            if( _CAP_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "CAP_CONTROL_PORT:  " << temp << endl;
            }

            _defaultport = atoi(temp);
        }
        else
            trouble = TRUE;

        
        if ( trouble == TRUE )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - " << "Unable to find cap control port config value in the configuration file!" << endl;
        }

        FreeLibrary(hLib);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Unable to load cparms.dll" << endl;
    }

    if ( !_running && !_dostop )
    {
        _running = TRUE;

        RWThreadFunction func = rwMakeThreadFunction( *this, &CtiCCServer::_checkstatus );
        _checkthr = func;

        func.start();

        if ( _listener == NULL )
            _listener = new CtiCCClientListener( _defaultport );

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

    /*if( _CAP_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - Outgoing Message has class ID of " << message->isA() << endl;
    }*/

    setChanged();
    notifyObservers();
    delete message;
}

/*---------------------------------------------------------------------------
    BroadcastMessage
    
    Returns the CtiCCMessage that is currently being broadcast
---------------------------------------------------------------------------*/
CtiMessage* CtiCCServer::BroadcastMessage()
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
    
