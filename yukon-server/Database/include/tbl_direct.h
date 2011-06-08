#pragma once

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dbmemobject.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceDirectComm : public CtiMemDBObject
{

protected:

   LONG           _deviceID;
   RWMutexLock    DirectCommMux;
   LONG           PortID;

public:

   CtiTableDeviceDirectComm();
   virtual ~CtiTableDeviceDirectComm();
   CtiTableDeviceDirectComm(const CtiTableDeviceDirectComm &aRef);
   CtiTableDeviceDirectComm& operator=(const CtiTableDeviceDirectComm &aRef);

   LONG getPortID() const;
   void setPortID(LONG id);

   LONG getDeviceID() const;
   CtiTableDeviceDirectComm& setDeviceID( const LONG did);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   static std::string getTableName();
};

