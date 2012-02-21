#include <boost/test/auto_unit_test.hpp>

#include "PointDataRequestFactory.h"
#include "DispatchPointdataRequest.h"

BOOST_AUTO_TEST_SUITE( test_PointDataRequest )

// mock dispatch connection to test the point data request functionality
class mock_DispatchConnection : public DispatchConnection
{
public:

    mock_DispatchConnection() : DispatchConnection("mock_DispatchConnection", -1, "localhost") {  }
};

BOOST_AUTO_TEST_CASE(test_point_data_request_factory)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection ) );

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    std::set<PointRequest>  emptyWatchlist, watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointId = 1100; pointId < 1105; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints(watchlist) );

    // We can send it more watchlists but it will not modify/replace the first valid one we load

    BOOST_CHECK_EQUAL( false , request->watchPoints(emptyWatchlist) );

    // Add 5 more point IDs to the watchlist

    for (long pointId = 1110; pointId < 1115; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( false , request->watchPoints(watchlist) );

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

    //because this point is abnormal and timestamp > previous 1102 pData, will be removed from values, added to rejected
    request->processNewMessage( new CtiPointDataMsg( 1102, 116.7, UnknownQuality, AnalogPointType ) );

    request->processNewMessage( new CtiPointDataMsg( 1104, 122.0, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg( 1100, 120.5, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( true , request->isComplete() );

    PointValueMap   receivedPoints = request->getPointValues();

    // Make sure the map is full

    BOOST_CHECK_EQUAL( 4, receivedPoints.size() );

    PointValueMap   rejectedPoints = request->getRejectedPointValues();
    BOOST_CHECK_EQUAL( 1, rejectedPoints.size() );

    // Test the values received against what was sent.  Note that ID = 1104 got
    //  multiple updates, make sure the last one received is the one we kept.
    //  ID = 1102 got a point with unknown quality - make sure this one is ignored.

    BOOST_CHECK_CLOSE( 120.5 , receivedPoints[1100].value , 0.0001 );
    BOOST_CHECK_CLOSE( 121.3 , receivedPoints[1101].value , 0.0001 );
    BOOST_CHECK_CLOSE( 116.7 , rejectedPoints[1102].value , 0.0001 );
    BOOST_CHECK_CLOSE( 120.2 , receivedPoints[1103].value , 0.0001 );
    BOOST_CHECK_CLOSE( 122.0 , receivedPoints[1104].value , 0.0001 );

    // We also want to make sure we didn't save the points we got that were not
    //  on our watchlist.

    BOOST_CHECK( receivedPoints.end() == receivedPoints.find(950) );
    BOOST_CHECK( receivedPoints.end() == receivedPoints.find(1112) );
}

BOOST_AUTO_TEST_SUITE_END()
