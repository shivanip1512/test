
#include "yukon.h"

#include "ControlStrategy.h"
#include "NoStrategy.h"
#include "Controllable.h"


Controllable::Controllable()
    : CapControlPao(),
      _strategy(StrategyPtr(new NoStrategy))        // NOTE: this is an unmanaged strategy!
{

}


Controllable::Controllable(RWDBReader& rdr, StrategyPtr strategy)
    : CapControlPao(rdr),
      _strategy(strategy)
{
    _strategy->registerControllable(getPaoId());
}


Controllable::Controllable(const Controllable & rhs)
    : CapControlPao(rhs),
      _strategy(rhs.getStrategy())
{
    _strategy->registerControllable(getPaoId());
}


Controllable::~Controllable()
{
    _strategy->unregisterControllable(getPaoId());
}


Controllable & Controllable::operator=(const Controllable & rhs)
{
    CapControlPao::operator=(rhs);

    if (this != &rhs)
    {
        setStrategy(rhs.getStrategy());
    }

    return *this;
}


StrategyPtr Controllable::getStrategy() const
{
    return _strategy;
}


void Controllable::setStrategy(StrategyPtr strategy)
{
    _strategy->unregisterControllable(getPaoId());
    _strategy = strategy;
    _strategy->registerControllable(getPaoId());
}

