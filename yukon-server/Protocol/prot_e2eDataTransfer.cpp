#include "precompiled.h"

#include "prot_e2eDataTransfer.h"
#include "e2e_exceptions.h"
#include "coap_helper.h"

extern "C" {
#include "coap/pdu.h"
#include "coap/block.h"
}

#include "logger.h"
#include "std_helper.h"
#include "ctitime.h"

#include <boost/assign/list_of.hpp>

#include <ctime>

namespace Cti::Protocols {

using namespace E2e;

E2eDataTransferProtocol::E2eDataTransferProtocol() :
    _generator(std::time(0))
{
}


void addToken(coap_pdu_t *pdu, const unsigned long token)
{
    unsigned char token_buf[4];

    const unsigned token_len = coap_encode_var_bytes(token_buf, token);

    coap_add_token(pdu, token_len, token_buf);
}


unsigned short E2eDataTransferProtocol::getOutboundId()
{
    return boost::random::uniform_int_distribution<>(0x1000, 0xf000)(_generator);
}


unsigned short E2eDataTransferProtocol::getOutboundIdForEndpoint(const RfnIdentifier endpointId)
{
    if( auto existingId = mapFindRef(_outboundIds, endpointId) )
    {
        existingId->active = true;
        return ++existingId->id;
    }

    const unsigned short newId = getOutboundId() + 1;

    _outboundIds.emplace(endpointId, RequestId { newId, true });

    return newId;
}


auto E2eDataTransferProtocol::sendRequest(const Bytes& payload, const RfnIdentifier endpointId, const unsigned long token) -> Bytes
{
    if( payload.size() > MaxOutboundPayload )
    {
        throw PayloadTooLarge();
    }

    auto pdu = Coap::scoped_pdu_ptr::make_confirmable_request(Coap::RequestMethod::Get, token, getOutboundIdForEndpoint(endpointId));

    coap_add_data(pdu, payload.size(), &payload.front());

    return pdu.as_bytes();
}


auto E2eDataTransferProtocol::sendPost(const Bytes& payload, const RfnIdentifier endpointId, const unsigned long token) -> Bytes
{
    if( payload.size() > MaxOutboundPayload )
    {
        throw PayloadTooLarge();
    }

    auto pdu = Coap::scoped_pdu_ptr::make_nonconfirmable_request(Coap::RequestMethod::Post, token, getOutboundIdForEndpoint(endpointId));

    coap_add_data(pdu, payload.size(), &payload.front());

    return pdu.as_bytes();
}


auto E2eDataTransferProtocol::sendReply(const Bytes& payload, const RfnIdentifier endpointId, const unsigned long token) -> Bytes
{
    if( payload.size() > MaxOutboundPayload )
    {
        throw PayloadTooLarge();
    }

    auto ack_pdu = Coap::scoped_pdu_ptr::make_data_ack(token, getOutboundIdForEndpoint(endpointId), payload);

    return ack_pdu.as_bytes();
}


auto E2eDataTransferProtocol::sendBlockReply(const Bytes& payload, const RfnIdentifier endpointId, const unsigned long token, Coap::Block block) -> Bytes
{
    if( payload.size() > MaxOutboundPayload )
    {
        throw PayloadTooLarge();
    }

    auto ack_pdu = Coap::scoped_pdu_ptr::make_block_ack(token, getOutboundIdForEndpoint(endpointId), payload, block);

    return ack_pdu.as_bytes();
}


YukonError_t E2eDataTransferProtocol::translateIndicationCode(const unsigned short code, const RfnIdentifier endpointId)
{
    switch( auto rc = Coap::ResponseCode { code } )
    {
        case Coap::ResponseCode::Content:  
            return ClientErrors::None;

        case Coap::ResponseCode::NotAcceptable:
            CTILOG_ERROR(dout, "Endpoint indicated Request Not Acceptable for device " << endpointId);
            return ClientErrors::E2eRequestNotAcceptable;

        default:
            CTILOG_DEBUG(dout, "Unexpected response code " << code << " for rfnIdentifier " << endpointId);

            return rc >= Coap::ResponseCode::BadRequest
                ? ClientErrors::E2eBadRequest
                : ClientErrors::None;
    }
}

auto E2eDataTransferProtocol::handleIndication(const Bytes& raw_indication_pdu, const RfnIdentifier endpointId) -> EndpointMessage
{
    EndpointMessage message;

    if( raw_indication_pdu.size() > COAP_MAX_PDU_SIZE )
    {
        throw PayloadTooLarge();
    }

    //  parse the payload into the CoAP packet
    auto indication_pdu = Coap::scoped_pdu_ptr::parse(raw_indication_pdu);

    message.id = indication_pdu->hdr->id;
    
    //  Decode the token
    message.token = coap_decode_var_bytes(indication_pdu->hdr->token, indication_pdu->hdr->token_length);
    
    message.code = indication_pdu->hdr->code;

    switch( indication_pdu->hdr->type )
    {
        case COAP_MESSAGE_ACK:
        {
            message.nodeOriginated = false;
            message.confirmable = false;

            const auto existingId = mapFindRef(_outboundIds, endpointId);

            if( ! existingId )
            {
                throw UnexpectedAck(indication_pdu->hdr->id);
            }
            if( indication_pdu->hdr->id != existingId->id )
            {
                throw UnexpectedAck(indication_pdu->hdr->id, existingId->id);
            }
            if( ! existingId->active )
            {
                throw RequestInactive(message.token);
            }

            existingId->active = false;

            break;
        }
        case COAP_MESSAGE_NON:
        case COAP_MESSAGE_CON:
        {
            //  This is the behavior at present.  Nodes only send "piggybacked responses" in an ACK to Yukon requests, and do not send CoAP "separate responses" yet.
            //    When we do receive separate responses, we will need to switch on the incoming request method/response code in indication_pdu->hdr->code, and ideally
            //    return separate message types for requests vs responses.
            message.nodeOriginated = true;

            if( message.code != COAP_REQUEST_GET )
            {
                CTILOG_WARN(dout, "Unknown request method " << message.code << " (" << indication_pdu->hdr->id << ") for endpointId " << endpointId);

                throw UnknownRequestMethod(message.code, indication_pdu->hdr->id);
            }

            message.confirmable = indication_pdu->hdr->type == COAP_MESSAGE_CON;
            const auto type = message.confirmable ? "CONfirmable" : "NONconfirmable";

            CTILOG_INFO(dout, "Received " << type << " packet ("<< indication_pdu->hdr->id <<") for endpointId "<< endpointId);

            if( indication_pdu->hdr->id == mapFind(_inboundIds, endpointId) )
            {
                CTILOG_WARN(dout, type << " packet was duplicate ("<< indication_pdu->hdr->id <<") for endpointId "<< endpointId);

                throw DuplicatePacket(indication_pdu->hdr->id);
            }

            _inboundIds[endpointId] = indication_pdu->hdr->id;

            break;
        }
        default:
        case COAP_MESSAGE_RST:
        {
            throw ResetReceived();
        }
    }

    coap_opt_iterator_t opt_iter;

    for( auto option = coap_check_option(indication_pdu, COAP_OPTION_URI_PATH, &opt_iter); option; option = coap_option_next(&opt_iter) )
    {
        message.path += "/" + std::string(reinterpret_cast<const char *>(coap_opt_value(option)), coap_opt_length(option));
    }

    //  Extract the data from the packet
    unsigned char *data;
    size_t len;

    coap_get_data(indication_pdu, &len, &data);

    message.data.assign(data, data + len);

    //  Look for any block option
    if( coap_block_t block; coap_get_block(indication_pdu, COAP_OPTION_BLOCK2, &block) )
    {
        if( const auto blockSize = Coap::BlockSize::ofSzx(block.szx) )
        {
            message.block = { block.num, !! block.m, *blockSize };
        }
        else
        {
            throw new InvalidBlockSize(block.szx);
        }
    }

    return message;
}


auto E2eDataTransferProtocol::sendBlockContinuation(const Coap::BlockSize blockSize, const unsigned num, const RfnIdentifier endpointId, const unsigned long token) -> Bytes
{
    auto continuation_pdu = Coap::scoped_pdu_ptr::make_get_continuation(token, getOutboundIdForEndpoint(endpointId), blockSize, num);

    return continuation_pdu.as_bytes();
}


auto E2eDataTransferProtocol::sendAck(const unsigned short id) -> Bytes
{
    return Coap::scoped_pdu_ptr::make_ack(id, Coap::ResponseCode::EmptyMessage).as_bytes();
}

auto E2eDataTransferProtocol::sendBadRequest(const unsigned short id) -> Bytes
{
    return Coap::scoped_pdu_ptr::make_ack(id, Coap::ResponseCode::BadRequest).as_bytes();
}


void E2eDataTransferProtocol::handleTimeout(const RfnIdentifier endpointId)
{
    if( auto existingId = mapFindRef(_outboundIds, endpointId) )
    {
        existingId->active = false;  //  timed out, ignore any late replies
    }
}

}