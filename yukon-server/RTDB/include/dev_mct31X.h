/*-----------------------------------------------------------------------------*
*
* File:   dev_mct31X
*
* Class:  CtiDeviceMCT31X
* Date:   4/24/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct31X.h-arc  $
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2006/10/04 19:14:19 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT31X_H__
#define __DEV_MCT31X_H__
#pragma warning( disable : 4786)


#include "dev_mct310.h"
#include "tbl_dv_mctiedport.h"

class IM_EX_DEVDB CtiDeviceMCT31X : public CtiDeviceMCT310
{
public:

    enum
    {
        ChannelCount = 3
    };

private:

    typedef CtiDeviceMCT310 Inherited;

    CtiTime _lastLPTime[ChannelCount],
            _nextLPTime[ChannelCount],
            _lastLPRequest[ChannelCount];

    CtiTableDeviceMCTIEDPort _iedPort;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

protected:

    virtual bool getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io );

    static const double MCT360_GEKV_KWHMultiplier;

    enum
    {
        FuncRead_DemandPos       = 0x92,
        FuncRead_DemandLen       =    7,
        FuncRead_StatusLen       =    1,

        FuncRead_MinMaxDemandPos = 0x93,
        FuncRead_MinMaxDemandLen =   12,

        FuncRead_FrozenDemandPos = 0x94,
        FuncRead_FrozenDemandLen =   12,

        //  these addresses are only valid for the 360 and 370
        MCT360_IEDKwhPos         = 0xa2,
        MCT360_IEDKvahPos        = 0xa7,
        MCT360_IEDKvarhPos       = 0xa7,
        MCT360_IEDDemandPos      = 0xa1,
        MCT360_IEDReqLen         =   13,
        MCT360_IEDLinkPos        = 0xa0,
        MCT360_IEDLinkLen        =    5,

        MCT360_IEDTimePos        = 0xaa,
        MCT360_IEDTimeLen        =   11,

        MCT360_IEDScanPos        = 0x74,
        MCT360_IEDScanLen        =    8,

        MCT360_IEDClassPos       = 0x76,
        MCT360_IEDClassLen       =    4
    };

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodeGetConfigIED   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusIED   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueIED    ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueKWH    ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueDemand ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValuePeak   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeStatus         ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool expectMore = false );
    INT decodeScanLoadProfile( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

public:

    enum
    {
        //  for dev_mctbroadcast
        MCT360_AlphaResetPos = 0xb0,
        MCT360_AlphaResetLen =    2,
        MCT360_LGS4ResetPos  = 0xc0,
        MCT360_LGS4ResetLen  =    3,
        MCT360_GEKVResetPos  = 0xc1,
        MCT360_GEKVResetLen  =    6,

        MCT360_LGS4ResetID   =    3,
        MCT360_GEKVResetID   =    4,

        //  for dev_mct errordecode
        MCT360_IED_VoltsPhaseA_PointOffset = 41,
        MCT360_IED_VoltsPhaseB_PointOffset = 42,
        MCT360_IED_VoltsPhaseC_PointOffset = 43,
        MCT360_IED_VoltsNeutralCurrent_PointOffset = 44
    };

    CtiDeviceMCT31X( );
    CtiDeviceMCT31X( const CtiDeviceMCT31X &aRef );

    virtual ~CtiDeviceMCT31X( );

    CtiDeviceMCT31X &operator=( const CtiDeviceMCT31X &aRef );

    CtiTableDeviceMCTIEDPort &getIEDPort( void );

    ULONG calcNextLPScanTime( void );
    INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    virtual bool calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    virtual void DecodeDatabaseReader( RWDBReader &rdr );
};
#endif // #ifndef __DEV_MCT31X_H__
