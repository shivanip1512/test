/*-----------------------------------------------------------------------------
    Filename:  lmserver.cpp
                    
    Programmer:  Josh Wolberg
    
    Description:  Source file for CtiLMServer.
        
    Initial Date:  2/12/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "lmserver.h"
#include "ctibase.h"
#include "logger.h"
#include "configparms.h"

extern ULONG _LM_DEBUG;

//The singleton instance of the server                                       
CtiLMServer* CtiLMServer::_instance = NULL;                                                                          

//The default port where the server will listen for client connections
int CtiLMServer::_defaultport = 1920;

/*------------------------------------------------------------------------
    getInstance
    
    Returns a pointer to the singleton instance of the server.
---------------------------------------------------------------------------*/
CtiLMServer* CtiLMServer::getInstance()
{
    if ( _instance == NULL )
        _instance = new CtiLMServer();

    return _instance;
}

/*---------------------------------------------------------------------------
    start
    
    Starts up the server
---------------------------------------------------------------------------*/
void CtiLMServer::start()
{
    RWCString str;
    char var[128];

    strcpy(var, "LOAD_MANAGEMENT_PORT");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        _defaultport = atoi(str);
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    /*char temp[80];

    HINSTANCE hLib = LoadLibrary("cparms.dll");

    if (hLib)
    {
        CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

        bool trouble = FALSE;

        if ( (*fpGetAsString)("LOAD_MANAGEMENT_PORT", temp, 80) )
        {
            if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - LOAD_MANAGEMENT_PORT:  " << temp << endl;
            }

            _defaultport = atoi(temp);
        }
        else
            trouble = TRUE;

        
        if ( trouble == TRUE )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to find load managemet port config value in the configuration file!" << endl;
        }

        FreeLibrary(hLib);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unable to load cparms.dll" << endl;
    }*/

    if ( !_running && !_dostop )
    {
        _running = TRUE;

        RWThreadFunction func = rwMakeThreadFunction( *this, &CtiLMServer::_checkstatus );
        _checkthr = func;

        func.start();

        if ( _listener == NULL )
            _listener = new CtiLMClientListener( _defaultport );

        _listener->start();
    }
}

/*---------------------------------------------------------------------------
    stop
    
    Stops the server
---------------------------------------------------------------------------*/    
void CtiLMServer::stop()
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
void CtiLMServer::Broadcast(CtiMessage* message)
{
    //Only allow one broadcast at a time!
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_broadcastmutex);

    if( _currentmessage != NULL )
    {
        delete _currentmessage;
        _currentmessage = NULL;
    }
    _currentmessage = message;

    if( _LM_DEBUG & LM_DEBUG_CLIENT )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Outgoing Message has class ID of " << message->isA() << endl;
    }

    setChanged();
    notifyObservers();
}

/*---------------------------------------------------------------------------
    BroadcastMessage
    
    Returns the CtiMessage that is currently being broadcast
---------------------------------------------------------------------------*/
CtiMessage* CtiLMServer::BroadcastMessage()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_broadcastmutex);

    return _currentmessage;
}

/*---------------------------------------------------------------------------
    _checkstatus
    
    Periodically checks to see whether the load management server should be
    shut down and then does so if necessary.
---------------------------------------------------------------------------*/
void CtiLMServer::_checkstatus()
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
                cout << RWTime()  << " - CtiLMServer::_checkstatus - " << msg.why() << endl;
            }*/
        }

        _running = FALSE;
        _dostop = FALSE;
    }                                                            
}
    
