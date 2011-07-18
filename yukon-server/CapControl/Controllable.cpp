
#include "precompiled.h"

#include "ControlStrategy.h"
#include "NoStrategy.h"
#include "Controllable.h"
#include "database_reader.h"

Controllable::Controllable()
    : CapControlPao(),
      _strategyManager(0),
      _strategyId(-1)
{

}


Controllable::Controllable(StrategyManager * strategyManager)
    : CapControlPao(),
      _strategyManager(strategyManager),
      _strategyId(-1)
{
    getStrategy()->registerControllable( getPaoId() );
}


Controllable::Controllable(Cti::RowReader & rdr, StrategyManager * strategyManager)
    : CapControlPao(rdr),
      _strategyManager(strategyManager),
      _strategyId(-1)
{
    getStrategy()->registerControllable( getPaoId() );
}


Controllable::Controllable(const Controllable & rhs)
    : CapControlPao(rhs),
      _strategyManager(rhs._strategyManager),
      _strategyId(rhs._strategyId)

{
    getStrategy()->registerControllable( getPaoId() );
}


Controllable::~Controllable()
{
    getStrategy()->unregisterControllable( getPaoId() );
}


Controllable & Controllable::operator=(const Controllable & rhs)
{
    CapControlPao::operator=( rhs );

    if ( this != &rhs )
    {
        getStrategy()->unregisterControllable( getPaoId() );

        _strategyManager = rhs._strategyManager;
        _strategyId      = rhs.getStrategyId();

        getStrategy()->registerControllable( getPaoId() );
    }

    return *this;
}


StrategyManager::SharedPtr Controllable::getStrategy() const
{
    if ( _strategyManager )
    {
        return _strategyManager->getStrategy( _strategyId );
    }

    return StrategyManager::getDefaultStrategy();
}


const long Controllable::getStrategyId() const
{
    return _strategyId;
}

void Controllable::setStrategy(const long strategyId)
{
    getStrategy()->unregisterControllable( getPaoId() );

    _strategyId = strategyId;

    getStrategy()->registerControllable( getPaoId() );
}


void Controllable::setStrategyManager(StrategyManager * strategyManager)
{
    _strategyManager = strategyManager;
}

