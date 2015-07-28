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
    typedef YukonErrorException CommandException;

    virtual ~DeviceCommand() = default;

    virtual bool isComplete()
    {
        return true;
    }

    struct point_data : CtiDeviceSingle::point_info
    {
        point_data()
        {
            tags = 0;
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
        unsigned tags;
    };

protected:

    /*
     *  Example with start offset = 5, length = 8
     *
     *  Little Endian
     *  |  7  6  5  4  3  2  1  0 | 15 14 13 12 11 10  9  8 | start offset
     *  | byte 0                  | byte 1                  | byte index
     *  |  2  1  0                |           7  6  5  4  3 | value alignment (msb = 7, lsb = 0)
     *
     *  Big Endian
     *  |  0  1  2  3  4  5  6  7 |  8  9 10 11 12 13 14 15 | start offset
     *  | byte 0                  | byte 1                  | byte index
     *  |                 7  6  5 |  4  3  2  1  0          | value alignment (msb = 7, lsb = 0)
     *
     */

    static unsigned getValueFromBits_bEndian(const Bytes &data, const unsigned start_offset, const unsigned length);
    static unsigned getValueFromBits_lEndian(const Bytes &data, const unsigned start_offset, const unsigned length);

    static void setBits_bEndian(Bytes &data, const unsigned start_offset, const unsigned length, const unsigned value);
    static void setBits_lEndian(Bytes &data, const unsigned start_offset, const unsigned length, const unsigned value);

    static std::vector<unsigned> getValueVectorFromBits_bEndian(const Bytes &data, const unsigned start_offset, const unsigned length, const unsigned count);

};


}
}
}
