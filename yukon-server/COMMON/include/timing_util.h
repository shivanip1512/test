#pragma once

#include "boost/optional/optional.hpp"
#include "dlldefs.h"

namespace Cti {
namespace Timing {

/**
 * Hold finite or infinite duration with milliseconds precision.
 */
class IM_EX_CTIBASE Chrono
{
    enum DurationType {
        Milliseconds,
        Infinite
    };

    const unsigned long _millis;
    const DurationType  _durationType;

    Chrono( DurationType durationType, unsigned long millis );

public:
    unsigned long milliseconds() const;
    std::string toString() const;

    operator bool() const
    {
        return (_durationType != Infinite);
    }

    static Chrono milliseconds( unsigned long millis );
    static Chrono seconds( unsigned long seconds );
    static Chrono infinite;
};

} // Timing
} // Cti
