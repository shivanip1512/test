#pragma once
#pragma warning( disable : 4786)

#include "port_serial.h"
#include "tbl_port_tcpip.h"

namespace Cti   {
namespace Ports {

class IM_EX_PRTDB TcpPort : public CtiPortSerial
{
private:

    TcpPort(const TcpPort& aRef);
    TcpPort& operator=(const TcpPort& aRef);

public:

   typedef CtiPortSerial Inherited;

   TcpPort() {};
   virtual ~TcpPort() {};

   virtual INT inMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, list<CtiMessage *> &traceList)
   {
       return 0;
   }
   virtual INT openPort(INT rate, INT bits, INT parity, INT stopbits)
   {
       return 0;
   }
   virtual INT outMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, list<CtiMessage *> &traceList)
   {
       return 0;
   }
};

typedef shared_ptr< TcpPort > TcpPortSPtr;

}
}

