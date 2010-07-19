
#include "yukon.h"

#include "ControlStrategy.h"
#include "NoStrategy.h"
#include "Controllable.h"
#include "database_reader.h"

Controllable::Controllable()
    : CapControlPao(),
      _strategyManager(0),
      _strategyID(-1)
{

}


Controllable::Controllable(StrategyManager * strategyManager)
    : CapControlPao(),
      _strategyManager(strategyManager),
      _strategyID(-1)
{
    getStrategy()->registerControllable( getPaoId() );
}


Controllable::Controllable(Cti::RowReader & rdr, StrategyManager * strategyManager)
    : CapControlPao(rdr),
      _strategyManager(strategyManager),
      _strategyID(-1)
{
    getStrategy()->registerControllable( getPaoId() );
}


Controllable::Controllable(const Controllable & rhs)
    : CapControlPao(rhs),
      _strategyManager(rhs._strategyManager),
      _strategyID(rhs._strategyID)

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
        _strategyID      = rhs.getStrategyID();

        getStrategy()->registerControllable( getPaoId() );
    }

    return *this;
}


StrategyManager::SharedPtr Controllable::getStrategy() const
{
    if ( _strategyManager )
    {
        return _strategyManager->getStrategy( _strategyID );
    }
    
    return StrategyManager::getDefaultStrategy();
}


const long Controllable::getStrategyID() const
{
    return _strategyID;
}


void Controllable::setStrategy(const long strategyID)
{
    getStrategy()->unregisterControllable( getPaoId() );

    _strategyID = strategyID;

    getStrategy()->registerControllable( getPaoId() );
}


void Controllable::setStrategyManager(StrategyManager * strategyManager)
{
    _strategyManager = strategyManager;
}

