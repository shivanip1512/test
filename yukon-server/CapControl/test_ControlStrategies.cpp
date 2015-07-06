#include <boost/test/unit_test.hpp>

#include "ControlStrategy.h"
#include "NoStrategy.h"
#include "IVVCSTrategy.h"
#include "StrategyLoader.h"
#include "test_reader.h"

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

BOOST_AUTO_TEST_CASE(test_Strategy_bool_flag_support)
{
    typedef Cti::Test::StringRow<19>                CCStrategyRow;
    typedef Cti::Test::TestReader<CCStrategyRow>    CCStrategyReader;

    CCStrategyRow columnNames =
    {
        "strategyid",
        "strategyname",
        "controlmethod",
        "maxdailyoperation",
        "maxoperationdisableflag",
        "peakstarttime",
        "peakstoptime",
        "controlinterval",
        "minresponsetime",
        "minconfirmpercent",
        "failurepercent",
        "daysofweek",
        "controlunits",
        "controldelaytime",
        "controlsendretries",
        "integrateflag",
        "integrateperiod",
        "likedayfallback",
        "enddaysettings"
    };

    std::vector<CCStrategyRow> rowVec
    {
        {
            "2",
            "KVar Strategy",
            "INDIVIDUAL_FEEDER",
            "0",
            "N",    // <----
            "0",
            "86400",
            "900",
            "900",
            "75",
            "25",
            "NYYYYYNN",
            "KVAR",
            "0",
            "0",
            "n",    // <----
            "0",
            "0",    // <----
            "(none)"
        },
        {
            "32",
            "IVVC Strategy",
            "SUBSTATION_BUS",
            "0",
            "Y",    // <----
            "0",
            "86400",
            "60",
            "900",
            "75",
            "25",
            "NYYYYYNN",
            "INTEGRATED_VOLT_VAR",
            "0",
            "0",
            "y",    // <----
            "0",
            "1",    // <----
            "(none)"
        }
    };

    StrategyManager::StrategyMap    strategies;

    CCStrategyReader reader( columnNames, rowVec );

    for ( const auto & row : rowVec )
    {
        reader();
        StrategyLoader::parseCoreReader( reader, strategies );
    }

    BOOST_REQUIRE_EQUAL( 2, strategies.size() );

    {
        StrategyManager::SharedPtr  strategy = strategies[ 2 ];

        BOOST_REQUIRE( strategy );

        BOOST_CHECK_EQUAL( false, strategy->getMaxOperationDisableFlag() );
        BOOST_CHECK_EQUAL( false, strategy->getIntegrateFlag() );
        BOOST_CHECK_EQUAL( false, strategy->getLikeDayFallBack() );
    }

    {
        StrategyManager::SharedPtr  strategy = strategies[ 32 ];

        BOOST_REQUIRE( strategy );

        BOOST_CHECK_EQUAL( true, strategy->getMaxOperationDisableFlag() );
        BOOST_CHECK_EQUAL( true, strategy->getIntegrateFlag() );
        BOOST_CHECK_EQUAL( true, strategy->getLikeDayFallBack() );
    }
}

BOOST_AUTO_TEST_SUITE_END()
