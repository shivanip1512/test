#include "yukon.h"

#include <windows.h>
#include "logger.h"
#include "guard.h"

#include "fdrinterface.h"
#include "fdrsocketlayer.h"
#include "fdrserverconnection.h"
#include "fdrdebuglevel.h"

/*
CtiFDRServerConnection::CtiFDRServerConnection(CtiFDRSocketLayer * aParent)
:    Inherited(aParent)

{
}
*/
CtiFDRServerConnection::CtiFDRServerConnection(SOCKET aConnection, SOCKADDR_IN aAddr, CtiFDRSocketLayer * aParent)
:    Inherited(aParent)

{
    setConnection (aConnection);
    setAddr (aAddr);
}


CtiFDRServerConnection::~CtiFDRServerConnection( )
{   
} 

/*
int CtiFDRServerConnection::init (int aPortNumber) 
{
    iThreadReceive = rwMakeThreadFunction(*this, 
                                          &CtiFDRServerConnection::threadFunctionGetDataFrom);
//    initializeConnection(aPortNumber);

   return NORMAL;
}

int CtiFDRServerConnection::init () 
{
    iThreadReceive = rwMakeThreadFunction(*this, 
                                          &CtiFDRServerConnection::threadFunctionGetDataFrom);
    if (getParent() == NULL)
    {
//        initializeConnection(0);
    }
    else
    {
//        initializeConnection(getParent()->getPortNumber());
    }

   return NORMAL;
}

*/
int CtiFDRServerConnection::init () 
{
    iThreadReceive = rwMakeThreadFunction(*this, 
                                          &CtiFDRServerConnection::threadFunctionGetDataFrom);
   return NORMAL;
}

int CtiFDRServerConnection::run () 
{
    iThreadReceive.start();
    return NORMAL;
}

int CtiFDRServerConnection::stop () 
{
    closeAndFailConnection();
    iThreadReceive.requestCancellation();    
    return NORMAL;
}


void CtiFDRServerConnection::threadFunctionGetDataFrom( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    CHAR            data[8172];
    INT retVal=0;        
    ULONG   bytesRead=0,totalMsgSize=0;
    int connectionBadCount=0;

    try
    {
        // don't do anything until the parent exists
        while (getParent() == NULL)
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);  
        }

        // loop until this is created
        while (getParent()->getConnectionSem() == NULL)
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);  
        }

        // must be available
        if (getParent()->getConnectionSem() != NULL)
        {
            // we're ready to receive data
            setConnectionStatus (CtiFDRSocketConnection::Ok);


            if (getParent()->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() <<" Initializing FDRServerConnection::threadFunctionGetDataFrom for " << getParent()->getName() << endl;
            }

            for ( ; ; )
            {
                pSelf.serviceCancellation( );

                // if the connection is failed, don't bother trying
                if (getConnectionStatus() ==  CtiFDRSocketConnection::Ok)
                {
                    memset (&data, '\0',8172);
                    // attempt to find out what type of message we're dealing with
                    retVal = readSocket((CHAR*)&data, 4, bytesRead);

                    // reset this every time through
                    connectionBadCount = 0;

                    // this where we re-initialize if needed
                    if (retVal == SOCKET_ERROR)
                    {
                        // closes and marks as failed
                        closeAndFailConnection();
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Read failed - client " << getParent()->getName() << endl;
                        }
                    }
                    else
                    {
                        // figure out how many more bytes we now need
                        totalMsgSize = getParent()->getMessageSize (data);

                        // make sure we have a number (greater than four, we already read 4)
                        if (totalMsgSize > 4)
                        {
                            retVal = readSocket((CHAR*)&data[4], totalMsgSize - 4, bytesRead);

                            // this where we re-initialize if needed
                            if (retVal == SOCKET_ERROR)
                            {
                                // closes and marks as failed
                                closeAndFailConnection();
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Read failed - client " << getParent()->getName() << endl;
                                }
                            }
                            else
                            {
                                /******************
                                * we may not know who connected to us so establish
                                * the return connection if the outboundconnection was
                                * invalid up to this point
                                *******************
                                */

                                if (getParent()->getName() == RWCString())
                                {
                                    getParent()->setName (getParent()->decodeClientName(data));

                                    /********************
                                    * check here if the client by name already existed
                                    *
                                    * NOTE:  when someone connects, we check for their IP in 
                                    * our list.  This works fine as long as there aren't 2 network
                                    * cards and therefore possibly 2 ips that may connect back
                                    *********************
                                    */
                                    // if we fail to connection back, fail the whole thing
                                    if (getParent()->initializeClientConnection())
                                    {
                                        closeAndFailConnection();
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Return client connection to " << RWCString (inet_ntoa(getAddr().sin_addr)) << " failed" << endl;
                                        }
                                    }
                                    else
                                    {
                                        getParent()->processMessageFromForeignSystem (data);
                                    }
                                }
                                else
                                {
                                   getParent()->processMessageFromForeignSystem (data);
                                }
                            }
                        }
                        else if(totalMsgSize == 0)
                        {
                            // closes and marks as failed
                            closeAndFailConnection();
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Read failed - client " << getParent()->getName() << endl;
                            }
                        }
                    }

                    // only make this valid if parent has registered
                    if (getParent()->isRegistered())
                        SetEvent (getParent()->getConnectionSem());
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
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Unable to open semaphore in server thread for " << getParent()->getName() << " loading interface failed" << endl;
        }

    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION of FDRServerConnection::threadFunctionGetDataFrom for " << getParent()->getName() << endl;
        return;
    }
    // try and catch the thread death
    catch ( ... )
    {
        setConnectionStatus (CtiFDRSocketConnection::Failed);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Fatal Error:  FDRServerConnection::threadFunctionGetDataFrom for " << getParent()->getName() << " is dead! " << endl;
        }
    }
}


INT CtiFDRServerConnection::readSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesRead)
{
    ULONG    bytesAvailable  = 0;
    LONG    bytesReceived  = 0;
    INT      retVal             = NORMAL;
    CHAR     *bPtr           = aBuffer;
    ULONG    totalByteCnt    = 0;

    try
    {
        // initialize
        aBytesRead = 0;

        // loop until something else is in the socket
        while (getConnectionStatus() ==  CtiFDRSocketConnection::Ok && bytesAvailable == 0)
        {
            // Find out if we have any bytes
            ioctlsocket(getConnection(), FIONREAD, &bytesAvailable);
            if (bytesAvailable == 0)
            {
                Sleep (50L);
            }
        }

        // see if we bailed because the socket failed		
        if (getConnectionStatus() ==  CtiFDRSocketConnection::Failed)
        {
            retVal = SOCKET_ERROR;
        }
        else
        {
            // we have data, try and get as much as we need
            do
            {
                // read out of buffer until we have everything
                if ((bytesReceived = recv (getConnection(),
                                           (bPtr + totalByteCnt),
                                           length-totalByteCnt,
                                           0)) <= 0)
                {
                    if (WSAGetLastError() == WSAEWOULDBLOCK)
                    {
                        Sleep (50L);
                        continue;
                    }
                    else
                    {
                        // problem with the receive
                        retVal = SOCKET_ERROR;
                        break;
                    }
                }

                // add to our total count
                totalByteCnt += bytesReceived;
                aBytesRead =  totalByteCnt;

            } while (totalByteCnt < length);
        }

    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Socket read in FDR server connection failed, re-initializing connection" <<endl;
    }

    return retVal;
}

