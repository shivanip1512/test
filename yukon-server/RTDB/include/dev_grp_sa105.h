#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sa105.h"
#include "tbl_lmg_sa205105.h"


class IM_EX_DEVDB CtiDeviceGroupSA105 : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

protected:

    CtiTableSA205105Group _loadGroup;

public:

    CtiDeviceGroupSA105();
    CtiDeviceGroupSA105(const CtiDeviceGroupSA105& aRef);
    virtual ~CtiDeviceGroupSA105();

    CtiDeviceGroupSA105& operator=(const CtiDeviceGroupSA105& aRef);

    CtiTableSA205105Group getLoadGroup() const;
    CtiTableSA205105Group& getLoadGroup();
    CtiDeviceGroupSA105& setLoadGroup(const CtiTableSA205105Group& aRef);

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
};
