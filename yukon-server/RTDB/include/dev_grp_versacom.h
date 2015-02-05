#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_versacom.h"

class IM_EX_DEVDB CtiDeviceGroupVersacom : public CtiDeviceGroupBase
{
    typedef CtiDeviceGroupBase Inherited;

protected:

   CtiTableVersacomLoadGroup     VersacomGroup;

public:

   CtiDeviceGroupVersacom();

   virtual LONG getRouteID();
   virtual std::string getDescription(const CtiCommandParser & parse) const;

   virtual std::string getSQLCoreStatement() const;

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
   virtual std::string getPutConfigAssignment(UINT modifier = 0);

};
