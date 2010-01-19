/* 
    COPYRIGHT: Copyright (C) 2010
                    Cooper Power Systems EAS
                    Cannon Technologies, Inc.
---------------------------------------------------------------------------*/

#include "yukon.h"

#include "StrategyManager.h"


const long StrategyManager::_defaultID = 0;


StrategyManager::StrategyManager( std::auto_ptr<StrategyLoader> loader )
    : _loader( loader )
{
    StrategyPtr     none( new NoStrategy );

    none->setStrategyId(_defaultID);

    _strategies.insert( std::make_pair( none->getStrategyId(), none ) );        // a sane default for get() to use...
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
    if (ID != _defaultID)               // do NOT erase the default
    {
        _strategies.erase(ID);
    }
}


void StrategyManager::unloadAll()
{
    StrategyPtr     none = getStrategy(_defaultID);                             // grab the default

    _strategies.clear();                                                        // clear the map
    _strategies.insert( std::make_pair( none->getStrategyId(), none ) );        // re-insert the default
}


StrategyPtr StrategyManager::getStrategy(const long ID) const
{
    StrategyMap::const_iterator iter = _strategies.find(ID);

    return iter != _strategies.end()
                    ? iter->second
                    : _strategies.find(_defaultID)->second;
}

