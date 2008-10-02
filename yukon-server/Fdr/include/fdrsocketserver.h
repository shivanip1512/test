/*
 *    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
 */
#ifndef __FDRSOCKETSERVER_H__
#define __FDRSOCKETSERVER_H__

#include <windows.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrclientserverconnection.h"
#include "fdrinterface.h"

class CtiTime;

class IM_EX_FDRBASE CtiFDRSocketServer : public CtiFDRInterface
{

    public:
        // constructors and destructors
        CtiFDRSocketServer(string &);

        virtual ~CtiFDRSocketServer();

        // This method is of no use to use, but it has to be defined in our class.
        // Fortunately it is never called via our baseclass.
        virtual int processMessageFromForeignSystem(CHAR *data) { return 0; };

        virtual int processMessageFromForeignSystem(
          CtiFDRClientServerConnection& connection, char* data, unsigned int size) = 0;

        virtual bool loadTranslationLists(void);
        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool send=false)=0;
        //Force Cleanup in sub classes from this point.
        virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)=0;

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

        typedef std::list<CtiFDRClientServerConnection*> ConnectionList;

    protected:
        void clearFailedLayers();
        SOCKET createBoundListener();

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

    private:
        bool loadList(string& aDirection,  CtiFDRPointList& aList);

        RWThreadFunction  _threadConnection;
        void threadFunctionConnection(void);

        RWThreadFunction  _threadHeartbeat;
        void threadFunctionSendHeartbeat(void);

        ConnectionList  _connectionList;
        CtiMutex _connectionListMutex;
        CtiFDRClientServerConnection* findConnectionForDestination(
            const CtiFDRClientServerConnection::Destination& destination) const;

        SOCKET _listenerSocket;
        unsigned short _portNumber;
        int _pointTimeVariation;
        int _timestampReasonabilityWindow;
        int _linkTimeout;

        string direction;
        HANDLE _shutdownEvent;
};


#endif  //  #ifndef __FDRSOCKETSERVER_H__


