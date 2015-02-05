#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_lmg_sa205105.h"


class IM_EX_DEVDB CtiDeviceGroupSA105 : public CtiDeviceGroupBase
{
    typedef CtiDeviceGroupBase Inherited;

protected:

    CtiTableSA205105Group _loadGroup;

public:

    CtiDeviceGroupSA105();

    const CtiTableSA205105Group& getLoadGroup() const;

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    void DecodeDatabaseReader(Cti::RowReader &rdr) override;
    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
};
