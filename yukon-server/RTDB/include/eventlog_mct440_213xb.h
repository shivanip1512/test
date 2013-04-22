#pragma once

#include <string>

namespace Cti {
namespace Devices {

class Mct440_213xBEventLog
{
public:
    static bool resolveEventCode( const unsigned long eventCode,
                                  const unsigned long argument,
                                  std::string& resolvedEventName,
                                  std::string& resolvedArgument );

};

}
}
