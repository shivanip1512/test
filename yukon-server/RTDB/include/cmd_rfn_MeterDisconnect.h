#pragma once

#include "cmd_rfn_Individual.h"
#include "rfn_uom.h"
#include "RfnMeterDisconnectMsg.h"

#include <optional>

namespace Cti::Devices::Commands {

class IM_EX_DEVDB RfnMeterDisconnectCommand : public RfnTwoWayCommand, 
    public InvokerFor<RfnMeterDisconnectCommand>
{
public:

    enum class CommandType : std::uint8_t
    {
        TerminateService  = 0x01,
        ArmForResume      = 0x02,
        ResumeImmediately = 0x03,
        Query             = 0x04
    };

    RfnMeterDisconnectCommand(CommandType action, long userMessageId);

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;
    RfnCommandResult error(const CtiTime now, const YukonError_t errorCode) override;

    std::string getCommandName() const override;
    unsigned char getCommandCode() const override;

    ASID getApplicationServiceId() const override;
    auto getPriorityClass() const -> Messaging::Rfn::PriorityClass override;

    unsigned char getOperation() const override;

    long getUserMessageId() const;

    using ReplyMsg = Messaging::Rfn::RfnMeterDisconnectConfirmationReplyMsg;

    auto getResponseMessage() const -> std::optional<ReplyMsg>;

private:

    const CommandType _action;
    const long _userMessageId;
    std::optional<ReplyMsg> _response;

    enum Command
    {
        Request  = 0x80,
        Response = 0x81
    };

    Bytes getCommandHeader() override;
    Bytes getCommandData()   override;
};

}
