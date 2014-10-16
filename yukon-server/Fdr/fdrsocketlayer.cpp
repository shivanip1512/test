#include "precompiled.h"

#include "logger.h"
#include "guard.h"
#include "socket_helper.h"

#include "fdrinterface.h"
#include "fdrsocketinterface.h"
#include "fdrclientconnection.h"
#include "fdrserverconnection.h"
#include "fdrsocketconnection.h"
#include "fdrsocketlayer.h"

using std::string;
using std::endl;

/************************************************************************
* Function Name: CtiFDRInterface::getInterfaceName()
*
* Description: returns the name of the Interface.  This set by the
*              implementing class.
*
*************************************************************************
*/


// constructors
CtiFDRSocketLayer::CtiFDRSocketLayer(string & interfaceName,
                                     FDRConnectionType aType,
                                     CtiFDRSocketInterface *aParent):
    iName (interfaceName),
    iParent (aParent),
    iLinkStatusID (0),
    iConnectionType (aType)

{
    iInBoundConnection = NULL;
    iOutBoundConnection = NULL;
    iSemaphore = NULL;

}

CtiFDRSocketLayer::CtiFDRSocketLayer(string & interfaceName,
                                     CtiFDRServerConnection *aInBoundConnection,
                                     FDRConnectionType aType,
                                     CtiFDRSocketInterface *aParent) :
    iName (interfaceName),
    iParent (aParent),
    iLinkStatusID (0),
    iConnectionType (aType)
{
    setInBoundConnection (aInBoundConnection);
    iInBoundConnection->setParent(this);

    iOutBoundConnection = NULL;
    iSemaphore = NULL;

}


// constructor of a single connection using one socket
CtiFDRSocketLayer::CtiFDRSocketLayer(string & interfaceName,
                                     SOCKET aInBound,
                                     SOCKET aOutBound,
                                     FDRConnectionType aType,
                                     CtiFDRSocketInterface *aParent):
    iName (interfaceName),
    iParent (aParent),
    iLinkStatusID (0),
    iConnectionType (aType)

{
    if (aInBound != NULL)
    {
        Cti::SocketAddress addr( Cti::SocketAddress::STORAGE_SIZE );

        getpeername(aInBound, &addr._addr.sa, &addr._addrlen);

        iInBoundConnection = new CtiFDRServerConnection(aInBound, addr, this);
    }

    if (aOutBound != NULL)
    {
        Cti::SocketAddress addr( Cti::SocketAddress::STORAGE_SIZE );

        getpeername(aInBound, &addr._addr.sa, &addr._addrlen);

        iOutBoundConnection = new CtiFDRClientConnection(aOutBound, this);
        iOutBoundConnection->setAddr(addr);
    }
    iSemaphore = NULL;
}


CtiFDRSocketLayer::~CtiFDRSocketLayer( )
{
    // stops all threads in this object
    stop();

    if (iInBoundConnection != NULL)
    {
        delete iInBoundConnection;
        iInBoundConnection = NULL;
    }

    if (iOutBoundConnection != NULL)
    {
        delete iOutBoundConnection;
        iOutBoundConnection = NULL;
    }
}


BOOL CtiFDRSocketLayer::operator==( const CtiFDRSocketLayer &other ) const
{
    return( (
             getName() ==        other.getName()          &&
//             getPortNumber() ==  other.getPortNumber()    &&
             getConnectionType() == other.getConnectionType()
             )
            );
}

long CtiFDRSocketLayer::getLinkStatusID( void ) const
{
    return iLinkStatusID;
}

CtiFDRSocketLayer &  CtiFDRSocketLayer::setLinkStatusID(const long aPointID)
{
    iLinkStatusID = aPointID;
    return *this;
}

CtiFDRServerConnection * CtiFDRSocketLayer::getInBoundConnection ()
{
    return iInBoundConnection;
}

CtiFDRSocketLayer& CtiFDRSocketLayer::setInBoundConnection (CtiFDRServerConnection * aServer)
{
    iInBoundConnection = aServer;
    return *this;
}

CtiFDRClientConnection * CtiFDRSocketLayer::getOutBoundConnection ()
{
    return iOutBoundConnection;
}

CtiFDRSocketLayer& CtiFDRSocketLayer::setOutBoundConnection (CtiFDRClientConnection * aClient)
{
    iOutBoundConnection = aClient;
    return *this;
}


CtiFDRSocketLayer::FDRConnectionType CtiFDRSocketLayer::getConnectionType () const
{
  return iConnectionType;
}

CtiFDRSocketLayer& CtiFDRSocketLayer::setConnectionType (CtiFDRSocketLayer::FDRConnectionType  aType)
{
  iConnectionType = aType;
  return *this;
}

int CtiFDRSocketLayer::getLinkTimeout( void ) const
{
    return iParent->getLinkTimeout();
}

USHORT CtiFDRSocketLayer::getPortNumber () const
{
    return iParent->getPortNumber();
}

string CtiFDRSocketLayer::getIpMask ()
{
    return iParent->getIpMask();
}

void CtiFDRSocketLayer::setIpMask (const string& ipMask)
{
    return iParent->setIpMask(ipMask);
}


USHORT CtiFDRSocketLayer::getConnectPortNumber () const
{
    return iParent->getConnectPortNumber();
}

int CtiFDRSocketLayer::getOutboundSendRate () const
{
    return iParent->getOutboundSendRate();
}

int CtiFDRSocketLayer::getOutboundSendInterval () const
{
    return iParent->getOutboundSendInterval();
}

bool CtiFDRSocketLayer::isRegistered ()
{
    return iParent->isRegistered();
}


int CtiFDRSocketLayer::sendAllPoints ()
{
    return iParent->sendAllPoints();
}

void CtiFDRSocketLayer::sendLinkState (int aState)
{
    if (getLinkStatusID() != 0)
    {
        CtiPointDataMsg     *pData;
        pData = new CtiPointDataMsg(getLinkStatusID(),
                                    aState,
                                    NormalQuality,
                                    StatusPointType);
        iParent->sendMessageToDispatch (pData);
    }
}


HEV & CtiFDRSocketLayer::getConnectionSem ()
{
    return iSemaphore;
}

CtiFDRSocketLayer& CtiFDRSocketLayer::setConnectionSem (HEV aSem)
{
    iSemaphore = aSem;
    return *this;
}

CtiFDRSocketConnection::FDRConnectionStatus CtiFDRSocketLayer::getInBoundConnectionStatus() const
{
    int value;
    CtiFDRSocketConnection::FDRConnectionStatus retVal = CtiFDRSocketConnection::Uninitialized;

    if (iInBoundConnection !=NULL)
    {
        retVal = iInBoundConnection->getConnectionStatus();
    }

    return retVal;
}

CtiFDRSocketLayer& CtiFDRSocketLayer::setInBoundConnectionStatus (CtiFDRSocketConnection::FDRConnectionStatus aStatus)
{
    if (iInBoundConnection !=NULL)
    {
        iInBoundConnection->setConnectionStatus (aStatus);
    }
    return *this;
}

CtiFDRSocketConnection::FDRConnectionStatus CtiFDRSocketLayer::getOutBoundConnectionStatus() const
{
    int value;
    CtiFDRSocketConnection::FDRConnectionStatus retVal = CtiFDRSocketConnection::Uninitialized;

    if (iOutBoundConnection !=NULL)
    {
        retVal = iOutBoundConnection->getConnectionStatus();
    }

    return retVal;
}

CtiFDRSocketLayer& CtiFDRSocketLayer::setOutBoundConnectionStatus (CtiFDRSocketConnection::FDRConnectionStatus aStatus)
{
    if (iOutBoundConnection !=NULL)
    {
        iOutBoundConnection->setConnectionStatus (aStatus);
    }
    return *this;
}


string & CtiFDRSocketLayer::getName(void)
{
    return iName;
}
string  CtiFDRSocketLayer::getName(void) const
{
    return iName;
}

CtiFDRSocketLayer& CtiFDRSocketLayer::setName (string aName)
{
  iName = aName;
  return *this;
}

int CtiFDRSocketLayer::init ()
{
    int retVal = ClientErrors::None;

    iThreadConnectionStatus = rwMakeThreadFunction(*this,
                                          &CtiFDRSocketLayer::threadFunctionConnectionStatus);

    if (iConnectionType == Client_Multiple)
    {
        Cti::AddrInfo pAddrInfo = Cti::makeTcpClientSocketAddress(iName, getPortNumber());
        if( !pAddrInfo )
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Failed to retrieve address info for \"" << iName << "\" port " << getPortNumber() << "(Error: " << pAddrInfo.getError() << ")");
            }

            retVal = ClientErrors::Abnormal;
        }
        else
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Attempting to connect to "<< iName << " at " << pAddrInfo);
            }

            // must be in this order because we need address info from server
            iOutBoundConnection = new CtiFDRClientConnection(Cti::SocketAddress(pAddrInfo->ai_addr, pAddrInfo->ai_addrlen), this);
            retVal              = iOutBoundConnection->init();
        }
    }
    else if (iConnectionType == Server_Single)
    {
        retVal = iInBoundConnection->init();
        retVal = iOutBoundConnection->init();

    }
    else if (iConnectionType == Server_Multiple)
    {
        retVal = iInBoundConnection->init();
    }
    else
    {
        CTILOG_ERROR(dout, getName() <<" connection type not implemented yet ("<< iConnectionType <<")");
    }

    return retVal;
}


int CtiFDRSocketLayer::run ()
{
    iThreadConnectionStatus.start();

    if (iConnectionType == Server_Single)
    {
        iInBoundConnection->run();
        iOutBoundConnection->run();
        sendLinkState (FDR_CONNECTED);
    }
    else if (iConnectionType == Server_Multiple)
    {
        iInBoundConnection->run();
        sendLinkState (FDR_CONNECTED);
    }
    else if (iConnectionType == Client_Multiple)
    {
        iOutBoundConnection->run();
        sendLinkState (FDR_CONNECTED);
    }
    else
    {
        CTILOG_ERROR(dout, getName() <<" connection type not implemented yet ("<< iConnectionType <<")");
    }

    return ClientErrors::None;
}

int CtiFDRSocketLayer::stop ()
{
    SetEvent (iSemaphore);
    iThreadConnectionStatus.requestCancellation();

    if (iInBoundConnection != NULL)
    {
        iInBoundConnection->stop();
    }
    if (iOutBoundConnection != NULL)
    {
        iOutBoundConnection->stop();
    }
    sendLinkState (FDR_NOT_CONNECTED);
    return ClientErrors::None;
}

int CtiFDRSocketLayer::closeAndFailConnection()
{
    int retVal=ClientErrors::None;

    // send a shutdown message

    if (iInBoundConnection != NULL)
    {
        if (iInBoundConnection->getConnection() != NULL)
        {
            iInBoundConnection->closeAndFailConnection();

            CTILOG_INFO(dout, "Closed and failed Inbound connection "<< getName());
        }
    }

    if (iOutBoundConnection != NULL)
    {
        if (iOutBoundConnection->getConnection() != NULL)
        {
            iOutBoundConnection->closeAndFailConnection();

            CTILOG_INFO(dout, "Closed and failed Outbound connection "<< getName());
        }
    }
    return retVal;
}

INT CtiFDRSocketLayer::write (CHAR *aBuffer, int aPriority)
{
    INT retVal = ClientErrors::None;

    if (iOutBoundConnection->getQueueHandle() != NULL)
    {
        // Ship it to the TCP/IP interface thread
        if (WriteQueue (iOutBoundConnection->getQueueHandle(),
                        0,
                        iParent->getMessageSize(aBuffer),
                        aBuffer,
                        aPriority))
        {
            CTILOG_ERROR(dout, "Failed to send data to "<< getName());

            delete []aBuffer;
            retVal = ClientErrors::Abnormal;
        }
    }
    else
    {
        delete []aBuffer;
        retVal = ClientErrors::Abnormal;
    }
    return retVal;
}

void CtiFDRSocketLayer::threadFunctionConnectionStatus( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    ULONG postCount;
    int   loopCnt=1;
    int   failedStatus = 0;
    DWORD semRet;
    CtiTime logTime;
    CtiTime validTime;      // This is the time of the last valid SetEvent.


    try
    {
        if (iParent->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "Initializing Thread for "<< getName());
        }

        iSemaphore = CreateEvent ((LPSECURITY_ATTRIBUTES)NULL,TRUE,false,NULL);
        if (iSemaphore != NULL)
        {
            for ( ; ; )
            {
                pSelf.serviceCancellation( );

                ResetEvent (iSemaphore);

                semRet = WaitForSingleObject(iSemaphore, gConfigParms.getValueAsULong("FDR_ACS_LINK_LOOP_DELAY", 15000L) );

                // returns an error 1 for a timeout, error or otherwise
                if(semRet == WAIT_TIMEOUT)
                {
                    pSelf.serviceCancellation( );

                    /**********************
                    * because we want to be able to cancel this thread,
                    * we are checking the time out more often
                    * still only fail the connection when the set timeout occurs
                    *********************
                    * for 60 seconds
                    ***********************
                    */

                    if ((loopCnt*gConfigParms.getValueAsULong("FDR_ACS_LINK_LOOP_DELAY", 15000L)) < (getLinkTimeout() * 1000))
                    {
                        loopCnt++;
                    }
                    else
                    {
                        loopCnt = 0;
                        logTime =CtiTime().now();

                        CTILOG_WARN(dout, "Link to "<< getName() <<" timed out.  Last message at " << validTime);

                        closeAndFailConnection();
                    }
                }
                else
                {
                    // we were tripped so a timeout is at least 60 seconds away
                    loopCnt = 1;

                    pSelf.serviceCancellation( );

                    /*******************
                    * we've been tripped by something so one of
                    * our connections must have failed
                    ********************
                    */
                    if (getInBoundConnectionStatus() == CtiFDRSocketConnection::Failed ||
                        getOutBoundConnectionStatus() == CtiFDRSocketConnection::Failed)
                    {
                        // this will do cleanup work on connections
                        CTILOG_INFO(dout, "Failing "<< getName() <<".  Last message at " << validTime);

                        logTime =CtiTime().now();
                        closeAndFailConnection();
                    }
                    else
                    {
                        validTime = CtiTime().now();
                        if ((logTime.seconds()+120) <= CtiTime::now().seconds())
                        {
                            CTILOG_INFO(dout, "Valid connection exists to "<< getName() <<" at "<< getInBoundConnection()->getAddr());

                            sendLinkState (FDR_CONNECTED);
                            logTime =CtiTime().now();

                        }
                    }

                }
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unable to create connection semaphore for " << getName() << ", loading interface failed");
        }
    }

    catch ( RWCancellation &)
    {
        closeAndFailConnection();

        CTILOG_INFO(dout, "Thread CANCELLATION for " << getName());

        return;
    }
    // try and catch the thread death
    catch ( ... )
    {
        closeAndFailConnection();

        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Thread for "<< getName() <<" is dead!");
    }
}

int CtiFDRSocketLayer::initializeClientConnection ()
{
    int retVal=ClientErrors::None;

    // create a new connection
    if (iInBoundConnection != NULL)
    {
        /*****************************
        * if we are a single socket connection, in and out are the same socket
        * therefore, we don't want to initialize the outbound socket
        ******************************
        */
        if (iOutBoundConnection == NULL)
        {
            iOutBoundConnection = new CtiFDRClientConnection( iInBoundConnection->getAddr(), this );
            retVal = iOutBoundConnection->init();

            if (retVal)
            {
                delete iOutBoundConnection;
                iOutBoundConnection = NULL;
            }
            else
            {
                // assuming this is ok
                if (!(retVal = iOutBoundConnection->run()))
                {
                    string desc,action;
                    desc = getName() + string ("'s client link has been established");
                    iParent->logEvent (desc,action, true);
                    setLinkStatusID(iParent->getClientLinkStatusID(getName()));
                    sendLinkState (FDR_CONNECTED);
                }

            }
        }
    }
    else
        retVal = ClientErrors::Abnormal;

    return retVal;
}


int CtiFDRSocketLayer::getMessageSize(CHAR *aBuffer)
{
    return iParent->getMessageSize (aBuffer);
}

string CtiFDRSocketLayer::decodeClientName(CHAR * aBuffer)
{
    return iParent->decodeClientName(aBuffer);
}


int CtiFDRSocketLayer::processMessageFromForeignSystem(CHAR *aBuffer)
{
    return iParent->processMessageFromForeignSystem(aBuffer);
}

CHAR *CtiFDRSocketLayer::buildForeignSystemHeartbeatMsg ()
{
    return iParent->buildForeignSystemHeartbeatMsg();
}

ULONG CtiFDRSocketLayer::getDebugLevel()
{
    return iParent->getDebugLevel();
}



