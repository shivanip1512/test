#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   connection
*
* Date:   9/16/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/connection.h-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/02/20 03:58:04 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __CONNECTION_H__
#define __CONNECTION_H__

#include <limits.h>

#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/mutex.h>
#include <rw/thr/thrfunc.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>
#include <rw/toolpro/neterr.h>

#include "dlldefs.h"
#include "exchange.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_reg.h"
#include "queue.h"


class IM_EX_MSG CtiConnection : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

   RWCString               _name;

   INT                     _termTime;
   RWTime                  _lastInQueueWrite;

   CtiRegistrationMsg      *_regMsg;
   CtiPointRegistrationMsg *_ptRegMsg;

   RWCString               _host;
   INT                     _port;

   CtiExchange             *_exchange;                     // Pointer so I can kill it dead at will...

   RWThreadFunction        outthread_;
   RWThreadFunction        inthread_;

   CtiQueue<CtiMessage, less<CtiMessage> > outQueue;        // outthread_ pops out of here
   CtiQueue<CtiMessage, less<CtiMessage> > *inQueue;        // inthread_ dumps into here


   /*
    *  State Descriptions:
    *
    *  _valid              Exchange/Socket/Portal is good to go.
    *  _dontReconnect      ConnectPortal should not re-establish the connection..
    *                         Someone has put us in shutdown mode
    *  _serverConnection   This Exchange/Socket/Portal cannot be reconnected.  Failure is terminal
    */
   union
   {
      UINT        _flag;

      struct         // Bit field status definitions
      {
         UINT     _bQuit                  : 1;
         UINT     _noLongerViable         : 1;     // One or both the threads have terminated.
         UINT     _connectCalled          : 1;     // Indicates that threads have started and queues exist.
         UINT     _valid                  : 1;
         UINT     _dontReconnect          : 1;
         UINT     _serverConnection       : 1;
         UINT     _localQueueAlloc        : 1;
      };
   };

private:
    void cleanConnection();
    void cleanExchange();


public:

   typedef  CtiQueue<CtiMessage, less<CtiMessage> > InQ_t;

   // Don't want anyone to use this one....
   CtiConnection( );
   CtiConnection( const INT &Port, const RWCString &Host, InQ_t *inQ = NULL, INT tt = 3);
   CtiConnection(CtiExchange *xchg, InQ_t *inQ = NULL, INT tt = 3);
   virtual ~CtiConnection();

   virtual void doConnect( const INT &Port, const RWCString &Host, InQ_t *inQ = NULL );
   virtual RWBoolean operator==(const CtiConnection& aRef) const;
   static unsigned hash(const CtiConnection& aRef);
   CtiMessage*    ReadConnQue(UINT Timeout = UINT_MAX);
   int            WriteConnQue(CtiMessage*, unsigned millitimeout = 0, bool cleaniftimedout = true);

   void        Shutdown();

   RWInetHost  getPeer() const;

   INT ConnectPortal();
   INT ManageSocketError( RWSockErr& msg );

   void ResetConnection();
   void ShutdownConnection();

   // This function starts the execution of the next two, which are threads.
   virtual int  ThreadInitiate();
   virtual void InThread();             // InBound messages from application come in here
   virtual void OutThread();            // OutBound messages to the applicaiton go through here

   virtual void ThreadTerminate();
   CtiQueue<CtiMessage, less<CtiMessage> > & getOutQueueHandle();
   CtiQueue<CtiMessage, less<CtiMessage> > & getInQueueHandle();

   BOOL  isViable() const;
   UINT  valid() const;
   void  forceTermination();
   INT   verifyConnection();
   INT   establishConnection(INT freq = 15);
   INT   checkCancellation(INT mssleep = 0);
   INT   waitForConnect();

   const RWTime& getLastReceiptTime() const;

   void  messagePeek( CtiMessage *MyMsg );
   void  recordRegistration( CtiMessage *msg );
   void  recordPointRegistration( CtiMessage *msg );

   RWCString who();

   RWCString   getName() const;
   CtiConnection& setName(const RWCString &str);

   int outQueueCount() const;
   void restartConnection();

};

#endif // #ifndef __CONNECTION_H__
