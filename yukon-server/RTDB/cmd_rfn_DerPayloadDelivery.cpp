#include "precompiled.h"

#include "cmd_rfn_DerPayloadDelivery.h"
#include "utility.h"


namespace Cti::Devices::Commands
{

RfnDerPayloadDeliveryCommand::RfnDerPayloadDeliveryCommand( const long paoID, const long messageID, const std::string & payload )
    :   _paoID( paoID ),
        _userMsgID( messageID ),
        _payload( convertHexStringToBytes( payload ) )
{
    // empty...
}

std::string RfnDerPayloadDeliveryCommand::getCommandName() const
{
    return "DER Payload Delivery Request";
}

auto RfnDerPayloadDeliveryCommand::getApplicationServiceId() const -> ASID
{
    return ASID::E2EAP_DER;
}

bool RfnDerPayloadDeliveryCommand::isOscoreEncrypted() const
{
    return false;
}

long RfnDerPayloadDeliveryCommand::getUserMessageId() const
{
    return _userMsgID;
}

auto RfnDerPayloadDeliveryCommand::getCommandHeader() -> Bytes
{
    return { };
}

auto RfnDerPayloadDeliveryCommand::getCommandData() -> Bytes
{
    return _payload;
}

unsigned char RfnDerPayloadDeliveryCommand::getOperation() const
{
    return { };
}

unsigned char RfnDerPayloadDeliveryCommand::getCommandCode() const
{
    return { };
}

RfnDerPayloadDeliveryCommand::ReplyMsg RfnDerPayloadDeliveryCommand::buildReply() const
{
    return
    {
        _paoID,
        _payload,
        _userMsgID,
        std::nullopt
    };
}

RfnCommandResult RfnDerPayloadDeliveryCommand::error( const CtiTime now, const YukonError_t errorCode )
{
    _response = buildReply();

    _response->error = { errorCode, "DER error" };

    return RfnIndividualCommand::error( now, errorCode );
}

RfnCommandResult RfnDerPayloadDeliveryCommand::decodeCommand( const CtiTime now, const RfnResponsePayload & response )
{
    _response = buildReply();

    return "OK";
}

auto RfnDerPayloadDeliveryCommand::getResponseMessage() const -> std::optional<ReplyMsg>
{
    return _response;
}

}

