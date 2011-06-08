#pragma once

#include "dev_mct.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct2xxDevice : public MctDevice
{
private:

    typedef MctDevice Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    CtiTime _lastLPRequestAttempt,
            _lastLPRequestBlockStart;

protected:

    virtual bool getOperation( const UINT &cmd,  BSTRUCT &bst ) const;

    enum
    {
        MCT2XX_PFCountPos = 0x20,
        MCT2XX_PFCountLen =    2,
        Memory_TimePos    = 0x46,
        Memory_TimeLen    =    3,

        MCT2XX_MultPos    = 0x90,
        MCT2XX_MultLen    =    2,

        MCT2XX_GenStatPos = 0x3D,
        MCT2XX_GenStatLen =    9,
        MCT2XX_OptionPos  = 0x2F,
        MCT2XX_OptionLen  =    1,

        MCT2XX_ResetPos   = 0x43,
        MCT2XX_ResetLen   =    3
    };

    enum
    {
        //  these addresses are not valid for the 210 series meters, but that
        //    doesn't matter because these are never added in the 210 initCommandStore

        MCT2XX_GroupAddressPos = 0x28,
        MCT2XX_GroupAddressLen =    5,

        MCT2XX_GroupAddressBronzePos      = 0x28,
        MCT2XX_GroupAddressBronzeLen      =    1,
        MCT2XX_GroupAddressLeadPos        = 0x29,
        MCT2XX_GroupAddressLeadLen        =    3,
        MCT2XX_GroupAddressGoldSilverPos  = 0x2c,
        MCT2XX_GroupAddressGoldSilverLen  =    1,
        MCT2XX_UniqueAddressPos           = 0x22,
        MCT2XX_UniqueAddressLen           =    6
    };

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );

    INT decodeGetValueKWH      ( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    INT decodeGetValueDemand   ( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    INT decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    INT decodeGetConfigModel   ( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    INT decodeGetConfigOptions ( INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );

public:

   Mct2xxDevice( );
   Mct2xxDevice( const Mct2xxDevice &aRef );
   virtual ~Mct2xxDevice( );

   Mct2xxDevice &operator=( const Mct2xxDevice &aRef );

};

}
}

