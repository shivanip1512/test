
#ifndef __RTE_XCU_H__
#define __RTE_XCU_H__

/*-----------------------------------------------------------------------------*
*
* File:   rte_xcu
*
* Class:  CtiRouteXCU
* Date:   9/30/1999
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/rte_xcu.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:58 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/tpslist.h>

#include "dev_base.h"
#include "rte_base.h"
#include "msg_pcrequest.h"

class IM_EX_DEVDB CtiRouteXCU : public CtiRouteBase
{
protected:

   CtiDevice            *Device;    // This object is NOT responsible for this memory..

private:

public:

   typedef CtiRouteBase Inherited;

   CtiRouteXCU();

   CtiRouteXCU(const CtiRouteXCU& aRef);
   ~CtiRouteXCU();

   CtiRouteXCU& operator=(const CtiRouteXCU& aRef);
   virtual void DumpData();

   void resetDevicePointer();
   CtiDevice*    getDevicePointer();
   CtiRouteXCU&  setDevicePointer(CtiDevice *p);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   static void getSQL(RWCString &Columns, RWCString &Tables, RWCString &Conditions);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

   INT assembleVersacomRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS >  &outList);
   INT assembleFisherPierceRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS >  &outList);
   INT assembleRippleRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS >  &outList);


};



#endif // #ifndef __RTE_XCU_H__
