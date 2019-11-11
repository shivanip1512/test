#include <boost/test/unit_test.hpp>

#include "pilserver.h"
#include "msg_pcreturn.h"
#include "dev_rfn.h"
#include "cmd_rfn_demandFreeze.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_pilserver )

using namespace std;

struct Test_PilServer : Cti::Pil::PilServer
{
    Cti::Test::test_DeviceManager dev_mgr;
    Cti::Test::test_PointManager  pt_mgr;
    Cti::Test::test_RouteManager  rte_mgr;

    Test_PilServer() :
        PilServer(dev_mgr, pt_mgr, rte_mgr)
    {}

    using PilServer::handleRfnDeviceResult;
    using PilServer::analyzeWhiteRabbits;
    using RequestQueue = PilServer::RequestQueue;

    std::vector<long> getDeviceGroupMembers(std::string groupname) const override
    {
        if( groupname == "/pi" )
        {
            return { 3,1,4,1,5,9,2,6,5,3 };
        }
        if( groupname == "/meters/collection/testing old-style group" )
        {
            return { 1,2,3,4,5 };
        }
        if( groupname == "/meters/alternate/testing old-style alternate group" )
        {
            return { 8,7,6,5,4 };
        }
        if( groupname == "/meters/billing/testing old-style billing group" )
        {
            return { 10,11,12,13,14 };
        }

        return {};
    }

    boost::ptr_vector<CtiMessage> vgList, retList;

    void sendResults(CtiDeviceBase::CtiMessageList &vgList_, CtiDeviceBase::CtiMessageList &retList_, const int priority, Cti::ConnectionHandle connectionHandle) override
    {
        for( CtiMessage *msg : vgList_ )
        {
            vgList.push_back(msg);
        }

        for( CtiMessage *msg : retList_ )
        {
            retList.push_back(msg);
        }
    }
};




struct pilEnvironment
{
    Test_PilServer testPilServer;

    Test_PilServer::RequestQueue execList;
    std::list<CtiMessage *> retList;
    Test_PilServer::RequestQueue groupRequests;

    ~pilEnvironment()
    {
        delete_container(retList);
    }
};


BOOST_AUTO_TEST_CASE(test_handleRfnDeviceResult)
{
    Test_PilServer pilServer;

    CtiDeviceManager::ptr_type dev = pilServer.dev_mgr.getDeviceByID(123);

    BOOST_REQUIRE(dev);

    CtiDeviceSingle *devSingle = dynamic_cast<CtiDeviceSingle *>(dev.get());

    BOOST_REQUIRE(devSingle);

    const Cti::ConnectionHandle handle{ 55441 };

    devSingle->incrementGroupMessageCount(11235, handle, 2);

    BOOST_CHECK_EQUAL(2, devSingle->getGroupMessageCount(11235, handle));

    Cti::Pil::RfnDeviceRequest::Parameters parameters;

    parameters.deviceId         = 123;
    parameters.userMessageId    = 11235;
    parameters.connectionHandle = handle;

    pilServer.handleRfnDeviceResult({
        { parameters, 9999, std::make_unique<Cti::Devices::Commands::RfnImmediateDemandFreezeCommand>() },
        { { "This was a triumph. I'm making a note here: HUGE SUCCESS." } }});

    BOOST_REQUIRE_EQUAL(1, pilServer.retList.size());

    {
        CtiReturnMsg &retMsg = dynamic_cast<CtiReturnMsg &>(pilServer.retList.front());

        BOOST_CHECK_EQUAL(retMsg.ResultString(), "This was a triumph. I'm making a note here: HUGE SUCCESS.");
        BOOST_CHECK(retMsg.ExpectMore());
    }

    BOOST_CHECK_EQUAL(1, devSingle->getGroupMessageCount(11235, handle));
}


BOOST_FIXTURE_TEST_SUITE(test_AnalyzeWhiteRabbits, pilEnvironment)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_no_op)
{
    CtiRequestMsg msg(0, "nonsense here");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_device_id)
{
    CtiRequestMsg msg(0, "nonsense here select deviceid 3");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here select deviceid 3");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 3);
}

BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_device_name)
{
    CtiRequestMsg msg(0, "nonsense here select name 'beetlebrox'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here select name 'beetlebrox'");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 42);
}

BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_device_name_not_found)
{
    CtiRequestMsg msg(0, "nonsense here select name 'zaphod'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(execList.empty());

    BOOST_CHECK_EQUAL(retList.size(), 1);

    CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    BOOST_CHECK_EQUAL(retMsg->Status(), ClientErrors::IdNotFound);
    BOOST_CHECK_EQUAL(retMsg->ResultString(), "No device with name 'zaphod' exists in the database.");
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_route_id)
{
    CtiRequestMsg msg(0, "nonsense here select route id 3");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here select route id 3");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 0);
    BOOST_CHECK_EQUAL(reqMsg->RouteId(), 3);
}

BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_route_name)
{
    CtiRequestMsg msg(0, "nonsense here select route name 'sixty-six'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here select route name 'sixty-six'");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 0);
    BOOST_CHECK_EQUAL(reqMsg->RouteId(), 84);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_route_name_not_found)
{
    CtiRequestMsg msg(0, "nonsense here select route name 'sixty-seven'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front().get();

    BOOST_REQUIRE(reqMsg);

    BOOST_CHECK_EQUAL(reqMsg->CommandString(), "nonsense here select route name 'sixty-seven'");
    BOOST_CHECK_EQUAL(reqMsg->DeviceId(), 0);
    BOOST_CHECK_EQUAL(reqMsg->RouteId(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_group_fail)
{
    CtiRequestMsg msg(0, "nonsense here select group '/fiddlesticks'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    BOOST_CHECK_EQUAL(retMsg->Status(), ClientErrors::IdNotFound);
    BOOST_CHECK_EQUAL(retMsg->ResultString(), "Group '/fiddlesticks' found no target devices.");

    BOOST_CHECK_EQUAL(execList.size(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_group_pi)
{
    CtiRequestMsg msg(0, "trancendental numbers select group '/pi'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(retList.empty());
    BOOST_CHECK(execList.empty());
    BOOST_REQUIRE_EQUAL(groupRequests.size(), 10);

    auto itr = groupRequests.begin();
    CtiRequestMsg* groupRequest;

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 3);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 1);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 4);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 1);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 9);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 2);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 6);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 3);

    BOOST_CHECK_EQUAL(retList.size(), 0);
    BOOST_CHECK_EQUAL(execList.size(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_group_old_style)
{
    CtiRequestMsg msg(0, "getvalue kwh select group 'testing old-style group'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(retList.empty());
    BOOST_CHECK(execList.empty());

    BOOST_REQUIRE_EQUAL(groupRequests.size(), 5);

    auto itr = groupRequests.begin();
    CtiRequestMsg* groupRequest;

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 1);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 2);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 3);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 4);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    BOOST_CHECK_EQUAL(retList.size(), 0);
    BOOST_CHECK_EQUAL(execList.size(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_altgroup_old_style)
{
    CtiRequestMsg msg(0, "getvalue kwh select altgroup 'testing old-style alternate group'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(retList.empty());
    BOOST_CHECK(execList.empty());

    BOOST_REQUIRE_EQUAL(groupRequests.size(), 5);

    auto itr = groupRequests.begin();
    CtiRequestMsg* groupRequest;

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 8);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 7);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 6);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 4);

    BOOST_CHECK_EQUAL(retList.size(), 0);
    BOOST_CHECK_EQUAL(execList.size(), 0);
}


BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_select_billgroup_old_style)
{
    CtiRequestMsg msg(0, "getvalue kwh select billgroup 'testing old-style billing group'");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(retList.empty());
    BOOST_CHECK(execList.empty());

    BOOST_REQUIRE_EQUAL(groupRequests.size(), 5);

    auto itr = groupRequests.begin();
    CtiRequestMsg* groupRequest;

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 10);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 11);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 12);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 13);

    groupRequest = (*itr++).get();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 14);

    BOOST_CHECK_EQUAL(retList.size(), 0);
    BOOST_CHECK_EQUAL(execList.size(), 0);
}

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()

