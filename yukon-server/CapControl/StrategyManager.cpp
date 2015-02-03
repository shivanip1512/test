
#include "precompiled.h"

#include "StrategyManager.h"
#include "StrategyLoader.h"
#include "NoStrategy.h"


const StrategyManager::SharedPtr StrategyManager::_defaultStrategy( new NoStrategy );


StrategyManager::StrategyManager( std::unique_ptr<StrategyLoader> loader )
    : _loader( std::move(loader) )
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


void StrategyManager::saveAllStates()
{
    saveStates(-1);
}


void StrategyManager::saveStates(const long ID)
{
    ReaderGuard guard(_lock);

    _strategyBackup.clear();

    if ( ID < 0 )   // backup all strategies
    {
        _strategyBackup.insert( _strategies.begin(), _strategies.end() );
    }
    else            // back up strategy ID if it exists
    {
        StrategyMap::const_iterator iter = _strategies.find(ID);

        if ( iter != _strategies.end() )
        {
            _strategyBackup.insert( *iter );
        }
    }
}


void StrategyManager::restoreAllStates()
{
    restoreStates(-1);
}


void StrategyManager::restoreStates(const long ID)
{
    ReaderGuard guard(_lock);

    if ( ID < 0 )   // restore states for all strategies
    {
        for ( StrategyMap::const_iterator b = _strategies.begin(), e = _strategies.end(); b != e; ++b )
        {
            StrategyMap::const_iterator iter = _strategyBackup.find( b->first );

            if ( iter != _strategyBackup.end() )
            {
                b->second->restoreStates( iter->second.get() );
            }
        }
    }
    else            // only restore states for strategy ID
    {
        StrategyMap::const_iterator target = _strategies.find(ID);

        if ( target != _strategies.end() )
        {
            StrategyMap::const_iterator iter = _strategyBackup.find(ID);

            if ( iter != _strategyBackup.end() )
            {
                target->second->restoreStates( iter->second.get() );
            }
        }
    }

    _strategyBackup.clear();
}

