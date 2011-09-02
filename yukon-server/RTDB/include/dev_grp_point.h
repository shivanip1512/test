#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_lmg_point.h"


class IM_EX_DEVDB CtiDeviceGroupPoint : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

protected:

    CtiTablePointGroup _loadGroup;

public:

    CtiDeviceGroupPoint();
    CtiDeviceGroupPoint(const CtiDeviceGroupPoint& aRef);
    virtual ~CtiDeviceGroupPoint();
    virtual LONG getRouteID();
    CtiDeviceGroupPoint& operator=(const CtiDeviceGroupPoint& aRef);

    INT generateRequest(CtiRequestMsg *pReq, CtiCommandParser &parse);
    std::string getDescription(const CtiCommandParser & parse) const;

    virtual std::string getSQLCoreStatement() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    CtiTablePointGroup& getLoadGroup() { return _loadGroup; }

};
