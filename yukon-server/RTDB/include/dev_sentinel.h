
#pragma warning( disable : 4786)
#ifndef __DEV_SENTINEL_H__
#define __DEV_SENTINEL_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_sentinel.h
*
* Class:
* Date:   8/19/2004
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_kv2.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/03/10 19:30:14 $
*

* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_ansi_sentinel.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "mgr_point.h"
#include "device.h"

class IM_EX_DEVDB CtiDeviceSentinel : public CtiDeviceMeter
{

public:

   CtiDeviceSentinel();
   virtual ~CtiDeviceSentinel();

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           RWTPtrSlist< CtiMessage >  &vgList,
                           RWTPtrSlist< CtiMessage >  &retList,
                           RWTPtrSlist< OUTMESS >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);
   virtual INT DemandReset( CtiRequestMsg *pReq,
                    CtiCommandParser &parse,
                    OUTMESS *&OutMessage,
                    RWTPtrSlist< CtiMessage > &vgList,
                    RWTPtrSlist< CtiMessage > &retList,
                    RWTPtrSlist< OUTMESS > &outList,
                    INT ScanPriority = MAXPRIORITY-4);


   virtual INT ResultDecode(INMESS                    *InMessage,
                            RWTime                    &TimeNow,
                            RWTPtrSlist< CtiMessage > &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist< OUTMESS >    &outList);

   virtual INT ErrorDecode(INMESS                     *InMessage,
                           RWTime                     &TimeNow,
                           RWTPtrSlist< CtiMessage >  &vgList,
                           RWTPtrSlist< CtiMessage >  &retList,
                           RWTPtrSlist< OUTMESS >     &outList);

   virtual INT ExecuteRequest( CtiRequestMsg         *pReq,
                       CtiCommandParser           &parse,
                       OUTMESS                   *&OutMessage,
                       RWTPtrSlist< CtiMessage >  &vgList,
                       RWTPtrSlist< CtiMessage >  &retList,
                       RWTPtrSlist< OUTMESS >     &outList );

   CtiProtocolANSI & getSentinelProtocol( void );
   void processDispatchReturnMessage( CtiReturnMsg *msgPtr );
   int buildScannerTableRequest (BYTE *ptr);
   INT sendCommResult( INMESS *InMessage);

   struct WANTS_HEADER
   {
      unsigned long  lastLoadProfileTime;
      int            numTablesRequested;
      int            command;
   };


private:
    CtiProtocolANSI_sentinel   _ansiProtocol;
};


#endif // #ifndef __DEV_SENTINEL_H__
