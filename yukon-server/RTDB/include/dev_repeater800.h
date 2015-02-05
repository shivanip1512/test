#pragma once

#include "dev_repeater.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Repeater800Device : public Repeater900Device
{
   typedef Repeater900Device Inherited;

   static const CommandSet _commandStore;
   static CommandSet initCommandStore();

protected:

    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    enum
    {
        Rpt800_PFCountPos = 0x36,
        Rpt800_PFCountLen =    3
    };

    YukonError_t ResultDecode (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    YukonError_t decodeGetValuePFCount(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

public:

   Repeater800Device();
};

}
}

