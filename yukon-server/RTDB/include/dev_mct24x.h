/*-----------------------------------------------------------------------------*
*
* File:   dev_mct2XX
*
* Class:  CtiDeviceMCT2XX
* Date:   5/3/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct2xx.h-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2004/12/07 18:16:10 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT24X_H__
#define __DEV_MCT24X_H__
#pragma warning( disable : 4786)


#include "dev_mct2xx.h"

class IM_EX_DEVDB CtiDeviceMCT24X : public CtiDeviceMCT2XX
{
protected:

    enum
    {
        MCT24X_MReadPos          = 0x89,
        MCT24X_MReadLen          =    3,  //  24 bit most recent (MREAD) copied from CUREAD value on interval boundary
        MCT24X_PutMReadPos       = 0x86,
        MCT24X_PutMReadLen       =    9,
        MCT24X_DiscPos           = 0x37,
        MCT24X_DiscLen           =    3,  //  Gets last latch cmnd recv, reserved byte, LCIMAG
        MCT24X_DemandPos         = 0x56,
        MCT24X_DemandLen         =    2,
        MCT24X_LPStatusPos       = 0x95,
        MCT24X_LPStatusLen       =    5,

        MCT24X_DemandIntervalPos = 0x58,
        MCT24X_DemandIntervalLen =    1,
        MCT24X_LPIntervalPos     = 0x97,
        MCT24X_LPIntervalLen     =    1,

        MCT24X_StatusPos         = 0x37,  //  just get disconnect status
        MCT24X_StatusLen         =    1,

        MCT250_StatusPos         = 0x43,  //  get the 4 status inputs
        MCT250_StatusLen         =    3,

        MCT24X_Status_Open       = 0x41,
        MCT24X_Status_Closed     = 0x42
    };

private:

    static DLCCommandSet _commandStore;
    RWTime _lastLPRequestAttempt, _lastLPRequestBlockStart;

public:

    typedef CtiDeviceMCT2XX Inherited;

    CtiDeviceMCT24X( );
    CtiDeviceMCT24X( const CtiDeviceMCT24X &aRef );
    virtual ~CtiDeviceMCT24X( );

    CtiDeviceMCT24X &operator=( const CtiDeviceMCT24X &aRef );

    static  bool initCommandStore( );
    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    ULONG calcNextLPScanTime( void );
    INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

    INT decodeScanLoadProfile     ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeScanStatus          ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigModel      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT24X_H__
