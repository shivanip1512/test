
#pragma warning( disable : 4786)
#ifndef __PORT_SHR_H__
#define __PORT_SHR_H__

/*-----------------------------------------------------------------------------*
*
* File:   port_shr
*
* Class:  CtiPortShare
* Date:   8/6/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/INCLUDE/port_shr.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:44 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "ctinexus.h"
#include "dlldefs.h"
#include "thread.h"
#include "logger.h"
#include "port_base.h"


class IM_EX_PORTGLOB CtiPortShare : public CtiThread
{
public:

   enum
   {
      PSHR_ERROR_NOREPLY = 31,
      PSHR_ERROR_SEQUENCE = 32,
      PSHR_ERROR_FRAME = 33,
      PSHR_ERROR_BADCRC = 34,
      PSHR_ERROR_BADLENGTH = 35,
      PSHR_ERROR_UNKNOWN = 37,
      PSHR_ERROR_DATABASE = 53,
      PSHR_ERROR_RTU_DISABLED = 78,
      PSHR_ERROR_PORT_DISABLED = 83,

   } CtiPortShareError_t;

protected:

   UINT _busy;                                  // counts outstanding requests.  > 1 indicates a problem.
   CTINEXUS _returnNexus;                       // Who gets us back where we belong.
   CtiPort *_port;                      // The port on which this share exists.

   CTINEXUS _listenNexus;
   INT      _listenPort;

   virtual void inThread( void ) = 0;
   virtual void outThread( void ) = 0;

   // additional thread states
   enum
   {
        INOUTSYNC = CtiThread::LAST,
        SOCKCONNECTED,
        SOCKFAILED
   };

private:

   CtiPortShare(const CtiPortShare& aRef)
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

public:

   CtiPortShare(CtiPort* myPort, INT listenPort);
   virtual ~CtiPortShare();

   UINT getBusyCount() const;

   CtiPortShare& setBusyCount(UINT bc);
   CtiPortShare& incBusyCount();
   CtiPortShare& decBusyCount();

   virtual void interruptBlockingAPI();

   CtiPort *getPort();

   static USHORT ProcessEventCode(USHORT EventCode);

   void createNexus(RWCString nexusName);
   void connectNexus();

   CTINEXUS* getReturnNexus() { return &_returnNexus; }

   CtiPortShare& setListenPort(INT prt);

};

inline CtiPortShare& CtiPortShare::setListenPort(INT prt) { _listenPort = prt; return *this; }

#endif // #ifndef __PORT_SHR_H__
