#pragma once

#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceSeriesV : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceSeriesV(const CtiTableDeviceSeriesV&);
    CtiTableDeviceSeriesV& operator=(const CtiTableDeviceSeriesV&);

protected:

    LONG      _device_id;
    unsigned  _start_code,
              _stop_code;
    bool      _save_history;
    INT       _tick_time,
              _transmit_offset,
              _power_value_high,
              _power_value_low,
              _power_value_multiplier,
              _power_value_offset,
              _retries;

public:

    CtiTableDeviceSeriesV();
    virtual ~CtiTableDeviceSeriesV();

    long                   getDeviceID() const;
    CtiTableDeviceSeriesV  setDeviceID(long id);

    int getTickTime()        const;
    int getTimeOffset()      const;
    int getTransmitterLow()  const;
    int getTransmitterHigh() const;

    unsigned getStartCode() const;
    unsigned getStopCode()  const;

    void DecodeDatabaseReader(Cti::RowReader &rdr);

    static std::string getTableName();
};
