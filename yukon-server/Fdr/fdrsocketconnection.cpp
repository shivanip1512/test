#include "precompiled.h"

#include "logger.h"
#include "guard.h"
#include "fdrsocketlayer.h"
#include "fdrsocketconnection.h"

/************************************************************************
* Function Name: CtiFDRInterface::getInterfaceName()
*
* Description: returns the name of the Interface.  This set by the
*              implementing class.
*
*************************************************************************
*/

// constructors
CtiFDRSocketConnection::CtiFDRSocketConnection(CtiFDRSocketLayer * aParent)
:    iParent (aParent),
     iConnectionStatus (Uninitialized)
{
    iConnection = NULL;
}

CtiFDRSocketConnection::CtiFDRSocketConnection(CtiFDRSocketLayer * aParent, const Cti::SocketAddress& aAddr )
:    iParent (aParent),
     iConnectionStatus (Uninitialized),
     iAddr (aAddr)
{
    iConnection = NULL;
}

CtiFDRSocketConnection::~CtiFDRSocketConnection( )
{
    closeAndFailConnection();
}

CtiFDRSocketLayer * CtiFDRSocketConnection::getParent ()
{
    return iParent;
}

CtiFDRSocketConnection& CtiFDRSocketConnection::setParent (CtiFDRSocketLayer * aParent)
{
    iParent = aParent;
    return *this;
}

const Cti::SocketAddress& CtiFDRSocketConnection::getAddr() const
{
    return iAddr;
}

CtiFDRSocketConnection& CtiFDRSocketConnection::setAddr(const Cti::SocketAddress& aAddr)
{
    iAddr = aAddr;
    return *this;
}

SOCKET CtiFDRSocketConnection::getConnection () const
{
    return iConnection;
}

SOCKET &CtiFDRSocketConnection::getConnection ()
{
    return iConnection;
}

CtiFDRSocketConnection& CtiFDRSocketConnection::setConnection (SOCKET aSocket)
{
    iConnection = aSocket;
    return *this;
}

CtiFDRSocketConnection::FDRConnectionStatus CtiFDRSocketConnection::getConnectionStatus () const
{
    return iConnectionStatus;
}

CtiFDRSocketConnection& CtiFDRSocketConnection::setConnectionStatus (CtiFDRSocketConnection::FDRConnectionStatus aFlag)
{
    iConnectionStatus = aFlag;
    return *this;
}

int CtiFDRSocketConnection::closeAndFailConnection ()
{
    int retVal = 0;

    setConnectionStatus (Failed);

    if (iConnection != NULL)
    {
        shutdown(iConnection, SD_BOTH);
        retVal = closesocket(iConnection);
        iConnection = NULL;
    }

    return retVal;
}

