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

    class delay_request_t;
    class emetcon_request_t;

    struct request_t
    {
        virtual ~request_t() = default;

        using CtiMessageList = std::list<CtiMessage *>;
        using OutMessageList = std::list<OUTMESS *>;

        struct RequestHandler
        {
            virtual void handleRequest(const delay_request_t &,   const INMESS &im, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) = 0;
            virtual void handleRequest(const emetcon_request_t &, const INMESS &im, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) = 0;
        };

        virtual void invokeRequestHandler(RequestHandler &rh, const INMESS &im, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) const = 0;
    };

    typedef std::unique_ptr<request_t> request_ptr;

    struct delay_request_t : request_t
    {
        CtiTime delay;

        delay_request_t(CtiTime until) :
            delay(until)
        { }

        void invokeRequestHandler(RequestHandler &rh, const INMESS &im, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) const override
        {
            rh.handleRequest(*this, im, vgList, retList, outList);
        }
    };

    class emetcon_request_t : public request_t
    {
    protected:
        unsigned short _function;

    public:
        emetcon_request_t(unsigned function) :
            _function(function)
        { }

        unsigned char function() const  {  return _function & 0xff;  }

        //  I would like to typedef away this long name, but it's worth the scads of characters
        //    to keep the type information intact.
        virtual Protocols::EmetconProtocol::IO_Bits io() const = 0;

        virtual unsigned length() const = 0;

        virtual Bytes payload() const  {  return Bytes();  }

        void invokeRequestHandler(RequestHandler &rh, const INMESS &im, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) const override
        {
            rh.handleRequest(*this, im, vgList, retList, outList);
        }
    };

    typedef std::unique_ptr<emetcon_request_t> emetcon_request_ptr;

    struct read_request_t : emetcon_request_t
    {
        read_request_t(unsigned function, unsigned length) :
            emetcon_request_t(function),
            read_length(length)
        { }

        unsigned read_length;

        unsigned length() const override  {  return read_length;  }

        Protocols::EmetconProtocol::IO_Bits io() const override
        {
            return (_function >= 0x100)
                ? (Protocols::EmetconProtocol::IO_Function_Read)
                : (Protocols::EmetconProtocol::IO_Read);
        }
    };

    struct write_request_t : emetcon_request_t
    {
        write_request_t(unsigned function, Bytes payload) :
            emetcon_request_t(function),
            data(payload)
        { }

        Bytes data;

        unsigned length()  const override  {  return data.size();  }
        Bytes    payload() const override  {  return data;  }

        Protocols::EmetconProtocol::IO_Bits io() const override
        {
            return (_function >= 0x100)
                ? (Protocols::EmetconProtocol::IO_Function_Write)
                : (Protocols::EmetconProtocol::IO_Write);
        }
    };

    virtual emetcon_request_ptr executeCommand(const CtiTime now) = 0;
    virtual request_ptr decodeCommand (const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points) = 0;
    virtual request_ptr error         (const CtiTime now, const YukonError_t error_code, std::string &description) = 0;

    virtual void cancel()  { }  //  no-op by default
};

}
}
}

