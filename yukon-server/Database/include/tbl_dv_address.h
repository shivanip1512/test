#pragma once

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw/thr/monitor.h>

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "types.h"
#include "logger.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceAddress : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   LONG     _masterAddress,
            _slaveAddress;
   INT      _postdelay;

private:

public:

   CtiTableDeviceAddress();
   CtiTableDeviceAddress(const CtiTableDeviceAddress& aRef);

   virtual ~CtiTableDeviceAddress();

   CtiTableDeviceAddress& operator=(const CtiTableDeviceAddress& aRef);

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
