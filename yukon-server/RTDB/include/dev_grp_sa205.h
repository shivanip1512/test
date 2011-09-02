#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sa205.h"
#include "tbl_lmg_sa205105.h"


class IM_EX_DEVDB CtiDeviceGroupSA205 : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

    int _lastSTime;     // Holds the last sent message for the restore graceful command.
    int _lastCTime;
    CtiTime _onePeriodLeft;

protected:

    CtiTableSA205105Group _loadGroup;

public:

    CtiDeviceGroupSA205();
    CtiDeviceGroupSA205(const CtiDeviceGroupSA205& aRef);
    virtual ~CtiDeviceGroupSA205();

    CtiDeviceGroupSA205& operator=(const CtiDeviceGroupSA205& aRef);

    CtiTableSA205105Group getLoadGroup() const;
    CtiTableSA205105Group& getLoadGroup();
    CtiDeviceGroupSA205& setLoadGroup(const CtiTableSA205105Group& aRef);

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
};
