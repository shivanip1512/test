
#define BOOST_AUTO_TEST_MAIN "Test CapControl Control Strategy Manager"

#include <boost/test/unit_test.hpp>

#include <string>

#include "StrategyManager.h"

using boost::unit_test_framework::test_suite;




class StrategyUnitTestLoader : public StrategyLoader
{

public:

    // default construction and destruction is OK

    virtual void load(const long ID, StrategyMap &strategies)
    {
        if (ID == -1)
        {
            long IDs[] = { 100, 110, 125 };
    
            for (int i = 0; i < sizeof(IDs)/ sizeof(*IDs); i++)
            {
                loadSingle(IDs[i], strategies);
            }
        }
        else
        {
            loadSingle(ID, strategies);
        }
    }

private:

    void loadSingle(const long ID, StrategyMap &strategies)
    {
        bool doInsertion = true;

        StrategyPtr newStrategy;

        switch (ID)
        {
            case 100:
            {
                newStrategy.reset( new KVarStrategy );
                newStrategy->setStrategyName("Test kVAr Strategy");
                break;
            }
            case 110:
            {
                newStrategy.reset( new PFactorKWKVarStrategy );
                newStrategy->setStrategyName("Test kVAr Power Factor Strategy");
                break;
            }
            case 125:
            {
                newStrategy.reset( new PFactorKWKVarStrategy );
                newStrategy->setStrategyName("Test Power Factor Strategy #2");
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
            strategies[ID] = newStrategy;
        }
    }
};



BOOST_AUTO_TEST_CASE(test_StrategyManager_default_initialization)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    // Check them - would expect that they all return the ID == 0 strategy

    StrategyPtr strategy = _strategyManager.getStrategy( 100 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 110 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 125 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 0 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reloadAll)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reloadAll();

    // OK - we should have 4 strategies loaded, one each at ID == 100, 110 and 125 and the default at ID == 0

    // Check them

    StrategyPtr strategy = _strategyManager.getStrategy( 100 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   100 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "Test kVAr Strategy" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::KVar );

    strategy = _strategyManager.getStrategy( 110 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   110 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "Test kVAr Power Factor Strategy" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::PFactorKWKVar );

    strategy = _strategyManager.getStrategy( 125 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   125 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "Test Power Factor Strategy #2" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::PFactorKWKVar );

    strategy = _strategyManager.getStrategy( 0 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reloadAll_get_nonexistent_strategy)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reloadAll();

    // OK - we should have 4 strategies loaded, one each at ID == 100, 110 and 125 and the default at ID == 0

    // Get a strategy that doesn't exist - would expect it to return the strategy at ID == 0

    StrategyPtr strategy = _strategyManager.getStrategy( 200 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reloadAll_then_unloadAll)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reloadAll();

    // OK - we should have 4 strategies loaded, one each at ID == 100, 110 and 125 and the default at ID == 0

    _strategyManager.unloadAll();

    // Check them - would expect that they all return the ID == 0 strategy

    StrategyPtr strategy = _strategyManager.getStrategy( 100 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 110 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 125 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 0 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reloadAll_then_unload_single)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reloadAll();

    // OK - we should have 4 strategies loaded, one each at ID == 100, 110 and 125 and the default at ID == 0

    _strategyManager.unload(110);

    // Check them - would expect that ID == 110 will return the ID == 0 strategy

    StrategyPtr strategy = _strategyManager.getStrategy( 100 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   100 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "Test kVAr Strategy" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::KVar );

    strategy = _strategyManager.getStrategy( 110 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 125 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   125 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "Test Power Factor Strategy #2" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::PFactorKWKVar );

    strategy = _strategyManager.getStrategy( 0 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reload_single)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reload(125);

    // Check them - would expect that that all return default strategy except ID == 125

    StrategyPtr strategy = _strategyManager.getStrategy( 100 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 110 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 125 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   125 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "Test Power Factor Strategy #2" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::PFactorKWKVar );

    strategy = _strategyManager.getStrategy( 0 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   0 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "(none)" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );
}

