

#pragma once

#include <limits.h>

#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceIED : public CtiMemDBObject
{
protected:

   LONG           _deviceID;
   string      _password;
   INT            _slaveAddress;

public:

   CtiTableDeviceIED();

   CtiTableDeviceIED(const CtiTableDeviceIED& aRef);

   virtual ~CtiTableDeviceIED();

   CtiTableDeviceIED& operator=(const CtiTableDeviceIED& aRef);

   INT                  getSlaveAddress() const;
   INT&                 getSlaveAddress();
   CtiTableDeviceIED    setSlaveAddress(INT &aInt);

   string            getPassword() const;
   string&           getPassword();
   CtiTableDeviceIED    setPassword(string &aStr);

   void DecodeDatabaseReader(const INT DeviceType, Cti::RowReader &rdr);

   static string getTableName();

   LONG getDeviceID() const;

   CtiTableDeviceIED& setDeviceID( const LONG did);
};

