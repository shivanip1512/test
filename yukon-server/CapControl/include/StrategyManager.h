
#pragma once

#include <memory>
#include <map>

#include <boost/shared_ptr.hpp>
#include <boost/weak_ptr.hpp>

#include "readers_writer_lock.h"
#include "ControlStrategy.h"


class StrategyLoader;



class StrategyManager
{

public:

    typedef boost::shared_ptr<ControlStrategy>  SharedPtr;
    typedef boost::weak_ptr<ControlStrategy>    WeakPtr;
    typedef std::map<long, SharedPtr>           StrategyMap;

    typedef Cti::readers_writer_lock_t          Lock;
    typedef Lock::reader_lock_guard_t           ReaderGuard;
    typedef Lock::writer_lock_guard_t           WriterGuard;

    StrategyManager( std::unique_ptr<StrategyLoader> loader );

    void reload(const long ID);
    void reloadAll();

    void unload(const long ID);
    void unloadAll();

    SharedPtr getStrategy(const long ID) const;

    const long getDefaultId() const;

    static const SharedPtr getDefaultStrategy();

    void executeAll() const;

    void saveStates(const long ID);
    void saveAllStates();

    void restoreStates(const long ID);
    void restoreAllStates();

private:

    mutable Lock    _lock;

    static const SharedPtr    _defaultStrategy;

    StrategyMap _strategies;
    StrategyMap _strategyBackup;

    std::unique_ptr<StrategyLoader>   _loader;
};

