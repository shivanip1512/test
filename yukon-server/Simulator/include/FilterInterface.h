#pragma once

#include "types.h"

namespace Cti {
namespace Simulator{

class Filter
{
public:
    virtual void filter(bytes &message)=0;
};

}
}
