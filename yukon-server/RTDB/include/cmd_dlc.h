#pragma once

#include "cmd_device.h"

#include "prot_emetcon.h"

namespace Cti {
namespace Devices {
namespace Commands {

//  forward declaration for ResultHandler
class Mct4xxCommand;

struct IM_EX_DEVDB DlcCommand : public DeviceCommand
{
    struct ResultHandler
    {
        virtual void handleCommandResult(const DlcCommand &command)  {};
        //  must include overloads for all children that require a result handler
        virtual void handleCommandResult(const Mct4xxCommand &command)  {};
    };

    //  to be overridden by children that require a result handler
    virtual void invokeResultHandler(ResultHandler &rh) const  { };

    class request_t
    {
    protected:
        unsigned short _function;

    public:
        request_t(unsigned function) :
            _function(function)
        { }

        virtual ~request_t() = default;

        unsigned char function() const  {  return _function & 0xff;  }

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
            return (_function >= 0x100)
                ? (Protocols::EmetconProtocol::IO_Function_Read)
                : (Protocols::EmetconProtocol::IO_Read);
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
            return (_function >= 0x100)
                ? (Protocols::EmetconProtocol::IO_Function_Write)
                : (Protocols::EmetconProtocol::IO_Write);
        }
    };

    virtual request_ptr executeCommand(const CtiTime now) = 0;
    virtual request_ptr decodeCommand (const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points) = 0;
    virtual request_ptr error         (const CtiTime now, const YukonError_t error_code, std::string &description) = 0;
};

}
}
}

