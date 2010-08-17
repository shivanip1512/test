#include "yukon.h"

#include "ConstraintViolation.h"
#include "ctidate.h"

#define BOOST_AUTO_TEST_MAIN "Test LM Constraint Violations"
#include <boost/test/unit_test.hpp>

class VectorHolder
{
public:

    std::vector<double>      _doubles;
    std::vector<int>         _ints;
    std::vector<std::string> _strings;
    std::vector<CtiTime>     _times;

    void clearVectors()
    {
        _doubles.clear();
        _ints.clear();
        _strings.clear();
        _times.clear();
    }

    void pushBackDouble(double value)
    {
        _doubles.push_back(value);
    }

    void pushBackInt(int value)
    {
        _ints.push_back(value);
    }

    void pushBackString(std::string str)
    {
        _strings.push_back(str);
    }

    void pushBackTime(CtiTime time)
    {
        _times.push_back(time);
    }

    bool equals(std::vector<double> doubles)
    {
        if (_doubles.size() != doubles.size())
        {
            return false;
        }
        return ( equal(_doubles.begin(), _doubles.end(), doubles.begin()) );
    }

    bool equals(std::vector<int> ints)
    {
        if (_ints.size() != ints.size())
        {
            return false;
        }
        return ( equal(_ints.begin(), _ints.end(), ints.begin()) );
    }

    bool equals(std::vector<std::string> strings)
    {
        if (_strings.size() != strings.size())
        {
            return false;
        }
        return ( equal(_strings.begin(), _strings.end(), strings.begin()) );
    }

    bool equals(std::vector<CtiTime> times)
    {
        if (_times.size() != times.size())
        {
            return false;
        }
        return ( equal(_times.begin(), _times.end(), times.begin()) );
    }
} _vectors;

class testConstraintViolation : public ConstraintViolation
{
public:
    testConstraintViolation(CV_Type_Double error, double value) :
        ConstraintViolation(error, value)
    { }

    testConstraintViolation(CV_Type_TimeInt error, const CtiTime &time, int value) :
        ConstraintViolation(error, time, value)
    { }

    testConstraintViolation(CV_Type_StringDouble error, const std::string &str, double value) :
        ConstraintViolation(error, str, value)
    { }

    testConstraintViolation(CV_Type_Time error, const CtiTime &time) :
        ConstraintViolation(error, time)
    { }

    testConstraintViolation(CV_Type_StringInt error, const std::string &str, int value) :
        ConstraintViolation(error, str, value)
    { }

    testConstraintViolation(CV_Type_NoParameters error) :
        ConstraintViolation(error)
    { }

    testConstraintViolation(CV_Type_ThreeTimes error, const CtiTime &time1, const CtiTime &time2, const CtiTime &time3) :
        ConstraintViolation(error, time1, time2, time3)
    { }

    testConstraintViolation(CV_Type_TwoTimes error, const CtiTime &time1, const CtiTime &time2) :
        ConstraintViolation(error, time1, time2)
    { }

    testConstraintViolation(CV_Type_TimeDoubleDouble error, const CtiTime &time, double value1, double value2) :
        ConstraintViolation(error, time, value1, value2)
    { }

    testConstraintViolation(CV_Type_String error, const std::string &str) :
        ConstraintViolation(error, str)
    { }

    testConstraintViolation(CV_Type_TimeTimeInt error, const CtiTime &time1, const CtiTime &time2, int value) :
        ConstraintViolation(error, time1, time2, value)
    { }

    std::vector<double> getDoubleParams()
    {
        return ConstraintViolation::getDoubleParams();
    }

    std::vector<int> getIntegerParams()
    {
        return ConstraintViolation::getIntegerParams();
    }

    std::vector<std::string> getStringParams()
    {
        return ConstraintViolation::getStringParams();
    }

    std::vector<CtiTime> getDateTimeParams()
    {
        return ConstraintViolation::getDateTimeParams();
    }
};


BOOST_AUTO_TEST_CASE(test_cv_type_d)
{
    {
        double testValueDouble = 4.0;

        _vectors.clearVectors();

        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_D_ControlledLessThanMinimum, 
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        double testValueDouble = 8.0;

        _vectors.clearVectors();

        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_D_ControlledLessThanMinimum, 
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_ti)
{
    {
        CtiDate today(12, 8, 2010);
        CtiTime testValueTime = CtiTime(today);
        int     testValueInt  = 4;

        _vectors.clearVectors();

        _vectors.pushBackInt(testValueInt);
        _vectors.pushBackTime(testValueTime);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TI_OutsideSeasonSchedule, 
                                                                        testValueTime,
                                                                        testValueInt);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_t)
{
    {
        CtiDate today(12, 8, 2010);
        CtiTime testValueTime = CtiTime(today);

        _vectors.clearVectors();

        _vectors.pushBackTime(testValueTime);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_T_ProhibitedHolidayRun, 
                                                                        testValueTime);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_sd)
{
    {
        std::string testValueString = "Everybody Loves Hugo";
        double testValueDouble = 4.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededDailyControlHours, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "That's why the Red Sox will never win the World Series.";
        double testValueDouble = 8.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededDailyControlHoursMsg2, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "Whatever happened, happened.";
        double testValueDouble = 15.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededDailyControlHoursMsg3, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "My name is Henry Gale and I'm from Minnesota";
        double testValueDouble = 16.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededMonthlyControlHours, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "Have you ever heard of Driveshaft? The band?";
        double testValueDouble = 23.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededMonthlyControlHoursMsg2, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "See you in another life, brother.";
        double testValueDouble = 42.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededMonthlyControlHoursMsg3, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "Don't tell me what I can't do!";
        double testValueDouble = 63.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededSeasonalControlHours, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "One of these days I'm gonna find a loophole, my friend.";
        double testValueDouble = 72.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededSeasonalControlHoursMsg2, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "It's good to see you out of those chains." ;
        double testValueDouble = 81.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededSeasonalControlHoursMsg3, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "Hey Freckles.";
        double testValueDouble = 87.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededAnnualControlHours, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "I don't know how long the battery will last.";
        double testValueDouble = 93.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededAnnualControlHoursMsg2, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "I always have a plan!";
        double testValueDouble = 101.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededAnnualControlHoursMsg3, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        std::string testValueString = "Do I know you?";
        double testValueDouble = 115.0;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackDouble(testValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_MinRestartTimeViolation, 
                                                                        testValueString,
                                                                        testValueDouble);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_si)
{
    {
        std::string testValueString = "If we can't live together, then we're gonna die alone.";
        int testValueInt = 19;

        _vectors.clearVectors();

        _vectors.pushBackString(testValueString);
        _vectors.pushBackInt(testValueInt);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SI_MaximumDailyOperationsReached, 
                                                                        testValueString,
                                                                        testValueInt);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_np)
{
    {
        _vectors.clearVectors();

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_ProposedTimesSpanMultipleWindows);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_CannotRunOutsideControlWindows);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_IdenticalGearID);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_CannotChangeFromLatching);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_GearCannotChangeStopping);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_GearCannotChangeInactive);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_ttt)
{
    {
        _vectors.clearVectors();

        CtiDate today(12, 8, 2010);
        CtiTime morning(today, 8, 0, 0);
        CtiTime afternoon(today, 12, 0, 0);
        CtiTime evening(today, 18, 0, 0);

        _vectors.pushBackTime(morning);
        _vectors.pushBackTime(afternoon);
        _vectors.pushBackTime(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_StopTimeOutsideControlWindow,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        CtiDate today(12, 9, 2010);
        CtiTime morning(today, 8, 30, 0);
        CtiTime afternoon(today, 12, 30, 0);
        CtiTime evening(today, 18, 30, 0);

        _vectors.pushBackTime(morning);
        _vectors.pushBackTime(afternoon);
        _vectors.pushBackTime(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_StartTimeOutsideControlWindow,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        CtiDate today(12, 10, 2010);
        CtiTime morning(today, 8, 15, 0);
        CtiTime afternoon(today, 12, 15, 0);
        CtiTime evening(today, 18, 15, 0);

        _vectors.pushBackTime(morning);
        _vectors.pushBackTime(afternoon);
        _vectors.pushBackTime(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStartTimeSameDate,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        CtiDate today(12, 11, 2010);
        CtiTime morning(today, 8, 45, 0);
        CtiTime afternoon(today, 12, 45, 0);
        CtiTime evening(today, 18, 45, 0);

        _vectors.pushBackTime(morning);
        _vectors.pushBackTime(afternoon);
        _vectors.pushBackTime(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStopTimeSameDate,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        CtiDate today(12, 12, 2010);
        CtiTime morning(today, 9, 0, 0);
        CtiTime afternoon(today, 13, 0, 0);
        CtiTime evening(today, 19, 0, 0);

        _vectors.pushBackTime(morning);
        _vectors.pushBackTime(afternoon);
        _vectors.pushBackTime(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStartTimeOverMidnight,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        CtiDate today(12, 1, 2011);
        CtiTime morning(today, 7, 0, 0);
        CtiTime afternoon(today, 13, 0, 0);
        CtiTime evening(today, 18, 30, 0);

        _vectors.pushBackTime(morning);
        _vectors.pushBackTime(afternoon);
        _vectors.pushBackTime(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStopTimeOverMidnight,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_tt)
{
    {
        _vectors.clearVectors();

        CtiDate today(12, 8, 2010);
        CtiTime now(today, 7, 0, 0);
        CtiTime then(today, 13, 0, 0);

        _vectors.pushBackTime(now);
        _vectors.pushBackTime(then);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TT_ProposedStartAfterStop,
                                                                        now,
                                                                        then);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        CtiDate today(12, 9, 2010);
        CtiTime now(today, 8, 0, 0);
        CtiTime then(today, 12, 0, 0);

        _vectors.pushBackTime(now);
        _vectors.pushBackTime(then);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TT_ProposedStopBeforeStart,
                                                                        now,
                                                                        then);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();

        CtiDate today(12, 10, 2010);
        CtiTime now(today, 7, 30, 0);
        CtiTime then(today, 13, 30, 0);

        _vectors.pushBackTime(now);
        _vectors.pushBackTime(then);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TT_CannotExtendStopTime,
                                                                        now,
                                                                        then);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_tdd)
{
    {
        _vectors.clearVectors();

        CtiDate today(12, 8, 2010);
        CtiTime now(today, 7, 30, 0);
        double testValueDouble1 = 4.0;
        double testValueDouble2 = 8.0;

        _vectors.pushBackTime(now);
        _vectors.pushBackDouble(testValueDouble1);
        _vectors.pushBackDouble(testValueDouble2);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TDD_ProposedStartTooSoon,
                                                                        now,
                                                                        testValueDouble1,
                                                                        testValueDouble2);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_s)
{
    {
        _vectors.clearVectors();
        
        std::string testValueString = "Another mission, The powers have called me away";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_ProhibitedWeekdayRun,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "Another time to carry my colors again";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_MasterProgramActive,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "My motication: an oath I've sworn to defend";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupDisabled,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "To win the honor of coming back home again";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxActivateViolation,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "No explanation will matter after we begin";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMinActivateViolation,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = " Unlock the dark destroyer that's buried within";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMinRestartViolation,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "My true vocation, and now my unfortunate friend";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxDailyOpsViolation,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "You will discover a war you're unable to win";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxDailyHoursViolation,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "I'll have you know that I've become indestructable";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxMonthlyHoursViolation,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "Determination that is incorruptable";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxSeasonalHoursViolation,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "From the other side a terror to behold";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxAnnualHoursViolation,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "Annihilation will be unavoidable";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupCannotControlInWindow,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "Every broken enemy will know that their opponent had to be invincible";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupNotInProgramControlWindow,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        std::string testValueString = "Take a last look around while you're alive: I'm an indestructible master of war!";

        _vectors.pushBackString(testValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupNotEnoughTimeLeftInWindow,
                                                                        testValueString);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_tti)
{
    {
        _vectors.clearVectors();
        
        CtiDate today(12, 8, 2010);
        CtiTime morning(today, 8, 0, 0);
        CtiTime evening(today, 17, 0, 0);
        int testValueInt = 8;

        _vectors.pushBackTime(morning);
        _vectors.pushBackTime(evening);
        _vectors.pushBackInt(testValueInt);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTI_LoadGroupCannotControlInWindowAdjust,
                                                                        morning,
                                                                        evening,
                                                                        testValueInt);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }

    {
        _vectors.clearVectors();
        
        CtiDate today(12, 9, 2010);
        CtiTime morning(today, 8, 30, 0);
        CtiTime evening(today, 17, 30, 0);
        int testValueInt = 53;

        _vectors.pushBackTime(morning);
        _vectors.pushBackTime(evening);
        _vectors.pushBackInt(testValueInt);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTI_LoadGroupCannotControlInWindowNoAdjust,
                                                                        morning,
                                                                        evening,
                                                                        testValueInt);

        BOOST_CHECK(_vectors.equals(testViolation.getDoubleParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getIntegerParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getStringParams()));
        BOOST_CHECK(_vectors.equals(testViolation.getDateTimeParams()));
    }
}

