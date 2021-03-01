#pragma once

#include "cmd_rfn_Individual.h"
#include "rfn_uom.h"
#include "RfnMeterReadMsg.h"

#include <optional>

namespace Cti::Devices::Commands {

class IM_EX_DEVDB RfnMeterReadCommand : public RfnTwoWayCommand, 
    public InvokerFor<RfnMeterReadCommand>
{
public:

    using ReplyMsg = Messaging::Rfn::RfnMeterReadDataReplyMsg;

    RfnMeterReadCommand(long userMessageId);

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;
    RfnCommandResult error(const CtiTime now, const YukonError_t errorCode) override;

    std::string getCommandName() const override;
    unsigned char getCommandCode() const override;

    ASID getApplicationServiceId() const override;
    auto getPriorityClass() const -> Messaging::Rfn::PriorityClass override;

    unsigned char getOperation() const override;

    long getUserMessageId() const;

    auto getResponseMessage() const -> std::optional<ReplyMsg>;

private:

    long _userMessageId;
    std::optional<ReplyMsg> _response;

    enum Command
    {
        Request = 0x01,
        Response_fmt1  = 0x02,
        Response_fmt23 = 0x03
    };

    Bytes getCommandHeader() override;
    Bytes getCommandData()   override;
};

}
