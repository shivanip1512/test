#pragma once

#include <rw/thr/thrfunc.h>
#include "dlldefs.h"
#include "queues.h"
#include "fdrsocketlayer.h"
#include "fdrsocketconnection.h"


class IM_EX_FDRBASE CtiFDRClientConnection : public CtiFDRSocketConnection
{
    typedef CtiFDRSocketConnection Inherited;

    public:
        CtiFDRClientConnection(SOCKADDR_IN aAddr, CtiFDRSocketLayer * aParent);
        CtiFDRClientConnection(SOCKET aSocket, CtiFDRSocketLayer * aParent=NULL);
        ~CtiFDRClientConnection();

        int initializeConnection (SOCKADDR_IN aAddr);
        INT writeSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesRead);
        HCTIQUEUE & getQueueHandle();

        int init ();
        int run  ();
        int stop ();

    protected:

        RWThreadFunction    iThreadHeartbeat;
        RWThreadFunction    iThreadSend;

        void threadFunctionSendHeartbeat( void );
        void threadFunctionSendDataTo( void );

    private:

        HCTIQUEUE               iQueueHandle;
};
