#pragma once

#include "yukon.h"
#include "dllbase.h"
#include <string>

namespace Cti {
namespace Config {

class IM_EX_CONFIG DNPStrings
{
public:
    static const std::string omitTimeRequest;
    static const std::string internalRetries;
    static const std::string enableUnsolicited;
    static const std::string enableDnpTimesyncs;
    static const std::string useLocalTime;
};

}
}
