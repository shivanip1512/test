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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __RTE_EXPRESSCOM_H__
#define __RTE_EXPRESSCOM_H__


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
                              std::list< CtiMessage* >      &vgList,
                              std::list< CtiMessage* >      &retList,
                              std::list< OUTMESS* >         &outList);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual void DecodeVersacomDatabaseReader(Cti::RowReader &rdr);
};
#endif // #ifndef __RTE_EXPRESSCOM_H__
