#pragma once

#include "cmdparse.h"
#include "dlldefs.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"

class IM_EX_PROT CtiProtocolSA205 : public Cti::Protocols::Interface
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiProtocolSA205(const CtiProtocolSA205&);
    CtiProtocolSA205& operator=(const CtiProtocolSA205&);

public:

    CtiProtocolSA205();
    virtual ~CtiProtocolSA205();
};
