#pragma once

#include "dev_mct24x.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Dct501Device : public Mct24xDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Dct501Device(const Dct501Device&);
    Dct501Device& operator=(const Dct501Device&);

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

    bool getOperation( const UINT &cmd,  BSTRUCT &bst ) const override;

    INT ModelDecode  ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    INT decodeGetValueDemand ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigModel ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeScanLoadProfile( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

public:

   Dct501Device( );

   virtual ULONG calcNextLPScanTime( void );
   virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, OutMessageList &outList );
   virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

};

}
}

