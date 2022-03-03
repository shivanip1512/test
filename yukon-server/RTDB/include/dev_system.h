#pragma once

#include "dev_base.h"


class CtiDeviceSystem : public CtiDeviceBase
{
    typedef CtiDeviceBase Inherited;

public:

   CtiDeviceSystem();

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   virtual std::string getDescription(const CtiCommandParser &parse) const;

   virtual YukonError_t ProcessInMessageResult(const INMESS&, const CtiTime, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

};
