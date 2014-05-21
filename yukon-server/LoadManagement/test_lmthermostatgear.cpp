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
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0"
        };

        columnValues = temp_columnValues;
    }
};


BOOST_AUTO_TEST_SUITE( test_lmthermostatgear )

/*
    Thermostat Ramping
        Profile values are all 0 to avoid the profile section of the command string
*/
BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_delta, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "DCH-";

    LMGearReader reader( columnNames,
                         std::vector<LMGearRow>( boost::assign::list_of( columnValues ) ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermoStatGear>   gear( new CtiLMProgramThermoStatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "DCH-" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( gear->getMode(),
                                            gear->getMinValue(),
                                            gear->getMaxValue(),
                                            gear->getPrecoolTemp(),
                                            gear->getControlTemp(),
                                            gear->getRestoreTemp(),
                                            gear->getRandom(),
                                            gear->getDelayTime(),
                                            gear->getPrecoolTime(),
                                            gear->getPrecoolHoldTime(),
                                            gear->getControlTime(),
                                            gear->getControlHoldTime(),
                                            gear->getRestoreTime(),
                                            12 ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint delta celsius mode heat ",
                       m->CommandString() );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_no_delta, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "ACH-";

    LMGearReader reader( columnNames,
                         std::vector<LMGearRow>( boost::assign::list_of( columnValues ) ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermoStatGear>   gear( new CtiLMProgramThermoStatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "ACH-" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( gear->getMode(),
                                            gear->getMinValue(),
                                            gear->getMaxValue(),
                                            gear->getPrecoolTemp(),
                                            gear->getControlTemp(),
                                            gear->getRestoreTemp(),
                                            gear->getRandom(),
                                            gear->getDelayTime(),
                                            gear->getPrecoolTime(),
                                            gear->getPrecoolHoldTime(),
                                            gear->getControlTime(),
                                            gear->getControlHoldTime(),
                                            gear->getRestoreTime(),
                                            12 ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint celsius mode heat ",
                       m->CommandString() );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_no_celsius, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "AFH-";

    LMGearReader reader( columnNames,
                         std::vector<LMGearRow>( boost::assign::list_of( columnValues ) ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermoStatGear>   gear( new CtiLMProgramThermoStatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "AFH-" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( gear->getMode(),
                                            gear->getMinValue(),
                                            gear->getMaxValue(),
                                            gear->getPrecoolTemp(),
                                            gear->getControlTemp(),
                                            gear->getRestoreTemp(),
                                            gear->getRandom(),
                                            gear->getDelayTime(),
                                            gear->getPrecoolTime(),
                                            gear->getPrecoolHoldTime(),
                                            gear->getControlTime(),
                                            gear->getControlHoldTime(),
                                            gear->getRestoreTime(),
                                            12 ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint mode heat ",
                       m->CommandString() );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_cool_mode, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "AF-I";

    LMGearReader reader( columnNames,
                         std::vector<LMGearRow>( boost::assign::list_of( columnValues ) ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermoStatGear>   gear( new CtiLMProgramThermoStatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "AF-I" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( gear->getMode(),
                                            gear->getMinValue(),
                                            gear->getMaxValue(),
                                            gear->getPrecoolTemp(),
                                            gear->getControlTemp(),
                                            gear->getRestoreTemp(),
                                            gear->getRandom(),
                                            gear->getDelayTime(),
                                            gear->getPrecoolTime(),
                                            gear->getPrecoolHoldTime(),
                                            gear->getControlTime(),
                                            gear->getControlHoldTime(),
                                            gear->getRestoreTime(),
                                            12 ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint mode cool ",
                       m->CommandString() );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_both_modes, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "AFHI";

    LMGearReader reader( columnNames,
                         std::vector<LMGearRow>( boost::assign::list_of( columnValues ) ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermoStatGear>   gear( new CtiLMProgramThermoStatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "AFHI" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( gear->getMode(),
                                            gear->getMinValue(),
                                            gear->getMaxValue(),
                                            gear->getPrecoolTemp(),
                                            gear->getControlTemp(),
                                            gear->getRestoreTemp(),
                                            gear->getRandom(),
                                            gear->getDelayTime(),
                                            gear->getPrecoolTime(),
                                            gear->getPrecoolHoldTime(),
                                            gear->getControlTime(),
                                            gear->getControlHoldTime(),
                                            gear->getRestoreTime(),
                                            12 ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint mode both ",
                       m->CommandString() );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_gear_settings_no_mode, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "ThermostatRamping";
    columnValues[ settingsIndex      ] = "AF--";

    LMGearReader reader( columnNames,
                         std::vector<LMGearRow>( boost::assign::list_of( columnValues ) ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermoStatGear>   gear( new CtiLMProgramThermoStatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::ThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "AF--" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointRequestMsg( gear->getMode(),
                                            gear->getMinValue(),
                                            gear->getMaxValue(),
                                            gear->getPrecoolTemp(),
                                            gear->getControlTemp(),
                                            gear->getRestoreTemp(),
                                            gear->getRandom(),
                                            gear->getDelayTime(),
                                            gear->getPrecoolTime(),
                                            gear->getPrecoolHoldTime(),
                                            gear->getControlTime(),
                                            gear->getControlHoldTime(),
                                            gear->getRestoreTime(),
                                            12 ) );

    BOOST_CHECK( ! m );     // invalid mode string results in a null message
}


/*
    Simple Thermostat Ramping
        Minimal profile values are required to generate a control string
*/
BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_simple_gear_settings_heat, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "SimpleThermostatRamping";
    columnValues[ settingsIndex      ] = "--H-";
    columnValues[ settingsIndex + 4  ] = "1";   // "ValueD"
    columnValues[ settingsIndex + 13 ] = "1";   // "RampRate"

    LMGearReader reader( columnNames,
                         std::vector<LMGearRow>( boost::assign::list_of( columnValues ) ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermoStatGear>   gear( new CtiLMProgramThermoStatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::SimpleThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "--H-" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointSimpleMsg( gear->getMode(),
                                           gear->getMinValue(),
                                           gear->getMaxValue(),
                                           gear->getPrecoolTemp(),
                                           gear->getRandom(),
                                           gear->getRampRate(),
                                           gear->getPrecoolTime(),
                                           gear->getPrecoolHoldTime(),
                                           gear->getControlTemp(),
                                           120,
                                           gear->getRestoreTime(),
                                           30,
                                           12 ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint delta mode heat "
                       "td 0 dsd 1 te 120 dsf -1 bump stage 30 ",
                       m->CommandString() );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_simple_gear_settings_cool, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "SimpleThermostatRamping";
    columnValues[ settingsIndex      ] = "---I";
    columnValues[ settingsIndex + 4  ] = "1";   // "ValueD"
    columnValues[ settingsIndex + 13 ] = "1";   // "RampRate"

    LMGearReader reader( columnNames,
                         std::vector<LMGearRow>( boost::assign::list_of( columnValues ) ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermoStatGear>   gear( new CtiLMProgramThermoStatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::SimpleThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "---I" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointSimpleMsg( gear->getMode(),
                                           gear->getMinValue(),
                                           gear->getMaxValue(),
                                           gear->getPrecoolTemp(),
                                           gear->getRandom(),
                                           gear->getRampRate(),
                                           gear->getPrecoolTime(),
                                           gear->getPrecoolHoldTime(),
                                           gear->getControlTemp(),
                                           120,
                                           gear->getRestoreTime(),
                                           30,
                                           12 ) );

    BOOST_CHECK_EQUAL( "control xcom setpoint delta mode cool "
                       "td 0 dsd 1 te 120 dsf -1 bump stage 30 ",
                       m->CommandString() );
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_simple_gear_settings_none_error, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "SimpleThermostatRamping";
    columnValues[ settingsIndex      ] = "----";
    columnValues[ settingsIndex + 4  ] = "1";   // "ValueD"
    columnValues[ settingsIndex + 13 ] = "1";   // "RampRate"

    LMGearReader reader( columnNames,
                         std::vector<LMGearRow>( boost::assign::list_of( columnValues ) ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermoStatGear>   gear( new CtiLMProgramThermoStatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::SimpleThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "----" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointSimpleMsg( gear->getMode(),
                                           gear->getMinValue(),
                                           gear->getMaxValue(),
                                           gear->getPrecoolTemp(),
                                           gear->getRandom(),
                                           gear->getRampRate(),
                                           gear->getPrecoolTime(),
                                           gear->getPrecoolHoldTime(),
                                           gear->getControlTemp(),
                                           120,
                                           gear->getRestoreTime(),
                                           30,
                                           12 ) );

    BOOST_CHECK( ! m );     // invalid mode string results in a null message
}


BOOST_FIXTURE_TEST_CASE( test_lmobjects_thermostat_simple_gear_settings_both_error, thermostat_gear_settings_helper )
{
    columnValues[ controlMethodIndex ] = "SimpleThermostatRamping";
    columnValues[ settingsIndex      ] = "--HI";
    columnValues[ settingsIndex + 4  ] = "1";   // "ValueD"
    columnValues[ settingsIndex + 13 ] = "1";   // "RampRate"

    LMGearReader reader( columnNames,
                         std::vector<LMGearRow>( boost::assign::list_of( columnValues ) ) );
    reader();

    boost::scoped_ptr<CtiLMProgramThermoStatGear>   gear( new CtiLMProgramThermoStatGear( reader ) );

    BOOST_CHECK_EQUAL( gear->getControlMethod(), CtiLMProgramDirectGear::SimpleThermostatRampingMethod );
    BOOST_CHECK_EQUAL( gear->getSettings(),      "--HI" );

    boost::scoped_ptr<CtiRequestMsg>
        m( group->createSetPointSimpleMsg( gear->getMode(),
                                           gear->getMinValue(),
                                           gear->getMaxValue(),
                                           gear->getPrecoolTemp(),
                                           gear->getRandom(),
                                           gear->getRampRate(),
                                           gear->getPrecoolTime(),
                                           gear->getPrecoolHoldTime(),
                                           gear->getControlTemp(),
                                           120,
                                           gear->getRestoreTime(),
                                           30,
                                           12 ) );

    BOOST_CHECK( ! m );     // invalid mode string results in a null message
}


BOOST_AUTO_TEST_SUITE_END()

