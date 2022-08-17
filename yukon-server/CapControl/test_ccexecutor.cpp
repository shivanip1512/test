#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "ccunittestutil.h"
#include "executorfactory.h"

#include <boost/ptr_container/ptr_vector.hpp>

#include "boost_test_helpers.h"
#include "capcontrol_test_helpers.h"

using namespace Cti::Test::CapControl;
using namespace Cti::CapControl;

namespace 
{

struct overrideGlobals
{
    boost::shared_ptr<Cti::Test::test_DeviceConfig>    fixtureConfig;

    Cti::Test::Override_ConfigManager overrideConfigManager;
    Cti::Test::PreventDatabaseConnections preventDatabaseConnections;

    struct test_CtiCapController : CtiCapController
    {
        using CtiCapController::porterReturnMsg;

        boost::ptr_vector<CtiRequestMsg> requests;
        boost::ptr_vector<CtiMultiMsg> points;
        boost::ptr_vector<CtiMultiMsg> pilMultiMsgs;

        void confirmCapBankControl(CtiMultiMsg* pilMultiMsg, CtiMultiMsg* multiMsg) override
        {
            if (pilMultiMsg)
            {
                pilMultiMsgs.push_back(pilMultiMsg);
            }
            if (multiMsg)
            {
                points.push_back(multiMsg);
            }
        }

        void manualCapBankControl(CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg) override
        {
            if (pilRequest)
            {
                requests.push_back(pilRequest);
            }
            if (multiMsg)
            {
                points.push_back(multiMsg);
            }
        }

        void sendMessageToDispatch(CtiMessage* message, Cti::CallSite cs) override
        {
            delete message;
        }
    };

    overrideGlobals() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
        BypassDatabaseForIdGen(test_tag);

        Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();

        CtiCCSubstationBusStore::setInstance(store);

        std::unique_ptr<StrategyManager> manager = std::make_unique<StrategyManager>(std::make_unique<StrategyUnitTestLoader>());
        StrategyManager *strategyManager = manager.get();

        manager->reloadAll();

        store->setStrategyManager(std::move(manager));

        area        = create_object<CtiCCArea>          (1, "test area");
        substation  = create_object<CtiCCSubstation>    (2, "test substation");
        bus         = create_object<CtiCCSubstationBus> (3, "test bus");
        feeder      = create_object<CtiCCFeeder>        (4, "test feeder");
        bank        = create_object<CtiCCCapBank>       (5, "test cap bank");

        initialize_area     (store, area);
        initialize_station  (store, CtiCCSubstationUnqPtr{ substation }, area, Cti::Test::use_in_unit_tests_only{});
        initialize_bus      (store, bus, substation);
        initialize_feeder   (store, feeder, bus, 1);
        initialize_capbank  (store, bank, feeder, 1);

        bus->setStrategy(100);
        bus->setStrategyManager(strategyManager);

        bank->createCbc(6, "cbc 7010");
        bank->setStatusPointId(8);

        store->addCapBankToCBCMap(bank);

        cc = new test_CtiCapController;

        CtiCapController::setInstance(cc);
    }

    ~overrideGlobals()
    {
        delete area;
    };

    CtiCCAreaPtr         area;
    CtiCCSubstationPtr   substation;
    CtiCCSubstationBus  *bus;
    CtiCCFeeder         *feeder;
    CtiCCCapBank        *bank;

    test_CtiCapController *cc;
};

struct defaultGlobals : overrideGlobals
{
    defaultGlobals() : overrideGlobals()
    {
        bank->getTwoWayPoints().assignTwoWayPointsAndAttributes(
            {LitePoint (7,  StatusPointType, "CBC Control Point", 6, 1, "control open", "control close", 1.0, 0)}, 
            {}, boost::none, boost::none);
    }
};

struct customGlobals : overrideGlobals
{
    customGlobals() : overrideGlobals()
    {
        bank->getTwoWayPoints().assignTwoWayPointsAndAttributes(
            { LitePoint(7,  StatusPointType, "CBC Control Point", 6, 1, "hippopotamus giraffe", "elephant monkey", 1.0, 0) },
            {}, boost::none, boost::none);
    }
};

}

BOOST_AUTO_TEST_SUITE(test_ccexecutor)

BOOST_FIXTURE_TEST_CASE(test_default_flip, defaultGlobals)
{
    ItemCommand *test_command = new ItemCommand(CapControlCommand::FLIP_7010_CAPBANK, 5);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

    CtiRequestMsg &request = cc->requests[0];

    BOOST_CHECK_EQUAL(request.CommandString(), "control flip");
    BOOST_CHECK_EQUAL(request.DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_default_send_open, defaultGlobals)
{
    ItemCommand *test_command = new ItemCommand(CapControlCommand::SEND_OPEN_CAPBANK, 5);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

    CtiRequestMsg &request = cc->requests[0];

    BOOST_CHECK_EQUAL(request.CommandString(), "control open");
    BOOST_CHECK_EQUAL(request.DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_default_send_close, defaultGlobals)
{
    ItemCommand *test_command = new ItemCommand(CapControlCommand::SEND_CLOSE_CAPBANK, 5);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

    CtiRequestMsg &request = cc->requests[0];

    BOOST_CHECK_EQUAL(request.CommandString(), "control close");
    BOOST_CHECK_EQUAL(request.DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_default_confirm_close, defaultGlobals)
{
    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_CLOSE, 5);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

    CtiRequestMsg &request = cc->requests[0];

    BOOST_CHECK_EQUAL(request.CommandString(), "control close");
    BOOST_CHECK_EQUAL(request.DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_default_confirm_open, defaultGlobals)
{
    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_OPEN, 5);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

    CtiRequestMsg &request = cc->requests[0];

    BOOST_CHECK_EQUAL(request.CommandString(), "control open");
    BOOST_CHECK_EQUAL(request.DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_default_feeder_control_open, defaultGlobals)
{
    bank->setControlStatus(CtiCCCapBank::OpenPending);

    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_FEEDER, 4);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->pilMultiMsgs.size(), 1);

    CtiMultiMsg &pilMulti = cc->pilMultiMsgs[0];

    CtiMultiMsg_vec &pilRequests = pilMulti.getData();

    BOOST_REQUIRE_EQUAL(pilRequests.size(), 1);

    CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMulti.getData()[0]);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control open");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_default_feeder_control_close, defaultGlobals)
{
    bank->setControlStatus(CtiCCCapBank::ClosePending);

    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_FEEDER, 4);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->pilMultiMsgs.size(), 1);

    CtiMultiMsg &pilMulti = cc->pilMultiMsgs[0];

    CtiMultiMsg_vec &pilRequests = pilMulti.getData();

    BOOST_REQUIRE_EQUAL(pilRequests.size(), 1);

    CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMulti.getData()[0]);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control close");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_default_subbus_control_open, defaultGlobals)
{
    bank->setControlStatus(CtiCCCapBank::OpenPending);

    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, 3);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->pilMultiMsgs.size(), 1);

    CtiMultiMsg &pilMulti = cc->pilMultiMsgs[0];

    CtiMultiMsg_vec &pilRequests = pilMulti.getData();

    BOOST_REQUIRE_EQUAL(pilRequests.size(), 1);

    CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMulti.getData()[0]);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control open");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_default_subbus_control_close, defaultGlobals)
{
    bank->setControlStatus(CtiCCCapBank::ClosePending);

    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, 3);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->pilMultiMsgs.size(), 1);

    CtiMultiMsg &pilMulti = cc->pilMultiMsgs[0];

    CtiMultiMsg_vec &pilRequests = pilMulti.getData();

    BOOST_REQUIRE_EQUAL(pilRequests.size(), 1);

    CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMulti.getData()[0]);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control close");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_custom_flip, customGlobals)
{
    ItemCommand *test_command = new ItemCommand(CapControlCommand::FLIP_7010_CAPBANK, 5);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

    CtiRequestMsg &request = cc->requests[0];

    BOOST_CHECK_EQUAL(request.CommandString(), "control flip");
    BOOST_CHECK_EQUAL(request.DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_custom_send_open, customGlobals)
{
    ItemCommand *test_command = new ItemCommand(CapControlCommand::SEND_OPEN_CAPBANK, 5);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

    CtiRequestMsg &request = cc->requests[0];

    BOOST_CHECK_EQUAL(request.CommandString(), "hippopotamus giraffe");
    BOOST_CHECK_EQUAL(request.DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_custom_send_close, customGlobals)
{
    ItemCommand *test_command = new ItemCommand(CapControlCommand::SEND_CLOSE_CAPBANK, 5);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

    CtiRequestMsg &request = cc->requests[0];

    BOOST_CHECK_EQUAL(request.CommandString(), "elephant monkey");
    BOOST_CHECK_EQUAL(request.DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_custom_confirm_close, customGlobals)
{
    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_CLOSE, 5);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

    CtiRequestMsg &request = cc->requests[0];

    BOOST_CHECK_EQUAL(request.CommandString(), "elephant monkey");
    BOOST_CHECK_EQUAL(request.DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_custom_confirm_open, customGlobals)
{
    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_OPEN, 5);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

    CtiRequestMsg &request = cc->requests[0];

    BOOST_CHECK_EQUAL(request.CommandString(), "hippopotamus giraffe");
    BOOST_CHECK_EQUAL(request.DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_custom_feeder_control_open, customGlobals)
{
    bank->setControlStatus(CtiCCCapBank::OpenPending);

    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_FEEDER, 4);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->pilMultiMsgs.size(), 1);

    CtiMultiMsg &pilMulti = cc->pilMultiMsgs[0];

    CtiMultiMsg_vec &pilRequests = pilMulti.getData();

    BOOST_REQUIRE_EQUAL(pilRequests.size(), 1);

    CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMulti.getData()[0]);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "hippopotamus giraffe");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_custom_feeder_control_close, customGlobals)
{
    bank->setControlStatus(CtiCCCapBank::ClosePending);

    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_FEEDER, 4);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->pilMultiMsgs.size(), 1);

    CtiMultiMsg &pilMulti = cc->pilMultiMsgs[0];

    CtiMultiMsg_vec &pilRequests = pilMulti.getData();

    BOOST_REQUIRE_EQUAL(pilRequests.size(), 1);

    CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMulti.getData()[0]);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "elephant monkey");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_custom_subbus_control_open, customGlobals)
{
    bank->setControlStatus(CtiCCCapBank::OpenPending);

    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, 3);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->pilMultiMsgs.size(), 1);

    CtiMultiMsg &pilMulti = cc->pilMultiMsgs[0];

    CtiMultiMsg_vec &pilRequests = pilMulti.getData();

    BOOST_REQUIRE_EQUAL(pilRequests.size(), 1);

    CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMulti.getData()[0]);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "hippopotamus giraffe");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);
}

BOOST_FIXTURE_TEST_CASE(test_custom_subbus_control_close, customGlobals)
{
    bank->setControlStatus(CtiCCCapBank::ClosePending);

    ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, 3);

    std::unique_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

    test_executor->execute();

    BOOST_REQUIRE_EQUAL(cc->pilMultiMsgs.size(), 1);

    CtiMultiMsg &pilMulti = cc->pilMultiMsgs[0];

    CtiMultiMsg_vec &pilRequests = pilMulti.getData();

    BOOST_REQUIRE_EQUAL(pilRequests.size(), 1);

    CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMulti.getData()[0]);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "elephant monkey");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);
}

BOOST_AUTO_TEST_SUITE_END()
