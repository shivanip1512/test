#include <boost/test/unit_test.hpp>

#include "ControlStrategy.h"
#include "NoStrategy.h"
#include "IVVCSTrategy.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_ControlStrategies )

BOOST_AUTO_TEST_CASE(test_NoStrategy_default_creation)
{
    NoStrategy   noStrategy;

    BOOST_CHECK_EQUAL( noStrategy.getUnitType(),                ControlStrategy::None );
    BOOST_CHECK_EQUAL( noStrategy.getMethodType(),              ControlStrategy::NoMethod );

    BOOST_CHECK_EQUAL( noStrategy.getStrategyId(),              -1 );
    BOOST_CHECK_EQUAL( noStrategy.getStrategyName(),            "(none)" );
    BOOST_CHECK_EQUAL( noStrategy.getControlMethod(),           ControlStrategy::NoControlMethod );
    BOOST_CHECK_EQUAL( noStrategy.getControlInterval(),         0 );
    BOOST_CHECK_EQUAL( noStrategy.getMaxConfirmTime(),          0 );
    BOOST_CHECK_EQUAL( noStrategy.getMinConfirmPercent(),       0 );
    BOOST_CHECK_EQUAL( noStrategy.getFailurePercent(),          0 );
    BOOST_CHECK_EQUAL( noStrategy.getControlSendRetries(),      0 );
    BOOST_CHECK_EQUAL( noStrategy.getControlDelayTime(),        0 );
    BOOST_CHECK_EQUAL( noStrategy.getIntegrateFlag(),           false );
    BOOST_CHECK_EQUAL( noStrategy.getIntegratePeriod(),         0 );
    BOOST_CHECK_EQUAL( noStrategy.getLikeDayFallBack(),         false );
    BOOST_CHECK_EQUAL( noStrategy.getMaxDailyOperation(),       0 );
    BOOST_CHECK_EQUAL( noStrategy.getMaxOperationDisableFlag(), false );
    BOOST_CHECK_EQUAL( noStrategy.getEndDaySettings(),          "(none)" );
    BOOST_CHECK_EQUAL( noStrategy.getControlUnits(),            ControlStrategy::NoControlUnit );
    BOOST_CHECK_EQUAL( noStrategy.getPeakStartTime(),           0 );
    BOOST_CHECK_EQUAL( noStrategy.getPeakStopTime(),            0 );
    BOOST_CHECK_EQUAL( noStrategy.getDaysOfWeek(),              "NNNNNNNN" );

    BOOST_CHECK_EQUAL( noStrategy.getPeakLag(),                 0.0 );
    BOOST_CHECK_EQUAL( noStrategy.getOffPeakLag(),              0.0 );
    BOOST_CHECK_EQUAL( noStrategy.getPeakLead(),                0.0 );
    BOOST_CHECK_EQUAL( noStrategy.getOffPeakLead(),             0.0 );
    BOOST_CHECK_EQUAL( noStrategy.getPeakVARLag(),              0.0 );
    BOOST_CHECK_EQUAL( noStrategy.getOffPeakVARLag(),           0.0 );
    BOOST_CHECK_EQUAL( noStrategy.getPeakVARLead(),             0.0 );
    BOOST_CHECK_EQUAL( noStrategy.getOffPeakVARLead(),          0.0 );
    BOOST_CHECK_EQUAL( noStrategy.getPeakPFSetPoint(),          0.0 );
    BOOST_CHECK_EQUAL( noStrategy.getOffPeakPFSetPoint(),       0.0 );

    noStrategy.setStrategyId(101);
    noStrategy.setStrategyName("Test Strategy");
    noStrategy.setControlMethod(ControlStrategy::IndividualFeederControlMethod);
    noStrategy.setControlInterval(1);
    noStrategy.setMaxConfirmTime(2);
    noStrategy.setMinConfirmPercent(3);
    noStrategy.setFailurePercent(4);
    noStrategy.setControlSendRetries(5);
    noStrategy.setControlDelayTime(6);
    noStrategy.setIntegrateFlag(true);
    noStrategy.setIntegratePeriod(7);
    noStrategy.setLikeDayFallBack(true);
    noStrategy.setMaxDailyOperation(8);
    noStrategy.setMaxOperationDisableFlag(true);
    noStrategy.setEndDaySettings("X");
    noStrategy.setPeakStartTime(1000);
    noStrategy.setPeakStopTime(2000);
    noStrategy.setDaysOfWeek("YYYYYYYY");

    BOOST_CHECK_EQUAL( noStrategy.getUnitType(),                ControlStrategy::None );
    BOOST_CHECK_EQUAL( noStrategy.getMethodType(),              ControlStrategy::NoMethod );

    BOOST_CHECK_EQUAL( noStrategy.getStrategyId(),              101 );
    BOOST_CHECK_EQUAL( noStrategy.getStrategyName(),            "Test Strategy");
    BOOST_CHECK_EQUAL( noStrategy.getControlMethod(),           ControlStrategy::NoControlMethod );
    BOOST_CHECK_EQUAL( noStrategy.getControlInterval(),         1 );
    BOOST_CHECK_EQUAL( noStrategy.getMaxConfirmTime(),          2 );
    BOOST_CHECK_EQUAL( noStrategy.getMinConfirmPercent(),       3 );
    BOOST_CHECK_EQUAL( noStrategy.getFailurePercent(),          4 );
    BOOST_CHECK_EQUAL( noStrategy.getControlSendRetries(),      5 );
    BOOST_CHECK_EQUAL( noStrategy.getControlDelayTime(),        6 );
    BOOST_CHECK_EQUAL( noStrategy.getIntegrateFlag(),           true );
    BOOST_CHECK_EQUAL( noStrategy.getIntegratePeriod(),         7 );
    BOOST_CHECK_EQUAL( noStrategy.getLikeDayFallBack(),         true );
    BOOST_CHECK_EQUAL( noStrategy.getMaxDailyOperation(),       8 );
    BOOST_CHECK_EQUAL( noStrategy.getMaxOperationDisableFlag(), true );
    BOOST_CHECK_EQUAL( noStrategy.getEndDaySettings(),          "X" );
    BOOST_CHECK_EQUAL( noStrategy.getControlUnits(),            ControlStrategy::NoControlUnit );
    BOOST_CHECK_EQUAL( noStrategy.getPeakStartTime(),           1000 );
    BOOST_CHECK_EQUAL( noStrategy.getPeakStopTime(),            2000 );
    BOOST_CHECK_EQUAL( noStrategy.getDaysOfWeek(),              "YYYYYYYY" );
}


BOOST_AUTO_TEST_CASE(test_IVVCStrategy_control_method_and_unit)
{
    IVVCStrategy    strategy( PointDataRequestFactoryPtr( new PointDataRequestFactory ) );

    // Default

    BOOST_CHECK_EQUAL( strategy.getControlUnits(),  ControlStrategy::IntegratedVoltVarControlUnit );
    BOOST_CHECK_EQUAL( strategy.getUnitType(),      ControlStrategy::IntegratedVoltVar );

    BOOST_CHECK_EQUAL( strategy.getControlMethod(), ControlStrategy::SubstationBusControlMethod );
    BOOST_CHECK_EQUAL( strategy.getMethodType(),    ControlStrategy::SubstationBus );

    // Change control method to 'BusOptimizedFeeder'

    strategy.setControlMethod( ControlStrategy::BusOptimizedFeederControlMethod );

    BOOST_CHECK_EQUAL( strategy.getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod );
    BOOST_CHECK_EQUAL( strategy.getMethodType(),    ControlStrategy::BusOptimizedFeeder );

    // Back to 'SubstationBus'

    strategy.setControlMethod( ControlStrategy::SubstationBusControlMethod );

    BOOST_CHECK_EQUAL( strategy.getControlMethod(), ControlStrategy::SubstationBusControlMethod );
    BOOST_CHECK_EQUAL( strategy.getMethodType(),    ControlStrategy::SubstationBus );

    // Any other inputs

    strategy.setControlMethod( ControlStrategy::NoControlMethod );

    BOOST_CHECK_EQUAL( strategy.getControlMethod(), ControlStrategy::SubstationBusControlMethod );
    BOOST_CHECK_EQUAL( strategy.getMethodType(),    ControlStrategy::SubstationBus );

    strategy.setControlMethod( ControlStrategy::IndividualFeederControlMethod );

    BOOST_CHECK_EQUAL( strategy.getControlMethod(), ControlStrategy::SubstationBusControlMethod );
    BOOST_CHECK_EQUAL( strategy.getMethodType(),    ControlStrategy::SubstationBus );

    strategy.setControlMethod( ControlStrategy::ManualOnlyControlMethod );

    BOOST_CHECK_EQUAL( strategy.getControlMethod(), ControlStrategy::SubstationBusControlMethod );
    BOOST_CHECK_EQUAL( strategy.getMethodType(),    ControlStrategy::SubstationBus );

    strategy.setControlMethod( ControlStrategy::TimeOfDayControlMethod );

    BOOST_CHECK_EQUAL( strategy.getControlMethod(), ControlStrategy::SubstationBusControlMethod );
    BOOST_CHECK_EQUAL( strategy.getMethodType(),    ControlStrategy::SubstationBus );

    strategy.setControlMethod( "" );

    BOOST_CHECK_EQUAL( strategy.getControlMethod(), ControlStrategy::SubstationBusControlMethod );
    BOOST_CHECK_EQUAL( strategy.getMethodType(),    ControlStrategy::SubstationBus );

    strategy.setControlMethod( "frobnoztication" );

    BOOST_CHECK_EQUAL( strategy.getControlMethod(), ControlStrategy::SubstationBusControlMethod );
    BOOST_CHECK_EQUAL( strategy.getMethodType(),    ControlStrategy::SubstationBus );
}

BOOST_AUTO_TEST_SUITE_END()
