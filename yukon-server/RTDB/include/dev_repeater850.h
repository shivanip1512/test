
#pragma once

#include "dev_repeater800.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Repeater850Device : public Repeater800Device
{
private:

   typedef Repeater800Device Inherited;

   static const CommandSet _commandStore;
   static CommandSet initCommandStore();

protected:

    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    enum
    {
        Rpt850_SSPecPos = 0x11,
        Rpt850_SSPecLen =    6
    };

    enum
    {
        SspecRev_BetaLo = 9,
        SspecRev_BetaHi = 200
    };

    virtual INT ResultDecode(INMESS*InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    INT decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
};

}
}

