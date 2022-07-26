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

bool RfnDerPayloadDeliveryCommand::isOscoreEncrypted() const
{
    return true;
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

RfnDerPayloadDeliveryCommand::ReplyMsg RfnDerPayloadDeliveryCommand::buildReply( const YukonError_t errorCode, const std::string & errorText ) const
{
    return
    {
        _paoID,
        _payload,
        _userMsgID,
        Messaging::Rfn::EdgeDrError { errorCode, errorText }
    };
}

RfnCommandResult RfnDerPayloadDeliveryCommand::error( const CtiTime now, const YukonError_t errorCode )
{
    _response = buildReply( errorCode, "Some kind of error..." );

    return RfnIndividualCommand::error( now, errorCode );
}

RfnCommandResult RfnDerPayloadDeliveryCommand::decodeCommand( const CtiTime now, const RfnResponsePayload & response )
{
    _response = buildReply( YukonError_t::None, "OK" );

    return "OK";
}

auto RfnDerPayloadDeliveryCommand::getResponseMessage() const -> std::optional<ReplyMsg>
{
    if ( _response )
    {
        return *_response;
    }
    return std::nullopt;
}

}

