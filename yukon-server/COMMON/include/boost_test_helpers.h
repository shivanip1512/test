#pragma once

#include "millisecond_timer.h"
#include "ctitime.h"
#include "ctidate.h"
#include "dbaccess.h"
#include "GlobalSettings.h"

#include <boost/bind.hpp>
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
    
class Override_CtiTime_TimeZoneInformation
{
    TIME_ZONE_INFORMATION _newTzi;

public:
    Override_CtiTime_TimeZoneInformation(TIME_ZONE_INFORMATION newTzi) :
        _newTzi(newTzi)
    {
        Cti::Time::overrideTimeZoneInformation(&_newTzi, test_tag);
    }

    ~Override_CtiTime_TimeZoneInformation()
    {
        Cti::Time::overrideTimeZoneInformation(nullptr, test_tag);
    }
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
        _newNow(newEpoch),
        _oldMakeNow(Cti::Time::MakeNowTime)
    {
        Cti::Time::MakeNowTime = [this]{ return MakeNow(); };
    }

    ~Override_CtiTime_Now()
    {
        Cti::Time::MakeNowTime = _oldMakeNow;
    }
};

void set_to_central_timezone()
{
    _putenv_s("TZ", "CST6CDT");
    _tzset();
}

void set_to_eastern_timezone()
{
    _putenv_s("TZ", "EST5EDT");
    _tzset();
}

void unset_timezone()
{
    _putenv_s("TZ", "");
    _tzset();
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