#include "precompiled.h"

#include "dnp_datalink_packet.h"

namespace Cti {
namespace Protocols {
namespace DNP {
namespace DatalinkPacket {

unsigned calcPacketLength( unsigned headerLen )
{
    unsigned packetLength = 0;

    if( headerLen >= HeaderCountedLength )
    {
        packetLength = HeaderLength;

        //  get the payload size by subtracting off the header bytes
        unsigned dataLength = headerLen - HeaderCountedLength;

        if( dataLength )
        {
            unsigned numBlocks = (dataLength + BlockLength - 1) / BlockLength;

            packetLength += dataLength;
            packetLength += numBlocks * CRCLength;  //  add on the CRC bytes
        }
    }

    return packetLength;
}

}
}
}
}

