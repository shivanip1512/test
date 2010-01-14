

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

#include "ccUnitTestUtil.h"

#include "KVarStrategy.h"
#include "PFactorKWKVarStrategy.h"
#include "VoltStrategy.h"


extern unsigned long _LIKEDAY_OVERRIDE_TIMEOUT;

using boost::unit_test_framework::test_suite;
using namespace std;

void initialize_feeder(CtiCCFeeder * feeder)
{
    feeder->setDisableFlag(FALSE);
    feeder->setLikeDayControlFlag(FALSE);
    feeder->setCurrentVarLoadPointId(1);
    feeder->setCurrentWattLoadPointId(1);
    feeder->setCurrentVoltLoadPointId(1);

    StrategyPtr strategy( new PFactorKWKVarStrategy );
    strategy->setControlMethod( ControlStrategy::IndividualFeederControlMethod );

    feeder->setStrategy(strategy);
}

void initialize_subbus(CtiCCSubstationBus * subBus)
{
    subBus->setDisableFlag(FALSE);
    subBus->setBusUpdatedFlag(FALSE);
    subBus->setLikeDayControlFlag(FALSE);
    subBus->setCurrentVarLoadPointId(1);
    subBus->setCurrentWattLoadPointId(1);
    subBus->setCurrentVoltLoadPointId(1);

    StrategyPtr strategy( new PFactorKWKVarStrategy );
    strategy->setControlMethod( ControlStrategy::IndividualFeederControlMethod );
    subBus->setStrategy(strategy);
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

    CtiTime timeNow  = CtiTime() - 700;//subtract a time period?
    feeder1->setLikeDayControlFlag(FALSE);
    feeder1->getStrategy()->setLikeDayFallBack(true);
    feeder1->setLastVoltPointTime(timeNow);
    feeder1->setLastCurrentVarPointUpdateTime(timeNow);
    feeder1->setLastWattPointTime(timeNow);

    // Subbus should be Volt strategy with IndividualFeeder control method.
    StrategyPtr strategy( new VoltStrategy );
    strategy->setControlMethod( ControlStrategy::IndividualFeederControlMethod );
    subbus->setStrategy(strategy);

    //This should have the feeder check return false,
    //and since it has not changed from false the update flags should be false
    strategy.reset( new KVarStrategy );
    strategy->setControlMethod( ControlStrategy::IndividualFeederControlMethod );
    strategy->setStrategyName("(none)");
    strategy->setLikeDayFallBack(true);
    feeder1->setStrategy(strategy);

    feeder1->setCurrentVoltLoadPointId(0);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(FALSE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == subbus->getBusUpdatedFlag());

    feeder1->getStrategy()->setStrategyName("(something)");//Makes the feeder controlunits be used

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(FALSE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == subbus->getBusUpdatedFlag());

    subbus->setLikeDayControlFlag(FALSE);
    feeder1->setLikeDayControlFlag(FALSE);
    subbus->setBusUpdatedFlag(FALSE);

    strategy.reset( new VoltStrategy );
    strategy->setControlMethod( ControlStrategy::IndividualFeederControlMethod );
    strategy->setStrategyName("(something)");
    strategy->setLikeDayFallBack(true);
    feeder1->setStrategy(strategy);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(FALSE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == subbus->getBusUpdatedFlag());

    strategy.reset( new PFactorKWKVarStrategy );
    strategy->setControlMethod( ControlStrategy::IndividualFeederControlMethod );
    strategy->setStrategyName("(something)");
    strategy->setLikeDayFallBack(true);
    feeder1->setStrategy(strategy);

    feeder1->setCurrentWattLoadPointId(0);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(FALSE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == subbus->getBusUpdatedFlag());

    strategy.reset( new VoltStrategy );
    strategy->setControlMethod( ControlStrategy::IndividualFeederControlMethod );
    strategy->setStrategyName("(something)");
    strategy->setLikeDayFallBack(true);
    feeder1->setStrategy(strategy);

    feeder1->setCurrentVoltLoadPointId(1);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(FALSE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == subbus->getBusUpdatedFlag());

    subbus->setBusUpdatedFlag(FALSE);
    subbus->setLikeDayControlFlag(FALSE);
    feeder1->setLikeDayControlFlag(FALSE);

    strategy.reset( new PFactorKWKVarStrategy );
    strategy->setControlMethod( ControlStrategy::IndividualFeederControlMethod );
    strategy->setStrategyName("(something)");
    strategy->setLikeDayFallBack(true);
    feeder1->setStrategy(strategy);

    feeder1->setCurrentWattLoadPointId(1);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(FALSE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == subbus->getBusUpdatedFlag());

    delete subbus;
    subbus = NULL;
    CtiCCSubstationBusStore::deleteInstance();
}

//Add tests for the other control methods

