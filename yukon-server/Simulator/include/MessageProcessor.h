#pragma once

#include "FilterImpl.h"
#include "FilterInterface.h"

#include <vector>

namespace Cti {
namespace Simulator{

class MessageProcessor
{
public:
    MessageProcessor();
    error_t ProcessMessage(bytes &message);

private:
    std::vector<Filter*> _filters;
};

}
}
