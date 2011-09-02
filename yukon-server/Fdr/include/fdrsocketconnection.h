#pragma once

#include "dlldefs.h"
#include "queues.h"

class CtiFDRSocketLayer;

class IM_EX_FDRBASE CtiFDRSocketConnection 
{                                    
    public:
        CtiFDRSocketConnection(CtiFDRSocketLayer * aParent=NULL);
        CtiFDRSocketConnection(CtiFDRSocketLayer * aParent, SOCKADDR_IN aType);
        ~CtiFDRSocketConnection();

        typedef enum {
             Uninitialized=0, 
             Ok,
             Failed
        } FDRConnectionStatus;

        CtiFDRSocketLayer * getParent ();
        CtiFDRSocketConnection& setParent (CtiFDRSocketLayer * aParent);

        SOCKADDR_IN &    getAddr();
        CtiFDRSocketConnection& setAddr (SOCKADDR_IN aType);

        SOCKET  & getConnection();
        SOCKET  getConnection() const;
        CtiFDRSocketConnection& setConnection (SOCKET aSocket);
        FDRConnectionStatus getConnectionStatus () const;
        CtiFDRSocketConnection& setConnectionStatus (FDRConnectionStatus aFlag);

        int closeAndFailConnection (void) ;

    private:

        SOCKET                  iConnection;            
        FDRConnectionStatus     iConnectionStatus;
        SOCKADDR_IN             iAddr;

        CtiFDRSocketLayer         *iParent;
};
