#pragma warning( disable : 4786)
#ifndef __DEV_KV2_H__
#define __DEV_KV2_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_kv2
*
* Class:
* Date:   6/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_kv2.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/09/30 21:37:22 $
*
*    History: 
      $Log: dev_kv2.h,v $
      Revision 1.6  2004/09/30 21:37:22  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.5  2003/04/25 15:14:07  dsutton
      Changed general scan and decode result


* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_ansi_kv2.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "mgr_point.h"
#include "device.h"

class IM_EX_DEVDB CtiDeviceKV2 : public CtiDeviceMeter
{

public:

   CtiDeviceKV2();
   virtual ~CtiDeviceKV2();

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           RWTPtrSlist< CtiMessage >  &vgList,
                           RWTPtrSlist< CtiMessage >  &retList,
                           RWTPtrSlist< OUTMESS >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);

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

   CtiProtocolANSI & getProtocol( void );
   void processDispatchReturnMessage( CtiReturnMsg *msgPtr );
   int buildScannerTableRequest (BYTE *ptr);

   struct WANTS_HEADER
   {
      unsigned long  lastLoadProfileTime;
      int            numTablesRequested;
      int            command;
   };


private:
    CtiProtocolANSI_kv2   _ansiProtocol;
};


#endif // #ifndef __DEV_KV2_H__
