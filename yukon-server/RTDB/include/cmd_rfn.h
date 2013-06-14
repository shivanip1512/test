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

    typedef Bytes RfnRequest;
    typedef Bytes RfnResponse;

    struct RfnResult
    {
        std::string description;
        std::vector<point_data> points;
        std::vector<CtiTableDynamicPaoInfo> paoInfo;
    };

    RfnRequest execute (const CtiTime now);
    virtual RfnResult decode (const CtiTime now, const RfnResponse &response) = 0;
    virtual RfnResult error  (const CtiTime now, const YukonError_t error_code) = 0;

protected:

    //
    // Functions called by execute() to create a request command
    //
    // Request command format :
    // 1-byte - Command Code
    // 1-byte - Operation
    // N-byte - Data
    //
    virtual unsigned char getCommandCode() const = 0;
    virtual unsigned char getOperation() const = 0;
    virtual Bytes         getData() = 0;

    //
    // Type Length Values
    //
    struct TypeLengthValue
    {
        unsigned char type;
        Bytes value;
    };

    static Bytes getBytesFromTlvs( std::vector<TypeLengthValue> tlvs );

};

}
}
}

