#pragma once

#include <string>

namespace Cti {

class Loggable
{
public:
    virtual std::string toString() const = 0;
};

} // namespace Cti
