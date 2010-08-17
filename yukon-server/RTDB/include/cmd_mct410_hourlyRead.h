#pragma once

#include "cmd_mct410.h"

#include "ctidate.h"

#include <boost/optional.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Mct410HourlyReadCommand : public Mct410Command
{
    CtiDate _date_begin;
    CtiDate _date_end;

    unsigned _channel;

    unsigned _retries;

    boost::optional<double> _midday_reading;

    static read_request_t requestDayBegin(CtiDate &date_begin, const unsigned channel, const CtiDate &Yesterday);
    static read_request_t requestDayEnd  (CtiDate &date_begin, const unsigned channel, const CtiDate &Yesterday);

    static double convertDelta(unsigned delta);

public:

    Mct410HourlyReadCommand(CtiDate date_begin, CtiDate date_end, const unsigned channel);
    ~Mct410HourlyReadCommand() {};

    virtual request_ptr execute(CtiTime now);
    virtual request_ptr decode(CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data_t> &points);
    virtual request_ptr error (CtiTime now, const unsigned function, std::string &description);
};

}
}
}

