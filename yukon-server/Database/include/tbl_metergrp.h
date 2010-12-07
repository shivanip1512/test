

#pragma once

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "yukon.h"
#include "vcomdefs.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceMeterGroup : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   string   _meterNumber;

   long getDeviceID() const;

public:

   CtiTableDeviceMeterGroup();

   CtiTableDeviceMeterGroup(const CtiTableDeviceMeterGroup& aRef);

   virtual ~CtiTableDeviceMeterGroup();

   CtiTableDeviceMeterGroup& operator=(const CtiTableDeviceMeterGroup& aRef);

   string getMeterNumber() const;
   CtiTableDeviceMeterGroup& setMeterNumber( const string &mNum );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

