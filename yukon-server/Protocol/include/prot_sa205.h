#pragma once

#include "cmdparse.h"
#include "dlldefs.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"

class IM_EX_PROT CtiProtocolSA205 : public Cti::Protocol::Interface
{
public:

    CtiProtocolSA205();
    CtiProtocolSA205( const CtiProtocolSA205& aRef );
    virtual ~CtiProtocolSA205();
};
