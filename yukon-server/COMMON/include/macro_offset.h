#pragma once

#include "dlldefs.h"
#include "boost/optional/optional.hpp"

namespace Cti {

class IM_EX_CTIBASE MacroOffset
{
    unsigned _value;

    bool     _initialized;

public:

    /// create uninitialized macro offset
    MacroOffset() :
        _initialized(false)
    {}

    /// create initialized macro offset
    MacroOffset( unsigned const aVal ) :
        _initialized(false)
    {
        *this = aVal;
    }

    /// copy macro offset
    MacroOffset( MacroOffset const& aRef ) :
        _initialized(false)
    {
        *this = aRef;
    }

    ~MacroOffset()
    {}

    operator bool() const
    {
        return _initialized;
    }

    MacroOffset& operator=(unsigned const rhs)
    {
        _value       = rhs;
        _initialized = true;
        return *this;
    }

    MacroOffset& operator=(MacroOffset const& rhs)
    {
        _value       = rhs._value;
        _initialized = rhs._initialized;
        return *this;
    }

    unsigned& operator*()
    {
        assert( _initialized );
        return _value;
    }

    unsigned const& operator*() const
    {
        assert( _initialized );
        return _value;
    }

    /// uninitialized macro offset
    static MacroOffset const none;
};

} // namespace Cti
