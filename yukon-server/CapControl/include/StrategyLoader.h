
#pragma once

#include "StrategyManager.h"
#include "row_reader.h"

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

protected:

    void parseCoreReader(Cti::RowReader & reader, StrategyManager::StrategyMap &strategies);

private:

    void loadCore(const long ID, StrategyManager::StrategyMap &strategies);

    void loadParameters(const long ID, StrategyManager::StrategyMap &strategies);
};

