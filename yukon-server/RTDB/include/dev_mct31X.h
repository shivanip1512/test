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
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2004/02/11 05:04:35 $
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
protected:

    static DLCCommandSet _commandStore;
    static const double MCT360_GEKV_KWHMultiplier;

private:

    RWTime _lastLPRequestAttempt[3], _lastLPRequestBlockStart[3], _lastLPTime[3], _nextLPTime[3];
    CtiTableDeviceMCTIEDPort _iedPort;

public:

    enum
    {
        MCT31X_FuncReadDemandPos = 0x92,
        MCT31X_FuncReadDemandLen =    7,
        MCT31X_FuncReadStatusLen =    1,

        //  get status and 3 accumulators
        MCT31X_FuncReadAccumPos  = 0x95,
        MCT31X_FuncReadAccumLen  =   10,

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
        MCT360_IEDClassLen       =    4,

        MCT360_AlphaResetPos     = 0xb0,
        MCT360_AlphaResetLen     =    2,
        MCT360_LGS4ResetPos      = 0xc0,
        MCT360_LGS4ResetLen      =    3,
        MCT360_GEKVResetPos      = 0xc1,
        MCT360_GEKVResetLen      =    6,

        MCT360_LGS4ResetID       =    3,
        MCT360_GEKVResetID       =    4,

        MCT360_IED_VoltsPhaseA_PointOffset = 41,
        MCT360_IED_VoltsPhaseB_PointOffset = 42,
        MCT360_IED_VoltsPhaseC_PointOffset = 43,
        MCT360_IED_VoltsNeutralCurrent_PointOffset = 44
    };

    typedef CtiDeviceMCT310 Inherited;

    CtiDeviceMCT31X( );
    CtiDeviceMCT31X( const CtiDeviceMCT31X &aRef );

    virtual ~CtiDeviceMCT31X( );

    CtiDeviceMCT31X &operator=( const CtiDeviceMCT31X &aRef );

    static  bool initCommandStore( );
    virtual bool getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io );

    CtiTableDeviceMCTIEDPort &getIEDPort( void );

    ULONG calcNextLPScanTime( void );
    INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    virtual bool calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    virtual void DecodeDatabaseReader( RWDBReader &rdr );

    virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

    INT decodeGetConfigIED   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetStatusIED   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueIED    ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueKWH    ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueDemand ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValuePeak   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeStatus         ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeScanLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT31X_H__
