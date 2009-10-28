

#define BOOST_AUTO_TEST_MAIN "Test LikeDayControl"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <string>
#include <time.h>
#include <locale>

#include "yukon.h"
#include "ctitime.h"
#include "ccfeeder.h"
#include "ccsubstationbus.h"
#include "ccsubstationbusstore.h"

#include "ccUnitTestUtil.h"

extern unsigned long _LIKEDAY_OVERRIDE_TIMEOUT;

using boost::unit_test_framework::test_suite;
using namespace std;

void initialize_feeder(CtiCCFeeder * feeder)
{
    feeder->setDisableFlag(false);
    feeder->setControlMethod(CtiCCSubstationBus::IndividualFeederControlMethod);
    feeder->setLikeDayControlFlag(false);
    feeder->setCurrentVarLoadPointId(1);
    feeder->setCurrentWattLoadPointId(1);
    feeder->setCurrentVoltLoadPointId(1);
    feeder->setControlUnits(CtiCCSubstationBus::PF_BY_KVARControlUnits);
}

void initialize_subbus(CtiCCSubstationBus * subBus)
{
    subBus->setDisableFlag(false);
    subBus->setBusUpdatedFlag(false);
    subBus->setControlMethod(CtiCCSubstationBus::IndividualFeederControlMethod);
    subBus->setLikeDayControlFlag(false);
    subBus->setCurrentVarLoadPointId(1);
    subBus->setCurrentWattLoadPointId(1);
    subBus->setCurrentVoltLoadPointId(1);
    subBus->setControlUnits(CtiCCSubstationBus::PF_BY_KVARControlUnits);
}

BOOST_AUTO_TEST_CASE(test_substationBus_likeday_individualfeeder)
{
    //Overloaded Store
    Test_CtiCCSubstationBusStore* testStore = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance(testStore);

    //Build up feeder
    CtiCCFeeder * feeder1 = create_object<CtiCCFeeder>(100, "Feeder1");
    initialize_feeder(feeder1);

    //Build up a substation bus with the feeders.
    CtiCCSubstationBus * subbus = create_object<CtiCCSubstationBus>(99,"Subbus1");
    initialize_subbus(subbus);
    subbus->getCCFeeders().push_back(feeder1);

    _LIKEDAY_OVERRIDE_TIMEOUT = 600;

    CtiTime time  = CtiTime() - 700;//subtract a time period?
    feeder1->setLikeDayControlFlag(false);
    feeder1->setLikeDayFallBack(true);
    feeder1->setLastVoltPointTime(time);
    feeder1->setLastCurrentVarPointUpdateTime(time);
    feeder1->setLastWattPointTime(time);

    //This should have the feeder check return false,
    //and since it has not changed from false the update flags should be false
    subbus->setControlUnits(CtiCCSubstationBus::VoltControlUnits);
    feeder1->setStrategyName("(none)");
    feeder1->setControlUnits(CtiCCSubstationBus::KVARControlUnits);
    feeder1->setCurrentVoltLoadPointId(0);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK_EQUAL(false,subbus->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(false,feeder1->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(false,subbus->getBusUpdatedFlag());

    feeder1->setStrategyName("(something)");//Makes the feeder controlunits be used
    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK_EQUAL(false,subbus->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(true,feeder1->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(true,subbus->getBusUpdatedFlag());

    subbus->setLikeDayControlFlag(false);
    feeder1->setLikeDayControlFlag(false);
    subbus->setBusUpdatedFlag(false);

    feeder1->setControlUnits(CtiCCSubstationBus::VoltControlUnits);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK_EQUAL(false,subbus->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(false,feeder1->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(false,subbus->getBusUpdatedFlag());

    feeder1->setControlUnits(CtiCCSubstationBus::PF_BY_KVARControlUnits);
    feeder1->setCurrentWattLoadPointId(0);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK_EQUAL(false,subbus->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(false,feeder1->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(false,subbus->getBusUpdatedFlag());

    feeder1->setCurrentVoltLoadPointId(1);
    feeder1->setControlUnits(CtiCCSubstationBus::VoltControlUnits);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK_EQUAL(false,subbus->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(true,feeder1->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(true,subbus->getBusUpdatedFlag());

    subbus->setBusUpdatedFlag(false);
    subbus->setLikeDayControlFlag(false);
    feeder1->setLikeDayControlFlag(false);
    feeder1->setControlUnits(CtiCCSubstationBus::PF_BY_KVARControlUnits);
    feeder1->setCurrentWattLoadPointId(1);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK_EQUAL(false,subbus->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(true,feeder1->getLikeDayControlFlag());
    BOOST_CHECK_EQUAL(true,subbus->getBusUpdatedFlag());

    delete subbus;
    subbus = NULL;
    CtiCCSubstationBusStore::deleteInstance();
}

//Add tests for the other control methods
