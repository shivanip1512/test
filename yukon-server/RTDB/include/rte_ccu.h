/*-----------------------------------------------------------------------------*
*
* File:   rte_ccu
*
* Class:  CtiRouteCCU
* Date:   9/30/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/rte_ccu.h-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2007/11/12 16:49:47 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __RTE_CCU_H__
#define __RTE_CCU_H__

#include <rw/tvordvec.h>
#include "rte_xcu.h"
#include "tbl_rtcarrier.h"
#include "tbl_rtrepeater.h"

class IM_EX_DEVDB CtiRouteCCU : public CtiRouteXCU
{
protected:

   CtiTableCarrierRoute    Carrier;

   // This is a vector of repeaters 0 to 7 in length... currently we rely on DBEditor to assure this...
   RWTValOrderedVector< CtiTableRepeaterRoute >  RepeaterList;

private:

public:

   typedef CtiRouteXCU Inherited;
   typedef RWTValOrderedVector< CtiTableRepeaterRoute > CtiRepeaterList_t;

   CtiRouteCCU();
   CtiRouteCCU(const CtiRouteCCU& aRef);

   ~CtiRouteCCU();

   CtiRouteCCU &operator=(const CtiRouteCCU& aRef);

   virtual void DumpData();

   CtiRepeaterList_t &getRepeaterList();

   virtual INT        getStages() const;

   virtual INT ExecuteRequest(CtiRequestMsg        *pReq,
                              CtiCommandParser     &parse,
                              OUTMESS             *&OutMessage,
                              list< CtiMessage* >  &vgList,
                              list< CtiMessage* >  &retList,
                              list< OUTMESS* >     &outList);

   INT         assembleVersacomRequest  (CtiRequestMsg        *pReq,
                                         CtiCommandParser     &parse,
                                         OUTMESS              *OutMessage,
                                         list< CtiMessage* >  &vgList,
                                         list< CtiMessage* >  &retList,
                                         list< OUTMESS* >     &outList);

   INT         assembleExpresscomRequest(CtiRequestMsg        *pReq,
                                         CtiCommandParser     &parse,
                                         OUTMESS              *OutMessage,
                                         list< CtiMessage* >  &vgList,
                                         list< CtiMessage* >  &retList,
                                         list< OUTMESS* >     &outList);

   INT         assembleDLCRequest       (CtiCommandParser     &parse,
                                         OUTMESS             *&OutMessage,
                                         list< CtiMessage* >  &vgList,
                                         list< CtiMessage* >  &retList,
                                         list< OUTMESS* >     &outList);

   virtual INT  getBus() const;
   virtual INT  getCCUFixBits() const;
   virtual INT  getCCUVarBits() const;

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual void DecodeRepeaterDatabaseReader(RWDBReader &rdr);
};
#endif // #ifndef __RTE_CCU_H__
