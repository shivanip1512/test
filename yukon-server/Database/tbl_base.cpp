

#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:57:57 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_base.h"

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

RWCString CtiTableDeviceBase::getTableName() { return RWCString("Device"); }

void CtiTableDeviceBase::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table( getTableName() );

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
    RWCString rwsTemp;

    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["alarminhibit"] >> rwsTemp;
    rwsTemp.toLower();
    _alarmInhibit = ((rwsTemp == 'y') ? TRUE : FALSE);

    rdr["controlinhibit"] >> rwsTemp;
    rwsTemp.toLower();
    _controlInhibit = ((rwsTemp == 'y') ? TRUE : FALSE);
}

