#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __FDRSOCKETLAYER_H__
#define __FDRSOCKETLAYER_H__

#include <rw/cstring.h>
#include <rw/tpslist.h>
#include <rw/thr/mutex.h>
#include <rw/thr/barrier.h>
#include <rw/thr/condtion.h>
#include <rw/thr/thrfunc.h> 

#include "dlldefs.h"
#include "queues.h"
#include "device.h"             // get the raw states
#include "fdrsocketconnection.h"

class CtiFDRInterface;
class CtiFDRServerConnection;
class CtiFDRClientConnection;
class CtiFDRSocketInterface;
class CtiMessage;

class RWThreadFunction;

class IM_EX_FDRBASE CtiFDRSocketLayer 
{                                    
    public:

        typedef enum {
             Server_Single=0, 
             Server_Multiple, 
             Client_Multiple
        } FDRConnectionType;

        CtiFDRSocketLayer::CtiFDRSocketLayer(RWCString & interfaceName, 
                                             FDRConnectionType aType,
                                             CtiFDRSocketInterface *aParent);    
        CtiFDRSocketLayer::CtiFDRSocketLayer(RWCString & interfaceName, 
                                             CtiFDRServerConnection *aInBoundConnection,
                                             FDRConnectionType aType,
                                             CtiFDRSocketInterface *aParent);
        CtiFDRSocketLayer::CtiFDRSocketLayer(RWCString & interfaceName, 
                                             SOCKET aInBound,
                                             SOCKET aOutBound,
                                             FDRConnectionType aType,
                                             CtiFDRSocketInterface *aParent);
        ~CtiFDRSocketLayer();

        BOOL operator==( const CtiFDRSocketLayer &other ) const;

        USHORT getPortNumber () const;
        CtiFDRSocketLayer& setPortNumber (INT aPortNumber);

        USHORT getConnectPortNumber () const;
        HEV & getConnectionSem ();
        CtiFDRSocketLayer& setConnectionSem (HEV aSem);

        RWCString       &   getName(void);
        RWCString          getName(void) const;
        CtiFDRSocketLayer& setName (RWCString aName);

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

        // convience functions used to get from layer to interface
        int getMessageSize(CHAR *data);
        RWCString decodeClientName(CHAR *data);
        int processMessageFromForeignSystem (CHAR *data);
        bool sendMessageToForeignSys ( CtiMessage *aMessage );
        CHAR *buildForeignSystemHeartbeatMsg (void);
        ULONG getDebugLevel(void);

        int initializeClientConnection (void);
        int initializeServerConnection (void);

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

        RWThreadFunction    iThreadConnectionStatus;
        void threadFunctionConnectionStatus( void );

    private:
        RWCString               iName;
        CtiFDRSocketInterface  *iParent;    
                                   
        CtiFDRServerConnection  *iInBoundConnection;            
        CtiFDRClientConnection  *iOutBoundConnection;            
                           
        FDRConnectionType       iConnectionType;
        HEV                     iSemaphore;
        long                    iLinkStatusID;
};


#endif // #ifndef __FDRSOCKETLAYER_H__

