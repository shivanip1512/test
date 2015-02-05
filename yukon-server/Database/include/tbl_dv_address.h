#pragma once

#include <limits.h>

#include <windows.h>

#include "types.h"
#include "logger.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceAddress : public CtiMemDBObject, private boost::noncopyable
{
   LONG     _deviceID;
   LONG     _masterAddress,
            _slaveAddress;
   INT      _postdelay;

public:

   CtiTableDeviceAddress();
   virtual ~CtiTableDeviceAddress();

   LONG getMasterAddress() const;
   void setMasterAddress(LONG a);

   LONG getSlaveAddress() const;
   void setSlaveAddress(LONG a);

   INT  getPostDelay() const;
   void setPostDelay(int d);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();

   LONG getDeviceID() const;
   CtiTableDeviceAddress& setDeviceID(const LONG did);

   virtual bool Insert();
   virtual bool Update();
   virtual bool Delete();

};
