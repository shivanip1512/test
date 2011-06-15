
#include "yukon.h"

#include "ZoneManager.h"
#include "ZoneLoader.h"


namespace Cti           {
namespace CapControl    {

/*
    This non-Zone is what is returned on a call to getZone() that has
    no corresponding entry in the map.  The ID of -1 is the standard
    CapControl way of saying: "Ignore me - I really don't exist"
 
    Someday I'd like to replace this with a thrown exception.
*/
const ZoneManager::SharedPtr ZoneManager::_defaultZone( new Zone( -1,       // Zone ID
                                                                  -1,       // Parent Zone ID
                                                                  -1,       // Subbus ID
                                                                  "(none)"  // Zone name
                                                                ) );


ZoneManager::ZoneManager( std::auto_ptr<ZoneLoader> loader )
    : _loader( loader )
{

}


void ZoneManager::reload(const long Id)
{
    ZoneMap results = _loader->load(Id);

    // update the mapping with the results of the loading
    {
        WriterGuard guard(_lock);

        for ( ZoneMap::const_iterator b = results.begin(), e = results.end(); b != e; ++b )
        {
            _zones[ b->first ] = results[ b->first ];
        }

        assignChildren();
    }
}


void ZoneManager::reloadAll()
{
    reload(-1);
}


void ZoneManager::unload(const long Id)
{
    WriterGuard guard(_lock);

    _zones.erase(Id);

    assignChildren();
}


void ZoneManager::unloadAll()
{
    WriterGuard guard(_lock);

    _zones.clear();
}


ZoneManager::SharedPtr ZoneManager::getZone(const long Id) const
{
    ReaderGuard guard(_lock);

    ZoneMap::const_iterator iter = _zones.find(Id);

    return iter != _zones.end()
                    ? iter->second
                    : _defaultZone;
}


Zone::IdSet ZoneManager::getZoneIdsBySubbus(const long subbusId) const
{
    Zone::IdSet  subset;

    for each ( const ZoneManager::ZoneMap::value_type & zone in _zones )
    {
        if ( zone.second->getSubbusId() == subbusId )
        {
            subset.insert( zone.first );
        }
    }

    return subset;
}


long ZoneManager::getRootZoneIdForSubbus(const long subbusId) const
{
    for each ( const ZoneManager::ZoneMap::value_type & zone in _zones )
    {
        if ( ( zone.second->getSubbusId() == subbusId ) &&
             ( zone.second->getId() == zone.second->getParentId() ) )
        {
            return zone.first;
        }
    }

    return -1;
}


Zone::IdSet ZoneManager::getAllChildrenOfZone(const long parentId) const
{
    Zone::IdSet  subset;

    getAllChildrenOfZone(parentId, subset);

    return subset;
}


/** 
 * Recursive helper function 
 */
void ZoneManager::getAllChildrenOfZone(const long parentId, Zone::IdSet & results) const
{
    Zone::IdSet children = getZone(parentId)->getChildIds();    // returns empty set if zone doesn't exist

    if ( ! children.empty() )
    {
        results.insert( children.begin(), children.end() );

        for each ( const Zone::IdSet::value_type & ID in children )
        {
            getAllChildrenOfZone(  ID, results );
        }
    }
}


void ZoneManager::assignChildren()
{
    // clear out existing parent-child hierarchy

    for ( ZoneMap::iterator b = _zones.begin(), e = _zones.end(); b != e; ++b )
    {
        b->second->clearChildIds();
    }

    // rebuild the parent-child hierarchy

    for ( ZoneMap::iterator b = _zones.begin(), e = _zones.end(); b != e; ++b )
    {
        SharedPtr zone = b->second;

        ZoneMap::iterator parent = _zones.find( zone->getParentId() );

        if ( parent != _zones.end() )
        {
            parent->second->addChildId( zone->getId() );
        }
    }
}

}   // namespace Cti
}   // namespace CapControl

