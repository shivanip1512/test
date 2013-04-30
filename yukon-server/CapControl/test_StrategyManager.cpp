#include <boost/test/unit_test.hpp>

#include "StrategyManager.h"
#include "StrategyLoader.h"
#include "KVarStrategy.h"
#include "NoStrategy.h"
#include "PFactorKWKVarStrategy.h"

#include "ccunittestutil.h"

BOOST_AUTO_TEST_SUITE( test_StrategyManager )


using Cti::Test::CapControl::StrategyUnitTestLoader;


BOOST_AUTO_TEST_CASE(test_StrategyManager_default_initialization)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    // Check them - would expect that they all return NoStrategy

    StrategyManager::SharedPtr strategy = _strategyManager.getStrategy( 100 );

    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reloadAll)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reloadAll();

    // OK - we should have 3 strategies loaded, one each at ID == 100, 110 and 125

    StrategyManager::SharedPtr strategy = _strategyManager.getStrategy( 100 );

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
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reloadAll_get_nonexistent_strategy)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reloadAll();

    // OK - we should have 3 strategies loaded, one each at ID == 100, 110 and 125

    // Get a strategy that doesn't exist - would expect it to return NoStrategy

    StrategyManager::SharedPtr strategy = _strategyManager.getStrategy( 200 );

    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reloadAll_then_unloadAll)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reloadAll();

    // OK - we should have 3 strategies loaded, one each at ID == 100, 110 and 125

    _strategyManager.unloadAll();

    // Check them - would expect that they all return NoStrategy

    StrategyManager::SharedPtr strategy = _strategyManager.getStrategy( 100 );

    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 110 );

    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 125 );

    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reloadAll_then_unload_single)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reloadAll();

    // OK - we should have 3 strategies loaded, one each at ID == 100, 110 and 125

    _strategyManager.unload(110);

    // Check them - would expect that ID == 110 will return NoStrategy

    StrategyManager::SharedPtr strategy = _strategyManager.getStrategy( 100 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   100 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "Test kVAr Strategy" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::KVar );

    strategy = _strategyManager.getStrategy( 110 );

    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 125 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   125 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "Test Power Factor Strategy #2" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::PFactorKWKVar );
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reload_single)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reload(125);

    // Check them - would expect that that all return NoStrategy except ID == 125

    StrategyManager::SharedPtr strategy = _strategyManager.getStrategy( 100 );

    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 110 );

    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::None );

    strategy = _strategyManager.getStrategy( 125 );

    BOOST_CHECK_EQUAL( strategy->getStrategyId(),   125 );
    BOOST_CHECK_EQUAL( strategy->getStrategyName(), "Test Power Factor Strategy #2" );
    BOOST_CHECK_EQUAL( strategy->getUnitType(),     ControlStrategy::PFactorKWKVar );
}


BOOST_AUTO_TEST_CASE(test_StrategyManager_reload_single_weak_pointer_invalidation_check)
{
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reloadAll();

    StrategyManager::WeakPtr   weak( _strategyManager.getStrategy( 110 ) );

    BOOST_CHECK_EQUAL( weak.expired(), false );

    _strategyManager.reload( 110 );

    BOOST_CHECK_EQUAL( weak.expired(), true );
}

BOOST_AUTO_TEST_SUITE_END()
