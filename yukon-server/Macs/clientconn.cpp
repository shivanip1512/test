/*-----------------------------------------------------------------------------*
*
* File:   clientconn
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/clientconn.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2006/03/17 23:37:55 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*-----------------------------------------------------------------------------
    Filename:  clientconn.cpp

    Programmer:  Aaron Lauinger

    Description: Source file for CtiMCConnection

    Initial Date:  5/12/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "clientconn.h"
#include "ctibase.h"

using std::ostream;
using std::less;
using std::endl;

/*---------------------------------------------------------------------------
    Constructor (should not be use)
---------------------------------------------------------------------------*/
CtiMCConnection::CtiMCConnection() :
_valid(false),
_connection( CtiListenerConnection("") )
{
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiMCConnection::CtiMCConnection( CtiListenerConnection& listenerConn ) :
_valid(true),
_connection( listenerConn )
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiMCConnection::~CtiMCConnection()
{
    close();
}

/*---------------------------------------------------------------------------
    start the connection
---------------------------------------------------------------------------*/
void CtiMCConnection::start()
{
    try
    {
        _connection.start();
    }
    catch (...)
    {
        _valid = FALSE;
    }
}

/*---------------------------------------------------------------------------
    isValid

    Returns TRUE is the connection is valid, FALSE otherwise
---------------------------------------------------------------------------*/
BOOL CtiMCConnection::isValid()
{
    if( _connection.verifyConnection() != NORMAL )
    {
        _valid = FALSE;
    }

    return _valid;
}

/*---------------------------------------------------------------------------
    close

    Closes the connection
---------------------------------------------------------------------------*/
void CtiMCConnection::close()
{
    _connection.close();
}

void CtiMCConnection::write(CtiMessage* msg)
{
    _connection.WriteConnQue( msg );
}

CtiMessage* CtiMCConnection::read()
{
    return _connection.ReadConnQue();
}

CtiMessage* CtiMCConnection::read(unsigned long millis)
{
    return _connection.ReadConnQue( millis );

}
