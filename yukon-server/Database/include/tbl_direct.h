#pragma once

#include <limits.h>

#include "dbmemobject.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceDirectComm : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceDirectComm(const CtiTableDeviceDirectComm&);
    CtiTableDeviceDirectComm& operator=(const CtiTableDeviceDirectComm&);

protected:

   LONG                _deviceID;
   CtiCriticalSection  DirectCommMux;
   LONG                PortID;

public:

   CtiTableDeviceDirectComm();
   virtual ~CtiTableDeviceDirectComm();

   LONG getPortID() const;
   void setPortID(LONG id);

   LONG getDeviceID() const;
   CtiTableDeviceDirectComm& setDeviceID( const LONG did);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   static std::string getTableName();
};

