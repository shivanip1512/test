#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_versacom.h"

class IM_EX_DEVDB CtiDeviceGroupVersacom : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

protected:

   CtiTableVersacomLoadGroup     VersacomGroup;

public:

   CtiDeviceGroupVersacom();

   CtiDeviceGroupVersacom(const CtiDeviceGroupVersacom& aRef);

   virtual ~CtiDeviceGroupVersacom();

   CtiDeviceGroupVersacom& operator=(const CtiDeviceGroupVersacom& aRef);

   CtiTableVersacomLoadGroup   getVersacomGroup() const;
   CtiTableVersacomLoadGroup&  getVersacomGroup();

   CtiDeviceGroupVersacom&     setVersacomGroup(const CtiTableVersacomLoadGroup& aRef);

   virtual LONG getRouteID();
   virtual std::string getDescription(const CtiCommandParser & parse) const;

   virtual std::string getSQLCoreStatement() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   virtual std::string getPutConfigAssignment(UINT modifier = 0);

};
