
#include "yukon.h"

#include "StrategyManager.h"
#include "StrategyLoader.h"
#include "NoStrategy.h"


const StrategyManager::SharedPtr StrategyManager::_defaultStrategy( new NoStrategy );


StrategyManager::StrategyManager( std::auto_ptr<StrategyLoader> loader )
    : _loader( loader )
{

}


void StrategyManager::reload(const long ID)
{
    StrategyMap results = _loader->load(ID);

    // update the mapping with the results of the loading
    {
        WriterGuard guard(_lock);

        for ( StrategyMap::const_iterator b = results.begin(), e =  results.end(); b != e; ++b )
        {
            _strategies[ b->first ] = results[ b->first ];
        }
    }
}


void StrategyManager::reloadAll()
{
    reload(-1);
}


void StrategyManager::unload(const long ID)
{
    WriterGuard guard(_lock);

    _strategies.erase(ID);
}


void StrategyManager::unloadAll()
{
    WriterGuard guard(_lock);

    _strategies.clear();
}


StrategyManager::SharedPtr StrategyManager::getStrategy(const long ID) const
{
    ReaderGuard guard(_lock);

    StrategyMap::const_iterator iter = _strategies.find(ID);

    return iter != _strategies.end()
                    ? iter->second
                    : getDefaultStrategy();
}


const long StrategyManager::getDefaultId() const
{
    return getDefaultStrategy()->getStrategyId();
}


const StrategyManager::SharedPtr StrategyManager::getDefaultStrategy()
{
    return _defaultStrategy;
}


void StrategyManager::executeAll() const
{
    ReaderGuard guard(_lock);

    for ( StrategyMap::const_iterator b = _strategies.begin(), e = _strategies.end(); b != e; ++b )
    {
        b->second->execute();
    }
}

