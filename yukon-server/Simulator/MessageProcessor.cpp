#include "yukon.h"

#include "MessageProcessor.h"
#include "cparms.h"

namespace Cti {
namespace Simulator{

MessageProcessor::MessageProcessor()
{
    Filter* f;
    int chance = gConfigParms.getValueAsInt("SIMULATOR_ERROR_DELAY_PERCENT");

    if (chance > 0)
    {
        f = new DelayFilter(chance);
        _filters.push_back(f);
    }
    
    // Add more filters later as they are implemented in the FilterImpl.cpp file.
}

error_t MessageProcessor::ProcessMessage(bytes &message)
{
    for each(Filter* f in _filters)
    {
        f->filter(message);
    }

    return error_t::success;
}

}
}
