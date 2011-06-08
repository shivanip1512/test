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
   std::string    _password;
   INT            _slaveAddress;

public:

   CtiTableDeviceIED();

   CtiTableDeviceIED(const CtiTableDeviceIED& aRef);

   virtual ~CtiTableDeviceIED();

   CtiTableDeviceIED& operator=(const CtiTableDeviceIED& aRef);

   INT                  getSlaveAddress() const;
   INT&                 getSlaveAddress();
   CtiTableDeviceIED    setSlaveAddress(INT &aInt);

   std::string            getPassword() const;
   std::string&           getPassword();
   CtiTableDeviceIED      setPassword(std::string &aStr);

   void DecodeDatabaseReader(const INT DeviceType, Cti::RowReader &rdr);

   static std::string getTableName();

   LONG getDeviceID() const;

   CtiTableDeviceIED& setDeviceID( const LONG did);
};

