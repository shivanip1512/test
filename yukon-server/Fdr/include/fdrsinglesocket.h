#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrsocketlayer.h"
#include "fdrinterface.h"

#define SINGLE_SOCKET_NULL               0
#define SINGLE_SOCKET_REGISTRATION       1
#define SINGLE_SOCKET_ACKNOWLEDGEMENT    2
#define SINGLE_SOCKET_VALUE            101
#define SINGLE_SOCKET_STATUS           102
#define SINGLE_SOCKET_VALMET_CONTROL   103 // arrgh, backward compatibility
#define SINGLE_SOCKET_CONTROL          201
#define SINGLE_SOCKET_FORCESCAN        110
#define SINGLE_SOCKET_TIMESYNC         401
#define SINGLE_SOCKET_STRATEGY         501
#define SINGLE_SOCKET_STRATEGYSTOP     503


class CtiTime;

class IM_EX_FDRBASE CtiFDRSingleSocket : public CtiFDRSocketInterface
{
    typedef CtiFDRSocketInterface Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDRSingleSocket(std::string &);

        virtual ~CtiFDRSingleSocket();

        CtiFDRSocketLayer * getLayer (void);
        CtiFDRSingleSocket& setLayer (CtiFDRSocketLayer * aLayer);

        // coming from the base class fdrsocketinterface
        virtual int processMessageFromForeignSystem (CHAR *data);
        virtual CHAR *buildForeignSystemHeartbeatMsg (void);
        virtual int getMessageSize(CHAR *data);
        virtual std::string decodeClientName(CHAR *data);

        virtual bool loadList(std::string &aDirection,  CtiFDRPointList &aList);

        virtual BOOL    init( void );
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

        // effective for all classes inheriting from here
        virtual CHAR *buildForeignSystemMsg (CtiFDRPoint &aPoint)=0;
        virtual bool buildAndWriteToForeignSystem (CtiFDRPoint &aPoint );
        virtual bool alwaysSendRegistrationPoints();
        virtual bool translateAndUpdatePoint(CtiFDRPointSPtr & translationPoint, int aIndex)=0;

        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);
        virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);

        virtual void signalReloadList();
        virtual void signalPointRemoved(std::string &pointName);

        virtual int processValueMessage(CHAR *data);
        virtual int processStatusMessage(CHAR *data);
        virtual int processControlMessage(CHAR *data);
        virtual int processScanMessage(CHAR *data);
        virtual int processRegistrationMessage(CHAR *data);
        virtual int processTimeSyncMessage(CHAR *data);

        virtual bool isRegistrationNeeded(void);
        virtual bool isClientConnectionValid (void);
        virtual void setCurrentClientLinkStates();

    protected:

        Cti::WorkerThread   iThreadConnection;
        void threadFunctionConnection( void );

    private:
        CtiFDRSocketLayer           *iLayer;
};
