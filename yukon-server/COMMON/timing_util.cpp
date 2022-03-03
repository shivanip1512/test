#include "precompiled.h"

#include "numstr.h"
#include "timing_util.h"
#include <cassert>


namespace Cti {
namespace Timing {

Chrono::Chrono( DurationType durationType, unsigned long millis ) :
    _durationType(durationType),
    _millis( millis )
{
}

unsigned long Chrono::milliseconds() const
{
    return _millis;
}

std::string Chrono::toString() const
{
    if( _durationType == Infinite )
    {
        return "infinite";
    }

    return CtiNumStr(_millis) + " milliseconds";
}

Chrono Chrono::milliseconds( unsigned long millis )
{
    return Chrono( Milliseconds, millis);
}

Chrono Chrono::seconds( unsigned long seconds )
{
    assert( seconds <= (ULONG_MAX / 1000) );
    return Chrono( Milliseconds, 1000 * seconds);
}

Chrono Chrono::infinite = Chrono( Infinite, std::numeric_limits<unsigned long>::max() );

} // Timing
} // Cti
