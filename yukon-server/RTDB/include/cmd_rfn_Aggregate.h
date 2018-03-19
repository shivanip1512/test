#pragma once

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnAggregateCommand : public RfnCommand, 
       InvokerFor<RfnAggregateCommand>
{
public:

    RfnAggregateCommand(RfnCommandList commands);

    ASID getApplicationServiceId() const override;

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response);

private:

    unsigned char getCommandCode() const override;
    unsigned char getOperation()   const override;

    Bytes getCommandHeader() override;
    Bytes getCommandData()   override;

    RfnCommandList _commands;
};

}
}
}
