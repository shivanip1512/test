#pragma once

#include <rw/thr/thrfunc.h> 
#include "dlldefs.h"
#include "queues.h"
#include "fdrsocketlayer.h"
#include "fdrsocketconnection.h"

class IM_EX_FDRBASE CtiFDRServerConnection : public CtiFDRSocketConnection
{                                    
    typedef CtiFDRSocketConnection Inherited;

    public:
        CtiFDRServerConnection(SOCKET aSocket, SOCKADDR_IN aAddr, CtiFDRSocketLayer * aParent=NULL);
        ~CtiFDRServerConnection();

        int initializeConnection (int aPortNumber);
        INT readSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesRead);

        int init (); 
        int init (int aPortNumber); 
        int run  (); 
        int stop (); 

    protected:

        RWThreadFunction    iThreadReceive;
        RWThreadFunction    iThreadConnectionStatus;
        
        void threadFunctionGetDataFrom( void );
        void threadFunctionConnectionStatus( void );
};
