#include <boost/test/unit_test.hpp>
#include "PointDataRequestFactory.h"
#include "DispatchPointdataRequest.h"
#include "ctidate.h"

#include "amq_constants.h"

BOOST_AUTO_TEST_SUITE( test_PointDataRequest )

// mock dispatch connection to test the point data request functionality
class mock_DispatchConnection : public DispatchConnection
{
public:

    mock_DispatchConnection() : DispatchConnection("mock_DispatchConnection" ) {  }

    virtual void writeIncomingMessageToQueue(CtiMessage *msgPtr) override {};
};


BOOST_AUTO_TEST_CASE(test_point_data_request_factory_watchlist_creation)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection()));

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    BOOST_REQUIRE( request );

    std::set<PointRequest>  emptyWatchlist, watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointId = 1100; pointId < 1105; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( 0 , request->getMissingPoints().size() );

    BOOST_CHECK_EQUAL( true , request->watchPoints(watchlist) );

    BOOST_CHECK_EQUAL( 5 , request->getMissingPoints().size() );

    // We can send it more watchlists but it will not modify/replace the first valid one we load

    BOOST_CHECK_EQUAL( false , request->watchPoints(emptyWatchlist) );

    BOOST_CHECK_EQUAL( 5 , request->getMissingPoints().size() );

    // Add 5 more point IDs to the watchlist

    for (long pointId = 1110; pointId < 1115; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( false , request->watchPoints(watchlist) );

    BOOST_CHECK_EQUAL( 5 , request->getMissingPoints().size() );
}


BOOST_AUTO_TEST_CASE(test_point_data_request_factory_ignore_points_not_on_watchlist)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection()));

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    BOOST_REQUIRE( request );

    std::set<PointRequest>  watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointId = 1100; pointId < 1105; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints(watchlist) );

    // Send a bunch of point data outside our pointId range [1100 <= pointId <= 1104]

    for ( int i = 0; i < 10; i++ )
    {
        request->processNewMessage( new CtiPointDataMsg( i + 950, 120.0, NormalQuality, AnalogPointType ) );
    }

    BOOST_CHECK_EQUAL( 0 , request->getPointValues().size() );
    BOOST_CHECK_EQUAL( 0 , request->getRejectedPointValues().size() );
    BOOST_CHECK_EQUAL( 5 , request->getMissingPoints().size() );
}


BOOST_AUTO_TEST_CASE(test_point_data_request_factory_all_good_data)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection()));

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    BOOST_REQUIRE( request );

    std::set<PointRequest>  watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointId = 1100; pointId < 1105; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints(watchlist) );

    // Receive a bunch of point data - complete should be false until we receive point data for all of the watched point IDs.
    //  If we get multiple data points for the same ID - we only keep the last one received.

    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg( 1100, 120.0, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg( 1101, 120.1, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg( 1102, 120.2, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg( 1103, 120.3, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( false , request->isComplete() );

    request->processNewMessage( new CtiPointDataMsg( 1104, 120.4, NormalQuality, AnalogPointType ) );
    BOOST_CHECK_EQUAL( true , request->isComplete() );

    BOOST_CHECK_EQUAL( 0 , request->getRejectedPointValues().size() );

    PointValueMap   receivedPoints = request->getPointValues();

    BOOST_CHECK_EQUAL( 5 , receivedPoints.size() );
    BOOST_CHECK_CLOSE( 120.0 , receivedPoints[1100].value , 0.0001 );
    BOOST_CHECK_CLOSE( 120.1 , receivedPoints[1101].value , 0.0001 );
    BOOST_CHECK_CLOSE( 120.2 , receivedPoints[1102].value , 0.0001 );
    BOOST_CHECK_CLOSE( 120.3 , receivedPoints[1103].value , 0.0001 );
    BOOST_CHECK_CLOSE( 120.4 , receivedPoints[1104].value , 0.0001 );
}


BOOST_AUTO_TEST_CASE(test_point_data_request_factory_all_good_data_for_one_point)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection()));

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    BOOST_REQUIRE( request );

    std::set<PointRequest>  watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointId = 1100; pointId < 1105; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints(watchlist) );

    request->processNewMessage( new CtiPointDataMsg( 1100, 120.0, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.1, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.2, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.3, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.4, NormalQuality, AnalogPointType ) );

    BOOST_CHECK_EQUAL( false , request->isComplete() );

    BOOST_CHECK_EQUAL( 0 , request->getRejectedPointValues().size() );

    PointValueMap   receivedPoints = request->getPointValues();

    BOOST_CHECK_EQUAL( 1 , receivedPoints.size() );
    BOOST_CHECK_CLOSE( 120.4 , receivedPoints[1100].value , 0.0001 );
}


BOOST_AUTO_TEST_CASE(test_point_data_request_factory_mixed_data_for_one_point_1)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection()));

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    BOOST_REQUIRE( request );

    std::set<PointRequest>  watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointId = 1100; pointId < 1105; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints(watchlist) );

    request->processNewMessage( new CtiPointDataMsg( 1100, 120.0, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.1, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.2, UnknownQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.3, UnknownQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.4, UnknownQuality, AnalogPointType ) );

    BOOST_CHECK_EQUAL( false , request->isComplete() );

    BOOST_CHECK_EQUAL( 0 , request->getRejectedPointValues().size() );

    PointValueMap   receivedPoints = request->getPointValues();

    BOOST_CHECK_EQUAL( 1 , receivedPoints.size() );
    BOOST_CHECK_CLOSE( 120.1 , receivedPoints[1100].value , 0.0001 );
}


BOOST_AUTO_TEST_CASE(test_point_data_request_factory_mixed_data_for_one_point_2)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection()));

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    BOOST_REQUIRE( request );

    std::set<PointRequest>  watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointId = 1100; pointId < 1105; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints(watchlist) );

    request->processNewMessage( new CtiPointDataMsg( 1100, 120.0, UnknownQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.1, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.2, UnknownQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.3, NormalQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.4, UnknownQuality, AnalogPointType ) );

    BOOST_CHECK_EQUAL( false , request->isComplete() );

    BOOST_CHECK_EQUAL( 0 , request->getRejectedPointValues().size() );

    PointValueMap   receivedPoints = request->getPointValues();

    BOOST_CHECK_EQUAL( 1 , receivedPoints.size() );
    BOOST_CHECK_CLOSE( 120.3 , receivedPoints[1100].value , 0.0001 );
}


BOOST_AUTO_TEST_CASE(test_point_data_request_factory_all_bad_data_for_one_point)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection()));

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    BOOST_REQUIRE( request );

    std::set<PointRequest>  watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointId = 1100; pointId < 1105; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints(watchlist) );

    request->processNewMessage( new CtiPointDataMsg( 1100, 120.0, UnknownQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.1, UnknownQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.2, UnknownQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.3, UnknownQuality, AnalogPointType ) );
    request->processNewMessage( new CtiPointDataMsg( 1100, 120.4, UnknownQuality, AnalogPointType ) );

    BOOST_CHECK_EQUAL( false , request->isComplete() );

    BOOST_CHECK_EQUAL( 0 , request->getPointValues().size() );

    PointValueMap   rejectedPoints = request->getRejectedPointValues();

    BOOST_CHECK_EQUAL( 1 , rejectedPoints.size() );
    BOOST_CHECK_CLOSE( 120.4 , rejectedPoints[1100].value , 0.0001 );
}


BOOST_AUTO_TEST_CASE(test_point_data_request_factory_complete_data)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection()));

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    BOOST_REQUIRE( request );

    std::set<PointRequest>  watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointId = 1100; pointId < 1105; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints(watchlist) );

    // Receive a bunch of point data - complete should be false until we receive point data for all of the watched point IDs.
    //  If we get multiple data points for the same ID - we only keep the last one received.

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

    // check results

    PointValueMap   rejectedPoints = request->getRejectedPointValues();

    BOOST_CHECK_EQUAL( 0, rejectedPoints.size() );

    PointValueMap   receivedPoints = request->getPointValues();

    BOOST_CHECK_EQUAL( 5, receivedPoints.size() );

    BOOST_CHECK_CLOSE( 120.5 , receivedPoints[1100].value , 0.0001 );
    BOOST_CHECK_CLOSE( 121.3 , receivedPoints[1101].value , 0.0001 );
    BOOST_CHECK_CLOSE( 119.7 , receivedPoints[1102].value , 0.0001 );
    BOOST_CHECK_CLOSE( 120.2 , receivedPoints[1103].value , 0.0001 );
    BOOST_CHECK_CLOSE( 122.0 , receivedPoints[1104].value , 0.0001 );
}


BOOST_AUTO_TEST_CASE(test_point_data_request_factory_incomplete_data)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection()));

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    BOOST_REQUIRE( request );

    std::set<PointRequest>  watchlist;

    // Add 5 point IDs to the watchlist

    for (long pointId = 1100; pointId < 1105; ++pointId)
    {
        PointRequest pr(pointId,OtherRequestType);
        watchlist.insert(pr);
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints(watchlist) );

    // Receive a bunch of point data - complete should be false until we receive point data for all of the watched point IDs.
    //  If we get multiple data points for the same ID - we only keep the last one received.

    BOOST_CHECK_EQUAL( false , request->isComplete() );

    CtiDate day{ 1, 1, 1990 };

    CtiTime now = CtiTime( day, 0, 0, 0 );

    CtiPointDataMsg * Message1 = new CtiPointDataMsg( 1101, 121.4, NormalQuality, AnalogPointType );
    CtiPointDataMsg * Message2 = new CtiPointDataMsg( 1104, 122.2, NormalQuality, AnalogPointType );
    CtiPointDataMsg * Message3 = new CtiPointDataMsg( 1101, 121.3, NormalQuality, AnalogPointType );
    CtiPointDataMsg * Message4 = new CtiPointDataMsg( 1102, 119.7, UnknownQuality, AnalogPointType );
    CtiPointDataMsg * Message5 = new CtiPointDataMsg( 1104, 120.9, NormalQuality, AnalogPointType );
    CtiPointDataMsg * Message6 = new CtiPointDataMsg( 1104, 122.0, NormalQuality, AnalogPointType );

    // Update timestamps for proper logging and process messages

    Message1->setTime(now);
    Message2->setTime(now);
    Message3->setTime(now);
    Message4->setTime(now);
    Message5->setTime(now);
    Message6->setTime(now);

    request->processNewMessage(Message1);
    request->processNewMessage(Message2);
    request->processNewMessage(Message3);
    request->processNewMessage(Message4);
    request->processNewMessage(Message5);
    request->processNewMessage(Message6);

    BOOST_CHECK_EQUAL( false , request->isComplete() );

    // check results

    const std::set<long>  shouldBeMissing{ 1100, 1103 };

    const std::set<long>  missingPoints = request->getMissingPoints();

    BOOST_CHECK_EQUAL_COLLECTIONS( missingPoints.begin(),   missingPoints.end(),
                                   shouldBeMissing.begin(), shouldBeMissing.end() );

    PointValueMap   rejectedPoints = request->getRejectedPointValues();

    BOOST_CHECK_EQUAL( 1 , rejectedPoints.size() );
    BOOST_CHECK_CLOSE( 119.7 , rejectedPoints[1102].value , 0.0001 );

    PointValueMap   receivedPoints = request->getPointValues();

    BOOST_CHECK_EQUAL( 2 , receivedPoints.size() );

    BOOST_CHECK_CLOSE( 121.3 , receivedPoints[1101].value , 0.0001 );
    BOOST_CHECK_CLOSE( 122.0 , receivedPoints[1104].value , 0.0001 );

    BOOST_CHECK_EQUAL(request->createStatusReport(),
        "\n Point Data Request Status: "
        "\n -------------------------- "
        "\n Points missing: "
        "\n {1100,1103}"
        "\n"
        "\n Points Received but rejected: "
        "\n Point Id: 1102 Quality: Unknown Value: 119.7 Timestamp: 01/01/1990 00:00:00"
        "\n"
        "\n Points Received and accepted: "
        "\n Point Id: 1101 Quality: Normal Value: 121.3 Timestamp: 01/01/1990 00:00:00"
        "\n Point Id: 1104 Quality: Normal Value: 122 Timestamp: 01/01/1990 00:00:00"
        "\n");
}

BOOST_AUTO_TEST_CASE(test_cap_control_ivvc_algorithm_stale_point_processing)
{
    PointDataRequestFactory testFactory;
    PointDataRequestPtr     pd_request = testFactory.createDispatchPointDataRequest( DispatchConnectionPtr( new mock_DispatchConnection()));

    // Need an actual DispatchPointDataRequest pointer so we can call processNewMessage().

    DispatchPointDataRequest * request = dynamic_cast<DispatchPointDataRequest*>( pd_request.get() );

    BOOST_REQUIRE( request );

    std::set<PointRequest>  watchlist;

    // Add 5 point IDs to the watchlist

    for ( long pointId = 1100; pointId < 1105; ++pointId )
    {
        PointRequest pr( pointId, OtherRequestType );
        watchlist.insert( pr );
    }

    BOOST_CHECK_EQUAL( true , request->watchPoints( watchlist ) );

    CtiDate day { 1, 1, 1990 };

    CtiTime now               = CtiTime( day, 0, 10, 0 );
    CtiTime fiveMinutesAgo    = CtiTime( day, 0, 5,  0 );
    CtiTime tenMinutesAgo     = CtiTime( day, 0, 0,  0 );

    // Create five custom messages ( by default, timestamp = CtiTime::now() )

    CtiPointDataMsg * Message1 = new CtiPointDataMsg( 1100, 120.0, NormalQuality, AnalogPointType );
    CtiPointDataMsg * Message2 = new CtiPointDataMsg( 1101, 120.1, NormalQuality, AnalogPointType );
    CtiPointDataMsg * Message3 = new CtiPointDataMsg( 1102, 120.2, NormalQuality, AnalogPointType );
    CtiPointDataMsg * Message4 = new CtiPointDataMsg( 1103, 120.3, NormalQuality, AnalogPointType );
    CtiPointDataMsg * Message5 = new CtiPointDataMsg( 1104, 120.4, NormalQuality, AnalogPointType );

    // Update timestamps and process messages

    Message1->setTime( now );
    Message2->setTime( now );
    Message3->setTime( now );
    Message4->setTime( tenMinutesAgo );
    Message5->setTime( tenMinutesAgo );

    request->processNewMessage( Message1 );
    request->processNewMessage( Message2 );
    request->processNewMessage( Message3 );
    request->processNewMessage( Message4 );
    request->processNewMessage( Message5 );

    BOOST_CHECK_EQUAL( true, request->isComplete() );

    // "now" messages are not stale, while "tenMinutesAgo" messages are

    BOOST_CHECK_EQUAL( false, request->isPointStale( 1100, fiveMinutesAgo ) );
    BOOST_CHECK_EQUAL( false, request->isPointStale( 1101, fiveMinutesAgo ) );
    BOOST_CHECK_EQUAL( false, request->isPointStale( 1102, fiveMinutesAgo ) );
    BOOST_CHECK_EQUAL( true,  request->isPointStale( 1103, fiveMinutesAgo ) );
    BOOST_CHECK_EQUAL( true,  request->isPointStale( 1104, fiveMinutesAgo ) );

    // Stale messages are moved to the rejected points list 

    BOOST_CHECK_EQUAL( 2, request->getRejectedPointValues().size() );

    PointValueMap   receivedPoints = request->getPointValues();

    BOOST_CHECK_EQUAL( 3, receivedPoints.size() );

    BOOST_CHECK_EQUAL( request->createStatusReport(),
        "\n Point Data Request Status: "
        "\n -------------------------- "
        "\n Points Received but rejected: "
        "\n Point Id: 1103 Quality: Normal Value: 120.3 Timestamp: 01/01/1990 00:00:00"
        "\n Point Id: 1104 Quality: Normal Value: 120.4 Timestamp: 01/01/1990 00:00:00"
        "\n"
        "\n Points Received and accepted: "
        "\n Point Id: 1100 Quality: Normal Value: 120 Timestamp: 01/01/1990 00:10:00"
        "\n Point Id: 1101 Quality: Normal Value: 120.1 Timestamp: 01/01/1990 00:10:00"
        "\n Point Id: 1102 Quality: Normal Value: 120.2 Timestamp: 01/01/1990 00:10:00"
        "\n" );
}

BOOST_AUTO_TEST_SUITE_END()
