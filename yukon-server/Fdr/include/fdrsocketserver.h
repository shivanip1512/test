#pragma once

#include "dlldefs.h"
#include "queues.h"
#include "fdrclientserverconnection.h"
#include "fdrinterface.h"

class CtiTime;

class IM_EX_FDRBASE CtiFDRSocketServer : public CtiFDRInterface
{

    public:
        // constructors and destructors
        CtiFDRSocketServer(std::string &);

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
        virtual unsigned long getHeaderBytes(const char* data, unsigned int size)= 0;
        virtual unsigned int getMagicInitialMsgSize()=0;

        virtual BOOL init( void );
        virtual BOOL run( void );
        virtual BOOL stop( void );

        // effective for all classes inheriting from here
        bool sendMessageToForeignSys(CtiMessage* aMessage);
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

        typedef std::list<CtiFDRClientServerConnectionSPtr> ConnectionList;

    protected:
        void insertPortToPointsMap(int portId, int pointId);
        void removePortToPointsMap(int portId, int pointId);

        void clearFailedLayers();
        SOCKET createBoundListener(unsigned short listeningPort);

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

        ConnectionList  _connectionList;

        CtiMutex _socketMutex;

        typedef std::map<unsigned short, SOCKET*> PortSocketMap;
        typedef PortSocketMap::iterator PortSocketMap_itr;

        PortSocketMap _socketConnections;
        mutable CtiMutex _connectionListMutex;
    private:

        bool loadList(std::string& aDirection,  CtiFDRPointList& aList);
        void threadFunctionSendHeartbeat(void);

        RWThreadFunction _threadHeartbeat;
        RWThreadFunction _threadSingleConnection;

        typedef std::map<int,std::set<int> > PortToPointsMap;
        PortToPointsMap _portToPointsMap;

        unsigned short _portNumber;
        int _pointTimeVariation;
        int _timestampReasonabilityWindow;
        int _linkTimeout;

        bool _singleListeningPort;

        HANDLE _shutdownEvent;
};

