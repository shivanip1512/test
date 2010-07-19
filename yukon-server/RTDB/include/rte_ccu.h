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
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
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

   static void adjustStagesToFollow(unsigned short &stagesToFollow, unsigned &messageFlags, const bool isOneWayCcu711Request);

private:

    enum
    {
        MaxStagesToFollow = 7
    };

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

   static string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual void addRepeater(const CtiTableRepeaterRoute &Rpt);
};

typedef boost::shared_ptr<CtiRouteCCU> CtiRouteCCUSPtr;

#endif // #ifndef __RTE_CCU_H__
