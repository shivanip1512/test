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

