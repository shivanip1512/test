#pragma once

#include "dlldefs.h"
#include "queues.h"
#include "fdrclientserverconnection.h"
#include "fdrinterface.h"
#include "socket_helper.h"

class CtiTime;

class IM_EX_FDRBASE CtiFDRSocketServer : public CtiFDRInterface
{

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDRSocketServer(std::string);

        virtual ~CtiFDRSocketServer();

        // This method is of no use to use, but it has to be defined in our class.
        // Fortunately it is never called via our baseclass.
        virtual int processMessageFromForeignSystem(CHAR *data) { return 0; };

        virtual int processMessageFromForeignSystem(Cti::Fdr::ServerConnection& connection, const char* data, unsigned int size) = 0;

        virtual bool loadTranslationLists(void);
        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool send=false)=0;
        //Force Cleanup in sub classes from this point.
        virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)=0;

        virtual unsigned int getMessageSize(const char* data) = 0;
        virtual unsigned int getHeaderLength()=0;

        virtual BOOL init( void );
        virtual BOOL run( void );
        virtual BOOL stop( void );

        // effective for all classes inheriting from here
        void sendMessageToForeignSys(CtiMessage* aMessage) override;
        void processCommandFromDispatch(CtiCommandMsg* commandMsg);

        virtual bool isClientConnectionValid (void);

        unsigned short  getPortNumber() const;

        void setPortNumber(const unsigned short port);
        int  getPointTimeVariation() const;
        void setPointTimeVariation(const int time);
        int  getTimestampReasonabilityWindow() const;
        void setTimestampReasonabilityWindow(const int window);
        int  getLinkTimeout() const;
        void setLinkTimeout(const int linkTimeout);

        void setSingleListeningPort(bool singleListeningPort);
        bool CtiFDRSocketServer::readConfig();

        typedef std::list<CtiFDRClientServerConnectionSPtr> ConnectionList;

    protected:
        void insertPortToPointsMap(int portId, int pointId);
        void removePortToPointsMap(int portId, int pointId);

        void clearFailedLayers();
        bool createBoundListener(unsigned short listeningPort, Cti::ServerSockets &listeningSockets);

        virtual CtiFDRClientServerConnectionSPtr createNewConnection(SOCKET newConnection) = 0;

        virtual void begineNewPoints() {};
        virtual bool processNewPoint(CtiFDRDestination& pointDestination, bool isSend) {return true;};

        bool sendAllPoints(int portNumber);
        bool sendAllPoints(CtiFDRClientServerConnectionSPtr layer);
        bool sendPoint(CtiFDRPointSPtr point);

        virtual bool forwardPointData(const CtiPointDataMsg& localMsg);
        virtual bool buildForeignSystemHeartbeatMsg(char** buffer,
                                                    unsigned int& bufferSize);
        virtual bool buildForeignSystemMessage(const CtiFDRDestination& destination,
                                               char** buffer,
                                               unsigned int& bufferSize) = 0;

        void threadFunctionConnection(unsigned short listeningPort, int startupDelaySeconds);

        virtual CtiFDRClientServerConnectionSPtr findConnectionForDestination(const CtiFDRDestination destination) const;

        ConnectionList   _connectionList;
        mutable CtiMutex _connectionListMutex;

    private:

        bool loadList(std::string& aDirection,  CtiFDRPointList& aList);
        void threadFunctionSendHeartbeat(void);

        Cti::WorkerThread _threadHeartbeat;
        boost::thread     _threadSingleConnection;

        typedef std::map<int,std::set<int> > PortToPointsMap;
        PortToPointsMap _portToPointsMap;

        unsigned short _portNumber;
        int _pointTimeVariation;
        int _timestampReasonabilityWindow;
        int _linkTimeout;
        bool _enableSendAllPoints;

        bool _singleListeningPort;

        HANDLE _shutdownEvent;

        typedef boost::shared_ptr<Cti::ServerSockets>      SocketsSharedPtr;
        typedef std::map<unsigned short, SocketsSharedPtr> PortSocketsMap;

        PortSocketsMap _socketConnections;
        CtiMutex       _socketMutex;
        bool           _socketShutdown;

        static const CHAR * KEY_ENABLE_SEND_ALL_POINTS;
};

