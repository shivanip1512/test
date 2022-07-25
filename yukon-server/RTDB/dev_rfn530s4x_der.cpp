#include "precompiled.h"

#include "dev_rfn530s4x_der.h"

#include "cmd_rfn_DerPayloadDelivery.h"


namespace Cti::Devices
{

YukonError_t Rfn530s4xDerDevice::executePutConfig(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests)
{
    if ( parse.isKeyValid( "der" ) )
    {
        const auto payload = parse.getsValue( "der" );

        rfnRequests.push_back( std::make_unique<Commands::RfnDerPayloadDeliveryCommand>( getID(), pReq->UserMessageId(), payload ) );

        return ClientErrors::None;
    }

    return ClientErrors::NoMethod;
}

}
