#pragma warning (disable: 4786)

#ifndef __RTE_VERSACOM_H__
#define __RTE_VERSACOM_H__

/*-----------------------------------------------------------------------------*
*
* File:   rte_versacom
*
* Class:  CtiRouteVersacom
* Date:   9/30/1999
*
* Author: Corey G. Plender
*
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/rte_versacom.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/04/22 19:47:20 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <rw/tpslist.h>

#include "dsm2.h"
#include "rte_xcu.h"
#include "tbl_rtversacom.h"
#include "master.h"
#include "ctibase.h"
#include "dev_remote.h"

class IM_EX_DEVDB CtiRouteVersacom : public CtiRouteXCU
{
protected:

   CtiTableVersacomRoute    Versacom;

private:

public:

   typedef CtiRouteXCU Inherited;


   CtiRouteVersacom();

   CtiRouteVersacom(const CtiRouteVersacom& aRef);
   ~CtiRouteVersacom();

   CtiRouteVersacom& operator=(const CtiRouteVersacom& aRef);
   virtual void DumpData();
   INT assembleVersacomRequest(CtiRequestMsg                  *pReq,
                               CtiCommandParser               &parse,
                               OUTMESS                        *OutMessage,
                               RWTPtrSlist< CtiMessage >      &vgList,
                               RWTPtrSlist< CtiMessage >      &retList,
                               RWTPtrSlist< OUTMESS >         &outList);

   virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual void DecodeVersacomDatabaseReader(RWDBReader &rdr);
};
#endif // #ifndef __RTE_VERSACOM_H__
