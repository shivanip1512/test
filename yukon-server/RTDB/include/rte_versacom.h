#pragma once

#include "dsm2.h"
#include "rte_xcu.h"
#include "tbl_rtversacom.h"
#include "master.h"
#include "ctibase.h"
#include "dev_remote.h"

class IM_EX_DEVDB CtiRouteVersacom : public CtiRouteXCU
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiRouteVersacom(const CtiRouteVersacom&);
    CtiRouteVersacom& operator=(const CtiRouteVersacom&);

protected:

   CtiTableVersacomRoute    Versacom;

public:

   typedef CtiRouteXCU Inherited;

   CtiRouteVersacom();

   virtual std::string toString() const override;

   YukonError_t assembleVersacomRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) override;

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
   void DecodeVersacomDatabaseReader(Cti::RowReader &rdr);
};
