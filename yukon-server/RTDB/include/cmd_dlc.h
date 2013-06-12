#pragma once

#include "cmd_device.h"

#include "prot_emetcon.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB DlcCommand : public DeviceCommand
{
public:

    struct request_t
    {
        request_t(unsigned function) :
            function(function)
        { }

        unsigned function;

        //  I would like to typedef away this long name, but it's worth the scads of characters
        //    to keep the type information intact.
        virtual Protocols::EmetconProtocol::IO_Bits io() const = 0;

        virtual unsigned length() const = 0;

        virtual Bytes payload() const  {  return Bytes();  }
    };

    typedef std::auto_ptr<request_t> request_ptr;

    struct read_request_t : request_t
    {
        read_request_t(unsigned function, unsigned length) :
            request_t(function),
            read_length(length)
        { }

        unsigned read_length;

        virtual unsigned length() const  {  return read_length;  }

        virtual Protocols::EmetconProtocol::IO_Bits io() const
        {
            return (function >= 0x100)?
                       (Protocols::EmetconProtocol::IO_Function_Read):
                       (Protocols::EmetconProtocol::IO_Read);
        }
    };

    struct write_request_t : request_t
    {
        write_request_t(unsigned function, Bytes payload) :
            request_t(function),
            data(payload)
        { }

        Bytes data;

        virtual unsigned  length()  const  {  return data.size();  }

        virtual Bytes payload() const  {  return data;  }

        virtual Protocols::EmetconProtocol::IO_Bits io() const
        {
            return (function >= 0x100)?
                       (Protocols::EmetconProtocol::IO_Function_Write):
                       (Protocols::EmetconProtocol::IO_Write);
        }
    };

    virtual request_ptr execute(const CtiTime now) = 0;
    virtual request_ptr decode(const CtiTime now, const unsigned function, const Bytes &payload, std::string &description, std::vector<point_data> &points) = 0;
    virtual request_ptr error (const CtiTime now, const int error_code, std::string &description) = 0;
};

}
}
}

