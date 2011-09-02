#pragma once

#include "ctinexus.h"
#include "dlldefs.h"
#include "thread.h"
#include "logger.h"
#include "port_base.h"
#include "string_utility.h"


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

   bool _sequenceFailReported;
   UINT _requestCount;                          // counts outstanding requests.  > 1 indicates a problem.
   boost::shared_ptr< CtiPort > _port;                 // The port on which this share exists.

   CTINEXUS _returnNexus;                       // returnNexus is formed as the "client" side of a socket to internalNexus (server side)
                                                // both are formed in this class.  returnNexus is passed into porter via the outmessage through queues,
                                                // so data is returned outbound only via the nexus.
   CTINEXUS _internalNexus;                     // internalNexus is the "server" side of the internal nexus from port_shr and porter proper.
   INT      _internalPort;                      // internal port for porter to share communicaitons.  Used to return IMs from port control.  It's number does not matter.

   std::map<unsigned char, unsigned char> _ccuError;

   virtual void inThread( void ) = 0;
   virtual void outThread( void ) = 0;

   std::string getIDString() const;

   unsigned getDebugLevel(unsigned mask=0xffffffff);

   // additional thread states
   enum
   {
        INWAITFOROUT = CtiThread::LAST,
        SOCKCONNECTED,
        SOCKFAILED,
        RUNCOMPLETE
   };

private:

   CtiPortShare(const CtiPortShare& aRef)
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " **** Checkpoint **** " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
   }

public:

   CtiPortShare(boost::shared_ptr< CtiPort > myPort, INT listenPort);
   virtual ~CtiPortShare();

   UINT getRequestCount() const;

   CtiPortShare& setRequestCount(UINT bc);
   CtiPortShare& incRequestCount();
   CtiPortShare& decRequestCount();

   virtual void interruptBlockingAPI();

   boost::shared_ptr< CtiPort > getPort();

   static USHORT ProcessEventCode(USHORT EventCode);

   void createNexus(std::string nexusName);
   void connectNexus();

   CTINEXUS* getReturnNexus() { return &_returnNexus; }

   CtiPortShare& setListenPort(INT prt);

   void          setSharedCCUError(unsigned char address, unsigned char error_byte);
   void          clearSharedCCUError(unsigned char address);
   bool          hasSharedCCUError(unsigned char address) const;
   unsigned char getSharedCCUError(unsigned char address) const;

};

inline CtiPortShare& CtiPortShare::setListenPort(INT prt) { _internalPort = prt; return *this; }
