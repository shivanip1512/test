/*-----------------------------------------------------------------------------*
*
* File:   tbl_base
*
* Date:   10/8/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_base.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/07/08 22:56:59 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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

CtiTableDeviceBase::CtiTableDeviceBase(const CtiTableDeviceBase &aRef) :
    _alarmInhibit(false),
    _controlInhibit(false),
    _useRadioDelay(true)
{
    *this = aRef;
}

CtiTableDeviceBase::~CtiTableDeviceBase()
{

}


CtiTableDeviceBase& CtiTableDeviceBase::operator=(const CtiTableDeviceBase &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        _alarmInhibit   = aRef.getAlarmInhibit();
        _controlInhibit = aRef.getControlInhibit();
        _useRadioDelay  = aRef.getRadioDelay();
    }

    return *this;
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

void CtiTableDeviceBase::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "Alarm Inhibit                               : " << _alarmInhibit << endl;
    dout << "Control Inhibit                             : " << _controlInhibit << endl;
}

void CtiTableDeviceBase::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    const string alarminhibit   = "alarminhibit";
    const string controlinhibit = "controlinhibit";

    string sTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr[alarminhibit  ] >> sTemp;
    _alarmInhibit   = !sTemp.empty() && (sTemp[0] == 'y' || sTemp[0] == 'Y');

    rdr[controlinhibit] >> sTemp;
    _controlInhibit = !sTemp.empty() && (sTemp[0] == 'y' || sTemp[0] == 'Y');
}
