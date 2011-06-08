#pragma once

#include "dev_mct22x.h"

namespace Cti {
namespace Devices {

class Lmt2Device : public Mct22xDevice
{
private:

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

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );

    INT decodeScanLoadProfile     ( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    INT decodeAccumScan(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT decodeDemandScan(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT decodeGetValueDefault(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT decodeGetStatusLoadProfile( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    INT decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );

    INT decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    INT calcAndInsertLPRequests( OUTMESS *&OutMessage, std::list< OUTMESS* > &outList );
    virtual bool calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

public:

    Lmt2Device();
    Lmt2Device( const Lmt2Device &aRef );
    virtual ~Lmt2Device();

    Lmt2Device& operator=(const Lmt2Device& aRef);

    ULONG calcNextLPScanTime( void );
};

}
}
