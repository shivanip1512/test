#pragma once

#include "dev_mct2xx.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct22xDevice : public Mct2xxDevice
{
private:

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

    virtual INT ModelDecode(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    int decodeGetValueDemand(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

public:

    Mct22xDevice();
    Mct22xDevice(const Mct22xDevice& aRef);

    virtual ~Mct22xDevice();

    Mct22xDevice& operator=(const Mct22xDevice& aRef);
};

}
}

