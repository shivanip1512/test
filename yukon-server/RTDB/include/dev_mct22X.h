#pragma once

#include "dev_mct2xx.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct22xDevice : public Mct2xxDevice
{
    typedef Mct2xxDevice Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

protected:

    virtual bool getOperation( const UINT &cmd,  BSTRUCT &bst ) const;

    enum
    {
        MCT22X_MReadPos    = 0x89,
        MCT22X_MReadLen    =    3,  //  24 bit most recent (MREAD) copied from CUREAD value on interval boundary
        MCT22X_DiscPos     = 0x37,
        MCT22X_DiscLen     =    3,  //  Gets last latch cmnd recv, reserved byte, LCIMAG
        MCT22X_PutMReadPos = 0x86,
        MCT22X_PutMReadLen =    9,
        MCT22X_DemandPos   = 0x86,
        MCT22X_DemandLen   =    6
    };

    YukonError_t ModelDecode (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    YukonError_t decodeGetValueDemand(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

public:

    Mct22xDevice();
};

}
}

