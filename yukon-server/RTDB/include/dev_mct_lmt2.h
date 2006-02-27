/*-----------------------------------------------------------------------------*
*
* File:   dev_mct_lmt2
*
* Class:  CtiDeviceMCT_LMT2
* Date:   6/19/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct_lmt2.h-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2006/02/27 23:58:32 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT_LMT2_H__
#define __DEV_MCT_LMT2_H__
#pragma warning( disable : 4786)


#include "dev_mct22x.h"

class CtiDeviceMCT_LMT2 : public CtiDeviceMCT22X
{
protected:

    enum
    {
        MCT_LMT2_LPStatusPos   = 0x95,
        MCT_LMT2_LPStatusLen   =    5,

        MCT_LMT2_LPIntervalPos = 0x97,
        MCT_LMT2_LPIntervalLen =    1,

        MCT_LMT2_ResetOverrideFunc = 0x57
    };

private:

   static DLCCommandSet _commandStore;
   CtiTime _lastLPRequestAttempt, _lastLPRequestBlockStart;

public:

   typedef CtiDeviceMCT22X Inherited;

    CtiDeviceMCT_LMT2();
    CtiDeviceMCT_LMT2( const CtiDeviceMCT_LMT2 &aRef );
    virtual ~CtiDeviceMCT_LMT2();

    CtiDeviceMCT_LMT2& operator=(const CtiDeviceMCT_LMT2& aRef);

    static  bool initCommandStore( );
    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    ULONG calcNextLPScanTime( void );
    INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodeScanLoadProfile     ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeAccumScan(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT decodeDemandScan(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT decodeGetValueDefault(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT decodeGetStatusLoadProfile( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
};
#endif // #ifndef __DEV_MCT_LMT2_H__
