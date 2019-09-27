#include "precompiled.h"

#include "coap_helper.h"

extern "C" {
#include "coap/block.h"
}

#include <vector>

namespace Cti::Protocols::Coap {

scoped_pdu_ptr::scoped_pdu_ptr(coap_pdu_t* pdu_) 
    : pdu { pdu_ } 
{
}

scoped_pdu_ptr::scoped_pdu_ptr(scoped_pdu_ptr&& other)
{
    *this = std::move(other);
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

void scoped_pdu_ptr::add_token(unsigned long token)
{
    unsigned char token_buf[4];

    const unsigned token_len = coap_encode_var_bytes(token_buf, token);

    coap_add_token(pdu, token_len, token_buf);
}

scoped_pdu_ptr scoped_pdu_ptr::make_get_request(unsigned long token, unsigned short id)
{
    scoped_pdu_ptr pdu { coap_pdu_init(COAP_MESSAGE_CON, static_cast<unsigned char>(RequestMethod::Get), id, COAP_MAX_PDU_SIZE) };

    pdu.add_token(token);

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_get_continuation(unsigned long token, unsigned short id)
{
    scoped_pdu_ptr pdu { coap_pdu_init(COAP_MESSAGE_CON, static_cast<unsigned char>(RequestMethod::Get), id, COAP_MAX_PDU_SIZE) };

    pdu.add_token(token);

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_post(unsigned long token, unsigned short id)
{
    scoped_pdu_ptr pdu { coap_pdu_init(COAP_MESSAGE_NON, static_cast<unsigned char>(RequestMethod::Post), id, COAP_MAX_PDU_SIZE) };

    pdu.add_token(token);

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_ack(unsigned short id, ResponseCode status)
{
    scoped_pdu_ptr pdu { coap_pdu_init(COAP_MESSAGE_ACK, static_cast<unsigned char>(status), id, COAP_MAX_PDU_SIZE) };

    return pdu;
}

scoped_pdu_ptr scoped_pdu_ptr::make_ack_with_data(unsigned long token, unsigned short id, std::vector<unsigned char> data)
{
    scoped_pdu_ptr pdu(coap_pdu_init(COAP_MESSAGE_ACK, static_cast<unsigned char>(ResponseCode::Content), id, COAP_MAX_PDU_SIZE));

    pdu.add_token(token);

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