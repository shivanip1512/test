
#pragma once

#include "dbaccess.h"

#include "ControlStrategy.h"
#include "IVVCStrategy.h"
#include "KVarStrategy.h"
#include "MultiVoltStrategy.h"
#include "MultiVoltVarStrategy.h"
#include "NoStrategy.h"
#include "PFactorKWKVarStrategy.h"
#include "PFactorKWKQStrategy.h"
#include "TimeOfDayStrategy.h"
#include "VoltStrategy.h"


class StrategyLoader
{

public:

    StrategyLoader()    {  }

    ~StrategyLoader()   {  }


    virtual void load(const long ID, StrategyMap &strategies) = 0;


private:


};


class StrategyDBLoader : public StrategyLoader
{

public:

    StrategyDBLoader() : StrategyLoader()   {  }

    ~StrategyDBLoader()   {  }

    virtual void load(const long ID, StrategyMap &strategies)
    {
        loadCore(ID, strategies);
        loadParameters(ID, strategies);
    }

private:

    void loadCore(const long ID, StrategyMap &strategies);

    void loadParameters(const long ID, StrategyMap &strategies);

    RWRecursiveLock<RWMutexLock>    _dbMutex;
};

