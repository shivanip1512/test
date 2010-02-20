#include "yukon.h"

#include "CommsBehaviorApplicator.h"

namespace Cti {
namespace Simulator{

CommsBehaviorApplicator::CommsBehaviorApplicator()
{
    /*
    CommsBehavior* f;
    int chance = gConfigParms.getValueAsInt("SIMULATOR_ERROR_DELAY_PERCENT");

    if (chance > 0)
    {
        f = new DelayBehavior(chance);
        _filters.push_back(f);
    }
    */
}

error_t CommsBehaviorApplicator::ProcessMessage(bytes &message)
{
    for each(CommsBehavior* b in _behaviors)
    {
        b->applyBehavior(message);
    }

    return error_t::success;
}

error_t CommsBehaviorApplicator::setBehavior(CommsBehavior* behavior)
{
    _behaviors.push_back(behavior);
    return error_t::success;
}

}
}
