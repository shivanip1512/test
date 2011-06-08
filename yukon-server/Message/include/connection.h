#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   connection
*
* Date:   9/16/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/connection.h-arc  $
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2007/12/10 23:02:57 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __CONNECTION_H__
#define __CONNECTION_H__

#include <limits.h>

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
#include "mutex.h"
#include "queue.h"

class IM_EX_MSG CtiConnection
{
public:

    typedef  CtiQueue<CtiMessage, std::greater<CtiMessage> > Que_t;
    // typedef  CtiFIFOQueue<CtiMessage> Que_t;

protected:

   std::string              _name;
   CtiTime                  _birth;

   INT                      _termTime;
   CtiTime                  _lastInQueueWrite;

   CtiRegistrationMsg      *_regMsg;
   CtiPointRegistrationMsg *_ptRegMsg;

   std::string              _host;
   INT                      _port;

   CtiExchange             *_exchange;                     // Pointer so I can kill it dead at will...

   RWThreadFunction         outthread_;
   RWThreadFunction         inthread_;

    Que_t outQueue;        // outthread_ pops out of here
    Que_t *inQueue;        // inthread_ dumps into here


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
         UINT     _preventInThreadReset   : 1;     // This is here because the In and Out threads can both
         UINT     _preventOutThreadReset  : 1;     // call cleanConnection, we dont want them fighting.
      };
   };

   virtual void writeIncomingMessageToQueue(CtiMessage *msgPtr);

private:
    void cleanConnection();
    void cleanExchange();

    mutable CtiMutex _mux;


public:

   // Don't want anyone to use this one....
   CtiConnection( );
   CtiConnection( const INT &Port, const std::string &Host, Que_t *inQ = NULL, INT tt = 3);
   CtiConnection(CtiExchange *xchg, Que_t *inQ = NULL, INT tt = 3);
   virtual ~CtiConnection();

   virtual void doConnect( const INT &Port, const std::string &Host, Que_t *inQ = NULL );
   virtual RWBoolean operator==(const CtiConnection& aRef) const;
   static unsigned hash(const CtiConnection& aRef);
   CtiMessage*    ReadConnQue(UINT Timeout = UINT_MAX);
   int            WriteConnQue(CtiMessage*, unsigned millitimeout = 0, bool cleaniftimedout = true);

   void   Shutdown();

   std::string getPeer() const;

   INT ConnectPortal();
   INT ManageSocketError( RWSockErr& msg );

   void ResetConnection();
   void ShutdownConnection();

   // This function starts the execution of the next two, which are threads.
   virtual int  ThreadInitiate();
   virtual void InThread();             // InBound messages from application come in here
   virtual void OutThread();            // OutBound messages to the applicaiton go through here

   virtual void ThreadTerminate();
    Que_t & getOutQueueHandle();
    Que_t & getInQueueHandle();

   BOOL  isViable() const;
   UINT  valid() const;
   void  forceTermination();
   INT   verifyConnection();
   INT   establishConnection();
   INT   checkCancellation(INT mssleep = 0);
   INT   waitForConnect();

   const CtiTime& getLastReceiptTime() const;

   void  messagePeek( CtiMessage *MyMsg );
   void  recordRegistration( CtiMessage *msg );
   void  recordPointRegistration( CtiMessage *msg );

   std::string who();

   std::string   getName() const;
   CtiConnection& setName(const std::string &str);

   int outQueueCount() const;
   void restartConnection();

   virtual void preWork();

};

typedef CtiConnection* CtiConnectionPtr;

#endif // #ifndef __CONNECTION_H__
