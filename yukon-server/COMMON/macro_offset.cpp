#include "precompiled.h"

#include <boost/lexical_cast.hpp>
#include "macro_offset.h"

namespace Cti {

/// create uninitialized macro offset
MacroOffset::MacroOffset() :
    _initialized(false)
{}

/// create initialized macro offset
MacroOffset::MacroOffset( unsigned const aVal ) :
    _initialized(false)
{
    *this = aVal;
}

/// copy macro offset
MacroOffset::MacroOffset( MacroOffset const& aRef ) :
    _initialized(false)
{
    *this = aRef;
}

MacroOffset::~MacroOffset()
{}

MacroOffset::operator bool() const
{
    return _initialized;
}

MacroOffset& MacroOffset::operator=(unsigned const rhs)
{
    _value       = rhs;
    _initialized = true;
    return *this;
}

MacroOffset& MacroOffset::operator=(MacroOffset const& rhs)
{
    _value       = rhs._value;
    _initialized = rhs._initialized;
    return *this;
}

unsigned& MacroOffset::operator*()
{
    assert( _initialized );
    return _value;
}

unsigned const& MacroOffset::operator*() const
{
    assert( _initialized );
    return _value;
}

std::string MacroOffset::asString() const
{
    if( !_initialized )
    {
        return "none";
    }

    return boost::lexical_cast<std::string>(_value);
}

MacroOffset const MacroOffset::none;

} // namespace Cti
