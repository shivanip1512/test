#pragma warning( disable : 4786 )

#ifndef __DEV_WELCO_H__
#define __DEV_WELCO_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_welco
*
* Class:  CtiDeviceWelco
* Date:   8/27/1999
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_welco.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:54 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <windows.h>
#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "mgr_point.h"
#include "connection.h"
#include "prot_welco.h"

class IM_EX_DEVDB CtiDeviceWelco : public CtiDeviceIDLC
{
protected:

   private:

   bool     _deadbandsSent;

public:

   typedef CtiDeviceIDLC Inherited;

   CtiDeviceWelco();
   CtiDeviceWelco(const CtiDeviceWelco& aRef);
   virtual ~CtiDeviceWelco();

   CtiDeviceWelco& operator=(const CtiDeviceWelco& aRef);
   INT WelCoContinue    (OUTMESS *OutMessage, INT Priority);
   INT WelCoGetError    (OUTMESS *OutMessage, INT Priority);
   INT WelCoPoll        (OUTMESS *OutMessage, INT Priority);
   INT WelCoReset       (OUTMESS *OutMessage, INT Priority);
   INT WelCoTimeSync    (OUTMESS *OutMessage, INT Priority);

   INT WelCoDeadBands   (OUTMESS *OutMessage, RWTPtrSlist< OUTMESS > &outList, INT Priority);

   INT WelCoDeadBands   (INMESS  *InMessage,  RWTPtrSlist< OUTMESS > &outList, INT Priority);
   INT WelCoTimeSync    (INMESS  *InMessage,  RWTPtrSlist< OUTMESS > &outList, INT Priority);

   bool getDeadbandsSent() const;
   CtiDeviceWelco& setDeadbandsSent(const bool b);

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   virtual INT GeneralScan(CtiRequestMsg *pReq,
                           CtiCommandParser &parse,
                           OUTMESS *&OutMessage,
                           RWTPtrSlist< CtiMessage > &vgList,
                           RWTPtrSlist< CtiMessage > &retList,
                           RWTPtrSlist< OUTMESS > &outList,
                           INT ScanPriority = MAXPRIORITY - 4);
   virtual INT AccumulatorScan(CtiRequestMsg *pReq,
                               CtiCommandParser &parse,
                               OUTMESS *&OutMessage,
                               RWTPtrSlist< CtiMessage > &vgList,
                               RWTPtrSlist< CtiMessage > &retList,
                               RWTPtrSlist< OUTMESS > &outList,
                               INT ScanPriority = MAXPRIORITY - 3);
   virtual INT IntegrityScan(CtiRequestMsg *pReq,
                             CtiCommandParser &parse,
                             OUTMESS *&OutMessage,
                             RWTPtrSlist< CtiMessage > &vgList,
                             RWTPtrSlist< CtiMessage > &retList,
                             RWTPtrSlist< OUTMESS > &outList,
                             INT ScanPriority = MAXPRIORITY - 4);

   virtual INT ErrorDecode(INMESS*,
                           RWTime&,
                           RWTPtrSlist< CtiMessage >   &vgList,
                           RWTPtrSlist< CtiMessage > &retList,
                           RWTPtrSlist<OUTMESS> &outList);

   virtual INT ResultDecode(INMESS*,
                            RWTime&,
                            RWTPtrSlist< CtiMessage >   &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist<OUTMESS> &outList);

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);

   virtual INT executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);


};
#endif // #ifndef __DEV_WELCO_H__
