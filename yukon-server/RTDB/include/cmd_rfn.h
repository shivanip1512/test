#pragma once

#include "cmd_device.h"

#include <boost/shared_ptr.hpp>

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
    };

    RfnRequest executeCommand(const CtiTime now);
    virtual RfnResult decodeCommand(const CtiTime now, const RfnResponse &response) = 0;
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
    virtual Bytes         getCommandData() = 0;

    virtual void    prepareCommandData(const CtiTime & now) { }

    //
    // Type Length Values
    //
    struct TypeLengthValue
    {
        unsigned char type;
        Bytes value;

        TypeLengthValue(unsigned char type_) : type(type_)  {}
        TypeLengthValue(unsigned char type_, Bytes value_) : type(type_), value(value_)  {}
    };

    static Bytes getBytesFromTlvs( const std::vector<TypeLengthValue> &tlvs );

    static std::vector<TypeLengthValue> getTlvsFromBytes( const Bytes &bytes );
    static std::vector<TypeLengthValue> getLongTlvsFromBytes( const Bytes &bytes );

private:

    static std::vector<TypeLengthValue> getTlvsFromBytesWithLength( const unsigned length, const Bytes &bytes );
};

typedef boost::shared_ptr<RfnCommand> RfnCommandSPtr;

}
}
}

