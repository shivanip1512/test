
/*-----------------------------------------------------------------------------*
*
* File:   rte_expresscom
*
* Class:  CtiRouteExpresscom
* Date:   7/18/2006
*
* Author: Jess Otteson
*
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/rte_versacom.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2006/07/19 19:00:59 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __RTE_EXPRESSCOM_H__
#define __RTE_EXPRESSCOM_H__


#include <rw/tpslist.h>

#include "dsm2.h"
#include "rte_xcu.h"
#include "tbl_rtversacom.h"
#include "master.h"
#include "ctibase.h"
#include "dev_remote.h"

class IM_EX_DEVDB CtiRouteExpresscom : public CtiRouteXCU
{
protected:

   CtiTableVersacomRoute    Versacom; //Leave this as this for now.., it can be used the naming is just unfortunate

private:

public:

   typedef CtiRouteXCU Inherited;


   CtiRouteExpresscom();

   CtiRouteExpresscom(const CtiRouteExpresscom& aRef);
   ~CtiRouteExpresscom();

   CtiRouteExpresscom& operator=(const CtiRouteExpresscom& aRef);
   virtual void DumpData();
   virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              list< CtiMessage* >      &vgList,
                              list< CtiMessage* >      &retList,
                              list< OUTMESS* >         &outList);

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual void DecodeVersacomDatabaseReader(RWDBReader &rdr);
};
#endif // #ifndef __RTE_EXPRESSCOM_H__
