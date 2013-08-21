/*-----------------------------------------------------------------------------
    Filename:  ccclientconn.cpp

    Programmer:  Josh Wolberg

    Description: Source file for CtiCCClientConnection

    Initial Date:  9/04/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "ccclientconn.h"
#include "ccmessage.h"
#include "ccexecutor.h"
#include "ccsubstationbusstore.h"
#include "ctibase.h"
#include "capcontroller.h"
#include "logger.h"
#include "utility.h"
#include "thread_monitor.h"

using namespace std;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCClientConnection::CtiCCClientConnection( CtiListenerConnection &listenerConn ) :
_valid(true),
_connection( listenerConn, & CtiCapController::getInClientMsgQueueHandle() )
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCClientConnection::~CtiCCClientConnection()
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Client Connection closing." << endl;
    }

    try
    {
        close();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Client Connection closed." << endl;
    }
}

/*---------------------------------------------------------------------------
    isValid

    Returns true is the connection is valid, false otherwise
---------------------------------------------------------------------------*/
bool CtiCCClientConnection::isValid()
{
    if( _connection.verifyConnection() != NORMAL )
    {
        _valid = false;
    }

    return _valid;
}

/*---------------------------------------------------------------------------
    close

    Closes the connection
---------------------------------------------------------------------------*/
void CtiCCClientConnection::close()
{
    _valid = false;

    _connection.close();
}

/*---------------------------------------------------------------------------
    write

    Writes a message into the queue which will be sent to the client.
---------------------------------------------------------------------------*/
void CtiCCClientConnection::write( CtiMessage* msg )
{
    _connection.WriteConnQue( msg );
}

/*---------------------------------------------------------------------------
    start

    start the connection
---------------------------------------------------------------------------*/
void CtiCCClientConnection::start()
{
    try
    {
        _connection.start();
    }
    catch (...)
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        _valid = FALSE;
    }
}
