#pragma once

#include "port_serial.h"
#include "tbl_port_tcpip.h"

namespace Cti   {
namespace Ports {

class IM_EX_PRTDB UdpPort : public CtiPortSerial
{
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
       return ClientErrors::None;
   }
   virtual YukonError_t openPort(INT rate, INT bits, INT parity, INT stopbits)
   {
       return ClientErrors::None;
   }
   virtual YukonError_t outMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, std::list<CtiMessage *> &traceList)
   {
       return ClientErrors::None;
   }
   virtual bool isViable()
   {
       return true;  //  connectionless, so always viable
   }
};

typedef boost::shared_ptr< UdpPort > UdpPortSPtr;

}
}



