/* 
    COPYRIGHT: Copyright (C) 2010
                    Cooper Power Systems EAS
                    Cannon Technologies, Inc.
---------------------------------------------------------------------------*/

#ifndef CTI_STRATEGYLOADER_H
#define CTI_STRATEGYLOADER_H

#include "dbaccess.h"

#include "ControlStrategies.h"
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





#endif

