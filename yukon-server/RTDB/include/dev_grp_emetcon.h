#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_emetcon.h"

class IM_EX_DEVDB CtiDeviceGroupEmetcon : public CtiDeviceGroupBase
{
    typedef CtiDeviceGroupBase Inherited;

protected:

   CtiTableEmetconLoadGroup      EmetconGroup;

public:

   CtiDeviceGroupEmetcon();

   virtual LONG getRouteID();
   virtual std::string getDescription(const CtiCommandParser & parse) const;

   virtual std::string getSQLCoreStatement() const;

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   static std::string CtiDeviceGroupEmetcon::generateCommandString(OUTMESS *&OutMessage);
};
