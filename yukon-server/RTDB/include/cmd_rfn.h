#pragma once

#include "cmd_device.h"
#include "dev_single.h"  //  for CtiDeviceSingle::point_info

#include "msg_pdata.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnCommand : public DeviceCommand
{
public:

    typedef std::vector<unsigned char> RfnRequest;
    typedef std::vector<unsigned char> RfnResponse;

    struct RfnResult
    {
        std::string description;
        std::vector<point_data> points;
        std::vector<CtiTableDynamicPaoInfo> paoInfo;
    };

    RfnRequest execute(const CtiTime now);
    virtual RfnResult  decode (const CtiTime now, const RfnResponse &response) = 0;
    virtual RfnResult  error  (const CtiTime now, const YukonError_t error_code) = 0;

protected:

    typedef std::vector<unsigned char> Bytes;

    struct TypeLengthValue
    {
        unsigned char type;
        unsigned char length;
        Bytes value;
    };

    virtual unsigned char getCommandCode() const = 0;
    virtual unsigned char getOperation() const = 0;
    virtual std::vector<TypeLengthValue> getTlvs() = 0;

    static unsigned getValueFromBits(const Bytes &data, const unsigned start_offset, const unsigned length);
    static void setBitsInVector(Bytes &data, const unsigned start_offset, const unsigned length, const unsigned value);

    static std::vector<unsigned> getValueVectorFromBits(const Bytes &data, const unsigned start_offset, const unsigned length, const unsigned count);
};

}
}
}

