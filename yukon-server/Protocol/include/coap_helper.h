#pragma once

extern "C" {
#include "coap/pdu.h"
#undef E  //  CoAP define that interferes with templates
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

}
}