#pragma once

#include "rwutil.h"

#include "types.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceIDLC : public CtiMemDBObject
{
protected:

   LONG        _deviceID;
   LONG        _address;       // IDLC Addressing, etc.
   INT         _postdelay;
   INT         _ccuAmpUseType;
   INT         _currentAmp;

   void setCCUAmpUseType( const INT aAmpUseType );  //  for unit test

private:

    static std::string getTableName();
    LONG getDeviceID() const;

public:

   CtiTableDeviceIDLC();
   CtiTableDeviceIDLC(const CtiTableDeviceIDLC& aRef);

   virtual ~CtiTableDeviceIDLC();

   CtiTableDeviceIDLC& operator=(const CtiTableDeviceIDLC& aRef);

   LONG getAddress() const;
   INT  getAmp();
   INT  getPostDelay() const;
   INT  getCCUAmpUseType() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

