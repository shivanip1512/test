
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
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/11/15 20:43:23 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_ansi.h"
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
   int makeMessageHeader( BYTE *ptr, int command );

private:

protected:

   struct WANTS_HEADER
   {
      unsigned long  lastLoadProfileTime;
      int            numTablesRequested;
      int            command;
   };

   CtiProtocolANSI   _ansiProtocol;
};


#endif // #ifndef __DEV_KV2_H__
