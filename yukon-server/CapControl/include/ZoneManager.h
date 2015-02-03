#pragma once

#include <memory>
#include <map>

#include <boost/make_shared.hpp>

#include "Zone.h"
#include "readers_writer_lock.h"

namespace Cti           {
namespace CapControl    {

class ZoneLoader;


class ZoneManager
{

public:

    typedef boost::shared_ptr<Zone>     SharedPtr;
    typedef std::map<long, SharedPtr>   ZoneMap;

    ZoneManager( std::unique_ptr<ZoneLoader> loader );

    void reload(const long Id);
    void reloadAll();

    void unload(const long Id);
    void unloadAll();

    SharedPtr getZone(const long Id) const;

    Zone::IdSet getAllChildrenOfZone(const long parentId) const;
    Zone::IdSet getZoneIdsBySubbus(const long subbusId) const;
    long getRootZoneIdForSubbus(const long subbusId) const;

private:

    void assignChildren();

    void getAllChildrenOfZone(const long parentId, Zone::IdSet & results) const;    // recursive

    typedef Cti::readers_writer_lock_t  Lock;
    typedef Lock::reader_lock_guard_t   ReaderGuard;
    typedef Lock::writer_lock_guard_t   WriterGuard;

    static const SharedPtr    _defaultZone;

    mutable Lock    _lock;

    ZoneMap         _zones;

    std::unique_ptr<ZoneLoader>   _loader;
};

}   // namespace Cti
}   // namespace CapControl

