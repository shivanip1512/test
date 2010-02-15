
#pragma once

#include "StrategyManager.h"


class StrategyLoader
{

public:

    StrategyLoader()    {  }

    ~StrategyLoader()   {  }

    virtual StrategyManager::StrategyMap load(const long ID) = 0;

private:

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

