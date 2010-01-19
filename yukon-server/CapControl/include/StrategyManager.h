/* 
    COPYRIGHT: Copyright (C) 2010
                    Cooper Power Systems EAS
                    Cannon Technologies, Inc.
---------------------------------------------------------------------------*/

#ifndef CTI_STRATEGYMANAGER_H
#define CTI_STRATEGYMANAGER_H

#include <memory>

#include "ControlStrategies.h"
#include "StrategyLoader.h"


class StrategyManager
{

public:


    StrategyManager( std::auto_ptr<StrategyLoader> loader );

    void reload(const long ID);
    void reloadAll();

    void unload(const long ID);
    void unloadAll();

    StrategyPtr getStrategy(const long ID) const;

private:

    static const long   _defaultID;

    StrategyMap _strategies;

    std::auto_ptr<StrategyLoader>   _loader;

};

#endif

