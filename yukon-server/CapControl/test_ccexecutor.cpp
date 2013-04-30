#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "ccunittestutil.h"
#include "executorfactory.h"

#include <boost/ptr_container/ptr_vector.hpp>

using namespace Cti::Test::CapControl;
using namespace Cti::CapControl;

BOOST_AUTO_TEST_SUITE( test_ccexecutor )

struct test_CtiCapController : CtiCapController
{
    using CtiCapController::porterReturnMsg;

    boost::ptr_vector<CtiRequestMsg> requests;
    boost::ptr_vector<CtiMultiMsg> points;
    boost::ptr_vector<CtiMultiMsg> pilMultiMsgs;

    virtual void confirmCapBankControl( CtiMultiMsg* pilMultiMsg, CtiMultiMsg* multiMsg )
    {
        if( pilMultiMsg )
        {
            pilMultiMsgs.push_back(pilMultiMsg);
        }
        if( multiMsg )
        {
            points.push_back(multiMsg);
        }
    }

    virtual void manualCapBankControl( CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg )
    {
        if( pilRequest )
        {
            requests.push_back(pilRequest);
        }
        if( multiMsg )
        {
            points.push_back(multiMsg);
        }
    }
};


BOOST_AUTO_TEST_CASE( test_commands )
{
    Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();

    CtiCCSubstationBusStore::setInstance(store);

    StrategyManager *strategyManager = new StrategyManager(std::auto_ptr<StrategyLoader>(new StrategyUnitTestLoader));
    std::auto_ptr<StrategyManager> manager = std::auto_ptr<StrategyManager>(strategyManager);

    manager->reloadAll();

    store->setStrategyManager(manager);

    CtiCCArea           *area       = create_object<CtiCCArea>           (1, "test area");
    CtiCCSubstation     *substation = create_object<CtiCCSubstation>     (2, "test substation");
    CtiCCSubstationBus  *bus        = create_object<CtiCCSubstationBus>  (3, "test bus");
    CtiCCFeeder         *feeder     = create_object<CtiCCFeeder>         (4, "test feeder");
    CtiCCCapBank        *bank       = create_object<CtiCCCapBank>        (5, "test cap bank");

    initialize_area   (store, area);
    initialize_station(store, substation, area);
    initialize_bus    (store, bus, substation);
    initialize_feeder (store, feeder, bus, 1);
    initialize_capbank(store, bank, feeder, 1);

    bus->setStrategy(100);
    bus->setStrategyManager(strategyManager);

    bank->setControlDeviceId(6);
    bank->setControlPointId(7);
    bank->setStatusPointId(8);
    bank->setControlDeviceType("cbc 7010");

    store->addCapBankToCBCMap(bank);

    std::auto_ptr<test_AttributeService> attributeService(new test_AttributeService);

    test_AttributeService &attributeBackdoor = *attributeService;

    store->setAttributeService(std::auto_ptr<AttributeService>(attributeService.release()));

    LitePoint p;

    p.setPaoId(6);
    p.setPointId(7);

    attributeBackdoor.points[p.getPointId()] = p;

    test_CtiCapController *cc = new test_CtiCapController;

    CtiCapController::setInstance(cc);

    //  Test default control strings
    {
        ItemCommand *test_command = new ItemCommand(CapControlCommand::FLIP_7010_CAPBANK, 5);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

        test_executor->execute();

        BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

        CtiRequestMsg &request = cc->requests[0];

        BOOST_CHECK_EQUAL(request.CommandString(), "control flip");
        BOOST_CHECK_EQUAL(request.DeviceId(), 6);
    }
    cc->requests.clear();
    {
        ItemCommand *test_command = new ItemCommand(CapControlCommand::SEND_OPEN_CAPBANK, 5);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

        test_executor->execute();

        BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

        CtiRequestMsg &request = cc->requests[0];

        BOOST_CHECK_EQUAL(request.CommandString(), "control open");
        BOOST_CHECK_EQUAL(request.DeviceId(), 6);
    }
    cc->requests.clear();
    {
        ItemCommand *test_command = new ItemCommand(CapControlCommand::SEND_CLOSE_CAPBANK, 5);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

        test_executor->execute();

        BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

        CtiRequestMsg &request = cc->requests[0];

        BOOST_CHECK_EQUAL(request.CommandString(), "control close");
        BOOST_CHECK_EQUAL(request.DeviceId(), 6);
    }
    cc->requests.clear();
    {
        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_CLOSE, 5);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

        test_executor->execute();

        BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

        CtiRequestMsg &request = cc->requests[0];

        BOOST_CHECK_EQUAL(request.CommandString(), "control close");
        BOOST_CHECK_EQUAL(request.DeviceId(), 6);
    }
    cc->requests.clear();
    {
        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_OPEN, 5);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

        test_executor->execute();

        BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

        CtiRequestMsg &request = cc->requests[0];

        BOOST_CHECK_EQUAL(request.CommandString(), "control open");
        BOOST_CHECK_EQUAL(request.DeviceId(), 6);
    }
    cc->requests.clear();
    {
        bank->setControlStatus(CtiCCCapBank::OpenPending);

        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_FEEDER, 4);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

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
    cc->pilMultiMsgs.clear();
    {
        bank->setControlStatus(CtiCCCapBank::ClosePending);

        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_FEEDER, 4);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

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
    cc->pilMultiMsgs.clear();
    {
        bank->setControlStatus(CtiCCCapBank::OpenPending);

        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, 3);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

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
    cc->pilMultiMsgs.clear();
    {
        bank->setControlStatus(CtiCCCapBank::ClosePending);

        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, 3);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

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
    cc->pilMultiMsgs.clear();

    p.setStateZeroControl("hippopotamus giraffe");
    p.setStateOneControl("elephant monkey");
    attributeBackdoor.points[p.getPointId()] = p;

    //  Test custom control strings
    {
        ItemCommand *test_command = new ItemCommand(CapControlCommand::FLIP_7010_CAPBANK, 5);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

        test_executor->execute();

        BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

        CtiRequestMsg &request = cc->requests[0];

        BOOST_CHECK_EQUAL(request.CommandString(), "control flip");
        BOOST_CHECK_EQUAL(request.DeviceId(), 6);
    }
    cc->requests.clear();
    {
        ItemCommand *test_command = new ItemCommand(CapControlCommand::SEND_OPEN_CAPBANK, 5);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

        test_executor->execute();

        BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

        CtiRequestMsg &request = cc->requests[0];

        BOOST_CHECK_EQUAL(request.CommandString(), "hippopotamus giraffe");
        BOOST_CHECK_EQUAL(request.DeviceId(), 6);
    }
    cc->requests.clear();
    {
        ItemCommand *test_command = new ItemCommand(CapControlCommand::SEND_CLOSE_CAPBANK, 5);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

        test_executor->execute();

        BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

        CtiRequestMsg &request = cc->requests[0];

        BOOST_CHECK_EQUAL(request.CommandString(), "elephant monkey");
        BOOST_CHECK_EQUAL(request.DeviceId(), 6);
    }
    cc->requests.clear();
    {
        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_CLOSE, 5);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

        test_executor->execute();

        BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

        CtiRequestMsg &request = cc->requests[0];

        BOOST_CHECK_EQUAL(request.CommandString(), "elephant monkey");
        BOOST_CHECK_EQUAL(request.DeviceId(), 6);
    }
    cc->requests.clear();
    {
        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_OPEN, 5);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

        test_executor->execute();

        BOOST_REQUIRE_EQUAL(cc->requests.size(), 1);

        CtiRequestMsg &request = cc->requests[0];

        BOOST_CHECK_EQUAL(request.CommandString(), "hippopotamus giraffe");
        BOOST_CHECK_EQUAL(request.DeviceId(), 6);
    }
    cc->requests.clear();
    {
        bank->setControlStatus(CtiCCCapBank::OpenPending);

        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_FEEDER, 4);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

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
    cc->pilMultiMsgs.clear();
    {
        bank->setControlStatus(CtiCCCapBank::ClosePending);

        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_FEEDER, 4);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

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
    cc->pilMultiMsgs.clear();
    {
        bank->setControlStatus(CtiCCCapBank::OpenPending);

        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, 3);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

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
    cc->pilMultiMsgs.clear();
    {
        bank->setControlStatus(CtiCCCapBank::ClosePending);

        ItemCommand *test_command = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, 3);

        std::auto_ptr<CtiCCExecutor> test_executor = CtiCCExecutorFactory::createExecutor(test_command);

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
    cc->pilMultiMsgs.clear();
}

BOOST_AUTO_TEST_SUITE_END()
