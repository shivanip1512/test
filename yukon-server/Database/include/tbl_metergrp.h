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
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceMeterGroup(const CtiTableDeviceMeterGroup&);
    CtiTableDeviceMeterGroup& operator=(const CtiTableDeviceMeterGroup&);

protected:

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

