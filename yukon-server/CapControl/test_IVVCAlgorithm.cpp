

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
                IVVCStrategy* strat = new IVVCStrategy();
                strat->setPointDataRequestFactory(requestFactory);
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

    std::list<long>  emptyWatchlist, watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointID = 1100; pointID < 1105; ++pointID)
    {
        watchlist.push_back(pointID);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints( watchlist ) );

    // We can send it more watchlists but it will not modify/replace the first valid one we load

    BOOST_CHECK_EQUAL( false , request->watchPoints( emptyWatchlist ) );

    // Add 5 more point IDs to the watchlist

    for (long pointID = 1110; pointID < 1115; ++pointID)
    {
        watchlist.push_back(pointID);
    }

    BOOST_CHECK_EQUAL( false , request->watchPoints( watchlist ) );

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

