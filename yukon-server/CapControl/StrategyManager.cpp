/* 
    COPYRIGHT: Copyright (C) 2010
                    Cooper Power Systems EAS
                    Cannon Technologies, Inc.
---------------------------------------------------------------------------*/

#include "yukon.h"

#include "StrategyManager.h"

/*
    New Strategy hierarchy starts below! 
*/

StrategyManager::StrategyManager( std::auto_ptr<StrategyLoader> loader )
    : _loader( loader )
{
    StrategyPtr     none( new NoStrategy );

    _strategies.insert( std::make_pair( 0, none ) );      // a sane default for get() to use...
}


void StrategyManager::reload(const long ID)
{
    _loader->load(ID, _strategies);
}


void StrategyManager::reloadAll()
{
    reload(-1);
}


void StrategyManager::unload(const long ID)
{
    _strategies.erase(ID);
}


void StrategyManager::unloadAll()
{
    _strategies.clear();
}


StrategyPtr StrategyManager::get(const long ID)
{
    StrategyMap::iterator   iter = _strategies.find(ID);

    return iter != _strategies.end()
                    ? iter->second
                    : _strategies.find(0)->second;
}

