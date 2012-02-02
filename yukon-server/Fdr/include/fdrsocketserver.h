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

        virtual int processMessageFromForeignSystem(
          Cti::Fdr::ServerConnection& connection, const char* data, unsigned int size) = 0;

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
        virtual int readConfig( void )=0;

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

        typedef std::list<CtiFDRClientServerConnection*> ConnectionList;

    protected:
        void clearFailedLayers();
        SOCKET createBoundListener(unsigned short listeningPort);

        virtual CtiFDRClientServerConnection* createNewConnection(SOCKET newConnection) = 0;

        virtual void begineNewPoints() {};
        virtual bool processNewPoint(CtiFDRDestination& pointDestination, bool isSend) {return true;};

        bool sendAllPoints(CtiFDRClientServerConnection* layer);
        virtual bool forwardPointData(const CtiPointDataMsg& localMsg);
        virtual bool buildForeignSystemHeartbeatMsg(char** buffer,
                                                    unsigned int& bufferSize);
        virtual bool buildForeignSystemMessage(const CtiFDRDestination& destination,
                                               char** buffer,
                                               unsigned int& bufferSize) = 0;

        void threadFunctionConnection(unsigned short listeningPort);

        ConnectionList  _connectionList;

        CtiMutex _socketMutex;

        typedef std::map<unsigned short, SOCKET*> PortSocketMap;
        typedef PortSocketMap::iterator PortSocketMap_itr;

        PortSocketMap _socketConnections;
    private:
        bool loadList(std::string& aDirection,  CtiFDRPointList& aList);

        RWThreadFunction _threadSingleConnection;

        RWThreadFunction _threadHeartbeat;
        void threadFunctionSendHeartbeat(void);

        CtiMutex _connectionListMutex;
        CtiFDRClientServerConnection* findConnectionForDestination(const CtiFDRDestination destination) const;

        unsigned short _portNumber;
        int _pointTimeVariation;
        int _timestampReasonabilityWindow;
        int _linkTimeout;

        bool _singleListeningPort;

        std::string direction;
        HANDLE _shutdownEvent;
};

