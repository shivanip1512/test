#pragma once

#include "prot_ansi.h"

class IM_EX_PROT  CtiProtocolANSI_focus: public Cti::Protocols::Ansi::CtiProtocolANSI
{
    typedef Cti::Protocols::Ansi::CtiProtocolANSI Inherited;

   public:

        CtiProtocolANSI_focus();
        virtual ~CtiProtocolANSI_focus();

        virtual void setAnsiDeviceType();
        virtual int calculateLPDataBlockStartIndex(ULONG lastLPTime);

   private:


};


