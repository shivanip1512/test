#pragma once

#include "dev_mct22x.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Lmt2Device : public Mct22xDevice
{
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

    YukonError_t ModelDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    YukonError_t decodeScanLoadProfile     (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t decodeAccumScan           (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t decodeDemandScan          (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t decodeGetValueDefault     (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t decodeGetStatusLoadProfile(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t decodeGetStatusInternal   (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    YukonError_t decodeGetConfigModel      (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    void calcAndInsertLPRequests( OUTMESS *&OutMessage, OutMessageList &outList ) override;
    bool calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage ) override;

public:

    Lmt2Device();

    ULONG calcNextLPScanTime( void ) override;
};

}
}
