#pragma warning( disable : 4786 )

#ifndef __DEV_ILEX_H__
#define __DEV_ILEX_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_ilex
*
* Class:  CtiDeviceILEX
* Date:   12/18/2001
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_welco.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <windows.h>
#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "mgr_point.h"
#include "connection.h"

class IM_EX_DEVDB CtiDeviceILEX : public CtiDeviceIDLC
{
protected:

private:

public:

   typedef CtiDeviceIDLC Inherited;

   CtiDeviceILEX();
   CtiDeviceILEX(const CtiDeviceILEX& aRef);
   virtual ~CtiDeviceILEX();

   CtiDeviceILEX& operator=(const CtiDeviceILEX& aRef);
   /*
    *  These guys initiate a scan based upon the type requested.
    */
/*
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
*/

};
#endif // #ifndef __DEV_ILEX_H__
