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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2006/02/27 23:58:33 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_TCU_H__
#define __DEV_TCU_H__


#include <windows.h>

#include <rw/tpslist.h>

#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "mgr_point.h"

class IM_EX_DEVDB CtiDeviceTCU : public CtiDeviceIDLC
{
protected:

   CtiTime              LastPointRefresh;

private:

   bool _sendFiller;


public:


   typedef CtiDeviceIDLC Inherited;

   CtiDeviceTCU();

   CtiDeviceTCU(const CtiDeviceTCU& aRef);

   virtual ~CtiDeviceTCU();

   CtiDeviceTCU& operator=(const CtiDeviceTCU& aRef);

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   INT               TCUDecode(INMESS *InMessage, CtiTime &ScanTime, list< CtiMessage* > &retList);
   CtiReturnMsg*  TCUDecodeStatus(INMESS *InMessage);

   INT               TCUControl(OUTMESS*, VSTRUCT*);
   INT               TCUScanAll(OUTMESS*);
   INT               TCULoop(OUTMESS*);

   virtual INT IntegrityScan(CtiRequestMsg *pReq,
                             CtiCommandParser &parse,
                             OUTMESS *&OutMessage,
                             list< CtiMessage* > &vgList,
                             list< CtiMessage* > &retList,
                             list< OUTMESS* > &outList,
                             INT ScanPriority = MAXPRIORITY - 4);
   virtual INT GeneralScan(CtiRequestMsg *pReq,
                           CtiCommandParser &parse,
                           OUTMESS *&OutMessage,
                           list< CtiMessage* > &vgList,
                           list< CtiMessage* > &retList,
                           list< OUTMESS* > &outList,
                           INT ScanPriority = MAXPRIORITY - 4);
   virtual INT ResultDecode(INMESS*,
                            CtiTime&,
                            list< CtiMessage* >   &vgList,
                            list< CtiMessage* > &retList,
                            list< OUTMESS* > &outList);

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              list< CtiMessage* >      &vgList,
                              list< CtiMessage* >      &retList,
                              list< OUTMESS* >         &outList);

   CtiDeviceTCU& setSendFiller(bool yesno);
   bool getSendFiller() const;

   virtual INT getProtocolWrap() const;

};
#endif // #ifndef __DEV_TCU_H__
