#pragma once

#include "dllbase.h"
#include "logger.h"
#include "dbmemobject.h"
#include "row_reader.h"

/*----------------------------------------------------------------------------------------*
 * CtiTableDeviceBase is the base class for all device objects in the YUKON system.
 *
 * The CtiTableDeviceBase object is the only one which is synonymous with a table
 *  he could also be known as CtiTableDevice
 *----------------------------------------------------------------------------------------*/

class IM_EX_CTIYUKONDB CtiTableDeviceBase : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
    bool      _alarmInhibit;
    bool      _controlInhibit;
    bool      _useRadioDelay;        // RadioDelay

public:

    typedef CtiMemDBObject Inherited;

    CtiTableDeviceBase();
    virtual ~CtiTableDeviceBase();

    CtiTableDeviceBase& setAlarmInhibit(bool b);
    CtiTableDeviceBase& setControlInhibit(bool b);
    CtiTableDeviceBase& setRadioDelay(bool b);
    bool  getAlarmInhibit() const;
    bool  getControlInhibit() const;
    bool  getRadioDelay() const;
    bool  useRadioDelays() const;
    static std::string getTableName();
    virtual std::string toString() const override;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
