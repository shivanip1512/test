/*-----------------------------------------------------------------------------*
*
* File:   dev_dct501
*
* Class:  CtiDeviceDCT501
* Date:   2002-feb-19
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_dct501.h-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2006/07/06 20:11:48 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_DCT501_H__
#define __DEV_DCT501_H__
#pragma warning( disable : 4786)


#include "dev_mct24x.h"

class IM_EX_DEVDB CtiDeviceDCT501 : public CtiDeviceMCT24X
{
protected:

    enum
    {
        DCT_AnalogsPos = 0x67,
        DCT_AnalogsLen =    8,

        DCT_LPChannels =    4
    };

private:

    static const CommandSet _commandStore;
    static CommandSet initCommandStore( );

    CtiTime  _lastLPRequest[DCT_LPChannels],
             _lastLPTime   [DCT_LPChannels],
             _nextLPTime   [DCT_LPChannels];

protected:

    typedef CtiDeviceMCT24X Inherited;

    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodeGetValueDemand( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigModel( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeScanLoadProfile( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

public:

   CtiDeviceDCT501( );
   CtiDeviceDCT501( const CtiDeviceDCT501 &aRef );
   virtual ~CtiDeviceDCT501( );

   CtiDeviceDCT501 &operator=( const CtiDeviceDCT501 &aRef );

   virtual ULONG calcNextLPScanTime( void );
   virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
   virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

};
#endif // #ifndef __DEV_DCT501_H__
