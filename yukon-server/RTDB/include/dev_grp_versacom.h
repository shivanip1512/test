#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_versacom.h"

class IM_EX_DEVDB CtiDeviceGroupVersacom : public CtiDeviceGroupBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceGroupVersacom(const CtiDeviceGroupVersacom&);
    CtiDeviceGroupVersacom& operator=(const CtiDeviceGroupVersacom&);

    typedef CtiDeviceGroupBase Inherited;

protected:

   CtiTableVersacomLoadGroup     VersacomGroup;

public:

   CtiDeviceGroupVersacom();

   virtual LONG getRouteID();
   virtual std::string getDescription(const CtiCommandParser & parse) const;

   virtual std::string getSQLCoreStatement() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   virtual std::string getPutConfigAssignment(UINT modifier = 0);

};
