/*-----------------------------------------------------------------------------*
*
* File:   dev_davis
*
* Class:  CtiDeviceDavis
* Date:   6/17/2002
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:36:10 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_DAVIS_H__
#define __DEV_DAVIS_H__
#pragma warning( disable : 4786 )


#include <windows.h>
#include "dev_ied.h"
#include "mgr_point.h"

class IM_EX_DEVDB CtiDeviceDavis : public CtiDeviceIED
{
protected:

private:

    INT generateScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = 14);

public:

   typedef CtiDeviceRemote Inherited;

   CtiDeviceDavis();
   CtiDeviceDavis(const CtiDeviceDavis& aRef);
   virtual ~CtiDeviceDavis();

   CtiDeviceDavis& operator=(const CtiDeviceDavis& aRef);
   /*
    *  These guys initiate a scan based upon the type requested.
    */

   virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
   virtual INT ErrorDecode(INMESS*, RWTime&, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);
   virtual INT ResultDecode(INMESS*, RWTime&, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);
   virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

   static RWCString getPointOffsetName(int offset);
};
#endif // #ifndef __DEV_DAVIS_H__
