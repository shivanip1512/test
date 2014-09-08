#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sa205.h"
#include "tbl_lmg_sa205105.h"


class IM_EX_DEVDB CtiDeviceGroupSA205 : public CtiDeviceGroupBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceGroupSA205(const CtiDeviceGroupSA205&);
    CtiDeviceGroupSA205& operator=(const CtiDeviceGroupSA205&);

    typedef CtiDeviceGroupBase Inherited;

    int _lastSTime;     // Holds the last sent message for the restore graceful command.
    int _lastCTime;
    CtiTime _onePeriodLeft;

protected:

    CtiTableSA205105Group _loadGroup;

public:

    CtiDeviceGroupSA205();

    const CtiTableSA205105Group& getLoadGroup() const;

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    void DecodeDatabaseReader(Cti::RowReader &rdr) override;
    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
};
