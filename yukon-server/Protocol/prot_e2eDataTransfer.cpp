#include "precompiled.h"

#include "prot_e2eDataTransfer.h"
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


std::vector<unsigned char> E2eDataTransferProtocol::sendRequest(const std::vector<unsigned char> &payload, const RfnIdentifier endpointId, const unsigned long token)
{
    if( payload.size() > MaxOutboundPayload )
    {
        throw PayloadTooLarge();
    }

    auto pdu = Coap::scoped_pdu_ptr::make_get_request(token, getOutboundIdForEndpoint(endpointId));

    coap_add_data(pdu, payload.size(), &payload.front());

    return pdu.as_bytes();
}


std::vector<unsigned char> E2eDataTransferProtocol::sendReply(const std::vector<unsigned char> &payload, const RfnIdentifier endpointId, const unsigned long token)
{
    if( payload.size() > MaxOutboundPayload )
    {
        throw PayloadTooLarge();
    }

    auto ack_pdu = Coap::scoped_pdu_ptr::make_ack_with_data(token, getOutboundIdForEndpoint(endpointId), payload);

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

E2eDataTransferProtocol::EndpointMessage E2eDataTransferProtocol::handleIndication(const std::vector<unsigned char> &raw_indication_pdu, const RfnIdentifier endpointId)
{
    EndpointMessage message;

    if( raw_indication_pdu.size() > COAP_MAX_PDU_SIZE )
    {
        throw PayloadTooLarge();
    }

    //  parse the payload into the CoAP packet
    auto indication_pdu = Coap::scoped_pdu_ptr::parse(raw_indication_pdu);

    //  Decode the token
    message.token = coap_decode_var_bytes(indication_pdu->hdr->token, indication_pdu->hdr->token_length);
    
    message.status = translateIndicationCode(indication_pdu->hdr->code, endpointId);

    switch( indication_pdu->hdr->type )
    {
        case COAP_MESSAGE_ACK:
        {
            message.nodeOriginated = false;

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
            message.nodeOriginated = true;

            const bool confirmable = indication_pdu->hdr->type == COAP_MESSAGE_CON;
            const std::string type = confirmable ? "CONfirmable" : "NONconfirmable";

            CTILOG_INFO(dout, "Received " << type << " packet ("<< indication_pdu->hdr->id <<") for endpointId "<< endpointId);

            if( indication_pdu->hdr->id == mapFind(_inboundIds, endpointId) )
            {
                CTILOG_WARN(dout, type << " packet was duplicate ("<< indication_pdu->hdr->id <<") for endpointId "<< endpointId);

                throw DuplicatePacket(indication_pdu->hdr->id);
            }

            _inboundIds[endpointId] = indication_pdu->hdr->id;

            if( confirmable )
            {
                message.ack = sendAck(indication_pdu->hdr->id);
            }

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
    coap_block_t block;

    if( coap_get_block(indication_pdu, COAP_OPTION_BLOCK2, &block) )
    {
        if( block.m )
        {
            message.blockContinuation =
                    sendBlockContinuation(block.szx, block.num + 1, endpointId, message.token);
        }
    }

    return message;
}


std::vector<unsigned char> E2eDataTransferProtocol::sendBlockContinuation(const unsigned size, const unsigned num, const RfnIdentifier endpointId, const unsigned long token)
{
    auto continuation_pdu = Coap::scoped_pdu_ptr::make_get_continuation(token, getOutboundIdForEndpoint(endpointId), size, num);

    return continuation_pdu.as_bytes();
}


std::vector<unsigned char> E2eDataTransferProtocol::sendAck(const unsigned short id)
{
    auto ack_pdu = Coap::scoped_pdu_ptr::make_ack(id, Coap::ResponseCode::EmptyMessage);

    return ack_pdu.as_bytes();
}


void E2eDataTransferProtocol::handleTimeout(const RfnIdentifier endpointId)
{
    if( auto existingId = mapFindRef(_outboundIds, endpointId) )
    {
        existingId->active = false;  //  timed out, ignore any late replies
    }
}

}