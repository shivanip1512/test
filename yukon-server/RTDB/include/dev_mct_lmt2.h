#pragma once

#include "dev_mct22x.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Lmt2Device : public Mct22xDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Lmt2Device(const Lmt2Device&);
    Lmt2Device& operator=(const Lmt2Device&);

    typedef Mct22xDevice Inherited;

   static const CommandSet _commandStore;
   static CommandSet initCommandStore();

   CtiTime _lastLPRequestAttempt,
           _lastLPRequestBlockStart;

protected:

    virtual bool getOperation( const UINT &cmd, BSTRUCT &bst ) const;

    enum
    {
        MCT_LMT2_LPStatusPos   = 0x95,
        MCT_LMT2_LPStatusLen   =    5,

        MCT_LMT2_LPIntervalPos = 0x97,
        MCT_LMT2_LPIntervalLen =    1,

        MCT_LMT2_ResetOverrideFunc = 0x57
    };

    virtual INT ModelDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    INT decodeScanLoadProfile     ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeAccumScan(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    INT decodeDemandScan(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    INT decodeGetValueDefault(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    INT decodeGetStatusLoadProfile( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetStatusInternal( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    INT decodeGetConfigModel(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    INT calcAndInsertLPRequests( OUTMESS *&OutMessage, OutMessageList &outList );
    virtual bool calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

public:

    Lmt2Device();

    ULONG calcNextLPScanTime( void );
};

}
}
