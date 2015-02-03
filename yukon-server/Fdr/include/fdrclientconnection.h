#pragma once

#include "dlldefs.h"
#include "queues.h"
#include "fdrsocketlayer.h"
#include "fdrsocketconnection.h"
#include "worker_thread.h"



class IM_EX_FDRBASE CtiFDRClientConnection : public CtiFDRSocketConnection
{
    typedef CtiFDRSocketConnection Inherited;

    public:
        CtiFDRClientConnection(const Cti::SocketAddress& aAddr, CtiFDRSocketLayer * aParent);
        CtiFDRClientConnection(SOCKET aSocket, CtiFDRSocketLayer * aParent=NULL);
        ~CtiFDRClientConnection();

        int initializeConnection (const Cti::SocketAddress& aAddr);
        INT writeSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesRead);
        HCTIQUEUE & getQueueHandle();

        int init ();
        int run  ();
        int stop ();

    protected:

        Cti::WorkerThread   iThreadHeartbeat;
        Cti::WorkerThread   iThreadSend;

        void threadFunctionSendHeartbeat( void );
        void threadFunctionSendDataTo( void );

    private:

        HCTIQUEUE               iQueueHandle;
};
