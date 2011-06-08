#pragma once

#include "dev_repeater.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Repeater800Device : public Repeater900Device
{
private:

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

    virtual INT ResultDecode(INMESS*InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    INT decodeGetValuePFCount(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

public:

   Repeater800Device();
   Repeater800Device(const Repeater800Device& aRef);

   virtual ~Repeater800Device();

   Repeater800Device& operator=(const Repeater800Device& aRef);
};

}
}

