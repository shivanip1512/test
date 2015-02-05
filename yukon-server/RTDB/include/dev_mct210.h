#pragma once

#include "dev_mct2xx.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct210Device : public Mct2xxDevice
{
    typedef Mct2xxDevice Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

protected:

    virtual bool getOperation( const UINT &cmd,  BSTRUCT &bst ) const;

    enum
    {
        MCT210_MReadPos    = 0x12,
        MCT210_MReadLen    =    3,  // 24 bit most recent (MREAD) copied from CUREAD value on interval boundary
        MCT210_DemandPos   = 0x0a,
        MCT210_DemandLen   =    2,
        MCT210_AccumPos    = 0x0f,
        MCT210_AccumLen    =    6,
        MCT210_StatusPos   = 0x36,
        MCT210_StatusLen   =    2,
        MCT210_PutMReadPos = 0x0f,
        MCT210_PutMReadLen =    9,

        MCT210_MultPos     = 0x19,
        MCT210_MultLen     =    2,

        MCT210_GenStatPos  = 0x30,
        MCT210_GenStatLen  =    9,

        MCT210_ResetPos    = 0x36,
        MCT210_ResetLen    =    3
    };

    YukonError_t ModelDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

public:

    enum
    {
        MCT210_StatusConnected    = 0x80,
        MCT210_StatusDisconnected = 0x40
    };

    Mct210Device();
};

}
}

