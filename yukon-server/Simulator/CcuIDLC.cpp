#include "precompiled.h"

#include "CcuIDLC.h"
#include "types.h"

namespace Cti {
namespace Simulator {

bool CcuIDLC::addressAvailable(Comms &comms)
{
    bytes address_buf;
    byte_appender address_appender = byte_appender(address_buf);

    unsigned long bytes_available = 0;

    //  read until we find the HDLC framing flag OR there's nothing left
    while( comms.peek(address_appender, 1) && address_buf[0] != Hdlc_FramingFlag )
    {
        comms.read(address_appender, 1);
        address_buf.clear();
    }

    return comms.available(2);
}

error_t CcuIDLC::peekAddress(Comms &comms, unsigned &address)
{
    bytes address_buf;

    unsigned long bytes_available = 0;

    if( !comms.peek(byte_appender(address_buf), 2) )
    {
        return "Timeout reading address";
    }

    //  second byte should be an address, not the IDLC framing flag...
    //    however, the framing flag would compute to an address of 63, which is valid,
    //    so this check will give us trouble for that address
    if( address_buf[0] != Hdlc_FramingFlag || address_buf[1] == Hdlc_FramingFlag )
    {
        return "HDLC framing error";
    }

    address = address_buf[1] >> 1;

    return error_t::success;
}


}
}
