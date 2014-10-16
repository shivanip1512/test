#include "precompiled.h"

#include "tbl_base.h"
#include "resolvers.h"

using std::transform;
using std::string;
using std::endl;

CtiTableDeviceBase::CtiTableDeviceBase() :
    _alarmInhibit(false),
    _controlInhibit(false),
    _useRadioDelay(true)
{
}

CtiTableDeviceBase::~CtiTableDeviceBase()
{
}

CtiTableDeviceBase& CtiTableDeviceBase::setAlarmInhibit(bool b)
{
    _alarmInhibit = b;
    return *this;
}
CtiTableDeviceBase& CtiTableDeviceBase::setControlInhibit(bool b)
{
    _controlInhibit = b;
    return *this;
}

CtiTableDeviceBase& CtiTableDeviceBase::setRadioDelay(bool b)
{
    _useRadioDelay = b;
    return *this;
}


bool  CtiTableDeviceBase::getAlarmInhibit() const
{
    return _alarmInhibit;
}
bool  CtiTableDeviceBase::getControlInhibit() const
{
    return _controlInhibit;
}
bool  CtiTableDeviceBase::getRadioDelay() const
{
    return _useRadioDelay;
}
bool  CtiTableDeviceBase::useRadioDelays() const
{
    return _useRadioDelay;
}

string CtiTableDeviceBase::getTableName() { return string("Device"); }

std::string CtiTableDeviceBase::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableDeviceBase";
    itemList.add("Alarm Inhibit")   << _alarmInhibit;
    itemList.add("Control Inhibit") << _controlInhibit;

    return itemList.toString();
}

void CtiTableDeviceBase::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    const string alarminhibit   = "alarminhibit";
    const string controlinhibit = "controlinhibit";

    string sTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr[alarminhibit  ] >> sTemp;
    _alarmInhibit   = !sTemp.empty() && (sTemp[0] == 'y' || sTemp[0] == 'Y');

    rdr[controlinhibit] >> sTemp;
    _controlInhibit = !sTemp.empty() && (sTemp[0] == 'y' || sTemp[0] == 'Y');
}
