/*-----------------------------------------------------------------------------*
*
* File:   server_b
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/INCLUDE/server_b.h-arc  $
* REVISION     :  $Revision: 1.15.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:38 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __SERVER_B_H__
#define __SERVER_B_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <iostream>
#include <functional>

#include <rw\thr\thrfunc.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw\rwerr.h>

#include "con_mgr.h"
#include "critical_Section.h"
#include "mutex.h"
#include "queue.h"
#include "smartmap.h"
#include "dlldefs.h"

class CtiCommandMsg;

IM_EX_CTISVR bool isQuestionable(const CtiConnectionManager *ptr, void *narg);

#define USE_CTIMUTEX TRUE

class IM_EX_CTISVR CtiServer
{
public:

    typedef CtiSmartMap< CtiConnectionManager >                 coll_type;              // This is the collection type!
    typedef CtiSmartMap< CtiConnectionManager >::ptr_type       ptr_type;
    typedef CtiSmartMap< CtiConnectionManager >::spiterator     spiterator;
    typedef CtiSmartMap< CtiConnectionManager >::insert_pair    insert_pair;


   #ifdef USE_CTIMUTEX
   typedef CtiLockGuard< CtiMutex > CtiServerExclusion;
   #else
   typedef CtiLockGuard< CtiCriticalSection > CtiServerExclusion;
   #endif

private:

protected:

   #ifdef USE_CTIMUTEX
   CtiMutex                   _server_exclusion;       // Mutual exclusion object.
   #else
   CtiCriticalSection         _server_exclusion;
   #endif

   RWThreadFunction           MainThread_;      // Thread which does work on the MainQueue_
   RWThreadFunction           ConnThread_;      // Thread which accepts connections.

   /*
    *  Data Stores.
    */
   CtiSmartMap< CtiConnectionManager > mConnectionTable;

   CtiConnection::Que_t          MainQueue_;        // Main queue (Message is the base class) Priority queue)
   CtiConnection::Que_t          DeferredQueue_;    // Deferred queue (Message is the base class) Priority queue)
   CtiFIFOQueue<CtiMessage>      CacheQueue_;       // Cache queue, holds points we need to load.

   RWInetPort                    NetPort;
   RWInetAddr                    NetAddr;    // This one for this server!

public:

   CtiServer();
   virtual ~CtiServer();

   virtual void join();

   virtual RWWaitStatus join(unsigned long milliseconds);
   virtual void  clientConnect(CtiServer::ptr_type CM);
   virtual void  clientShutdown(CtiServer::ptr_type CM);
   virtual int   clientRegistration(CtiServer::ptr_type CM);
   virtual int   commandMsgHandler(CtiCommandMsg *Cmd);
   virtual int   clientArbitrationWinner(CtiServer::ptr_type CM);
   virtual int   clientConfrontEveryone(PULONG pClientCount = NULL);
   virtual int   clientPurgeQuestionables(PULONG pDeadClients = NULL);

   virtual std::string getMyServerName() const;

   virtual void  shutdown();
   void          terminate();

   ptr_type findConnectionManager( long cid );
};


#endif      // #ifndef __SERVER_B_H__
