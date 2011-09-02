#pragma once

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
                               std::list< CtiMessage* >      &vgList,
                               std::list< CtiMessage* >      &retList,
                               std::list< OUTMESS* >         &outList);

   virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              std::list< CtiMessage* >      &vgList,
                              std::list< CtiMessage* >      &retList,
                              std::list< OUTMESS* >         &outList);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual void DecodeVersacomDatabaseReader(Cti::RowReader &rdr);
};
