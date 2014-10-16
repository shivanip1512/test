#include "precompiled.h"

#include "prot_e2eDataTransfer.h"

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


class scoped_pdu_ptr
{
    coap_pdu_t *pdu;
public:
    scoped_pdu_ptr(coap_pdu_t *pdu_) : pdu(pdu_) {}

    ~scoped_pdu_ptr() { coap_delete_pdu(pdu); }

    operator coap_pdu_t *()  const { return pdu; }
    coap_pdu_t *operator->() const { return pdu; }
};


void addToken(coap_pdu_t *pdu, const unsigned long token)
{
    unsigned char token_buf[4];

    const unsigned token_len = coap_encode_var_bytes(token_buf, token);

    coap_add_token(pdu, token_len, token_buf);
}


unsigned short E2eDataTransferProtocol::getOutboundIdForEndpoint(long endpointId)
{
    if( ! _outboundIds.count(endpointId) )
    {
        _outboundIds[endpointId] = boost::random::uniform_int_distribution<>(0x1000, 0xf000)(_generator);
    }

    return ++_outboundIds[endpointId];
}


std::vector<unsigned char> E2eDataTransferProtocol::sendRequest(const std::vector<unsigned char> &payload, const long endpointId, const unsigned long token)
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


boost::optional<E2eDataTransferProtocol::EndpointResponse> E2eDataTransferProtocol::handleIndication(const std::vector<unsigned char> &raw_indication_pdu, const long endpointId)
{
    EndpointResponse er;

    if( raw_indication_pdu.size() > COAP_MAX_PDU_SIZE )
    {
        throw PayloadTooLarge();
    }

    //  parse the payload into the CoAP packet
    std::vector<unsigned char> mutable_raw_pdu(raw_indication_pdu);

    scoped_pdu_ptr indication_pdu(coap_pdu_init(COAP_MESSAGE_NON, COAP_REQUEST_GET, COAP_INVALID_TID, COAP_MAX_PDU_SIZE));

    coap_pdu_parse(&mutable_raw_pdu.front(), mutable_raw_pdu.size(), indication_pdu);

    if( indication_pdu->hdr->code >= COAP_RESPONSE_400_BAD_REQUEST )
    {
        CTILOG_ERROR(dout, "Unexpected header code ("<< indication_pdu->hdr->code <<") for endpointId "<< endpointId);

        return boost::none;
    }

    switch( indication_pdu->hdr->type )
    {
        case COAP_MESSAGE_ACK:
        {
            if( indication_pdu->hdr->id != _outboundIds[endpointId] )
            {
                CTILOG_ERROR(dout, "Unexpected ACK ("<< indication_pdu->hdr->id <<" != "<< _outboundIds[endpointId] <<") for endpointId "<< endpointId);

                return boost::none;
            }

            break;
        }
        case COAP_MESSAGE_NON:
        {
            CTILOG_INFO(dout, "Received NONconfirmable packet ("<< indication_pdu->hdr->id <<") for endpointId "<< endpointId);

            if( _inboundIds.count(endpointId) && _inboundIds[endpointId] == indication_pdu->hdr->id )
            {
                CTILOG_WARN(dout, "NONconfirmable packet was duplicate ("<< indication_pdu->hdr->id <<") for endpointId "<< endpointId);

                return boost::none;
            }

            _inboundIds[endpointId] = indication_pdu->hdr->id;

            break;
        }
        case COAP_MESSAGE_CON:
        {
            CTILOG_INFO(dout, "Received CONfirmable packet ("<< indication_pdu->hdr->id <<") for endpointId "<< endpointId);

            if( _inboundIds.count(endpointId) && _inboundIds[endpointId] == indication_pdu->hdr->id )
            {
                CTILOG_WARN(dout, "CONfirmable packet was duplicate ("<< indication_pdu->hdr->id <<") for endpointId "<< endpointId);
            }

            _inboundIds[endpointId] = indication_pdu->hdr->id;

            er.ack =
                    sendAck(indication_pdu->hdr->id);

            break;
        }
        default:
        {
            CTILOG_WARN(dout, "E2eIndicationMsg ID mismatch ("<< indication_pdu->hdr->id <<" != "<< _outboundIds[endpointId] <<") for endpointId "<< endpointId);

            return boost::none;
        }
    }

    //  Decode the token
    er.token = coap_decode_var_bytes(indication_pdu->hdr->token, indication_pdu->hdr->token_length);

    //  Extract the data from the packet
    unsigned char *data;
    size_t len;

    coap_get_data(indication_pdu, &len, &data);

    er.data.assign(data, data + len);

    //  Look for any block option
    coap_block_t block;

    if( coap_get_block(indication_pdu, COAP_OPTION_BLOCK2, &block) )
    {
        if( block.m )
        {
            er.blockContinuation =
                    sendBlockContinuation(block.szx, block.num + 1, endpointId, er.token);
        }
    }

    return er;
}


std::vector<unsigned char> E2eDataTransferProtocol::sendBlockContinuation(const unsigned size, const unsigned num, const long endpointId, const unsigned long token)
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


void E2eDataTransferProtocol::handleTimeout(const long endpointId)
{
    ++_outboundIds[endpointId];  //  invalidate the ID so we ignore any late replies
}


}
}


