#pragma once

#include "dev_single.h"  //  for CtiDeviceSingle::point_info

#include <exception>
#include <string>

#include <boost/noncopyable.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB DeviceCommand : boost::noncopyable
{
public:

    typedef std::vector<unsigned char> Bytes;

    virtual bool isComplete()
    {
        return true;
    }

    struct CommandException : std::exception
    {
        CommandException(int code, std::string description) :
            error_code(code),
            error_description(description)
        {
        }

        std::string error_description;
        int error_code;

        virtual const char *what() const
        {
            return error_description.c_str();
        }
    };

    struct point_data : CtiDeviceSingle::point_info
    {
        point_data()
        {
            freeze_bit = false;  //  This is obnoxious.  The freeze_bit only applies to frozen kWh, but it's polluting everything else.
        }

        point_data &operator=(CtiDeviceSingle::point_info &other)
        {
            description = other.description;
            quality     = other.quality;
            value       = other.value;

            return *this;
        }

        CtiPointType_t type;
        unsigned offset;
        std::string name;
        CtiTime time;
    };

protected:

    static unsigned getValueFromBits(const Bytes &data, const unsigned start_offset, const unsigned length);

    static std::vector<unsigned> getValueVectorFromBits(const Bytes &data, const unsigned start_offset, const unsigned length, const unsigned count);

    static void setBits(Bytes &data, const unsigned start_offset, const unsigned length, const unsigned value);

    static unsigned getValueFromBitsLE(const Bytes &data, const unsigned start_offset, const unsigned length);
};

}
}
}
