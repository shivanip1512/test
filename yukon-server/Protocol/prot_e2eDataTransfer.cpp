#include "precompiled.h"

#include "prot_e2eDataTransfer.h"

extern "C" {
#include "coap/pdu.h"
#include "coap/block.h"
}

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

E2eDataTransferProtocol::SendInfo E2eDataTransferProtocol::sendRequest(const std::vector<unsigned char> &payload, const unsigned short id)
{
    SendInfo si;

    if( payload.size() > MaxOutboundPayload )
    {
        //  throw E2eDataTransferPayloadTooLarge();
        si.status = -1;  //PayloadTooLarge;

        return si;
    }

    scoped_pdu_ptr pdu(coap_pdu_init(COAP_MESSAGE_CON, COAP_REQUEST_GET, id, COAP_MAX_PDU_SIZE));

    coap_add_data(pdu, payload.size(), &payload.front());

    const unsigned char *raw_pdu = reinterpret_cast<unsigned char *>(pdu->hdr);

    si.message.assign(raw_pdu, raw_pdu + pdu->length);

    return si;
}

E2eDataTransferProtocol::EndpointResponse E2eDataTransferProtocol::handleIndication(const std::vector<unsigned char> &payload)
{
    EndpointResponse er;

    if( payload.size() > 1000 ) // MaxInboundPayload )
    {
        //  throw E2eDataTransferPayloadTooLarge();
        er.status = -1;  //PayloadTooLarge;

        return er;
    }

    scoped_pdu_ptr pdu(coap_pdu_init(COAP_MESSAGE_NON, COAP_REQUEST_GET, COAP_INVALID_TID, COAP_MAX_PDU_SIZE));

    std::copy(payload.begin(), payload.end(), reinterpret_cast<unsigned char *>(pdu->hdr));

    coap_block_t block;

    unsigned char *data;
    size_t len;

    coap_get_data(pdu, &len, &data);

    er.data.assign(data, data + len);

    if( coap_get_block(pdu, COAP_OPTION_BLOCK2, &block) )
    {
        if( block.m )
        {
            scoped_pdu_ptr continuation_pdu = coap_pdu_init(COAP_MESSAGE_CON, COAP_REQUEST_GET, pdu->hdr->id, COAP_MAX_PDU_SIZE);

            block.num++;

            unsigned char buf[4];

            unsigned len = coap_encode_var_bytes(buf, (block.num << 4) | block.szx);

            coap_add_option(continuation_pdu, COAP_OPTION_BLOCK2, len, buf);

            const unsigned char *raw_pdu = reinterpret_cast<unsigned char *>(continuation_pdu->hdr);

            er.blockContinuation = std::vector<unsigned char>(raw_pdu, raw_pdu + continuation_pdu->length);
        }
    }

    return er;
}


}
}


