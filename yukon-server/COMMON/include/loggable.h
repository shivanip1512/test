#pragma once

#include <string>

namespace Cti {

struct Loggable
{
    virtual std::string toString() const = 0;
};

} // namespace Cti
