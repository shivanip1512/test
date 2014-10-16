#pragma once

#include "dlldefs.h"

namespace Cti {

class IM_EX_CTIBASE MacroOffset
{
    unsigned _value;
    bool     _initialized;

public:

    MacroOffset();
    MacroOffset( unsigned const aVal );
    MacroOffset( MacroOffset const& aRef );
    ~MacroOffset();

    operator bool() const;

    MacroOffset& operator=(unsigned const rhs);
    MacroOffset& operator=(MacroOffset const& rhs);

    unsigned& operator*();

    unsigned const& operator*() const;

    std::string asString() const;

    /// uninitialized macro offset
    static MacroOffset const none;
};

} // namespace Cti
