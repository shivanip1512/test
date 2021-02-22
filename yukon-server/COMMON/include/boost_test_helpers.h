#pragma once

#include "millisecond_timer.h"
#include "ctitime.h"
#include "ctidate.h"
#include "dbaccess.h"
#include "GlobalSettings.h"

#include <boost/bind/bind.hpp>
#include <boost/optional.hpp>
#include <boost/optional/optional_io.hpp>

#include <vector>

namespace Cti {

#define BOOST_CHECK_EQUAL_RANGES(x, y) do { \
    const auto& l = (x); \
    const auto& r = (y); \
    BOOST_CHECK_EQUAL_COLLECTIONS(l.begin(), l.end(), r.begin(), r.end()); \
} while ( false )

#define BOOST_REQUIRE_EQUAL_RANGES(x, y) do { \
    const auto& l = (x); \
    const auto& r = (y); \
    BOOST_REQUIRE_EQUAL_COLLECTIONS(l.begin(), l.end(), r.begin(), r.end()); \
} while ( false )

}

namespace Cti::Test {
struct use_in_unit_tests_only {};
}

extern Cti::Test::use_in_unit_tests_only test_tag;

namespace Cti::Test {

struct byte_str
{
    typedef std::vector<unsigned char> uchar_vector;
    typedef std::vector<unsigned char> printable_vector;

    uchar_vector bytes;
    printable_vector printable;

    byte_str(const char *str)
    {
        for( char* pos; *str;  )
        {
            if( *str == ' ' )
            {
                ++str;
            }
            else
            {
                bytes.push_back(strtoul(str, &pos, 16));

                str = pos;
            }
        }

        printable.assign(bytes.begin(), bytes.end());
    }

    printable_vector::const_iterator begin() const {  return printable.begin();  }
    printable_vector::const_iterator end()   const {  return printable.end();  }

    const size_t size() const   {  return bytes.size();  }

    const unsigned char *data()      const  {  return &bytes.front();  }
    const char          *char_data() const  {  return reinterpret_cast<const char *>(data());  }
};

namespace {  //  internal linkage so we can include it in multiple translation units

struct test_GlobalSettings : Cti::GlobalSettings
{
    std::string getStringImpl(Strings  setting, std::string default) override
    {
        return default;
    }
    int getIntegerImpl(Integers setting, int default) override
    {
        return default;
    }
    bool getBooleanImpl(Booleans setting, bool default) override
    {
        return default;
    }
};

class Override_GlobalSettings
{
    std::unique_ptr<GlobalSettings> origGlobalSettings;
public:
    Override_GlobalSettings()
    {
        origGlobalSettings = std::make_unique<test_GlobalSettings>();

        std::swap(origGlobalSettings, gGlobalSettings);
    }

    ~Override_GlobalSettings()
    {
        std::swap(origGlobalSettings, gGlobalSettings);
    }
};

struct dst_shifts {
    struct shift {
        int month;  //  1-indexed
        int day;    //  1-indexed
        std::chrono::minutes hh_mm;
    };
    shift start;
    shift end;
};

using namespace std::chrono_literals;

static const std::array<dst_shifts, 60> dst_shift_table { 
                dst_shifts
    /* 1970 */  { { 4, 26, 2h }, { 10, 25, 2h } },
    /* 1971 */  { { 4, 25, 2h }, { 10, 31, 2h } },
    /* 1972 */  { { 4, 30, 2h }, { 10, 29, 2h } },
    /* 1973 */  { { 4, 29, 2h }, { 10, 28, 2h } },
    /* 1974 */  { { 1,  6, 2h }, { 10, 27, 2h } },
    /* 1975 */  { { 2, 23, 2h }, { 10, 26, 2h } },
    /* 1976 */  { { 4, 25, 2h }, { 10, 31, 2h } },
    /* 1977 */  { { 4, 24, 2h }, { 10, 30, 2h } },
    /* 1978 */  { { 4, 30, 2h }, { 10, 29, 2h } },
    /* 1979 */  { { 4, 29, 2h }, { 10, 28, 2h } },
    /* 1980 */  { { 4, 27, 2h }, { 10, 26, 2h } },
    /* 1981 */  { { 4, 26, 2h }, { 10, 25, 2h } },
    /* 1982 */  { { 4, 25, 2h }, { 10, 31, 2h } },
    /* 1983 */  { { 4, 24, 2h }, { 10, 30, 2h } },
    /* 1984 */  { { 4, 29, 2h }, { 10, 28, 2h } },
    /* 1985 */  { { 4, 28, 2h }, { 10, 27, 2h } },
    /* 1986 */  { { 4, 27, 2h }, { 10, 26, 2h } },
    /* 1987 */  { { 4,  5, 2h }, { 10, 25, 2h } },
    /* 1988 */  { { 4,  3, 2h }, { 10, 30, 2h } },
    /* 1989 */  { { 4,  2, 2h }, { 10, 29, 2h } },
    /* 1990 */  { { 4,  1, 2h }, { 10, 28, 2h } },
    /* 1991 */  { { 4,  7, 2h }, { 10, 27, 2h } },
    /* 1992 */  { { 4,  5, 2h }, { 10, 25, 2h } },
    /* 1993 */  { { 4,  4, 2h }, { 10, 31, 2h } },
    /* 1994 */  { { 4,  3, 2h }, { 10, 30, 2h } },
    /* 1995 */  { { 4,  2, 2h }, { 10, 29, 2h } },
    /* 1996 */  { { 4,  7, 2h }, { 10, 27, 2h } },
    /* 1997 */  { { 4,  6, 2h }, { 10, 26, 2h } },
    /* 1998 */  { { 4,  5, 2h }, { 10, 25, 2h } },
    /* 1999 */  { { 4,  4, 2h }, { 10, 31, 2h } },
    /* 2000 */  { { 4,  2, 2h }, { 10, 29, 2h } },
    /* 2001 */  { { 4,  1, 2h }, { 10, 28, 2h } },
    /* 2002 */  { { 4,  7, 2h }, { 10, 27, 2h } },
    /* 2003 */  { { 4,  6, 2h }, { 10, 26, 2h } },
    /* 2004 */  { { 4,  4, 2h }, { 10, 31, 2h } },
    /* 2005 */  { { 4,  3, 2h }, { 10, 30, 2h } },
    /* 2006 */  { { 4,  2, 2h }, { 10, 29, 2h } },
    /* 2007 */  { { 3, 11, 2h }, { 11,  4, 2h } },
    /* 2008 */  { { 3,  9, 2h }, { 11,  2, 2h } },
    /* 2009 */  { { 3,  8, 2h }, { 11,  1, 2h } },
    /* 2010 */  { { 3, 14, 2h }, { 11,  7, 2h } },
    /* 2011 */  { { 3, 13, 2h }, { 11,  6, 2h } },
    /* 2012 */  { { 3, 11, 2h }, { 11,  4, 2h } },
    /* 2013 */  { { 3, 10, 2h }, { 11,  3, 2h } },
    /* 2014 */  { { 3,  9, 2h }, { 11,  2, 2h } },
    /* 2015 */  { { 3,  8, 2h }, { 11,  1, 2h } },
    /* 2016 */  { { 3, 13, 2h }, { 11,  6, 2h } },
    /* 2017 */  { { 3, 12, 2h }, { 11,  5, 2h } },
    /* 2018 */  { { 3, 11, 2h }, { 11,  4, 2h } },
    /* 2019 */  { { 3, 10, 2h }, { 11,  3, 2h } },
    /* 2020 */  { { 3,  8, 2h }, { 11,  1, 2h } },
    /* 2021 */  { { 3, 14, 2h }, { 11,  7, 2h } },
    /* 2022 */  { { 3, 13, 2h }, { 11,  6, 2h } },
    /* 2023 */  { { 3, 12, 2h }, { 11,  5, 2h } },
    /* 2024 */  { { 3, 10, 2h }, { 11,  3, 2h } },
    /* 2025 */  { { 3,  9, 2h }, { 11,  2, 2h } },
    /* 2026 */  { { 3,  8, 2h }, { 11,  1, 2h } },
    /* 2027 */  { { 3, 14, 2h }, { 11,  7, 2h } },
    /* 2028 */  { { 3, 12, 2h }, { 11,  5, 2h } },
    /* 2029 */  { { 3, 11, 2h }, { 11,  4, 2h } },
};

struct Test_TimezoneHelper : Cti::Time::TimezoneHelper
{
    const TIME_ZONE_INFORMATION timezone;

    Test_TimezoneHelper(TIME_ZONE_INFORMATION tzi)
        :   timezone(tzi)
    {
    }

    static dst_shifts get_dst_shift(int years_since_1970)
    {
        //  If we have a record for that year, return it directly
        if( years_since_1970 < dst_shift_table.size() )
        {
            return dst_shift_table[years_since_1970];
        }

        //  Otherwise, calculate it using the 2007 DST rules
        tm second_week_of_march {};
        second_week_of_march.tm_year = years_since_1970 + 70;
        second_week_of_march.tm_mon = 2;  //  0-based
        second_week_of_march.tm_mday = 15;
        const time_t start = ::_mkgmtime(&second_week_of_march);
        //  call gmtime to get the day of week (tm_wday, 0-6)
        gmtime_s(&second_week_of_march, &start);

        tm first_week_of_november {};
        first_week_of_november.tm_year = years_since_1970 + 70;
        first_week_of_november.tm_mon = 10;  //  0-based
        first_week_of_november.tm_mday = 7;
        const time_t end = ::_mkgmtime(&first_week_of_november);
        //  call gmtime to get the day of week (tm_wday, 0-6)
        gmtime_s(&first_week_of_november, &end);

        return {
            //  Subtract off the day of week to move back to Sunday
            {  3, second_week_of_march.tm_mday   - second_week_of_march.tm_wday,   2h },
            { 11, first_week_of_november.tm_mday - first_week_of_november.tm_wday, 2h } };
    }

    tm localtime(const ctitime_t* seconds) const override
    {
        namespace chr = std::chrono;

        auto local_seconds =
            seconds
                ? *seconds
                : time(nullptr);

        tm as_standard {};
        const auto standard_seconds = local_seconds - ((timezone.Bias + timezone.StandardBias) * 60);

        //  First calculate in Standard time
        ::gmtime_s(&as_standard, &standard_seconds);
        as_standard.tm_isdst = false;

        //  If we do not have a daylight offset, then return the standard time without any further checks
        if( ! timezone.DaylightName[0] )
        {
            return as_standard;
        }

        //  tm_year is years since 1900, so bail if before 1970
        if( as_standard.tm_year < 70 )
        {
            return as_standard;
        }

        const auto& dst_shift = get_dst_shift(as_standard.tm_year - 70);

        const auto standard_tpl = std::tuple(as_standard.tm_mon + 1, as_standard.tm_mday, as_standard.tm_hour * 60 + as_standard.tm_min);
        const auto dst_start    = std::tuple(dst_shift.start.month, dst_shift.start.day, dst_shift.start.hh_mm.count());

        if( standard_tpl < dst_start )
        {
            return as_standard;
        }

        tm as_daylight{};
        const auto daylight_seconds = local_seconds - ((timezone.Bias + timezone.DaylightBias) * 60);

        //  Then calculate as Daylight time
        ::gmtime_s(&as_daylight, &daylight_seconds);
        as_daylight.tm_isdst = true;

        const auto daylight_tpl = std::tuple(as_daylight.tm_mon + 1, as_daylight.tm_mday, as_daylight.tm_hour * 60 + as_daylight.tm_min);
        const auto dst_end      = std::tuple(dst_shift.end.month, dst_shift.end.day, dst_shift.end.hh_mm.count());

        if( daylight_tpl >= dst_end )
        {
            return as_standard;
        }

        return as_daylight;
    }
    tm gmtime(const ctitime_t* seconds) const override
    {
        tm result {};
        ::gmtime_s(&result, seconds);
        return result;
    }
    ctitime_t mktime(tm* descriptor) const override
    {
        if( ! descriptor )
        {
            return _mkgmtime(descriptor);
        }

        namespace chr = std::chrono;

        auto offset = timezone.Bias + timezone.StandardBias;

        //  If we have a daylight offset, check to see if this falls within it
        if( timezone.DaylightName[0] )
        {
            //  tm_year is years since 1900, so allow anything from 1970 onward
            if( descriptor->tm_year >= 70 )
            {
                const auto& dst_shift = get_dst_shift(descriptor->tm_year - 70);

                //  Skip the ambiguous time - consider us back in Standard time at 1 AM
                const auto dst_end_hhmm   = dst_shift.end.hh_mm.count() - (timezone.StandardBias - timezone.DaylightBias);

                const auto md_hhmm   = std::tuple(descriptor->tm_mon + 1, descriptor->tm_mday, descriptor->tm_hour * 60 + descriptor->tm_min);
                const auto dst_start = std::tuple(dst_shift.start.month, dst_shift.start.day, dst_shift.start.hh_mm.count());
                const auto dst_end   = std::tuple(dst_shift.end.month,   dst_shift.end.day,   dst_end_hhmm);

                if( md_hhmm >= dst_start &&
                    md_hhmm < dst_end )
                {
                    offset = timezone.Bias + timezone.DaylightBias;
                }
            }
        }

        const auto result = _mkgmtime(descriptor);

        return result + offset * 60;
    }
    TIME_ZONE_INFORMATION GetTZI() const override
    {
        return timezone;
    }
};

enum class Timezones : size_t
{
    USA_Eastern,
    USA_Central,
    USA_Mountain,
    USA_Pacific,
    USA_Arizona,
    IND_Standard,
    NPL_Standard,
};

template <Timezones t>
class Override_Timezone
{
public:
    Override_Timezone()
    {
        const auto index = static_cast<size_t>(t);
        static_assert(index < timezones.size());
        oldHelper =
            Cti::Time::exchangeTimezoneHelper(
                std::make_unique<Test_TimezoneHelper>(timezones[index]), 
                test_tag);
    }
    ~Override_Timezone()
    {
        auto discard = Cti::Time::exchangeTimezoneHelper(std::move(oldHelper), test_tag);
    }

private:
    std::unique_ptr<Cti::Time::TimezoneHelper> oldHelper;
        
    static inline const std::array<TIME_ZONE_INFORMATION, 7> timezones {
        TIME_ZONE_INFORMATION
        {
            5 * 60,
            L"Eastern Standard Time", {}, 0,
            L"Eastern Daylight Time", {}, -60 },
        {   
            6 * 60,
            L"Central Standard Time", {}, 0,
            L"Central Daylight Time", {}, -60 },
        {   
            7 * 60,
            L"Mountain Standard Time", {}, 0,
            L"Mountain Daylight Time", {}, -60 },
        {   
            8 * 60,
            L"Pacific Standard Time", {}, 0,
            L"Pacific Daylight Time", {}, -60 },
        {   
            7 * 60,
            L"US Mountain Standard Time", {}, 0,
            L"", {}, 0 },
        {   
            -(5 * 60 + 30),
            L"India Standard Time", {}, 0,
            L"", {}, 0 },
        {   
            -(5 * 60 + 45),
            L"Nepal Standard Time", {}, 0,
            L"", {}, 0 },
    };
};

class Override_CtiTime_Now
{
    std::function<CtiTime()> _oldMakeNow;

    CtiTime _newNow;
    Cti::Timing::MillisecondTimer _elapsed;

    CtiTime MakeNow()
    {
        return _newNow + _elapsed.elapsed() / 1000;
    }

public:
    Override_CtiTime_Now(CtiTime newEpoch) :
        _newNow(newEpoch)
    {
        _oldMakeNow = Cti::Time::exchangeMakeNow([this]{ return MakeNow(); }, test_tag);
    }

    ~Override_CtiTime_Now()
    {
        auto discard = Cti::Time::exchangeMakeNow(_oldMakeNow, test_tag);
    }
};

CtiTime parseIso8601String(std::string iso_str)
{
    //  Requires a string in the format yyyy-mm-dd hh:mm:ss

    if( iso_str.length() == 19 )
    {
        try
        {
            const auto year = boost::lexical_cast<unsigned>(iso_str.substr(0, 4));
            const auto month = boost::lexical_cast<unsigned>(iso_str.substr(5, 2));
            const auto day = boost::lexical_cast<unsigned>(iso_str.substr(8, 2));

            const auto hour = boost::lexical_cast<unsigned>(iso_str.substr(11, 2));
            const auto min = boost::lexical_cast<unsigned>(iso_str.substr(14, 2));
            const auto sec = boost::lexical_cast<unsigned>(iso_str.substr(17, 2));

            //  naive date construction - no range checking, so we count
            //    on CtiDate() resetting itself to 1/1/1970
            return CtiTime(CtiDate(day, month, year), hour, min, sec);
        }
        catch( boost::gregorian::bad_year&/*ex*/ )
        {
        }
        catch( boost::bad_lexical_cast&/*ex*/ )
        {
        }
    }

    return CtiTime::neg_infin;
}

[[nodiscard]] auto set_to_central_timezone()
{
    return Override_Timezone<Timezones::USA_Central>();
}

[[nodiscard]] auto set_to_eastern_timezone()
{
    return Override_Timezone<Timezones::USA_Eastern>();
}

[[nodiscard]] auto set_to_pacific_timezone()
{
    return Override_Timezone<Timezones::USA_Pacific>();
}

[[nodiscard]] auto set_to_india_timezone()
{
    return Override_Timezone<Timezones::IND_Standard>();
}

[[nodiscard]] auto set_to_nepal_timezone()
{
    return Override_Timezone<Timezones::NPL_Standard>();
}

class Override_CtiDate_Now
{
    std::function<CtiDate()> _oldMakeNow;

    CtiDate _newNow;

public:
    Override_CtiDate_Now(CtiDate newEpoch) :
        _newNow(newEpoch)
    {
        _oldMakeNow = [this]{ return _newNow; };

        std::swap(_oldMakeNow, Cti::Date::MakeNowDate);
    }

    ~Override_CtiDate_Now()
    {
        std::swap(_oldMakeNow, Cti::Date::MakeNowDate);
    }
};

struct PreventDatabaseConnections
{
    std::function<SAConnection*(void)> oldDatabaseConnectionFactory;

    PreventDatabaseConnections()
        :   oldDatabaseConnectionFactory { Cti::Database::gDatabaseConnectionFactory }
    {
        Cti::Database::gDatabaseConnectionFactory = throwingFactory;
    }

    static SAConnection* throwingFactory()
    {
        throw std::exception("Database access is forbidden in unit tests");
    }

    ~PreventDatabaseConnections()
    {
        Cti::Database::gDatabaseConnectionFactory = oldDatabaseConnectionFactory;
    }
};

}
}