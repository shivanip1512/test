#pragma once

#include <rw/db/reader.h>
#include <rw/db/db.h>
#include <rw/db/table.h>

#include "types.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"

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

    static string getTableName();
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

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
};

