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
* REVISION     :  $Revision: 1.15.4.2 $
* DATE         :  $Date: 2008/11/17 23:06:32 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_WELCO_H__
#define __DEV_WELCO_H__
#pragma warning( disable : 4786 )



#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "prot_welco.h"

class IM_EX_DEVDB CtiDeviceWelco : public CtiDeviceIDLC
{
private:

   typedef CtiDeviceIDLC Inherited;

   int     _deadbandsSent;

protected:

public:

   CtiDeviceWelco();
   CtiDeviceWelco(const CtiDeviceWelco& aRef);
   virtual ~CtiDeviceWelco();

   CtiDeviceWelco& operator=(const CtiDeviceWelco& aRef);
   INT WelCoContinue    (OUTMESS *OutMessage, INT Priority);
   INT WelCoGetError    (OUTMESS *OutMessage, INT Priority);
   INT WelCoPoll        (OUTMESS *OutMessage, INT Priority);
   INT WelCoReset       (OUTMESS *OutMessage, INT Priority);
   INT WelCoTimeSync    (OUTMESS *OutMessage, INT Priority);

   INT WelCoDeadBands   (OUTMESS *OutMessage, std::list< OUTMESS* > &outList, INT Priority);

   INT WelCoDeadBands   (INMESS  *InMessage,  std::list< OUTMESS* > &outList, INT Priority);
   INT WelCoTimeSync    (INMESS  *InMessage,  std::list< OUTMESS* > &outList, INT Priority);

   bool getDeadbandsSent() const;
   void incDeadbandsSent();
   CtiDeviceWelco& setDeadbandsSent(const bool b);

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   virtual INT GeneralScan(CtiRequestMsg *pReq,
                           CtiCommandParser &parse,
                           OUTMESS *&OutMessage,
                           std::list< CtiMessage* > &vgList,
                           std::list< CtiMessage* > &retList,
                           std::list< OUTMESS* > &outList,
                           INT ScanPriority = MAXPRIORITY - 4);
   virtual INT AccumulatorScan(CtiRequestMsg *pReq,
                               CtiCommandParser &parse,
                               OUTMESS *&OutMessage,
                               std::list< CtiMessage* > &vgList,
                               std::list< CtiMessage* > &retList,
                               std::list< OUTMESS* > &outList,
                               INT ScanPriority = MAXPRIORITY - 3);
   virtual INT IntegrityScan(CtiRequestMsg *pReq,
                             CtiCommandParser &parse,
                             OUTMESS *&OutMessage,
                             std::list< CtiMessage* > &vgList,
                             std::list< CtiMessage* > &retList,
                             std::list< OUTMESS* > &outList,
                             INT ScanPriority = MAXPRIORITY - 4);

   virtual INT ErrorDecode(const INMESS      &InMessage,
                           const CtiTime      TimeNow,
                           std::list<CtiMessage*> &retList);

   virtual INT ResultDecode(INMESS*,
                            CtiTime&,
                            std::list< CtiMessage* >   &vgList,
                            std::list< CtiMessage* > &retList,
                            std::list< OUTMESS* > &outList);

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              std::list< CtiMessage* >      &vgList,
                              std::list< CtiMessage* >      &retList,
                              std::list< OUTMESS* >         &outList);

   virtual INT executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

   virtual bool clearedForScan(int scantype);
   virtual void resetForScan(int scantype);

};
#endif // #ifndef __DEV_WELCO_H__
