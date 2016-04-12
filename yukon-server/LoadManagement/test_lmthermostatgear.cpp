#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>
#include <boost/scoped_ptr.hpp>

#include "lmutility.h"
#include "test_reader.h"

#include "lmProgramThermostatGear.h"
#include "lmGroupExpresscom.h"



struct thermostat_gear_settings_helper
{
    typedef Cti::Test::StringRow<42>            LMGearRow;
    typedef Cti::Test::TestReader<LMGearRow>    LMGearReader;

    LMGearRow   columnNames,
                columnValues;

    const std::size_t   settingsIndex,
                        controlMethodIndex;

    boost::scoped_ptr<CtiLMGroupExpresscom>     group;

    std::string logMessage;

    thermostat_gear_settings_helper()
        :   settingsIndex( 28 ),
            controlMethodIndex( 3 ),
            group( new CtiLMGroupExpresscom )
    {
        group->setPAOId( 1234 );

        LMGearRow temp_columnNames =
        {
            "DeviceID",
            "GearName",
            "GearNumber",
            "ControlMethod",
            "MethodRate",
            "MethodPeriod",
            "MethodRateCount",
            "CycleRefreshRate",
            "MethodStopType",
            "ChangeCondition",
            "ChangeDuration",
            "ChangePriority",
            "ChangeTriggerNumber",
            "ChangeTriggerOffset",
            "PercentReduction",
            "GroupSelectionMethod",
            "MethodOptionType",
            "MethodOptionMax",
            "GearID",
            "RampInInterval",
            "RampInPercent",
            "RampOutInterval",
            "RampOutPercent",
            "FrontRampOption",
            "FrontRampTime",
            "BackRampOption",
            "BackRampTime",
            "KwReduction",
            "Settings",
            "MinValue",
            "MaxValue",
            "ValueB",
            "ValueD",
            "ValueF",
            "Random",
            "ValueTA",
            "ValueTB",
            "ValueTC",
            "ValueTD",
            "ValueTE",
            "ValueTF",
            "RampRate"
        };

        columnNames = temp_columnNames;

        LMGearRow temp_columnValues =
        {
            "101",
            "Gear",
            "2",
            "<uninitialized>",
            "5",
            "6",
            "7",
            "8",
            "Restore",
            "None",
            "9",
            "10",
            "11",
            "1.2",
            "10",
            "LastControlled",
            "FixedCount",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "(none)",
            "19",
            "(none)",
            "20",
            "3.4",
            "<uninitialized>",
            "1",
            "255",
            "-1",
            "3",
            "-2",
            "60",
            "30",
            "60",
            "45",
            "120",
            "75",
            "90",
            "1"
        };

        columnValues = temp_columnValues;
    }
};


BOOST_AUTO_TEST_SUITE( test_lmthermostatgear )

/*
    Thermostat Ramping
*/
BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_delta, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "DCH-";

    LMGearReader reader( columnNames, std::vector<LMGearRow>({ columnValues }));
    reader();

    boost::scoped_ptr<CtiLMProgramThermostatGear>   gear( new CtiLMProgramThermostatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "DCH-" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( *gear,
                                            12,
                                            logMessage ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint delta celsius mode heat "
                       "min 1 max 255 tr 60 ta 30 tb 60 dsb -1 tc 45 td 120 dsd 3 te 75 tf 90 dsf -2",
                       m->CommandString() );

    BOOST_CHECK_EQUAL( "heat, 30m hold, -1 over 60m, 45m hold, +3 over 120m, 75m hold, -2 over 90m, 60m rand",
                       logMessage );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_no_delta, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "ACH-";

    LMGearReader reader( columnNames, std::vector<LMGearRow>({ columnValues }));
    reader();

    boost::scoped_ptr<CtiLMProgramThermostatGear>   gear( new CtiLMProgramThermostatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "ACH-" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( *gear,
                                            12,
                                            logMessage ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint celsius mode heat "
                       "min 1 max 255 tr 60 ta 30 tb 60 dsb -1 tc 45 td 120 dsd 3 te 75 tf 90 dsf -2",
                       m->CommandString() );

    BOOST_CHECK_EQUAL( "heat, 30m hold, -1 over 60m, 45m hold, +3 over 120m, 75m hold, -2 over 90m, 60m rand",
                       logMessage );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_no_celsius, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "AFH-";

    LMGearReader reader( columnNames, std::vector<LMGearRow>({ columnValues }));
    reader();

    boost::scoped_ptr<CtiLMProgramThermostatGear>   gear( new CtiLMProgramThermostatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "AFH-" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( *gear,
                                            12,
                                            logMessage ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint mode heat "
                       "min 1 max 255 tr 60 ta 30 tb 60 dsb -1 tc 45 td 120 dsd 3 te 75 tf 90 dsf -2",
                       m->CommandString() );

    BOOST_CHECK_EQUAL( "heat, 30m hold, -1 over 60m, 45m hold, +3 over 120m, 75m hold, -2 over 90m, 60m rand",
                       logMessage );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_cool_mode, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "AF-I";

	LMGearReader reader(columnNames, std::vector<LMGearRow>({ columnValues }));
    reader();

    boost::scoped_ptr<CtiLMProgramThermostatGear>   gear( new CtiLMProgramThermostatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "AF-I" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( *gear,
                                            12,
                                            logMessage ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint mode cool "
                       "min 1 max 255 tr 60 ta 30 tb 60 dsb -1 tc 45 td 120 dsd 3 te 75 tf 90 dsf -2",
                       m->CommandString() );

    BOOST_CHECK_EQUAL( "cool, 30m hold, -1 over 60m, 45m hold, +3 over 120m, 75m hold, -2 over 90m, 60m rand",
                       logMessage );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_both_modes, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "AFHI";

    LMGearReader reader( columnNames, std::vector<LMGearRow>( { columnValues } ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermostatGear>   gear( new CtiLMProgramThermostatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "AFHI" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( *gear,
                                            12,
                                            logMessage ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint mode both "
                       "min 1 max 255 tr 60 ta 30 tb 60 dsb -1 tc 45 td 120 dsd 3 te 75 tf 90 dsf -2",
                       m->CommandString() );

    BOOST_CHECK_EQUAL( "both, 30m hold, -1 over 60m, 45m hold, +3 over 120m, 75m hold, -2 over 90m, 60m rand",
                       logMessage );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_no_mode, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "AF--";

    LMGearReader reader( columnNames, std::vector<LMGearRow>( { columnValues } ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermostatGear>   gear( new CtiLMProgramThermostatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "AF--" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( *gear,
                                            12,
                                            logMessage ) );

    BOOST_CHECK( ! m );     // invalid mode string results in a null message

    BOOST_CHECK( logMessage.empty() );
}


/*
    Simple Thermostat Ramping
*/
BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_simple_gear_settings_heat, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "SimpleThermostatRamping";
    columnValues[ settingsIndex      ] = "--H-";

    LMGearReader reader( columnNames, std::vector<LMGearRow>( { columnValues } ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermostatGear>   gear( new CtiLMProgramThermostatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::SimpleThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "--H-" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointSimpleMsg( *gear,
                                           300,
                                           30,
                                           12,
                                           logMessage ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint delta mode heat "
                       "min 1 max 255 tr 60 tb 60 dsb -1 tc 45 td 0 dsd 1 te 105 tf 90 dsf 0 bump stage 30",
                       m->CommandString() );

    BOOST_CHECK_EQUAL( "heat, -1 over 60m, 45m hold, +1 over 0m, 105m hold, +0 over 90m, 60m rand",
                       logMessage );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_simple_gear_settings_cool, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "SimpleThermostatRamping";
    columnValues[ settingsIndex      ] = "---I";

    LMGearReader reader( columnNames, std::vector<LMGearRow>( { columnValues } ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermostatGear>   gear( new CtiLMProgramThermostatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::SimpleThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "---I" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointSimpleMsg( *gear,
                                           300,
                                           30,
                                           12,
                                           logMessage ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint delta mode cool "
                       "min 1 max 255 tr 60 tb 60 dsb -1 tc 45 td 0 dsd 1 te 105 tf 90 dsf 0 bump stage 30",
                       m->CommandString() );

    BOOST_CHECK_EQUAL( "cool, -1 over 60m, 45m hold, +1 over 0m, 105m hold, +0 over 90m, 60m rand",
                       logMessage );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_simple_gear_settings_none_error, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "SimpleThermostatRamping";
    columnValues[ settingsIndex      ] = "----";

    LMGearReader reader( columnNames, std::vector<LMGearRow>( { columnValues } ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermostatGear>   gear( new CtiLMProgramThermostatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::SimpleThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "----" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointSimpleMsg( *gear,
                                           120,
                                           30,
                                           12,
                                           logMessage ) );

    BOOST_CHECK( ! m );     // invalid mode string results in a null message

    BOOST_CHECK( logMessage.empty() );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_simple_gear_settings_both_error, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "SimpleThermostatRamping";
    columnValues[ settingsIndex      ] = "--HI";

    LMGearReader reader( columnNames, std::vector<LMGearRow>( { columnValues } ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermostatGear>   gear( new CtiLMProgramThermostatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::SimpleThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "--HI" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointSimpleMsg( *gear,
                                           120,
                                           30,
                                           12,
                                           logMessage ) );

    BOOST_CHECK( ! m );     // invalid mode string results in a null message

    BOOST_CHECK( logMessage.empty() );
}


BOOST_AUTO_TEST_SUITE_END()

