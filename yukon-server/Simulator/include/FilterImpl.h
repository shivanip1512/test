#pragma once

#include "FilterInterface.h"
#include "logger.h"

namespace Cti {
namespace Simulator{

class DelayFilter : public Filter
{
public:
    DelayFilter(int chance);
    virtual void filter(bytes &message);

private:
    bytes _delayed;
    int _chance;
};

}
}
