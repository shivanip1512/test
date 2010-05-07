

#define BOOST_AUTO_TEST_MAIN "Test CapControl IVVC Algorithm"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <iostream>
#include <time.h>
#include <sstream>    // for istringstream
#include <locale>

#include "yukon.h"
#include "ctitime.h"
#include "ccsubstationbus.h"
#include "ccsubstation.h"
#include "ccarea.h"
#include "capcontroller.h"
#include "ccsubstationbusstore.h"
#include "ccexecutor.h"
#include "ccmessage.h"
#include "mgr_paosched.h"
#include "pointdefs.h"
#include "ccoriginalparent.h"
#include "ccUnitTestUtil.h"

#include "StrategyManager.h"
#include "IVVCStrategy.h"

#include "DispatchPointDataRequest.h"

using boost::unit_test_framework::test_suite;
using namespace std;

extern ULONG _MAX_KVAR;
extern ULONG _SEND_TRIES;

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


BOOST_AUTO_TEST_CASE(test_point_data_request_factory)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection ) );

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    std::set<long>  emptyWatchlist, watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointID = 1100; pointID < 1105; ++pointID)
    {
        watchlist.insert(pointID);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints( watchlist,emptyWatchlist ) );

    // We can send it more watchlists but it will not modify/replace the first valid one we load

    BOOST_CHECK_EQUAL( false , request->watchPoints( emptyWatchlist,emptyWatchlist ) );

    // Add 5 more point IDs to the watchlist

    for (long pointID = 1110; pointID < 1115; ++pointID)
    {
        watchlist.insert(pointID);
    }

    BOOST_CHECK_EQUAL( false , request->watchPoints( watchlist,emptyWatchlist ) );

    // Receive a bunch of point data - complete should be false until we receive point data for all of the watched point IDs.
    //  If we get multiple data points for the same ID - we only keep the last one received.

    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg(  950, 121.0, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg( 1101, 121.4, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg( 1104, 122.2, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg( 1101, 121.3, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1103, 120.2, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1102, 119.7, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1112, 118.9, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1104, 120.9, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1102, 116.7, UnknownQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1104, 122.0, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg( 1100, 120.5, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( true , request->isComplete() );

    PointValueMap   receivedPoints = request->getPointValues();

    // Make sure the map is full

    BOOST_CHECK_EQUAL( 5, receivedPoints.size() );

    // Test the values received against what was sent.  Note that ID = 1104 got
    //  multiple updates, make sure the last one received is the one we kept.
    //  ID = 1102 got a point with unknown quality - make sure this one is ignored.

    BOOST_CHECK_CLOSE( 120.5 , receivedPoints[1100].value , 0.0001 );
    BOOST_CHECK_CLOSE( 121.3 , receivedPoints[1101].value , 0.0001 );
    BOOST_CHECK_CLOSE( 119.7 , receivedPoints[1102].value , 0.0001 );
    BOOST_CHECK_CLOSE( 120.2 , receivedPoints[1103].value , 0.0001 );
    BOOST_CHECK_CLOSE( 122.0 , receivedPoints[1104].value , 0.0001 );

    // We also want to make sure we didn't save the points we got that were not
    //  on our watchlist.

    BOOST_CHECK( receivedPoints.end() == receivedPoints.find(950) );
    BOOST_CHECK( receivedPoints.end() == receivedPoints.find(1112) );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_power_factor_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        double test_calculatePowerFactor(const double varValue, const double wattValue)
        {
            return calculatePowerFactor(varValue, wattValue);
        }
    };

    test_IVVCAlgorithm  _algorithm;

    // check to 6 significant digits (rounded)

    BOOST_CHECK_CLOSE( 1.000000 , _algorithm.test_calculatePowerFactor(     0,  6000 ) , 0.0001 );

    BOOST_CHECK_CLOSE( 0.000000 , _algorithm.test_calculatePowerFactor( -1500,     0 ) , 0.0001 );
    BOOST_CHECK_CLOSE( 0.000000 , _algorithm.test_calculatePowerFactor( -1500,     0 ) , 0.0001 );

    BOOST_CHECK_CLOSE( 0.986394 , _algorithm.test_calculatePowerFactor(  1000,  6000 ) , 0.0001 );
    BOOST_CHECK_CLOSE( 0.986394 , _algorithm.test_calculatePowerFactor( -1000,  6000 ) , 0.0001 );

    BOOST_CHECK_CLOSE( 0.970143 , _algorithm.test_calculatePowerFactor(  1500,  6000 ) , 0.0001 );
    BOOST_CHECK_CLOSE( 0.970143 , _algorithm.test_calculatePowerFactor( -1500,  6000 ) , 0.0001 );

    BOOST_CHECK_CLOSE( 0.977189 , _algorithm.test_calculatePowerFactor(  1234,  5678 ) , 0.0001 );
    BOOST_CHECK_CLOSE( 0.977189 , _algorithm.test_calculatePowerFactor( -1234,  5678 ) , 0.0001 );

    BOOST_CHECK_CLOSE( 0.924276 , _algorithm.test_calculatePowerFactor(  2345,  5678 ) , 0.0001 );
    BOOST_CHECK_CLOSE( 0.924276 , _algorithm.test_calculatePowerFactor( -2345,  5678 ) , 0.0001 );

    BOOST_CHECK_CLOSE( 0.707107 , _algorithm.test_calculatePowerFactor(  6000,  6000 ) , 0.0001 );
    BOOST_CHECK_CLOSE( 0.707107 , _algorithm.test_calculatePowerFactor( -6000,  6000 ) , 0.0001 );
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

    BOOST_CHECK_CLOSE( -881.9 , _algorithm.test_calculateTargetPFVars( 125.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE(  881.9 , _algorithm.test_calculateTargetPFVars(  75.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE(  484.3 , _algorithm.test_calculateTargetPFVars(  90.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE( -484.3 , _algorithm.test_calculateTargetPFVars( 110.0, 1000.0) , 0.1 );

    BOOST_CHECK_CLOSE(    0.0 , _algorithm.test_calculateTargetPFVars( 100.0, 1000.0) , 0.1 );
}


BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_ltc_tap_operation_calculation)
{
    struct test_IVVCAlgorithm : public IVVCAlgorithm
    {
        test_IVVCAlgorithm() : IVVCAlgorithm( PointDataRequestFactoryPtr( new PointDataRequestFactory ) ) {  }

        int test_calculateVte(const PointValueMap &voltages, const double Vmin, const double Vrm, const double Vmax)
        {
            return calculateVte(voltages, Vmin, Vrm, Vmax);
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
        The fifth and sixth parameter are point IDs to ignore in the tap operation calculation.  Use this
         feature to generate different test values with the same set of data.
    */

    // Don't exclude any points - all voltages within limits, on both sides of the margin - no operation

    BOOST_CHECK_EQUAL(  0 , _algorithm.test_calculateVte( _voltages, 115.0, 119.0, 125.0 ) );

    // exclude the only point that is below the marginal voltage - should tap down

    //ignorePoints.insert(1006);
    _voltages.erase(1006);
    BOOST_CHECK_EQUAL( -1 , _algorithm.test_calculateVte( _voltages, 115.0, 119.0, 125.0 ) );

    // Don't exclude any points - single voltage over limit - should tap down
    //ignorePoints.clear();
    _value.value    = 118.2;
    _voltages[1006] = _value;
    BOOST_CHECK_EQUAL( -1 , _algorithm.test_calculateVte( _voltages, 115.0, 119.0, 122.0 ) );

    // exclude the only point that is above the max voltage - no operation
    //ignorePoints.insert(1002);
    _voltages.erase(1002);
    BOOST_CHECK_EQUAL(  0 , _algorithm.test_calculateVte( _voltages, 115.0, 119.0, 122.0 ) );


    // Don't exclude any points - single voltage over limit and single voltage under limit - should tap down
    //ignorePoints.clear();
    _value.value    = 122.1;
    _voltages[1002] = _value;
    BOOST_CHECK_EQUAL( -1 , _algorithm.test_calculateVte( _voltages, 119.0, 120.0, 122.0 ) );

    // exclude the point above the max voltage - should tap up
    //ignorePoints.insert(1002);
    _voltages.erase(1002);
    BOOST_CHECK_EQUAL(  1 , _algorithm.test_calculateVte( _voltages, 119.0, 120.0, 122.0 ) );

    // exclude the point below the min voltage - should tap down
    //ignorePoints.clear();
    _value.value    = 122.1;
    _voltages[1002] = _value;

    //ignorePoints.insert(1006);
    _voltages.erase(1006);
    BOOST_CHECK_EQUAL( -1 , _algorithm.test_calculateVte( _voltages, 119.0, 120.0, 122.0 ) );

    // exclude the points above the max voltage and below min - no operation
    //ignorePoints.insert(1002);
    _voltages.erase(1002);
    BOOST_CHECK_EQUAL(  0 , _algorithm.test_calculateVte( _voltages, 119.0, 120.0, 122.0 ) );
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

