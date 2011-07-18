

#define BOOST_AUTO_TEST_MAIN "Test LikeDayControl"

#include "precompiled.h"
#include "ctitime.h"
#include "ccfeeder.h"
#include "ccsubstationbus.h"

#include "ccUnitTestUtil.h"

#include "StrategyManager.h"
#include "KVarStrategy.h"
#include "PFactorKWKVarStrategy.h"
#include "VoltStrategy.h"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <string>
#include <time.h>
#include <locale>

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
}

void initialize_subbus(CtiCCSubstationBus * subBus)
{
    subBus->setDisableFlag(FALSE);
    subBus->setBusUpdatedFlag(FALSE);
    subBus->setLikeDayControlFlag(FALSE);
    subBus->setCurrentVarLoadPointId(1);
    subBus->setCurrentWattLoadPointId(1);
    subBus->setCurrentVoltLoadPointId(1);
}

class StrategyUnitTestLoader : public StrategyLoader
{

public:

    // default construction and destruction is OK

    virtual StrategyManager::StrategyMap load(const long ID)
    {
        StrategyManager::StrategyMap loaded;

        if (ID < 0)
        {
            long IDs[] = { 10, 100, 200, 300 };

            for (int i = 0; i < sizeof(IDs)/ sizeof(*IDs); i++)
            {
                loadSingle(IDs[i], loaded);
            }
        }
        else
        {
            loadSingle(ID, loaded);
        }

        return loaded;
    }

private:

    void loadSingle(const long ID, StrategyManager::StrategyMap &strategies)
    {
        bool doInsertion = true;

        StrategyManager::SharedPtr  newStrategy;

        switch (ID)
        {
            case 10:
            {
                newStrategy.reset( new VoltStrategy );
                break;
            }
            case 100:
            {
                newStrategy.reset( new KVarStrategy );
                newStrategy->setStrategyName("(none)");
                newStrategy->setLikeDayFallBack(true);
                break;
            }
            case 200:
            {
                newStrategy.reset( new VoltStrategy );
                newStrategy->setStrategyName("(something)");
                newStrategy->setLikeDayFallBack(true);
                break;
            }
            case 300:
            {
                newStrategy.reset( new PFactorKWKVarStrategy );
                newStrategy->setStrategyName("(something)");
                newStrategy->setLikeDayFallBack(true);
                break;
            }
            default:
            {
                doInsertion = false;
                break;
            }
        }

        if (doInsertion)
        {
            newStrategy->setStrategyId(ID);
            newStrategy->setControlMethod( ControlStrategy::IndividualFeederControlMethod );
            strategies[ID] = newStrategy;
        }
    }
};

BOOST_AUTO_TEST_CASE(test_substationBus_likeday_individualfeeder)
{
    //Overloaded Store
    Test_CtiCCSubstationBusStore* testStore = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance(testStore);

    // Initialize the Strategy Manager.
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );
    _strategyManager.reloadAll();

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
    feeder1->setLastVoltPointTime(timeNow);
    feeder1->setLastCurrentVarPointUpdateTime(timeNow);
    feeder1->setLastWattPointTime(timeNow);

    // since we can't pass the manager in on object creation we attach it here.
    subbus->setStrategyManager( &_strategyManager );
    feeder1->setStrategyManager( &_strategyManager );

    // Subbus should be Volt strategy with IndividualFeeder control method.
    subbus->setStrategy(10);

    //This should have the feeder check return false,
    //and since it has not changed from false the update flags should be false

    feeder1->setStrategy(100);
    feeder1->setCurrentVoltLoadPointId(0);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(FALSE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == subbus->getBusUpdatedFlag());

    feeder1->getStrategy()->setStrategyName("(something)");//Makes the feeder controlunits be used

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(TRUE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == subbus->getBusUpdatedFlag());

    subbus->setLikeDayControlFlag(FALSE);
    feeder1->setLikeDayControlFlag(FALSE);
    subbus->setBusUpdatedFlag(FALSE);

    feeder1->setStrategy(200);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(FALSE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == subbus->getBusUpdatedFlag());

    feeder1->setStrategy(300);

    feeder1->setCurrentWattLoadPointId(0);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(FALSE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(FALSE == subbus->getBusUpdatedFlag());

    feeder1->setStrategy(200);

    feeder1->setCurrentVoltLoadPointId(1);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(TRUE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == subbus->getBusUpdatedFlag());

    subbus->setBusUpdatedFlag(FALSE);
    subbus->setLikeDayControlFlag(FALSE);
    feeder1->setLikeDayControlFlag(FALSE);

    feeder1->setStrategy(300);

    feeder1->setCurrentWattLoadPointId(1);

    subbus->performDataOldAndFallBackNecessaryCheck();

    BOOST_CHECK(TRUE == subbus->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == feeder1->getLikeDayControlFlag());
    BOOST_CHECK(TRUE == subbus->getBusUpdatedFlag());

    delete subbus;
    subbus = NULL;
    CtiCCSubstationBusStore::deleteInstance();
}

//Add tests for the other control methods

