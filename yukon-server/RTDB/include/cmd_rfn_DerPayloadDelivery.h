#pragma once

#include "cmd_rfn_Individual.h"
#include "RfnEdgeDrMessaging.h"


namespace Cti::Devices::Commands
{

class IM_EX_DEVDB RfnDerPayloadDeliveryCommand
    :   public RfnTwoWayCommand,
        public InvokerFor<RfnDerPayloadDeliveryCommand>
{
public:

    RfnDerPayloadDeliveryCommand( const long paoID, const long messageID, const std::string & payload );

    std::string getCommandName() const override;

    ASID getApplicationServiceId() const override;

    bool isOscoreEncrypted() const override;

    long getUserMessageId() const;

    using ReplyMsg = Messaging::Rfn::EdgeDrDataNotification;

    auto getResponseMessage() const -> std::optional<ReplyMsg>;

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;
    RfnCommandResult error(const CtiTime now, const YukonError_t errorCode) override;

private:

    ReplyMsg buildReply() const;

    Bytes getCommandHeader() override;
    Bytes getCommandData()   override;

    //  unused
    unsigned char getOperation()   const override;
    unsigned char getCommandCode() const override;

    const long  _paoID;
    const long  _userMsgID;
    const Bytes _payload;

    std::optional<ReplyMsg> _response;
};

}

