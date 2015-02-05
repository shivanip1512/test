#pragma once

#include "dlldefs.h"
#include "streamSocketConnection.h"
#include "port_shr.h"

class IM_EX_PORTGLOB CtiPortShareIP : public CtiPortShare
{
   bool _reconnect;
   bool _broadcast;

   Cti::StreamSocketConnection _scadaNexus;

   int _ipPort;                                 // IP port to expose.

   boost::thread _outThread;
   boost::thread _inThread;

   void shutDown();

   int determineTimeout(unsigned char *buffer, unsigned int len);

public:

   CtiPortShareIP(CtiPortSPtr myPort, INT listenPort);
   virtual ~CtiPortShareIP();

   int getIPPort() const;
   CtiPortShareIP& setIPPort(int prt);

   virtual void run();

   using CtiPortShare::start; // unhide start method from parent
   void start(CtiPortShare& shr);

   void inThread();
   void outThread();

   int inThreadConnectNexus();
   int outThreadValidateNexus();

};

