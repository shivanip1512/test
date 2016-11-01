#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "ccunittestutil.h"
#include "msg_pcreturn.h"

#include "boost_test_helpers.h"
#include "capcontrol_test_helpers.h"

using namespace Cti::Test::CapControl;
using namespace Cti::CapControl;

struct overrideGlobals
{
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    overrideGlobals() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
    }
};

BOOST_FIXTURE_TEST_SUITE( test_capcontroller, overrideGlobals )

struct test_AttributeService : AttributeService
{
    std::map<int, LitePoint> points;

    virtual LitePoint getLitePointById(int pointid)
    {
        return points[pointid];
    }
};

struct test_CtiCapController : CtiCapController
{
    using CtiCapController::porterReturnMsg;
};


BOOST_AUTO_TEST_CASE( test_porterReturnMsg_oneway_device )
{
    Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();

    CtiCCSubstationBusStore::setInstance(store);

    CtiCCAreaPtr         area       = create_object<CtiCCArea>           (1, "test area");
    CtiCCSubstation     *substation = create_object<CtiCCSubstation>     (2, "test substation");
    CtiCCSubstationBus  *bus        = create_object<CtiCCSubstationBus>  (3, "test bus");
    CtiCCFeeder         *feeder     = create_object<CtiCCFeeder>         (4, "test feeder");
    CtiCCCapBank        *bank       = create_object<CtiCCCapBank>        (5, "test cap bank");

    initialize_area   (store, area);
    initialize_station(store, substation, area, Cti::Test::use_in_unit_tests_only{});
    initialize_bus    (store, bus, substation);
    initialize_feeder (store, feeder, bus, 1);
    initialize_capbank(store, bank, feeder, 1);

    bank->setControlDeviceId(6);
    bank->setControlPointId(7);
    bank->setControlDeviceType("cbc-3010");

    store->addCapBankToCBCMap(bank);

    std::auto_ptr<test_AttributeService> attributeService(new test_AttributeService);

    test_AttributeService &attributeBackdoor = *attributeService;

    store->setAttributeService(std::auto_ptr<AttributeService>(attributeService.release()));

    LitePoint p;

    p.setPaoId(6);
    p.setPointId(7);

    attributeBackdoor.points[p.getPointId()] = p;

    test_CtiCapController cc;

    const CtiReturnMsg controlOpen_noError (6, "control open",  "n/a", 0);
    const CtiReturnMsg controlOpen_error   (6, "control open",  "n/a", 1);

    const CtiReturnMsg controlClose_noError(6, "control close", "n/a", 0);
    const CtiReturnMsg controlClose_error  (6, "control close", "n/a", 1);

    const CtiReturnMsg hippopotamusAlligator_noError(6, "hippopotamus alligator", "n/a", 0);
    const CtiReturnMsg hippopotamusAlligator_error  (6, "hippopotamus alligator", "n/a", 1);

    const CtiReturnMsg giraffeBaboon_noError(6, "giraffe baboon", "n/a", 0);
    const CtiReturnMsg giraffeBaboon_error  (6, "giraffe baboon", "n/a", 1);

    const CtiReturnMsg controlFlip_noError (6, "control flip", "n/a", 0);
    const CtiReturnMsg controlFlip_error   (6, "control flip", "n/a", 1);

    {
        //  Note that we have not set the control strings yet, so the point has none.
        //p.setStateZeroControl("");
        //p.setStateOneControl("");

        attributeBackdoor.points[p.getPointId()] = p;

        //  Verify default "control open"
        {
            bank->setControlStatus(-1);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(hippopotamusAlligator_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlOpen_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenPending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlOpen_error);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenQuestionable );
        }

        //  Verify default "control close"
        {
            bank->setControlStatus(-1);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(giraffeBaboon_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlClose_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::ClosePending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlClose_error);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::CloseQuestionable );
        }

        //  Verify "control flip" (cannot be customized)
        {
            bank->setControlStatus(CtiCCCapBank::ClosePending);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlFlip_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::ClosePending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlFlip_error);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::CloseFail );

            bank->setControlStatus(CtiCCCapBank::OpenPending);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlFlip_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenPending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlFlip_error);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenFail );
        }
    }
    {
        p.setStateZeroControl("control open");
        p.setStateOneControl("control close");

        attributeBackdoor.points[p.getPointId()] = p;

        //  Verify explicit "control open"
        {
            bank->setControlStatus(-1);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(hippopotamusAlligator_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlOpen_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenPending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlOpen_error);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenQuestionable );
        }

        //  Verify explicit "control close"
        {
            bank->setControlStatus(-1);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(giraffeBaboon_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlClose_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::ClosePending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlClose_error);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::CloseQuestionable );
        }
    }
    {
        p.setStateZeroControl("hippopotamus alligator");
        p.setStateOneControl("giraffe baboon");

        attributeBackdoor.points[p.getPointId()] = p;

        //  Verify custom "control open"
        {
            bank->setControlStatus(-1);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlOpen_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(hippopotamusAlligator_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenPending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(hippopotamusAlligator_error);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenQuestionable );
        }

        //  Verify custom "control close"
        {
            bank->setControlStatus(-1);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(controlClose_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(giraffeBaboon_noError);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::ClosePending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(giraffeBaboon_error);

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::CloseQuestionable );
        }
    }

    delete substation;
    delete area;
}

BOOST_AUTO_TEST_SUITE_END()
