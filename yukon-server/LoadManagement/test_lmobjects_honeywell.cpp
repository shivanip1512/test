
#include <boost/test/unit_test.hpp>

#include "devicetypes.h"
#include "lmutility.h"
#include "test_reader.h"

#include "lmgrouphoneywell.h"
#include "honeywellCycleGear.h"
#include "honeywellSetpointGear.h"

extern const CtiTime gInvalidCtiTime;


BOOST_AUTO_TEST_SUITE( test_lmobjects_honeywell )

BOOST_AUTO_TEST_CASE(test_lmobjects_honeywell_group)
{
    typedef Cti::Test::StringRow<10>            LMGroupRow;
    typedef Cti::Test::TestReader<LMGroupRow>   LMGroupReader;

    LMGroupRow columnNames =
    {
        "GroupID",
        "Category",
        "PaoClass",
        "PaoName",
        "Type",
        "Description",
        "DisableFlag",
        "AlarmInhibit",
        "ControlInhibit",
        "KwCapacity"
    };

    LMGroupRow columnValues =
    {
        "1234",
        "DEVICE",
        "GROUP",
        "Test Group",
        "HONEYWELL GROUP",
        "(none)",
        "N",
        "N",
        "N",
        "3.1415"
    };

    std::vector<LMGroupRow> rowVec { columnValues };

    LMGroupReader reader(columnNames, rowVec);

    reader();

    LMGroupHoneywell group{ reader };

    // Document the initial state of the object

    BOOST_CHECK_EQUAL(group.getPAOId(), 1234);

    BOOST_CHECK_EQUAL(group.getPAOCategory(), "DEVICE");
    BOOST_CHECK_EQUAL(group.getPAOClass(), "GROUP");
    BOOST_CHECK_EQUAL(group.getPAOName(), "Test Group");
    BOOST_CHECK_EQUAL(group.getPAOType(), TYPE_LMGROUP_HONEYWELL);
    BOOST_CHECK_EQUAL(group.getPAOTypeString(), "HONEYWELL GROUP");
    BOOST_CHECK_EQUAL(group.getPAODescription(), "(none)");

    BOOST_CHECK_EQUAL(group.getDisableFlag(), FALSE);
    BOOST_CHECK_EQUAL(group.getAlarmInhibit(), FALSE);
    BOOST_CHECK_EQUAL(group.getControlInhibit(), FALSE);

    BOOST_CHECK_EQUAL(group.getIsRampingIn(), false);
    BOOST_CHECK_EQUAL(group.getIsRampingOut(), false);

    BOOST_CHECK_EQUAL(group.getCurrentHoursDaily(), 0);
    BOOST_CHECK_EQUAL(group.getCurrentHoursMonthly(), 0);
    BOOST_CHECK_EQUAL(group.getCurrentHoursSeasonal(), 0);
    BOOST_CHECK_EQUAL(group.getCurrentHoursAnnually(), 0);

    BOOST_CHECK_EQUAL(group.getHoursDailyPointId(), 0);
    BOOST_CHECK_EQUAL(group.getHoursMonthlyPointId(), 0);
    BOOST_CHECK_EQUAL(group.getHoursSeasonalPointId(), 0);
    BOOST_CHECK_EQUAL(group.getHoursAnnuallyPointId(), 0);

    BOOST_CHECK_EQUAL(group.getLastControlSent(), gInvalidCtiTime);
    BOOST_CHECK_EQUAL(group.getControlStartTime(), gInvalidCtiTime);
    BOOST_CHECK_EQUAL(group.getControlCompleteTime(), gInvalidCtiTime);
    BOOST_CHECK_EQUAL(group.getNextControlTime(), gInvalidCtiTime);
    BOOST_CHECK_EQUAL(group.getLastStopTimeSent(), gInvalidCtiTime);

    BOOST_CHECK_EQUAL(group.getGroupControlState(), CtiLMGroupBase::InactiveState);

    BOOST_CHECK_EQUAL(group.getLastControlString(), "");

    BOOST_CHECK_EQUAL(group.getDailyOps(), 0);
    BOOST_CHECK_CLOSE(group.getKWCapacity(), 3.1415, 1e-4);

    // clone to test operators
    auto clone = group.replicate();

    BOOST_CHECK(group == *clone);

    clone->setPAOId(clone->getPAOId() + 1);

    BOOST_CHECK(group != *clone);
}


BOOST_AUTO_TEST_CASE(test_lmobjects_honeywell_cycle_gear)
{
    typedef Cti::Test::StringRow<29>            LMGearRow;
    typedef Cti::Test::TestReader<LMGearRow>    LMGearReader;

    LMGearRow columnNames =
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
        "StopCommandRepeat"
    };

    LMGearRow columnValues =
    {
        "101",
        "Test Honeywell Cycle Gear",
        "2",
        "HoneywellCycle",
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
        "2"
    };

    std::vector<LMGearRow> rowVec { columnValues };

    LMGearReader reader(columnNames, rowVec);

    reader();

    HoneywellCycleGear gear{ reader };

    BOOST_CHECK_EQUAL(gear.getProgramPAOId(), 101);
    BOOST_CHECK_EQUAL(gear.getGearName(), "Test Honeywell Cycle Gear");
    BOOST_CHECK_EQUAL(gear.getGearNumber(), 2);
    BOOST_CHECK_EQUAL(gear.getControlMethod(), "HoneywellCycle");
    BOOST_CHECK_EQUAL(gear.getMethodRate(), 5);
    BOOST_CHECK_EQUAL(gear.getMethodPeriod(), 6);
    BOOST_CHECK_EQUAL(gear.getMethodRateCount(), 7);
    BOOST_CHECK_EQUAL(gear.getCycleRefreshRate(), 8);
    BOOST_CHECK_EQUAL(gear.getMethodStopType(), "Restore");
    BOOST_CHECK_EQUAL(gear.getChangeCondition(), "None");
    BOOST_CHECK_EQUAL(gear.getChangeDuration(), 9);
    BOOST_CHECK_EQUAL(gear.getChangePriority(), 10);
    BOOST_CHECK_EQUAL(gear.getChangeTriggerNumber(), 11);
    BOOST_CHECK_CLOSE(gear.getChangeTriggerOffset(), 1.2, 1e-1);
    BOOST_CHECK_EQUAL(gear.getPercentReduction(), 10);
    BOOST_CHECK_EQUAL(gear.getGroupSelectionMethod(), "LastControlled");
    BOOST_CHECK_EQUAL(gear.getMethodOptionType(), "FixedCount");
    BOOST_CHECK_EQUAL(gear.getMethodOptionMax(), 13);
    BOOST_CHECK_EQUAL(gear.getUniqueID(), 14);
    BOOST_CHECK_EQUAL(gear.getRampInInterval(), 15);
    BOOST_CHECK_EQUAL(gear.getRampInPercent(), 16);
    BOOST_CHECK_EQUAL(gear.getRampOutInterval(), 17);
    BOOST_CHECK_EQUAL(gear.getRampOutPercent(), 18);
    BOOST_CHECK_EQUAL(gear.getFrontRampOption(), "(none)");
    BOOST_CHECK_EQUAL(gear.getBackRampOption(), "(none)");
    BOOST_CHECK_CLOSE(gear.getKWReduction(), 3.4, 1e-1);
    BOOST_CHECK_EQUAL(gear.getStopRepeatCount(), 2);

    // No public access to 'FrontRampTime' or 'BackRampTime' from the reader in the gear object

    // clone to test operators
    auto clone = gear.replicate();

    BOOST_CHECK(gear == *clone);

    clone->setProgramPAOId(clone->getProgramPAOId() + 1);

    BOOST_CHECK(gear != *clone);
}

BOOST_AUTO_TEST_CASE(test_lmobjects_honeywell_setpoint_gear)
{
    typedef Cti::Test::StringRow<43>            LMGearRow;
    typedef Cti::Test::TestReader<LMGearRow>    LMGearReader;

    LMGearRow columnNames =
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
        "StopCommandRepeat",
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

    std::vector<LMGearRow> columnValues
    {
        {
            "101",
            "Test Honeywell Setpoint Gear",
            "2",
            "HoneywellSetpoint",
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
            "Mandatory",    // <---- mandatory: yes
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
            "2",
            "---I",         // <---- heat/cool mode: cool
            "0",
            "4",            // <---- heat/cool setpoint: +4 degrees
            "6",            // <---- pre-heat/pre-cool setpoint: +6 degrees
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
        }
    };

    LMGearReader reader( columnNames, columnValues );

    reader();

    HoneywellSetpointGear gear{ reader };

    // Just testing the differences in the base gear type, plus the thermostat stuff

    BOOST_CHECK_EQUAL(gear.getProgramPAOId(), 101);
    BOOST_CHECK_EQUAL(gear.getGearName(), "Test Honeywell Setpoint Gear");
    BOOST_CHECK_EQUAL(gear.getControlMethod(), "HoneywellSetpoint");

    BOOST_CHECK_EQUAL( gear.getSettings(), "---I" );
    BOOST_CHECK( ! gear.isHeatControl() );

    BOOST_CHECK( gear.isMandatory() );

    BOOST_CHECK_EQUAL( gear.getSetpointOffset(), 4 );
}

BOOST_AUTO_TEST_SUITE_END()

