#include "precompiled.h"

#include "ConstraintViolation.h"
#include "ctidate.h"

#define BOOST_AUTO_TEST_MAIN "Test LM Constraint Violations"
#include <boost/test/unit_test.hpp>

struct VectorHolder
{
    std::vector<double>      doubles;
    std::vector<int>         ints;
    std::vector<std::string> strings;
    std::vector<CtiTime>     times;
};

template <class T> bool fancyFeastEqual(const std::vector<T> &lhs, const std::vector<T> &rhs)
{
    if (lhs.size() != rhs.size())
    {
        return false;
    }
    return ( equal(lhs.begin(), lhs.end(), rhs.begin()) );
}

struct testConstraintViolation : ConstraintViolation
{
    using ConstraintViolation::getDoubleParams;
    using ConstraintViolation::getIntegerParams;
    using ConstraintViolation::getStringParams;
    using ConstraintViolation::getDateTimeParams;

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
};


BOOST_AUTO_TEST_CASE(test_cv_type_d)
{
    {
        double expectedValueDouble = 4.0;

        VectorHolder vectors;

        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_D_ControlledLessThanMinimum,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        double expectedValueDouble = 8.0;

        VectorHolder vectors;

        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_D_ControlledLessThanMinimum,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_ti)
{
    {
        CtiDate today(12, 8, 2010);
        CtiTime expectedValueTime = CtiTime(today);
        int     expectedValueInt  = 4;

        VectorHolder vectors;

        vectors.ints.push_back(expectedValueInt);
        vectors.times.push_back(expectedValueTime);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TI_OutsideSeasonSchedule,
                                                                        expectedValueTime,
                                                                        expectedValueInt);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.ints, testViolation.getIntegerParams()));
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_t)
{
    {
        CtiDate today(12, 8, 2010);
        CtiTime expectedValueTime = CtiTime(today);

        VectorHolder vectors;

        vectors.times.push_back(expectedValueTime);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_T_ProhibitedHolidayRun,
                                                                        expectedValueTime);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_sd)
{
    {
        std::string expectedValueString = "Everybody Loves Hugo";
        double expectedValueDouble = 4.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededDailyControlHours,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "That's why the Red Sox will never win the World Series.";
        double expectedValueDouble = 8.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededDailyControlHoursMsg2,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "Whatever happened, happened.";
        double expectedValueDouble = 15.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededDailyControlHoursMsg3,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "My name is Henry Gale and I'm from Minnesota";
        double expectedValueDouble = 16.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededMonthlyControlHours,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "Have you ever heard of Driveshaft? The band?";
        double expectedValueDouble = 23.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededMonthlyControlHoursMsg2,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "See you in another life, brother.";
        double expectedValueDouble = 42.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededMonthlyControlHoursMsg3,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "Don't tell me what I can't do!";
        double expectedValueDouble = 63.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededSeasonalControlHours,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "One of these days I'm gonna find a loophole, my friend.";
        double expectedValueDouble = 72.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededSeasonalControlHoursMsg2,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "It's good to see you out of those chains." ;
        double expectedValueDouble = 81.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededSeasonalControlHoursMsg3,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "Hey Freckles.";
        double expectedValueDouble = 87.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededAnnualControlHours,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "I don't know how long the battery will last.";
        double expectedValueDouble = 93.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededAnnualControlHoursMsg2,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "I always have a plan!";
        double expectedValueDouble = 101.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_ExceededAnnualControlHoursMsg3,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        std::string expectedValueString = "Do I know you?";
        double expectedValueDouble = 115.0;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.doubles.push_back(expectedValueDouble);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SD_MinRestartTimeViolation,
                                                                        expectedValueString,
                                                                        expectedValueDouble);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_si)
{
    {
        std::string expectedValueString = "If we can't live together, then we're gonna die alone.";
        int expectedValueInt = 19;

        VectorHolder vectors;

        vectors.strings.push_back(expectedValueString);
        vectors.ints.push_back(expectedValueInt);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_SI_MaximumDailyOperationsReached,
                                                                        expectedValueString,
                                                                        expectedValueInt);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.ints,    testViolation.getIntegerParams()));
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_np)
{
    {
        VectorHolder vectors;

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_ProposedTimesSpanMultipleWindows);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_CannotRunOutsideControlWindows);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_IdenticalGearID);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_CannotChangeFromLatching);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_GearCannotChangeStopping);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_NP_GearCannotChangeInactive);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_ttt)
{
    {
        VectorHolder vectors;

        CtiDate today(12, 8, 2010);
        CtiTime morning(today, 8, 0, 0);
        CtiTime afternoon(today, 12, 0, 0);
        CtiTime evening(today, 18, 0, 0);

        vectors.times.push_back(morning);
        vectors.times.push_back(afternoon);
        vectors.times.push_back(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_StopTimeOutsideControlWindow,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }

    {
        VectorHolder vectors;

        CtiDate today(12, 9, 2010);
        CtiTime morning(today, 8, 30, 0);
        CtiTime afternoon(today, 12, 30, 0);
        CtiTime evening(today, 18, 30, 0);

        vectors.times.push_back(morning);
        vectors.times.push_back(afternoon);
        vectors.times.push_back(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_StartTimeOutsideControlWindow,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }

    {
        VectorHolder vectors;

        CtiDate today(12, 10, 2010);
        CtiTime morning(today, 8, 15, 0);
        CtiTime afternoon(today, 12, 15, 0);
        CtiTime evening(today, 18, 15, 0);

        vectors.times.push_back(morning);
        vectors.times.push_back(afternoon);
        vectors.times.push_back(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStartTimeSameDate,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }

    {
        VectorHolder vectors;

        CtiDate today(12, 11, 2010);
        CtiTime morning(today, 8, 45, 0);
        CtiTime afternoon(today, 12, 45, 0);
        CtiTime evening(today, 18, 45, 0);

        vectors.times.push_back(morning);
        vectors.times.push_back(afternoon);
        vectors.times.push_back(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStopTimeSameDate,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }

    {
        VectorHolder vectors;

        CtiDate today(12, 12, 2010);
        CtiTime morning(today, 9, 0, 0);
        CtiTime afternoon(today, 13, 0, 0);
        CtiTime evening(today, 19, 0, 0);

        vectors.times.push_back(morning);
        vectors.times.push_back(afternoon);
        vectors.times.push_back(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStartTimeOverMidnight,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }

    {
        VectorHolder vectors;

        CtiDate today(12, 1, 2011);
        CtiTime morning(today, 7, 0, 0);
        CtiTime afternoon(today, 13, 0, 0);
        CtiTime evening(today, 18, 30, 0);

        vectors.times.push_back(morning);
        vectors.times.push_back(afternoon);
        vectors.times.push_back(evening);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStopTimeOverMidnight,
                                                                        morning,
                                                                        afternoon,
                                                                        evening);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_tt)
{
    {
        VectorHolder vectors;

        CtiDate today(12, 8, 2010);
        CtiTime now(today, 7, 0, 0);
        CtiTime then(today, 13, 0, 0);

        vectors.times.push_back(now);
        vectors.times.push_back(then);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TT_ProposedStartAfterStop,
                                                                        now,
                                                                        then);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }

    {
        VectorHolder vectors;

        CtiDate today(12, 9, 2010);
        CtiTime now(today, 8, 0, 0);
        CtiTime then(today, 12, 0, 0);

        vectors.times.push_back(now);
        vectors.times.push_back(then);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TT_ProposedStopBeforeStart,
                                                                        now,
                                                                        then);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }

    {
        VectorHolder vectors;

        CtiDate today(12, 10, 2010);
        CtiTime now(today, 7, 30, 0);
        CtiTime then(today, 13, 30, 0);

        vectors.times.push_back(now);
        vectors.times.push_back(then);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TT_CannotExtendStopTime,
                                                                        now,
                                                                        then);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_tdd)
{
    {
        VectorHolder vectors;

        CtiDate today(12, 8, 2010);
        CtiTime now(today, 7, 30, 0);
        double expectedValueDouble1 = 4.0;
        double expectedValueDouble2 = 8.0;

        vectors.times.push_back(now);
        vectors.doubles.push_back(expectedValueDouble1);
        vectors.doubles.push_back(expectedValueDouble2);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TDD_ProposedStartTooSoon,
                                                                        now,
                                                                        expectedValueDouble1,
                                                                        expectedValueDouble2);

        BOOST_CHECK(fancyFeastEqual(vectors.doubles, testViolation.getDoubleParams()));
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_s)
{
    {
        VectorHolder vectors;

        std::string expectedValueString = "Another mission, The powers have called me away";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_ProhibitedWeekdayRun,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "Another time to carry my colors again";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_MasterProgramActive,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "My motivation: an oath I've sworn to defend";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupDisabled,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "To win the honor of coming back home again";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxActivateViolation,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "No explanation will matter after we begin";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMinActivateViolation,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "Unlock the dark destroyer that's buried within";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMinRestartViolation,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "My true vocation, and now my unfortunate friend";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxDailyOpsViolation,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "You will discover a war you're unable to win";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxDailyHoursViolation,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "I'll have you know that I've become indestructable";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxMonthlyHoursViolation,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "Determination that is incorruptable";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxSeasonalHoursViolation,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "From the other side a terror to behold";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxAnnualHoursViolation,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "Annihilation will be unavoidable";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupCannotControlInWindow,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "Every broken enemy will know that their opponent had to be invincible";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupNotInProgramControlWindow,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }

    {
        VectorHolder vectors;

        std::string expectedValueString = "Take a last look around while you're alive: I'm an indestructible master of war!";

        vectors.strings.push_back(expectedValueString);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_S_LoadGroupNotEnoughTimeLeftInWindow,
                                                                        expectedValueString);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(testViolation.getIntegerParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.strings, testViolation.getStringParams()));
        BOOST_CHECK(testViolation.getDateTimeParams().empty());
    }
}

BOOST_AUTO_TEST_CASE(test_cv_type_tti)
{
    {
        VectorHolder vectors;

        CtiDate today(12, 8, 2010);
        CtiTime morning(today, 8, 0, 0);
        CtiTime evening(today, 17, 0, 0);
        int expectedValueInt = 8;

        vectors.times.push_back(morning);
        vectors.times.push_back(evening);
        vectors.ints.push_back(expectedValueInt);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTI_LoadGroupCannotControlInWindowAdjust,
                                                                        morning,
                                                                        evening,
                                                                        expectedValueInt);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.ints, testViolation.getIntegerParams()));
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }

    {
        VectorHolder vectors;

        CtiDate today(12, 9, 2010);
        CtiTime morning(today, 8, 30, 0);
        CtiTime evening(today, 17, 30, 0);
        int expectedValueInt = 53;

        vectors.times.push_back(morning);
        vectors.times.push_back(evening);
        vectors.ints.push_back(expectedValueInt);

        testConstraintViolation testViolation = testConstraintViolation(ConstraintViolation::CV_TTI_LoadGroupCannotControlInWindowNoAdjust,
                                                                        morning,
                                                                        evening,
                                                                        expectedValueInt);

        BOOST_CHECK(testViolation.getDoubleParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.ints, testViolation.getIntegerParams()));
        BOOST_CHECK(testViolation.getStringParams().empty());
        BOOST_CHECK(fancyFeastEqual(vectors.times, testViolation.getDateTimeParams()));
    }
}

