#pragma once

#include "dlldefs.h"

extern "C" {
#include "coap/pdu.h"
}

namespace Cti::Protocols::Coap {

enum class ResponseCode;

class IM_EX_PROT scoped_pdu_ptr
{
    coap_pdu_t *pdu;

    scoped_pdu_ptr(coap_pdu_t *pdu_);

    void free_pdu();

    void add_token(unsigned long token);

public:
    ~scoped_pdu_ptr();

    //  move operations due to owning a pointer
    scoped_pdu_ptr(scoped_pdu_ptr&& other);
    scoped_pdu_ptr& operator=(scoped_pdu_ptr&& other);

    //  do not allow copy construction or assignment
    scoped_pdu_ptr(const scoped_pdu_ptr& other) = delete;
    scoped_pdu_ptr& operator=(const scoped_pdu_ptr&) = delete;

    static scoped_pdu_ptr make_get_request(unsigned long token, unsigned short id);
    static scoped_pdu_ptr make_get_continuation(unsigned long token, unsigned short id);
    static scoped_pdu_ptr make_post(unsigned long token, unsigned short id);
    static scoped_pdu_ptr make_ack(unsigned short id, ResponseCode status);
    static scoped_pdu_ptr make_ack_with_data(unsigned long token, unsigned short id, std::vector<unsigned char> data);
    static scoped_pdu_ptr parse(std::vector<unsigned char> packet);

    std::vector<unsigned char> as_bytes() const;

    operator coap_pdu_t *()  const;
    coap_pdu_t *operator->() const;
};

enum class RequestMethod {
    Get    = COAP_REQUEST_GET,
    Post   = COAP_REQUEST_POST,
    Put    = COAP_REQUEST_PUT,
    Delete = COAP_REQUEST_DELETE
};

enum class ResponseCode {
    EmptyMessage  = COAP_RESPONSE_CODE(  0),
    Content       = COAP_RESPONSE_CODE(205),
    BadRequest    = COAP_RESPONSE_CODE(400),
    NotAcceptable = COAP_RESPONSE_CODE(406),
};

}