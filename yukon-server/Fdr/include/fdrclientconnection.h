#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   fdrclientconnection
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/INCLUDE/fdrclientconnection.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:00 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __FDRCLIENTCONNECTION_H__
#define __FDRCLIENTCONNECTION_H__

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
#endif

