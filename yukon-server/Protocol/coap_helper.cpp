#include "precompiled.h"

#include "coap_helper.h"
#include "std_helper.h"

extern "C" {
#include "coap/block.h"
}

#include <vector>

namespace Cti::Protocols::Coap {

StreamBufferSink& operator<<(StreamBufferSink& s, const ResponseCode r) {
    return s << "[CoAP error " << static_cast<int>(r) << "]";
}
    
scoped_pdu_ptr::scoped_pdu_ptr(coap_pdu_t* pdu_)
    : pdu { pdu_ } 
{
}

scoped_pdu_ptr::scoped_pdu_ptr(scoped_pdu_ptr&& other)
{
    pdu = other.pdu;
    other.pdu = nullptr;
}

scoped_pdu_ptr& scoped_pdu_ptr::operator=(scoped_pdu_ptr&& other)
{
    free_pdu();

    pdu = other.pdu;
    other.pdu = nullptr;

    return *this;
}

scoped_pdu_ptr::~scoped_pdu_ptr()
{
    free_pdu();
}

void scoped_pdu_ptr::free_pdu()
{
    if( pdu )
    {
        coap_delete_pdu(pdu);

        pdu = nullptr;
    }
}

scoped_pdu_ptr::operator coap_pdu_t*() const
{
    return pdu;
}

coap_pdu_t* scoped_pdu_ptr::operator->() const
{
    return pdu;
}

void scoped_pdu_ptr::add_token(unsigned long token)
{
    unsigned char token_buf[4];

    const unsigned token_len = coap_encode_var_bytes(token_buf, token);

    coap_add_token(pdu, token_len, token_buf);
}

scoped_pdu_ptr scoped_pdu_ptr::make_confirmable_request(RequestMethod method, unsigned long token, unsigned short id)
{
    scoped_pdu_ptr pdu { coap_pdu_init(COAP_MESSAGE_CON, as_underlying(method), id, COAP_MAX_PDU_SIZE) };

    pdu.add_token(token);

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_get_continuation(unsigned long token, unsigned short id, const BlockSize size, const unsigned num)
{
    scoped_pdu_ptr pdu { coap_pdu_init(COAP_MESSAGE_CON, as_underlying(RequestMethod::Get), id, COAP_MAX_PDU_SIZE) };

    pdu.add_token(token);

    unsigned char buf[4];

    unsigned len = coap_encode_var_bytes(buf, (num << 4) | size.getSzx());

    coap_add_option(pdu, COAP_OPTION_BLOCK2, len, buf);

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_nonconfirmable_request(RequestMethod method, unsigned long token, unsigned short id)
{
    scoped_pdu_ptr pdu { coap_pdu_init(COAP_MESSAGE_NON, as_underlying(method), id, COAP_MAX_PDU_SIZE) };

    pdu.add_token(token);

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_nonconfirmable_request(RequestMethod method, unsigned short id)
{
    scoped_pdu_ptr pdu { coap_pdu_init(COAP_MESSAGE_NON, as_underlying(method), id, COAP_MAX_PDU_SIZE) };

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_ack(unsigned short id, ResponseCode status)
{
    scoped_pdu_ptr pdu { coap_pdu_init(COAP_MESSAGE_ACK, as_underlying(status), id, COAP_MAX_PDU_SIZE) };

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_ack(unsigned long token, unsigned short id, ResponseCode status)
{
    scoped_pdu_ptr pdu{ coap_pdu_init(COAP_MESSAGE_ACK, as_underlying(status), id, COAP_MAX_PDU_SIZE) };

    pdu.add_token(token);

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_data_ack(unsigned long token, unsigned short id, std::vector<unsigned char> data)
{
    scoped_pdu_ptr pdu(coap_pdu_init(COAP_MESSAGE_ACK, as_underlying(ResponseCode::Content), id, COAP_MAX_PDU_SIZE));

    pdu.add_token(token);

    coap_add_data(pdu, data.size(), data.data());

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_block_ack(unsigned long token, unsigned short id, std::vector<unsigned char> data, const Block block)
{
    scoped_pdu_ptr pdu(coap_pdu_init(COAP_MESSAGE_ACK, as_underlying(ResponseCode::Content), id, COAP_MAX_PDU_SIZE));

    pdu.add_token(token);

    unsigned char buf[4];

    unsigned len = coap_encode_var_bytes(buf, (block.num << 4) | (block.more << 3) | block.blockSize.getSzx());

    coap_add_option(pdu, COAP_OPTION_BLOCK2, len, buf);

    coap_add_data(pdu, data.size(), data.data());

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::parse(std::vector<unsigned char> packet)
{
    //  defaults, will be overwritten by coap_pdu_parse() below
    scoped_pdu_ptr pdu(coap_pdu_init(COAP_MESSAGE_NON, COAP_REQUEST_GET, COAP_INVALID_TID, COAP_MAX_PDU_SIZE));

    coap_pdu_parse(packet.data(), packet.size(), pdu);

    return pdu;
}

std::vector<unsigned char> scoped_pdu_ptr::as_bytes() const
{
    const unsigned char *raw_pdu = reinterpret_cast<unsigned char *>(pdu->hdr);

    return std::vector<unsigned char>(raw_pdu, raw_pdu + pdu->length);
}

}