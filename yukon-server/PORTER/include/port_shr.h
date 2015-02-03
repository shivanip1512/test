#pragma once

#include "streamSocketConnection.h"
#include "dlldefs.h"
#include "thread.h"
#include "logger.h"
#include "port_base.h"


class IM_EX_PORTGLOB CtiPortShare : public CtiThread
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiPortShare(const CtiPortShare&);
    CtiPortShare& operator=(const CtiPortShare&);

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

   Cti::StreamSocketConnection _returnNexus;    // returnNexus is formed as the "client" side of a socket to internalNexus (server side)
                                                // both are formed in this class.  returnNexus is passed into porter via the outmessage through queues,
                                                // so data is returned outbound only via the nexus.
   Cti::StreamSocketConnection _internalNexus;  // internalNexus is the "server" side of the internal nexus from port_shr and porter proper.
   INT      _internalPort;                      // internal port for porter to share communicaitons.  Used to return IMs from port control.  It's number does not matter.

   HANDLE   _shutdownEvent;

   std::map<unsigned char, unsigned char> _ccuError;

   virtual void inThread( void ) = 0;
   virtual void outThread( void ) = 0;

   std::string getIDString() const;

   unsigned getDebugLevel(unsigned mask=0xffffffff);

   // additional thread states
   enum
   {
        INWAITFOROUT = CtiThread::LAST,
        RUNCOMPLETE
   };

public:

   CtiPortShare(boost::shared_ptr< CtiPort > myPort, INT listenPort);
   virtual ~CtiPortShare();

   UINT getRequestCount() const;

   CtiPortShare& setRequestCount(UINT bc);
   CtiPortShare& incRequestCount();
   CtiPortShare& decRequestCount();

   boost::shared_ptr< CtiPort > getPort();

   static USHORT ProcessEventCode(USHORT EventCode);

   void createNexus(const std::string &nexusName);
   void connectNexus();

   Cti::StreamSocketConnection* getReturnNexus() { return &_returnNexus; }

   void          setSharedCCUError(unsigned char address, unsigned char error_byte);
   void          clearSharedCCUError(unsigned char address);
   bool          hasSharedCCUError(unsigned char address) const;
   unsigned char getSharedCCUError(unsigned char address) const;

};

