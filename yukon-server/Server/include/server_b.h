#pragma once

#include "con_mgr.h"
#include "critical_Section.h"
#include "mutex.h"
#include "queue.h"
#include "smartmap.h"

class CtiCommandMsg;

IM_EX_CTISVR bool isQuestionable(const CtiConnectionManager *ptr, void *narg);

class IM_EX_CTISVR CtiServer
{
public:

    typedef CtiSmartMap< CtiConnectionManager >  coll_type;
    typedef coll_type::ptr_type       ptr_type;
    typedef coll_type::spiterator     spiterator;
    typedef coll_type::insert_pair    insert_pair;

    typedef CtiLockGuard< CtiMutex > CtiServerExclusion;

protected:

   CtiMutex  _server_exclusion;  // Mutual exclusion object.

   boost::thread  _mainThread;   // Thread which does work on the MainQueue_
   boost::thread  _connThread;   // Thread which accepts connections.

   coll_type mConnectionTable;

   CtiConnection::Que_t          MainQueue_;        // Main queue (Message is the base class) Priority queue)
   CtiConnection::Que_t          DeferredQueue_;    // Deferred queue (Message is the base class) Priority queue)
   CtiFIFOQueue<CtiMessage>      CacheQueue_;       // Cache queue, holds points we need to load.

public:

   CtiServer();
   virtual ~CtiServer();

   void join();
   bool join(unsigned long milliseconds);  //  returns true if thread joined

   void  clientConnect(CtiServer::ptr_type &CM);
   virtual void  clientShutdown(CtiServer::ptr_type &CM);
   virtual YukonError_t clientRegistration(CtiServer::ptr_type &CM);
   virtual void  commandMsgHandler(CtiCommandMsg *Cmd);
   virtual int   clientArbitrationWinner(CtiServer::ptr_type &CM);
   int   clientConfrontEveryone(PULONG pClientCount);
   virtual int   clientPurgeQuestionables(PULONG pDeadClients);

   virtual std::string getMyServerName() const;

   virtual void  shutdown();
   void          terminate();

   ptr_type findConnectionManager(const Cti::ConnectionHandle handle);
};
