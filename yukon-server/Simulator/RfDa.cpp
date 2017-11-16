#include "precompiled.h"

#include "RfDa.h"

#include "rfn_identifier.h"

#include "CParms.h"

#include "logger.h"

#include "std_helper.h"
#include "random_generator.h"

#include <map>
#include <random>
#include <time.h>

namespace Cti {
namespace Simulator {

std::vector<unsigned char> RfDa::Dnp3Address(const std::vector<unsigned char>& request, const RfnIdentifier & rfnId)
{
    if( ! request.empty() )
    {
        switch( request[0] )
        {
            case 0x35:
            {
                auto serial = std::stoi(rfnId.serialNumber);

                return { 0x36, static_cast<unsigned char>(serial >> 8), static_cast<unsigned char>(serial) };
            }
        }
    }
    return {};
}

}
}