#include "precompiled.h"

#include "numstr.h"
#include "timing_util.h"


namespace Cti {
namespace Timing {

Chrono::Chrono()
{}

Chrono::Chrono( unsigned long millis ) : _millis(millis)
{}

unsigned long Chrono::milliseconds() const
{
    assert( _millis );
    return *_millis;
}

std::string Chrono::toString() const
{
    if( ! _millis )
    {
        return "undefined";
    }

    return CtiNumStr(*_millis) + " milliseconds";
}

Chrono Chrono::milliseconds( unsigned long millis )
{
    return Chrono(millis);
}

Chrono Chrono::seconds( unsigned long seconds )
{
    assert( seconds <= (ULONG_MAX / 1000) );
    return Chrono(1000 * seconds);
}

Chrono Chrono::infinite = Chrono();

} // Timing
} // Cti
