#include "precompiled.h"
#include "logger.h"
#include "guard.h"

#include "fdrinterface.h"
#include "fdrsocketlayer.h"
#include "fdrclientconnection.h"
#include "fdrdebuglevel.h"
#include "socket_helper.h"
#include "win_helper.h"

using std::string;
using std::endl;

CtiFDRClientConnection::CtiFDRClientConnection(const Cti::SocketAddress& aAddr, CtiFDRSocketLayer * aParent)
:    Inherited(aParent, aAddr)
{
    iQueueHandle = NULL;
}

CtiFDRClientConnection::CtiFDRClientConnection(SOCKET aSocket, CtiFDRSocketLayer * aParent)
:    Inherited(aParent)
{
    setConnection (aSocket);
    iQueueHandle = NULL;
}


CtiFDRClientConnection::~CtiFDRClientConnection( )
{
}

HCTIQUEUE & CtiFDRClientConnection::getQueueHandle()
{
    return iQueueHandle;
}


int CtiFDRClientConnection::init ()
{
    int retVal;

    iThreadSend = rwMakeThreadFunction(*this,
                                            &CtiFDRClientConnection::threadFunctionSendDataTo);

    iThreadHeartbeat = rwMakeThreadFunction(*this,
                                          &CtiFDRClientConnection::threadFunctionSendHeartbeat);

    // this will be false only if the client and server are using the same socket
    if( !getAddr() )
    {
        retVal = ClientErrors::None;
    }
    else
    {
        retVal = initializeConnection( getAddr() );
    }

    return retVal;
}

int CtiFDRClientConnection::run ()
{
    iThreadSend.start();
    iThreadHeartbeat.start();
    return ClientErrors::None;
}

int CtiFDRClientConnection::stop ()
{
    closeAndFailConnection();
    iThreadSend.requestCancellation();
    iThreadHeartbeat.requestCancellation();
    return ClientErrors::None;
}


/**************************************************************************
* Function Name: CtiFDR_ACS::sendDataToACSThreadFunction( void )
*
* Description: thread accesses the socket to send data
*
***************************************************************************
*/
void CtiFDRClientConnection::threadFunctionSendDataTo( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    CHAR *buffer=NULL;
    ULONG bytesSent,bytesRead;
    UCHAR priority;
    int retVal = ClientErrors::None;
    int connectionBadCount=0;
    int outCount=0;

    try
    {
        // loop until parent exists
        while (getParent() == NULL)
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (250);
        }

        // Create the queue for handling incoming messages
        if (CreateQueue (&iQueueHandle))
        {
            CTILOG_ERROR(dout, "Unable to allocate space for "<< getParent()->getName() <<" out queue");
        }
        else
        {
            // loop until this is created
            while (getParent()->getConnectionSem() == NULL)
            {
                pSelf.serviceCancellation( );
                pSelf.sleep (250);
            }

            if (getParent()->getConnectionSem() != NULL)
            {
                // we're ready to fly now
                setConnectionStatus(CtiFDRSocketConnection::Ok);

                if (getParent()->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Initializing threadFunctionSendDataTo for "<< getParent()->getName());
                }

                // this will all non-control points to the foreign system
                int retFlag = getParent()->sendAllPoints();
                int dispatchErr=0;
                int notRegErr=0;

                while (retFlag)
                {
                    // loop until we get cancelled or this is successful
                    pSelf.serviceCancellation( );

                    if (retFlag == FDR_NOT_CONNECTED_TO_DISPATCH)
                    {
                        dispatchErr++;
                        if (dispatchErr >= 20)
                        {
                            CTILOG_ERROR(dout, "Connection to dispatch is invalid - Unable to upload requested points to "<< getParent()->getName());
                            dispatchErr=0;
                        }
                    }
                    else if (retFlag == FDR_CLIENT_NOT_REGISTERED)
                    {
                        dispatchErr=0;
                        notRegErr++;
                        if (notRegErr >= 20)
                        {
                            CTILOG_ERROR(dout, "Client for "<< getParent()->getName() <<" has not registered - Unable to upload requested points");
                            notRegErr=0;
                        }
                    }
                    retFlag = getParent()->sendAllPoints();
                }


                // Now sit and wait for stuff to come in
                for (;;)
                {
                    pSelf.serviceCancellation( );

                    bytesRead = 0;

                    const int queueReturn = ReadFrontElement(iQueueHandle, &bytesRead, (PVOID *) &buffer, DCWW_NOWAIT, &priority);

                    // only try to send if the connection is available
                    if (getConnectionStatus() ==  CtiFDRSocketConnection::Ok)
                    {
                        if (bytesRead == 0 && queueReturn != ERROR_QUE_EMPTY)
                        {
                            CTILOG_ERROR(dout, "Could not read "<< getParent()->getName() <<" out queue ("<< queueReturn <<")");
                        }
                        else if (queueReturn == ClientErrors::None)
                        {
                            pSelf.serviceCancellation( );
                            retVal = writeSocket(buffer, getParent()->getMessageSize(buffer), bytesSent);

                            // reset this every time through
                            connectionBadCount = 0;
                            outCount++;

                            // this where we re-initialize if needed
                            if (retVal == SOCKET_ERROR)
                            {
                                // closes and marks as failed
                                closeAndFailConnection();

                                CTILOG_ERROR(dout, "writeSocket() failed - client "<< getParent()->getName());

                                outCount=0;
                            }
                            else
                            {
                                if (outCount >= getParent()->getOutboundSendRate())
                                {
                                    //don't call sleep routine if nothing is set
                                    if (getParent()->getOutboundSendInterval())
                                    {
                                        CTILOG_WARN(dout, "Maximum throughput of " << getParent()->getOutboundSendRate() <<" entries per "<<
                                                getParent()->getOutboundSendInterval() << " second(s) reached, waiting one interval");

                                        pSelf.sleep (getParent()->getOutboundSendInterval()*1000);
                                    }
                                    outCount =0;
                                }
                            }

                            // do this no matter what
//                            SetEvent (getParent()->getConnectionSem());
                        }
                        else
                        {
                            // nothing there, sleep and set the outcount
                            outCount=0;
                            pSelf.sleep (50);
                        }
                    }
                    else
                    {
                        connectionBadCount++;
                        if (connectionBadCount >= 5)
                        {
                            closeAndFailConnection();
                            connectionBadCount = 0;
                        }
                        else
                        {
                            pSelf.sleep (1000);
                        }
                    }
                    // free the memory of the stuff on the queue
                    delete []buffer;
                    buffer = NULL;
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Unable to open connection semaphore for "<< getParent()->getName() <<" loading interface failed");
            }
        }
    }

    catch ( RWCancellation & )
    {
        CTILOG_INFO(dout, "CANCELLATION of threadFunctionSendDataTo for "<< getParent()->getName());
        CloseQueue (iQueueHandle);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "threadFunctionSendDataTo for "<< getParent()->getName() <<" is dead!");
        CloseQueue (iQueueHandle);
    }

    return;
}

/**************************************************************************
* Function Name: CtiFDR_ACS::sendHeartbeatToACSThreadFunction( void )
*
* Description: thread that sends NULL messages to ACS
*
***************************************************************************
*/
void CtiFDRClientConnection::threadFunctionSendHeartbeat( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    string timestamp;
    CHAR *heartbeat=NULL;

    try
    {
        // loop until parent exists
        while (getParent() == NULL || iQueueHandle == NULL)
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (500);
        }

        if (getParent()->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "Initializing threadFunctionSendHeartbeat for "<< getParent()->getName());
        }

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep(10000);

            // check and make sure we're connected
            if (getConnectionStatus() ==  CtiFDRSocketConnection::Ok)
            {
                //Allocate the memory for message to whomever
                heartbeat = getParent()->buildForeignSystemHeartbeatMsg ();

                if (heartbeat != NULL)
                {
                    // Ship it to the TCP/IP interface thread
                    if (WriteQueue (iQueueHandle,
                                    0,
                                    getParent()->getMessageSize(heartbeat),
                                    heartbeat,
                                    MAXPRIORITY))
                    {
                        delete []heartbeat;
                        heartbeat = NULL;

                        if (getParent()->getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "Could not send heartbeat to "<< getParent()->getName() <<" at "<<  getAddr());
                        }
                    }
                    else
                    {
                        if (getParent()->getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "Sent heartbeat to "<< getParent()->getName() <<" at "<<  getAddr());
                        }
                    }
                }
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CTILOG_INFO(dout, "CANCELLATION of threadFunctionSendHeartbeat for "<< getParent()->getName());
    }
    // try and catch the thread death
    catch ( ... )
    {
        setConnectionStatus(CtiFDRSocketConnection::Failed);

        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "threadFunctionSendHeartbeat for "<< getParent()->getName() <<" is dead!");
    }
}

int CtiFDRClientConnection::initializeConnection( const Cti::SocketAddress& aAddr )
{
    // do this so we have time to get shut down properly
    CTISleep (2000);

    // create a socket
    SOCKET tmpConnection = socket(aAddr._addr.sa.sa_family, SOCK_STREAM, IPPROTO_TCP);
    if( tmpConnection == INVALID_SOCKET )
    {
        return SOCKET_ERROR;
    }

    BOOL ka=true;
    if( setsockopt(tmpConnection, SOL_SOCKET, SO_REUSEADDR, (char*)&ka, sizeof(BOOL) ))
    {
        shutdown(tmpConnection, SD_BOTH);
        closesocket(tmpConnection);
        return SOCKET_ERROR;
    }

    const string hostIp = getParent()->getIpMask();
    if( hostIp != "" )
    {
        Cti::AddrInfo ai = Cti::makeTcpClientSocketAddress(hostIp, getParent()->getConnectPortNumber());
        if( !ai )
        {
            CTILOG_ERROR(dout, "Failed to resolve host IP: "<< hostIp <<" (Error: "<< ai.getError() <<")");

            shutdown(tmpConnection, SD_BOTH);
            closesocket(tmpConnection);
            return SOCKET_ERROR;
        }

        if( bind( tmpConnection, ai->ai_addr, ai->ai_addrlen ) == SOCKET_ERROR )
        {
            const int errorCode = WSAGetLastError();
            CTILOG_ERROR(dout, "Failed to bind to: "<< ai <<" (Error: "<< ai.getError() <<")");

            shutdown(tmpConnection, SD_BOTH);
            closesocket(tmpConnection);
            return SOCKET_ERROR;
        }

        CTILOG_INFO(dout, "Successful bind on the return connection to "<< ai);
    }

    if( connect( tmpConnection, &aAddr._addr.sa, aAddr._addrlen ) == SOCKET_ERROR )
    {
        shutdown(tmpConnection, SD_BOTH);
        closesocket(tmpConnection);
        return SOCKET_ERROR;
    }

    setConnection(tmpConnection);

    return ClientErrors::None;
}

INT CtiFDRClientConnection::writeSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesWritten)
{
    ULONG    bytesAvailable  = 0;
    ULONG    bytesSent  = 0;
    INT      retVal             = ClientErrors::None;
    ULONG    totalByteCnt    = 0;

    aBytesWritten = 0;

    try
    {
        // send the data
        while((bytesSent = send(getConnection(), aBuffer, length, 0)) == SOCKET_ERROR &&
          WSAGetLastError() == WSAEWOULDBLOCK)
        {
            rwRunnable().yield();
        }

        if (bytesSent == SOCKET_ERROR)
        {
            const DWORD error = WSAGetLastError();
            CTILOG_DEBUG(dout, "Socket send() failed with error code "<< error <<" / "<< Cti::getSystemErrorMessage(error));

            retVal =  SOCKET_ERROR;
        }
        else if( bytesSent != length)
        {
            const DWORD error = WSAGetLastError();
            CTILOG_DEBUG(dout, "Socket send() wrote "<< bytesSent <<" bytes, intended to write "<< length);

            retVal = SOCKET_ERROR;
        }
        else
        {
            aBytesWritten = bytesSent;
        }

    }
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout)
    }

    return retVal;
}

