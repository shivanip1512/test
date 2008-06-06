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
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2008/06/06 20:28:44 $
*

* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __DEV_SENTINEL_H__
#define __DEV_SENTINEL_H__


#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_ansi_sentinel.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "device.h"
#include "dllyukon.h"

class IM_EX_DEVDB CtiDeviceSentinel : public CtiDeviceMeter
{

public:

   CtiDeviceSentinel();
   virtual ~CtiDeviceSentinel();

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           list< CtiMessage* >  &vgList,
                           list< CtiMessage* >  &retList,
                           list< OUTMESS* >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);
   virtual INT DemandReset( CtiRequestMsg *pReq,
                    CtiCommandParser &parse,
                    OUTMESS *&OutMessage,
                    list< CtiMessage* > &vgList,
                    list< CtiMessage* > &retList,
                    list< OUTMESS* > &outList,
                    INT ScanPriority = MAXPRIORITY-4);


   virtual INT ResultDecode(INMESS                    *InMessage,
                            CtiTime                    &TimeNow,
                            list< CtiMessage* > &vgList,
                            list< CtiMessage* > &retList,
                            list< OUTMESS* >    &outList);

   virtual INT ErrorDecode(INMESS                     *InMessage,
                           CtiTime                     &TimeNow,
                           list< CtiMessage* >  &vgList,
                           list< CtiMessage* >  &retList,
                           list< OUTMESS* >     &outList,
                           bool &overrideExpectMore);

   virtual INT ExecuteRequest( CtiRequestMsg         *pReq,
                       CtiCommandParser           &parse,
                       OUTMESS                   *&OutMessage,
                       list< CtiMessage* >  &vgList,
                       list< CtiMessage* >  &retList,
                       list< OUTMESS* >     &outList );

   CtiProtocolANSI_sentinel& getSentinelProtocol( void );
   void processDispatchReturnMessage( list< CtiReturnMsg* > &retList, UINT archiveFlag );
   int buildScannerTableRequest (BYTE *ptr, UINT flags);
   int buildCommanderTableRequest (BYTE *ptr, UINT flags);
   INT sendCommResult( INMESS *InMessage);

   struct WANTS_HEADER
   {
      unsigned long  lastLoadProfileTime;
      int            numTablesRequested;
      int            command;
   };


private:
    CtiProtocolANSI_sentinel   _ansiProtocol;

    //UINT _parseFlags;
    string _result_string;

    unsigned long _lastLPTime;
};


#endif // #ifndef __DEV_SENTINEL_H__
