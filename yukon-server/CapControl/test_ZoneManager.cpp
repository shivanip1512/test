#include <boost/test/unit_test.hpp>

#include "ZoneManager.h"
#include "ZoneLoader.h"

using Cti::CapControl::Zone;
using Cti::CapControl::ZoneLoader;
using Cti::CapControl::ZoneManager;

BOOST_AUTO_TEST_SUITE( test_ZoneManager )

class ZoneUnitTestLoader : public ZoneLoader
{

public:

    // default construction and destruction is OK

    virtual ZoneManager::ZoneMap load(const long Id)
    {
        ZoneManager::ZoneMap zones;

        if (Id < 0)
        {
            long Ids[] = { 100, 110, 115 };

            for (int i = 0; i < sizeof(Ids)/ sizeof(*Ids); i++)
            {
                loadSingle(Ids[i], zones);
            }
        }
        else
        {
            loadSingle(Id, zones);
        }

        return zones;
    }

private:

    void loadSingle(const long Id, ZoneManager::ZoneMap &zones)
    {
        bool doInsertion = true;
        ZoneManager::SharedPtr newZone;

        switch (Id)
        {
            case 100: newZone = ZoneManager::SharedPtr( new Zone( Id, 135, 35, "The Neutral Zone" ) );  break;
            case 110: newZone = ZoneManager::SharedPtr( new Zone( Id, 115, 48, "The Twilight Zone" ) ); break;
            case 115: newZone = ZoneManager::SharedPtr( new Zone( Id, 115, 48, "The Dead Zone" ) );
                newZone->addChildId(110);
                break;

            default:  doInsertion = false;
        }

        if (doInsertion)
        {
            zones[Id] = newZone;
        }
    }
};


BOOST_AUTO_TEST_CASE(test_ZoneManager_default_initialization)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    ZoneManager::SharedPtr zone = zoneManager.getZone(100);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );

    zone = zoneManager.getZone(110);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );

    zone = zoneManager.getZone(115);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );
}


BOOST_AUTO_TEST_CASE(test_ZoneManager_default_initialization_reloadAll_unloadAll)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    ZoneManager::SharedPtr zone = zoneManager.getZone(100);

    BOOST_CHECK_EQUAL( 100,  zone->getId() );

    zone = zoneManager.getZone(110);

    BOOST_CHECK_EQUAL( 110,  zone->getId() );

    zone = zoneManager.getZone(111);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );

    zone = zoneManager.getZone(115);

    BOOST_CHECK_EQUAL( 115,  zone->getId() );

    zoneManager.unloadAll();

    zone = zoneManager.getZone(100);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );

    zone = zoneManager.getZone(110);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );

    zone = zoneManager.getZone(111);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );

    zone = zoneManager.getZone(115);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );
}


BOOST_AUTO_TEST_CASE(test_ZoneManager_default_initialization_reloadAll_unload)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    zoneManager.unload(110);

    ZoneManager::SharedPtr zone = zoneManager.getZone(100);

    BOOST_CHECK_EQUAL( 100,  zone->getId() );

    zone = zoneManager.getZone(110);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );

    zone = zoneManager.getZone(115);

    BOOST_CHECK_EQUAL( 115,  zone->getId() );
}


BOOST_AUTO_TEST_CASE(test_ZoneManager_default_initialization_reload)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reload(110);

    ZoneManager::SharedPtr zone = zoneManager.getZone(100);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );

    zone = zoneManager.getZone(110);

    BOOST_CHECK_EQUAL( 110,  zone->getId() );

    zone = zoneManager.getZone(115);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );
}


BOOST_AUTO_TEST_CASE(test_ZoneManager_get_zones_by_Subbus)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    Zone::IdSet subset = zoneManager.getZoneIdsBySubbus(48);

    BOOST_CHECK_EQUAL(  2,  subset.size() );
}


BOOST_AUTO_TEST_CASE(test_ZoneManager_get_all_child_zones)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    Zone::IdSet subset = zoneManager.getAllChildrenOfZone(100);

    BOOST_CHECK_EQUAL(  0,  subset.size() );

    subset = zoneManager.getAllChildrenOfZone(110);

    BOOST_CHECK_EQUAL(  0,  subset.size() );

    subset = zoneManager.getAllChildrenOfZone(115);

    BOOST_CHECK_EQUAL(  1,  subset.size() );

    BOOST_CHECK_EQUAL(110,  *subset.begin() );
}


BOOST_AUTO_TEST_CASE(test_ZoneManager_get_root_zone_for_Subbus)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    BOOST_CHECK_EQUAL(  115,  zoneManager.getRootZoneIdForSubbus(48) );
}

BOOST_AUTO_TEST_SUITE_END()
