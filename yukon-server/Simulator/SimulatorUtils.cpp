#include "precompiled.h"

#include "SimulatorUtils.h"

namespace Cti{
namespace Simulator {

bool isDnpHeader(bytes peek_buf)
{
    if( peek_buf.size() < 2 )
    {
        return false;
    }

    return ((peek_buf[0] == 0x05) && (peek_buf[1] == 0x64));
}

}
}
