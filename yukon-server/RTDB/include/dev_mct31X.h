#pragma warning( disable : 4786)
#ifndef __DEV_MCT31X_H__
#define __DEV_MCT31X_H__

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:52 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_mct310.h"
#include "tbl_dv_mctiedport.h"

class IM_EX_DEVDB CtiDeviceMCT31X : public CtiDeviceMCT310
{
protected:

    static CTICMDSET _commandStore;

    enum
    {
        MCT31X_FuncReadDemandAddr = 0x92,
        MCT31X_FuncReadDemandLen  =    7,
        MCT31X_FuncReadStatusLen  =    1,

        //  get status and 3 accumulators
        MCT31X_FuncReadAccumAddr  = 0x95,
        MCT31X_FuncReadAccumLen   =   10,

        //  these addresses are only valid for the 360 and 370
        MCT360_IEDKwhAddr         = 0xa2,
        MCT360_IEDKvahAddr        = 0xa7,
        MCT360_IEDKvarhAddr       = 0xa7,
        MCT360_IEDDemandAddr      = 0xa1,
        MCT360_IEDReqLen          =   13,

        MCT360_IEDTimeAddr        = 0xaa,
        MCT360_IEDTimeLen         =    6,

        MCT360_IEDScanAddr        = 0x74,
        MCT360_IEDScanLen         =    6,

        MCT360_IEDClassAddr       = 0x76,
        MCT360_IEDClassLen        =    4,

        MCT360_IEDResetAddr       = 0xb0,
        MCT360_IEDResetLen        =    2
    };

private:

    RWTime _lastLPRequestAttempt[3], _lastLPRequestBlockStart[3], _lastLPTime[3], _nextLPTime[3];
    CtiTableDeviceMCTIEDPort _iedPort;

public:

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

    virtual void DecodeDatabaseReader( RWDBReader &rdr );

    virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

    INT decodeGetConfigIED   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueIED    ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueKWH    ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueDemand ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeStatus         ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeScanLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT31X_H__
