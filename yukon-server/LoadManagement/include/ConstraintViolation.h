#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>

#include "ctitime.h"

class ConstraintViolation : public RWCollectable
{
RWDECLARE_COLLECTABLE( ConstraintViolation )

private:

    int _errorCode;
    std::vector<double>      _doubleParams;
    std::vector<int>         _integerParams;
    std::vector<std::string> _stringParams;
    std::vector<CtiTime>     _datetimeParams;

public:

    enum CV_Enum_Double
    {
        CV_ControlledLessThanMinimum = 107,
        CV_ControlledMoreThanMaximum = 108
    };

    enum CV_Enum_TimeInt
    {
        CV_OutsideSeasonSchedule = 100
    };

    enum CV_Enum_Time
    {
        CV_ProhibitedHolidayRun = 101
    };

    enum CV_Enum_StringDouble
    {
        CV_ExceededDailyControlHours        = 103,
        CV_ExceededDailyControlHoursMsg2    = 128,
        CV_ExceededDailyControlHoursMsg3    = 129,
        CV_ExceededMonthlyControlHours      = 104,
        CV_ExceededMonthlyControlHoursMsg2  = 130,
        CV_ExceededMonthlyControlHoursMsg3  = 131,
        CV_ExceededSeasonalControlHours     = 105,
        CV_ExceededSeasonalControlHoursMsg2 = 132,
        CV_ExceededSeasonalControlHoursMsg3 = 133,
        CV_ExceededAnnualControlHours       = 106,
        CV_ExceededAnnualControlHoursMsg2   = 134,
        CV_ExceededAnnualControlHoursMsg3   = 135,
        CV_MinRestartTimeViolation          = 109
    };

    enum CV_Enum_StringInt
    {
        CV_MaximumDailyOperationsReached = 110
    };

    enum CV_Enum_Empty
    {
        CV_ProposedTimesSpanMultipleWindows = 111,
        CV_CannotRunOutsideControlWindows   = 112,
        CV_IdenticalGearID                  = 124,
        CV_CannotChangeFromLatching         = 125,
        CV_GearCannotChangeStopping         = 126,
        CV_GearCannotChangeInactive         = 127
    };

    enum CV_Enum_ThreeTimes
    {
        CV_StopTimeOutsideControlWindow           = 113,
        CV_StartTimeOutsideControlWindow          = 114,
        CV_InvalidProposedCAStartTimeSameDate     = 117,
        CV_InvalidProposedCAStopTimeSameDate      = 118,
        CV_InvalidProposedCAStartTimeOverMidnight = 119,
        CV_InvalidProposedCAStopTimeOverMidnight  = 120
    };

    enum CV_Enum_TwoTimes
    {
        CV_ProposedStartAfterStop  = 115,
        CV_ProposedStopBeforeStart = 116,
        CV_CannotExtendStopTime    = 123
    };

    enum CV_Enum_TimeDoubleDouble
    {
        CV_ProposedStartTooSoon = 121
    };

    enum CV_Enum_String
    {
        CV_ProhibitedWeekdayRun = 102,
        CV_MasterProgramActive  = 122,
        CV_LoadGroupDisabled    = 136,
        CV_LoadGroupMaxActivateViolation = 137,
        CV_LoadGroupMinActivateViolation = 138,
        CV_LoadGroupMinRestartViolation  = 139,
        CV_LoadGroupMaxDailyOpsViolation = 140,
        CV_LoadGroupMaxDailyHoursViolation = 141,
        CV_LoadGroupMaxMonthlyHoursViolation = 142, 
        CV_LoadGroupMaxSeasonalHoursViolation = 143,
        CV_LoadGroupMaxAnnualHoursViolation = 144,
        CV_LoadGroupCannotControlInWindow = 145,
        CV_LoadGroupNotInProgramControlWindow = 148,
        CV_LoadGroupNotEnoughTimeLeftInWindow = 149
    };

    enum CV_Enum_TimeTimeInt
    {
        CV_LoadGroupCannotControlInWindowAdjust = 146,  // These two messages are couple with CV_LoadGroupCannotControlInWindow
        CV_LoadGroupCannotControlInWindowNoAdjust = 147 // from the CV_Enum_String enum group.
    };

    ConstraintViolation() { };

    ConstraintViolation(CV_Enum_Double error, double value);
    ConstraintViolation(CV_Enum_TimeInt error, const CtiTime &time, int value);
    ConstraintViolation(CV_Enum_StringDouble error, const std::string &str, double value);
    ConstraintViolation(CV_Enum_Time error, const CtiTime &time);
    ConstraintViolation(CV_Enum_StringInt error, const std::string &str, int value);
    ConstraintViolation(CV_Enum_Empty error);
    ConstraintViolation(CV_Enum_ThreeTimes error, const CtiTime &time1, const CtiTime &time2, const CtiTime &time3);
    ConstraintViolation(CV_Enum_TwoTimes error, const CtiTime &time1, const CtiTime &time2);
    ConstraintViolation(CV_Enum_TimeDoubleDouble error, const CtiTime &time, double value1, double value2);
    ConstraintViolation(CV_Enum_String error, const std::string &str);
    ConstraintViolation(CV_Enum_TimeTimeInt error, const CtiTime &time1, const CtiTime &time2, int value);

    int getErrorCode() const;

    std::vector<double>      getDoubleParams()   const;
    std::vector<int>         getIntegerParams()  const;
    std::vector<std::string> getStringParams()   const;
    std::vector<CtiTime>     getDateTimeParams() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

};
