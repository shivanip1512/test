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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:16:05 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_base.h"
#include "resolvers.h"

#include "rwutil.h"

using std::transform;

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

void CtiTableDeviceBase::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table( getTableName().c_str() );

    selector <<
        devTbl["deviceid"] <<
        devTbl["alarminhibit"] <<
        devTbl["controlinhibit"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );
}

void CtiTableDeviceBase::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "Alarm Inhibit                               : " << _alarmInhibit << endl;
    dout << "Control Inhibit                             : " << _controlInhibit << endl;
}

void CtiTableDeviceBase::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["alarminhibit"] >> rwsTemp;
    transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    if(rwsTemp.length() > 0)
    {
        _alarmInhibit = ((rwsTemp[(size_t)0] == 'y') ? TRUE : FALSE);
    }

    rdr["controlinhibit"] >> rwsTemp;
    transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    if(rwsTemp.length() > 0)
    {
        _controlInhibit = ((rwsTemp[(size_t)0] == 'y') ? TRUE : FALSE);
    }
}

