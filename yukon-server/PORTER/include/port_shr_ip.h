
#pragma warning( disable : 4786)
#ifndef __PORT_SHR_IP_H__
#define __PORT_SHR_IP_H__

/*-----------------------------------------------------------------------------*
*
* File:   port_shr_ip
*
* Class:  CtiPortShareIP
* Date:   8/6/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/INCLUDE/port_shr_ip.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:44 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/thr/thread.h>
#include <rw/thr/thrfunc.h>
#include <rw/toolpro/socket.h>

#include "dlldefs.h"
#include "ctinexus.h"
#include "port_shr.h"

class IM_EX_PORTGLOB CtiPortShareIP : public CtiPortShare
                                      
{
protected:

   bool _reconnect;
   bool _broadcast;

   CTINEXUS _scadaNexus, _scadaListenNexus;

   int _ipPort;                                 // IP port to expose.

private:

   RWThreadFunction _outThread;

public:

   CtiPortShareIP(CtiPort *myPort, INT listenPort);
   virtual ~CtiPortShareIP();

   int getIPPort() const;
   CtiPortShareIP& setIPPort(int prt);

   virtual void run();

   void start(CtiPortShare& shr);

   void inThread();
   void outThread();
      
   int inThreadConnectNexus();
   int outThreadValidateNexus();



};
#endif // #ifndef __PORT_SHR_IP_H__
