#pragma once

#include "dev_mct24x.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Dct501Device : public Mct24xDevice
{
protected:

    enum
    {
        DCT_AnalogsPos = 0x67,
        DCT_AnalogsLen =    8,

        DCT_LPChannels =    4
    };

private:

    typedef Mct24xDevice Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore( );

    CtiTime  _lastLPRequest[DCT_LPChannels],
             _lastLPTime   [DCT_LPChannels],
             _nextLPTime   [DCT_LPChannels];

protected:

    virtual bool getOperation( const UINT &cmd,  BSTRUCT &bst ) const;

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );

    INT decodeGetValueDemand( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    INT decodeGetConfigModel( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    INT decodeScanLoadProfile( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );

public:

   Dct501Device( );
   Dct501Device( const Dct501Device &aRef );
   virtual ~Dct501Device( );

   Dct501Device &operator=( const Dct501Device &aRef );

   virtual ULONG calcNextLPScanTime( void );
   virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, std::list< OUTMESS* > &outList );
   virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

};

}
}

