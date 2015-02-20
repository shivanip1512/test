#include <boost/test/unit_test.hpp>

#include "boost_test_helpers.h"
#include "ZoneManager.h"
#include "ZoneLoader.h"

#include <boost/assign/list_of.hpp>

using Cti::CapControl::Zone;
using Cti::CapControl::ZoneLoader;
using Cti::CapControl::ZoneManager;


struct simple_zone_setup
{
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
            ZoneManager::SharedPtr newZone;

            switch (Id)
            {
                case 100: newZone = ZoneManager::SharedPtr( new Zone( Id, 135, 35, "The Neutral Zone" ) );  break;
                case 110: newZone = ZoneManager::SharedPtr( new Zone( Id, 115, 48, "The Twilight Zone" ) ); break;
                case 115: newZone = ZoneManager::SharedPtr( new Zone( Id, 115, 48, "The Dead Zone" ) );
                    newZone->addChildId(110);
                    break;
            }

            if (newZone)
            {
                zones[Id] = newZone;
            }
        }
    };

    ZoneManager zoneManager;

    simple_zone_setup()
        :   zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) )
    {
    }
};


BOOST_FIXTURE_TEST_SUITE(test_ZoneManager_simple, simple_zone_setup)

BOOST_AUTO_TEST_CASE(test_ZoneManager_default_initialization)
{
    ZoneManager::SharedPtr zone = zoneManager.getZone(100);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );

    zone = zoneManager.getZone(110);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );

    zone = zoneManager.getZone(115);

    BOOST_CHECK_EQUAL(  -1,  zone->getId() );
}

BOOST_AUTO_TEST_CASE(test_ZoneManager_default_initialization_reloadAll_unloadAll)
{
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
    zoneManager.reloadAll();

    Zone::IdSet subset = zoneManager.getZoneIdsBySubbus(48);

    BOOST_CHECK_EQUAL(  2,  subset.size() );

    BOOST_CHECK_EQUAL(110,  *subset.begin() );

    BOOST_CHECK_EQUAL(115,  *subset.rbegin() );
}


BOOST_AUTO_TEST_CASE(test_ZoneManager_get_all_child_zones)
{
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
    zoneManager.reloadAll();

    BOOST_CHECK_EQUAL(  115,  zoneManager.getRootZoneIdForSubbus(48) );
}

BOOST_AUTO_TEST_SUITE_END()


struct complex_zone_setup
{
    class ZoneUnitTestLoader : public ZoneLoader
    {
    public:

        // default construction and destruction is OK

        virtual ZoneManager::ZoneMap load(const long Id)
        {
            ZoneManager::ZoneMap zones;

            if (Id < 0)
            {
                long Ids[] = { 101, 103, 104, 106, 107, 108, 109 };

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
            ZoneManager::SharedPtr newZone;

            switch (Id)
            {
                case 101: newZone = ZoneManager::SharedPtr( new Zone( Id, 101, 35, "The Root Zone" ) );         break;
                case 103: newZone = ZoneManager::SharedPtr( new Zone( Id, 101, 35, "The Left Zone" ) );         break;
                case 104: newZone = ZoneManager::SharedPtr( new Zone( Id, 103, 35, "The Left Right Zone" ) );   break;
                case 106: newZone = ZoneManager::SharedPtr( new Zone( Id, 107, 35, "The Right Left Zone" ) );   break;
                case 107: newZone = ZoneManager::SharedPtr( new Zone( Id, 101, 35, "The Right Zone" ) );        break;
                case 108: newZone = ZoneManager::SharedPtr( new Zone( Id, 107, 35, "The Right Right Zone" ) );  break;
                case 109: newZone = ZoneManager::SharedPtr( new Zone( Id, 103, 35, "The Left Left Zone" ) );    break;
            }

            if (newZone)
            {
                zones[Id] = newZone;
            }
        }
    };

    ZoneManager zoneManager;

    complex_zone_setup()
        :   zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) )
    {
        zoneManager.reloadAll();

    }
};


BOOST_FIXTURE_TEST_SUITE(test_ZoneManager_complex, complex_zone_setup)

BOOST_AUTO_TEST_CASE(test_ZoneManager_zone_subsets_by_subbus)
{
    Zone::IdSet results
        = boost::assign::list_of
            ( 101 )
            ( 103 )
            ( 104 )
            ( 106 )
            ( 107 )
            ( 108 )
            ( 109 )
        ;

    Zone::IdSet subset = zoneManager.getZoneIdsBySubbus( 35 );

    BOOST_CHECK_EQUAL_RANGES( results, subset );
}


BOOST_AUTO_TEST_CASE(test_ZoneManager_all_children_of_zone)
{
    // Get all children of ID 101
    {
        Zone::IdSet results
            = boost::assign::list_of
                ( 103 )
                ( 104 )
                ( 106 )
                ( 107 )
                ( 108 )
                ( 109 )
            ;

        Zone::IdSet subset = zoneManager.getAllChildrenOfZone( 101 );

        BOOST_CHECK_EQUAL_RANGES( results, subset );
    }

    // Get all children of ID 103
    {
        Zone::IdSet results
            = boost::assign::list_of
                ( 104 )
                ( 109 )
            ;

        Zone::IdSet subset = zoneManager.getAllChildrenOfZone( 103 );

        BOOST_CHECK_EQUAL_RANGES( results, subset );
    }

    // Get all children of ID 107
    {
        Zone::IdSet results
            = boost::assign::list_of
                ( 106 )
                ( 108 )
            ;

        Zone::IdSet subset = zoneManager.getAllChildrenOfZone( 107 );

        BOOST_CHECK_EQUAL_RANGES( results, subset );
    }

    // Get all children of ID 104, 106, 108 and 109
    {
        Zone::IdSet emptySet;

        Zone::IdSet subset = zoneManager.getAllChildrenOfZone( 104 );

        BOOST_CHECK_EQUAL_RANGES( emptySet, subset );

        subset = zoneManager.getAllChildrenOfZone( 106 );

        BOOST_CHECK_EQUAL_RANGES( emptySet, subset );

        subset = zoneManager.getAllChildrenOfZone( 108 );

        BOOST_CHECK_EQUAL_RANGES( emptySet, subset );

        subset = zoneManager.getAllChildrenOfZone( 109 );

        BOOST_CHECK_EQUAL_RANGES( emptySet, subset );
    }
}


BOOST_AUTO_TEST_CASE(test_ZoneManager_all_children_of_zone_with_zone_reload_and_reconfiguration)
{
    Zone::IdSet results
        = boost::assign::list_of
            ( 101 )
            ( 103 )
            ( 104 )
            ( 106 )
            ( 107 )
            ( 108 )
            ( 109 )
        ;

    Zone::IdSet subset = zoneManager.getZoneIdsBySubbus( 35 );

    BOOST_CHECK_EQUAL_RANGES( results, subset );

    results.erase( 108 );

    zoneManager.unload( 108 );

    subset = zoneManager.getZoneIdsBySubbus( 35 );

    BOOST_CHECK_EQUAL_RANGES( results, subset );
}

BOOST_AUTO_TEST_SUITE_END()

