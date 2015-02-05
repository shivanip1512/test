#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_lmg_point.h"


class IM_EX_DEVDB CtiDeviceGroupPoint : public CtiDeviceGroupBase
{
    typedef CtiDeviceGroupBase Inherited;

protected:

    CtiTablePointGroup _loadGroup;

public:

    CtiDeviceGroupPoint();

    virtual LONG getRouteID();

    INT generateRequest(CtiRequestMsg *pReq, CtiCommandParser &parse);
    std::string getDescription(const CtiCommandParser & parse) const;

    virtual std::string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr) override;
    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    const CtiTablePointGroup& getLoadGroup() const { return _loadGroup; }

};
