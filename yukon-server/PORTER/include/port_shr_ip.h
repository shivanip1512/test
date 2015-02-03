#pragma once

#include "dlldefs.h"
#include "streamSocketConnection.h"
#include "port_shr.h"

class IM_EX_PORTGLOB CtiPortShareIP : public CtiPortShare
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiPortShareIP(const CtiPortShareIP&);
    CtiPortShareIP& operator=(const CtiPortShareIP&);

protected:

   bool _reconnect;
   bool _broadcast;

   Cti::StreamSocketConnection _scadaNexus;

   int _ipPort;                                 // IP port to expose.

private:

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

