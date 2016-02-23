#pragma once

#include "dlldefs.h"
#include "queues.h"
#include "socket_helper.h"

class CtiFDRSocketLayer;

class IM_EX_FDRBASE CtiFDRSocketConnection 
{                                    
    public:
        DEBUG_INSTRUMENTATION;

        CtiFDRSocketConnection(CtiFDRSocketLayer * aParent=NULL);
        CtiFDRSocketConnection(CtiFDRSocketLayer * aParent, const Cti::SocketAddress& aAddr );
        ~CtiFDRSocketConnection();

        typedef enum {
             Uninitialized=0, 
             Ok,
             Failed
        } FDRConnectionStatus;

        CtiFDRSocketLayer * getParent ();
        CtiFDRSocketConnection& setParent (CtiFDRSocketLayer * aParent);

        const Cti::SocketAddress& getAddr() const;
        CtiFDRSocketConnection&   setAddr( const Cti::SocketAddress& aAddr );

        SOCKET  & getConnection();
        SOCKET  getConnection() const;
        CtiFDRSocketConnection& setConnection (SOCKET aSocket);
        FDRConnectionStatus getConnectionStatus () const;
        CtiFDRSocketConnection& setConnectionStatus (FDRConnectionStatus aFlag);

        int closeAndFailConnection (void) ;

    private:

        SOCKET                  iConnection;            
        FDRConnectionStatus     iConnectionStatus;

        Cti::SocketAddress      iAddr;

        CtiFDRSocketLayer         *iParent;
};
