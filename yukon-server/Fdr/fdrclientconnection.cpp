#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <windows.h>
#include "yukon.h"
#include "logger.h"
#include "guard.h"

#include "fdrinterface.h"
#include "fdrsocketlayer.h"
#include "fdrclientconnection.h"
#include "fdrdebuglevel.h"

CtiFDRClientConnection::CtiFDRClientConnection(SOCKADDR_IN aAddr, CtiFDRSocketLayer * aParent)
:    Inherited(aParent,aAddr)
{
    iQueueHandle = NULL;
}

CtiFDRClientConnection::CtiFDRClientConnection(SOCKET aSocket, CtiFDRSocketLayer * aParent)
:    Inherited(aParent)
{
    setConnection (aSocket);

    SOCKADDR_IN addr;
    // address can't be zero
    addr.sin_addr.S_un.S_addr = 0;

    iQueueHandle = NULL;
    setAddr (addr);
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

    // this will be zero only if the client and server are using the same socket
    if (getAddr().sin_addr.S_un.S_addr == 0)
    {
        retVal = NORMAL;
    }
    else
    {
        retVal = initializeConnection (getAddr());
    }

    return retVal;
}

int CtiFDRClientConnection::run () 
{
    iThreadSend.start();
    iThreadHeartbeat.start();
    return NORMAL;
}

int CtiFDRClientConnection::stop () 
{
    closeAndFailConnection();
    iThreadSend.requestCancellation();    
    iThreadHeartbeat.requestCancellation();    
    return NORMAL;
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
    REQUESTDATA queueResult;
    UCHAR priority;
    int retVal = NORMAL;
    int queueReturn;
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
        if (CreateQueue (&iQueueHandle,
                         QUE_PRIORITY))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Unable to allocate space for " << getParent()->getName() << " out queue" << endl;
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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() <<" Initializing FDRClientConnection::threadFunctionSendDataTo for " << getParent()->getName()  << endl;
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
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Connection to dispatch is invalid" <<endl;
                            dout << RWTime() << " Unable to upload requested points to " << getParent()->getName() << endl;
                            dispatchErr=0;
                        }
                    }
                    else if (retFlag == FDR_CLIENT_NOT_REGISTERED)
                    {
                        dispatchErr=0;
                        notRegErr++;
                        if (notRegErr >= 20)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Client for " << getParent()->getName() << " has not registered" <<endl;
                            dout << RWTime() << " Unable to upload requested points " << endl;
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

                    queueReturn = ReadQueue (iQueueHandle,
                                   &queueResult,
                                   &bytesRead,
                                   (PVOID *) &buffer,
                                   0,
                                   DCWW_NOWAIT,
                                   &priority);

                    // only try to send if the connection is available
                    if (getConnectionStatus() ==  CtiFDRSocketConnection::Ok)
                    {
                        if (bytesRead == 0 && queueReturn != ERROR_QUE_EMPTY) 
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error reading from " << getParent()->getName() << " out queue" << queueReturn << endl;
                        }
                        else if (queueReturn == NO_ERROR)
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
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Write failed - client " << getParent()->getName() << endl;
                                }
                                outCount=0;
                            }
                            else
                            {
                                if (outCount >= getParent()->getOutboundSendRate())
                                {
                                    //don't call sleep routine if nothing is set
                                    if (getParent()->getOutboundSendInterval())
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Maximum throughput of " << getParent()->getOutboundSendRate() << " entries per ";
                                            dout << getParent()->getOutboundSendInterval() << " second(s) reached, waiting one interval " << endl;
                                        }

                                        pSelf.sleep (getParent()->getOutboundSendInterval()*1000);
                                    }
                                    outCount =0;
                                }
    //                            CtiLockGuard<CtiLogger> doubt_guard(dout);
    //                            dout << RWTime() << " FIX FIX FIX bytes sent - " << bytesSent << endl;
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unable to open connection semaphore for " << getParent()->getName() << " loading interface failed" << endl;
            }
        }
    }   

    catch ( RWCancellation &cancellationMsg )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "CANCELLATION of FDRClientConnection::threadFunctionSendDataTo for " << getParent()->getName() << endl;
        }
        CloseQueue (iQueueHandle);
    }

    // try and catch the thread death
    catch ( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Fatal Error:  FDRClientConnection::threadFunctionSendDataTo for " << getParent()->getName() << " is dead! " << endl;
        }
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
    RWCString timestamp;
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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() <<" Initializing FDRClientConnection::threadFunctionSendHeartbeat for " << getParent()->getName()  << endl;
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
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error sending heartbeat to "<< getParent()->getName() << " at " <<  RWCString (inet_ntoa(getAddr().sin_addr)) << endl;
                        }
                    }
                    else
                    {
                        if (getParent()->getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Sending heartbeat to  "<< getParent()->getName()<< " at " <<  RWCString (inet_ntoa(getAddr().sin_addr)) << endl;
                        }
                    }
                }
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION of FDRClientConnection::threadFunctionSendHeartbeat for " << getParent()->getName()  << endl;
    }
    // try and catch the thread death
    catch ( ... )
    {
        setConnectionStatus(CtiFDRSocketConnection::Failed);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Fatal Error:  FDRClientConnection::threadFunctionSendHeartbeat for " << getParent()->getName() << " is dead! " << endl;
        }
    }
}

int CtiFDRClientConnection::initializeConnection (SOCKADDR_IN aAddr) 
{
    int retVal = NORMAL;
    SOCKET tmpConnection;
    int                   returnLength;
    LPHOSTENT               hostEntry;

    // do this so we have time to get shut down properly
    CTISleep (2000);

    // create a socket
    tmpConnection = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (tmpConnection == INVALID_SOCKET)
    {
        shutdown(tmpConnection, 2);
        closesocket(tmpConnection);     
        retVal = SOCKET_ERROR;
    }
    else
    {
        BOOL ka=true;
        if (setsockopt(tmpConnection, SOL_SOCKET, SO_REUSEADDR, (char*)&ka, sizeof(BOOL)))
        {
            shutdown(tmpConnection, 2);
            retVal = closesocket(tmpConnection);     
            tmpConnection = NULL;
            retVal = SOCKET_ERROR;
        }
        else
        {
            aAddr.sin_addr.s_addr = aAddr.sin_addr.s_addr;
            aAddr.sin_family = AF_INET;
            aAddr.sin_port = htons(getParent()->getConnectPortNumber());

            retVal = connect (tmpConnection,(struct sockaddr *) &aAddr,sizeof (aAddr));

            if (retVal == SOCKET_ERROR)
            {
                shutdown(tmpConnection, 2);
                closesocket(tmpConnection);     
                tmpConnection = NULL;
            }
            else
            {
                setConnection(tmpConnection);
                retVal = NORMAL;
            }
        }
    }
    return retVal;
}

INT CtiFDRClientConnection::writeSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesWritten)
{
    ULONG    bytesAvailable  = 0;
    ULONG    bytesSent  = 0;
    INT      retVal             = NORMAL;
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

        if (bytesSent == SOCKET_ERROR || bytesSent != length)
        {
            retVal =  SOCKET_ERROR;
        }
        else
        {
            aBytesWritten = bytesSent;
        }

    }
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retVal;
}

