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
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceAddress : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceAddress(const CtiTableDeviceAddress&);
    CtiTableDeviceAddress& operator=(const CtiTableDeviceAddress&);

protected:

   LONG     _deviceID;
   LONG     _masterAddress,
            _slaveAddress;
   INT      _postdelay;

private:

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
