#pragma once

#include "cmdparse.h"
#include "dlldefs.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"

class IM_EX_PROT CtiProtocolSA105 : public Cti::Protocols::Interface
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiProtocolSA105(const CtiProtocolSA105&);
    CtiProtocolSA105& operator=(const CtiProtocolSA105&);

public:

    CtiProtocolSA105();
    virtual ~CtiProtocolSA105();
};
