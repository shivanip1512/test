#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sasimple.h"
#include "tbl_lmg_sasimple.h"

class IM_EX_DEVDB CtiDeviceGroupSADigital : public CtiDeviceGroupBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceGroupSADigital(const CtiDeviceGroupSADigital&);
    CtiDeviceGroupSADigital& operator=(const CtiDeviceGroupSADigital&);

    typedef CtiDeviceGroupBase Inherited;

protected:

    CtiTableSASimpleGroup   _loadGroup;

public:

    CtiDeviceGroupSADigital();
    virtual ~CtiDeviceGroupSADigital();

    CtiTableSASimpleGroup getLoadGroup() const;
    CtiTableSASimpleGroup& getLoadGroup();
    CtiDeviceGroupSADigital& setLoadGroup(const CtiTableSASimpleGroup& aRef);

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

};
