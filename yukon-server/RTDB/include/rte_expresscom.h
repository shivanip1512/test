#pragma once

#include "dsm2.h"
#include "rte_xcu.h"
#include "tbl_rtversacom.h"
#include "dllbase.h"
#include "dev_remote.h"

class IM_EX_DEVDB CtiRouteExpresscom : public CtiRouteXCU
{
   CtiTableVersacomRoute    Versacom; //Leave this as this for now.., it can be used the naming is just unfortunate

public:

   typedef CtiRouteXCU Inherited;

   CtiRouteExpresscom();

   virtual std::string toString() const override;

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) override;

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
   void DecodeVersacomDatabaseReader(Cti::RowReader &rdr);
};
