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
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/12/20 17:20:29 $
*
*    History:
      $Log: dev_kv2.h,v $
      Revision 1.12  2005/12/20 17:20:29  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.11  2005/09/29 21:19:24  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.10  2005/06/16 19:18:00  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.9  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.8  2005/03/10 20:21:07  mfisher
      changed getProtocol to getKV2Protocol so it wouldn't interfere with the new dev_single getProtocol

      Revision 1.7  2004/12/10 21:58:43  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.6  2004/09/30 21:37:22  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.5  2003/04/25 15:14:07  dsutton
      Changed general scan and decode result


* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __DEV_KV2_H__
#define __DEV_KV2_H__

#include <string>

#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_ansi_kv2.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "mgr_point.h"
#include "device.h"
#include "dllyukon.h"

using std::string;

class IM_EX_DEVDB CtiDeviceKV2 : public CtiDeviceMeter
{

public:

   CtiDeviceKV2();
   virtual ~CtiDeviceKV2();

   virtual INT DemandReset( CtiRequestMsg *pReq, 
                    CtiCommandParser &parse, 
                    OUTMESS *&OutMessage, 
                    RWTPtrSlist< CtiMessage > &vgList,
                    RWTPtrSlist< CtiMessage > &retList, 
                    RWTPtrSlist< OUTMESS > &outList, 
                    INT ScanPriority = MAXPRIORITY-4);

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           RWTPtrSlist< CtiMessage >  &vgList,
                           RWTPtrSlist< CtiMessage >  &retList,
                           RWTPtrSlist< OUTMESS >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);

   virtual INT ResultDecode(INMESS                    *InMessage,
                            CtiTime                    &TimeNow,
                            RWTPtrSlist< CtiMessage > &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist< OUTMESS >    &outList);

   virtual INT ErrorDecode(INMESS                     *InMessage,
                           CtiTime                     &TimeNow,
                           RWTPtrSlist< CtiMessage >  &vgList,
                           RWTPtrSlist< CtiMessage >  &retList,
                           RWTPtrSlist< OUTMESS >     &outList);
   virtual INT ExecuteRequest( CtiRequestMsg         *pReq,
                       CtiCommandParser           &parse,
                       OUTMESS                   *&OutMessage,
                       RWTPtrSlist< CtiMessage >  &vgList,
                       RWTPtrSlist< CtiMessage >  &retList,
                       RWTPtrSlist< OUTMESS >     &outList );


   CtiProtocolANSI & getKV2Protocol( void );
   void processDispatchReturnMessage( RWTPtrSlist< CtiReturnMsg >  &retList, UINT archiveFlag );
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
    CtiProtocolANSI_kv2   _ansiProtocol;

    UINT _parseFlags;
    string _result_string;
    unsigned long _lastLPTime;

};


#endif // #ifndef __DEV_KV2_H__
