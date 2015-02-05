#pragma once

#include <limits.h>

#include "yukon.h"
#include "vcomdefs.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceMeterGroup : public CtiMemDBObject, private boost::noncopyable
{
   LONG          _deviceID;
   std::string   _meterNumber;

   long getDeviceID() const;

public:

   CtiTableDeviceMeterGroup();
   virtual ~CtiTableDeviceMeterGroup();

   std::string getMeterNumber() const;
   CtiTableDeviceMeterGroup& setMeterNumber( const std::string &mNum );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

