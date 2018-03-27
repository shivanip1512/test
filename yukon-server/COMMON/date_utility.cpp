#include "precompiled.h"

#include "date_utility.h"
#include "utility.h"

#include <boost/optional.hpp>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>

#include <regex>

namespace Cti {

CtiDate parseDateString(std::string date_str)
{
    std::vector<std::string> dateParts;

    boost::split(dateParts, date_str, is_chars{'-','/'});

    if( dateParts.size() == 3 )
    {
        try
        {
            int month = boost::lexical_cast<unsigned>(dateParts[0]);
            int day   = boost::lexical_cast<unsigned>(dateParts[1]);
            int year  = boost::lexical_cast<unsigned>(dateParts[2]);

            if( year < 100 )
            {
                year += 2000;  //  this will need to change around 2050
            }

            //  naive date construction - no range checking, so we count
            //    on CtiDate() resetting itself to 1/1/1970
            return CtiDate(day, month, year);
        }
        catch( boost::bad_lexical_cast &/*ex*/ )
        {
        }
    }

    return CtiDate::neg_infin;
}

boost::optional<TimeParts> parseTimeString(std::string time_str)
{
    std::vector<std::string> timeParts;
    boost::split(timeParts, time_str, is_char{':'});

    //  make sure none of the strings are empty
    if( ! std::count_if(timeParts.begin(), timeParts.end(), boost::bind(&std::string::empty, _1)) )
    {
        try
        {
            TimeParts result = {0, 0, 0};

            switch( timeParts.size() )
            {
                case 3:
                {
                    result.second = boost::lexical_cast<unsigned>(timeParts[2]);
                }   //  fall through
                case 2:
                {
                    result.minute = boost::lexical_cast<unsigned>(timeParts[1]);
                    result.hour   = boost::lexical_cast<unsigned>(timeParts[0]);

                    if( result.second < 60 &&
                        result.minute < 60 &&
                        result.hour   < 24 )
                    {
                        return result;
                    }
                }
            }
        }
        catch( boost::bad_lexical_cast &/*ex*/ )
        {
        }
    }

    return boost::none;
}

boost::optional<std::chrono::duration<double>> parseDurationString(const std::string duration)
{
    const std::regex period_regex { "\\d+(\\.\\d+)?[hms]" };

    if( std::regex_match(duration, period_regex) )
    {
        size_t end;

        const auto count = std::stod(duration, &end);

        if( end + 1 == duration.size() )
        {
            switch( duration.back() )
            {
            case 's':  return std::chrono::duration<double, std::chrono::seconds::period> { count };
            case 'm':  return std::chrono::duration<double, std::chrono::minutes::period> { count };
            case 'h':  return std::chrono::duration<double, std::chrono::hours  ::period> { count };
            }
        }
    }

    return boost::none;
}

}
