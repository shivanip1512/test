#pragma once

#include "ctitime.h"

#include "collectable.h"

class ConstraintViolation
{
public:
    DECLARE_COLLECTABLE( ConstraintViolation );

private:

    int _errorCode;
    std::vector<double>      _doubleParams;
    std::vector<int>         _integerParams;
    std::vector<std::string> _stringParams;
    std::vector<CtiTime>     _datetimeParams;

    ConstraintViolation() { };

public:

    ConstraintViolation( int errorCode,
                         const std::vector<double>& doubleParams,
                         const std::vector<int>& integerParams,
                         const std::vector<std::string>& stringParams,
                         const std::vector<CtiTime>& datetimeParams ) :
    _errorCode( errorCode ),
    _doubleParams( doubleParams ),
    _integerParams( integerParams ),
    _datetimeParams( datetimeParams )
    {};

    enum CV_Type_Double
    {
        CV_D_ControlledLessThanMinimum = 107,
        CV_D_ControlledMoreThanMaximum = 108
    };

    enum CV_Type_TimeInt
    {
        CV_TI_OutsideSeasonSchedule = 100
    };

    enum CV_Type_Time
    {
        CV_T_ProhibitedHolidayRun = 101
    };

    enum CV_Type_StringDouble
    {
        CV_SD_ExceededDailyControlHours        = 103,
        CV_SD_ExceededDailyControlHoursMsg2    = 128,
        CV_SD_ExceededDailyControlHoursMsg3    = 129,
        CV_SD_ExceededMonthlyControlHours      = 104,
        CV_SD_ExceededMonthlyControlHoursMsg2  = 130,
        CV_SD_ExceededMonthlyControlHoursMsg3  = 131,
        CV_SD_ExceededSeasonalControlHours     = 105,
        CV_SD_ExceededSeasonalControlHoursMsg2 = 132,
        CV_SD_ExceededSeasonalControlHoursMsg3 = 133,
        CV_SD_ExceededAnnualControlHours       = 106,
        CV_SD_ExceededAnnualControlHoursMsg2   = 134,
        CV_SD_ExceededAnnualControlHoursMsg3   = 135,
        CV_SD_MinRestartTimeViolation          = 109
    };

    enum CV_Type_StringInt
    {
        CV_SI_MaximumDailyOperationsReached = 110
    };

    enum CV_Type_NoParameters
    {
        CV_NP_ProposedTimesSpanMultipleWindows = 111,
        CV_NP_CannotRunOutsideControlWindows   = 112,
        CV_NP_IdenticalGearID                  = 124,
        CV_NP_CannotChangeFromLatching         = 125,
        CV_NP_GearCannotChangeStopping         = 126,
        CV_NP_GearCannotChangeInactive         = 127,
        CV_NP_ControlWindowSpansMidnight       = 150
    };

    enum CV_Type_ThreeTimes
    {
        CV_TTT_StopTimeOutsideControlWindow           = 113,
        CV_TTT_StartTimeOutsideControlWindow          = 114,
        CV_TTT_InvalidProposedCAStartTimeSameDate     = 117,
        CV_TTT_InvalidProposedCAStopTimeSameDate      = 118,
        CV_TTT_InvalidProposedCAStartTimeOverMidnight = 119,
        CV_TTT_InvalidProposedCAStopTimeOverMidnight  = 120
    };

    enum CV_Type_TwoTimes
    {
        CV_TT_ProposedStartAfterStop  = 115,
        CV_TT_ProposedStopBeforeStart = 116,
        CV_TT_CannotExtendStopTime    = 123
    };

    enum CV_Type_TimeDoubleDouble
    {
        CV_TDD_ProposedStartTooSoon = 121
    };

    enum CV_Type_String
    {
        CV_S_ProhibitedWeekdayRun = 102,
        CV_S_MasterProgramActive  = 122,
        CV_S_LoadGroupDisabled    = 136,
        CV_S_LoadGroupMaxActivateViolation = 137,
        CV_S_LoadGroupMinActivateViolation = 138,
        CV_S_LoadGroupMinRestartViolation  = 139,
        CV_S_LoadGroupMaxDailyOpsViolation = 140,
        CV_S_LoadGroupMaxDailyHoursViolation = 141,
        CV_S_LoadGroupMaxMonthlyHoursViolation = 142,
        CV_S_LoadGroupMaxSeasonalHoursViolation = 143,
        CV_S_LoadGroupMaxAnnualHoursViolation = 144,
        CV_S_LoadGroupCannotControlInWindow = 145,
        CV_S_LoadGroupNotInProgramControlWindow = 148,
        CV_S_LoadGroupNotEnoughTimeLeftInWindow = 149
    };

    enum CV_Type_TimeTimeInt
    {
        CV_TTI_LoadGroupCannotControlInWindowAdjust = 146,  // These two messages are couple with CV_LoadGroupCannotControlInWindow
        CV_TTI_LoadGroupCannotControlInWindowNoAdjust = 147 // from the CV_Enum_String enum group.
    };

    ConstraintViolation(CV_Type_Double error, double value);
    ConstraintViolation(CV_Type_TimeInt error, const CtiTime &time, int value);
    ConstraintViolation(CV_Type_StringDouble error, const std::string &str, double value);
    ConstraintViolation(CV_Type_Time error, const CtiTime &time);
    ConstraintViolation(CV_Type_StringInt error, const std::string &str, int value);
    ConstraintViolation(CV_Type_NoParameters error);
    ConstraintViolation(CV_Type_ThreeTimes error, const CtiTime &time1, const CtiTime &time2, const CtiTime &time3);
    ConstraintViolation(CV_Type_TwoTimes error, const CtiTime &time1, const CtiTime &time2);
    ConstraintViolation(CV_Type_TimeDoubleDouble error, const CtiTime &time, double value1, double value2);
    ConstraintViolation(CV_Type_String error, const std::string &str);
    ConstraintViolation(CV_Type_TimeTimeInt error, const CtiTime &time1, const CtiTime &time2, int value);

    int getErrorCode() const;

    bool operator==(const ConstraintViolation &rhs) const;

    const std::vector<double>&      getDoubleParams()   const;
    const std::vector<int>&         getIntegerParams()  const;
    const std::vector<std::string>& getStringParams()   const;
    const std::vector<CtiTime>&     getDateTimeParams() const;

    std::size_t getMemoryConsumption() const;
};
