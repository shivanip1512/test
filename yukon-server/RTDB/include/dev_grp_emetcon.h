#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_emetcon.h"

class IM_EX_DEVDB CtiDeviceGroupEmetcon : public CtiDeviceGroupBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceGroupEmetcon(const CtiDeviceGroupEmetcon&);
    CtiDeviceGroupEmetcon& operator=(const CtiDeviceGroupEmetcon&);

    typedef CtiDeviceGroupBase Inherited;

protected:

   CtiTableEmetconLoadGroup      EmetconGroup;

public:

   CtiDeviceGroupEmetcon();

   virtual LONG getRouteID();
   virtual std::string getDescription(const CtiCommandParser & parse) const;

   virtual std::string getSQLCoreStatement() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              CtiMessageList      &vgList,
                              CtiMessageList      &retList,
                              OutMessageList         &outList);

   static std::string CtiDeviceGroupEmetcon::generateCommandString(OUTMESS *&OutMessage);
};
