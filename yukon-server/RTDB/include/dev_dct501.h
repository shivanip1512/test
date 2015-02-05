#pragma once

#include "dev_mct24x.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Dct501Device : public Mct24xDevice
{
    enum
    {
        DCT_AnalogsPos = 0x67,
        DCT_AnalogsLen =    8,

        DCT_LPChannels =    4
    };

    typedef Mct24xDevice Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore( );

    CtiTime  _lastLPRequest[DCT_LPChannels],
             _lastLPTime   [DCT_LPChannels],
             _nextLPTime   [DCT_LPChannels];

protected:

    bool getOperation( const UINT &cmd,  BSTRUCT &bst ) const override;

    YukonError_t ModelDecode  ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    YukonError_t decodeGetValueDemand ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigModel ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeScanLoadProfile( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

public:

   Dct501Device( );

   virtual ULONG calcNextLPScanTime( void );
   virtual void  calcAndInsertLPRequests( OUTMESS *&OutMessage, OutMessageList &outList );
   virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

};

}
}

