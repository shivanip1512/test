#pragma once

#include "cmd_base.h"
#include "dev_single.h"  //  for CtiDeviceSingle::point_info

#include "words.h"
#include "prot_emetcon.h"
#include "msg_pdata.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB DlcCommand : public BaseCommand
{
public:

    typedef std::vector<unsigned char> payload_t;

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

        virtual payload_t payload() const  {  return payload_t();  }
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
        write_request_t(unsigned function, payload_t payload) :
            request_t(function),
            data(payload)
        { }

        payload_t data;

        virtual unsigned  length()  const  {  return data.size();  }

        virtual payload_t payload() const  {  return data;  }

        virtual Protocols::EmetconProtocol::IO_Bits io() const
        {
            return (function >= 0x100)?
                       (Protocols::EmetconProtocol::IO_Function_Write):
                       (Protocols::EmetconProtocol::IO_Write);
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

    virtual request_ptr execute(const CtiTime now) = 0;
    virtual request_ptr decode(const CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points) = 0;
    virtual request_ptr error (const CtiTime now, const int error_code, std::string &description) = 0;

protected:

    static unsigned getValueFromBits(const payload_t &data, const unsigned start_offset, const unsigned length);

    static std::vector<unsigned> getValueVectorFromBits(const payload_t &data, const unsigned start_offset, const unsigned length, const unsigned count);

};

}
}
}

