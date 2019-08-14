#pragma once

extern "C" {
#include "coap/pdu.h"
}

namespace Cti::Protocols::Coap {

class scoped_pdu_ptr
{
    coap_pdu_t *pdu;
public:
    scoped_pdu_ptr(coap_pdu_t *pdu_) : pdu(pdu_) {}

    ~scoped_pdu_ptr() { coap_delete_pdu(pdu); }

    operator coap_pdu_t *()  const { return pdu; }
    coap_pdu_t *operator->() const { return pdu; }
};

enum class ResponseCode {
    EmptyMessage  = COAP_RESPONSE_CODE(  0),
    Content       = COAP_RESPONSE_CODE(205),
    BadRequest    = COAP_RESPONSE_CODE(400),
    NotAcceptable = COAP_RESPONSE_CODE(406),
};

}