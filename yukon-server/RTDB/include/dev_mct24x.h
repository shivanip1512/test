#pragma once

#include "dev_mct2xx.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct24xDevice : public Mct2xxDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Mct24xDevice(const Mct24xDevice&);
    Mct24xDevice& operator=(const Mct24xDevice&);

    typedef Mct2xxDevice Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    CtiTime _lastLPRequestAttempt,
            _lastLPRequestBlockStart;

protected:

    virtual bool getOperation( const UINT &cmd, BSTRUCT &bst ) const;

    enum
    {
        MCT24X_MReadPos          = 0x89,
        MCT24X_MReadLen          =    3,  //  24 bit most recent (MREAD) copied from CUREAD value on interval boundary
        MCT24X_PutMReadPos       = 0x86,
        MCT24X_PutMReadLen       =    9,
        MCT24X_DiscPos           = 0x37,
        MCT24X_DiscLen           =    3,  //  Gets last latch cmnd recv, reserved byte, LCIMAG
        MCT24X_DemandPos         = 0x56,
        MCT24X_DemandLen         =    2,
        MCT24X_LPStatusPos       = 0x95,
        MCT24X_LPStatusLen       =    5,

        MCT24X_DemandIntervalPos = 0x58,
        MCT24X_DemandIntervalLen =    1,
        MCT24X_LPIntervalPos     = 0x97,
        MCT24X_LPIntervalLen     =    1,

        MCT24X_StatusPos         = 0x37,  //  just get disconnect status
        MCT24X_StatusLen         =    1,

        MCT250_StatusPos         = 0x43,  //  get the 4 status inputs
        MCT250_StatusLen         =    3,

        MCT24X_Status_Open       = 0x41,
        MCT24X_Status_Closed     = 0x42
    };

    INT calcAndInsertLPRequests( OUTMESS *&OutMessage, OutMessageList &outList ) override;
    bool calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage ) override;

    INT ModelDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    INT decodeScanLoadProfile     ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeScanStatus          ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigModel      ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetStatusLoadProfile( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

public:

    Mct24xDevice( );

    ULONG calcNextLPScanTime( void ) override;
};

}
}

