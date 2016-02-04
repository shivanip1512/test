#include "precompiled.h"
#include "clientconn.h"


/*---------------------------------------------------------------------------
    Constructor (should not be use)
---------------------------------------------------------------------------*/
CtiMCConnection::CtiMCConnection()
    :   _valid(false),
        _connection( CtiListenerConnection("") )
{
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiMCConnection::CtiMCConnection( CtiListenerConnection& listenerConn, CtiConnection::Que_t *inQ )
    :   _valid(true),
        _connection( listenerConn, inQ )
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiMCConnection::~CtiMCConnection()
{
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
bool CtiMCConnection::isValid()
{
    if( ! _connection.isConnectionUsable() )
    {
        _valid = false;
    }

    return _valid;
}

/*---------------------------------------------------------------------------
     Write a message to send to the outgoing connection queue
---------------------------------------------------------------------------*/
void CtiMCConnection::write(CtiMessage* msg)
{
    _connection.WriteConnQue(msg, CALLSITE);
}

/*---------------------------------------------------------------------------
     Returns true if the server connection pointer matches
---------------------------------------------------------------------------*/
bool CtiMCConnection::hasConnection(const Cti::ConnectionHandle connectionHandle) const
{
    return _connection.getConnectionId() == connectionHandle.getConnectionId();
}
