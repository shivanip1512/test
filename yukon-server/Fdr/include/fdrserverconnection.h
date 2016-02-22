#pragma once

#include "dlldefs.h"
#include "queues.h"
#include "fdrsocketlayer.h"
#include "fdrsocketconnection.h"
#include "worker_thread.h"


class IM_EX_FDRBASE CtiFDRServerConnection : public CtiFDRSocketConnection
{                                    
    typedef CtiFDRSocketConnection Inherited;

    public:
        DEBUG_INSTRUMENTATION

        CtiFDRServerConnection(SOCKET aSocket, const Cti::SocketAddress& aAddr, CtiFDRSocketLayer * aParent=NULL);
        ~CtiFDRServerConnection();

        int initializeConnection (int aPortNumber);
        INT readSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesRead);

        int init (); 
        int init (int aPortNumber); 
        int run  (); 
        int stop (); 

    protected:

        Cti::WorkerThread   iThreadReceive;
        
        void threadFunctionGetDataFrom( void );
};
