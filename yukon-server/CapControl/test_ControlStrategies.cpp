
#define BOOST_AUTO_TEST_MAIN "Test CapControl Control Strategies"

#include <boost/test/unit_test.hpp>
#include <string>

#include "ControlStrategy.h"
#include "NoStrategy.h"

using boost::unit_test_framework::test_suite;
using namespace std;


BOOST_AUTO_TEST_CASE(test_NoStrategy_default_creation)
{
    NoStrategy   noStrategy;

    BOOST_CHECK_EQUAL( noStrategy.getUnitType(),                ControlStrategy::None );
    BOOST_CHECK_EQUAL( noStrategy.getMethodType(),              ControlStrategy::NoMethod );

    BOOST_CHECK_EQUAL( noStrategy.getStrategyId(),              0 );
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

