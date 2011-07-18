#include "precompiled.h"

#include "logger.h"
#include "port_udp.h"

using std::string;
using std::endl;

namespace Cti   {
namespace Ports {

const string &UdpPort::getIPAddress() const  {  return _tcpIpInfo.getIPAddress();  }
INT           UdpPort::getIPPort()    const  {  return _tcpIpInfo.getIPPort();     }


void UdpPort::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    try
    {
        Inherited::DecodeDatabaseReader(rdr);
        _tcpIpInfo.DecodeDatabaseReader(rdr);       // get the base class handled
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

const string &UdpPort::getEncodingKey()  const
{
    return _tcpIpInfo.getEncodingKey();
}

const string &UdpPort::getEncodingType() const
{
    return _tcpIpInfo.getEncodingType();
}

}
}

