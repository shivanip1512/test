#pragma once
#pragma warning( disable : 4786)


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

   CTINEXUS _scadaNexus;

   int _ipPort;                                 // IP port to expose.

private:

   RWThreadFunction _outThread;
   RWThreadFunction _inThread;

   void shutDown();

   int determineTimeout(unsigned char *buffer, unsigned int len);

public:

   CtiPortShareIP(CtiPortSPtr myPort, INT listenPort);
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

