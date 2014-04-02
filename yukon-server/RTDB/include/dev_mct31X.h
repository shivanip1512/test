#pragma once

#include "dev_mct310.h"
#include "tbl_dv_mctiedport.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct31xDevice : public Mct310Device
{
public:

    enum
    {
        ChannelCount = 3
    };

private:

    typedef Mct310Device Inherited;

    CtiTime _lastLPTime[ChannelCount],
            _nextLPTime[ChannelCount],
            _lastLPRequest[ChannelCount];

    CtiTableDeviceMCTIEDPort _iedPort;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    struct IedResetCommand
    {
        unsigned char function;
        std::vector<unsigned char> payload;

        //  We can remove this when we have C++11's initializer list syntax
        IedResetCommand( unsigned char function_, std::vector<unsigned char> payload_ ) :
            function(function_),
            payload(payload_)
        {
        }
    };

    typedef std::map<int, IedResetCommand> IedTypesToCommands;

    static const IedTypesToCommands ResetCommandsByIedType;
    static IedTypesToCommands initIedResetCommands();

    enum
    {
        FuncRead_DemandPos       = 0x92,
        FuncRead_DemandLen       =    7,
        FuncRead_StatusLen       =    1,

        FuncRead_MinMaxDemandPos = 0x93,
        FuncRead_MinMaxDemandLen =   12,

        FuncRead_FrozenDemandPos = 0x94,
        FuncRead_FrozenDemandLen =   12,

        MCT360_LGS4ResetID   =    3,
        MCT360_GEKVResetID   =    4,

        //  these addresses are only valid for the 360 and 370
        MCT360_AlphaResetPos = 0xb0,
        MCT360_LGS4ResetPos  = 0xc0,
        MCT360_GEKVResetPos  = 0xc1,

        MCT360_IEDKwhPos     = 0xa2,
        MCT360_IEDKvahPos    = 0xa7,
        MCT360_IEDKvarhPos   = 0xa7,
        MCT360_IEDDemandPos  = 0xa1,
        MCT360_IEDReqLen     =   13,
        MCT360_IEDLinkPos    = 0xa0,
        MCT360_IEDLinkLen    =    5,

        MCT360_IEDTimePos    = 0xaa,
        MCT360_IEDTimeLen    =   11,

        MCT360_IEDScanPos    = 0x74,
        MCT360_IEDScanLen    =    8,

        MCT360_IEDClassPos   = 0x76,
        MCT360_IEDClassLen   =    4
    };

protected:

    virtual bool getOperation( const UINT &cmd, BSTRUCT &bst ) const;

    virtual INT executePutValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT ModelDecode( const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    INT decodeGetConfigIED   ( const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetStatusIED   ( const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueIED    ( const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueKWH    ( const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueDemand ( const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValuePeak   ( const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeStatus         ( const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool expectMore = false );
    INT decodeScanLoadProfile( const INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

public:

    enum
    {
        //  for dev_mct errordecode
        MCT360_IED_VoltsPhaseA_PointOffset = 41,
        MCT360_IED_VoltsPhaseB_PointOffset = 42,
        MCT360_IED_VoltsPhaseC_PointOffset = 43,
        MCT360_IED_VoltsNeutralCurrent_PointOffset = 44
    };

    Mct31xDevice( );
    Mct31xDevice( const Mct31xDevice &aRef );

    virtual ~Mct31xDevice( );

    Mct31xDevice &operator=( const Mct31xDevice &aRef );

    CtiTableDeviceMCTIEDPort &getIEDPort( void );

    ULONG calcNextLPScanTime( void );
    INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, OutMessageList &outList );
    virtual bool calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

}
}

