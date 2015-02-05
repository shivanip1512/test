#pragma once

#include <limits.h>

#include <bitset>

#include "yukon.h"
#include "vcomdefs.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "da_load_profile.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceLoadProfile : public CtiMemDBObject, public Cti::DataAccessLoadProfile, private boost::noncopyable
{
public:

    enum
    {
        MaxCollectedChannel = 4
    };

protected:

   LONG  _deviceID;
   INT   _lastIntervalDemandRate;
   INT   _loadProfileDemandRate;
   INT   _voltageDemandInterval;
   INT   _voltageProfileRate;

   std::bitset<MaxCollectedChannel> _channelsValid;

public:

   CtiTableDeviceLoadProfile();
   virtual ~CtiTableDeviceLoadProfile();

   virtual int  getLastIntervalDemandRate() const;
   virtual int  getLoadProfileDemandRate()  const;
   virtual int  getVoltageDemandInterval()  const;
   virtual int  getVoltageProfileRate()     const;
   virtual bool isChannelValid(int channel) const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();

   LONG getDeviceID() const;
};
