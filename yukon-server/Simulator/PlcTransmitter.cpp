#include "precompiled.h"

#include "PlcTransmitter.h"

namespace Cti {
namespace Simulator {

//  refer to Section 2 EMETCON Protocols, 2-1 to 2-12, PDF pages 7 to 18
unsigned PlcTransmitter::dlc_time(unsigned bits)
{
    if( !bits )  return 0;

    return ((BitLength_TransmitOverhead + bits) * BitTime_usec) / 1000;
}

unsigned PlcTransmitter::dlc_time(unsigned bits_out, unsigned bits_in)
{
    return dlc_time(bits_out) + dlc_time(bits_in);
}

}
}

