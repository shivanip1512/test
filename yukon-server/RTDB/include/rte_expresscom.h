#pragma once

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
