
#pragma once

#include <memory>

#include "ControlStrategy.h"
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

    const long getDefaultId() const;

    void executeAll() const;

private:

    static const long   _defaultID;

    StrategyMap _strategies;

    std::auto_ptr<StrategyLoader>   _loader;

};

