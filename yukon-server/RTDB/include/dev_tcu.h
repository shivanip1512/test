
#ifndef __DEV_TCU_H__
#define __DEV_TCU_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_tcu
*
* Class:  CtiDeviceTCU
* Date:   9/14/1999
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_tcu.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <windows.h>

#include <rw/tpslist.h>

#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "mgr_point.h"
#include "connection.h"

class IM_EX_DEVDB CtiDeviceTCU : public CtiDeviceIDLC
{
protected:

   RWTime              LastPointRefresh;

private:

   bool _sendFiller;


public:


   typedef CtiDeviceIDLC Inherited;

   CtiDeviceTCU();// : _sendFiller(false) {}

   CtiDeviceTCU(const CtiDeviceTCU& aRef);/*
   {
      *this = aRef;
   }                                        */

   virtual ~CtiDeviceTCU();/*
   {
   }                         */

   CtiDeviceTCU& operator=(const CtiDeviceTCU& aRef);/*
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);
         setSendFiller( aRef.getSendFiller() );
      }
      return *this;
   }                                                   */

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   INT               TCUDecode(INMESS *InMessage, RWTime &ScanTime, RWTPtrSlist< CtiMessage > &retList);
   CtiReturnMsg*  TCUDecodeStatus(INMESS *InMessage);

   INT               TCUControl(OUTMESS*, VSTRUCT*);
   INT               TCUScanAll(OUTMESS*);
   INT               TCULoop(OUTMESS*);

   virtual INT IntegrityScan(CtiRequestMsg *pReq,
                             CtiCommandParser &parse,
                             OUTMESS *&OutMessage,
                             RWTPtrSlist< CtiMessage > &vgList,
                             RWTPtrSlist< CtiMessage > &retList,
                             RWTPtrSlist< OUTMESS > &outList,
                             INT ScanPriority = MAXPRIORITY - 4);
   virtual INT GeneralScan(CtiRequestMsg *pReq,
                           CtiCommandParser &parse,
                           OUTMESS *&OutMessage,
                           RWTPtrSlist< CtiMessage > &vgList,
                           RWTPtrSlist< CtiMessage > &retList,
                           RWTPtrSlist< OUTMESS > &outList,
                           INT ScanPriority = MAXPRIORITY - 4);
   virtual INT ResultDecode(INMESS*,
                            RWTime&,
                            RWTPtrSlist< CtiMessage >   &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist< OUTMESS > &outList);

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);

   CtiDeviceTCU& setSendFiller(bool yesno);
   bool getSendFiller() const;

};
#endif // #ifndef __DEV_TCU_H__
