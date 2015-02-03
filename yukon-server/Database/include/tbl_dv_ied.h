#pragma once

#include <limits.h>


#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceIED : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceIED(const CtiTableDeviceIED&);
    CtiTableDeviceIED& operator=(const CtiTableDeviceIED&);

protected:

   LONG           _deviceID;
   std::string    _password;
   INT            _slaveAddress;

public:

   CtiTableDeviceIED();
   virtual ~CtiTableDeviceIED();

   INT            getSlaveAddress() const;
   INT&           getSlaveAddress();
   void           setSlaveAddress(INT &aInt);

   std::string    getPassword() const;
   std::string&   getPassword();
   void           setPassword(std::string &aStr);

   void DecodeDatabaseReader(const INT DeviceType, Cti::RowReader &rdr);

   static std::string getTableName();

   LONG getDeviceID() const;

   CtiTableDeviceIED& setDeviceID( const LONG did);
};

