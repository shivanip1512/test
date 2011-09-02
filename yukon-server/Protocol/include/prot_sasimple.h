#pragma once

#include "cmdparse.h"
#include "dlldefs.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"

class IM_EX_PROT CtiProtocolSASimple : public Cti::Protocol::Interface
{
public:
    CtiProtocolSASimple();
    CtiProtocolSASimple( const CtiProtocolSASimple& aRef );
    ~CtiProtocolSASimple();
};
