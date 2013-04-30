#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "ccunittestutil.h"

using namespace Cti::Test::CapControl;
using namespace Cti::CapControl;

BOOST_AUTO_TEST_SUITE( test_capcontroller )

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

    {
        //  Note that we have not set the control strings yet, so the point has none.
        //p.setStateZeroControl("");
        //p.setStateOneControl("");

        attributeBackdoor.points[p.getPointId()] = p;

        //  Verify default "control open"
        {
            bank->setControlStatus(-1);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "hippopotamus alligator", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control open", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenPending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control open", 1, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenQuestionable );
        }

        //  Verify default "control close"
        {
            bank->setControlStatus(-1);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "giraffe baboon", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control close", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::ClosePending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control close", 1, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::CloseQuestionable );
        }

        //  Verify "control flip" (cannot be customized)
        {
            bank->setControlStatus(CtiCCCapBank::ClosePending);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control flip", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::ClosePending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control flip", 1, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::CloseFail );

            bank->setControlStatus(CtiCCCapBank::OpenPending);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control flip", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenPending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control flip", 1, "n/a");

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
            cc.porterReturnMsg(6, "hippopotamus alligator", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control open", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenPending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control open", 1, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenQuestionable );
        }

        //  Verify explicit "control close"
        {
            bank->setControlStatus(-1);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "giraffe baboon", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control close", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::ClosePending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control close", 1, "n/a");

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
            cc.porterReturnMsg(6, "control open", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "hippopotamus alligator", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenPending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "hippopotamus alligator", 1, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::OpenQuestionable );
        }

        //  Verify custom "control close"
        {
            bank->setControlStatus(-1);

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "control close", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), -1 );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "giraffe baboon", 0, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::ClosePending );

            bus->setRecentlyControlledFlag(true);
            cc.porterReturnMsg(6, "giraffe baboon", 1, "n/a");

            BOOST_CHECK_EQUAL( bank->getControlStatus(), CtiCCCapBank::CloseQuestionable );
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()
