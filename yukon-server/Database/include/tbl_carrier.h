#pragma once

#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceCarrier : public CtiMemDBObject, private boost::noncopyable
{
   LONG     _deviceID;
   INT      _address;

public:

   CtiTableDeviceCarrier();
   virtual ~CtiTableDeviceCarrier();

   INT  getAddress() const;
   CtiTableDeviceCarrier& setAddress( const INT aAddress );

   LONG getDeviceID() const;
   CtiTableDeviceCarrier& setDeviceID( const LONG did );

   static std::string getTableName();

   bool isInitialized();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

