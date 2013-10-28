#include "precompiled.h"

#include "prot_e2eDataTransfer.h"

extern "C" {
#include "coap/pdu.h"
#include "coap/block.h"
}

#include "std_helper.h"

#include <boost/assign/list_of.hpp>

namespace Cti {
namespace Protocols {

class scoped_pdu_ptr
{
    coap_pdu_t *pdu;
public:
    scoped_pdu_ptr(coap_pdu_t *pdu_) : pdu(pdu_) {}

    ~scoped_pdu_ptr() { coap_delete_pdu(pdu); }

    operator coap_pdu_t *()  const { return pdu; }
    coap_pdu_t *operator->() const { return pdu; }
};


std::vector<unsigned char> E2eDataTransferProtocol::sendRequest(const std::vector<unsigned char> &payload, const unsigned short id)
{
    if( payload.size() > MaxOutboundPayload )
    {
        throw PayloadTooLarge();
    }

    scoped_pdu_ptr pdu(coap_pdu_init(COAP_MESSAGE_CON, COAP_REQUEST_GET, id, COAP_MAX_PDU_SIZE));

    //coap_add_token(pdu, len, data);

    coap_add_data(pdu, payload.size(), &payload.front());

    const unsigned char *raw_pdu = reinterpret_cast<unsigned char *>(pdu->hdr);

    return std::vector<unsigned char>(raw_pdu, raw_pdu + pdu->length);
}


std::vector<unsigned char> E2eDataTransferProtocol::sendBlockContinuation(const BlockInfo &bi, const unsigned short id)
{
    scoped_pdu_ptr continuation_pdu = coap_pdu_init(COAP_MESSAGE_CON, COAP_REQUEST_GET, id, COAP_MAX_PDU_SIZE);

    unsigned char buf[4];

    unsigned len = coap_encode_var_bytes(buf, (bi.num << 4) | bi.size);

    coap_add_option(continuation_pdu, COAP_OPTION_BLOCK2, len, buf);

    const unsigned char *raw_pdu = reinterpret_cast<unsigned char *>(continuation_pdu->hdr);

    return std::vector<unsigned char>(raw_pdu, raw_pdu + continuation_pdu->length);
}


namespace {

const std::map<unsigned int, E2eDataTransferProtocol::EndpointResponse::MessageType> MessageTypes = boost::assign::map_list_of
        (COAP_MESSAGE_NON, E2eDataTransferProtocol::EndpointResponse::Nonconfirmable)
        (COAP_MESSAGE_CON, E2eDataTransferProtocol::EndpointResponse::Confirmable)
        (COAP_MESSAGE_ACK, E2eDataTransferProtocol::EndpointResponse::Ack)
        (COAP_MESSAGE_RST, E2eDataTransferProtocol::EndpointResponse::Reset);
}


E2eDataTransferProtocol::EndpointResponse E2eDataTransferProtocol::handleIndication(const std::vector<unsigned char> &payload)
{
    EndpointResponse er;

    if( payload.size() > MaxInboundPayload )
    {
        throw PayloadTooLarge();
    }

    scoped_pdu_ptr indication_pdu(coap_pdu_init(COAP_MESSAGE_NON, COAP_REQUEST_GET, COAP_INVALID_TID, COAP_MAX_PDU_SIZE));

    std::vector<unsigned char> mutable_payload(payload);

    coap_pdu_parse(&mutable_payload.front(), mutable_payload.size(), indication_pdu);

    coap_block_t block;

    unsigned char *data;
    size_t len;

    coap_get_data(indication_pdu, &len, &data);

    if( const boost::optional<EndpointResponse::MessageType> mt = mapFind(MessageTypes, indication_pdu->hdr->type) )
    {
        er.type = *mt;
    }
    else
    {
        er.type = EndpointResponse::Unknown;
    }

    er.id = indication_pdu->hdr->id;
    er.data.assign(data, data + len);

    if( coap_get_block(indication_pdu, COAP_OPTION_BLOCK2, &block) )
    {
        if( block.m )
        {
            BlockInfo bi;

            bi.size = block.szx;
            bi.num  = block.num + 1;

            er.blockContinuation = bi;
        }
    }

    return er;
}


}
}


