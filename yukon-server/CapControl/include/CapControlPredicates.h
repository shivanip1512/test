#include "CapControlPao.h"
#include "Controllable.h"

namespace Cti {
namespace CapControl {

inline bool isEnabled(CapControlPao * pao)
{
    return !pao->getDisableFlag();
}

inline bool existsAndEnabled(CapControlPao * pao)
{
    return pao && isEnabled(pao);
}

inline bool notIvvcStrategy(const Controllable * controllable)
{
    return controllable->getStrategy()->getUnitType() != ControlStrategy::IntegratedVoltVar;
}

inline bool hasStrategy(const Controllable * controllable)
{
    return controllable->getStrategy()->getUnitType() != ControlStrategy::None;
}

}
}
