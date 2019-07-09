#pragma once

#include "dlldefs.h"
#include "queues.h"
#include "fdrsocketconnection.h"
#include "worker_thread.h"

class CtiFDRInterface;
class CtiFDRServerConnection;
class CtiFDRClientConnection;
class CtiFDRSocketInterface;
class CtiMessage;

class RWThreadFunction;

class IM_EX_FDRBASE CtiFDRSocketLayer
{
    public:
        DEBUG_INSTRUMENTATION;

        typedef enum {
             Server_Single=0,
             Server_Multiple,
             Client_Multiple
        } FDRConnectionType;

        CtiFDRSocketLayer::CtiFDRSocketLayer(const std::string & interfaceName,
                                             FDRConnectionType aType,
                                             CtiFDRSocketInterface *aParent);
        CtiFDRSocketLayer::CtiFDRSocketLayer(const std::string & interfaceName,
                                             CtiFDRServerConnection *aInBoundConnection,
                                             FDRConnectionType aType,
                                             CtiFDRSocketInterface *aParent);
        CtiFDRSocketLayer::CtiFDRSocketLayer(const std::string & interfaceName,
                                             SOCKET aInBound,
                                             SOCKET aOutBound,
                                             FDRConnectionType aType,
                                             CtiFDRSocketInterface *aParent);
        ~CtiFDRSocketLayer();

        BOOL operator==( const CtiFDRSocketLayer &other ) const;

        USHORT getPortNumber () const;

        std::string getIpMask();
        void setIpMask(const std::string& ipMask);

        USHORT getConnectPortNumber () const;
        HEV & getConnectionSem ();
        CtiFDRSocketLayer& setConnectionSem (HEV aSem);

        std::string       &   getName(void);
        std::string        getName(void) const;
        CtiFDRSocketLayer& setName (std::string aName);

        CtiFDRSocketConnection::FDRConnectionStatus getInBoundConnectionStatus() const;
        CtiFDRSocketLayer& setInBoundConnectionStatus (CtiFDRSocketConnection::FDRConnectionStatus aStatus);

        CtiFDRSocketConnection::FDRConnectionStatus getOutBoundConnectionStatus() const;
        CtiFDRSocketLayer& setOutBoundConnectionStatus (CtiFDRSocketConnection::FDRConnectionStatus aStatus);

        FDRConnectionType         getConnectionType () const;
        CtiFDRSocketLayer& setConnectionType (FDRConnectionType aType);

        CtiFDRServerConnection * getInBoundConnection ();
        CtiFDRSocketLayer& setInBoundConnection (CtiFDRServerConnection *aServer);

        CtiFDRClientConnection * getOutBoundConnection ();
        CtiFDRSocketLayer& setOutBoundConnection (CtiFDRClientConnection *aClient);

        int getLinkTimeout () const;

        // convience functions used to get from layer to interface
        int getMessageSize(CHAR *data);
        std::string decodeClientName(CHAR *data);
        int processMessageFromForeignSystem (CHAR *data);
        CHAR *buildForeignSystemHeartbeatMsg (void);
        ULONG getDebugLevel(void);

        int initializeClientConnection (void);

        int closeAndFailConnection (void);
        INT write (CHAR *aBuffer, int aPriority = (MAXPRIORITY-1));
        int sendAllPoints (void);
        void sendLinkState (int aState);

        int  getOutboundSendRate () const;
        int  getOutboundSendInterval () const;
        bool isRegistered();

        long                getLinkStatusID( void ) const;
        CtiFDRSocketLayer &  setLinkStatusID(const long aPointID);

        int init ();
        int run  ();
        int stop ();

    protected:

        Cti::WorkerThread   iThreadConnectionStatus;
        void threadFunctionConnectionStatus( void );

    private:
        std::string            iName;
        CtiFDRSocketInterface  *iParent;

        CtiFDRServerConnection  *iInBoundConnection;
        CtiFDRClientConnection  *iOutBoundConnection;

        FDRConnectionType       iConnectionType;
        HEV                     iSemaphore;
        long                    iLinkStatusID;
};
