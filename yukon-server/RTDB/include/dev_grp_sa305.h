#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sa305.h"
#include "tbl_lmg_sa305.h"


class IM_EX_DEVDB CtiDeviceGroupSA305 : public CtiDeviceGroupBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceGroupSA305(const CtiDeviceGroupSA305&);
    CtiDeviceGroupSA305& operator=(const CtiDeviceGroupSA305&);

public:
    typedef enum
    {
        SA305_DI_Control = 1,
        SA305_DLC_Control = 2
    } CtiSACommand_t;

private:

   typedef CtiDeviceGroupBase Inherited;

    CtiSACommand_t _lastSACommandType;

protected:

    CtiTableSA305LoadGroup _loadGroup;

public:

    CtiDeviceGroupSA305();

    const CtiTableSA305LoadGroup& getLoadGroup() const;

    CtiSACommand_t getLastSACommandType() const { return _lastSACommandType; }

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual std::string getPutConfigAssignment(UINT modifier = 0);

};
