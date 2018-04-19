#include "precompiled.h"

#include "prot_e2eDataTransfer.h"
#include "coap_helper.h"

extern "C" {
#include "coap/pdu.h"
#include "coap/block.h"
#undef E  //  CoAP define that interferes with templates
}

#include "logger.h"
#include "std_helper.h"
#include "ctitime.h"

#include <boost/assign/list_of.hpp>

#include <ctime>

namespace Cti {
namespace Protocols {


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
    auto outbound_itr = _outboundIds.find(endpointId);

    if( outbound_itr == _outboundIds.end() )
    {
        outbound_itr = _outboundIds.emplace(endpointId, getOutboundId()).first;
    }

    return ++(outbound_itr->second);
}


std::vector<unsigned char> E2eDataTransferProtocol::sendRequest(const std::vector<unsigned char> &payload, const RfnIdentifier endpointId, const unsigned long token)
{
    if( payload.size() > MaxOutboundPayload )
    {
        throw PayloadTooLarge();
    }

    scoped_pdu_ptr pdu(coap_pdu_init(COAP_MESSAGE_CON, COAP_REQUEST_GET, getOutboundIdForEndpoint(endpointId), COAP_MAX_PDU_SIZE));

    addToken(pdu, token);

    coap_add_data(pdu, payload.size(), &payload.front());

    const unsigned char *raw_pdu = reinterpret_cast<unsigned char *>(pdu->hdr);

    return std::vector<unsigned char>(raw_pdu, raw_pdu + pdu->length);
}


E2eDataTransferProtocol::EndpointMessage E2eDataTransferProtocol::handleIndication(const std::vector<unsigned char> &raw_indication_pdu, const RfnIdentifier endpointId)
{
    EndpointMessage message;

    if( raw_indication_pdu.size() > COAP_MAX_PDU_SIZE )
    {
        throw PayloadTooLarge();
    }

    //  parse the payload into the CoAP packet
    std::vector<unsigned char> mutable_raw_pdu(raw_indication_pdu);

    scoped_pdu_ptr indication_pdu(coap_pdu_init(COAP_MESSAGE_NON, COAP_REQUEST_GET, COAP_INVALID_TID, COAP_MAX_PDU_SIZE));

    coap_pdu_parse(&mutable_raw_pdu.front(), mutable_raw_pdu.size(), indication_pdu);

    if( indication_pdu->hdr->code == COAP_RESPONSE_406_NOT_ACCEPTABLE )
    {
        throw RequestNotAcceptable();
    }
    if( indication_pdu->hdr->code >= COAP_RESPONSE_400_BAD_REQUEST )
    {
        const unsigned human_readable =
            indication_pdu->hdr->code / 32 * 100 +
            indication_pdu->hdr->code % 32;

        throw BadRequest(human_readable);
    }

    switch( indication_pdu->hdr->type )
    {
        case COAP_MESSAGE_ACK:
        {
            message.nodeOriginated = false;

            const auto outbound_itr = _outboundIds.find(endpointId);

            if( outbound_itr == _outboundIds.end() )
            {
                throw UnexpectedAck(indication_pdu->hdr->id);
            }
            else if( indication_pdu->hdr->id != outbound_itr->second )
            {
                throw UnexpectedAck(indication_pdu->hdr->id, _outboundIds[endpointId]);
            }

            break;
        }
        case COAP_MESSAGE_NON:
        case COAP_MESSAGE_CON:
        {
            message.nodeOriginated = true;

            const bool confirmable = indication_pdu->hdr->type == COAP_MESSAGE_CON;
            const std::string type = confirmable ? "CONfirmable" : "NONconfirmable";

            CTILOG_INFO(dout, "Received " << type << " packet ("<< indication_pdu->hdr->id <<") for endpointId "<< endpointId);

            const auto inbound_itr = _inboundIds.find(endpointId);

            if( inbound_itr != _inboundIds.end() && inbound_itr->second == indication_pdu->hdr->id )
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

    //  Decode the token
    message.token = coap_decode_var_bytes(indication_pdu->hdr->token, indication_pdu->hdr->token_length);

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
    scoped_pdu_ptr continuation_pdu = coap_pdu_init(COAP_MESSAGE_CON, COAP_REQUEST_GET, getOutboundIdForEndpoint(endpointId), COAP_MAX_PDU_SIZE);

    addToken(continuation_pdu, token);

    unsigned char buf[4];

    unsigned len = coap_encode_var_bytes(buf, (num << 4) | size);

    coap_add_option(continuation_pdu, COAP_OPTION_BLOCK2, len, buf);

    const unsigned char *raw_pdu = reinterpret_cast<const unsigned char *>(continuation_pdu->hdr);

    return std::vector<unsigned char>(raw_pdu, raw_pdu + continuation_pdu->length);
}


std::vector<unsigned char> E2eDataTransferProtocol::sendAck(const unsigned short id)
{
    scoped_pdu_ptr ack_pdu(coap_pdu_init(COAP_MESSAGE_ACK, COAP_EMPTY_MESSAGE_CODE, id, COAP_MAX_PDU_SIZE));

    const unsigned char *raw_pdu = reinterpret_cast<const unsigned char *>(ack_pdu->hdr);

    return std::vector<unsigned char>(raw_pdu, raw_pdu + ack_pdu->length);
}


void E2eDataTransferProtocol::handleTimeout(const RfnIdentifier endpointId)
{
    auto itr = _outboundIds.find(endpointId);

    if( itr != _outboundIds.end() )
    {
        itr->second++;  //  invalidate the ID so we ignore any late replies
    }
}


}
}


