#pragma once

#include <windows.h>    

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"
#include "socket_helper.h"

class CtiFDRSocketConnection;
class CtiTime;

class IM_EX_FDRBASE CtiFDRSocketInterface : public CtiFDRInterface
{
    typedef CtiFDRInterface Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDRSocketInterface(std::string & interfaceType, int aPort=0, int aWindow = 120);

        virtual ~CtiFDRSocketInterface();

        virtual BOOL    init( void );
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

        bool loadTranslationLists(void);

        int  getPortNumber () const;
        CtiFDRSocketInterface& setPortNumber(int aPort);

        std::string getIpMask();
        void setIpMask(const std::string& ipMask);

        int  getLinkTimeout () const;
        CtiFDRSocketInterface& setLinkTimeout(int aLinkTimeout);

        int  getConnectPortNumber () const;
        CtiFDRSocketInterface& setConnectPortNumber(int aPort);
        int  getTimestampReasonabilityWindow () const;
        CtiFDRSocketInterface& setTimestampReasonabilityWindow(int aWindow);

        int  getPointTimeVariation () const;
        CtiFDRSocketInterface& setPointTimeVariation(int aTime);

        virtual bool loadList(std::string &aDirection, CtiFDRPointList &aList) = 0;
        virtual CHAR *buildForeignSystemHeartbeatMsg (void) = 0;
        virtual INT getMessageSize(CHAR *data)=0;
        virtual std::string decodeClientName(CHAR *data)=0;
        virtual int  sendAllPoints(void);
        void sendMessageToForeignSys ( CtiMessage *aMessage ) override;
        virtual bool buildAndWriteToForeignSystem (CtiFDRPoint &aPoint )=0;
        virtual bool alwaysSendRegistrationPoints();


        bool isRegistered();
        CtiFDRSocketInterface &setRegistered (bool aFlag=true);
        virtual bool CtiFDRSocketInterface::isClientConnectionValid (void);

        void shutdownListener();

        static FLOAT   ntohieeef (LONG NetLong);
        static LONG    htonieeef (FLOAT  HostFloat);

    protected:

        Cti::ServerSockets _listenerSockets;
        CtiMutex           _listenerMux;
        HANDLE             _listenerShutdownEvent;

        bool isListenerShutdown();

    private:

        int     iPortNumber;
        int     iTimestampReasonabilityWindow;
        bool    iRegistered;
        int     iPointTimeVariation;
        bool    _listenerShutdown;

        std::string _ipMask;

        /********************************
        * added for Progress energy to run RCCS and Yukon on the same machine
        * both applications are trying to listen and connect on socket 1000
        * so I'm adding a connect socket number to make this work BIG UGLY HACK !!
        *
        * value is checked inside of fdrclientconnection and used there if set
        *********************************
        */
        int     iConnectPortNumber;


        /********************************
        * long overdue change that allows the user to set the connection timeout between
        * two systems.  It defaults to 60 seconds
        *********************************
        */
        int                   iLinkTimeout;
};
