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
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct2XX.h-arc  $
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2006/12/27 05:44:38 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT2XX_H__
#define __DEV_MCT2XX_H__
#pragma warning( disable : 4786)


#include "dev_mct.h"

class IM_EX_DEVDB CtiDeviceMCT2XX : public CtiDeviceMCT
{
private:

    typedef CtiDeviceMCT Inherited;

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

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodeGetValueKWH      ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueDemand   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigModel   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigOptions ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

public:

   CtiDeviceMCT2XX( );
   CtiDeviceMCT2XX( const CtiDeviceMCT2XX &aRef );
   virtual ~CtiDeviceMCT2XX( );

   CtiDeviceMCT2XX &operator=( const CtiDeviceMCT2XX &aRef );

};
#endif // #ifndef __DEV_MCT2XX_H__
