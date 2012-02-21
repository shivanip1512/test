#include <boost/test/unit_test.hpp>

#include "ccUnitTestUtil.h"

#include "IVVCStrategy.h"
#include "ZoneManager.h"

using Cti::CapControl::Zone;
using Cti::CapControl::ZoneLoader;
using Cti::CapControl::ZoneManager;

extern ULONG _MAX_KVAR;
extern ULONG _SEND_TRIES;

BOOST_AUTO_TEST_SUITE( test_IVVCAlgorithm )

class StrategyUnitTestLoader : public StrategyLoader
{

public:

    StrategyUnitTestLoader()
    {
        requestFactory.reset(new PointDataRequestFactory);
    }

    virtual StrategyManager::StrategyMap load(const long ID)
    {
        StrategyManager::StrategyMap loaded;

        if (ID < 0)
        {
            long IDs[] = { 100 };

            for (int i = 0; i < sizeof(IDs)/ sizeof(*IDs); i++)
            {
                loadSingle(IDs[i], loaded);
            }
        }
        else
        {
            loadSingle(ID, loaded);
        }

        return loaded;
    }

private:

    PointDataRequestFactoryPtr requestFactory;

    void loadSingle(const long ID, StrategyManager::StrategyMap &strategies)
    {
        bool doInsertion = true;

        StrategyManager::SharedPtr  newStrategy;

        switch (ID)
        {
            case 100:
            {
                IVVCStrategy* strat = new IVVCStrategy(requestFactory);
                strat->setStrategyName("Test IVVC Strategy");
                newStrategy.reset(strat);
                break;
            }
            default:
            {
                doInsertion = false;
                break;
            }
        }

        if (doInsertion)
        {
            newStrategy->setStrategyId(ID);
            strategies[ID] = newStrategy;
        }
    }
};


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
        bool doInsertion = true;
        ZoneManager::SharedPtr newZone;

        switch (Id)
        {
            case 101:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 101, 35, "The Root Zone" ) );
                break;
            }
            case 103:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 101, 35, "The Left Zone" ) );
                break;
            }
            case 104:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 103, 35, "The Left Right Zone" ) );
                break;
            }
            case 106:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 107, 35, "The Right Left Zone" ) );
                break;
            }
            case 107:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 101, 35, "The Right Zone" ) );
                break;
            }
            case 108:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 107, 35, "The Right Right Zone" ) );
                break;
            }
            case 109:
            {
                newZone = ZoneManager::SharedPtr( new Zone( Id, 103, 35, "The Left Left Zone" ) );
                break;
            }

            default:  doInsertion = false;
        }

        if (doInsertion)
        {
            zones[Id] = newZone;
        }
    }
};


void initialize_area(Test_CtiCCSubstationBusStore* store, CtiCCArea* area)
{
    store->insertAreaToPaoMap(area);
    area->setDisableFlag(FALSE);
}


void initialize_station(Test_CtiCCSubstationBusStore* store, CtiCCSubstation* station, CtiCCArea* parentArea)
{
    station->setSaEnabledFlag(FALSE);
    station->setParentId(parentArea->getPaoId());
    parentArea->getSubStationList()->push_back(station->getPaoId());
    store->insertSubstationToPaoMap(station);
    station->setDisableFlag(FALSE);
}


void initialize_bus(Test_CtiCCSubstationBusStore* store, CtiCCSubstationBus* bus, CtiCCSubstation* parentStation)
{
    bus->setParentId(parentStation->getPaoId());
    bus->setEventSequence(22);
    bus->setCurrentVarLoadPointId(1);
    bus->setCurrentVarLoadPointValue(55, CtiTime());
    bus->setVerificationFlag(FALSE);
    parentStation->getCCSubIds()->push_back(bus->getPaoId());
    store->insertSubBusToPaoMap(bus);
    bus->setDisableFlag(FALSE);
    bus->setVerificationFlag(FALSE);
    bus->setPerformingVerificationFlag(FALSE);
    bus->setVerificationDoneFlag(FALSE);
}


void initialize_feeder(Test_CtiCCSubstationBusStore* store, CtiCCFeeder* feed, CtiCCSubstationBus* parentBus, long displayOrder)
{

    long feederId = feed->getPaoId();
    long busId = parentBus->getPaoId();
    feed->setParentId(busId);
    feed->setDisplayOrder(displayOrder);
    parentBus->getCCFeeders().push_back(feed);
    store->insertItemsIntoMap(CtiCCSubstationBusStore::FeederIdSubBusIdMap, &feederId, &busId);
    store->insertFeederToPaoMap(feed);
    feed->setDisableFlag(FALSE);
    feed->setVerificationFlag(FALSE);
    feed->setPerformingVerificationFlag(FALSE);
    feed->setVerificationDoneFlag(FALSE);

    feed->setStrategy( -1 );        // init to NoStrategy

    feed->setCurrentVarPointQuality(NormalQuality);
    feed->setWaitForReCloseDelayFlag(false);

}


void initialize_capbank(Test_CtiCCSubstationBusStore* store, CtiCCCapBank* cap, CtiCCFeeder* parentFeed, long displayOrder)
{
    long bankId = cap->getPaoId();
    long fdrId = parentFeed->getPaoId();
    cap->setParentId(fdrId);
    cap->setControlOrder(displayOrder);
    cap->setCloseOrder(displayOrder);
    cap->setTripOrder(displayOrder);
    parentFeed->getCCCapBanks().push_back(cap);
    store->insertItemsIntoMap(CtiCCSubstationBusStore::CapBankIdFeederIdMap, &bankId, &fdrId);
    cap->setOperationalState(CtiCCCapBank::SwitchedOperationalState);
    cap->setDisableFlag(FALSE);
    cap->setVerificationFlag(FALSE);
    cap->setPerformingVerificationFlag(FALSE);
    cap->setVerificationDoneFlag(FALSE);
    cap->setBankSize(600);

    cap->setControlPointId(1);
}

BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_voltage_flatness_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        double test_calculateVf(const PointValueMap &voltages)
        {
            return calculateVf(voltages);
        }
    };

    std::set<long> ignorePoints;
    test_IVVCAlgorithm  _algorithm;

    PointValue    _value;
    PointValueMap _voltages;

    _value.quality   = NormalQuality;
    _value.timestamp = CtiTime();

    _value.value    = 120.0;
    _voltages[1000] = _value;

    _value.value    = 121.2;
    _voltages[1001] = _value;

    _value.value    = 122.1;
    _voltages[1002] = _value;

    _value.value    = 119.6;
    _voltages[1003] = _value;

    _value.value    = 120.3;
    _voltages[1004] = _value;

    _value.value    = 119.8;
    _voltages[1005] = _value;

    _value.value    = 118.2;
    _voltages[1006] = _value;

    _value.value    = 121.9;
    _voltages[1007] = _value;

    /*
        The second and third parameter are point IDs to ignore in the flatness calculation.  Use this
         feature to generate different test values with the same set of data.
    */

    // check to 6 significant digits (rounded)

    // Don't exclude any points
    BOOST_CHECK_CLOSE( 2.187500 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );

    // Exclude a single point
    //ignorePoints.insert(1000);
    _voltages.erase(1000);
    BOOST_CHECK_CLOSE( 2.242857 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );
    BOOST_CHECK_CLOSE( 2.242857 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );

    //ignorePoints.clear();
    _value.value    = 120.0;
    _voltages[1000] = _value;

    //ignorePoints.insert(1002);
    _voltages.erase(1002);
    BOOST_CHECK_CLOSE( 1.942857 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );
    BOOST_CHECK_CLOSE( 1.942857 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );

    // Exclude two points
    //ignorePoints.insert(1000);
    _voltages.erase(1000);
    BOOST_CHECK_CLOSE( 1.966667 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );
    BOOST_CHECK_CLOSE( 1.966667 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );

    //ignorePoints.clear();
    _value.value    = 120.0;
    _voltages[1000] = _value;
    _value.value    = 122.1;
    _voltages[1002] = _value;

    //ignorePoints.insert(1006);
    _voltages.erase(1006);
    //ignorePoints.insert(1007);
    _voltages.erase(1007);
    BOOST_CHECK_CLOSE( 0.900000 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );
    BOOST_CHECK_CLOSE( 0.900000 , _algorithm.test_calculateVf( _voltages ) , 0.0001 );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_bus_weight_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        double test_calculateBusWeight(const double Kv, const double Vf, const double Kp, const double powerFactor)
        {
            return calculateBusWeight(Kv, Vf, Kp, powerFactor, 1.0);    // Tying the last param to 1.0...
        }
    };

    test_IVVCAlgorithm  _algorithm;

    // check to 6 significant digits (rounded)

    BOOST_CHECK_CLOSE( 0.500000 , _algorithm.test_calculateBusWeight( 0.500000, 1.000000, 0.500000, 1.000000 ) , 0.0001 );

    BOOST_CHECK_CLOSE( 2.000000 , _algorithm.test_calculateBusWeight( 0.500000, 1.000000, 0.500000, 0.970000 ) , 0.0001 );

    BOOST_CHECK_CLOSE( 2.625000 , _algorithm.test_calculateBusWeight( 0.250000, 2.500000, 0.500000, 0.960000 ) , 0.0001 );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_target_power_factor_var_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        double test_calculateTargetPFVars(const double targetPF, const double wattValue)
        {
            return calculateTargetPFVars(targetPF, wattValue);
        }
    };

    test_IVVCAlgorithm  _algorithm;

    // check to 1 significant digits (rounded)

    BOOST_CHECK_CLOSE( -881.9 , _algorithm.test_calculateTargetPFVars( -75.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE(  881.9 , _algorithm.test_calculateTargetPFVars(  75.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE(  484.3 , _algorithm.test_calculateTargetPFVars(  90.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE( -484.3 , _algorithm.test_calculateTargetPFVars( -90.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE(    0.0 , _algorithm.test_calculateTargetPFVars( 100.0, 1000.0) , 0.1 );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_regulator_tap_operation_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        using IVVCAlgorithm::calculateVte;
    };

    test_IVVCAlgorithm  _algorithm;
    std::map<long, CtiCCMonitorPointPtr>    monitorMap;
    IVVCStrategy    strategy( PointDataRequestFactoryPtr( new PointDataRequestFactory ) );

    PointValue    _value;
    PointValueMap _voltages;

    _value.quality   = NormalQuality;
    _value.timestamp = CtiTime();

    _value.value    = 120.0;
    _voltages[1000] = _value;

    _value.value    = 121.2;
    _voltages[1001] = _value;

    _value.value    = 122.1;
    _voltages[1002] = _value;

    _value.value    = 119.6;
    _voltages[1003] = _value;

    _value.value    = 120.3;
    _voltages[1004] = _value;

    _value.value    = 119.8;
    _voltages[1005] = _value;

    _value.value    = 118.2;
    _voltages[1006] = _value;

    _value.value    = 121.9;
    _voltages[1007] = _value;

    strategy.restoreParameters("Lower Volt Limit", "PEAK", "115.0");
    strategy.restoreParameters("Voltage Regulation Margin", "PEAK", "4.0");
    strategy.restoreParameters("Upper Volt Limit", "PEAK", "125.0");
    /*
        The fifth and sixth parameter are point IDs to ignore in the tap operation calculation.  Use this
         feature to generate different test values with the same set of data.
    */

    // Don't exclude any points - all voltages within limits, on both sides of the margin - no operation

    BOOST_CHECK_EQUAL(  0 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // exclude the only point that is below the marginal voltage - should tap down
    _voltages.erase(1006);

    BOOST_CHECK_EQUAL( -1 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // Don't exclude any points - single voltage over limit - should tap down
    _value.value    = 118.2;
    _voltages[1006] = _value;
    strategy.restoreParameters("Upper Volt Limit", "PEAK", "122.0");

    BOOST_CHECK_EQUAL( -1 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // exclude the only point that is above the max voltage - no operation
    _voltages.erase(1002);

    BOOST_CHECK_EQUAL(  0 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // Don't exclude any points - single voltage over limit and single voltage under limit - should tap down
    _value.value    = 122.1;
    _voltages[1002] = _value;

    strategy.restoreParameters("Lower Volt Limit", "PEAK", "119.0");
    strategy.restoreParameters("Voltage Regulation Margin", "PEAK", "1.0");

    BOOST_CHECK_EQUAL( -1 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // exclude the point above the max voltage - should tap up
    _voltages.erase(1002);

    BOOST_CHECK_EQUAL(  1 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // exclude the point below the min voltage - should tap down
    _value.value    = 122.1;
    _voltages[1002] = _value;

    _voltages.erase(1006);

    BOOST_CHECK_EQUAL( -1 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );

    // exclude the points above the max voltage and below min - no operation
    _voltages.erase(1002);

    BOOST_CHECK_EQUAL(  0 , _algorithm.calculateVte( _voltages, &strategy, monitorMap, true ) );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm)
{
    Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance(store);

    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );
    _strategyManager.reloadAll();

    CtiCCArea           *area     = create_object<CtiCCArea>(1, "Area-1");
    CtiCCSubstation     *station  = create_object<CtiCCSubstation>(2, "Substation-A");
    CtiCCSubstationBus  *bus      = create_object<CtiCCSubstationBus>(3, "SubBus-A1");
    CtiCCFeeder         *feeder   = create_object<CtiCCFeeder>(11, "Feeder");
    CtiCCCapBank        *capbank1 = create_object<CtiCCCapBank>(14, "Capbank1");
    CtiCCCapBank        *capbank2 = create_object<CtiCCCapBank>(15, "Capbank2");
    CtiCCCapBank        *capbank3 = create_object<CtiCCCapBank>(16, "Capbank3");

    Test_CtiCapController *controller = new Test_CtiCapController();
    CtiCapController::setInstance(controller);

    initialize_area(store, area);
    initialize_station(store, station, area);
    initialize_bus(store, bus, station);

    initialize_feeder(store, feeder, bus, 1);

    initialize_capbank(store, capbank1, feeder, 1);
    initialize_capbank(store, capbank2, feeder, 2);
    initialize_capbank(store, capbank3, feeder, 3);

    bus->setStrategyManager( &_strategyManager );
    bus->setStrategy(100);

    // END of setup....





    BOOST_CHECK( true );        // something here so hudson doesn't choke on an empty unit test



    store->deleteInstance();

}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_zone_subsets_by_subbus)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    Zone::IdSet results;

    results.insert(101);
    results.insert(103);
    results.insert(104);
    results.insert(106);
    results.insert(107);
    results.insert(108);
    results.insert(109);

    Zone::IdSet subset = zoneManager.getZoneIdsBySubbus(35);

    BOOST_CHECK_EQUAL_COLLECTIONS( results.begin(), results.end(), subset.begin(), subset.end() );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_all_children_of_zone)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    // Get all children of ID 101

    Zone::IdSet results101;

    results101.insert(103);
    results101.insert(104);
    results101.insert(106);
    results101.insert(107);
    results101.insert(108);
    results101.insert(109);

    Zone::IdSet subset101 = zoneManager.getAllChildrenOfZone(101);

    BOOST_CHECK_EQUAL_COLLECTIONS( results101.begin(), results101.end(), subset101.begin(), subset101.end() );

    // Get all children of ID 103

    Zone::IdSet results103;

    results103.insert(104);
    results103.insert(109);

    Zone::IdSet subset103 = zoneManager.getAllChildrenOfZone(103);

    BOOST_CHECK_EQUAL_COLLECTIONS( results103.begin(), results103.end(), subset103.begin(), subset103.end() );

    // Get all children of ID 107

    Zone::IdSet results107;

    results107.insert(106);
    results107.insert(108);

    Zone::IdSet subset107 = zoneManager.getAllChildrenOfZone(107);

    BOOST_CHECK_EQUAL_COLLECTIONS( results107.begin(), results107.end(), subset107.begin(), subset107.end() );

    // Get all children of ID 104, 106, 108 and 109

    Zone::IdSet emptySet;

    Zone::IdSet subset104 = zoneManager.getAllChildrenOfZone(104);

    BOOST_CHECK_EQUAL_COLLECTIONS( emptySet.begin(), emptySet.end(), subset104.begin(), subset104.end() );

    Zone::IdSet subset106 = zoneManager.getAllChildrenOfZone(106);

    BOOST_CHECK_EQUAL_COLLECTIONS( emptySet.begin(), emptySet.end(), subset106.begin(), subset106.end() );

    Zone::IdSet subset108 = zoneManager.getAllChildrenOfZone(108);

    BOOST_CHECK_EQUAL_COLLECTIONS( emptySet.begin(), emptySet.end(), subset108.begin(), subset108.end() );

    Zone::IdSet subset109 = zoneManager.getAllChildrenOfZone(109);

    BOOST_CHECK_EQUAL_COLLECTIONS( emptySet.begin(), emptySet.end(), subset109.begin(), subset109.end() );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_all_children_of_zone_with_zone_reload_and_reconfiguration)
{
    ZoneManager zoneManager( std::auto_ptr<ZoneUnitTestLoader>( new ZoneUnitTestLoader ) );

    zoneManager.reloadAll();

    Zone::IdSet results;

    results.insert(101);
    results.insert(103);
    results.insert(104);
    results.insert(106);
    results.insert(107);
    results.insert(108);
    results.insert(109);

    Zone::IdSet subset = zoneManager.getZoneIdsBySubbus(35);

    BOOST_CHECK_EQUAL_COLLECTIONS( results.begin(), results.end(), subset.begin(), subset.end() );

    results.erase(108);

    zoneManager.unload(108);

    subset = zoneManager.getZoneIdsBySubbus(35);

    BOOST_CHECK_EQUAL_COLLECTIONS( results.begin(), results.end(), subset.begin(), subset.end() );
}

BOOST_AUTO_TEST_SUITE_END()
