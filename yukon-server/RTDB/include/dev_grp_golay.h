#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sasimple.h"
#include "tbl_lmg_sasimple.h"

class IM_EX_DEVDB CtiDeviceGroupGolay : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

protected:

    CtiTableSASimpleGroup   _loadGroup;

public:

    CtiDeviceGroupGolay();
    virtual ~CtiDeviceGroupGolay();

    CtiTableSASimpleGroup getLoadGroup() const;
    CtiTableSASimpleGroup& getLoadGroup();
    CtiDeviceGroupGolay& setLoadGroup(const CtiTableSASimpleGroup& aRef);

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

};
