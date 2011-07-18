#include "precompiled.h"

#include "dnp_object_internalindications.h"
#include "logger.h"
#include "cparms.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

int InternalIndications::restoreBits(const unsigned char *buf, int bitoffset, int len)
{
    //  we're not doing any error checking on the length and bit offset;
    //    that's a larger problem with the DNP protocol in general

    _valid = true;

    return 1;  //  1 bit long
}


}
}
}

