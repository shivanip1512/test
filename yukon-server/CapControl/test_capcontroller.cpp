#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "ccunittestutil.h"
#include "msg_pcreturn.h"

#include "boost_test_helpers.h"
#include "capcontrol_test_helpers.h"

using namespace Cti::Test::CapControl;
using namespace Cti::CapControl;

struct test_CtiCapController : CtiCapController
{
    using CtiCapController::porterReturnMsg;
};

namespace {

struct overrideGlobals
{
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;
    Cti::Test::PreventDatabaseConnections preventDatabaseConnections;

    overrideGlobals() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
        Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();

        CtiCCSubstationBusStore::setInstance(store);

        area = create_object<CtiCCArea>(1, "test area");
        substation = create_object<CtiCCSubstation>(2, "test substation");
        bus = create_object<CtiCCSubstationBus>(3, "test bus");
        feeder = create_object<CtiCCFeeder>(4, "test feeder");
        bank = create_object<CtiCCCapBank>(5, "test cap bank");

        initialize_area(store, area);
        initialize_station(store, CtiCCSubstationUnqPtr{ substation }, area, Cti::Test::use_in_unit_tests_only{});
        initialize_bus(store, bus, substation);
        initialize_feeder(store, feeder, bus, 1);
        initialize_capbank(store, bank, feeder, 1);

        bank->createCbc(6, "cbc 7010");

        store->addCapBankToCBCMap(bank);
    }

    ~overrideGlobals() 
    {
        delete area;
    };

    CtiCCAreaPtr         area;
    CtiCCSubstation     *substation;
    CtiCCSubstationBus  *bus;
    CtiCCFeeder         *feeder;
    CtiCCCapBank        *bank;

    test_CtiCapController cc;
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
            { LitePoint(7,  StatusPointType, "CBC Control Point", 6, 1, "hippopotamus alligator", "giraffe baboon", 1.0, 0) },
            {}, boost::none, boost::none);
    }
};

}

BOOST_AUTO_TEST_SUITE(test_capcontroller)

BOOST_FIXTURE_TEST_CASE(test_default_control_open, defaultGlobals)
{
    const CtiReturnMsg controlOpen_noError          (6, "control open", "n/a", 0);
    const CtiReturnMsg controlOpen_error            (6, "control open", "n/a", 1);
    const CtiReturnMsg hippopotamusAlligator_noError(6, "hippopotamus alligator", "n/a", 0);

    bank->setControlStatus(-1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(hippopotamusAlligator_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), -1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlOpen_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::OpenPending);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlOpen_error);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::OpenQuestionable);
}


BOOST_FIXTURE_TEST_CASE(test_default_control_close, defaultGlobals)
{

    const CtiReturnMsg controlClose_noError (6, "control close", "n/a", 0);
    const CtiReturnMsg controlClose_error   (6, "control close", "n/a", 1);
    const CtiReturnMsg giraffeBaboon_noError(6, "giraffe baboon", "n/a", 0);

    bank->setControlStatus(-1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(giraffeBaboon_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), -1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlClose_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::ClosePending);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlClose_error);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::CloseQuestionable);
}

BOOST_FIXTURE_TEST_CASE(test_control_flip, defaultGlobals)
{
    const CtiReturnMsg controlFlip_noError  (6, "control flip", "n/a", 0);
    const CtiReturnMsg controlFlip_error    (6, "control flip", "n/a", 1);

    bank->setControlStatus(CtiCCCapBank::ClosePending);
    
    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlFlip_noError);
    
    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::ClosePending);
    
    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlFlip_error);
    
    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::CloseFail);
    
    bank->setControlStatus(CtiCCCapBank::OpenPending);
    
    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlFlip_noError);
    
    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::OpenPending);
    
    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlFlip_error);
    
    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::OpenFail);
}

BOOST_FIXTURE_TEST_CASE(test_explicit_control_open, defaultGlobals)
{
    const CtiReturnMsg controlOpen_noError          (6, "control open", "n/a", 0);
    const CtiReturnMsg controlOpen_error            (6, "control open", "n/a", 1);
    const CtiReturnMsg hippopotamusAlligator_noError(6, "hippopotamus alligator", "n/a", 0);

    bank->setControlStatus(-1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(hippopotamusAlligator_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), -1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlOpen_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::OpenPending);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlOpen_error);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::OpenQuestionable);
}

BOOST_FIXTURE_TEST_CASE(test_explicit_control_close, defaultGlobals)
{
    const CtiReturnMsg controlClose_noError (6, "control close", "n/a", 0);
    const CtiReturnMsg controlClose_error   (6, "control close", "n/a", 1);
    const CtiReturnMsg giraffeBaboon_noError(6, "giraffe baboon", "n/a", 0);

    bank->setControlStatus(-1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(giraffeBaboon_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), -1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlClose_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::ClosePending);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlClose_error);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::CloseQuestionable);
}

BOOST_FIXTURE_TEST_CASE(test_custom_control_open, customGlobals)
{
    const CtiReturnMsg controlOpen_noError          (6, "control open", "n/a", 0);
    const CtiReturnMsg hippopotamusAlligator_noError(6, "hippopotamus alligator", "n/a", 0);
    const CtiReturnMsg hippopotamusAlligator_error  (6, "hippopotamus alligator", "n/a", 1);

    bank->setControlStatus(-1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlOpen_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), -1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(hippopotamusAlligator_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::OpenPending);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(hippopotamusAlligator_error);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::OpenQuestionable);
}

BOOST_FIXTURE_TEST_CASE(test_custom_control_close, customGlobals)
{
    const CtiReturnMsg controlClose_noError (6, "control close", "n/a", 0);
    const CtiReturnMsg giraffeBaboon_noError(6, "giraffe baboon", "n/a", 0);
    const CtiReturnMsg giraffeBaboon_error  (6, "giraffe baboon", "n/a", 1);

    bank->setControlStatus(-1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(controlClose_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), -1);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(giraffeBaboon_noError);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::ClosePending);

    bus->setRecentlyControlledFlag(true);
    cc.porterReturnMsg(giraffeBaboon_error);

    BOOST_CHECK_EQUAL(bank->getControlStatus(), CtiCCCapBank::CloseQuestionable);
}

BOOST_AUTO_TEST_SUITE_END()
