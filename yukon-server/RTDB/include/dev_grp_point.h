#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_lmg_point.h"


class IM_EX_DEVDB CtiDeviceGroupPoint : public CtiDeviceGroupBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceGroupPoint(const CtiDeviceGroupPoint&);
    CtiDeviceGroupPoint& operator=(const CtiDeviceGroupPoint&);

    typedef CtiDeviceGroupBase Inherited;

protected:

    CtiTablePointGroup _loadGroup;

public:

    CtiDeviceGroupPoint();

    virtual LONG getRouteID();

    INT generateRequest(CtiRequestMsg *pReq, CtiCommandParser &parse);
    std::string getDescription(const CtiCommandParser & parse) const;

    virtual std::string getSQLCoreStatement() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    const CtiTablePointGroup& getLoadGroup() const { return _loadGroup; }

};
