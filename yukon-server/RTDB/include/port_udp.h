#pragma once

#include "port_serial.h"
#include "tbl_port_tcpip.h"

namespace Cti   {
namespace Ports {

class IM_EX_PRTDB UdpPort : public CtiPortSerial
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    UdpPort(const UdpPort&);
    UdpPort& operator=(const UdpPort&);

protected:

   CtiTablePortTCPIP    _tcpIpInfo;

public:

   typedef CtiPortSerial Inherited;

   UdpPort() {};

   INT           getIPPort()    const;
   const std::string &getIPAddress() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   const std::string &getEncodingKey()  const;
   const std::string &getEncodingType() const;

   virtual YukonError_t inMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, std::list<CtiMessage *> &traceList)
   {
       return NoError;
   }
   virtual YukonError_t openPort(INT rate, INT bits, INT parity, INT stopbits)
   {
       return NoError;
   }
   virtual YukonError_t outMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, std::list<CtiMessage *> &traceList)
   {
       return NoError;
   }
   virtual bool isViable()
   {
       return true;  //  connectionless, so always viable
   }
};

typedef boost::shared_ptr< UdpPort > UdpPortSPtr;

}
}



