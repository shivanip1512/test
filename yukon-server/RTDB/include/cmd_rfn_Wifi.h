#pragma once

#include "cmd_rfn_Individual.h"
#include <map>

namespace Cti::Devices::Commands {

class IM_EX_DEVDB RfnWifiGetCommunicationStatusUpdateCommand : public RfnOneWayCommand
{
public:

    //  unused
    unsigned char getOperation()   const override;
    unsigned char getCommandCode() const override;

    std::string getCommandName() override;
    ASID getApplicationServiceId() const override;

private:

    enum class Command : uint8_t
    {
        Request = 0x48
    };

    enum class Operation : uint8_t
    {
        GetCommunicationStatusUpdate = 0x01
    };
        
    Bytes getCommandHeader() override;
    Bytes getCommandData()   override;
};

}