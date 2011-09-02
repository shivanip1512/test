#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_emetcon.h"

class IM_EX_DEVDB CtiDeviceGroupEmetcon : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

protected:

   CtiTableEmetconLoadGroup      EmetconGroup;

public:

   CtiDeviceGroupEmetcon();//   {}

   CtiDeviceGroupEmetcon(const CtiDeviceGroupEmetcon& aRef);/*
   {
      *this = aRef;
   }                                                          */

   virtual ~CtiDeviceGroupEmetcon();// {}

   CtiDeviceGroupEmetcon& operator=(const CtiDeviceGroupEmetcon& aRef);

   CtiTableEmetconLoadGroup   getEmetconGroup() const;//      { return EmetconGroup; }
   CtiTableEmetconLoadGroup&  getEmetconGroup();//            { return EmetconGroup; }

   CtiDeviceGroupEmetcon&     setEmetconGroup(const CtiTableEmetconLoadGroup& aRef);
   virtual LONG getRouteID();
   virtual std::string getDescription(const CtiCommandParser & parse) const;

   virtual std::string getSQLCoreStatement() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              std::list< CtiMessage* >      &vgList,
                              std::list< CtiMessage* >      &retList,
                              std::list< OUTMESS* >         &outList);

   static std::string CtiDeviceGroupEmetcon::generateCommandString(OUTMESS *&OutMessage);
};
