#pragma once

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {


class IM_EX_DEVDB RfDaReadDnpSlaveAddressCommand : public RfnCommand
{
    unsigned short _dnp3SlaveAddress;

protected:

    enum CommandCode
    {
        CommandCode_Request         = 0x01,
        CommandCode_Response        = 0x02
    };

    virtual Bytes getCommandHeader();  //  returns getCommandCode() in a vector
    virtual unsigned char getCommandCode() const;
    virtual Bytes getCommandData();  //  empty
    virtual unsigned char getOperation() const;  //  unused

public:

    virtual void invokeResultHandler( ResultHandler & rh ) const;

    virtual Messaging::Rfn::ApplicationServiceIdentifiers getApplicationServiceId() const;

    unsigned short getDnpSlaveAddress() const;

    virtual RfnCommandResult decodeCommand( const CtiTime now, const RfnResponsePayload & response );
};


}
}
}

