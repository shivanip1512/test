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
    boost::optional<unsigned long> _millis;

    Chrono();
    Chrono( unsigned long millis );

public:
    unsigned long milliseconds() const;

    std::string toString() const;

    operator bool() const
    {
        return _millis;
    }

    static Chrono milliseconds( unsigned long millis );
    static Chrono seconds( unsigned long seconds );
    static Chrono infinite;
};

} // Timing
} // Cti
