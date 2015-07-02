
#pragma once

#include "StrategyManager.h"

namespace Cti
{
class RowReader;
}

void parseCoreReader(Cti::RowReader & reader, StrategyManager::StrategyMap &strategies);



class StrategyLoader
{

public:

    StrategyLoader()    {  }

    ~StrategyLoader()   {  }

    virtual StrategyManager::StrategyMap load(const long ID) = 0;
};



class StrategyDBLoader : public StrategyLoader
{

public:

    StrategyDBLoader() : StrategyLoader()   {  }

    ~StrategyDBLoader()   {  }

    virtual StrategyManager::StrategyMap load(const long ID);

private:

    void loadCore(const long ID, StrategyManager::StrategyMap &strategies);

    void loadParameters(const long ID, StrategyManager::StrategyMap &strategies);
};

