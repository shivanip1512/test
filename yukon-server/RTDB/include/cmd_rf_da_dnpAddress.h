#pragma once

#include "cmd_rfn_Individual.h"

namespace Cti {
namespace Devices {
namespace Commands {


class IM_EX_DEVDB RfDaReadDnpSlaveAddressCommand : public RfnTwoWayCommand,
       InvokerFor<RfDaReadDnpSlaveAddressCommand>
{
    unsigned short _dnp3SlaveAddress;

protected:

    enum CommandCode
    {
        CommandCode_Request         = 0x35,
        CommandCode_Response        = 0x36
    };

    virtual Bytes getCommandHeader();  //  returns getCommandCode() in a vector
    virtual unsigned char getCommandCode() const;
    virtual Bytes getCommandData();  //  empty
    virtual unsigned char getOperation() const;  //  unused

public:

    virtual ASID getApplicationServiceId() const;

    unsigned short getDnpSlaveAddress() const;

    RfnCommandResult decodeCommand( const CtiTime now, const RfnResponsePayload & response ) override;

    std::string getCommandName() override;
};


}
}
}

