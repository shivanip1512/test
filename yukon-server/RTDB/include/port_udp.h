#pragma once
#pragma warning( disable : 4786)

#include "port_serial.h"
#include "tbl_port_tcpip.h"

namespace Cti   {
namespace Ports {

class IM_EX_PRTDB UdpPort : public CtiPortSerial
{
protected:

   CtiTablePortTCPIP    _tcpIpInfo;

private:

    UdpPort(const UdpPort& aRef);
    UdpPort& operator=(const UdpPort& aRef);

public:

   typedef CtiPortSerial Inherited;

   UdpPort() {};
   virtual ~UdpPort() {};

   INT           getIPPort()    const;
   const string &getIPAddress() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   const string &getEncodingKey()  const;
   const string &getEncodingType() const;

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
   virtual bool isViable()
   {
       return true;  //  connectionless, so always viable
   }
};

typedef shared_ptr< UdpPort > UdpPortSPtr;

}
}



