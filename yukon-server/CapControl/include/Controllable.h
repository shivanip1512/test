

#pragma once


#include "CapControlPao.h"
#include "ControlStrategy.h"



class Controllable : public CapControlPao
{

public:

    Controllable();

    Controllable(RWDBReader& rdr, StrategyPtr strategy);

    Controllable(const Controllable & rhs);

    ~Controllable();

    Controllable & operator=(const Controllable & rhs);

    StrategyPtr getStrategy() const;

    void setStrategy(StrategyPtr strategy);

private:

    StrategyPtr _strategy;

};

