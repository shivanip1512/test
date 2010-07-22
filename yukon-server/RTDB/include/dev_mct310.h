#pragma once

#include "dev_mct.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct310Device : public MctDevice
{
private:

    typedef MctDevice Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

protected:

    virtual bool getOperation( const UINT &cmd,  BSTRUCT &bst ) const;

    enum Memory
    {
        Memory_DemandPos = 0x2c,
        Memory_DemandLen =    2,

        Memory_StatusPos = 0x05,
        Memory_StatusLen =    2,

        MCT3XX_PutMRead1Pos            = 0x20,
        MCT3XX_PutMRead2Pos            = 0x32,
        MCT3XX_PutMRead3Pos            = 0x51,
        MCT3XX_PutMReadLen             =    6,

        MCT3XX_Mult1Pos                = 0x26,
        MCT3XX_Mult2Pos                = 0x38,
        MCT3XX_Mult3Pos                = 0x57,
        MCT3XX_MultLen                 =    2,

        MCT3XX_PFCountPos              = 0x07,
        MCT3XX_PFCountLen              =    2,

        MCT3XX_ResetPos                = 0x06,
        MCT3XX_ResetLen                =    1,

        MCT3XX_TimePos                 = 0x7A,
        MCT3XX_TimeLen                 =    5,

        MCT3XX_LPStatusPos             = 0x70,
        MCT3XX_LPStatusLen             =    9,

        MCT3XX_DemandIntervalPos       = 0x1B,
        MCT3XX_DemandIntervalLen       =    1,
        MCT3XX_LPIntervalPos           = 0x76,
        MCT3XX_LPIntervalLen           =    1,

        MCT3XX_OptionPos               = 0x02,
        MCT3XX_OptionLen               =    6,
        MCT3XX_GenStatPos              = 0x05,
        MCT3XX_GenStatLen              =    9,

        MCT3XX_MinMaxPeakConfigPos     = 0x03,
    };

    enum Functions
    {
        FuncRead_MReadPos        = 0x90,
        FuncRead_MReadLen        =    9,
        FuncRead_FrozenPos       = 0x91,
        FuncRead_FrozenLen       =    9,
    };

    enum Commands
    {
        Command_PeakOff          = 0x59,
        Command_PeakOn           = 0x5a,
    };

    enum
    {
        MCT3XX_GroupAddressPos           = 0x10,
        MCT3XX_GroupAddressLen           =    5,

        MCT3XX_UniqueAddressPos          = 0x0a,
        MCT3XX_UniqueAddressLen          =    6,
        MCT3XX_GroupAddressBronzePos     = 0x10,
        MCT3XX_GroupAddressBronzeLen     =    1,
        MCT3XX_GroupAddressLeadPos       = 0x11,
        MCT3XX_GroupAddressLeadLen       =    3,
        MCT3XX_GroupAddressGoldSilverPos = 0x14,
        MCT3XX_GroupAddressGoldSilverLen =    1,

        MCT310_Rollover                  =   100000,   //  5 digits
        MCT310_MaxPulseCount             = 10000000,
    };

    static  DOUBLE translateStatusValue( INT PointOffset, INT PointType, INT DeviceType, PBYTE DataValueArray );

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodeGetValueKWH         ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueDemand      ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValuePeak        ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusInternal   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusLoadProfile( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigModel      ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigOptions    ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeScanLoadProfile     ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodePutConfigPeakMode   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    //  virtual so that the MCT318 can override them
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    void decodeAccumulators( ULONG *result, INT accum_cnt, BYTE *Data );
    void decodeStati( INT &stat, INT which, BYTE *Data );

public:

    enum
    {
        MCT310_StatusConnected         = 0x00,
        MCT310_StatusConnectArmed      = 0x40,
        MCT310_StatusConnectInProgress = 0x80,
        MCT310_StatusDisconnected      = 0xc0
    };

    Mct310Device( );
    Mct310Device( const Mct310Device &aRef );
    virtual ~Mct310Device( );

    Mct310Device &operator=( const Mct310Device &aRef );

    //  virtual so that the MCT318 can override it
    virtual ULONG calcNextLPScanTime( void );
};

}
}

