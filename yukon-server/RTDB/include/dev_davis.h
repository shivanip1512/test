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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2006/02/27 23:58:31 $
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

    INT generateScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = 14);

public:

   typedef CtiDeviceRemote Inherited;

   CtiDeviceDavis();
   CtiDeviceDavis(const CtiDeviceDavis& aRef);
   virtual ~CtiDeviceDavis();

   CtiDeviceDavis& operator=(const CtiDeviceDavis& aRef);
   /*
    *  These guys initiate a scan based upon the type requested.
    */

   virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
   virtual INT ErrorDecode(INMESS*, CtiTime&, list< CtiMessage* >   &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
   virtual INT ResultDecode(INMESS*, CtiTime&, list< CtiMessage* >   &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
   virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

   static string getPointOffsetName(int offset);
};
#endif // #ifndef __DEV_DAVIS_H__
