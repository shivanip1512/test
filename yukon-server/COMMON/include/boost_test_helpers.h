#pragma once

#include "millisecond_timer.h"
#include "ctitime.h"
#include "ctidate.h"

#include <boost/function.hpp>
#include <boost/bind.hpp>
#include <boost/optional.hpp>

#include <vector>

namespace Cti {

#define BOOST_CHECK_EQUAL_RANGES(x, y) \
BOOST_CHECK_EQUAL_COLLECTIONS((x).begin(), (x).end(), (y).begin(), (y).end())

#define BOOST_REQUIRE_EQUAL_RANGES(x, y) \
BOOST_REQUIRE_EQUAL_COLLECTIONS((x).begin(), (x).end(), (y).begin(), (y).end())

namespace Test {

namespace {

char fromAscii(char c)
{
    if( c >= 'a' && c <= 'f' )  return c - 'a' + 10;

    if( c >= 'A' && c <= 'F' )  return c - 'A' + 10;

    if( c >= '0' && c <= '9' )  return c - '0';

    return 0;
}

}

struct byte_str
{
    typedef std::vector<unsigned char> uchar_vector;
    typedef std::vector<unsigned char> printable_vector;

    uchar_vector bytes;
    printable_vector printable;

    byte_str(const char *str)
    {
        if( ! str )
        {
            return;
        }

        boost::optional<unsigned char> byte;

        while( *str )
        {
            if( *str != ' ' )
            {
                if( byte )
                {
                    *byte <<= 4;
                    *byte |= fromAscii(*str);
                }
                else
                {
                    byte = fromAscii(*str);
                }
            }

            ++str;

            if( *str == ' ' || ! *str )
            {
                if( byte )
                {
                    bytes.push_back(*byte);
                    byte.reset();
                }
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

namespace {  //  hack to get around multiple linkages when included in multiple translation units

class Override_CtiTime_Now
{
    boost::function<CtiTime()> _oldMakeNow;

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
        Cti::Time::MakeNowTime = boost::bind(&Override_CtiTime_Now::MakeNow, this);
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

    CtiDate MakeNow()
    {
        return _newNow;
    }

public:
    Override_CtiDate_Now(CtiDate newEpoch) :
        _newNow(newEpoch)
    {
        _oldMakeNow = boost::bind(&Override_CtiDate_Now::MakeNow, this);

        std::swap(_oldMakeNow, Cti::Date::MakeNowDate);
    }

    ~Override_CtiDate_Now()
    {
        std::swap(_oldMakeNow, Cti::Date::MakeNowDate);
    }
};

}
}
}
