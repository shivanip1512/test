#include <boost/test/unit_test.hpp>

#include "pilserver.h"
#include "msg_pcreturn.h"

#include <boost/assign/list_of.hpp>

BOOST_AUTO_TEST_SUITE( test_pilserver )

using namespace std;

struct test_DeviceManager : CtiDeviceManager
{
    CtiDeviceSPtr dev;

    test_DeviceManager() :
        dev(new CtiDeviceBase())
    {
        dev->setID(42);
    }

    virtual ptr_type RemoteGetEqualbyName(const std::string &RemoteName)
    {
        return (RemoteName == "beetlebrox")
            ? dev
            : ptr_type();
    }
} test_deviceManager;

struct test_RouteManager : CtiRouteManager
{
    CtiRouteSPtr rte;

    struct test_route : CtiRouteBase
    {
        test_route() {
            _tblPAO.setID(84);
        }
        YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) override { return ClientErrors::None; }
    };

    test_RouteManager() :
        rte(new test_route())
    {
    }

    virtual ptr_type getRouteByName(std::string RouteName)
    {
        return (RouteName == "sixty-six")
            ? rte
            : ptr_type();
    }
} test_routeManager;


struct Test_PilServer : Cti::Pil::PilServer
{
    Test_PilServer() :
        PilServer(&test_deviceManager, 0, &test_routeManager)
    {}

    virtual std::vector<long> getDeviceGroupMembers(std::string groupname) const
    {
        if( groupname == "/pi" )
        {
            return boost::assign::list_of(3),1,4,1,5,9,2,6,5,3;
        }
        if( groupname == "/meters/collection/testing old-style group" )
        {
            return boost::assign::list_of(1),2,3,4,5;
        }
        if( groupname == "/meters/alternate/testing old-style alternate group" )
        {
            return boost::assign::list_of(8),7,6,5,4;
        }
        if( groupname == "/meters/billing/testing old-style billing group" )
        {
            return boost::assign::list_of(10),11,12,13,14;
        }

        return std::vector<long>();
    }
};




struct pilEnvironment
{
    Test_PilServer testPilServer;

    std::list<CtiRequestMsg *> execList;
    std::list<CtiMessage *> retList;
    boost::ptr_deque<CtiRequestMsg> groupRequests;

    ~pilEnvironment()
    {
        delete_container(execList);
        delete_container(retList);
    }
};


BOOST_FIXTURE_TEST_SUITE(test_AnalyzeWhiteRabbits, pilEnvironment)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

BOOST_AUTO_TEST_CASE(test_analyzeWhiteRabbits_no_op)
{
    CtiRequestMsg msg(0, "nonsense here");

    testPilServer.analyzeWhiteRabbits(msg,  CtiCommandParser(msg.CommandString()), execList, groupRequests, retList);

    BOOST_CHECK(groupRequests.empty());
    BOOST_CHECK(retList.empty());

    BOOST_REQUIRE_EQUAL(execList.size(), 1);

    CtiRequestMsg *reqMsg = execList.front();

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

    CtiRequestMsg *reqMsg = execList.front();

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

    CtiRequestMsg *reqMsg = execList.front();

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

    CtiRequestMsg *reqMsg = execList.front();

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

    CtiRequestMsg *reqMsg = execList.front();

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

    CtiRequestMsg *reqMsg = execList.front();

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

    boost::ptr_deque<CtiRequestMsg>::auto_type groupRequest;

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 3);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 1);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 4);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 1);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 9);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 2);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 6);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "trancendental numbers select group '/pi'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    groupRequest = groupRequests.pop_front();
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

    boost::ptr_deque<CtiRequestMsg>::auto_type groupRequest;

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 1);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 2);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 3);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select group 'testing old-style group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 4);

    groupRequest = groupRequests.pop_front();
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

    boost::ptr_deque<CtiRequestMsg>::auto_type groupRequest;

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 8);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 7);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 6);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select altgroup 'testing old-style alternate group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 5);

    groupRequest = groupRequests.pop_front();
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

    boost::ptr_deque<CtiRequestMsg>::auto_type groupRequest;

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 10);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 11);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 12);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 13);

    groupRequest = groupRequests.pop_front();
    BOOST_CHECK_EQUAL(groupRequest->CommandString(), "getvalue kwh select billgroup 'testing old-style billing group'");
    BOOST_CHECK_EQUAL(groupRequest->DeviceId(), 14);

    BOOST_CHECK_EQUAL(retList.size(), 0);
    BOOST_CHECK_EQUAL(execList.size(), 0);
}

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()

