#pragma once

#include "CapControlPao.h"
#include "StrategyManager.h"


class Controllable : public CapControlPao
{

public:

    Controllable();

    Controllable(StrategyManager * strategyManager);

    Controllable(Cti::RowReader & rdr, StrategyManager * strategyManager);

    Controllable(const Controllable & rhs);

    ~Controllable();

    Controllable & operator=(const Controllable & rhs);

    StrategyManager::SharedPtr getStrategy() const;

    const long getStrategyId() const;

    void setStrategy(const long strategyId);

    void setStrategyManager(StrategyManager * strategyManager);

private:

    long _strategyId;

    StrategyManager * _strategyManager;
};
