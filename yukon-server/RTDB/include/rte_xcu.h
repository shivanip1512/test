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
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2004/05/19 14:54:39 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __RTE_XCU_H__
#define __RTE_XCU_H__


#include <rw/tpslist.h>

#include "dev_base.h"
#include "rte_base.h"
#include "msg_pcrequest.h"
#include "smartmap.h"


class IM_EX_DEVDB CtiRouteXCU : public CtiRouteBase
{
protected:

   CtiDeviceSPtr _transmitterDevice;    // This object is NOT responsible for this memory..

private:

    void enablePrefix(bool enable);

public:

   typedef CtiRouteBase Inherited;

   CtiRouteXCU();

   CtiRouteXCU(const CtiRouteXCU& aRef);
   ~CtiRouteXCU();

   CtiRouteXCU& operator=(const CtiRouteXCU& aRef);
   virtual void DumpData();

   void resetDevicePointer();
   CtiRouteXCU&  setDevicePointer(CtiDeviceSPtr p);

   virtual LONG getTrxDeviceID() const;

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

   INT assembleVersacomRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS >  &outList);
   INT assembleFisherPierceRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS >  &outList);
   INT assembleRippleRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS >  &outList);
   INT assembleExpresscomRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
   INT assembleSA305Request(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
   INT assembleSA105205Request(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
   INT assembleSASimpleRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

};



#endif // #ifndef __RTE_XCU_H__
