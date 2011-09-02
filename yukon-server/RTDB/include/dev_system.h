#pragma once

#include "dev_base.h"


class CtiDeviceSystem : public CtiDeviceBase
{
private:

    typedef CtiDeviceBase Inherited;

protected:

public:

   CtiDeviceSystem();

   CtiDeviceSystem(const CtiDeviceSystem& aRef);

   virtual ~CtiDeviceSystem();

   CtiDeviceSystem& operator=(const CtiDeviceSystem& aRef);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              std::list< CtiMessage* >      &vgList,
                              std::list< CtiMessage* >      &retList,
                              std::list< OUTMESS* >         &outList);

   virtual std::string getDescription(const CtiCommandParser &parse) const;

   virtual INT  ProcessResult(INMESS*, CtiTime&, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

};
